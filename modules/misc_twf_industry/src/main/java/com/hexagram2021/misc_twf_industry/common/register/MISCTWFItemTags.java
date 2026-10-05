package com.hexagram2021.misc_twf_industry.common.register;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import static com.hexagram2021.misc_twf_industry.MiscTwfIndustry.CONTENT_NAMESPACE;

/**
 * 工业生态模块物品标签注册类喵~
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

	/** 空方法，用于触发类加载以完成静态字段初始化喵~ */
	public static void init() {
		// Lazy init
	}

	@SuppressWarnings("SameParameterValue")
	private static TagKey<Item> create(String name) {
		return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(CONTENT_NAMESPACE, name));
	}
}
