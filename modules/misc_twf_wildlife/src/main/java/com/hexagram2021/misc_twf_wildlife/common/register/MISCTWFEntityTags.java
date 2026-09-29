package com.hexagram2021.misc_twf_wildlife.common.register;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

import static com.hexagram2021.misc_twf_wildlife.MiscTwfWildlife.CONTENT_NAMESPACE;

/**
 * 农牧生态模块实体类型标签注册类喵~
 *
 * @author liudongyu
 */
public final class MISCTWFEntityTags {
	/** 可排便的动物实体类型标签喵~ */
	public static final TagKey<EntityType<?>> POOPING_ANIMALS = create("pooping_animals");

	private MISCTWFEntityTags() {
	}

	public static void init() {
		// Lazy init
	}

	@SuppressWarnings("SameParameterValue")
	private static TagKey<EntityType<?>> create(String name) {
		return TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CONTENT_NAMESPACE, name));
	}
}
