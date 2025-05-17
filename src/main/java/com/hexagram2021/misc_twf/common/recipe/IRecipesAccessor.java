package com.hexagram2021.misc_twf.common.recipe;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.Map;

public interface IRecipesAccessor {
	Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> misc_twf$recipes();
	Map<ResourceLocation, Recipe<?>> misc_twf$byName();

	void misc_twf$setRecipes(Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> recipes);
	void misc_twf$setByName(Map<ResourceLocation, Recipe<?>> byName);
}
