package com.hexagram2021.misc_twf_hearing.ai;

import com.hexagram2021.misc_twf_hearing.common.config.MISCTWFHearingConfig;
import com.hexagram2021.misc_twf_hearing.common.util.MISCTWFHearingLogger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

/**
 * 单个实体的听觉档案喵~：只承载"与实体类型相关"的数值，Goal 行为参数走全局配置。
 *
 * <p>P0 取值为"全局配置 + 白名单实体的听觉倍率覆盖"（配置项
 * {@code goal.entity_hearing_multipliers}，格式 {@code namespace:path=倍率}）；
 * P1 若切换到 NeoForge Data Map，只需替换 {@link #of(EntityType)} 的实现喵~</p>
 *
 * @param hearingMultiplier  听觉倍率（有效半径 = radius × 该值）喵~
 * @param queryIntervalTicks 查询间隔（tick）喵~
 * @param queryJitterTicks   查询错峰抖动（tick）喵~
 * @param investigateSpeed   调查时的移动速度修正喵~
 * @param arrivalDistance    到达判定距离（格）喵~
 * @author liudongyu
 */
public record HearingProfile(float hearingMultiplier, int queryIntervalTicks, int queryJitterTicks,
							 float investigateSpeed, float arrivalDistance) {
	/**
	 * 解析指定实体类型的听觉档案喵~
	 *
	 * @param entityType 实体类型喵~
	 * @return 听觉档案喵~
	 */
	public static HearingProfile of(EntityType<?> entityType) {
		return new HearingProfile(
				resolveMultiplier(entityType),
				MISCTWFHearingConfig.QUERY_INTERVAL_TICKS.get(),
				MISCTWFHearingConfig.QUERY_JITTER_TICKS.get(),
				(float) (double) MISCTWFHearingConfig.SPEED_MODIFIER.get(),
				(float) (double) MISCTWFHearingConfig.ARRIVAL_DISTANCE.get()
		);
	}

	private static float resolveMultiplier(EntityType<?> entityType) {
		ResourceLocation id = BuiltInRegistries.ENTITY_TYPE.getKeyOrNull(entityType);
		if(id == null) {
			return 1.0F;
		}
		String prefix = id + "=";
		for(String entry : MISCTWFHearingConfig.ENTITY_HEARING_MULTIPLIERS.get()) {
			String trimmed = entry.trim();
			if(!trimmed.startsWith(prefix)) {
				continue;
			}
			String value = trimmed.substring(prefix.length()).trim();
			try {
				return Math.max(0.0F, Float.parseFloat(value));
			} catch(NumberFormatException exception) {
				MISCTWFHearingLogger.warn(
						"[hearing] invalid hearing multiplier '" + value + "' for " + id +
								" in goal.entity_hearing_multipliers; falling back to 1.0."
				);
				return 1.0F;
			}
		}
		return 1.0F;
	}
}
