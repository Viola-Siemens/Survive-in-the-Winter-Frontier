package com.hexagram2021.misc_twf.common.recipe.cache;

import net.minecraft.world.item.crafting.Recipe;

import java.util.List;

public interface IListRecipe {
	List<? extends Recipe<?>> getSubRecipes();
}
