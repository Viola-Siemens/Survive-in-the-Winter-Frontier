package com.hexagram2021.misc_twf.mixin.vanilla.entities;

import com.hexagram2021.misc_twf.common.register.MISCTWFItems;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.item.Item;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(Parrot.class)
public class ParrotEntityMixin {
	@Shadow @Final
	private static Set<Item> TAME_FOOD;

	@Inject(method = "<clinit>", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/animal/Parrot;TAME_FOOD:Ljava/util/Set;", shift = At.Shift.AFTER, opcode = Opcodes.PUTSTATIC))
	private static void misc_twf$addWinterWheatToTameFood(CallbackInfo ci) {
		TAME_FOOD.add(MISCTWFItems.Materials.WINTER_WHEAT.get());
	}
}
