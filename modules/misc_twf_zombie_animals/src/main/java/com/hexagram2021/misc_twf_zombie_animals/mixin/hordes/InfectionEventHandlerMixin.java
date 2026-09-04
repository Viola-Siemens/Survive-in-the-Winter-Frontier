package com.hexagram2021.misc_twf_zombie_animals.mixin.hordes;

import com.hexagram2021.misc_twf_zombie_animals.server.MISCTWFImmunitySavedData;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.smileycorp.hordes.common.event.InfectionDeathEvent;
import net.smileycorp.hordes.infection.InfectionEventHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Hordes 感染事件豁免 mixin 喵~
 *
 * <p>当实体处于“对僵尸化免疫”状态时，跳过 Hordes 的感染施加与感染死亡转化（决策 D4：免疫属主随僵尸动物模块）喵~</p>
 *
 * @author liudongyu
 */
@Mixin(value = InfectionEventHandler.class, priority = 42)
public class InfectionEventHandlerMixin {
	/**
	 * 在 Hordes 的受伤感染处理开头豁免免疫实体喵~
	 *
	 * @param event 生物受伤事件（Post）喵~
	 * @param ci    回调信息喵~
	 */
	@Inject(method = "onDamage", at = @At("HEAD"), cancellable = true, remap = false)
	private void misc_twf_zombie_animals$ignoreIfImmuneToInfection(LivingDamageEvent.Post event, CallbackInfo ci) {
		LivingEntity entity = event.getEntity();
		if(MISCTWFImmunitySavedData.isImmuneToZombification(entity.getUUID())) {
			ci.cancel();
		}
	}

	/**
	 * 在 Hordes 的感染死亡转化处理开头豁免免疫实体喵~
	 *
	 * @param event 感染死亡事件喵~
	 * @param ci    回调信息喵~
	 */
	@Inject(method = "onInfectDeath", at = @At("HEAD"), cancellable = true, remap = false)
	private void misc_twf_zombie_animals$ignoreConversionIfImmuneToInfectionDeath(InfectionDeathEvent event, CallbackInfo ci) {
		if(MISCTWFImmunitySavedData.isImmuneToZombification(event.getEntity().getUUID())) {
			ci.cancel();
		}
	}
}
