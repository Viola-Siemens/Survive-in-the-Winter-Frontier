package com.hexagram2021.misc_twf.common.recipe.compat;

import com.hexagram2021.misc_twf.common.recipe.MoldWorkbenchRecipe;
import com.hexagram2021.misc_twf.common.register.MISCTWFBlocks;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;

public class MoldWorkbenchRecipeCategory extends AbstractRecipeCategory<MoldWorkbenchRecipe> {
	public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(MODID, "mold_workbench");

	public MoldWorkbenchRecipeCategory(IGuiHelper guiHelper) {
		super(
				JEIHelper.MISCTWFJEIRecipeTypes.MOLD_WORKBENCH,
				Component.translatable("block.misc_twf.mold_workbench"),
				guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(MISCTWFBlocks.MOLD_WORKBENCH.get())),
				82, 34
		);
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, MoldWorkbenchRecipe recipe, IFocusGroup focuses) {
		builder.addSlot(RecipeIngredientRole.INPUT, 1, 9).addIngredients(recipe.input());
		builder.addSlot(RecipeIngredientRole.OUTPUT, 61, 9).addItemStack(recipe.getResultItem());
	}

	@Override
	public boolean isHandled(MoldWorkbenchRecipe recipe) {
		return !recipe.isSpecial();
	}
}
