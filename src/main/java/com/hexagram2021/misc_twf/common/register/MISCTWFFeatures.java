package com.hexagram2021.misc_twf.common.register;

import com.hexagram2021.misc_twf.common.world.features.MonsterEggFeature;
import net.minecraft.world.level.levelgen.feature.Feature;

import java.util.function.Consumer;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;

public final class MISCTWFFeatures {
	public static final MonsterEggFeature MONSTER_EGG = new MonsterEggFeature(MonsterEggFeature.MonsterEggFeatureConfiguration.CODEC);

	public static void init(Consumer<Feature<?>> register) {
		MONSTER_EGG.setRegistryName(MODID, "monster_egg");
		register.accept(MONSTER_EGG);
	}
}
