package com.hexagram2021.misc_twf.common.register;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;

public final class MISCTWFItemTags {
	public static final TagKey<Item> BATTERY = create("battery");
	public static final TagKey<Item> WAYFARER_ARMORS = create("wayfarer_armors");

	private MISCTWFItemTags() {
	}

	public static void init() {
	}

	private static TagKey<Item> create(String name) {
		return TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(MODID, name));
	}
}
