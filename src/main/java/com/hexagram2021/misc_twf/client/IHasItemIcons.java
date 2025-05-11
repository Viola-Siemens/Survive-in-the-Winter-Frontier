package com.hexagram2021.misc_twf.client;

import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.function.Supplier;

public interface IHasItemIcons {
	void misc_twf$setLazyItemIconsGetter(Supplier<List<ItemStack>> itemIcons);
}
