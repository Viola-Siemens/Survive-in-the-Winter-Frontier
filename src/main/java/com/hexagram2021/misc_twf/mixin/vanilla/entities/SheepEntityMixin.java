package com.hexagram2021.misc_twf.mixin.vanilla.entities;

import com.hexagram2021.misc_twf.common.util.IngredientHack;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Sheep.class)
public class SheepEntityMixin {
	@WrapOperation(method = "registerGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/Ingredient;of([Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/item/crafting/Ingredient;"))
	private Ingredient misc_twf$wrapOperation(ItemLike[] items, Operation<Ingredient> original) {
		return IngredientHack.addWinterWheatToIngredient(items);
	}
}
