package com.hexagram2021.misc_twf.common.register;

import com.google.common.collect.ImmutableList;
import com.hexagram2021.misc_twf.common.block.entity.MonsterEggBlockEntity;
import com.hexagram2021.misc_twf.common.world.features.MonsterEggFeature;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.random.Weight;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraftforge.registries.ForgeRegistries;

public final class MISCTWFConfiguredFeatures {
	public static final Holder<ConfiguredFeature<?, ?>> MONSTER_EGG = BuiltinRegistries.register(
			BuiltinRegistries.CONFIGURED_FEATURE, "monster_egg", new ConfiguredFeature<>(MISCTWFFeatures.MONSTER_EGG, new MonsterEggFeature.MonsterEggFeatureConfiguration(
					Util.make(() -> {
						ImmutableList.Builder<MonsterEggBlockEntity.MonsterEggEntry> builder = ImmutableList.builder();
						builder.add(new MonsterEggBlockEntity.MonsterEggEntry(EntityType.ZOMBIE, Weight.of(6)));
						EntityType<?> NIGHT_HUNTER = ForgeRegistries.ENTITIES.getValue(new ResourceLocation("zombie_extreme", "night_hunter"));
						if(NIGHT_HUNTER != null) {
							builder.add(new MonsterEggBlockEntity.MonsterEggEntry(NIGHT_HUNTER, Weight.of(5)));
						}
						EntityType<?> INFECTED_JUGGERNAUT = ForgeRegistries.ENTITIES.getValue(new ResourceLocation("zombie_extreme", "infected_juggernaut"));
						if(INFECTED_JUGGERNAUT != null) {
							builder.add(new MonsterEggBlockEntity.MonsterEggEntry(INFECTED_JUGGERNAUT, Weight.of(2)));
						}
						EntityType<?> INFECTED_HAZMAT = ForgeRegistries.ENTITIES.getValue(new ResourceLocation("zombie_extreme", "infected_hazmat"));
						if(INFECTED_HAZMAT != null) {
							builder.add(new MonsterEggBlockEntity.MonsterEggEntry(INFECTED_HAZMAT, Weight.of(2)));
						}
						EntityType<?> PATIENT_ZERO = ForgeRegistries.ENTITIES.getValue(new ResourceLocation("zombie_extreme", "patient_zero"));
						if(PATIENT_ZERO != null) {
							builder.add(new MonsterEggBlockEntity.MonsterEggEntry(PATIENT_ZERO, Weight.of(2)));
						}
						return builder.build();
					})
			))
	);

	private MISCTWFConfiguredFeatures() {
	}

	public static void init() {
	}
}
