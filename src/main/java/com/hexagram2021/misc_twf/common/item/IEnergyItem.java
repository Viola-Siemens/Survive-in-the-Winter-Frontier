package com.hexagram2021.misc_twf.common.item;

import com.hexagram2021.misc_twf.common.ForgeEventHandler;
import com.hexagram2021.misc_twf.common.util.MISCTWFLogger;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.energy.CapabilityEnergy;

public interface IEnergyItem {
	int getEnergyCapability();

	int getMaxEnergyReceiveSpeed();
	int getMaxEnergyExtractSpeed();

	@SuppressWarnings("unchecked")
	default void readEnergyShareTag(CompoundTag nbt, ItemStack stack) {
		MISCTWFLogger.info(nbt);
		if(nbt.contains(ForgeEventHandler.ENERGY.toString(), Tag.TAG_INT)) {
			Tag energy = nbt.get(ForgeEventHandler.ENERGY.toString());
			stack.getCapability(CapabilityEnergy.ENERGY).ifPresent(ies -> {
				if(ies instanceof INBTSerializable) {
					INBTSerializable<Tag> nbtSerializable = (INBTSerializable<Tag>)ies;
					nbtSerializable.deserializeNBT(energy);
				}
			});
			nbt.remove(ForgeEventHandler.ENERGY.toString());
		}
	}

	default CompoundTag getMaxEnergyTag(ItemStack itemStack) {
		CompoundTag nbt = itemStack.getOrCreateTag();
		nbt.putInt(ForgeEventHandler.ENERGY.toString(), this.getEnergyCapability());
		return nbt;
	}
}
