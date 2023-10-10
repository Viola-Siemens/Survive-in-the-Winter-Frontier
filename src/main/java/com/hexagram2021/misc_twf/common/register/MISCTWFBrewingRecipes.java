package com.hexagram2021.misc_twf.common.register;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.brewing.BrewingRecipe;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;

public class MISCTWFBrewingRecipes {
	public static void init() {
		BrewingRecipeRegistry.addRecipe(new MISCTWFBrewingRecipe(
				PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.STRONG_HEALING),
				MISCTWFItems.Materials.MYSTERIOUS_FLESH,
				new ItemStack(MISCTWFItems.Materials.MUTANT_POTION)
		));
	}

	private static final class MISCTWFBrewingRecipe extends BrewingRecipe {
		public MISCTWFBrewingRecipe(ItemStack input, ItemLike add, ItemStack output) {
			super(Ingredient.of(input), Ingredient.of(add), output);
		}

		@Override
		public boolean isInput(ItemStack input) {
			ItemStack[] itemStacks = this.getInput().getItems();
			for(ItemStack itemstack : itemStacks) {
				if (itemstack.sameItem(input) && PotionUtils.getPotion(input).equals(PotionUtils.getPotion(itemstack))) {
					return true;
				}
			}
			return false;
		}
	}
}
