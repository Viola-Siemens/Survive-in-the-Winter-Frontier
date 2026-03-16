package com.hexagram2021.misc_twf.common.register;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;

/**
 * 模组流体标签注册类喵~
 * 定义用于血液等自定义流体的标签喵~
 *
 * @author liudongyu
 */
public final class MISCTWFFluidTags {
	/** 血液流体标签喵~ */
	public static final TagKey<Fluid> BLOOD = create("blood");

	private MISCTWFFluidTags() {
	}

	@SuppressWarnings("SameParameterValue")
	private static TagKey<Fluid> create(String name) {
		return TagKey.create(Registries.FLUID, ResourceLocation.fromNamespaceAndPath(MODID, name));
	}
}
