package com.hexagram2021.misc_twf.common.util;

import com.hexagram2021.misc_twf.common.register.MISCTWFItems;
import net.minecraft.world.item.ItemStack;

public final class EnergyUtils {
	public static boolean canStoreEnergy(ItemStack itemStack) {
		return itemStack.is(MISCTWFItems.NIGHT_VISION_DEVICE.get());
	}

	private EnergyUtils() {
	}
}
