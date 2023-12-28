package com.hexagram2021.misc_twf.mixin;

import com.hexagram2021.misc_twf.common.register.MISCTWFFluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Entity.class)
public class EntityMixin {
	@Redirect(method = "updateInWaterStateAndDoFluidPushing", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;updateFluidHeightAndDoFluidPushing(Lnet/minecraft/tags/TagKey;D)Z"))
	private boolean misc_twf$updateInBloodState(Entity instance, TagKey<Fluid> fluidTag, double multiplier) {
		return instance.updateFluidHeightAndDoFluidPushing(fluidTag, multiplier) ||
				instance.updateFluidHeightAndDoFluidPushing(MISCTWFFluidTags.BLOOD, 0.008D);
	}
}
