package com.hexagram2021.misc_twf.common.block.properties;

import net.minecraft.util.StringRepresentable;

import javax.annotation.Nullable;

/**
 * 模具加工台多方块结构的部件枚举喵~
 * <p>
 * 模具加工台是一个 3（宽）x 2（高）的多方块结构，面朝玩家放置时，从左到右、从上到下排列如下：
 * <pre>
 *   LEFT_UP    |    UP    | RIGHT_UP
 *   LEFT_BOTTOM|  BOTTOM  | RIGHT_BOTTOM
 * </pre>
 * 其中 {@link #BOTTOM} 为主方块（持有方块实体），{@link #LEFT_BOTTOM} 也持有方块实体喵~
 *
 * @author liudongyu
 */
public enum MoldWorkbenchPart implements StringRepresentable {
	/** 左上部件喵~ */
	LEFT_UP("left_up"),
	/** 上方部件喵~ */
	UP("up"),
	/** 右上部件喵~ */
	RIGHT_UP("right_up"),
	/** 左下部件喵~ */
	LEFT_BOTTOM("left_bottom"),
	/** 下方主部件，持有方块实体喵~ */
	BOTTOM("bottom"),
	/** 右下部件喵~ */
	RIGHT_BOTTOM("right_bottom");

	private final String name;

	MoldWorkbenchPart(String name) {
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
	 * 沿水平方向移动指定步数后得到的部件喵~
	 *
	 * @param right 向右移动的步数，负数表示向左喵~
	 * @return 移动后的部件，若目标位置不存在则返回 {@code null} 喵~
	 */
	@Nullable
	public MoldWorkbenchPart moveHorizontal(int right) {
		return switch (right) {
			case -2 -> switch (this) {
				case RIGHT_BOTTOM -> LEFT_BOTTOM;
				case RIGHT_UP -> LEFT_UP;
				default -> null;
			};
			case -1 -> switch (this) {
				case RIGHT_BOTTOM -> BOTTOM;
				case RIGHT_UP -> UP;
				case BOTTOM -> LEFT_BOTTOM;
				case UP -> LEFT_UP;
				default -> null;
			};
			case 0 -> this;
			case 1 -> switch (this) {
				case LEFT_BOTTOM -> BOTTOM;
				case LEFT_UP -> UP;
				case BOTTOM -> RIGHT_BOTTOM;
				case UP -> RIGHT_UP;
				default -> null;
			};
			case 2 -> switch (this) {
				case LEFT_BOTTOM -> RIGHT_BOTTOM;
				case LEFT_UP -> RIGHT_UP;
				default -> null;
			};
			default -> null;
		};
	}
	/**
	 * 沿垂直方向移动指定步数后得到的部件喵~
	 *
	 * @param up 向上移动的步数，负数表示向下喵~
	 * @return 移动后的部件，若目标位置不存在则返回 {@code null} 喵~
	 */
	@Nullable
	public MoldWorkbenchPart moveVertical(int up) {
		return switch (up) {
			case -1 -> switch (this) {
				case LEFT_UP -> LEFT_BOTTOM;
				case UP -> BOTTOM;
				case RIGHT_UP -> RIGHT_BOTTOM;
				default -> null;
			};
			case 0 -> this;
			case 1 -> switch (this) {
				case LEFT_BOTTOM -> LEFT_UP;
				case BOTTOM -> UP;
				case RIGHT_BOTTOM -> RIGHT_UP;
				default -> null;
			};
			default -> null;
		};
	}

	/**
	 * 判断当前部件是否可以在给定的下方部件之上存活喵~
	 * <p>
	 * 上层部件（LEFT_UP、UP、RIGHT_UP）需要对应的下层部件支撑，下层部件可以独立存在喵~
	 *
	 * @param below 正下方方块的部件类型，若下方不是模具加工台则为 {@code null} 喵~
	 * @return 当前部件是否可以存活喵~
	 */
	public boolean canSurviveOn(@Nullable MoldWorkbenchPart below) {
		return switch (this) {
			case UP -> below == BOTTOM;
			case LEFT_UP -> below == LEFT_BOTTOM;
			case RIGHT_UP -> below == RIGHT_BOTTOM;
			default -> true;
		};
	}
}
