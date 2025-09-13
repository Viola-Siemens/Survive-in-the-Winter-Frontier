package com.hexagram2021.misc_twf.mixin.vanilla.entities;

import com.hexagram2021.misc_twf.common.util.IngredientHack;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.item.crafting.Ingredient;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Chicken.class)
public class ChickenEntityMixin {
	@Shadow @Final @Mutable
	private static Ingredient FOOD_ITEMS;

	@Inject(method = "<clinit>", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/animal/Chicken;FOOD_ITEMS:Lnet/minecraft/world/item/crafting/Ingredient;", shift = At.Shift.AFTER))
	private static void misc_twf$injectFoodItems(CallbackInfo ci) {
		FOOD_ITEMS = IngredientHack.addWinterWheatSeedsToIngredient(FOOD_ITEMS);
	}
}
