package com.hexagram2021.misc_twf.common.recipe;

import com.google.common.collect.ImmutableMap;
import com.hexagram2021.misc_twf.client.IHasCustomIconRecipe;
import com.hexagram2021.misc_twf.common.recipe.cache.CachedRecipeList;
import com.hexagram2021.misc_twf.common.register.MISCTWFBlocks;
import com.hexagram2021.misc_twf.common.register.MISCTWFRecipeSerializers;
import com.hexagram2021.misc_twf.common.register.MISCTWFRecipeTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.Tags;

import java.util.List;
import java.util.Map;

/**
 * 回收炉配方，定义了回收炉将物品分解为多种原材料的逻辑喵~
 * <p>
 * 回收炉是多输出配方，一次输入可产出多个不同物品喵~
 * 支持经验奖励和自定义回收时间喵~
 *
 * @param ingredient 配方的输入物品（含数量）喵~
 * @param group 配方分组名称喵~
 * @param results 配方的输出结果列表喵~
 * @param experience 回收获得的经验值喵~
 * @param recoveringTime 回收所需时间（游戏刻）喵~
 *
 * @author liudongyu
 */
public record RecoveryFurnaceRecipe(ItemStack ingredient, String group, List<ItemStack> results, float experience, int recoveringTime) implements Recipe<SingleRecipeInput>, IHasCustomIconRecipe {
	/** 回收炉配方的缓存列表，用于快速查询已加载的配方喵~ */
	public static final CachedRecipeList<RecoveryFurnaceRecipe> recipeList = new CachedRecipeList<>(
			MISCTWFRecipeTypes.RECOVERY_FURNACE,
			RecoveryFurnaceRecipe.class
	);

	/** 常见可回收物品标签到对应物品的映射表，用于在 JEI 等兼容层中显示回收产物喵~ */
	public static final Map<TagKey<Item>, Item> COMMON_RECOVERABLE_TAGS = Util.make(() -> {
		ImmutableMap.Builder<TagKey<Item>, Item> builder = ImmutableMap.builder();
		builder.put(Tags.Items.NUGGETS_GOLD, Items.GOLD_NUGGET);
		builder.put(Tags.Items.NUGGETS_IRON, Items.IRON_NUGGET);
		builder.put(Tags.Items.INGOTS_COPPER, Items.COPPER_INGOT);
		builder.put(Tags.Items.INGOTS_GOLD, Items.GOLD_INGOT);
		builder.put(Tags.Items.INGOTS_IRON, Items.IRON_INGOT);
		builder.put(Tags.Items.INGOTS_NETHERITE, Items.NETHERITE_INGOT);
		builder.put(Tags.Items.STORAGE_BLOCKS_COPPER, Items.COPPER_BLOCK);
		builder.put(Tags.Items.STORAGE_BLOCKS_EMERALD, Items.EMERALD_BLOCK);
		builder.put(Tags.Items.STORAGE_BLOCKS_GOLD, Items.GOLD_BLOCK);
		builder.put(Tags.Items.STORAGE_BLOCKS_IRON, Items.IRON_BLOCK);
		builder.put(Tags.Items.STORAGE_BLOCKS_LAPIS, Items.LAPIS_BLOCK);
		builder.put(Tags.Items.STORAGE_BLOCKS_NETHERITE, Items.NETHERITE_BLOCK);
		builder.put(Tags.Items.GEMS_AMETHYST, Items.AMETHYST_SHARD);
		builder.put(Tags.Items.GEMS_DIAMOND, Items.DIAMOND);
		builder.put(Tags.Items.GEMS_EMERALD, Items.EMERALD);
		builder.put(Tags.Items.GEMS_LAPIS, Items.LAPIS_LAZULI);
		builder.put(Tags.Items.GEMS_PRISMARINE, Items.PRISMARINE_CRYSTALS);
		builder.put(Tags.Items.GEMS_QUARTZ, Items.QUARTZ);
		return builder.build();
	});

	/**
	 * 检查输入物品是否与配方匹配喵~
	 * 需要物品类型相同且输入数量不少于配方要求喵~
	 *
	 * @param container 单物品输入容器喵~
	 * @param level 当前世界喵~
	 * @return 匹配则返回 true喵~
	 */
	@Override
	public boolean matches(SingleRecipeInput container, Level level) {
		ItemStack slotItem = container.getItem(0);
		return ItemStack.isSameItem(this.ingredient, slotItem) && this.ingredient.getCount() <= slotItem.getCount();
	}

	/**
	 * @deprecated 多输出配方，请使用 {@link RecoveryFurnaceRecipe#results()} 获取所有产出喵~
	 */
	@Deprecated
	@Override
	public ItemStack getResultItem(HolderLookup.Provider provider) {
		return this.results.getFirst();
	}

	/**
	 * @deprecated 多输出配方，请使用 {@link RecoveryFurnaceRecipe#assembleAll(SingleRecipeInput)} 获取产出的完整结果喵~
	 */
	@Deprecated
	@Override
	public ItemStack assemble(SingleRecipeInput container, HolderLookup.Provider provider) {
		return this.getResultItem(provider).copy();
	}

	/**
	 * 组装所有配方产物并返回副本列表喵~
	 *
	 * @param container 单物品输入容器喵~
	 * @return 所有产出物品的副本列表喵~
	 */
	public List<ItemStack> assembleAll(SingleRecipeInput container) {
		return this.results.stream().map(ItemStack::copy).toList();
	}

	@Override
	public boolean canCraftInDimensions(int wid, int hgt) {
		return true;
	}

	@Override
	public NonNullList<Ingredient> getIngredients() {
		NonNullList<Ingredient> ret = NonNullList.create();
		ret.add(Ingredient.of(this.ingredient));
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
	public RecipeSerializer<RecoveryFurnaceRecipe> getSerializer() {
		return MISCTWFRecipeSerializers.RECOVERY_FURNACE.get();
	}

	@Override
	public RecipeType<RecoveryFurnaceRecipe> getType() {
		return MISCTWFRecipeTypes.RECOVERY_FURNACE.get();
	}

	/**
	 * 返回回收炉配方在配方书中的自定义图标，即输入物品喵~
	 *
	 * @param ingredient 默认的配方结果物品喵~
	 * @return 配方输入物品作为图标喵~
	 */
	@Override
	public ItemStack misc_twf$recipeIcon(ItemStack ingredient) {
		return this.ingredient;
	}

	/**
	 * 回收炉配方的序列化器，基于 Codec 实现 JSON 和网络编解码喵~
	 */
	public static class Serializer implements RecipeSerializer<RecoveryFurnaceRecipe> {
		/** 默认回收时间（游戏刻）喵~ */
		public static final int DEFAULT_RECOVERING_TIME = 200;

		/** 配方的 MapCodec 编解码器，用于 JSON 序列化与反序列化喵~ */
		private static final MapCodec<RecoveryFurnaceRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
				ItemStack.CODEC.fieldOf("ingredient").forGetter(RecoveryFurnaceRecipe::ingredient),
				Codec.STRING.fieldOf("group").forGetter(RecoveryFurnaceRecipe::group),
				ItemStack.CODEC.listOf().fieldOf("results").forGetter(RecoveryFurnaceRecipe::results),
				Codec.FLOAT.optionalFieldOf("experience", 0.0F).forGetter(RecoveryFurnaceRecipe::experience),
				Codec.INT.optionalFieldOf("recoveringTime", DEFAULT_RECOVERING_TIME).forGetter(RecoveryFurnaceRecipe::recoveringTime)
		).apply(instance, RecoveryFurnaceRecipe::new));

		/** 配方的 StreamCodec 网络编解码器，用于客户端与服务端之间的网络传输喵~ */
		private static final StreamCodec<RegistryFriendlyByteBuf, RecoveryFurnaceRecipe> STREAM_CODEC = StreamCodec.composite(
				ItemStack.STREAM_CODEC, RecoveryFurnaceRecipe::ingredient,
				ByteBufCodecs.STRING_UTF8, RecoveryFurnaceRecipe::group,
				ItemStack.LIST_STREAM_CODEC, RecoveryFurnaceRecipe::results,
				ByteBufCodecs.FLOAT, RecoveryFurnaceRecipe::experience,
				ByteBufCodecs.INT, RecoveryFurnaceRecipe::recoveringTime,
				RecoveryFurnaceRecipe::new
		);

		@Override
		public MapCodec<RecoveryFurnaceRecipe> codec() {
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, RecoveryFurnaceRecipe> streamCodec() {
			return STREAM_CODEC;
		}
	}
}
