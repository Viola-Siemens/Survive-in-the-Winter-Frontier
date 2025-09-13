package com.hexagram2021.misc_twf.mixin.vanilla;

import com.hexagram2021.misc_twf.client.IHasCustomIconRecipe;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.screens.recipebook.RecipeButton;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(RecipeButton.class)
public class RecipeButtonMixin {
	@WrapOperation(method = {"renderButton", "getTooltipText", "updateNarration"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/Recipe;getResultItem()Lnet/minecraft/world/item/ItemStack;"))
	private ItemStack misc_twf$tryReplaceIcon(Recipe<?> instance, Operation<ItemStack> original) {
		ItemStack ret = original.call(instance);
		if(instance instanceof IHasCustomIconRecipe recipe) {
			return recipe.misc_twf$recipeIcon(ret);
		}
		return ret;
	}
}
