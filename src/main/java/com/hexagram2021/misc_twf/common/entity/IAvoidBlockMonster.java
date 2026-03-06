package com.hexagram2021.misc_twf.common.entity;

import com.hexagram2021.misc_twf.common.entity.goal.AvoidBlockGoal;

/**
 * 避开特定方块的怪物接口喵~
 * <p>
 * 该接口为怪物实体提供避开特定方块（如紫外线灯）的能力，使怪物会主动远离这些方块喵~
 * </p>
 *
 * @see com.hexagram2021.misc_twf.mixin.vanilla.entities.MonsterMixin
 * @see AvoidBlockGoal
 * @author liudongyu
 */
@SuppressWarnings("java:S100")
public interface IAvoidBlockMonster {
	/**
	 * 获取怪物的避开方块目标（AI Goal）喵~
	 *
	 * @return 避开方块的 AI 目标对象喵~
	 */
	AvoidBlockGoal<?> misc_twf$getAvoidBlockGoal();
}
