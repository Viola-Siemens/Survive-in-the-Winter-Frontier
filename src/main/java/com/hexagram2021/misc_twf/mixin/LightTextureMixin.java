package com.hexagram2021.misc_twf.mixin;

import com.hexagram2021.misc_twf.common.register.MISCTWFItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

import java.util.Map;

@Mixin(LightTexture.class)
public class LightTextureMixin {
	@Shadow @Final
	private Minecraft minecraft;

	@SuppressWarnings("ConstantConditions")
	@ModifyConstant(method = "updateLightTexture", constant = @Constant(floatValue = 0.0F, ordinal = 1))
	public float getNightVisionDeviceBrightness(float constant) {
		ICuriosItemHandler handler = CuriosApi.getCuriosHelper().getCuriosHandler(this.minecraft.player).orElse(null);
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
							return 1.0F;
						}
					}
				}
			}
		}
		ItemStack stack = this.minecraft.player.getItemBySlot(EquipmentSlot.HEAD);
		if(stack.is(MISCTWFItems.WAYFARER_ARMORS.get(EquipmentSlot.HEAD).get())) {
			IEnergyStorage ies = stack.getCapability(CapabilityEnergy.ENERGY).orElse(null);
			if (ies != null && ies.getEnergyStored() > 0) {
				return 1.0F;
			}
		}
		return constant;
	}
}
