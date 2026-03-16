package com.hexagram2021.misc_twf.common.register;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class MISCTWFBlockTags {
    public static final TagKey<Block> SOUND_BARRIER = create("sound_barrier");

    private MISCTWFBlockTags() {
    }

    public static void init() {
    }

    private static TagKey<Block> create(String name) {
        return TagKey.create(Registries.BLOCK, ResourceLocation.withDefaultNamespace(name));
    }
}
