package com.hexagram2021.misc_twf.common.recipe;

import com.hexagram2021.misc_twf.common.recipe.cache.CachedRecipeList;
import com.hexagram2021.misc_twf.common.register.MISCTWFRecipeSerializers;
import com.hexagram2021.misc_twf.common.register.MISCTWFRecipeTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

/**
 * 模具工作台配方，定义了模具工作台将原料加工为模具产品的逻辑喵~
 * <p>
 * 每个配方包含一个输入原料、一个输出结果和可配置的加工时间喵~
 *
 * @param input 配方的输入原料喵~
 * @param result 配方的输出结果喵~
 * @param workingTime 加工所需时间（游戏刻）喵~
 *
 * @author liudongyu
 */
public record MoldWorkbenchRecipe(Ingredient input, ItemStack result, int workingTime) implements Recipe<SingleRecipeInput> {
	/** 默认加工时间（游戏刻），当配方 JSON 未指定 working_time 时使用喵~ */
	public static final int DEFAULT_WORKING_TIME = 40;

	/** 模具工作台配方的缓存列表，用于快速查询已加载的配方喵~ */
	public static final CachedRecipeList<MoldWorkbenchRecipe> recipeList = new CachedRecipeList<>(
			MISCTWFRecipeTypes.MOLD_WORKBENCH,
			MoldWorkbenchRecipe.class
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
		return this.result;
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
		return MISCTWFRecipeSerializers.MOLD_WORKBENCH.get();
	}

	@Override
	public RecipeType<?> getType() {
		return MISCTWFRecipeTypes.MOLD_WORKBENCH.get();
	}

	/**
	 * 模具工作台配方的序列化器，基于 Codec 实现 JSON 和网络编解码喵~
	 */
	public static class Serializer implements RecipeSerializer<MoldWorkbenchRecipe> {
		/** 配方的 MapCodec 编解码器，用于 JSON 序列化与反序列化喵~ */
		private static final MapCodec<MoldWorkbenchRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
				Ingredient.CODEC.fieldOf("ingredient").forGetter(MoldWorkbenchRecipe::input),
				ItemStack.STRICT_CODEC.fieldOf("result").forGetter(MoldWorkbenchRecipe::result),
				Codec.INT.optionalFieldOf("working_time", DEFAULT_WORKING_TIME).forGetter(MoldWorkbenchRecipe::workingTime)
		).apply(instance, MoldWorkbenchRecipe::new));

		/** 配方的 StreamCodec 网络编解码器，用于客户端与服务端之间的网络传输喵~ */
		private static final StreamCodec<RegistryFriendlyByteBuf, MoldWorkbenchRecipe> STREAM_CODEC = StreamCodec.composite(
				Ingredient.CONTENTS_STREAM_CODEC, MoldWorkbenchRecipe::input,
				ItemStack.STREAM_CODEC, MoldWorkbenchRecipe::result,
				ByteBufCodecs.INT, MoldWorkbenchRecipe::workingTime,
				MoldWorkbenchRecipe::new
		);

		@Override
		public MapCodec<MoldWorkbenchRecipe> codec() {
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, MoldWorkbenchRecipe> streamCodec() {
			return STREAM_CODEC;
		}
	}
}
