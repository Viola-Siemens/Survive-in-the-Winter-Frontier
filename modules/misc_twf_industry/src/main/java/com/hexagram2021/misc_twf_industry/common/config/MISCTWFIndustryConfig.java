package com.hexagram2021.misc_twf_industry.common.config;

import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

/**
 * 工业生态模块的通用配置喵~
 *
 * <p>包含紫外线灯作用半径、能量装备容量与远行者护甲套装效果等随本模块域的键喵~</p>
 *
 * @author liudongyu
 */
@SuppressWarnings("java:S4968")
public final class MISCTWFIndustryConfig {
	private static final String REGISTRY_NAME_MATCHER = "([a-z0-9_.-]+:[a-z0-9_/.-]+)";

	private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

	/** 紫外线灯阻止刷怪的作用半径（方块）喵~ */
	public static final ModConfigSpec.IntValue ULTRAVIOLET_LAMPS_RADIUS = BUILDER
			.comment("The radius (blocks) of ultraviolet lamps to prevent hostiles' spawning.")
			.defineInRange("ULTRAVIOLET_LAMPS_RADIUS", 16, 2, 128);
	/** 夜视仪满能量可持续使用时间（秒）喵~ */
	public static final ModConfigSpec.IntValue NIGHT_VISION_DEVICE_ENERGY_CAPABILITY = BUILDER
			.comment("The maximum time (in seconds) will a night vision device be used without charging.")
			.defineInRange("NIGHT_VISION_DEVICE_ENERGY_CAPABILITY", 6000, 0, 120000);
	/** 普通蓄电池容量（秒）喵~ */
	public static final ModConfigSpec.IntValue ORDINARY_ACCUMULATOR_CAPABILITY = BUILDER
			.comment("The maximum time (in seconds) will a ordinary accumulator be used without charging.")
			.defineInRange("ORDINARY_ACCUMULATOR_CAPABILITY", 3600, 0, 120000);
	/** 军用蓄电池容量（秒）喵~ */
	public static final ModConfigSpec.IntValue MILITARY_ACCUMULATOR_CAPABILITY = BUILDER
			.comment("The maximum time (in seconds) will a military accumulator be used without charging.")
			.defineInRange("MILITARY_ACCUMULATOR_CAPABILITY", 18000, 0, 120000);
	/** 远行者护甲容量（秒）喵~ */
	public static final ModConfigSpec.IntValue WAYFARER_ARMOR_CAPABILITY = BUILDER
			.comment("The maximum time (in seconds) will a wayfarer armor be used without charging.")
			.defineInRange("WAYFARER_ARMOR_CAPABILITY", 8400, 0, 120000);
	/** 穿戴整套远行者护甲时附加的效果列表喵~ */
	public static final ModConfigSpec.ConfigValue<List<? extends String>> WAYFARER_ARMOR_EFFECTS = BUILDER
			.comment("When a player wears the entire suit of wayfarer armor, which effects will be applied to this player.")
			.defineList(
					"WAYFARER_ARMOR_EFFECTS", List.of("cold_sweat:ice_resistance"),
					() -> "minecraft:effect_name",
					o -> o instanceof String str && str.matches(REGISTRY_NAME_MATCHER)
			);

	private static final ModConfigSpec CONFIG_SPEC = BUILDER.build();

	/**
	 * 获取配置规格喵~
	 *
	 * @return 配置规格喵~
	 */
	public static ModConfigSpec getConfig() {
		return CONFIG_SPEC;
	}

	private MISCTWFIndustryConfig() {
	}
}
