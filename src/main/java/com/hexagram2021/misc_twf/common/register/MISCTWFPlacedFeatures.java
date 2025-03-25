package com.hexagram2021.misc_twf.common.register;

import net.minecraft.core.Holder;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.placement.*;

import java.util.List;

public final class MISCTWFPlacedFeatures {
	public static final Holder<PlacedFeature> MONSTER_EGG = BuiltinRegistries.register(
			BuiltinRegistries.PLACED_FEATURE, "monster_egg",
			new PlacedFeature(MISCTWFConfiguredFeatures.MONSTER_EGG, List.of(
					CountPlacement.of(32),
					InSquarePlacement.spread(),
					HeightRangePlacement.uniform(VerticalAnchor.absolute(-56), VerticalAnchor.absolute(40)),
					BiomeFilter.biome()
			))
	);

	private MISCTWFPlacedFeatures() {
	}

	public static void init() {
	}
}
