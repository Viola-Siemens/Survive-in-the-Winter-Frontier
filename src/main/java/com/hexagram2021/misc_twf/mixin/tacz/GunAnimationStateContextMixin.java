package com.hexagram2021.misc_twf.mixin.tacz;

import com.hexagram2021.misc_twf.common.util.IAmmoBackpack;
import com.tacz.guns.api.item.IAmmo;
import com.tacz.guns.client.animation.statemachine.GunAnimationStateContext;
import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.capability.ITravelersBackpack;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import java.util.function.Function;

@Mixin(value = GunAnimationStateContext.class, remap = false)
public abstract class GunAnimationStateContextMixin {
	@Shadow
	protected abstract <T> Optional<T> processCameraEntity(Function<Entity, T> processor);

	@Shadow
	private ItemStack currentGunItem;

	@SuppressWarnings("ConstantConditions")
	@Inject(method = "hasAmmoToConsume", at = @At(value = "RETURN", ordinal = 2), cancellable = true)
	private void misc_twf$checkTravelersBackpackTacSlot(CallbackInfoReturnable<Boolean> cir) {
		if(cir.getReturnValue()) {
			return;
		}
		cir.setReturnValue(processCameraEntity(entity -> {
			if(entity instanceof Player player) {
				ITravelersBackpack backpack = CapabilityUtils.getCapability(player).orElse(null);
				if (backpack == null) {
					return false;
				}
				IAmmoBackpack ammoBackpack = (IAmmoBackpack) backpack.getContainer();
				if (!ammoBackpack.canStoreAmmo()) {
					return false;
				}
				for (int i = 0; i < ammoBackpack.getAmmoHandler().getSlots(); ++i) {
					ItemStack ammoStack = ammoBackpack.getAmmoHandler().getStackInSlot(i);

					if (ammoStack.getItem() instanceof IAmmo iAmmo && iAmmo.isAmmoOfGun(this.currentGunItem, ammoStack)) {
						return true;
					}
				}
			}
			return false;
		}).orElse(false));
	}
}
