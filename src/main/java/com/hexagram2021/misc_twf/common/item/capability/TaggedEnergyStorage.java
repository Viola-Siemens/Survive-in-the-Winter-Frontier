package com.hexagram2021.misc_twf.common.item.capability;

import com.hexagram2021.misc_twf.common.ForgeEventHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.energy.IEnergyStorage;

public class TaggedEnergyStorage implements IEnergyStorage {
	private final ItemStack self;
	protected int energy = 0;
	protected final int capacity;
	protected final int maxReceive;
	protected final int maxExtract;

	public TaggedEnergyStorage(ItemStack itemStack, int capacity, int maxReceive, int maxExtract) {
		this.capacity = capacity;
		this.maxReceive = maxReceive;
		this.maxExtract = maxExtract;
		this.self = itemStack;
	}

	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		if (!this.canReceive()) {
			return 0;
		}

		int energyReceived = Math.min(this.capacity - this.energy, Math.min(this.maxReceive, maxReceive));
		if (!simulate) {
			this.energy += energyReceived;
		}
		this.updateEnergyTag();
		return energyReceived;
	}

	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
		if (!this.canExtract()) {
			return 0;
		}

		int energyExtracted = Math.min(this.energy, Math.min(this.maxExtract, maxExtract));
		if (!simulate) {
			this.energy -= energyExtracted;
		}
		this.updateEnergyTag();
		return energyExtracted;
	}

	@Override
	public int getEnergyStored() {
		return this.energy;
	}

	@Override
	public int getMaxEnergyStored() {
		return this.capacity;
	}

	@Override
	public boolean canExtract() {
		return this.maxExtract > 0;
	}

	@Override
	public boolean canReceive() {
		return this.maxReceive > 0;
	}

	public void setEnergy(int energy) {
		this.energy = energy;
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
