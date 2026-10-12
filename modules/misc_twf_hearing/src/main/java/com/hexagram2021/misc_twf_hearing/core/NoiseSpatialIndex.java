package com.hexagram2021.misc_twf_hearing.core;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.function.Predicate;

/**
 * 噪声空间索引喵~：以 {@code ChunkPos.asLong(x, z)} 为键的区块桶。
 *
 * <p>噪声只放入**声源所在区块**的桶中，不按传播半径复制到周围区块；查询时按监听点/区域计算
 * 需要扫描的区块范围，并且**只遍历自己已持有的内存数据**，不访问方块、不触发区块同步加载喵~</p>
 *
 * @author liudongyu
 */
public final class NoiseSpatialIndex {
	private final Long2ObjectMap<List<NoisePulse>> byChunk = new Long2ObjectOpenHashMap<>();

	/**
	 * 计算坐标所在区块的键喵~
	 *
	 * @param position 世界坐标喵~
	 * @return 区块键喵~
	 */
	public static long chunkKey(Vec3 position) {
		return chunkKey(Mth.floor(position.x) >> 4, Mth.floor(position.z) >> 4);
	}

	/**
	 * 计算区块键喵~
	 *
	 * @param chunkX 区块 X 坐标喵~
	 * @param chunkZ 区块 Z 坐标喵~
	 * @return 区块键喵~
	 */
	public static long chunkKey(int chunkX, int chunkZ) {
		return (long) chunkZ << 32 | chunkX & 0xFFFFFFFFL;
	}

	/**
	 * 将噪声加入索引喵~
	 *
	 * @param pulse 噪声喵~
	 */
	public void add(NoisePulse pulse) {
		this.bucket(chunkKey(pulse.position())).add(pulse);
	}

	/**
	 * 将噪声从索引中移除喵~
	 *
	 * @param pulse 噪声喵~
	 */
	public void remove(NoisePulse pulse) {
		this.removeFrom(chunkKey(pulse.position()), pulse);
	}

	/**
	 * 噪声位置变化后重新分桶（合并导致跨区块时使用）喵~
	 *
	 * @param pulse       噪声喵~
	 * @param oldChunkKey 变更前的区块键喵~
	 */
	public void rebucket(NoisePulse pulse, long oldChunkKey) {
		long newChunkKey = chunkKey(pulse.position());
		if(oldChunkKey == newChunkKey) {
			return;
		}
		this.removeFrom(oldChunkKey, pulse);
		this.bucket(newChunkKey).add(pulse);
	}

	/**
	 * 收集区域内的噪声：语义为"噪声**发生位置**位于 region 内"喵~
	 *
	 * @param region 查询区域喵~
	 * @param now    当前游戏时间（tick）喵~
	 * @param filter 额外过滤条件喵~
	 * @return 命中的内部噪声列表喵~
	 */
	public List<NoisePulse> collect(AABB region, long now, Predicate<NoisePulse> filter) {
		int minChunkX = Mth.floor(region.minX) >> 4;
		int maxChunkX = Mth.floor(region.maxX) >> 4;
		int minChunkZ = Mth.floor(region.minZ) >> 4;
		int maxChunkZ = Mth.floor(region.maxZ) >> 4;
		List<NoisePulse> result = Lists.newArrayList();
		for(int chunkX = minChunkX; chunkX <= maxChunkX; chunkX++) {
			for(int chunkZ = minChunkZ; chunkZ <= maxChunkZ; chunkZ++) {
				List<NoisePulse> bucket = this.byChunk.get(chunkKey(chunkX, chunkZ));
				if(bucket == null) {
					continue;
				}
				for(NoisePulse pulse : bucket) {
					if(!pulse.isExpired(now) && region.contains(pulse.position()) && filter.test(pulse)) {
						result.add(pulse);
					}
				}
			}
		}
		return result;
	}

	/**
	 * 收集监听点附近可能可听的噪声（按给定最大半径裁剪区块范围）喵~
	 *
	 * @param listener 监听点喵~
	 * @param radius   最大扫描半径（格）喵~
	 * @param now      当前游戏时间（tick）喵~
	 * @param filter   额外过滤条件喵~
	 * @return 命中的内部噪声列表喵~
	 */
	public List<NoisePulse> collectAround(Vec3 listener, double radius, long now, Predicate<NoisePulse> filter) {
		int chunkRadius = Mth.ceil(radius / 16.0D);
		int centerChunkX = Mth.floor(listener.x) >> 4;
		int centerChunkZ = Mth.floor(listener.z) >> 4;
		List<NoisePulse> result = Lists.newArrayList();
		double radiusSqr = radius * radius;
		for(int chunkX = centerChunkX - chunkRadius; chunkX <= centerChunkX + chunkRadius; chunkX++) {
			for(int chunkZ = centerChunkZ - chunkRadius; chunkZ <= centerChunkZ + chunkRadius; chunkZ++) {
				List<NoisePulse> bucket = this.byChunk.get(chunkKey(chunkX, chunkZ));
				if(bucket == null) {
					continue;
				}
				for(NoisePulse pulse : bucket) {
					if(!pulse.isExpired(now) && pulse.position().distanceToSqr(listener) <= radiusSqr && filter.test(pulse)) {
						result.add(pulse);
					}
				}
			}
		}
		return result;
	}

	/**
	 * 清空索引喵~
	 */
	public void clear() {
		this.byChunk.clear();
	}

	/**
	 * 获取索引中的区块桶数量喵~
	 *
	 * @return 区块桶数量喵~
	 */
	public int chunkCount() {
		return this.byChunk.size();
	}

	/**
	 * 获取全部区块桶键的副本（容量保护时遍历用）喵~
	 *
	 * @return 区块桶键列表喵~
	 */
	public List<Long> chunkKeys() {
		return Lists.newArrayList(this.byChunk.keySet());
	}

	/**
	 * 获取指定区块桶内的噪声数量喵~
	 *
	 * @param chunkKey 区块键喵~
	 * @return 噪声数量喵~
	 */
	public int bucketSize(long chunkKey) {
		List<NoisePulse> bucket = this.byChunk.get(chunkKey);
		return bucket == null ? 0 : bucket.size();
	}

	/**
	 * 获取索引中的噪声总数喵~
	 *
	 * @return 噪声总数喵~
	 */
	public int size() {
		int size = 0;
		for(List<NoisePulse> bucket : this.byChunk.values()) {
			size += bucket.size();
		}
		return size;
	}

	private List<NoisePulse> bucket(long key) {
		return this.byChunk.computeIfAbsent(key, ignored -> Lists.newArrayList());
	}

	private void removeFrom(long key, NoisePulse pulse) {
		List<NoisePulse> bucket = this.byChunk.get(key);
		if(bucket == null) {
			return;
		}
		bucket.remove(pulse);
		if(bucket.isEmpty()) {
			this.byChunk.remove(key);
		}
	}
}
