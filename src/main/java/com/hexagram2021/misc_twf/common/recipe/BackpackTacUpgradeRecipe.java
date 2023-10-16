package com.hexagram2021.misc_twf.common.recipe;

import com.google.gson.JsonObject;
import com.hexagram2021.misc_twf.common.register.MISCTWFRecipeSerializers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.UpgradeRecipe;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class BackpackTacUpgradeRecipe extends UpgradeRecipe {
	public BackpackTacUpgradeRecipe(ResourceLocation id, Ingredient base, Ingredient addition, ItemStack result) {
		super(id, base, addition, result);
	}

	@Override
	public ItemStack assemble(Container container) {
		ItemStack itemstack = super.assemble(container);
		CompoundTag compoundtag = itemstack.getOrCreateTag();
		compoundtag.putBoolean("UpgradeToTac", true);
		itemstack.setTag(compoundtag.copy());

		return itemstack;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return MISCTWFRecipeSerializers.BACKPACK_TAC_UPGRADE.get();
	}

	public static class Serializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<BackpackTacUpgradeRecipe> {
		@Override
		public BackpackTacUpgradeRecipe fromJson(ResourceLocation id, JsonObject json) {
			Ingredient base = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "base"));
			Ingredient addition = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "addition"));
			ItemStack result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));
			return new BackpackTacUpgradeRecipe(id, base, addition, result);
		}

		@Override
		public BackpackTacUpgradeRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
			Ingredient base = Ingredient.fromNetwork(buf);
			Ingredient addition = Ingredient.fromNetwork(buf);
			ItemStack result = buf.readItem();
			return new BackpackTacUpgradeRecipe(id, base, addition, result);
		}

		@Override
		public void toNetwork(FriendlyByteBuf buf, BackpackTacUpgradeRecipe recipe) {
			recipe.base.toNetwork(buf);
			recipe.addition.toNetwork(buf);
			buf.writeItem(recipe.result);
		}
	}
}
