package com.hexagram2021.misc_twf_zombie_animals.common.config;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * 僵尸动物模块的通用配置喵~
 *
 * @author liudongyu
 */
public final class MISCTWFZombieAnimalsConfig {
	private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

	/** 是否允许玩家用金苹果治愈僵尸动物喵~ */
	public static final ModConfigSpec.BooleanValue ZOMBIE_ANIMALS_CAN_BE_HEALED = BUILDER
			.comment("If true, players can use golden apples to heal zombie animals")
			.define("ZOMBIE_ANIMALS_CAN_BE_HEALED", true);

	private static final ModConfigSpec CONFIG_SPEC = BUILDER.build();

	/**
	 * 获取配置规格喵~
	 *
	 * @return 配置规格喵~
	 */
	public static ModConfigSpec getConfig() {
		return CONFIG_SPEC;
	}

	private MISCTWFZombieAnimalsConfig() {
	}
}
