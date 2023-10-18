package com.hexagram2021.misc_twf.mixin;

import com.hexagram2021.misc_twf.common.util.IAmmoBackpack;
import com.tac.guns.common.ReloadTracker;
import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.capability.ITravelersBackpack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ReloadTracker.class)
public class ReloadTrackerMixin {
	@SuppressWarnings("ConstantConditions")
	@Inject(method = "shrinkFromAmmoPool", at = @At(value = "RETURN"), remap = false)
	private void setTravelersBackpackChanged(ItemStack[] ammoStacks, Player player, int shrinkAmount, CallbackInfo ci) {
		ITravelersBackpack backpack = CapabilityUtils.getCapability(player).orElse(null);
		if(backpack == null) {
			return;
		}
		IAmmoBackpack ammoBackpack = (IAmmoBackpack)backpack.getContainer();
		if(!ammoBackpack.canStoreAmmo()) {
			return;
		}
		ammoBackpack.saveAmmo(backpack.getContainer().getItemStack().getOrCreateTag());
		CapabilityUtils.synchronise(player);
		CapabilityUtils.synchroniseToOthers(player);
	}
}
