package com.hexagram2021.misc_twf.common.item;

import com.hexagram2021.misc_twf.common.ForgeEventHandler;
import com.hexagram2021.misc_twf.common.config.MISCTWFCommonConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.energy.CapabilityEnergy;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import javax.annotation.Nullable;

public class NightVisionDeviceItem extends Item implements ICurioItem, IEnergyItem {
	public NightVisionDeviceItem(Properties props) {
		super(props);
	}

	@Override
	public void curioTick(SlotContext slotContext, ItemStack stack) {
		LivingEntity entity = slotContext.entity();
		if(entity.tickCount % 20 == 0) {
			stack.getCapability(CapabilityEnergy.ENERGY).ifPresent(ies -> ies.extractEnergy(1, false));
		}
	}

	@Override
	public CompoundTag getShareTag(ItemStack stack) {
		CompoundTag nbt = stack.getOrCreateTag();
		stack.getCapability(CapabilityEnergy.ENERGY).ifPresent(ies -> nbt.putInt(ForgeEventHandler.ENERGY.toString(), ies.getEnergyStored()));
		return nbt;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void readShareTag(ItemStack stack, @Nullable CompoundTag nbt) {
		super.readShareTag(stack, nbt);

		if (nbt != null && nbt.contains(ForgeEventHandler.ENERGY.toString(), Tag.TAG_INT)) {
			stack.getCapability(CapabilityEnergy.ENERGY).ifPresent(ies -> {
				if(ies instanceof INBTSerializable) {
					INBTSerializable<Tag> nbtSerializable = (INBTSerializable<Tag>)ies;
					nbtSerializable.deserializeNBT(nbt.get(ForgeEventHandler.ENERGY.toString()));
				}
			});
		}
	}

	@Override
	public int getEnergyCapability() {
		return MISCTWFCommonConfig.NIGHT_VISION_DEVICE_ENERGY_CAPABILITY.get();
	}

	@Override
	public int getMaxEnergyReceiveSpeed() {
		return 2;
	}

	@Override
	public int getMaxEnergyExtractSpeed() {
		return 1;
	}
}
