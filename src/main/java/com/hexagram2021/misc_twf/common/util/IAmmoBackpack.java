package com.hexagram2021.misc_twf.common.util;

import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.items.ItemStackHandler;

/**
 * 弹药背包接口，为旅行背包提供 TAC 弹药存储能力喵~
 * 通过 Mixin 注入到旅行背包容器中，使其支持弹药物品的存取和持久化喵~
 *
 * @author liudongyu
 */
public interface IAmmoBackpack {
	/**
	 * 判断该背包是否已升级为支持弹药存储喵~
	 *
	 * @return 如果已升级则返回 true 喵~
	 */
	boolean canStoreAmmo();

	/**
	 * 获取弹药物品栏处理器喵~
	 *
	 * @return 弹药槽的 ItemStackHandler 喵~
	 */
	ItemStackHandler getAmmoHandler();

	/**
	 * 将弹药槽数据保存到 NBT 标签中喵~
	 *
	 * @param compound 要写入的 NBT 复合标签喵~
	 */
	void saveAmmo(CompoundTag compound);
}
