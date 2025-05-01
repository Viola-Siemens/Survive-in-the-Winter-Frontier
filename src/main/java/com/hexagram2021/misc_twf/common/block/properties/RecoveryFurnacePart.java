package com.hexagram2021.misc_twf.common.block.properties;

import net.minecraft.util.StringRepresentable;

import javax.annotation.Nullable;

public enum RecoveryFurnacePart implements StringRepresentable {
	TOP("top"),
	BOTTOM("bottom"),		//main
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

	public boolean canSurviveOn(@Nullable RecoveryFurnacePart below) {
		return this != TOP || below == BOTTOM;
	}
}
