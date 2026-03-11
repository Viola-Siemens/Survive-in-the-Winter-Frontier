package com.hexagram2021.misc_twf.common.block.properties;

import net.minecraft.util.StringRepresentable;

import javax.annotation.Nullable;

/**
 * 回收炉多方块结构的部件枚举喵~
 * <p>
 * 回收炉是一个 L 形多方块结构，由三个方块组成：
 * <pre>
 *     TOP
 *   BOTTOM  FRONT
 * </pre>
 * 其中 {@link #BOTTOM} 为主方块（持有方块实体），{@link #TOP} 位于其正上方，
 * {@link #FRONT} 位于朝向方向的前方同层位置喵~
 *
 * @author liudongyu
 */
public enum RecoveryFurnacePart implements StringRepresentable {
	/** 顶部部件，位于主方块正上方喵~ */
	TOP("top"),
	/** 底部主部件，持有方块实体喵~ */
	BOTTOM("bottom"),
	/** 前方部件，位于主方块朝向方向的同层位置喵~ */
	FRONT("front");

	private final String name;

	RecoveryFurnacePart(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return this.name;
	}

	@Override
	public String getSerializedName() {
		return this.name;
	}

	/**
	 * 沿水平方向（朝向轴）移动指定步数后得到的部件喵~
	 *
	 * @param front 向前移动的步数，负数表示向后喵~
	 * @return 移动后的部件，若目标位置不存在则返回 {@code null} 喵~
	 */
	@Nullable
	public RecoveryFurnacePart moveHorizontal(int front) {
		if(front == -1) {
			return this == FRONT ? BOTTOM : null;
		}
		if(front == 1) {
			return this == BOTTOM ? FRONT : null;
		}
		if(front == 0) {
			return this;
		}
		return null;
	}
	/**
	 * 沿垂直方向移动指定步数后得到的部件喵~
	 *
	 * @param up 向上移动的步数，负数表示向下喵~
	 * @return 移动后的部件，若目标位置不存在则返回 {@code null} 喵~
	 */
	@Nullable
	public RecoveryFurnacePart moveVertical(int up) {
		if(up == -1) {
			return this == TOP ? BOTTOM : null;
		}
		if(up == 1) {
			return this == BOTTOM ? TOP : null;
		}
		if(up == 0) {
			return this;
		}
		return null;
	}

	/**
	 * 判断当前部件是否可以在给定的下方部件之上存活喵~
	 * <p>
	 * 只有 {@link #TOP} 需要 {@link #BOTTOM} 在正下方支撑，其余部件可以独立存在喵~
	 *
	 * @param below 正下方方块的部件类型，若下方不是回收炉则为 {@code null} 喵~
	 * @return 当前部件是否可以存活喵~
	 */
	public boolean canSurviveOn(@Nullable RecoveryFurnacePart below) {
		return this != TOP || below == BOTTOM;
	}
}
