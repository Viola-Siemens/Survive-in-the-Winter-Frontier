package com.hexagram2021.misc_twf.common.recipe.compat;

import com.hexagram2021.misc_twf.common.recipe.MoldWorkbenchRecipe;
import com.hexagram2021.misc_twf.common.register.MISCTWFBlocks;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.config.Constants;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;

public class MoldWorkbenchRecipeCategory implements IRecipeCategory<MoldWorkbenchRecipe> {
	public static final ResourceLocation UID = new ResourceLocation(MODID, "mold_workbench");
	public static final ResourceLocation TEXTURE = Constants.RECIPE_GUI_VANILLA;

	public static final int width = 82;
	public static final int height = 34;

	private final IDrawable background;
	private final IDrawable icon;

	public MoldWorkbenchRecipeCategory(IGuiHelper guiHelper) {
		this.background = guiHelper.createDrawable(TEXTURE, 0, 220, width, height);
		this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(MISCTWFBlocks.MOLD_WORKBENCH.get()));
	}

	@Override
	public RecipeType<MoldWorkbenchRecipe> getRecipeType() {
		return JEIHelper.MISCTWFJEIRecipeTypes.MOLD_WORKBENCH;
	}

	@Override
	public Component getTitle() {
		return new TranslatableComponent("block.misc_twf.mold_workbench");
	}

	@Override
	public IDrawable getBackground() {
		return this.background;
	}

	@Override
	public IDrawable getIcon() {
		return this.icon;
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

	@SuppressWarnings("removal")
	@Override
	public ResourceLocation getUid() {
		return UID;
	}

	@SuppressWarnings("removal")
	@Override
	public Class<? extends MoldWorkbenchRecipe> getRecipeClass() {
		return MoldWorkbenchRecipe.class;
	}
}
