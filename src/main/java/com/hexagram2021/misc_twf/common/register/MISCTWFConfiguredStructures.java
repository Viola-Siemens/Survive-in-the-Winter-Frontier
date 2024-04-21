package com.hexagram2021.misc_twf.common.register;

import com.google.common.collect.Lists;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.StructureSpawnOverride;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Map;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;

public final class MISCTWFConfiguredStructures {
	private static final List<MobSpawnSettings.SpawnerData> MONSTER_OVERRIDE_LIST = Util.make(() -> {
		List<MobSpawnSettings.SpawnerData> ret = Lists.newArrayList();
		ret.add(new MobSpawnSettings.SpawnerData(EntityType.ZOMBIE, 30, 2, 4));
		EntityType<?> NIGHT_HUNTER = ForgeRegistries.ENTITIES.getValue(new ResourceLocation("zombie_extreme", "night_hunter"));
		if(NIGHT_HUNTER != null) {
			ret.add(new MobSpawnSettings.SpawnerData(NIGHT_HUNTER, 5, 1, 2));
		}
		EntityType<?> INFECTED_JUGGERNAUT = ForgeRegistries.ENTITIES.getValue(new ResourceLocation("zombie_extreme", "infected_juggernaut"));
		if(INFECTED_JUGGERNAUT != null) {
			ret.add(new MobSpawnSettings.SpawnerData(INFECTED_JUGGERNAUT, 10, 1, 2));
		}
		EntityType<?> INFECTED_HAZMAT = ForgeRegistries.ENTITIES.getValue(new ResourceLocation("zombie_extreme", "infected_hazmat"));
		if(INFECTED_HAZMAT != null) {
			ret.add(new MobSpawnSettings.SpawnerData(INFECTED_HAZMAT, 5, 1, 2));
		}
		EntityType<?> SMOKER = ForgeRegistries.ENTITIES.getValue(new ResourceLocation("undead_revamp2", "thesmoker"));
		if(SMOKER != null) {
			ret.add(new MobSpawnSettings.SpawnerData(SMOKER, 3, 1, 2));
		}
		EntityType<?> BOMBER = ForgeRegistries.ENTITIES.getValue(new ResourceLocation("undead_revamp2", "bomber"));
		if(BOMBER != null) {
			ret.add(new MobSpawnSettings.SpawnerData(BOMBER, 2, 1, 2));
		}
		EntityType<?> SPITTER = ForgeRegistries.ENTITIES.getValue(new ResourceLocation("undead_revamp2", "thespitter"));
		if(SPITTER != null) {
			ret.add(new MobSpawnSettings.SpawnerData(SPITTER, 2, 1, 2));
		}
		EntityType<?> SUCKER = ForgeRegistries.ENTITIES.getValue(new ResourceLocation("undead_revamp2", "sucker"));
		if(SUCKER != null) {
			ret.add(new MobSpawnSettings.SpawnerData(SUCKER, 3, 1, 2));
		}
		EntityType<?> HUNTER = ForgeRegistries.ENTITIES.getValue(new ResourceLocation("undead_revamp2", "thehunter"));
		if(HUNTER != null) {
			ret.add(new MobSpawnSettings.SpawnerData(HUNTER, 4, 1, 2));
		}
		EntityType<?> RAT_KING = ForgeRegistries.ENTITIES.getValue(new ResourceLocation("zombie_extreme", "rat_king"));
		if(RAT_KING != null) {
			ret.add(new MobSpawnSettings.SpawnerData(RAT_KING, 1, 1, 2));
		}
		EntityType<?> INFLATED = ForgeRegistries.ENTITIES.getValue(new ResourceLocation("zombie_extreme", "inflated"));
		if(INFLATED != null) {
			ret.add(new MobSpawnSettings.SpawnerData(INFLATED, 1, 1, 2));
		}
		EntityType<?> PATIENT_ZERO = ForgeRegistries.ENTITIES.getValue(new ResourceLocation("zombie_extreme", "patient_zero"));
		if(PATIENT_ZERO != null) {
			ret.add(new MobSpawnSettings.SpawnerData(PATIENT_ZERO, 1, 1, 2));
		}
		EntityType<?> CHAINSAW = ForgeRegistries.ENTITIES.getValue(new ResourceLocation("zombie_extreme", "chainsaw"));
		if(CHAINSAW != null) {
			ret.add(new MobSpawnSettings.SpawnerData(CHAINSAW, 1, 1, 2));
		}
		return ret;
	});

	public static final Holder<ConfiguredStructureFeature<?, ?>> BOSS_LAIR = register(
			"boss_lair",
			MISCTWFStructures.BOSS_LAIR.configured(
					NoneFeatureConfiguration.INSTANCE, MISCTWFBiomeTags.HAS_BOSS_LAIR, true,
					Map.of(
							MobCategory.MONSTER, new StructureSpawnOverride(
									StructureSpawnOverride.BoundingBoxType.STRUCTURE,
									WeightedRandomList.create(MONSTER_OVERRIDE_LIST)
							)
					)
			)
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
