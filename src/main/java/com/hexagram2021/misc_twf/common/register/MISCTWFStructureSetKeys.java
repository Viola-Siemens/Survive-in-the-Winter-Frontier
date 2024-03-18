package com.hexagram2021.misc_twf.common.register;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.StructureSet;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;

public final class MISCTWFStructureSetKeys {
	public static final ResourceKey<StructureSet> BOSS_LAIR = createKey("boss_lair");

	private MISCTWFStructureSetKeys() {
	}

	@SuppressWarnings("SameParameterValue")
	private static ResourceKey<StructureSet> createKey(String name) {
		return ResourceKey.create(Registry.STRUCTURE_SET_REGISTRY, new ResourceLocation(MODID, name));
	}
}
