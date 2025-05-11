package com.hexagram2021.misc_twf.common.recipe;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.hexagram2021.misc_twf.client.IHasCustomIconRecipe;
import com.hexagram2021.misc_twf.common.recipe.cache.CachedRecipeList;
import com.hexagram2021.misc_twf.common.register.MISCTWFBlocks;
import com.hexagram2021.misc_twf.common.register.MISCTWFRecipeSerializers;
import com.hexagram2021.misc_twf.common.register.MISCTWFRecipeTypes;
import com.tacz.guns.crafting.result.GunSmithTableResult;
import com.tacz.guns.resource.CommonAssetsManager;
import net.minecraft.Util;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public record RecoveryFurnaceRecipe(ResourceLocation id, GunSmithTableResult ingredient, List<ItemStack> results, float experience, int recoveringTime) implements Recipe<Container>, IHasCustomIconRecipe {
	public static final CachedRecipeList<RecoveryFurnaceRecipe> recipeList = new CachedRecipeList<>(
			MISCTWFRecipeTypes.RECOVERY_FURNACE,
			RecoveryFurnaceRecipe.class
	);

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
		builder.put(Tags.Items.STORAGE_BLOCKS_QUARTZ, Items.QUARTZ_BLOCK);
		builder.put(Tags.Items.GEMS_AMETHYST, Items.AMETHYST_SHARD);
		builder.put(Tags.Items.GEMS_DIAMOND, Items.DIAMOND);
		builder.put(Tags.Items.GEMS_EMERALD, Items.EMERALD);
		builder.put(Tags.Items.GEMS_LAPIS, Items.LAPIS_LAZULI);
		builder.put(Tags.Items.GEMS_PRISMARINE, Items.PRISMARINE_CRYSTALS);
		builder.put(Tags.Items.GEMS_QUARTZ, Items.QUARTZ);
		return builder.build();
	});

	@Override
	public boolean matches(Container container, Level level) {
		ItemStack ingredient = this.ingredient.getResult();
		ItemStack slotItem = container.getItem(0);
		return containsTag(slotItem, ingredient) && ingredient.getCount() <= slotItem.getCount();
	}

	private static final ImmutableSet<String> CHECKING_TAG_KEYS = Util.make(() -> {
		ImmutableSet.Builder<String> builder = ImmutableSet.builder();
		builder.add("AmmoId");
		builder.add("AttachmentId");
		builder.add("GunId");
		return builder.build();
	});

	private static boolean containsTag(ItemStack source, ItemStack target) {
		CompoundTag targetTag = target.getTag();
		if(targetTag == null) {
			return true;
		}
		CompoundTag srcTag = source.getTag();
		if(srcTag == null) {
			return false;
		}
		return targetTag.getAllKeys().stream().filter(CHECKING_TAG_KEYS::contains).allMatch(key -> Objects.equals(targetTag.get(key), srcTag.get(key)));
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
		ret.add(Ingredient.of(this.ingredient.getResult()));
		return ret;
	}

	@Override
	public String getGroup() {
		return this.ingredient.getGroup();
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

	@Override
	public ItemStack misc_twf$recipeIcon(ItemStack ingredient) {
		return this.ingredient.getResult();
	}

	public static class Serializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<RecoveryFurnaceRecipe> {
		public static final int DEFAULT_RECOVERING_TIME = 200;

		@SuppressWarnings("deprecation")
		@Override
		public RecoveryFurnaceRecipe fromJson(ResourceLocation id, JsonObject json) {
			JsonElement ingredientJson = GsonHelper.isArrayNode(json, "ingredient") ? GsonHelper.getAsJsonArray(json, "ingredient") : GsonHelper.getAsJsonObject(json, "ingredient");

			GunSmithTableResult ingredient;
			if(ingredientJson.isJsonObject()) {
				ingredient = CommonAssetsManager.GSON.fromJson(ingredientJson.getAsJsonObject(), GunSmithTableResult.class);
			} else {
				throw new JsonSyntaxException("Expected ingredient to be a JsonObject, was " + GsonHelper.getType(ingredientJson));
			}
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
			return new RecoveryFurnaceRecipe(id, ingredient, results, experience, time);
		}

		@Override
		public RecoveryFurnaceRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
			ItemStack resultItem = buf.readItem();
			String group = buf.readUtf();
			List<ItemStack> results = buf.readCollection(Lists::newArrayListWithCapacity, FriendlyByteBuf::readItem);
			float experience = buf.readFloat();
			int time = buf.readVarInt();
			return new RecoveryFurnaceRecipe(id, new GunSmithTableResult(resultItem, group), results, experience, time);
		}

		@Override
		public void toNetwork(FriendlyByteBuf buf, RecoveryFurnaceRecipe recipe) {
			buf.writeItem(recipe.ingredient.getResult());
			buf.writeUtf(recipe.ingredient.getGroup());
			buf.writeCollection(recipe.results, FriendlyByteBuf::writeItem);
			buf.writeFloat(recipe.experience);
			buf.writeVarInt(recipe.recoveringTime);
		}
	}
}
