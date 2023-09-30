package com.hexagram2021.misc_twf.mixin;

import com.hexagram2021.misc_twf.server.MISCTWFSavedData;
import net.minecraft.world.entity.LivingEntity;
import net.smileycorp.hordes.common.infection.InfectionRegister;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InfectionRegister.class)
public class InfectionRegisterMixin {
	@Inject(method = "tryToInfect", at = @At(value = "HEAD"), remap = false, cancellable = true)
	private static void checkImmuneToInfect(LivingEntity entity, CallbackInfo ci) {
		if(MISCTWFSavedData.isImmuneToZombification(entity.getUUID())) {
			ci.cancel();
		}
	}
}
