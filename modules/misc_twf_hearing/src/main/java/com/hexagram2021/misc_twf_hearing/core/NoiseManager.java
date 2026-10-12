package com.hexagram2021.misc_twf_hearing.core;

import com.google.common.collect.Lists;
import com.hexagram2021.misc_twf_hearing.api.NoiseEmitEvent;
import com.hexagram2021.misc_twf_hearing.api.NoiseSnapshot;
import com.hexagram2021.misc_twf_hearing.api.NoiseSpec;
import com.hexagram2021.misc_twf_hearing.common.config.MISCTWFHearingConfig;
import com.hexagram2021.misc_twf_hearing.common.util.MISCTWFHearingLogger;
import com.hexagram2021.misc_twf_hearing.common.util.TickRateLimiter;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

/**
 * 每个维度一份的噪声管理器喵~（内部类，外部请使用 {@code HearingApi}）。
 *
 * <p>生命周期：随 {@link ServerLevel} 创建、随维度卸载/服务器停止释放；**不持久化**
 * （不写 NBT、不进 SavedData、不存区块），服务器重启后全部消失，这是预期行为喵~</p>
 *
 * <p>线程约束：所有方法都只能在服务端主线程调用（由 {@code HearingApi} 统一校验）喵~</p>
 *
 * @author liudongyu
 */
@ApiStatus.Internal
public final class NoiseManager {
	private static final Object2ObjectMap<ServerLevel, NoiseManager> MANAGERS = new Object2ObjectOpenHashMap<>();
	/** 容量保护时的丢弃顺序：强度低者优先，其次过期更早者喵~ */
	private static final Comparator<NoisePulse> WEAKEST_FIRST =
			Comparator.comparingDouble(NoisePulse::strength).thenComparingLong(NoisePulse::expireTick);

	private final ServerLevel level;
	private final NoiseSpatialIndex index = new NoiseSpatialIndex();
	private final Long2ObjectMap<NoisePulse> byId = new Long2ObjectOpenHashMap<>();
	/** 单维度容量告警的限频游标（每维度独立，不与其他类别抢额度）喵~ */
	private final TickRateLimiter levelCapWarning = new TickRateLimiter(200L);
	/** 单区块容量告警的限频游标（与单维度告警彼此独立）喵~ */
	private final TickRateLimiter chunkCapWarning = new TickRateLimiter(200L);
	private long nextId = 1L;
	private long lastCleanupTick = Long.MIN_VALUE;
	private long emittedCount;
	private long mergedCount;
	private long queryCount;
	private long candidateCount;

	private NoiseManager(ServerLevel level) {
		this.level = level;
	}

	/**
	 * 获取（必要时创建）指定维度的噪声管理器喵~
	 *
	 * @param level 服务端维度喵~
	 * @return 噪声管理器喵~
	 */
	public static NoiseManager get(ServerLevel level) {
		return MANAGERS.computeIfAbsent(level, NoiseManager::new);
	}

	/**
	 * 获取已存在的噪声管理器喵~
	 *
	 * @param level 服务端维度喵~
	 * @return 噪声管理器，不存在时为空喵~
	 */
	@Nullable
	public static NoiseManager getIfPresent(ServerLevel level) {
		return MANAGERS.get(level);
	}

	/**
	 * 获取全部已存在的噪声管理器喵~
	 *
	 * @return 管理器列表喵~
	 */
	public static List<NoiseManager> all() {
		return Lists.newArrayList(MANAGERS.values());
	}

	/**
	 * 释放指定维度的噪声管理器喵~
	 *
	 * @param level 服务端维度喵~
	 */
	public static void release(ServerLevel level) {
		NoiseManager manager = MANAGERS.remove(level);
		if(manager != null) {
			manager.clear();
		}
	}

	/**
	 * 释放全部噪声管理器（服务器停止时调用）喵~
	 */
	public static void releaseAll() {
		for(NoiseManager manager : MANAGERS.values()) {
			manager.clear();
		}
		MANAGERS.clear();
	}

	/**
	 * 产生噪声（含事件、合并与容量保护）喵~
	 *
	 * @param position 发声位置喵~
	 * @param spec     噪声规格喵~
	 * @param source   来源实体，可能为空喵~
	 * @return 写入后的噪声快照；被事件取消时为 null喵~
	 */
	@Nullable
	public NoiseSnapshot emit(Vec3 position, NoiseSpec spec, @Nullable Entity source) {
		long now = this.level.getGameTime();
		NoiseEmitEvent.Pre pre = new NoiseEmitEvent.Pre(this.level, position, spec, source);
		NeoForge.EVENT_BUS.post(pre);
		if(pre.isCanceled()) {
			return null;
		}
		Vec3 finalPosition = pre.getPosition();
		NoiseSpec finalSpec = pre.getSpec();
		UUID sourceUuid = source == null ? null : source.getUUID();

		NoisePulse merged = sourceUuid == null ? null : this.findMergeCandidate(finalPosition, finalSpec, sourceUuid, now);
		NoisePulse pulse;
		if(merged != null) {
			long oldChunkKey = NoiseSpatialIndex.chunkKey(merged.position());
			merged.merge(finalPosition, finalSpec, now);
			this.index.rebucket(merged, oldChunkKey);
			this.mergedCount++;
			pulse = merged;
		} else {
			this.enforceCapacity(now);
			pulse = new NoisePulse(this.nextId++, finalPosition, finalSpec, sourceUuid, now);
			this.index.add(pulse);
			this.byId.put(pulse.id(), pulse);
			this.emittedCount++;
		}

		NoiseSnapshot snapshot = pulse.snapshot();
		NeoForge.EVENT_BUS.post(new NoiseEmitEvent.Post(this.level, snapshot, source));
		return snapshot;
	}

	/**
	 * 区域查询：返回**发生位置**位于区域内的未过期噪声喵~
	 *
	 * @param area 查询区域喵~
	 * @return 噪声快照列表喵~
	 */
	public List<NoiseSnapshot> query(AABB area) {
		long now = this.level.getGameTime();
		List<NoisePulse> pulses = this.index.collect(area, now, pulse -> true);
		this.queryCount++;
		this.candidateCount += pulses.size();
		return pulses.stream().map(NoisePulse::snapshot).toList();
	}

	/**
	 * 区域查询并取强度最大者（严格比较 {@code strength}）喵~
	 *
	 * @param area      查询区域喵~
	 * @param predicate 额外过滤条件喵~
	 * @return 最强噪声快照喵~
	 */
	public Optional<NoiseSnapshot> findStrongest(AABB area, Predicate<NoiseSnapshot> predicate) {
		NoisePulse best = null;
		for(NoisePulse pulse : this.queryPulses(area)) {
			if(!predicate.test(pulse.snapshot())) {
				continue;
			}
			if(best == null || pulse.strength() > best.strength()) {
				best = pulse;
			}
		}
		return Optional.ofNullable(best).map(NoisePulse::snapshot);
	}

	/**
	 * 监听点查询：按 {@link NoiseScoring} 评分选出最值得调查的噪声喵~
	 *
	 * @param listener          监听点喵~
	 * @param hearingMultiplier 听觉倍率喵~
	 * @param predicate         额外过滤条件喵~
	 * @return 最值得调查的噪声快照喵~
	 */
	public Optional<NoiseSnapshot> findBestAudible(Vec3 listener, float hearingMultiplier, Predicate<NoiseSnapshot> predicate) {
		long now = this.level.getGameTime();
		double scanRadius = MISCTWFHearingConfig.QUERY_MAX_RADIUS.get();
		List<NoisePulse> candidates = this.index.collectAround(listener, scanRadius, now, pulse -> true);
		this.queryCount++;
		this.candidateCount += candidates.size();
		float minScore = (float) (double) MISCTWFHearingConfig.MIN_SCORE_THRESHOLD.get();
		NoisePulse best = null;
		float bestScore = minScore;
		for(NoisePulse pulse : candidates) {
			if(!NoiseScoring.isAudible(pulse, listener, hearingMultiplier) || !predicate.test(pulse.snapshot())) {
				continue;
			}
			float score = NoiseScoring.score(pulse, listener, hearingMultiplier, now);
			if(score >= bestScore) {
				bestScore = score;
				best = pulse;
			}
		}
		return Optional.ofNullable(best).map(NoisePulse::snapshot);
	}

	/**
	 * 周期性清理过期噪声并执行容量保护喵~
	 */
	public void cleanup() {
		long now = this.level.getGameTime();
		if(this.lastCleanupTick != Long.MIN_VALUE && now - this.lastCleanupTick < MISCTWFHearingConfig.CLEANUP_INTERVAL_TICKS.get()) {
			return;
		}
		this.lastCleanupTick = now;
		this.removeExpired(now);
		this.enforceCapacity(now);
	}

	/**
	 * 获取当前维度统计信息喵~
	 *
	 * @return 统计信息喵~
	 */
	public NoiseStats stats() {
		return new NoiseStats(this.byId.size(), this.index.chunkCount(), this.emittedCount, this.mergedCount, this.queryCount, this.candidateCount);
	}

	/**
	 * 获取该管理器所属维度喵~
	 *
	 * @return 服务端维度喵~
	 */
	public ServerLevel level() {
		return this.level;
	}

	private List<NoisePulse> queryPulses(AABB area) {
		long now = this.level.getGameTime();
		List<NoisePulse> pulses = this.index.collect(area, now, pulse -> true);
		this.queryCount++;
		this.candidateCount += pulses.size();
		return pulses;
	}

	@Nullable
	private NoisePulse findMergeCandidate(Vec3 position, NoiseSpec spec, UUID sourceUuid, long now) {
		int mergeWindow = MISCTWFHearingConfig.MERGE_WINDOW_TICKS.get();
		double mergeDistance = MISCTWFHearingConfig.MERGE_DISTANCE.get();
		NoisePulse best = null;
		for(NoisePulse pulse : this.index.collectAround(position, mergeDistance, now, pulse -> true)) {
			if(!pulse.type().equals(spec.type()) || !sourceUuid.equals(pulse.sourceUuid())) {
				continue;
			}
			if(now - pulse.lastEmitTick() > mergeWindow) {
				continue;
			}
			if(pulse.position().distanceToSqr(position) > mergeDistance * mergeDistance) {
				continue;
			}
			if(best == null || pulse.expireTick() > best.expireTick()) {
				best = pulse;
			}
		}
		return best;
	}

	private void removeExpired(long now) {
		List<NoisePulse> expired = Lists.newArrayList();
		for(NoisePulse pulse : this.byId.values()) {
			if(pulse.isExpired(now)) {
				expired.add(pulse);
			}
		}
		for(NoisePulse pulse : expired) {
			this.index.remove(pulse);
			this.byId.remove(pulse.id());
		}
	}

	private void enforceCapacity(long now) {
		int maxPerLevel = MISCTWFHearingConfig.MAX_ACTIVE_PER_LEVEL.get();
		int maxPerChunk = MISCTWFHearingConfig.MAX_ACTIVE_PER_CHUNK.get();
		if(this.byId.size() >= maxPerLevel) {
			this.removeExpired(now);
		}
		// 一次算出需要丢弃的数量，再批量丢弃：避免"每次丢一个都全表扫一遍"的平方级开销喵~
		int levelOverflow = this.byId.size() - maxPerLevel + 1;
		if(levelOverflow > 0) {
			this.dropWeakest(levelOverflow);
			if(this.levelCapWarning.tryAcquire(now)) {
				MISCTWFHearingLogger.warn("[hearing] active noises reached the per-level cap (" + maxPerLevel + "), dropped " + levelOverflow + " oldest/weakest ones.");
			}
		}
		if(maxPerChunk > 0) {
			// chunkKeys() 返回的就是键的副本，直接迭代即可（丢弃过程中会移除空桶）喵~
			for(long chunkKey : this.index.chunkKeys()) {
				int chunkOverflow = this.index.bucketSize(chunkKey) - maxPerChunk + 1;
				if(chunkOverflow <= 0) {
					continue;
				}
				this.dropWeakestInChunk(chunkKey, chunkOverflow);
				if(this.chunkCapWarning.tryAcquire(now)) {
					MISCTWFHearingLogger.warn("[hearing] active noises reached the per-chunk cap (" + maxPerChunk + "), dropped " + chunkOverflow + " oldest/weakest ones.");
				}
			}
		}
	}

	/**
	 * 丢弃最弱的若干噪声（强度低者优先，其次过期更早者）喵~
	 *
	 * @param count 需要丢弃的数量喵~
	 */
	private void dropWeakest(int count) {
		List<NoisePulse> pulses = Lists.newArrayList(this.byId.values());
		pulses.sort(WEAKEST_FIRST);
		this.removePulses(pulses, count);
	}

	/**
	 * 丢弃指定区块内最弱的若干噪声喵~
	 *
	 * @param chunkKey 区块键喵~
	 * @param count    需要丢弃的数量喵~
	 */
	private void dropWeakestInChunk(long chunkKey, int count) {
		List<NoisePulse> pulses = Lists.newArrayList();
		for(NoisePulse pulse : this.byId.values()) {
			if(NoiseSpatialIndex.chunkKey(pulse.position()) == chunkKey) {
				pulses.add(pulse);
			}
		}
		pulses.sort(WEAKEST_FIRST);
		this.removePulses(pulses, count);
	}

	private void removePulses(List<NoisePulse> sortedPulses, int count) {
		int limit = Math.min(count, sortedPulses.size());
		for(int i = 0; i < limit; i++) {
			NoisePulse pulse = sortedPulses.get(i);
			this.index.remove(pulse);
			this.byId.remove(pulse.id());
		}
	}

	private void clear() {
		this.index.clear();
		this.byId.clear();
	}

	/**
	 * 循声管理器的运行时统计喵~
	 *
	 * @param activePulses   当前有效噪声数喵~
	 * @param indexedChunks  索引中的区块桶数喵~
	 * @param emittedCount   累计新建噪声数喵~
	 * @param mergedCount    累计合并次数喵~
	 * @param queryCount     累计查询次数喵~
	 * @param candidateCount 累计查询候选数喵~
	 * @author liudongyu
	 */
	public record NoiseStats(int activePulses, int indexedChunks, long emittedCount, long mergedCount,
	                         long queryCount, long candidateCount) {
	}
}
