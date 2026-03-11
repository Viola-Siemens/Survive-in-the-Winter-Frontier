package com.hexagram2021.misc_twf.mixin.vanilla;

import com.hexagram2021.misc_twf.common.register.MISCTWFItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

import java.util.Map;

/**
 * 针对原版 {@link LightTexture} 类的 Mixin 喵~
 * <br/>
 * 当玩家佩戴夜视仪或旅行者头盔且电量充足时，修改光照纹理使玩家获得夜视效果喵~
 *
 * @author liudongyu
 */
@Mixin(LightTexture.class)
public class LightTextureMixin {
	@Shadow @Final
	private Minecraft minecraft;

	@SuppressWarnings("ConstantConditions")
	@ModifyConstant(method = "updateLightTexture", constant = @Constant(floatValue = 0.0F, ordinal = 1))
	public float misc_twf$getNightVisionDeviceBrightness(float constant) {
		Float ret = CuriosApi.getCuriosInventory(this.minecraft.player).map(handler -> {
			Map<String, ICurioStacksHandler> curios = handler.getCurios();
			for (Map.Entry<String, ICurioStacksHandler> entry : curios.entrySet()) {
				ICurioStacksHandler stacksHandler = entry.getValue();
				IDynamicStackHandler stackHandler = stacksHandler.getStacks();
				for (int i = 0; i < stacksHandler.getSlots(); i++) {
					ItemStack stack = stackHandler.getStackInSlot(i);
					if (stack.is(MISCTWFItems.NIGHT_VISION_DEVICE.get())) {
						IEnergyStorage ies = stack.getCapability(Capabilities.EnergyStorage.ITEM);
						if (ies != null && ies.getEnergyStored() > 0) {
							return 1.0F;
						}
					}
				}
			}
			return null;
		}).orElse(null);
		if(ret != null) {
			return ret;
		}
		ItemStack stack = this.minecraft.player.getItemBySlot(EquipmentSlot.HEAD);
		if(stack.is(MISCTWFItems.WAYFARER_ARMORS.get(ArmorItem.Type.HELMET).get())) {
			IEnergyStorage ies = stack.getCapability(Capabilities.EnergyStorage.ITEM);
			if (ies != null && ies.getEnergyStored() > 0) {
				return 1.0F;
			}
		}
		return constant;
	}
}
