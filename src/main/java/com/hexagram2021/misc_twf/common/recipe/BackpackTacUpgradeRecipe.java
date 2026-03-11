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
		/**
		 * 从网络字节缓冲区中反序列化配方喵~
		 *
		 * @param buf 网络字节缓冲区喵~
		 * @return 反序列化得到的背包战术升级配方喵~
		 */
		public static BackpackTacUpgradeRecipe fromNetwork(RegistryFriendlyByteBuf buf) {
			Ingredient template = Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
			Ingredient base = Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
			Ingredient addition = Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
			ItemStack result = ItemStack.STREAM_CODEC.decode(buf);
			return new BackpackTacUpgradeRecipe(template, base, addition, result);
		}

		/**
		 * 将配方序列化写入网络字节缓冲区喵~
		 *
		 * @param buf 网络字节缓冲区喵~
		 * @param recipe 要序列化的配方喵~
		 */
		public static void toNetwork(RegistryFriendlyByteBuf buf, BackpackTacUpgradeRecipe recipe) {
			Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.template);
			Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.base);
			Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.addition);
			ItemStack.STREAM_CODEC.encode(buf, recipe.result);
		}

		private static final MapCodec<BackpackTacUpgradeRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
				Ingredient.CODEC.fieldOf("template").forGetter(recipe -> recipe.template),
				Ingredient.CODEC.fieldOf("base").forGetter(recipe -> recipe.base),
				Ingredient.CODEC.fieldOf("addition").forGetter(recipe -> recipe.addition),
				ItemStack.STRICT_CODEC.fieldOf("result").forGetter(recipe -> recipe.result)
		).apply(instance, BackpackTacUpgradeRecipe::new));
		public static final StreamCodec<RegistryFriendlyByteBuf, BackpackTacUpgradeRecipe> STREAM_CODEC = StreamCodec.of(
				BackpackTacUpgradeRecipe.Serializer::toNetwork,
				BackpackTacUpgradeRecipe.Serializer::fromNetwork
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
