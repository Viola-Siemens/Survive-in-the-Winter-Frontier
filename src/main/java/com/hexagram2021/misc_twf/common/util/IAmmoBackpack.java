package com.hexagram2021.misc_twf.common.util;

import net.minecraftforge.items.ItemStackHandler;

public interface IAmmoBackpack {
	boolean canStoreAmmo();

	ItemStackHandler getAmmoHandler();
}
