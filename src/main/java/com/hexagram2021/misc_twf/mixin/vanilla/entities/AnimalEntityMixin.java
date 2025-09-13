package com.hexagram2021.misc_twf.mixin.vanilla.entities;

import com.hexagram2021.misc_twf.common.register.MISCTWFItems;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Animal.class)
public class AnimalEntityMixin {
	@ModifyReturnValue(method = "isFood", at = @At(value = "RETURN"))
	private boolean misc_twf$modifyIsFood(boolean original, ItemStack stack) {
		return original || stack.is(MISCTWFItems.Materials.WINTER_WHEAT.get());
	}
}
