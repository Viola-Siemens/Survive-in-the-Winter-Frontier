package com.hexagram2021.misc_twf.client.screen;

import com.hexagram2021.misc_twf.common.menu.RecoveryFurnaceMenu;
import com.hexagram2021.misc_twf.common.menu.recipe_book.RecoveryFurnaceRecipeBookComponent;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;

/**
 * 回收炉的客户端 GUI 界面喵~
 * 提供回收炉的物品回收操作界面，集成了配方书组件以支持配方浏览和搜索喵~
 * 显示燃烧进度和回收进度的动画指示器喵~
 *
 * @author liudongyu
 */
public class RecoveryFurnaceScreen extends AbstractContainerScreen<RecoveryFurnaceMenu> implements RecipeUpdateListener {
	public final RecoveryFurnaceRecipeBookComponent recipeBookComponent = new RecoveryFurnaceRecipeBookComponent();
	private boolean widthTooNarrow;
	private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/container/recovery_furnace.png");

	public RecoveryFurnaceScreen(RecoveryFurnaceMenu menu, Inventory inventory, Component title) {
		super(menu, inventory, title);
	}

	@Override
	public void init() {
		super.init();
		this.widthTooNarrow = this.width < 379;
		assert this.minecraft != null;
		this.recipeBookComponent.init(this.width, this.height, this.minecraft, this.widthTooNarrow, this.menu);
		this.leftPos = this.recipeBookComponent.updateScreenPosition(this.width, this.imageWidth);
		this.addRenderableWidget(new ImageButton(this.leftPos + 5, this.height / 2 - 49, 20, 18, RecipeBookComponent.RECIPE_BUTTON_SPRITES, button -> {
			this.recipeBookComponent.toggleVisibility();
			this.leftPos = this.recipeBookComponent.updateScreenPosition(this.width, this.imageWidth);
			button.setPosition(this.leftPos + 5, this.height / 2 - 49);
		}));
		this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
	}

	@Override
	public void containerTick() {
		super.containerTick();
		this.recipeBookComponent.tick();
	}

	@Override
	public void render(GuiGraphics transform, int x, int y, float partialTicks) {
		if (this.recipeBookComponent.isVisible() && this.widthTooNarrow) {
			this.renderBackground(transform, x, y, partialTicks);
			this.recipeBookComponent.render(transform, x, y, partialTicks);
		} else {
			super.render(transform, x, y, partialTicks);
			this.recipeBookComponent.render(transform, x, y, partialTicks);
			this.recipeBookComponent.renderGhostRecipe(transform, this.leftPos, this.topPos, true, partialTicks);
		}

		this.renderTooltip(transform, x, y);
		this.recipeBookComponent.renderTooltip(transform, this.leftPos, this.topPos, x, y);
	}

	@Override
	protected void renderBg(GuiGraphics transform, float partialTicks, int x, int y) {
		transform.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
		if (this.menu.isLit()) {
			int progress = this.menu.getLitProgress();
			transform.blit(TEXTURE, this.leftPos + 62, this.topPos + 50 - progress, 176, 15 - progress, 15, progress + 1);
		}

		transform.blit(TEXTURE, this.leftPos + 84, this.topPos + 34, 176, 15, this.menu.getBurnProgress() + 1, 16);
	}

	@Override
	public boolean mouseClicked(double x, double y, int button) {
		if (this.recipeBookComponent.mouseClicked(x, y, button)) {
			return true;
		}
		return (this.widthTooNarrow && this.recipeBookComponent.isVisible()) || super.mouseClicked(x, y, button);
	}

	@Override
	protected void slotClicked(Slot slot, int slotNum, int buttonNum, ClickType clickType) {
		super.slotClicked(slot, slotNum, buttonNum, clickType);
		this.recipeBookComponent.slotClicked(slot);
	}

	@Override
	public boolean keyPressed(int key, int scanCode, int modifiers) {
		return !this.recipeBookComponent.keyPressed(key, scanCode, modifiers) && super.keyPressed(key, scanCode, modifiers);
	}

	@Override
	protected boolean hasClickedOutside(double x, double y, int left, int top, int mouseButton) {
		boolean mainOutside = x < left || y < top || x >= (left + this.imageWidth) || y >= (top + this.imageHeight);
		return this.recipeBookComponent.hasClickedOutside(x, y, this.leftPos, this.topPos, this.imageWidth, this.imageHeight, mouseButton) && mainOutside;
	}

	@Override
	public boolean charTyped(char code, int modifiers) {
		return this.recipeBookComponent.charTyped(code, modifiers) || super.charTyped(code, modifiers);
	}

	@Override
	public void recipesUpdated() {
		this.recipeBookComponent.recipesUpdated();
	}

	@Override
	public RecipeBookComponent getRecipeBookComponent() {
		return this.recipeBookComponent;
	}
}
