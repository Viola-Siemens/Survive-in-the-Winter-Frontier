package com.hexagram2021.misc_twf.mixin;

import com.hexagram2021.misc_twf.common.register.MISCTWFItems;
import me.srrapero720.embeddiumplus.features.true_darkness.Darkness;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

import java.util.Map;

@Mixin(Darkness.class)
public class DarknessMixin {
	@Shadow(remap = false)
	public static boolean enabled;

	@SuppressWarnings("ConstantConditions")
	@Inject(method = "updateLuminance", at = @At(value = "FIELD", target = "Lme/srrapero720/embeddiumplus/features/true_darkness/Darkness;enabled:Z", shift = At.Shift.BEFORE, ordinal = 1, remap = false), remap = false, cancellable = true)
	private static void disableIfWearingDevice(float tickDelta, Minecraft client, GameRenderer worldRenderer, float prevFlicker, CallbackInfo ci) {
		ICuriosItemHandler handler = CuriosApi.getCuriosHelper().getCuriosHandler(client.player).orElse(null);
		if(handler != null) {
			Map<String, ICurioStacksHandler> curios = handler.getCurios();
			for (Map.Entry<String, ICurioStacksHandler> entry : curios.entrySet()) {
				ICurioStacksHandler stacksHandler = entry.getValue();
				IDynamicStackHandler stackHandler = stacksHandler.getStacks();
				for (int i = 0; i < stacksHandler.getSlots(); i++) {
					ItemStack stack = stackHandler.getStackInSlot(i);
					if (stack.is(MISCTWFItems.NIGHT_VISION_DEVICE.get())) {
						IEnergyStorage ies = stack.getCapability(CapabilityEnergy.ENERGY).orElse(null);
						if (ies != null && ies.getEnergyStored() > 0) {
							enabled = false;
							ci.cancel();
						}
					}
				}
			}
		}
		ItemStack stack = client.player.getItemBySlot(EquipmentSlot.HEAD);
		if(stack.is(MISCTWFItems.WAYFARER_ARMORS.get(EquipmentSlot.HEAD).get())) {
			IEnergyStorage ies = stack.getCapability(CapabilityEnergy.ENERGY).orElse(null);
			if (ies != null && ies.getEnergyStored() > 0) {
				enabled = false;
				ci.cancel();
			}
		}
	}
}
