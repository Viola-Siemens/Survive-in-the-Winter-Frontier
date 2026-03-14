package com.hexagram2021.misc_twf.common.register;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;

/**
 * 模组物品标签注册类喵~
 * 定义用于蓄电池和远行者装甲等功能的物品标签喵~
 *
 * @author liudongyu
 */
public final class MISCTWFItemTags {
	/** 蓄电池物品标签喵~ */
	public static final TagKey<Item> BATTERY = create("battery");
	/** 远行者系列盔甲物品标签喵~ */
	public static final TagKey<Item> WAYFARER_ARMORS = create("wayfarer_armors");

	private MISCTWFItemTags() {
	}

	public static void init() {
	}

	private static TagKey<Item> create(String name) {
		return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(MODID, name));
	}
}
