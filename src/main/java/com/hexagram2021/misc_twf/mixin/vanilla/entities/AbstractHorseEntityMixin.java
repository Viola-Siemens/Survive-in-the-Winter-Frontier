package com.hexagram2021.misc_twf.mixin.vanilla.entities;

import com.hexagram2021.misc_twf.common.register.MISCTWFItems;
import com.hexagram2021.misc_twf.common.util.IngredientHack;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractHorse.class)
public class AbstractHorseEntityMixin {
	@Shadow @Final @Mutable
	private static Ingredient FOOD_ITEMS;

	@Inject(method = "<clinit>", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/animal/horse/AbstractHorse;FOOD_ITEMS:Lnet/minecraft/world/item/crafting/Ingredient;", shift = At.Shift.AFTER))
	private static void misc_twf$injectFoodItems(CallbackInfo ci) {
		FOOD_ITEMS = IngredientHack.addWinterWheatToIngredient(FOOD_ITEMS);
	}

	@WrapOperation(method = "handleEating", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z", ordinal = 0))
	private boolean misc_twf$tryIsWinterWheat(ItemStack itemStack, Item item, Operation<Boolean> original) {
		return original.call(itemStack, item) || itemStack.is(MISCTWFItems.Materials.WINTER_WHEAT.get());
	}
}
