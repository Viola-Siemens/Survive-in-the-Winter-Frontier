package com.hexagram2021.misc_twf.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.theillusivec4.diet.api.DietCapability;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
	@Unique
	private static final String MISC_TWF$GROUP_SALT = "salt";

	@Inject(method = "eat", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;addEatEffect(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;)V", shift = At.Shift.AFTER))
	public void misc_twf$applyToDietIfSalted(Level level, ItemStack itemStack, CallbackInfoReturnable<ItemStack> cir) {
		CompoundTag tag = itemStack.getTag();
		if(tag != null && tag.getBoolean("Salted")) {
			if((Object)this instanceof ServerPlayer serverPlayer) {
				DietCapability.get(serverPlayer).ifPresent(c -> {
					if(c.getValues().containsKey(MISC_TWF$GROUP_SALT)) {
						c.setValue(MISC_TWF$GROUP_SALT, c.getValue(MISC_TWF$GROUP_SALT) + 0.02F);
						c.sync();
					}
				});
			}
		}
	}
}
