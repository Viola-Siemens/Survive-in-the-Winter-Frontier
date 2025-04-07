package com.hexagram2021.misc_twf.common.block.properties;

import net.minecraft.util.StringRepresentable;

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
}
