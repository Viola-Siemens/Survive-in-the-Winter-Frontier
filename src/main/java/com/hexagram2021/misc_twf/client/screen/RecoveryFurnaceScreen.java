package com.hexagram2021.misc_twf.client.screen;

import com.hexagram2021.misc_twf.common.menu.RecoveryFurnaceMenu;
import com.hexagram2021.misc_twf.common.menu.recipe_book.RecoveryFurnaceRecipeBookComponent;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;

public class RecoveryFurnaceScreen extends AbstractContainerScreen<RecoveryFurnaceMenu> implements RecipeUpdateListener {
	private static final ResourceLocation RECIPE_BUTTON_LOCATION = new ResourceLocation("textures/gui/recipe_button.png");
	public final RecoveryFurnaceRecipeBookComponent recipeBookComponent = new RecoveryFurnaceRecipeBookComponent();
	private boolean widthTooNarrow;
	private static final ResourceLocation TEXTURE = new ResourceLocation(MODID, "textures/gui/container/recovery_furnace.png");

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
		this.addRenderableWidget(new ImageButton(this.leftPos + 5, this.height / 2 - 49, 20, 18, 0, 0, 19, RECIPE_BUTTON_LOCATION, button -> {
			this.recipeBookComponent.toggleVisibility();
			this.leftPos = this.recipeBookComponent.updateScreenPosition(this.width, this.imageWidth);
			((ImageButton)button).setPosition(this.leftPos + 5, this.height / 2 - 49);
		}));
		this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
	}

	@Override
	public void containerTick() {
		super.containerTick();
		this.recipeBookComponent.tick();
	}

	@Override
	public void render(PoseStack transform, int x, int y, float partialTicks) {
		this.renderBackground(transform);
		if (this.recipeBookComponent.isVisible() && this.widthTooNarrow) {
			this.renderBg(transform, partialTicks, x, y);
			this.recipeBookComponent.render(transform, x, y, partialTicks);
		} else {
			this.recipeBookComponent.render(transform, x, y, partialTicks);
			super.render(transform, x, y, partialTicks);
			this.recipeBookComponent.renderGhostRecipe(transform, this.leftPos, this.topPos, true, partialTicks);
		}

		this.renderTooltip(transform, x, y);
		this.recipeBookComponent.renderTooltip(transform, this.leftPos, this.topPos, x, y);
	}

	@Override
	protected void renderBg(PoseStack transform, float partialTicks, int x, int y) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, TEXTURE);
		this.blit(transform, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
		if (this.menu.isLit()) {
			int progress = this.menu.getLitProgress();
			this.blit(transform, this.leftPos + 33, this.topPos + 48 - progress, 176, 12 - progress, 14, progress + 1);
		}

		this.blit(transform, this.leftPos + 56, this.topPos + 34, 176, 14, this.menu.getBurnProgress() + 1, 16);
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
		boolean mainOutside = x < (double)left || y < (double)top || x >= (double)(left + this.imageWidth) || y >= (double)(top + this.imageHeight);
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

	@Override
	public void removed() {
		this.recipeBookComponent.removed();
		super.removed();
	}
}
