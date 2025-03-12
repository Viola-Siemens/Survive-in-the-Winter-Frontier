package com.hexagram2021.misc_twf.mixin.tacz;

import com.hexagram2021.misc_twf.common.util.IAmmoBackpack;
import com.tacz.guns.api.item.IAmmo;
import com.tacz.guns.client.gui.overlay.GunHudOverlay;
import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.capability.ITravelersBackpack;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GunHudOverlay.class, remap = false)
public class GunHudOverlayMixin {
	@Shadow
	private static int cacheInventoryAmmoCount;

	@SuppressWarnings("ConstantConditions")
	@Inject(method = "handleInventoryAmmo", at = @At(value = "RETURN"))
	private static void misc_twf$handleTravelersBackpackTacSlot(ItemStack stack, Inventory inventory, CallbackInfo ci) {
		Player player = inventory.player;
		ITravelersBackpack backpack = CapabilityUtils.getCapability(player).orElse(null);
		if (backpack == null) {
			return;
		}
		IAmmoBackpack ammoBackpack = (IAmmoBackpack)backpack.getContainer();
		if (!ammoBackpack.canStoreAmmo()) {
			return;
		}
		for (int i = 0; i < ammoBackpack.getAmmoHandler().getSlots(); ++i) {
			ItemStack ammoStack = ammoBackpack.getAmmoHandler().getStackInSlot(i);

			if (ammoStack.getItem() instanceof IAmmo iAmmo && iAmmo.isAmmoOfGun(stack, ammoStack)) {
				cacheInventoryAmmoCount += ammoStack.getCount();
			}
		}
	}
}
