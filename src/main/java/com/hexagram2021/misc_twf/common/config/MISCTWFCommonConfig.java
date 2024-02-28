package com.hexagram2021.misc_twf.common.config;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

public class MISCTWFCommonConfig {
	private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
	private static final ForgeConfigSpec SPEC;

	public static final ForgeConfigSpec.IntValue MILK_INTERVAL;
	public static final ForgeConfigSpec.IntValue ULTRAVIOLET_LAMPS_RADIUS;
	public static final ForgeConfigSpec.IntValue ANIMAL_POOPING_INTERVAL;
	public static final ForgeConfigSpec.IntValue ANIMAL_POOPING_INTERVAL_NOISE;
	public static final ForgeConfigSpec.IntValue NIGHT_VISION_DEVICE_ENERGY_CAPABILITY;
	public static final ForgeConfigSpec.IntValue ORDINARY_ACCUMULATOR_CAPABILITY;
	public static final ForgeConfigSpec.IntValue MILITARY_ACCUMULATOR_CAPABILITY;
	public static final ForgeConfigSpec.IntValue WAYFARER_ARMOR_CAPABILITY;
	public static final ForgeConfigSpec.ConfigValue<List<? extends String>> WAYFARER_ARMOR_EFFECTS;

	public static final ForgeConfigSpec.BooleanValue ZOMBIE_ANIMALS_CAN_BE_HEALED;

	static {
		BUILDER.push("misc_twf-common-config");
			MILK_INTERVAL = BUILDER.comment("The cool down for cows and goats to produce milk (in seconds).").defineInRange("MILK_INTERVAL", 60, 0, 120000);
			ULTRAVIOLET_LAMPS_RADIUS = BUILDER.comment("The radius (blocks) of ultraviolet lamps to prevent hostiles' spawning.").defineInRange("ULTRAVIOLET_LAMPS_RADIUS", 16, 2, 128);
			ANIMAL_POOPING_INTERVAL = BUILDER.comment("The minimum cool down for animals to poop (in seconds).").defineInRange("ANIMAL_POOPING_INTERVAL", 600, 5, 120000);
			ANIMAL_POOPING_INTERVAL_NOISE = BUILDER.comment("The randomly additional cool down for animals to poop (in seconds).").defineInRange("ANIMAL_POOPING_INTERVAL_NOISE", 120, 0, 120000);
			NIGHT_VISION_DEVICE_ENERGY_CAPABILITY = BUILDER.comment("The maximum time (in seconds) will a night vision device be used without charging.").defineInRange("NIGHT_VISION_DEVICE_ENERGY_CAPABILITY", 6000, 0, 120000);
			ORDINARY_ACCUMULATOR_CAPABILITY = BUILDER.comment("The maximum time (in seconds) will a ordinary accumulator be used without charging.").defineInRange("ORDINARY_ACCUMULATOR_CAPABILITY", 3600, 0, 120000);
			MILITARY_ACCUMULATOR_CAPABILITY = BUILDER.comment("The maximum time (in seconds) will a military accumulator be used without charging.").defineInRange("MILITARY_ACCUMULATOR_CAPABILITY", 18000, 0, 120000);
			WAYFARER_ARMOR_CAPABILITY = BUILDER.comment("The maximum time (in seconds) will a wayfarer armor be used without charging.").defineInRange("WAYFARER_ARMOR_CAPABILITY", 8400, 0, 120000);
			WAYFARER_ARMOR_EFFECTS = BUILDER.comment("When a player wears the entire suit of wayfarer armor, which effects will be applied to this player.").defineList("WAYFARER_ARMOR_EFFECTS", List.of("cold_sweat:ice_resistance"), o -> o instanceof String str && ResourceLocation.isValidResourceLocation(str));

			ZOMBIE_ANIMALS_CAN_BE_HEALED = BUILDER.comment("If true, players can use golden apples to heal zombie animals").define("ZOMBIE_ANIMALS_CAN_BE_HEALED", true);
		BUILDER.pop();

		SPEC = BUILDER.build();
	}

	public static ForgeConfigSpec getConfig() {
		return SPEC;
	}
}
