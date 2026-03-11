package com.hexagram2021.misc_twf.common.item;

import net.minecraft.world.item.Item;

/**
 * 蓄电池物品的抽象基类，提供默认的充放电速度实现喵~
 *
 * @author liudongyu
 */
public abstract class AccumulatorItem extends Item implements IEnergyItem {
	/**
	 * 构造一个蓄电池物品喵~
	 *
	 * @param props 物品属性喵~
	 */
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
}
