package com.hexagram2021.misc_twf.common.recipe;

import com.hexagram2021.misc_twf.common.recipe.cache.CachedRecipeList;
import com.hexagram2021.misc_twf.common.register.MISCTWFRecipeSerializers;
import com.hexagram2021.misc_twf.common.register.MISCTWFRecipeTypes;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * 模具拆卸机配方，定义了模具拆卸机的输入原料和多个输出结果喵~
 *
 * @param input 配方的输入原料喵~
 * @param results 配方的输出结果列表喵~
 *
 * @author liudongyu
 */
public record MoldDetacherRecipe(Ingredient input, List<ItemStack> results) implements Recipe<SingleRecipeInput> {
	/** 模具拆卸机配方的缓存列表，用于快速查询已加载的配方喵~ */
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
	 * 模具拆卸机配方的序列化器，基于 Codec 实现 JSON 和网络编解码喵~
	 */
	public static class Serializer implements RecipeSerializer<MoldDetacherRecipe> {
		/** 配方的 MapCodec 编解码器，用于 JSON 序列化与反序列化喵~ */
		private static final MapCodec<MoldDetacherRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
				Ingredient.CODEC.fieldOf("ingredient").forGetter(MoldDetacherRecipe::input),
				ItemStack.CODEC.listOf().fieldOf("results").forGetter(MoldDetacherRecipe::results)
		).apply(instance, MoldDetacherRecipe::new));

		/** 配方的 StreamCodec 网络编解码器，用于客户端与服务端之间的网络传输喵~ */
		private static final StreamCodec<RegistryFriendlyByteBuf, MoldDetacherRecipe> STREAM_CODEC = StreamCodec.composite(
				Ingredient.CONTENTS_STREAM_CODEC, MoldDetacherRecipe::input,
				ItemStack.LIST_STREAM_CODEC, MoldDetacherRecipe::results,
				MoldDetacherRecipe::new
		);

		@Override
		public MapCodec<MoldDetacherRecipe> codec() {
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, MoldDetacherRecipe> streamCodec() {
			return STREAM_CODEC;
		}
	}
}
