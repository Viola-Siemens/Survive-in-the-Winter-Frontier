package com.hexagram2021.misc_twf.common.recipe.compat;

import com.hexagram2021.misc_twf.client.screen.RecoveryFurnaceScreen;
import com.hexagram2021.misc_twf.common.block.entity.MoldWorkbenchBlockEntity;
import com.hexagram2021.misc_twf.common.block.entity.RecoveryFurnaceBlockEntity;
import com.hexagram2021.misc_twf.common.menu.MoldWorkbenchMenu;
import com.hexagram2021.misc_twf.common.menu.RecoveryFurnaceMenu;
import com.hexagram2021.misc_twf.common.recipe.MoldDetacherRecipe;
import com.hexagram2021.misc_twf.common.recipe.MoldWorkbenchRecipe;
import com.hexagram2021.misc_twf.common.recipe.RecoveryFurnaceRecipe;
import com.hexagram2021.misc_twf.common.recipe.cache.CachedRecipeList;
import com.hexagram2021.misc_twf.common.register.MISCTWFBlocks;
import com.hexagram2021.misc_twf.common.util.MISCTWFLogger;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
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
		RecipeType<MoldWorkbenchRecipe> MOLD_WORKBENCH = new RecipeType<>(MoldWorkbenchRecipeCategory.UID, MoldWorkbenchRecipe.class);
		RecipeType<RecoveryFurnaceRecipe> RECOVERY_FURNACE = new RecipeType<>(RecoveryFurnaceRecipeCategory.UID, RecoveryFurnaceRecipe.class);
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
				new MoldDetacherRecipeCategory(guiHelper),
				new MoldWorkbenchRecipeCategory(guiHelper),
				new RecoveryFurnaceRecipeCategory(guiHelper)
		);
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		MISCTWFLogger.info("Adding recipes to JEI!!");
		registration.addRecipes(MISCTWFJEIRecipeTypes.MOLD_DETACHER, getRecipes(MoldDetacherRecipe.recipeList));
		registration.addRecipes(MISCTWFJEIRecipeTypes.MOLD_WORKBENCH, getRecipes(MoldWorkbenchRecipe.recipeList));
		registration.addRecipes(MISCTWFJEIRecipeTypes.RECOVERY_FURNACE, getRecipes(RecoveryFurnaceRecipe.recipeList));
	}

	@SuppressWarnings("SameParameterValue")
	private static <T extends Recipe<?>> List<T> getRecipes(CachedRecipeList<T> cachedList) {
		return new ArrayList<>(cachedList.getRecipes(Objects.requireNonNull(Minecraft.getInstance().level)));
	}

	@Override
	public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
		registration.addRecipeTransferHandler(
				MoldWorkbenchMenu.class,
				MISCTWFJEIRecipeTypes.MOLD_WORKBENCH,
				MoldWorkbenchBlockEntity.SLOT_INPUT, 1,
				MoldWorkbenchMenu.INV_SLOT_START, 36
		);
		registration.addRecipeTransferHandler(
				RecoveryFurnaceMenu.class,
				MISCTWFJEIRecipeTypes.RECOVERY_FURNACE,
				RecoveryFurnaceBlockEntity.SLOT_INPUT, 1,
				RecoveryFurnaceMenu.INV_SLOT_START, 36
		);
		registration.addRecipeTransferHandler(
				RecoveryFurnaceMenu.class,
				RecipeTypes.FUELING,
				RecoveryFurnaceBlockEntity.SLOT_FUEL, 1,
				RecoveryFurnaceMenu.INV_SLOT_START, 36
		);
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
		registration.addRecipeCatalyst(new ItemStack(MISCTWFBlocks.MOLD_WORKBENCH.get()), MISCTWFJEIRecipeTypes.MOLD_WORKBENCH);
		registration.addRecipeCatalyst(new ItemStack(MISCTWFBlocks.MOLD_DETACHER.get()), MISCTWFJEIRecipeTypes.MOLD_DETACHER);
		registration.addRecipeCatalyst(new ItemStack(MISCTWFBlocks.RECOVERY_FURNACE.get()), MISCTWFJEIRecipeTypes.RECOVERY_FURNACE);
	}

	@Override
	public void registerGuiHandlers(IGuiHandlerRegistration registration) {
		registration.addRecipeClickArea(RecoveryFurnaceScreen.class, 83, 32, 28, 23, MISCTWFJEIRecipeTypes.RECOVERY_FURNACE, RecipeTypes.FUELING);
	}
}
