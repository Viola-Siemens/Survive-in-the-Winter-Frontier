package com.hexagram2021.misc_twf_hearing.api;

import com.hexagram2021.misc_twf_hearing.common.config.MISCTWFHearingConfig;
import com.hexagram2021.misc_twf_hearing.common.config.NoiseTypeSettings;
import com.hexagram2021.misc_twf_hearing.common.util.MISCTWFHearingLogger;
import com.hexagram2021.misc_twf_hearing.core.NoiseManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * 循声系统公开 API 喵~：所有方法都是**服务端权威**且只能在服务端主线程调用。
 *
 * <p>只解决两个问题："怪物能不能听见这个声音？"与"听见以后去哪个坐标调查？"，
 * 不判断声音是谁发出的、不接管 Target、不修改视觉与寻路喵~</p>
 *
 * <p>本类检测到的两类问题都属于**调用方编程错误**（非法坐标、非服务端线程），
 * 处理策略为"每个服务器会话、每个类别只输出一次 WARN，之后同类降为 DEBUG"，
 * 既保证开发者一定能看到，又不会在错误路径上刷屏，也不会与运行期容量告警互抢限频额度喵~</p>
 *
 * @author liudongyu
 */
public final class HearingApi {
	/** 非法坐标是否已提示过（每服务器会话每类别一次）喵~ */
	private static final AtomicBoolean NON_FINITE_POSITION_WARNED = new AtomicBoolean(false);
	/** 非服务端线程是否已提示过（每服务器会话每类别一次）喵~ */
	private static final AtomicBoolean WRONG_THREAD_WARNED = new AtomicBoolean(false);
	/**
	 * 产生一条噪声喵~
	 *
	 * @param level    服务端维度喵~
	 * @param position 发声位置（必须是有限值）喵~
	 * @param spec     噪声规格喵~
	 * @param source   来源实体，可能为空喵~
	 * @return 写入后的噪声快照；被 {@link NoiseEmitEvent.Pre} 取消或调用线程非法时为 null喵~
	 */
	@Nullable
	public static NoiseSnapshot emit(ServerLevel level, Vec3 position, NoiseSpec spec, @Nullable Entity source) {
		if(!assertServerThread(level)) {
			return null;
		}
		if(!isFinite(position)) {
			reportProgrammingError(NON_FINITE_POSITION_WARNED, () -> "[hearing] rejected a noise with non-finite position: " + position);
			return null;
		}
		return NoiseManager.get(level).emit(position, spec, source);
	}

	/**
	 * 按类型产生一条噪声（强度与存活时长从该类型配置读取，半径由调用方给出）喵~
	 *
	 * @param level    服务端维度喵~
	 * @param position 发声位置喵~
	 * @param type     噪声类型（未配置类型回退到 {@link NoiseTypes#CUSTOM}）喵~
	 * @param radius   基础传播半径（格）喵~
	 * @param source   来源实体，可能为空喵~
	 * @return 写入后的噪声快照；被取消或线程非法时为 null喵~
	 */
	@Nullable
	public static NoiseSnapshot emit(ServerLevel level, Vec3 position, ResourceLocation type, float radius, @Nullable Entity source) {
		NoiseTypeSettings settings = MISCTWFHearingConfig.getSettings(type);
		NoiseSpec spec = new NoiseSpec(type, radius, settings.strength(), settings.rollLifetime(level.getRandom()));
		return emit(level, position, spec, source);
	}

	/**
	 * 区域查询：返回**发生位置**位于区域内的未过期噪声（不因传播半径与区域相交而返回）喵~
	 *
	 * @param level 服务端维度喵~
	 * @param area  查询区域喵~
	 * @return 噪声快照列表；线程非法时为空列表喵~
	 */
	public static List<NoiseSnapshot> query(ServerLevel level, AABB area) {
		if(!assertServerThread(level)) {
			return List.of();
		}
		return NoiseManager.get(level).query(area);
	}

	/**
	 * 区域查询并取强度最大者（严格比较 {@code strength}）喵~
	 *
	 * @param level 服务端维度喵~
	 * @param area  查询区域喵~
	 * @return 最强噪声快照喵~
	 */
	public static Optional<NoiseSnapshot> findStrongest(ServerLevel level, AABB area) {
		return findStrongest(level, area, noise -> true);
	}

	/**
	 * 区域查询并取强度最大者（严格比较 {@code strength}）喵~
	 *
	 * @param level     服务端维度喵~
	 * @param area      查询区域喵~
	 * @param predicate 额外过滤条件喵~
	 * @return 最强噪声快照喵~
	 */
	public static Optional<NoiseSnapshot> findStrongest(ServerLevel level, AABB area, Predicate<NoiseSnapshot> predicate) {
		if(!assertServerThread(level)) {
			return Optional.empty();
		}
		return NoiseManager.get(level).findStrongest(area, predicate);
	}

	/**
	 * 以监听点为基准，按统一评分选出最值得调查的噪声喵~
	 *
	 * @param level             服务端维度喵~
	 * @param listenerPosition  监听点喵~
	 * @param hearingMultiplier 听觉倍率（有效半径 = radius × 该值）喵~
	 * @param predicate         额外过滤条件喵~
	 * @return 最值得调查的噪声快照喵~
	 */
	public static Optional<NoiseSnapshot> findBestAudible(ServerLevel level, Vec3 listenerPosition, float hearingMultiplier,
	                                                     Predicate<NoiseSnapshot> predicate) {
		if(!assertServerThread(level)) {
			return Optional.empty();
		}
		return NoiseManager.get(level).findBestAudible(listenerPosition, hearingMultiplier, predicate);
	}

	/**
	 * 解析噪声来源实体（仅用于调试与外部 API，**不允许**被自动转换为攻击目标）喵~
	 *
	 * @param level 服务端维度喵~
	 * @param noise 噪声快照喵~
	 * @return 来源实体喵~
	 */
	public static Optional<Entity> resolveSource(ServerLevel level, NoiseSnapshot noise) {
		if(noise.sourceUuid() == null || !assertServerThread(level)) {
			return Optional.empty();
		}
		return Optional.ofNullable(level.getEntity(noise.sourceUuid()));
	}

	private static boolean assertServerThread(ServerLevel level) {
		if(level.getServer().isSameThread()) {
			return true;
		}
		reportProgrammingError(WRONG_THREAD_WARNED,
				() -> "[hearing] noise APIs must be called on the server thread; the call was rejected.");
		return false;
	}

	/**
	 * 报告调用方编程错误：每个服务器会话、每类别首次输出 WARN，之后同类降为 DEBUG 喵~
	 *
	 * @param warned  该类错误的"已提示"标记喵~
	 * @param message 错误内容（仅在需要输出时才求值）喵~
	 */
	private static void reportProgrammingError(AtomicBoolean warned, Supplier<String> message) {
		if(warned.compareAndSet(false, true)) {
			MISCTWFHearingLogger.warn(message.get());
		} else {
			MISCTWFHearingLogger.debug(message);
		}
	}

	private static boolean isFinite(Vec3 position) {
		return Double.isFinite(position.x) && Double.isFinite(position.y) && Double.isFinite(position.z);
	}

	private HearingApi() {
	}
}
