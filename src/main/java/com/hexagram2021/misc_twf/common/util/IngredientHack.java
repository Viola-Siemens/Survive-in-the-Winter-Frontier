package com.hexagram2021.misc_twf.common.util;

import com.hexagram2021.misc_twf.common.register.MISCTWFItems;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Stream;

public final class IngredientHack {
	public static Ingredient addWinterWheatToIngredient(Ingredient original) {
		return Ingredient.of(Stream.concat(Arrays.stream(original.getItems()).map(ItemStack::getItem), Stream.of(MISCTWFItems.Materials.WINTER_WHEAT.get())).toArray(ItemLike[]::new));
	}
	public static <T extends ItemLike> Ingredient addWinterWheatToIngredient(T[] inputs) {
		return Ingredient.of(Stream.concat(Arrays.stream(inputs), Stream.of(MISCTWFItems.Materials.WINTER_WHEAT.get())).toArray(ItemLike[]::new));
	}
	public static <T extends ItemLike> Ingredient addWinterWheatToIngredient(Collection<T> inputs) {
		return Ingredient.of(Stream.concat(inputs.stream(), Stream.of(MISCTWFItems.Materials.WINTER_WHEAT.get())).toArray(ItemLike[]::new));
	}

	public static Ingredient addWinterWheatSeedsToIngredient(Ingredient original) {
		return Ingredient.of(Stream.concat(Arrays.stream(original.getItems()).map(ItemStack::getItem), Stream.of(MISCTWFItems.Materials.WINTER_WHEAT_SEEDS.get())).toArray(ItemLike[]::new));
	}

	private IngredientHack() {
	}
}
