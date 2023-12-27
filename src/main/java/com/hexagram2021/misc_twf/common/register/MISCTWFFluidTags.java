package com.hexagram2021.misc_twf.common.register;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.ForgeRegistries;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;

public final class MISCTWFFluidTags {
	public static final TagKey<Fluid> BLOOD = create("blood");

	private MISCTWFFluidTags() {
	}

	@SuppressWarnings("SameParameterValue")
	private static TagKey<Fluid> create(String name) {
		return TagKey.create(ForgeRegistries.Keys.FLUIDS, new ResourceLocation(MODID, name));
	}
}
