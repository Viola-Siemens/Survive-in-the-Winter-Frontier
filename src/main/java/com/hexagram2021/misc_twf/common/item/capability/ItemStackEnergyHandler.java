package com.hexagram2021.misc_twf.common.item.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemStackEnergyHandler implements ICapabilityProvider, INBTSerializable<Tag> {
	private final EnergyStorage energyStorage;
	private final LazyOptional<IEnergyStorage> holder;

	public ItemStackEnergyHandler(int capability) {
		this.energyStorage = new EnergyStorage(capability);
		this.holder = LazyOptional.of(() -> this.energyStorage);
	}

	@Override @NotNull
	public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
		return CapabilityEnergy.ENERGY.orEmpty(cap, this.holder);
	}

	@Override
	public Tag serializeNBT() {
		return this.energyStorage.serializeNBT();
	}

	@Override
	public void deserializeNBT(Tag nbt) {
		this.energyStorage.deserializeNBT(nbt);
	}
}