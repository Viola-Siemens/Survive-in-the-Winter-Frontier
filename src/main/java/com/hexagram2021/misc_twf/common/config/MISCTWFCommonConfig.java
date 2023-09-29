package com.hexagram2021.misc_twf.common.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class MISCTWFCommonConfig {
	private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
	private static final ForgeConfigSpec SPEC;

	public static final ForgeConfigSpec.IntValue MILK_INTERVAL;

	static {
		BUILDER.push("misc_twf-common-config");
			MILK_INTERVAL = BUILDER.comment("The cool down for cows and goats to produce milk (in ticks, for example, 600 for 30 seconds).").defineInRange("MILK_INTERVAL", 1200, 0, 96000);
		BUILDER.pop();

		SPEC = BUILDER.build();
	}

	public static ForgeConfigSpec getConfig() {
		return SPEC;
	}
}
