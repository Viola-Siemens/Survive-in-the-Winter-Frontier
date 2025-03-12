package com.hexagram2021.misc_twf.mixin.tacz;

import com.hexagram2021.misc_twf.common.util.IAmmoBackpack;
import com.tacz.guns.api.item.IAmmo;
import com.tacz.guns.api.item.gun.AbstractGunItem;
import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.capability.ITravelersBackpack;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = AbstractGunItem.class, remap = false)
public class AbstractGunItemMixin {
	@SuppressWarnings("ConstantConditions")
	@Inject(method = "canReload", at = @At(value = "RETURN", ordinal = 3), cancellable = true)
	private void misc_twf$checkTravelersBackpackTacSlot(LivingEntity shooter, ItemStack gunItem, CallbackInfoReturnable<Boolean> cir) {
		if(cir.getReturnValue()) {
			return;
		}
		if(shooter instanceof Player player) {
			ITravelersBackpack backpack = CapabilityUtils.getCapability(player).orElse(null);
			if (backpack == null) {
				return;
			}
			IAmmoBackpack ammoBackpack = (IAmmoBackpack) backpack.getContainer();
			if (!ammoBackpack.canStoreAmmo()) {
				return;
			}
			for (int i = 0; i < ammoBackpack.getAmmoHandler().getSlots(); ++i) {
				ItemStack ammoStack = ammoBackpack.getAmmoHandler().getStackInSlot(i);

				if (ammoStack.getItem() instanceof IAmmo iAmmo && iAmmo.isAmmoOfGun(gunItem, ammoStack)) {
					cir.setReturnValue(true);
					return;
				}
			}
		}
	}
}
