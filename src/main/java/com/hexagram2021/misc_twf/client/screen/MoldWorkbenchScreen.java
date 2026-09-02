package com.hexagram2021.misc_twf.client.screen;

import com.hexagram2021.misc_twf.common.menu.MoldWorkbenchMenu;
import com.hexagram2021.misc_twf.common.recipe.MoldWorkbenchRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.List;
import java.util.Objects;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;

/**
 * 模具加工台的客户端 GUI 界面喵~
 * 提供配方选择、滚动浏览和配方预览等交互功能喵~
 * 支持鼠标点击选择配方、滚轮滚动和拖拽滚动条浏览配方列表喵~
 *
 * @author liudongyu
 */
public class MoldWorkbenchScreen extends AbstractContainerScreen<MoldWorkbenchMenu> {
	private static final ResourceLocation BG_LOCATION = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/container/mold_workbench.png");
	private static final int SCROLLER_WIDTH = 12;
	private static final int SCROLLER_HEIGHT = 15;
	private static final int RECIPES_COLUMNS = 4;
	private static final int RECIPES_ROWS = 3;
	private static final int RECIPES_IMAGE_SIZE_WIDTH = 16;
	private static final int RECIPES_IMAGE_SIZE_HEIGHT = 18;
	private static final int SCROLLER_FULL_HEIGHT = 54;
	private static final int RECIPES_X = 52;
	private static final int RECIPES_Y = 14;
	private float scrollOffs;
	private boolean scrolling;
	private int startIndex;
	private boolean displayRecipes;

	/**
	 * 构造函数
	 * @param menu 菜单
	 * @param inventory 玩家物品栏
	 * @param title 标题
	 */
	public MoldWorkbenchScreen(MoldWorkbenchMenu menu, Inventory inventory, Component title) {
		super(menu, inventory, title);
		menu.registerUpdateListener(this::containerChanged);
		--this.titleLabelY;
	}

	@Override
	public void render(GuiGraphics transform, int x, int y, float partialTicks) {
		super.render(transform, x, y, partialTicks);
		this.renderTooltip(transform, x, y);
	}

	@Override
	protected void renderBg(GuiGraphics transform, float partialTicks, int x, int y) {
		transform.blit(BG_LOCATION, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
		int skipY = (int)(41.0F * this.scrollOffs);
		transform.blit(BG_LOCATION, this.leftPos + 119, this.topPos + 15 + skipY, 176 + (this.isScrollBarActive() ? 0 : SCROLLER_WIDTH), 0, SCROLLER_WIDTH, SCROLLER_HEIGHT);
		int recipeX = this.leftPos + RECIPES_X;
		int recipeY = this.topPos + RECIPES_Y;
		int endIndex = this.startIndex + RECIPES_COLUMNS * RECIPES_ROWS;
		this.renderButtons(transform, x, y, recipeX, recipeY, endIndex);
		this.renderRecipes(transform, recipeX, recipeY, endIndex);
	}

	@Override
	protected void renderTooltip(GuiGraphics transform, int x, int y) {
		super.renderTooltip(transform, x, y);
		if (this.displayRecipes) {
			int recipeX = this.leftPos + RECIPES_X;
			int recipeY = this.topPos + RECIPES_Y;
			int endIndex = this.startIndex + RECIPES_COLUMNS * RECIPES_ROWS;
			List<RecipeHolder<MoldWorkbenchRecipe>> recipes = this.menu.getRecipes();

			for(int i = this.startIndex; i < endIndex && i < this.menu.getNumRecipes(); ++i) {
				int index = i - this.startIndex;
				int renderX = recipeX + index % RECIPES_COLUMNS * RECIPES_IMAGE_SIZE_WIDTH;
				int renderY = recipeY + index / RECIPES_COLUMNS * RECIPES_IMAGE_SIZE_HEIGHT + 2;
				if (x >= renderX && x < renderX + RECIPES_IMAGE_SIZE_WIDTH && y >= renderY && y < renderY + RECIPES_IMAGE_SIZE_HEIGHT) {
					transform.renderTooltip(this.font, recipes.get(i).value().getResultItem(
							Objects.requireNonNull(Objects.requireNonNull(this.minecraft).level).registryAccess()
					), x, y);
				}
			}
		}
		if (this.menu.mechanicalArmSlot == this.hoveredSlot && !this.hoveredSlot.hasItem()) {
			transform.renderTooltip(this.font, Component.translatable("container.misc_twf.mold_workbench.tooltip.mechanical_arm_slot"), x, y);
		}
	}

	private void renderButtons(GuiGraphics transform, int x, int y, int recipeX, int recipeY, int endIndex) {
		for(int i = this.startIndex; i < endIndex && i < this.menu.getNumRecipes(); ++i) {
			int index = i - this.startIndex;
			int renderX = recipeX + index % RECIPES_COLUMNS * RECIPES_IMAGE_SIZE_WIDTH;
			int renderY = recipeY + index / RECIPES_COLUMNS * RECIPES_IMAGE_SIZE_HEIGHT + 2;
			int buttonY = this.imageHeight;
			if (i == this.menu.getSelectedRecipeIndex()) {
				buttonY += RECIPES_IMAGE_SIZE_HEIGHT;
			} else if (x >= renderX && y >= renderY && x < renderX + RECIPES_IMAGE_SIZE_WIDTH && y < renderY + RECIPES_IMAGE_SIZE_HEIGHT) {
				buttonY += RECIPES_IMAGE_SIZE_HEIGHT * 2;
			}

			transform.blit(BG_LOCATION, renderX, renderY - 1, 0, buttonY, RECIPES_IMAGE_SIZE_WIDTH, RECIPES_IMAGE_SIZE_HEIGHT);
		}
	}

	private void renderRecipes(GuiGraphics transform, int recipeX, int recipeY, int endIndex) {
		List<RecipeHolder<MoldWorkbenchRecipe>> recipes = this.menu.getRecipes();

		for(int i = this.startIndex; i < endIndex && i < this.menu.getNumRecipes(); ++i) {
			int index = i - this.startIndex;
			int renderX = recipeX + index % RECIPES_COLUMNS * RECIPES_IMAGE_SIZE_WIDTH;
			int renderY = recipeY + index / RECIPES_COLUMNS * RECIPES_IMAGE_SIZE_HEIGHT + 2;
			transform.renderItem(recipes.get(i).value().getResultItem(
					Objects.requireNonNull(Objects.requireNonNull(this.minecraft).level).registryAccess()
			),renderX, renderY);
			if(i == this.menu.getSelectedRecipeIndex()) {
				int progress = this.menu.getWorkingProgress();
				transform.blit(BG_LOCATION, renderX - 1, renderY + 15, RECIPES_IMAGE_SIZE_WIDTH, this.imageHeight + 1, progress, 1);
				transform.blit(BG_LOCATION, renderX + progress - 1, renderY + 15, RECIPES_IMAGE_SIZE_WIDTH + progress, this.imageHeight, MoldWorkbenchMenu.PROGRESS_BAR_LENGTH - progress, 1);
			}
		}
	}

	@SuppressWarnings({"IntegerDivisionInFloatingPointContext", "java:S2184"})
	@Override
	public boolean mouseClicked(double x, double y, int mouseButton) {
		this.scrolling = false;
		if (this.displayRecipes) {
			int recipeX = this.leftPos + RECIPES_X;
			int recipeY = this.topPos + RECIPES_Y;
			int endIndex = this.startIndex + RECIPES_COLUMNS * RECIPES_ROWS;

			for(int i = this.startIndex; i < endIndex; ++i) {
				int index = i - this.startIndex;
				double diffX = x - (recipeX + index % 4 * 16);
				double diffY = y - (recipeY + index / 4 * 18);
				if (diffX >= 0.0 && diffY >= 0.0 && diffX < 16.0 && diffY < 18.0) {
					assert this.minecraft != null;
					assert this.minecraft.player != null;
					if (this.menu.clickMenuButton(this.minecraft.player, i)) {
						Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_STONECUTTER_SELECT_RECIPE, 1.0F));
						assert this.minecraft.gameMode != null;
						this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, i);
						return true;
					}
				}
			}

			recipeX = this.leftPos + 119;
			recipeY = this.topPos + 9;
			if (x >= recipeX && x < recipeX + 12 && y >= recipeY && y < recipeY + 54) {
				this.scrolling = true;
			}
		}

		return super.mouseClicked(x, y, mouseButton);
	}

	@Override
	public boolean mouseDragged(double fromX, double fromY, int activeButton, double toX, double toY) {
		if (this.scrolling && this.isScrollBarActive()) {
			int recipeY = this.topPos + RECIPES_Y;
			int maxRecipeY = recipeY + SCROLLER_FULL_HEIGHT;
			this.scrollOffs = Mth.clamp(((float)fromY - recipeY - 7.5F) / (maxRecipeY - recipeY - 15.0F), 0.0F, 1.0F);
			this.startIndex = (int)((this.scrollOffs * this.getOffscreenRows()) + 0.5D) * RECIPES_COLUMNS;
			return true;
		}
		return super.mouseDragged(fromX, fromY, activeButton, toX, toY);
	}

	@Override
	public boolean mouseScrolled(double x, double y, double deltaX, double deltaY) {
		if (this.isScrollBarActive()) {
			int totalY = this.getOffscreenRows();
			float scrollOffShift = (float)deltaY / totalY;
			this.scrollOffs = Mth.clamp(this.scrollOffs - scrollOffShift, 0.0F, 1.0F);
			this.startIndex = (int)((this.scrollOffs * totalY) + 0.5D) * RECIPES_COLUMNS;
		}

		return true;
	}

	private boolean isScrollBarActive() {
		return this.displayRecipes && this.menu.getNumRecipes() > RECIPES_COLUMNS * RECIPES_ROWS;
	}

	protected int getOffscreenRows() {
		return (this.menu.getNumRecipes() + RECIPES_COLUMNS - 1) / RECIPES_COLUMNS - RECIPES_ROWS;
	}

	private void containerChanged() {
		this.displayRecipes = this.menu.hasInputItem();
		if (!this.displayRecipes) {
			this.scrollOffs = 0.0F;
			this.startIndex = 0;
		}
	}
}
