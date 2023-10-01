package com.hexagram2021.misc_twf.common.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class MISCTWFCommonConfig {
	private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
	private static final ForgeConfigSpec SPEC;

	public static final ForgeConfigSpec.IntValue MILK_INTERVAL;
	public static final ForgeConfigSpec.IntValue NIGHT_VISION_DEVICE_ENERGY_CAPABILITY;

	static {
		BUILDER.push("misc_twf-common-config");
			MILK_INTERVAL = BUILDER.comment("The cool down for cows and goats to produce milk (in seconds).").defineInRange("MILK_INTERVAL", 60, 0, 120000);
			NIGHT_VISION_DEVICE_ENERGY_CAPABILITY = BUILDER.comment("The maximum time (in seconds) will a night vision device be used without charging.").defineInRange("NIGHT_VISION_DEVICE_ENERGY_CAPABILITY", 3600, 0, 120000);
		BUILDER.pop();

		SPEC = BUILDER.build();
	}

	public static ForgeConfigSpec getConfig() {
		return SPEC;
	}
}
