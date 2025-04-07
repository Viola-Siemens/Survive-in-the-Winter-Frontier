package com.hexagram2021.misc_twf.common.recipe;

import com.google.gson.JsonObject;
import com.hexagram2021.misc_twf.common.recipe.cache.CachedRecipeList;
import com.hexagram2021.misc_twf.common.register.MISCTWFRecipeSerializers;
import com.hexagram2021.misc_twf.common.register.MISCTWFRecipeTypes;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistryEntry;

public record MoldWorkbenchRecipe(ResourceLocation id, Ingredient input, ItemStack result, int workingTime) implements Recipe<Container> {
	public static final int DEFAULT_WORKING_TIME = 40;

	public static final CachedRecipeList<MoldWorkbenchRecipe> recipeList = new CachedRecipeList<>(
			MISCTWFRecipeTypes.MOLD_WORKBENCH,
			MoldWorkbenchRecipe.class
	);

	@Override
	public ResourceLocation getId() {
		return this.id;
	}

	@Override
	public NonNullList<Ingredient> getIngredients() {
		return NonNullList.of(Ingredient.EMPTY, this.input);
	}

	@Override
	public boolean matches(Container container, Level level) {
		return this.input.test(container.getItem(0));
	}

	@Override
	public ItemStack getResultItem() {
		return this.result;
	}

	@Override
	public ItemStack assemble(Container container) {
		return this.getResultItem().copy();
	}

	@Override
	public boolean canCraftInDimensions(int h, int w) {
		return true;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return MISCTWFRecipeSerializers.MOLD_WORKBENCH.get();
	}

	@Override
	public RecipeType<?> getType() {
		return MISCTWFRecipeTypes.MOLD_WORKBENCH.get();
	}

	public static class Serializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<MoldWorkbenchRecipe> {
		@Override
		public MoldWorkbenchRecipe fromJson(ResourceLocation id, JsonObject jsonObject) {
			Ingredient input;
			if (GsonHelper.isArrayNode(jsonObject, "ingredient")) {
				input = Ingredient.fromJson(GsonHelper.getAsJsonArray(jsonObject, "ingredient"));
			} else {
				input = Ingredient.fromJson(GsonHelper.getAsJsonObject(jsonObject, "ingredient"));
			}
			ItemStack result = ShapedRecipe.itemStackFromJson(GsonHelper.convertToJsonObject(
					GsonHelper.getAsJsonObject(jsonObject, "result"), "element of item list"
			));
			int workingTime = GsonHelper.getAsInt(jsonObject, "working_time", DEFAULT_WORKING_TIME);
			return new MoldWorkbenchRecipe(id, input, result, workingTime);
		}

		@Override
		public MoldWorkbenchRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
			Ingredient input = Ingredient.fromNetwork(buf);
			ItemStack result = buf.readItem();
			int workingTime = buf.readVarInt();
			return new MoldWorkbenchRecipe(id, input, result, workingTime);
		}

		@Override
		public void toNetwork(FriendlyByteBuf buf, MoldWorkbenchRecipe recipe) {
			recipe.input.toNetwork(buf);
			buf.writeItem(recipe.result);
			buf.writeVarInt(recipe.workingTime);
		}
	}
}
