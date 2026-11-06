package com.hexagram2021.misc_twf_adventure.common.fluid;

import com.hexagram2021.misc_twf_adventure.common.register.MISCTWFFluids;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;

public interface FluidConstructor<T extends Fluid> {
	T create(MISCTWFFluids.FluidEntry<T> fluidEntry, TagKey<Fluid> fluidTag);
}
