package com.hexagram2021.misc_twf.common.menu.recipe_book;

import com.hexagram2021.misc_twf.common.block.entity.RecoveryFurnaceBlockEntity;
import com.hexagram2021.misc_twf.common.recipe.RecoveryFurnaceRecipe;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

@OnlyIn(Dist.CLIENT)
public class RecoveryFurnaceRecipeBookComponent extends RecipeBookComponent {
	private static final Component FILTER_NAME = new TranslatableComponent("gui.recipebook.toggleRecipes.misc_twf.recoverable");

	@Nullable
	private Ingredient fuels;

	@Override
	protected void initFilterButtonTextures() {
		this.filterButton.initTextureValues(152, 182, 28, 18, RECIPE_BOOK_LOCATION);
	}

	@Override
	public void slotClicked(@Nullable Slot slot) {
		super.slotClicked(slot);
		if (slot != null && slot.index < this.menu.getSize()) {
			this.ghostRecipe.clear();
		}
	}

	@Override
	public void setupGhostRecipe(Recipe<?> recipe, List<Slot> slots) {
		if(recipe instanceof RecoveryFurnaceRecipe recoveryFurnaceRecipe) {
			List<ItemStack> results = recoveryFurnaceRecipe.results();
			this.ghostRecipe.setRecipe(recoveryFurnaceRecipe);
			for(int i = 0; i < results.size(); ++i) {
				Slot slot = slots.get(i + RecoveryFurnaceBlockEntity.SLOT_RESULT_START);
				this.ghostRecipe.addIngredient(Ingredient.of(results.get(i)), slot.x, slot.y);
			}
			Ingredient ingredient = recoveryFurnaceRecipe.ingredient();
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
