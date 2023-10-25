package com.hexagram2021.misc_twf.common.item.capability;

import com.hexagram2021.misc_twf.common.ForgeEventHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.energy.EnergyStorage;

public class TaggedEnergyStorage extends EnergyStorage {
	private final ItemStack self;

	public TaggedEnergyStorage(ItemStack itemStack, int capacity, int maxReceive, int maxExtract) {
		super(capacity, maxReceive, maxExtract);
		this.self = itemStack;
	}

	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		int energy = super.receiveEnergy(maxReceive, simulate);
		this.updateEnergyTag();
		return energy;
	}

	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
		int energy = super.extractEnergy(maxExtract, simulate);
		this.updateEnergyTag();
		return energy;
	}

	@Override
	public void deserializeNBT(Tag nbt) {
		super.deserializeNBT(nbt);
		this.updateEnergyTag();
	}

	public void updateEnergyTag() {
		if(this.self.getTag() == null && this.energy == 0) {
			return;
		}
		CompoundTag nbt = this.self.getOrCreateTag();
		nbt.putInt(ForgeEventHandler.ENERGY.toString(), this.energy);
		this.self.setTag(nbt);
	}
}
