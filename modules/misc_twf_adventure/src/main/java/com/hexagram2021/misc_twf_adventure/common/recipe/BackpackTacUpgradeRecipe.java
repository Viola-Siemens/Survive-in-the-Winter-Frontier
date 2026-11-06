package com.hexagram2021.misc_twf_adventure.common.recipe;

import com.hexagram2021.misc_twf_adventure.common.data_component.TravelersBackpackTacData;
import com.hexagram2021.misc_twf_adventure.common.register.MISCTWFDataComponentTypes;
import com.hexagram2021.misc_twf_adventure.common.register.MISCTWFRecipeSerializers;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import net.minecraft.world.level.Level;

/**
 * 旅行者背包战术升级配方喵~：通过锻造台把背包升级为带弹药槽的战术版本。
 *
 * <p>1.21 中 `SmithingTransformRecipe` 的字段已私有化，因此改为直接实现 {@link SmithingRecipe}，
 * 自行持有模板/基础/附加原料与结果，并在组装时写入战术背包数据组件喵~</p>
 *
 * @param template 模板原料喵~
 * @param base     基础原料（背包）喵~
 * @param addition 附加原料喵~
 * @param result   合成结果物品喵~
 *
 * @author liudongyu
 */
public record BackpackTacUpgradeRecipe(Ingredient template, Ingredient base, Ingredient addition,
									   ItemStack result) implements SmithingRecipe {
	@Override
	public boolean matches(SmithingRecipeInput input, Level level) {
		return this.template.test(input.template()) && this.base.test(input.base()) && this.addition.test(input.addition());
	}

	@Override
	public ItemStack assemble(SmithingRecipeInput input, HolderLookup.Provider provider) {
		ItemStack itemStack = this.result.copy();
		TravelersBackpackTacData data = itemStack.getOrDefault(
				MISCTWFDataComponentTypes.TRAVELERS_BACKPACK_TAC_DATA, TravelersBackpackTacData.EMPTY
		);
		itemStack.set(MISCTWFDataComponentTypes.TRAVELERS_BACKPACK_TAC_DATA,
				new TravelersBackpackTacData(true, data.ammoInventory()));
		return itemStack;
	}

	@Override
	public ItemStack getResultItem(HolderLookup.Provider provider) {
		return this.result;
	}

	@Override
	public boolean isTemplateIngredient(ItemStack stack) {
		return this.template.test(stack);
	}

	@Override
	public boolean isBaseIngredient(ItemStack stack) {
		return this.base.test(stack);
	}

	@Override
	public boolean isAdditionIngredient(ItemStack stack) {
		return this.addition.test(stack);
	}

	@Override
	public boolean isIncomplete() {
		return this.template.hasNoItems() || this.base.hasNoItems() || this.addition.hasNoItems();
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return MISCTWFRecipeSerializers.BACKPACK_TAC_UPGRADE.get();
	}

	/**
	 * 获取模板原料喵~
	 *
	 * @return 模板原料喵~
	 */
	@Override
	public Ingredient template() {
		return this.template;
	}

	/**
	 * 获取基础原料喵~
	 *
	 * @return 基础原料喵~
	 */
	@Override
	public Ingredient base() {
		return this.base;
	}

	/**
	 * 获取附加原料喵~
	 *
	 * @return 附加原料喵~
	 */
	@Override
	public Ingredient addition() {
		return this.addition;
	}

	/**
	 * 获取结果物品喵~
	 *
	 * @return 结果物品喵~
	 */
	@Override
	public ItemStack result() {
		return this.result;
	}

	/**
	 * 战术背包升级配方的序列化器喵~
	 *
	 * @author liudongyu
	 */
	public static class Serializer implements RecipeSerializer<BackpackTacUpgradeRecipe> {
		/**
		 * 配方 JSON 编解码器喵~
		 */
		private static final MapCodec<BackpackTacUpgradeRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
				Ingredient.CODEC.fieldOf("template").forGetter(BackpackTacUpgradeRecipe::template),
				Ingredient.CODEC.fieldOf("base").forGetter(BackpackTacUpgradeRecipe::base),
				Ingredient.CODEC.fieldOf("addition").forGetter(BackpackTacUpgradeRecipe::addition),
				ItemStack.STRICT_CODEC.fieldOf("result").forGetter(BackpackTacUpgradeRecipe::result)
		).apply(instance, BackpackTacUpgradeRecipe::new));

		/**
		 * 配方网络编解码器喵~
		 */
		public static final StreamCodec<RegistryFriendlyByteBuf, BackpackTacUpgradeRecipe> STREAM_CODEC = StreamCodec.composite(
				Ingredient.CONTENTS_STREAM_CODEC, BackpackTacUpgradeRecipe::template,
				Ingredient.CONTENTS_STREAM_CODEC, BackpackTacUpgradeRecipe::base,
				Ingredient.CONTENTS_STREAM_CODEC, BackpackTacUpgradeRecipe::addition,
				ItemStack.STREAM_CODEC, BackpackTacUpgradeRecipe::result,
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