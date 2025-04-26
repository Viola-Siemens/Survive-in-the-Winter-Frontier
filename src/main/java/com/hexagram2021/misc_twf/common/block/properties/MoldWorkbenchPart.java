package com.hexagram2021.misc_twf.common.block.properties;

import net.minecraft.util.StringRepresentable;

import javax.annotation.Nullable;

public enum MoldWorkbenchPart implements StringRepresentable {
	LEFT_UP("left_up"),
	UP("up"),
	RIGHT_UP("right_up"),
	LEFT_BOTTOM("left_bottom"),
	BOTTOM("bottom"),		//main
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

	public boolean canSurviveOn(@Nullable MoldWorkbenchPart below) {
		return switch (this) {
			case UP -> below == BOTTOM;
			case LEFT_UP -> below == LEFT_BOTTOM;
			case RIGHT_UP -> below == RIGHT_BOTTOM;
			default -> true;
		};
	}
}
