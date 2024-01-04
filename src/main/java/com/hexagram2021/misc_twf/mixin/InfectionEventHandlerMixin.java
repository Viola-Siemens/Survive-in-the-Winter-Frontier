package com.hexagram2021.misc_twf.mixin;

import com.hexagram2021.misc_twf.server.MISCTWFSavedData;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.smileycorp.hordes.common.event.InfectionDeathEvent;
import net.smileycorp.hordes.common.infection.InfectionEventHandler;
import net.smileycorp.hordes.common.infection.InfectionRegister;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = InfectionEventHandler.class, priority = 42)
public class InfectionEventHandlerMixin {
	@Redirect(method = "onDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;)Z"), remap = false)
	private boolean misc_twf$ignoreIfPlayerVillagerImmuneToInfection(LivingEntity instance, MobEffectInstance mobEffectInstance) {
		if(MISCTWFSavedData.isImmuneToZombification(instance.getUUID())) {
			return false;
		}
		return instance.addEffect(mobEffectInstance);
	}

	@Redirect(method = "onDamage", at = @At(value = "INVOKE", target = "Lnet/smileycorp/hordes/common/infection/InfectionRegister;tryToInfect(Lnet/minecraft/world/entity/LivingEntity;)V", remap = false), remap = false)
	private void misc_twf$ignoreIfMobImmuneToInfection(LivingEntity entity) {
		if(!MISCTWFSavedData.isImmuneToZombification(entity.getUUID())) {
			InfectionRegister.tryToInfect(entity);
		}
	}

	@Inject(method = "onInfectDeath", at = @At(value = "HEAD"), cancellable = true, remap = false)
	private void misc_twf$ignoreConversionIfImmuneToInfectionDeath(InfectionDeathEvent event, CallbackInfo ci) {
		if(MISCTWFSavedData.isImmuneToZombification(event.getEntity().getUUID())) {
			ci.cancel();
		}
	}
}
