package com.hexagram2021.misc_twf.common.util;

import com.hexagram2021.misc_twf.common.item.IEnergyItem;
import net.minecraft.world.item.ItemStack;

public final class EnergyUtils {
	public static boolean canStoreEnergy(ItemStack itemStack) {
		return itemStack.getItem() instanceof IEnergyItem;
	}

	private EnergyUtils() {
	}
}
