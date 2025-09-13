package com.hexagram2021.misc_twf.mixin.vanilla.entities.ai;

import com.hexagram2021.misc_twf.common.util.IngredientHack;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.entity.animal.goat.GoatAi;
import net.minecraft.world.item.crafting.Ingredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GoatAi.class)
public class GoatAiMixin {
	@ModifyReturnValue(method = "getTemptations", at = @At(value = "RETURN"))
	private static Ingredient misc_twf$modifyTemptations(Ingredient original) {
		return IngredientHack.addWinterWheatToIngredient(original);
	}
}
