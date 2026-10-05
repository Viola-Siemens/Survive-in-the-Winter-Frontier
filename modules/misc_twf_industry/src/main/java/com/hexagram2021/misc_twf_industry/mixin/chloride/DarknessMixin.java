package com.hexagram2021.misc_twf_industry.mixin.chloride;

import com.hexagram2021.misc_twf_industry.common.register.MISCTWFItems;
import me.srrapero720.chloride.impl.Darkness;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

import java.util.Map;

/**
 * 针对 Chloride（原 embeddiumplus）真黑暗效果的 Mixin 喵~
 *
 * <p>当玩家佩戴尚有电量的夜视仪（Curios head 槽）或远行者头盔时，
 * 在 Chloride 计算黑暗亮度前关闭真黑暗，避免夜视装备被真黑暗吞掉喵~</p>
 *
 * @author liudongyu
 */
@Pseudo
@Mixin(value = Darkness.class, remap = false)
public class DarknessMixin {
	/** 模组真是黑暗功能是否有效 */
	@Shadow
	public static boolean enabled;

	/**
	 * 在 Chloride 更新黑暗亮度表前拦截喵~
	 *
	 * @param ci 回调信息喵~
	 */
	@Inject(method = "updateLuminance", at = @At("HEAD"), cancellable = true, remap = false)
	private static void misc_twf_industry$disableDarknessWithNightVisionDevice(CallbackInfo ci) {
		if(misc_twf_industry$hasChargedNightVision()) {
			enabled = false;
			ci.cancel();
		}
	}

	/**
	 * 判断玩家是否佩戴有电的夜视装备喵~
	 *
	 * @return 是否应关闭真黑暗喵~
	 */
	@Unique
	private static boolean misc_twf_industry$hasChargedNightVision() {
		Minecraft minecraft = Minecraft.getInstance();
		if(minecraft.player == null) {
			return false;
		}
		Boolean curio = CuriosApi.getCuriosInventory(minecraft.player).map(handler -> {
			Map<String, ICurioStacksHandler> curios = handler.getCurios();
			for(Map.Entry<String, ICurioStacksHandler> entry : curios.entrySet()) {
				ICurioStacksHandler stacksHandler = entry.getValue();
				IDynamicStackHandler stackHandler = stacksHandler.getStacks();
				for(int i = 0; i < stacksHandler.getSlots(); i++) {
					ItemStack stack = stackHandler.getStackInSlot(i);
					if(stack.is(MISCTWFItems.NIGHT_VISION_DEVICE.get())) {
						IEnergyStorage ies = stack.getCapability(Capabilities.EnergyStorage.ITEM);
						if(ies != null && ies.getEnergyStored() > 0) {
							return true;
						}
					}
				}
			}
			return false;
		}).orElse(false);
		if(curio) {
			return true;
		}
		ItemStack head = minecraft.player.getItemBySlot(EquipmentSlot.HEAD);
		if(head.is(MISCTWFItems.WAYFARER_ARMORS.get(ArmorItem.Type.HELMET).get())) {
			IEnergyStorage ies = head.getCapability(Capabilities.EnergyStorage.ITEM);
			return ies != null && ies.getEnergyStored() > 0;
		}
		return false;
	}
}
