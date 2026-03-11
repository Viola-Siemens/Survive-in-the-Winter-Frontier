package com.hexagram2021.misc_twf.common.item;

import com.hexagram2021.misc_twf.common.config.MISCTWFCommonConfig;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

/**
 * 夜视仪物品，作为 Curios 饰品佩戴时每秒消耗能量以维持夜视效果喵~
 *
 * @author liudongyu
 */
public class NightVisionDeviceItem extends Item implements ICurioItem, IEnergyItem {
	/**
	 * 构造一个夜视仪物品喵~
	 *
	 * @param props 物品属性喵~
	 */
	public NightVisionDeviceItem(Properties props) {
		super(props);
	}

	@Override
	public void curioTick(SlotContext slotContext, ItemStack stack) {
		LivingEntity entity = slotContext.entity();
		if(entity.tickCount % 20 == 0) {
			IEnergyStorage ies = stack.getCapability(Capabilities.EnergyStorage.ITEM);
			if(ies != null) {
				ies.extractEnergy(1, false);
			}
		}
	}

	@Override
	public int getEnergyCapability() {
		return MISCTWFCommonConfig.NIGHT_VISION_DEVICE_ENERGY_CAPABILITY.get();
	}

	@Override
	public int getMaxEnergyReceiveSpeed() {
		return 5;
	}

	@Override
	public int getMaxEnergyExtractSpeed() {
		return 1;
	}
}
