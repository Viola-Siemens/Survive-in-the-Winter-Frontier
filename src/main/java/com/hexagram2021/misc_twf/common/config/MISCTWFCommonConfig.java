package com.hexagram2021.misc_twf.common.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class MISCTWFCommonConfig {
	private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
	private static final ForgeConfigSpec SPEC;

	public static final ForgeConfigSpec.IntValue MILK_INTERVAL;
	public static final ForgeConfigSpec.IntValue NIGHT_VISION_DEVICE_ENERGY_CAPABILITY;
	public static final ForgeConfigSpec.IntValue ORDINARY_ACCUMULATOR_CAPABILITY;
	public static final ForgeConfigSpec.IntValue MILITARY_ACCUMULATOR_CAPABILITY;
	public static final ForgeConfigSpec.IntValue WAYFARER_ARMOR_CAPABILITY;

	public static final ForgeConfigSpec.BooleanValue ZOMBIE_ANIMALS_CAN_BE_HEALED;

	static {
		BUILDER.push("misc_twf-common-config");
			MILK_INTERVAL = BUILDER.comment("The cool down for cows and goats to produce milk (in seconds).").defineInRange("MILK_INTERVAL", 60, 0, 120000);
			NIGHT_VISION_DEVICE_ENERGY_CAPABILITY = BUILDER.comment("The maximum time (in seconds) will a night vision device be used without charging.").defineInRange("NIGHT_VISION_DEVICE_ENERGY_CAPABILITY", 6000, 0, 120000);
			ORDINARY_ACCUMULATOR_CAPABILITY = BUILDER.comment("The maximum time (in seconds) will a ordinary accumulator be used without charging.").defineInRange("ORDINARY_ACCUMULATOR_CAPABILITY", 3600, 0, 120000);
			MILITARY_ACCUMULATOR_CAPABILITY = BUILDER.comment("The maximum time (in seconds) will a military accumulator be used without charging.").defineInRange("MILITARY_ACCUMULATOR_CAPABILITY", 18000, 0, 120000);
			WAYFARER_ARMOR_CAPABILITY = BUILDER.comment("The maximum time (in seconds) will a wayfarer armor be used without charging.").defineInRange("WAYFARER_ARMOR_CAPABILITY", 8400, 0, 120000);

			ZOMBIE_ANIMALS_CAN_BE_HEALED = BUILDER.comment("If true, players can use golden apples to heal zombie animals").define("ZOMBIE_ANIMALS_CAN_BE_HEALED", true);
		BUILDER.pop();

		SPEC = BUILDER.build();
	}

	public static ForgeConfigSpec getConfig() {
		return SPEC;
	}
}
