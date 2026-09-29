package com.hexagram2021.misc_twf_wildlife.common.config;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * 农牧生态模块的通用配置喵~
 *
 * @author liudongyu
 */
public final class MISCTWFWildlifeConfig {
	private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

	/** 牛与山羊产奶冷却（秒）喵~ */
	public static final ModConfigSpec.IntValue MILK_INTERVAL = BUILDER
			.comment("The cool down for cows and goats to produce milk (in seconds).")
			.defineInRange("MILK_INTERVAL", 60, 0, 120000);
	/** 动物排便最小冷却（秒）喵~ */
	public static final ModConfigSpec.IntValue ANIMAL_POOPING_INTERVAL = BUILDER
			.comment("The minimum cool down for animals to poop (in seconds).")
			.defineInRange("ANIMAL_POOPING_INTERVAL", 600, 5, 120000);
	/** 动物排便额外随机冷却（秒）喵~ */
	public static final ModConfigSpec.IntValue ANIMAL_POOPING_INTERVAL_NOISE = BUILDER
			.comment("The randomly additional cool down for animals to poop (in seconds).")
			.defineInRange("ANIMAL_POOPING_INTERVAL_NOISE", 120, 0, 120000);

	private static final ModConfigSpec CONFIG_SPEC = BUILDER.build();

	/**
	 * 获取配置规格喵~
	 *
	 * @return 配置规格喵~
	 */
	public static ModConfigSpec getConfig() {
		return CONFIG_SPEC;
	}

	private MISCTWFWildlifeConfig() {
	}
}
