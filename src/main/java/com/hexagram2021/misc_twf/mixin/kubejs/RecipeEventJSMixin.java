package com.hexagram2021.misc_twf.mixin.kubejs;

import com.google.gson.JsonObject;
import com.hexagram2021.misc_twf.common.recipe.DynamicRecipesUtil;
import com.hexagram2021.misc_twf.common.recipe.IRecipesAccessor;
import dev.latvian.mods.kubejs.recipe.RecipeEventJS;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(value = RecipeEventJS.class, remap = false)
public class RecipeEventJSMixin {
	@Inject(method = "post", at = @At(value = "RETURN"), require = 0)
	public void injectRecipes(RecipeManager recipeManager, Map<ResourceLocation, JsonObject> recipeJsons, CallbackInfo ci) {
		if(recipeManager instanceof IRecipesAccessor recipesAccessor) {
			DynamicRecipesUtil.addDynamicRecipes(recipesAccessor);
		}
	}
}
