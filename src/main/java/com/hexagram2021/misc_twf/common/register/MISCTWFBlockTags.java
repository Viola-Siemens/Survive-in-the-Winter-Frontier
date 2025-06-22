package com.hexagram2021.misc_twf.common.register;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;

public class MISCTWFBlockTags {
    public static final TagKey<Block> SOUND_BARRIER = create("sound_barrier");

    private MISCTWFBlockTags() {
    }

    public static void init() {
    }

    private static TagKey<Block> create(String name) {
        return TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation(name));
    }
}
