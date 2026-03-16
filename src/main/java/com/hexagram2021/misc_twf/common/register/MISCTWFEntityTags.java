package com.hexagram2021.misc_twf.common.register;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;

/**
 * 模组实体类型标签注册类喵~
 * 定义用于动物排便等功能的实体类型标签喵~
 *
 * @author liudongyu
 */
public final class MISCTWFEntityTags {
	/** 可排便的动物实体类型标签喵~ */
	public static final TagKey<EntityType<?>> POOPING_ANIMALS = create("pooping_animals");

	private MISCTWFEntityTags() {
	}

	public static void init() {
	}

	@SuppressWarnings("SameParameterValue")
	private static TagKey<EntityType<?>> create(String name) {
		return TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(MODID, name));
	}
}
