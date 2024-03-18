package com.hexagram2021.misc_twf.common.register;

import com.hexagram2021.misc_twf.common.world.structures.BossLairFeature;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import java.util.function.Consumer;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;

public final class MISCTWFStructures {
	public static final BossLairFeature BOSS_LAIR = new BossLairFeature(NoneFeatureConfiguration.CODEC);

	public static void init(Consumer<StructureFeature<?>> register) {
		register(BOSS_LAIR, "boss_lair", register);
	}

	@SuppressWarnings("SameParameterValue")
	private static void register(StructureFeature<?> entry, String name, Consumer<StructureFeature<?>> consumer) {
		entry.setRegistryName(new ResourceLocation(MODID, name));
		consumer.accept(entry);
	}
}
