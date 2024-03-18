package com.hexagram2021.misc_twf.common.register;

import net.minecraft.core.Holder;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;

public final class MISCTWFConfiguredStructures {
	public static final Holder<ConfiguredStructureFeature<?, ?>> BOSS_LAIR = register(
			"boss_lair",
			MISCTWFStructures.BOSS_LAIR.configured(NoneFeatureConfiguration.INSTANCE, MISCTWFBiomeTags.HAS_BOSS_LAIR, true)
	);

	private MISCTWFConfiguredStructures() {
	}

	@SuppressWarnings("SameParameterValue")
	private static Holder<ConfiguredStructureFeature<?, ?>> register(String name, ConfiguredStructureFeature<?, ?> configuredStructureFeature) {
		return BuiltinRegistries.register(BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE, new ResourceLocation(MODID, name), configuredStructureFeature);
	}

	public static void init() {
	}
}
