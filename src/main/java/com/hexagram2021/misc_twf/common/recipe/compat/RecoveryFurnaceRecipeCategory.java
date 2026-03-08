package com.hexagram2021.misc_twf.common.recipe.compat;

import com.hexagram2021.misc_twf.common.recipe.RecoveryFurnaceRecipe;
import com.hexagram2021.misc_twf.common.register.MISCTWFBlocks;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.placement.HorizontalAlignment;
import mezz.jei.api.gui.placement.VerticalAlignment;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;

public class RecoveryFurnaceRecipeCategory extends AbstractRecipeCategory<RecoveryFurnaceRecipe> {
	public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(MODID, "recovery_furnace");
	public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/jei/recovery_furnace.png");

	public static final int WIDTH = 118;

	private final int regularCookTime;

	public RecoveryFurnaceRecipeCategory(IGuiHelper guiHelper) {
		super(
				JEIHelper.MISCTWFJEIRecipeTypes.RECOVERY_FURNACE,
				Component.translatable("block.misc_twf.recovery_furnace"),
				guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(MISCTWFBlocks.RECOVERY_FURNACE.get())),
				WIDTH, 54
		);
		this.regularCookTime = RecoveryFurnaceRecipe.Serializer.DEFAULT_RECOVERING_TIME;
	}

	@Override
	public void createRecipeExtras(IRecipeExtrasBuilder builder, RecoveryFurnaceRecipe recipe, IFocusGroup focuses) {
		int cookTime = recipe.recoveringTime();
		if (cookTime <= 0) {
			cookTime = regularCookTime;
		}
		builder.addAnimatedRecipeArrow(cookTime)
				.setPosition(26, 17);
		builder.addAnimatedRecipeFlame(300)
				.setPosition(1, 20);

		this.addExperience(builder, recipe);
		this.addCookTime(builder, recipe);
	}

	protected void addExperience(IRecipeExtrasBuilder builder, RecoveryFurnaceRecipe recipe) {
		float experience = recipe.experience();
		if (experience > 0) {
			Component experienceString = Component.translatable("gui.jei.category.smelting.experience", experience);
			builder.addText(experienceString, getWidth() - 20, 10)
					.setPosition(0, 0, getWidth(), getHeight(), HorizontalAlignment.RIGHT, VerticalAlignment.TOP)
					.setTextAlignment(HorizontalAlignment.RIGHT)
					.setColor(0xFF808080);
		}
	}

	protected void addCookTime(IRecipeExtrasBuilder builder, RecoveryFurnaceRecipe recipe) {
		int cookTime = recipe.recoveringTime();
		if (cookTime <= 0) {
			cookTime = regularCookTime;
		}
		if (cookTime > 0) {
			int cookTimeSeconds = cookTime / 20;
			Component timeString = Component.translatable("gui.jei.category.smelting.time.seconds", cookTimeSeconds);
			builder.addText(timeString, getWidth() - 20, 10)
					.setPosition(0, 0, getWidth(), getHeight(), HorizontalAlignment.RIGHT, VerticalAlignment.BOTTOM)
					.setTextAlignment(HorizontalAlignment.RIGHT)
					.setTextAlignment(VerticalAlignment.BOTTOM)
					.setColor(0xFF808080);
		}
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, RecoveryFurnaceRecipe recipe, IFocusGroup focuses) {
		builder.addSlot(RecipeIngredientRole.INPUT, 1, 1).addItemStack(recipe.ingredient().getResult());
		List<ItemStack> outputs = recipe.results();
		for(int i = 0; i < outputs.size(); ++i) {
			builder.addSlot(RecipeIngredientRole.OUTPUT, 83 + 18 * (i % 2), 10 + 18 * (i / 2)).addItemStack(outputs.get(i));
		}
	}

	@Override
	public boolean isHandled(RecoveryFurnaceRecipe recipe) {
		return !recipe.isSpecial();
	}
}
