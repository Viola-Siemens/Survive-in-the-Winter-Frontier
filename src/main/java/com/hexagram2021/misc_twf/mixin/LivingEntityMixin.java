package com.hexagram2021.misc_twf.mixin;

import com.hexagram2021.misc_twf.common.register.MISCTWFFluidTags;
import com.hexagram2021.misc_twf.common.util.MISCTWFLogger;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
	@Shadow
	protected abstract void jumpInLiquid(TagKey<Fluid> fluidTag);

	public LivingEntityMixin(EntityType<?> entityType, Level level) {
		super(entityType, level);
	}

	@Redirect(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getFluidHeight(Lnet/minecraft/tags/TagKey;)D", ordinal = 1))
	private double misc_twf$getCustomFluidHeight(LivingEntity instance, TagKey<Fluid> tagKey) {
		double bloodHeight = instance.getFluidHeight(MISCTWFFluidTags.BLOOD);
		if (bloodHeight > 0.0D) {
			return bloodHeight;
		}
		return instance.getFluidHeight(tagKey);
	}

	@Inject(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isInLava()Z", ordinal = 1, shift = At.Shift.BEFORE))
	private void misc_twf$jumpInBlood(CallbackInfo ci) {
		LivingEntity instance = (LivingEntity)(Object) this;
		double bloodHeight = instance.getFluidHeight(MISCTWFFluidTags.BLOOD);
		if (bloodHeight > instance.getFluidJumpThreshold()) {
			this.jumpInLiquid(MISCTWFFluidTags.BLOOD);
		}
	}
}
