package com.hexagram2021.misc_twf.common.register;

import com.hexagram2021.misc_twf.common.block.properties.MoldWorkbenchPart;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public final class MISCTWFBlockStateProperties {
	public static final EnumProperty<MoldWorkbenchPart> MOLD_WORKBENCH_PART = EnumProperty.create("part", MoldWorkbenchPart.class);

	private MISCTWFBlockStateProperties() {
	}

	public static void init() {
	}
}
