package com.hexagram2021.misc_twf.common.entity;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.goat.Goat;

/**
 * 产奶动物接口，为牛和山羊等动物提供挤奶冷却时间管理功能喵~
 * <p>
 * 该接口定义了挤奶冷却时间的数据访问器和相关操作方法，防止玩家无限频繁地挤奶喵~
 * </p>
 *
 * @see com.hexagram2021.misc_twf.mixin.vanilla.entities.CowEntityMixin
 * @see com.hexagram2021.misc_twf.mixin.vanilla.entities.GoatEntityMixin
 * @author liudongyu
 */
public interface IProduceMilk {
	/**
	 * 获取当前的挤奶冷却时间（单位：游戏刻）喵~
	 *
	 * @return 剩余冷却时间，0 表示可以挤奶喵~
	 */
	int misc_twf$getMilkCoolDown();

	/**
	 * 设置挤奶冷却时间喵~
	 *
	 * @param delay 冷却时间（单位：游戏刻）喵~
	 */
	void misc_twf$setMilkCoolDown(int delay);

	/**
	 * 检查动物是否可以被挤奶喵~
	 *
	 * @return 如果冷却时间已结束（≤0），返回 true，否则返回 false 喵~
	 */
	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	default boolean misc_twf$isAvailableToProduceMilk() {
		return this.misc_twf$getMilkCoolDown() <= 0;
	}

	@SuppressWarnings({"NotNullFieldNotInitialized", "java:S1104", "java:S1444", "java:S3008"})
	final class DataAccessors {

		/**
		 * 牛的挤奶冷却时间数据访问器喵~
		 */
		public static EntityDataAccessor<Integer> DATA_COW_MILK_COOL_DOWN;
		/**
		 * 山羊的挤奶冷却时间数据访问器喵~
		 */
		public static EntityDataAccessor<Integer> DATA_GOAT_MILK_COOL_DOWN;

		private DataAccessors() {
		}

		static {
			// bootstrap
			try {
				Class.forName(Cow.class.getName());
				Class.forName(Goat.class.getName());
			} catch (ClassNotFoundException e) {
				throw new ExceptionInInitializerError(e);
			}
		}
	}
}
