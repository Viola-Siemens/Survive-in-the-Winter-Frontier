package com.hexagram2021.misc_twf.common.recipe.compat;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.hexagram2021.misc_twf.common.recipe.RecoveryFurnaceRecipe;
import com.hexagram2021.misc_twf.common.register.MISCTWFBlocks;
import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;

public class RecoveryFurnaceRecipeCategory implements IRecipeCategory<RecoveryFurnaceRecipe> {
	public static final ResourceLocation UID = new ResourceLocation(MODID, "recovery_furnace");
	public static final ResourceLocation TEXTURE = new ResourceLocation(MODID, "textures/gui/jei/recovery_furnace.png");

	public static final int width = 118;
	public static final int height = 54;

	protected final IDrawableStatic staticFlame;
	protected final IDrawableAnimated animatedFlame;
	private final IDrawable background;
	private final int regularCookTime;
	private final IDrawable icon;
	private final LoadingCache<Integer, IDrawableAnimated> cachedArrows;

	public RecoveryFurnaceRecipeCategory(IGuiHelper guiHelper) {
		this.staticFlame = guiHelper.createDrawable(TEXTURE, 118, 0, 15, 15);
		this.animatedFlame = guiHelper.createAnimatedDrawable(this.staticFlame, 300, IDrawableAnimated.StartDirection.TOP, true);
		this.background = guiHelper.createDrawable(TEXTURE, 0, 0, width, height);
		this.regularCookTime = RecoveryFurnaceRecipe.Serializer.DEFAULT_RECOVERING_TIME;
		this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(MISCTWFBlocks.RECOVERY_FURNACE.get()));
		this.cachedArrows = CacheBuilder.newBuilder().maximumSize(25L).build(new CacheLoader<>() {
			@Override
			public IDrawableAnimated load(Integer cookTime) {
				return guiHelper.drawableBuilder(TEXTURE, 118, 15, 24, 17).buildAnimated(cookTime, IDrawableAnimated.StartDirection.LEFT, false);
			}
		});
	}

	protected IDrawableAnimated getArrow(RecoveryFurnaceRecipe recipe) {
		int cookTime = recipe.recoveringTime();
		if (cookTime <= 0) {
			cookTime = this.regularCookTime;
		}

		return this.cachedArrows.getUnchecked(cookTime);
	}

	@Override
	public void draw(RecoveryFurnaceRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack poseStack, double mouseX, double mouseY) {
		this.animatedFlame.draw(poseStack, 30, 19);
		IDrawableAnimated arrow = this.getArrow(recipe);
		arrow.draw(poseStack, 52, 18);
		this.drawExperience(recipe, poseStack);
		this.drawCookTime(recipe, poseStack);
	}

	protected void drawExperience(RecoveryFurnaceRecipe recipe, PoseStack poseStack) {
		float experience = recipe.experience();
		if (experience > 0.0F) {
			TranslatableComponent experienceString = new TranslatableComponent("gui.jei.category.smelting.experience", experience);
			Minecraft minecraft = Minecraft.getInstance();
			Font fontRenderer = minecraft.font;
			int stringWidth = fontRenderer.width(experienceString);
			fontRenderer.draw(poseStack, experienceString, (float)(this.background.getWidth() - stringWidth), 0.0F, -8355712);
		}
	}

	protected void drawCookTime(RecoveryFurnaceRecipe recipe, PoseStack poseStack) {
		int cookTime = recipe.recoveringTime();
		if (cookTime > 0) {
			int cookTimeSeconds = cookTime / 20;
			TranslatableComponent timeString = new TranslatableComponent("gui.jei.category.smelting.time.seconds", cookTimeSeconds);
			Minecraft minecraft = Minecraft.getInstance();
			Font fontRenderer = minecraft.font;
			int stringWidth = fontRenderer.width(timeString);
			fontRenderer.draw(poseStack, timeString, (float)(this.background.getWidth() - stringWidth), 45.0F, -8355712);
		}
	}

	@Override
	public RecipeType<RecoveryFurnaceRecipe> getRecipeType() {
		return JEIHelper.MISCTWFJEIRecipeTypes.RECOVERY_FURNACE;
	}

	@Override
	public Component getTitle() {
		return new TranslatableComponent("block.misc_twf.recovery_furnace");
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

	@SuppressWarnings("removal")
	@Override
	public ResourceLocation getUid() {
		return UID;
	}

	@SuppressWarnings("removal")
	@Override
	public Class<? extends RecoveryFurnaceRecipe> getRecipeClass() {
		return RecoveryFurnaceRecipe.class;
	}
}
