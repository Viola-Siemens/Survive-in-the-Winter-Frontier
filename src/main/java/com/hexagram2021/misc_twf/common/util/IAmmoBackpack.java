package com.hexagram2021.misc_twf.common.util;

import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.items.ItemStackHandler;

public interface IAmmoBackpack {
	boolean canStoreAmmo();

	ItemStackHandler getAmmoHandler();

	void saveAmmo(CompoundTag compound);
}
