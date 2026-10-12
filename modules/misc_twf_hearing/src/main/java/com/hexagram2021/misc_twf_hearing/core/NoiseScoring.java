package com.hexagram2021.misc_twf_hearing.core;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

/**
 * 噪声评分喵~：全局唯一的评分实现，禁止在 Goal、API 或第三方代码里复制不同公式。
 *
 * <pre>
 * effectiveRadius = radius × hearingMultiplier
 * distanceFactor  = clamp(1 - distance / effectiveRadius, 0, 1)
 * remaining       = clamp((expireTick - now) / (expireTick - lastEmitTick), 0, 1)
 * freshnessFactor = 0.5 + 0.5 × remaining
 * score           = strength × distanceFactor × freshnessFactor
 * </pre>
 *
 * <p>评分只反映"多值得去看一眼"，与攻击优先级、仇恨、Target 权重无关喵~</p>
 *
 * @author liudongyu
 */
public final class NoiseScoring {
	/**
	 * 计算有效听觉半径喵~
	 *
	 * @param pulse             噪声喵~
	 * @param hearingMultiplier 听觉倍率喵~
	 * @return 有效半径（格）喵~
	 */
	public static float effectiveRadius(NoisePulse pulse, float hearingMultiplier) {
		return Math.max(0.0F, pulse.radius() * Math.max(0.0F, hearingMultiplier));
	}

	/**
	 * 判断监听点是否能听见该噪声喵~
	 *
	 * @param pulse             噪声喵~
	 * @param listener          监听点喵~
	 * @param hearingMultiplier 听觉倍率喵~
	 * @return 是否可听喵~
	 */
	public static boolean isAudible(NoisePulse pulse, Vec3 listener, float hearingMultiplier) {
		float radius = effectiveRadius(pulse, hearingMultiplier);
		return pulse.position().distanceToSqr(listener) <= (double) radius * (double) radius;
	}

	/**
	 * 计算噪声对指定监听点的调查评分喵~
	 *
	 * @param pulse             噪声喵~
	 * @param listener          监听点喵~
	 * @param hearingMultiplier 听觉倍率喵~
	 * @param now               当前游戏时间（tick）喵~
	 * @return 评分（0.0 ~ 1.0）喵~
	 */
	public static float score(NoisePulse pulse, Vec3 listener, float hearingMultiplier, long now) {
		float radius = effectiveRadius(pulse, hearingMultiplier);
		if(radius <= 0.0F) {
			return 0.0F;
		}
		double distance = Math.sqrt(pulse.position().distanceToSqr(listener));
		float distanceFactor = (float) Mth.clamp(1.0D - distance / radius, 0.0D, 1.0D);
		long span = Math.max(1L, pulse.expireTick() - pulse.lastEmitTick());
		float remaining = (float) Mth.clamp((double) (pulse.expireTick() - now) / (double) span, 0.0D, 1.0D);
		float freshnessFactor = 0.5F + 0.5F * remaining;
		return pulse.strength() * distanceFactor * freshnessFactor;
	}

	private NoiseScoring() {
	}
}
