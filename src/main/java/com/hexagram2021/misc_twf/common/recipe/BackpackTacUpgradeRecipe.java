package com.hexagram2021.misc_twf.common.recipe;

import com.hexagram2021.misc_twf.common.data_component.TravelersBackpackTacData;
import com.hexagram2021.misc_twf.common.register.MISCTWFDataComponentTypes;
import com.hexagram2021.misc_twf.common.register.MISCTWFRecipeSerializers;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

/**
 * 旅行者背包战术升级配方，通过锻造台将背包升级为战术版本喵~
 * <p>
 * 合成时会在结果物品上设置战术背包数据组件喵~
 *
 * @author liudongyu
 */
public class BackpackTacUpgradeRecipe extends SmithingTransformRecipe {
	/**
	 * 构造一个背包战术升级配方喵~
	 *
	 * @param template 模板原料喵~
	 * @param base 基础原料（背包）喵~
	 * @param addition 附加原料喵~
	 * @param result 合成结果物品喵~
	 */
	public BackpackTacUpgradeRecipe(Ingredient template, Ingredient base, Ingredient addition, ItemStack result) {
		super(template, base, addition, result);
	}

	/**
	 * 组装配方结果，在锻造产物上设置 TAC 弹药槽数据组件喵~
	 *
	 * @param container 锻造台输入容器喵~
	 * @param provider 注册表查询提供者喵~
	 * @return 附带 TAC 数据的结果物品喵~
	 */
	@Override
	public ItemStack assemble(SmithingRecipeInput container, HolderLookup.Provider provider) {
		ItemStack itemStack = super.assemble(container, provider);
		TravelersBackpackTacData data = itemStack.getOrDefault(MISCTWFDataComponentTypes.TRAVELERS_BACKPACK_TAC_DATA, TravelersBackpackTacData.EMPTY);
		data = new TravelersBackpackTacData(true, data.ammoInventory());
		itemStack.set(MISCTWFDataComponentTypes.TRAVELERS_BACKPACK_TAC_DATA, data);

		return itemStack;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return MISCTWFRecipeSerializers.BACKPACK_TAC_UPGRADE.get();
	}

	/**
	 * 背包战术升级配方的序列化器，负责配方的编解码喵~
	 */
	public static class Serializer implements RecipeSerializer<BackpackTacUpgradeRecipe> {
		/** 配方的 MapCodec 编解码器，用于 JSON 序列化与反序列化喵~ */
		private static final MapCodec<BackpackTacUpgradeRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
				Ingredient.CODEC.fieldOf("template").forGetter(recipe -> recipe.template),
				Ingredient.CODEC.fieldOf("base").forGetter(recipe -> recipe.base),
				Ingredient.CODEC.fieldOf("addition").forGetter(recipe -> recipe.addition),
				ItemStack.STRICT_CODEC.fieldOf("result").forGetter(recipe -> recipe.result)
		).apply(instance, BackpackTacUpgradeRecipe::new));
		/** 配方的 StreamCodec 网络编解码器，用于客户端与服务端之间的网络传输喵~ */
		public static final StreamCodec<RegistryFriendlyByteBuf, BackpackTacUpgradeRecipe> STREAM_CODEC = StreamCodec.composite(
				Ingredient.CONTENTS_STREAM_CODEC, recipe -> recipe.template,
				Ingredient.CONTENTS_STREAM_CODEC, recipe -> recipe.base,
				Ingredient.CONTENTS_STREAM_CODEC, recipe -> recipe.addition,
				ItemStack.STREAM_CODEC, recipe -> recipe.result,
				BackpackTacUpgradeRecipe::new
		);

		@Override
		public MapCodec<BackpackTacUpgradeRecipe> codec() {
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, BackpackTacUpgradeRecipe> streamCodec() {
			return STREAM_CODEC;
		}
	}
}
