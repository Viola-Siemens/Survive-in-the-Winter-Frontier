package com.hexagram2021.misc_twf.common.item;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public abstract class AccumulatorItem extends Item implements IEnergyItem {
	protected AccumulatorItem(Properties props) {
		super(props);
	}

	@Override
	public int getMaxEnergyReceiveSpeed() {
		return 10;
	}

	@Override
	public int getMaxEnergyExtractSpeed() {
		return 1;
	}

	@Override
	public CompoundTag getShareTag(ItemStack stack) {
		CompoundTag nbt = stack.getTag();
		nbt = this.getEnergyShareTag(nbt == null ? new CompoundTag() : nbt.copy(), stack);
		return nbt;
	}

	@Override
	public void readShareTag(ItemStack stack, @Nullable CompoundTag nbt) {
		super.readShareTag(stack, nbt);

		if (nbt != null) {
			this.readEnergyShareTag(nbt, stack);
		}
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
