package com.hexagram2021.misc_twf.mixin;

import com.hexagram2021.misc_twf.common.util.IAmmoBackpack;
import com.tac.guns.common.Gun;
import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.capability.ITravelersBackpack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.ArrayList;

import static com.tac.guns.common.Gun.isAmmo;

@Mixin(Gun.class)
public class GunMixin {
	@SuppressWarnings("ConstantConditions")
	@Inject(method = "findAmmo", at = @At(value = "INVOKE", target = "Ljava/util/ArrayList;toArray([Ljava/lang/Object;)[Ljava/lang/Object;", shift = At.Shift.BEFORE, ordinal = 1), remap = false, locals = LocalCapture.CAPTURE_FAILSOFT)
	private static void checkTravelersBackpackTacSlot(Player player, ResourceLocation id, CallbackInfoReturnable<ItemStack[]> cir, ArrayList<ItemStack> stacks) {
		ITravelersBackpack backpack = CapabilityUtils.getCapability(player).orElse(null);
		if(backpack == null) {
			return;
		}
		IAmmoBackpack ammoBackpack = (IAmmoBackpack)backpack.getContainer();
		if(!ammoBackpack.canStoreAmmo()) {
			return;
		}
		for(int i = 0; i < ammoBackpack.getAmmoHandler().getSlots(); ++i) {
			ItemStack ammoStack = ammoBackpack.getAmmoHandler().getStackInSlot(i);
			if (isAmmo(ammoStack, id)) {
				stacks.add(ammoStack);
			}
		}
	}
}
