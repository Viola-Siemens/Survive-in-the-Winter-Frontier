package com.hexagram2021.misc_twf.common.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.hexagram2021.misc_twf.common.block.entity.MoldDetacherBlockEntity;
import com.hexagram2021.misc_twf.common.recipe.cache.CachedRecipeList;
import com.hexagram2021.misc_twf.common.register.MISCTWFRecipeSerializers;
import com.hexagram2021.misc_twf.common.register.MISCTWFRecipeTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

/**
 * 模具拆卸机配方，定义了模具拆卸机的输入原料和多个输出结果喵~
 *
 * @param id 配方的资源路径标识符喵~
 * @param input 配方的输入原料喵~
 * @param results 配方的输出结果列表喵~
 *
 * @author liudongyu
 */
public record MoldDetacherRecipe(ResourceLocation id, Ingredient input, NonNullList<ItemStack> results) implements Recipe<SingleRecipeInput> {
	public static final CachedRecipeList<MoldDetacherRecipe> recipeList = new CachedRecipeList<>(
			MISCTWFRecipeTypes.MOLD_DETACHER,
			MoldDetacherRecipe.class
	);

	@Override
	public NonNullList<Ingredient> getIngredients() {
		return NonNullList.of(Ingredient.EMPTY, this.input);
	}

	@Override
	public boolean matches(SingleRecipeInput container, Level level) {
		return this.input.test(container.getItem(0));
	}

	@Override
	public ItemStack getResultItem(HolderLookup.Provider provider) {
		return this.results.getFirst();
	}

	@Override
	public ItemStack assemble(SingleRecipeInput container, HolderLookup.Provider provider) {
		return this.getResultItem(provider).copy();
	}

	@Override
	public boolean canCraftInDimensions(int h, int w) {
		return true;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return MISCTWFRecipeSerializers.MOLD_DETACHER.get();
	}

	@Override
	public RecipeType<?> getType() {
		return MISCTWFRecipeTypes.MOLD_DETACHER.get();
	}

	/**
	 * 模具拆卸机配方的序列化器，负责 JSON 解析和网络编解码喵~
	 */
	public static class Serializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<MoldDetacherRecipe> {
		@Override
		public MoldDetacherRecipe fromJson(ResourceLocation id, JsonObject jsonObject) {
			Ingredient input;
			if (GsonHelper.isArrayNode(jsonObject, "ingredient")) {
				input = Ingredient.fromJson(GsonHelper.getAsJsonArray(jsonObject, "ingredient"));
			} else {
				input = Ingredient.fromJson(GsonHelper.getAsJsonObject(jsonObject, "ingredient"));
			}
			NonNullList<ItemStack> results = itemsFromJson(GsonHelper.getAsJsonArray(jsonObject, "results"));
			if(results.isEmpty()) {
				throw new JsonParseException("No results for mold detacher recipe.");
			}
			if(results.size() > MoldDetacherBlockEntity.MAX_RESULT_COUNT) {
				throw new JsonParseException("Too many results for mold detacher recipe. The maximum is %d.".formatted(MoldDetacherBlockEntity.MAX_RESULT_COUNT));
			}
			return new MoldDetacherRecipe(id, input, results);
		}

		private static NonNullList<ItemStack> itemsFromJson(JsonArray jsonArray) {
			NonNullList<ItemStack> nonNullList = NonNullList.create();

			for(int i = 0; i < jsonArray.size(); ++i) {
				ItemStack itemStack = ShapedRecipe.itemStackFromJson(GsonHelper.convertToJsonObject(jsonArray.get(i), "element of item list"));
				if(!itemStack.isEmpty()) {
					nonNullList.add(itemStack);
				}
			}

			return nonNullList;
		}

		@Override
		public MoldDetacherRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
			Ingredient input = Ingredient.fromNetwork(buf);
			int i = buf.readVarInt();
			NonNullList<ItemStack> results = NonNullList.withSize(i, ItemStack.EMPTY);

			results.replaceAll(ignored -> buf.readItem());
			return new MoldDetacherRecipe(id, input, results);
		}

		@Override
		public void toNetwork(FriendlyByteBuf buf, MoldDetacherRecipe recipe) {
			recipe.input.toNetwork(buf);
			buf.writeVarInt(recipe.results.size());
			recipe.results.forEach(buf::writeItem);
		}
	}
}
