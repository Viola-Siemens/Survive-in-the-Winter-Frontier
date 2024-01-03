package com.hexagram2021.misc_twf.common.recipe.compat;

import com.hexagram2021.misc_twf.common.recipe.MoldDetacherRecipe;
import com.hexagram2021.misc_twf.common.register.MISCTWFBlocks;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;

public class MoldDetacherRecipeCategory implements IRecipeCategory<MoldDetacherRecipe> {
	public static final ResourceLocation UID = new ResourceLocation(MODID, "mold_detach");
	public static final ResourceLocation TEXTURE = new ResourceLocation(MODID, "textures/gui/jei/mold_detacher.png");

	public static final int width = 128;
	public static final int height = 48;

	private final IDrawable background;
	private final IDrawable icon;

	public MoldDetacherRecipeCategory(IGuiHelper guiHelper) {
		this.background = guiHelper.createDrawable(TEXTURE, 0, 0, width, height);
		this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(MISCTWFBlocks.MOLD_DETACHER));
	}

	@Override
	public RecipeType<MoldDetacherRecipe> getRecipeType() {
		return JEIHelper.MISCTWFJEIRecipeTypes.MOLD_DETACHER;
	}

	@Override
	public Component getTitle() {
		return new TranslatableComponent("block.misc_twf.mold_detacher");
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
	public void setRecipe(IRecipeLayoutBuilder builder, MoldDetacherRecipe recipe, IFocusGroup focuses) {
		builder.addSlot(RecipeIngredientRole.INPUT, 12, 16).addIngredients(recipe.input());
		for(int i = 0; i < recipe.results().size(); ++i) {
			builder.addSlot(RecipeIngredientRole.OUTPUT, 67 + 18 * i, 16).addItemStack(recipe.results().get(i));
		}
	}

	@Override
	public boolean isHandled(MoldDetacherRecipe recipe) {
		return !recipe.isSpecial();
	}

	@SuppressWarnings("removal")
	@Override
	public ResourceLocation getUid() {
		return UID;
	}

	@SuppressWarnings("removal")
	@Override
	public Class<? extends MoldDetacherRecipe> getRecipeClass() {
		return MoldDetacherRecipe.class;
	}
}
