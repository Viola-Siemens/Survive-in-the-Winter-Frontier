package com.hexagram2021.misc_twf.mixin.vanilla;

import com.google.gson.JsonElement;
import com.hexagram2021.misc_twf.common.recipe.DynamicRecipesUtil;
import com.hexagram2021.misc_twf.common.recipe.IRecipesAccessor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(RecipeManager.class)
public class RecipeManagerMixin implements IRecipesAccessor {
	@Shadow
	private Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> recipes;
	@Shadow
	private Map<ResourceLocation, Recipe<?>> byName;

	@Inject(method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V", at = @At(value = "RETURN"))
	private void misc_twf$addDynamicRecipes(Map<ResourceLocation, JsonElement> recipeJsons, ResourceManager resourceManager, ProfilerFiller profilerFiller, CallbackInfo ci) {
		DynamicRecipesUtil.addDynamicRecipes(this);
	}

	@Override
	public Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> misc_twf$recipes() {
		return this.recipes;
	}

	@Override
	public Map<ResourceLocation, Recipe<?>> misc_twf$byName() {
		return this.byName;
	}

	@Override
	public void misc_twf$setRecipes(Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> recipes) {
		this.recipes = recipes;
	}

	@Override
	public void misc_twf$setByName(Map<ResourceLocation, Recipe<?>> byName) {
		this.byName = byName;
	}
}
