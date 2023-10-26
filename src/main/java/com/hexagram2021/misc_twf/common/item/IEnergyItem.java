package com.hexagram2021.misc_twf.common.item;

import com.hexagram2021.misc_twf.common.ForgeEventHandler;
import com.hexagram2021.misc_twf.common.item.capability.TaggedEnergyStorage;
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
		if(nbt.contains(ForgeEventHandler.ENERGY.toString(), Tag.TAG_INT)) {
			stack.getCapability(CapabilityEnergy.ENERGY).ifPresent(ies -> {
				if(ies instanceof INBTSerializable) {
					INBTSerializable<Tag> nbtSerializable = (INBTSerializable<Tag>)ies;
					nbtSerializable.deserializeNBT(nbt.get(ForgeEventHandler.ENERGY.toString()));
				} else if(ies instanceof TaggedEnergyStorage es) {
					es.setEnergy(nbt.getInt(ForgeEventHandler.ENERGY.toString()));
				}
			});
		}
	}

	default CompoundTag getMaxEnergyTag(ItemStack itemStack) {
		CompoundTag nbt = itemStack.getOrCreateTag();
		nbt.putInt(ForgeEventHandler.ENERGY.toString(), this.getEnergyCapability());
		return nbt;
	}
}
