package com.hexagram2021.misc_twf_hearing.common.config;

import com.hexagram2021.misc_twf_hearing.api.NoiseSpec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * 单个噪声类型的配置视图喵~
 *
 * <p>持有配置项本身而不是快照值，因此整合包在运行期修改配置后立即生效喵~</p>
 *
 * @author liudongyu
 */
public final class NoiseTypeSettings {
	private final ModConfigSpec.DoubleValue radius;
	private final ModConfigSpec.DoubleValue strength;
	private final ModConfigSpec.IntValue lifetimeMin;
	private final ModConfigSpec.IntValue lifetimeMax;

	NoiseTypeSettings(ModConfigSpec.DoubleValue radius, ModConfigSpec.DoubleValue strength,
			ModConfigSpec.IntValue lifetimeMin, ModConfigSpec.IntValue lifetimeMax) {
		this.radius = radius;
		this.strength = strength;
		this.lifetimeMin = lifetimeMin;
		this.lifetimeMax = lifetimeMax;
	}

	/**
	 * 获取基础传播半径（格）喵~
	 *
	 * @return 基础传播半径喵~
	 */
	public float radius() {
		return this.radius.get().floatValue();
	}

	/**
	 * 获取噪声强度（0.0 ~ 1.0）喵~
	 *
	 * @return 噪声强度喵~
	 */
	public float strength() {
		return this.strength.get().floatValue();
	}

	/**
	 * 获取存活时长下界（tick，含）喵~
	 *
	 * @return 存活时长下界喵~
	 */
	public int lifetimeMin() {
		return this.lifetimeMin.get();
	}

	/**
	 * 获取存活时长上界（tick，不含）喵~
	 *
	 * @return 存活时长上界喵~
	 */
	public int lifetimeMax() {
		return this.lifetimeMax.get();
	}

	/**
	 * 按区间随机掷出本次噪声的存活时长喵~
	 *
	 * @param random 随机源喵~
	 * @return 存活时长（tick）喵~
	 */
	public int rollLifetime(RandomSource random) {
		int min = this.lifetimeMin();
		int max = Math.max(min + 1, this.lifetimeMax());
		return min + random.nextInt(max - min);
	}

	/**
	 * 按本类型配置掷出一个噪声规格喵~
	 *
	 * @param type   噪声类型 id喵~
	 * @param random 随机源喵~
	 * @return 噪声规格喵~
	 */
	public NoiseSpec rollSpec(ResourceLocation type, RandomSource random) {
		return new NoiseSpec(type, this.radius(), this.strength(), this.rollLifetime(random));
	}
}
