package com.hexagram2021.misc_twf.common.register;

import net.minecraft.core.Holder;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;

import java.util.List;

@SuppressWarnings("unused")
public final class MISCTWFStructureSets {
	public static final Holder<StructureSet> BOSS_LAIR = register(
			MISCTWFStructureSetKeys.BOSS_LAIR,
			MISCTWFConfiguredStructures.BOSS_LAIR,
			new RandomSpreadStructurePlacement(200, 50, RandomSpreadType.TRIANGULAR, 1460517424)
	);

	private MISCTWFStructureSets() {
	}

	private static Holder<StructureSet> register(ResourceKey<StructureSet> key, StructureSet set) {
		return BuiltinRegistries.register(BuiltinRegistries.STRUCTURE_SETS, key, set);
	}

	@SuppressWarnings("SameParameterValue")
	private static Holder<StructureSet> register(ResourceKey<StructureSet> key, Holder<ConfiguredStructureFeature<?, ?>> structure, StructurePlacement placement) {
		return register(key, new StructureSet(structure, placement));
	}

	private static Holder<StructureSet> register(ResourceKey<StructureSet> key, List<StructureSet.StructureSelectionEntry> structures, StructurePlacement placement) {
		return register(key, new StructureSet(structures, placement));
	}

	public static void init() {
	}
}
