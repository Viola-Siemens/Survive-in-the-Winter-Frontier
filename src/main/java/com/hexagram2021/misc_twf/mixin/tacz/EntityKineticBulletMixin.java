package com.hexagram2021.misc_twf.mixin.tacz;

import com.hexagram2021.misc_twf.common.register.MISCTWFAttributes;
import com.tacz.guns.entity.EntityKineticBullet;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@SuppressWarnings("DataFlowIssue")
@Mixin(value = EntityKineticBullet.class, remap = false)
public class EntityKineticBulletMixin {
	@ModifyVariable(method = "tacAttackEntity", at = @At("HEAD"), argsOnly = true, index = 3)
	private float misc_twf$applyGunMastery(float value) {
		float multiplier = 1.0F;
		if(((EntityKineticBullet)(Object)this).getOwner() instanceof LivingEntity livingEntity) {
			AttributeInstance gunMastery = livingEntity.getAttribute(MISCTWFAttributes.GUN_MASTERY);
			if(gunMastery != null) {
				multiplier += (float)(gunMastery.getValue() / 100.0D);
			}
		}
		return value * multiplier;
	}
}
