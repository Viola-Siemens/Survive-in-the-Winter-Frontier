package com.hexagram2021.misc_twf.common.register;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;

/**
 * 模组生物群系标签注册类喵~
 * 定义用于结构生成等功能的生物群系标签喵~
 *
 * @author liudongyu
 */
public final class MISCTWFBiomeTags {
	/** Boss 巢穴结构可生成的生物群系标签喵~ */
	public static final TagKey<Biome> HAS_BOSS_LAIR = create("has_structure/boss_lair");

	@SuppressWarnings("SameParameterValue")
	private static TagKey<Biome> create(String name) {
		return TagKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(MODID, name));
	}

	private MISCTWFBiomeTags() {
	}
}
