package com.hexagram2021.misc_twf.common.menu.recipe_book;

import com.hexagram2021.misc_twf.common.block.entity.RecoveryFurnaceBlockEntity;
import com.hexagram2021.misc_twf.common.recipe.RecoveryFurnaceRecipe;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

/**
 * 回收炉的配方书组件喵~
 * 自定义了配方书的过滤按钮纹理、幽灵配方显示和燃料物品集合喵~
 * 支持在回收炉界面中浏览和搜索可用的回收配方喵~
 *
 * @author liudongyu
 */
@OnlyIn(Dist.CLIENT)
public class RecoveryFurnaceRecipeBookComponent extends RecipeBookComponent {
	private static final Component FILTER_NAME = Component.translatable("gui.recipebook.toggleRecipes.misc_twf.recoverable");

	private static final WidgetSprites FILTER_SPRITES = new WidgetSprites(
			ResourceLocation.withDefaultNamespace("recipe_book/furnace_filter_enabled"),
			ResourceLocation.withDefaultNamespace("recipe_book/furnace_filter_disabled"),
			ResourceLocation.withDefaultNamespace("recipe_book/furnace_filter_enabled_highlighted"),
			ResourceLocation.withDefaultNamespace("recipe_book/furnace_filter_disabled_highlighted")
	);

	@Nullable
	private Ingredient fuels;

	@Override
	protected void initFilterButtonTextures() {
		this.filterButton.initTextureValues(FILTER_SPRITES);
	}

	@Override
	public void slotClicked(@Nullable Slot slot) {
		super.slotClicked(slot);
		if (slot != null && slot.index < this.menu.getSize()) {
			this.ghostRecipe.clear();
		}
	}

	@Override
	public void setupGhostRecipe(RecipeHolder<?> recipe, List<Slot> slots) {
		if(recipe.value() instanceof RecoveryFurnaceRecipe recoveryFurnaceRecipe) {
			List<ItemStack> results = recoveryFurnaceRecipe.results();
			this.ghostRecipe.setRecipe(recipe);
			for(int i = 0; i < results.size(); ++i) {
				Slot slot = slots.get(i + RecoveryFurnaceBlockEntity.SLOT_RESULT_START);
				this.ghostRecipe.addIngredient(Ingredient.of(results.get(i)), slot.x, slot.y);
			}
			Ingredient ingredient = Ingredient.of(recoveryFurnaceRecipe.ingredient().getResult());
			Slot fuelSlot = slots.get(RecoveryFurnaceBlockEntity.SLOT_FUEL);
			if (fuelSlot.getItem().isEmpty()) {
				if (this.fuels == null) {
					this.fuels = Ingredient.of(this.getFuelItems().stream().map(ItemStack::new));
				}

				this.ghostRecipe.addIngredient(this.fuels, fuelSlot.x, fuelSlot.y);
			}

			if (!ingredient.isEmpty()) {
				Slot inputSlot = slots.get(RecoveryFurnaceBlockEntity.SLOT_INPUT);
				this.ghostRecipe.addIngredient(ingredient, inputSlot.x, inputSlot.y);
			}
		}
	}

	@Override
	protected Component getRecipeFilterName() {
		return FILTER_NAME;
	}

	@SuppressWarnings("deprecation")
	protected Set<Item> getFuelItems() {
		return AbstractFurnaceBlockEntity.getFuel().keySet();
	}
}
