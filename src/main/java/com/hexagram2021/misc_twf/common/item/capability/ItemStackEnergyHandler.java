package com.hexagram2021.misc_twf.common.item.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.IntTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemStackEnergyHandler implements ICapabilityProvider, INBTSerializable<IntTag> {
	private final TaggedEnergyStorage energyStorage;
	private final LazyOptional<IEnergyStorage> holder;

	public ItemStackEnergyHandler(ItemStack itemStack, int capability, int receiveSpeed, int extractSpeed) {
		this.energyStorage = new TaggedEnergyStorage(itemStack, capability, receiveSpeed, extractSpeed);
		this.holder = LazyOptional.of(() -> this.energyStorage);
	}

	@Override @NotNull
	public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
		return CapabilityEnergy.ENERGY.orEmpty(cap, this.holder);
	}

	@Override
	public IntTag serializeNBT() {
		return IntTag.valueOf(this.energyStorage.getEnergyStored());
	}

	@Override
	public void deserializeNBT(IntTag nbt) {
		this.energyStorage.setEnergy(nbt.getAsInt());
	}
}
