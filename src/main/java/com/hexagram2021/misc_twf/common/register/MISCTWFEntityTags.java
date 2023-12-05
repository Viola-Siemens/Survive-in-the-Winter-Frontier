package com.hexagram2021.misc_twf.common.register;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;

public final class MISCTWFEntityTags {
	public static final TagKey<EntityType<?>> POOPING_ANIMALS = create("pooping_animals");

	private MISCTWFEntityTags() {
	}

	public static void init() {
	}

	@SuppressWarnings("SameParameterValue")
	private static TagKey<EntityType<?>> create(String name) {
		return TagKey.create(Registry.ENTITY_TYPE_REGISTRY, new ResourceLocation(MODID, name));
	}
}
