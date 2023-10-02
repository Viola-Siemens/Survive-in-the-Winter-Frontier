package com.hexagram2021.misc_twf.common.item;

import com.hexagram2021.misc_twf.common.ForgeEventHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.energy.CapabilityEnergy;

public interface IEnergyItem {
	int getEnergyCapability();

	int getMaxEnergyReceiveSpeed();
	int getMaxEnergyExtractSpeed();

	default void getEnergyShareTag(CompoundTag nbt, ItemStack stack) {
		stack.getCapability(CapabilityEnergy.ENERGY).ifPresent(ies -> nbt.putInt(ForgeEventHandler.ENERGY.toString(), ies.getEnergyStored()));
	}

	@SuppressWarnings("unchecked")
	default void readEnergyShareTag(CompoundTag nbt, ItemStack stack) {
		if(nbt.contains(ForgeEventHandler.ENERGY.toString(), Tag.TAG_INT)) {
			stack.getCapability(CapabilityEnergy.ENERGY).ifPresent(ies -> {
				if(ies instanceof INBTSerializable) {
					INBTSerializable<Tag> nbtSerializable = (INBTSerializable<Tag>)ies;
					nbtSerializable.deserializeNBT(nbt.get(ForgeEventHandler.ENERGY.toString()));
				}
			});
		}
	}
}
