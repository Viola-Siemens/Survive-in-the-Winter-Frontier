package com.hexagram2021.misc_twf.common.recipe.compat;

import com.hexagram2021.misc_twf.common.recipe.MoldDetacherRecipe;
import com.hexagram2021.misc_twf.common.register.MISCTWFBlocks;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;

public class MoldDetacherRecipeCategory extends AbstractRecipeCategory<MoldDetacherRecipe> {
	public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(MODID, "mold_detach");
	public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/jei/mold_detacher.png");

	public static final int WIDTH = 128;
	public static final int HEIGHT = 48;

	private final IDrawable background;

	public MoldDetacherRecipeCategory(IGuiHelper guiHelper) {
		super(
				JEIHelper.MISCTWFJEIRecipeTypes.MOLD_DETACHER,
				Component.translatable("block.misc_twf.mold_detacher"),
				guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(MISCTWFBlocks.MOLD_DETACHER.get())),
				WIDTH, HEIGHT
		);
		this.background = guiHelper.createDrawable(TEXTURE, 0, 0, WIDTH, HEIGHT);
	}

	@Override
	public void draw(MoldDetacherRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
		super.draw(recipe, recipeSlotsView, guiGraphics, mouseX, mouseY);
		this.background.draw(guiGraphics);
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
}
