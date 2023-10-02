package com.hexagram2021.misc_twf.common.item;

import net.minecraft.world.item.Item;

public abstract class AccumulatorItem extends Item implements IEnergyItem {
	protected AccumulatorItem(Properties props) {
		super(props);
	}

	@Override
	public int getMaxEnergyReceiveSpeed() {
		return 2;
	}

	@Override
	public int getMaxEnergyExtractSpeed() {
		return 1;
	}
}
