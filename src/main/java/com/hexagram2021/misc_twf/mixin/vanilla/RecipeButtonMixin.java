package com.hexagram2021.misc_twf.mixin.vanilla;

import com.hexagram2021.misc_twf.client.IHasCustomIconRecipe;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.screens.recipebook.RecipeButton;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * 配方按钮 Mixin，在配方书 UI 中为实现了 {@link IHasCustomIconRecipe} 的配方替换显示图标喵~
 *
 * @author liudongyu
 */
@Mixin(RecipeButton.class)
public class RecipeButtonMixin {
	/**
	 * 包装 {@link Recipe#getResultItem} 调用，当配方实现了 {@link IHasCustomIconRecipe} 时替换为自定义图标喵~
	 *
	 * @param instance 配方实例喵~
	 * @param registryAccess 注册表查询提供者喵~
	 * @param original 原始方法调用喵~
	 * @return 自定义图标物品，或原始结果物品喵~
	 */
	@WrapOperation(method = {"renderWidget", "getTooltipText", "updateWidgetNarration"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/Recipe;getResultItem(Lnet/minecraft/core/HolderLookup$Provider;)Lnet/minecraft/world/item/ItemStack;"))
	private ItemStack misc_twf$tryReplaceIcon(Recipe<?> instance, HolderLookup.Provider registryAccess, Operation<ItemStack> original) {
		ItemStack ret = original.call(instance, registryAccess);
		if(instance instanceof IHasCustomIconRecipe recipe) {
			return recipe.misc_twf$recipeIcon(ret);
		}
		return ret;
	}
}
