package com.hexagram2021.misc_twf.common.item;

import com.hexagram2021.misc_twf.common.config.MISCTWFCommonConfig;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.energy.CapabilityEnergy;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class NightVisionDeviceItem extends Item implements ICurioItem, IEnergyItem {
	public NightVisionDeviceItem(Properties props) {
		super(props);
	}

	@Override
	public void curioTick(SlotContext slotContext, ItemStack stack) {
		LivingEntity entity = slotContext.entity();
		if(entity.tickCount % 20 == 0) {
			entity.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 30));
			stack.getCapability(CapabilityEnergy.ENERGY).ifPresent(ies -> ies.extractEnergy(1, false));
		}
	}

	@Override
	public int getEnergyCapability() {
		return MISCTWFCommonConfig.NIGHT_VISION_DEVICE_ENERGY_CAPABILITY.get();
	}
}
