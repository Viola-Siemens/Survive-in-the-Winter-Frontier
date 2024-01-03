package com.hexagram2021.misc_twf.common.recipe.compat;

import com.hexagram2021.misc_twf.common.recipe.MoldDetacherRecipe;
import com.hexagram2021.misc_twf.common.recipe.cache.CachedRecipeList;
import com.hexagram2021.misc_twf.common.register.MISCTWFBlocks;
import com.hexagram2021.misc_twf.common.util.MISCTWFLogger;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.*;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;

@JeiPlugin
public class JEIHelper implements IModPlugin {
	public interface MISCTWFJEIRecipeTypes {
		RecipeType<MoldDetacherRecipe> MOLD_DETACHER = new RecipeType<>(MoldDetacherRecipeCategory.UID, MoldDetacherRecipe.class);
	}

	private static final ResourceLocation UID = new ResourceLocation(MODID, "main");

	@Override
	public ResourceLocation getPluginUid() {
		return UID;
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registry) {
		//Recipes
		IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();
		registry.addRecipeCategories(
				new MoldDetacherRecipeCategory(guiHelper)
		);
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		MISCTWFLogger.info("Adding recipes to JEI!!");
		registration.addRecipes(MISCTWFJEIRecipeTypes.MOLD_DETACHER, getRecipes(MoldDetacherRecipe.recipeList));
	}

	@SuppressWarnings("SameParameterValue")
	private static <T extends Recipe<?>> List<T> getRecipes(CachedRecipeList<T> cachedList) {
		return new ArrayList<>(cachedList.getRecipes(Objects.requireNonNull(Minecraft.getInstance().level)));
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
		registration.addRecipeCatalyst(new ItemStack(MISCTWFBlocks.MOLD_DETACHER), MISCTWFJEIRecipeTypes.MOLD_DETACHER);
	}
}
