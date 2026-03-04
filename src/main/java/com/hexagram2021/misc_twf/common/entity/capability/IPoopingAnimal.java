package com.hexagram2021.misc_twf.common.entity.capability;

import com.hexagram2021.misc_twf.common.config.MISCTWFCommonConfig;
import net.minecraft.world.entity.LivingEntity;

/**
 * 动物排便能力接口，定义了动物定期排便的基本行为喵~
 * 实现该接口的实体将能够周期性地生成动物粪便物品喵~
 *
 * @author liudongyu
 */
public interface IPoopingAnimal {
	/**
	 * 获取距离下次排便的剩余时间（以刻为单位）喵~
	 *
	 * @return 剩余时间（刻）喵~
	 */
	int getPoopingRemainingTicks();

	/**
	 * 设置距离下次排便的剩余时间（以刻为单位）喵~
	 *
	 * @param ticks 剩余时间（刻）喵~
	 */
	void setPoopingRemainingTicks(int ticks);

	/**
	 * 执行排便操作，在实体周围生成动物粪便物品喵~
	 *
	 * @param self 执行排便的生物实体喵~
	 */
	void poop(LivingEntity self);

	/**
	 * 每刻更新排便计时器，时间到达时触发排便并重置计时器喵~
	 *
	 * @param self 执行更新的生物实体喵~
	 */
	default void tick(LivingEntity self) {
		int ticks = this.getPoopingRemainingTicks() - 1;
		if(ticks > 0) {
			this.setPoopingRemainingTicks(ticks);
			return;
		}
		this.poop(self);
		this.resetPoopingTicks(self);
	}

	/**
	 * 重置排便计时器为随机值，使用实体的随机数生成器增加变化性喵~
	 * 计算公式为：(基础间隔 + 随机噪声) × 20 刻喵~
	 *
	 * @param self 需要重置计时器的生物实体喵~
	 */
	default void resetPoopingTicks(LivingEntity self) {
		this.setPoopingRemainingTicks((MISCTWFCommonConfig.ANIMAL_POOPING_INTERVAL.get() + self.getRandom().nextInt(MISCTWFCommonConfig.ANIMAL_POOPING_INTERVAL_NOISE.get())) * 20);
	}
}
