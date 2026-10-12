package com.hexagram2021.misc_twf_hearing.api;

import net.minecraft.resources.ResourceLocation;

/**
 * 噪声规格喵~：描述"一次噪声应该被怎样听见"的不可变参数（不含位置与来源实体）。
 *
 * <p>构造时做防御性校验：{@code radius} 必须为有限正数，{@code strength} 必须为非负有限数并 clamp 到 1.0，
 * {@code lifetimeTicks} 至少为 1；NaN / Infinity 一律拒绝，避免异常数据进入空间索引喵~</p>
 *
 * <p>{@code lifetimeTicks} 由类型配置的区间随机掷出（{@code min + random.nextInt(max - min)}），
 * 因此同一行为每次的存活时长并不相同喵~</p>
 *
 * @author liudongyu
 */
public record NoiseSpec(ResourceLocation type, float radius, float strength, int lifetimeTicks) {
	/**
	 * 构造函数
	 *
	 * @param type          噪声类型（见 {@link NoiseTypes}）喵~
	 * @param radius        基础传播半径（格），实际可听距离为 {@code radius × hearingMultiplier} 喵~
	 * @param strength      同一怪物比较多个声音时使用的强度（0.0 ~ 1.0）喵~
	 * @param lifetimeTicks 存活时长（tick）喵~
	 */
	public NoiseSpec {
		if(type == null) {
			throw new IllegalArgumentException("noise type must not be null");
		}
		if(!Float.isFinite(radius) || radius <= 0.0F) {
			throw new IllegalArgumentException("noise radius must be finite and > 0, but was " + radius);
		}
		if(!Float.isFinite(strength) || strength < 0.0F) {
			throw new IllegalArgumentException("noise strength must be finite and >= 0, but was " + strength);
		}
		if(lifetimeTicks <= 0) {
			throw new IllegalArgumentException("noise lifetimeTicks must be > 0, but was " + lifetimeTicks);
		}
		strength = Math.min(1.0F, strength);
	}
}
