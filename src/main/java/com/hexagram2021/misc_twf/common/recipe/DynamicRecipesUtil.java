package com.hexagram2021.misc_twf.common.recipe;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.hexagram2021.misc_twf.common.register.MISCTWFRecipeTypes;
import com.hexagram2021.misc_twf.mixin.tacz.GunSmithTableResultAccess;
import com.hexagram2021.misc_twf.mixin.tacz.RawGunTableResultAccess;
import com.tacz.guns.crafting.GunSmithTableRecipe;
import com.tacz.guns.crafting.result.GunSmithTableResult;
import com.tacz.guns.crafting.result.RawGunTableResult;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;

public final class DynamicRecipesUtil {
	@SuppressWarnings("deprecation")
	public static void addDynamicRecipes(IRecipesAccessor recipesAccessor) {
		Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> recipes = recipesAccessor.misc_twf$recipes();
		Map<ResourceLocation, Recipe<?>> byName = recipesAccessor.misc_twf$byName();

		ImmutableMap.Builder<ResourceLocation, Recipe<?>> typedBuilder = ImmutableMap.builder();
		Map<ResourceLocation, Recipe<?>> origins = recipes.get(MISCTWFRecipeTypes.RECOVERY_FURNACE.get());
		if(origins != null) {
			typedBuilder.putAll(origins);
		}
		ImmutableMap.Builder<ResourceLocation, Recipe<?>> fullBuilder = ImmutableMap.builder();
		fullBuilder.putAll(byName);

		recipes.get(com.tacz.guns.init.ModRecipe.GUN_SMITH_TABLE_CRAFTING.get()).forEach((id, recipe) -> {
			if(recipe instanceof GunSmithTableRecipe gunSmithTableRecipe) {
				RawGunTableResult raw = ((GunSmithTableResultAccess)gunSmithTableRecipe.getResult()).misc_twf$getResult();
				ResourceLocation newId = new ResourceLocation(MODID, "recovery_furnace/" + id.getPath());
				if(raw == null) {
					return;
				}
				float exp = switch (((RawGunTableResultAccess)raw).misc_twf$getType()) {
					case GunSmithTableResult.GUN -> 1.0F;
					case GunSmithTableResult.AMMO -> 0.15F;
					case GunSmithTableResult.ATTACHMENT -> 0.35F;
					default -> 0.0F;
				};
				ImmutableList.Builder<ItemStack> outputBuilder = ImmutableList.builder();
				gunSmithTableRecipe.getInputs().forEach(ingredient -> {
					for(Ingredient.Value value: ingredient.getIngredient().values) {
						if(value instanceof Ingredient.TagValue tagValue) {
							Item item = RecoveryFurnaceRecipe.COMMON_RECOVERABLE_TAGS.get(tagValue.tag);
							if(item != null && ingredient.getCount() > 0) {
								int count = Math.min(ingredient.getCount(), item.getMaxStackSize());
								ItemStack newIngredient = new ItemStack(item, count);
								outputBuilder.add(newIngredient);
								break;
							}
						} else {
							value.getItems().stream()
									.filter(itemStack -> RecoveryFurnaceRecipe.COMMON_RECOVERABLE_TAGS.containsValue(itemStack.getItem()))
									.findAny().ifPresent(itemStack -> {
										ItemStack newIngredient = new ItemStack(itemStack.getItem(), ingredient.getCount());
										outputBuilder.add(newIngredient);
									});
							break;
						}
					}
				});
				List<ItemStack> outputs = outputBuilder.build();
				if(!outputs.isEmpty() && outputs.size() <= 4) {
					RecoveryFurnaceRecipe newRecipe = new RecoveryFurnaceRecipe(newId, new GunSmithTableResult(raw), outputs, exp, RecoveryFurnaceRecipe.Serializer.DEFAULT_RECOVERING_TIME);
					typedBuilder.put(newId, newRecipe);
					fullBuilder.put(newId, newRecipe);
				}
			}
		});
		ImmutableMap.Builder<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> recipesBuilder = ImmutableMap.builder();
		AtomicBoolean flag = new AtomicBoolean(false);
		recipes.forEach((rt, rs) -> {
			if(rt == MISCTWFRecipeTypes.RECOVERY_FURNACE.get()) {
				recipesBuilder.put(rt, typedBuilder.build());
				flag.set(true);
			} else {
				recipesBuilder.put(rt, rs);
			}
		});
		if(!flag.get()) {
			recipesBuilder.put(MISCTWFRecipeTypes.RECOVERY_FURNACE.get(), typedBuilder.build());
		}
		recipesAccessor.misc_twf$setRecipes(recipesBuilder.build());
		recipesAccessor.misc_twf$setByName(fullBuilder.build());
	}
}
