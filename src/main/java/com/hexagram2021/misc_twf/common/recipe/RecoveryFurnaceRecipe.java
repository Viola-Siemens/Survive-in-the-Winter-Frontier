package com.hexagram2021.misc_twf.common.recipe;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.hexagram2021.misc_twf.common.register.MISCTWFBlocks;
import com.hexagram2021.misc_twf.common.register.MISCTWFRecipeSerializers;
import com.hexagram2021.misc_twf.common.register.MISCTWFRecipeTypes;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.List;

public record RecoveryFurnaceRecipe(ResourceLocation id, String group, Ingredient ingredient, List<ItemStack> results, float experience, int recoveringTime) implements Recipe<Container> {
	@Override
	public boolean matches(Container container, Level level) {
		return this.ingredient.test(container.getItem(0));
	}

	@Deprecated
	@Override
	public ItemStack getResultItem() {
		return this.results.get(0);
	}

	@Deprecated
	@Override
	public ItemStack assemble(Container container) {
		return this.getResultItem().copy();
	}

	public List<ItemStack> assembleAll(Container container) {
		return this.results.stream().map(ItemStack::copy).toList();
	}

	@Override
	public boolean canCraftInDimensions(int wid, int hgt) {
		return true;
	}

	@Override
	public NonNullList<Ingredient> getIngredients() {
		NonNullList<Ingredient> ret = NonNullList.create();
		ret.add(this.ingredient);
		return ret;
	}

	@Override
	public String getGroup() {
		return this.group;
	}

	@Override
	public ItemStack getToastSymbol() {
		return new ItemStack(MISCTWFBlocks.RECOVERY_FURNACE);
	}

	@Override
	public ResourceLocation getId() {
		return this.id;
	}

	@Override
	public RecipeSerializer<RecoveryFurnaceRecipe> getSerializer() {
		return MISCTWFRecipeSerializers.RECOVERY_FURNACE.get();
	}

	@Override
	public RecipeType<RecoveryFurnaceRecipe> getType() {
		return MISCTWFRecipeTypes.RECOVERY_FURNACE.get();
	}

	public static class Serializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<RecoveryFurnaceRecipe> {
		private static final int DEFAULT_RECOVERING_TIME = 200;

		@SuppressWarnings("deprecation")
		@Override
		public RecoveryFurnaceRecipe fromJson(ResourceLocation id, JsonObject json) {
			String group = GsonHelper.getAsString(json, "group", "");
			JsonElement ingredientJson = GsonHelper.isArrayNode(json, "ingredient") ? GsonHelper.getAsJsonArray(json, "ingredient") : GsonHelper.getAsJsonObject(json, "ingredient");
			Ingredient ingredient = Ingredient.fromJson(ingredientJson);
			if (!json.has("result")) {
				throw new JsonSyntaxException("Missing result, expected to find a string or object");
			}
			List<ItemStack> results = Lists.newArrayList();
			JsonElement resultJson = json.get("result");
			if(resultJson instanceof JsonArray array) {
				array.forEach(resultJsonElement -> {
					if(resultJsonElement.isJsonObject()) {
						results.add(ShapedRecipe.itemStackFromJson(resultJsonElement.getAsJsonObject()));
					} else {
						throw new JsonSyntaxException("Expected element of item list to be a JsonObject, was " + GsonHelper.getType(resultJsonElement));
					}
				});
			} else if (resultJson.isJsonObject()) {
				results.add(ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result")));
			} else {
				String resultItem = GsonHelper.getAsString(json, "result");
				ResourceLocation resourcelocation = new ResourceLocation(resultItem);
				results.add(new ItemStack(Registry.ITEM.getOptional(resourcelocation).orElseThrow(() -> new IllegalStateException("Item: " + resultItem + " does not exist"))));
			}

			float experience = GsonHelper.getAsFloat(json, "experience", 0.0F);
			int time = GsonHelper.getAsInt(json, "recoveringtime", DEFAULT_RECOVERING_TIME);
			return new RecoveryFurnaceRecipe(id, group, ingredient, results, experience, time);
		}

		@Override
		public RecoveryFurnaceRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
			String group = buf.readUtf();
			Ingredient ingredient = Ingredient.fromNetwork(buf);
			List<ItemStack> results = buf.readCollection(Lists::newArrayListWithCapacity, FriendlyByteBuf::readItem);
			float experience = buf.readFloat();
			int time = buf.readVarInt();
			return new RecoveryFurnaceRecipe(id, group, ingredient, results, experience, time);
		}

		@Override
		public void toNetwork(FriendlyByteBuf buf, RecoveryFurnaceRecipe recipe) {
			buf.writeUtf(recipe.group);
			recipe.ingredient.toNetwork(buf);
			buf.writeCollection(recipe.results, FriendlyByteBuf::writeItem);
			buf.writeFloat(recipe.experience);
			buf.writeVarInt(recipe.recoveringTime);
		}
	}
}
