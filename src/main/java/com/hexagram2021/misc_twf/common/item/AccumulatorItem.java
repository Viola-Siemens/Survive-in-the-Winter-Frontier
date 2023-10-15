package com.hexagram2021.misc_twf.common.item;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

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

	@Override
	public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> list) {
		if (this.allowdedIn(tab)) {
			ItemStack itemStack = new ItemStack(this);
			this.readShareTag(itemStack, this.getMaxEnergyTag());
			list.add(itemStack);
		}
	}
}
