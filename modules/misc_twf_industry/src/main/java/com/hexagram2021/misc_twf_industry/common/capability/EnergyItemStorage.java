package com.hexagram2021.misc_twf_industry.common.capability;

import com.hexagram2021.misc_twf_industry.common.item.IEnergyItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.neoforge.energy.IEnergyStorage;

/**
 * 物品 EnergyStorage 实现类
 *
 * @author liudongyu
 */
public record EnergyItemStorage(ItemStack stack, IEnergyItem item) implements IEnergyStorage {
	private static final String ENERGY_KEY = "energy";

	@Override
	public int receiveEnergy(int toReceive, boolean simulate) {
		int stored = this.getEnergyStored();
		int received = Math.min(toReceive, Math.min(this.item.getMaxEnergyReceiveSpeed(), this.item.getEnergyCapability() - stored));
		if(received <= 0) {
			return 0;
		}
		if(!simulate) {
			this.setEnergy(stored + received);
		}
		return received;
	}

	@Override
	public int extractEnergy(int toExtract, boolean simulate) {
		int stored = this.getEnergyStored();
		int extracted = Math.min(toExtract, Math.min(this.item.getMaxEnergyExtractSpeed(), stored));
		if(extracted <= 0) {
			return 0;
		}
		if(!simulate) {
			this.setEnergy(stored - extracted);
		}
		return extracted;
	}

	@Override
	public int getEnergyStored() {
		CustomData data = this.stack.get(DataComponents.CUSTOM_DATA);
		if (data == null) {
			return 0;
		}
		return data.copyTag().getInt(ENERGY_KEY);
	}

	@Override
	public int getMaxEnergyStored() {
		return this.item.getEnergyCapability();
	}

	@Override
	public boolean canExtract() {
		return true;
	}

	@Override
	public boolean canReceive() {
		return true;
	}

	/**
	 * 修改当前存储能量
	 * @param energy 能量值
	 */
	public void setEnergy(int energy) {
		CompoundTag tag = this.stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
		if (energy <= 0) {
			tag.remove(ENERGY_KEY);
			if (tag.isEmpty()) {
				this.stack.remove(DataComponents.CUSTOM_DATA);
			} else {
				this.stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
			}
		} else {
			tag.putInt(ENERGY_KEY, Math.min(energy, this.item.getEnergyCapability()));
			this.stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
		}
	}
}
