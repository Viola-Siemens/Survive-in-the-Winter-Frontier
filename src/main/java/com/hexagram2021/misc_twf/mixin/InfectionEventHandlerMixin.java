package com.hexagram2021.misc_twf.mixin;

import com.hexagram2021.misc_twf.server.MISCTWFSavedData;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.smileycorp.hordes.common.event.InfectionDeathEvent;
import net.smileycorp.hordes.common.infection.InfectionEventHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = InfectionEventHandler.class, priority = 42)
public class InfectionEventHandlerMixin {
	@Inject(method = "onDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;)Z", shift = At.Shift.BEFORE), remap = false, cancellable = true)
	private void misc_twf$ignoreIfPlayerVillagerImmuneToInfection(LivingDamageEvent event, CallbackInfo ci) {
		LivingEntity entity = event.getEntityLiving();
		if(MISCTWFSavedData.isImmuneToZombification(entity.getUUID())) {
			ci.cancel();
		}
	}

	@Inject(method = "onDamage", at = @At(value = "INVOKE", target = "Lnet/smileycorp/hordes/common/infection/InfectionRegister;tryToInfect(Lnet/minecraft/world/entity/LivingEntity;)V", shift = At.Shift.BEFORE, remap = false), remap = false, cancellable = true)
	private void misc_twf$ignoreIfMobImmuneToInfection(LivingDamageEvent event, CallbackInfo ci) {
		LivingEntity entity = event.getEntityLiving();
		if(MISCTWFSavedData.isImmuneToZombification(entity.getUUID())) {
			ci.cancel();
		}
	}

	@Inject(method = "onInfectDeath", at = @At(value = "HEAD"), cancellable = true, remap = false)
	private void misc_twf$ignoreConversionIfImmuneToInfectionDeath(InfectionDeathEvent event, CallbackInfo ci) {
		if(MISCTWFSavedData.isImmuneToZombification(event.getEntity().getUUID())) {
			ci.cancel();
		}
	}
}
