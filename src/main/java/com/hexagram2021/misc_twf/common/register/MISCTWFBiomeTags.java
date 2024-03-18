package com.hexagram2021.misc_twf.common.register;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;

public final class MISCTWFBiomeTags {
	public static final TagKey<Biome> HAS_BOSS_LAIR = create("has_structure/boss_lair");

	@SuppressWarnings("SameParameterValue")
	private static TagKey<Biome> create(String name) {
		return TagKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(MODID, name));
	}

	private MISCTWFBiomeTags() {
	}
}
