package com.hexagram2021.misc_twf.common.item;

/**
 * 能量物品接口，定义了物品能量存储和传输的基本能力喵~
 *
 * @author liudongyu
 */
public interface IEnergyItem {
	/**
	 * 获取物品的能量容量上限喵~
	 *
	 * @return 能量容量上限值喵~
	 */
	int getEnergyCapability();

	/**
	 * 获取物品每 tick 最大充能速度喵~
	 *
	 * @return 每 tick 最大充能量喵~
	 */
	int getMaxEnergyReceiveSpeed();
	/**
	 * 获取物品每 tick 最大放电速度喵~
	 *
	 * @return 每 tick 最大放电量喵~
	 */
	int getMaxEnergyExtractSpeed();
}
