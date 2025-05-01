package com.hexagram2021.misc_twf.common.register;

import com.hexagram2021.misc_twf.common.block.properties.MoldWorkbenchPart;
import com.hexagram2021.misc_twf.common.block.properties.RecoveryFurnacePart;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public final class MISCTWFBlockStateProperties {
	public static final EnumProperty<MoldWorkbenchPart> MOLD_WORKBENCH_PART = EnumProperty.create("part", MoldWorkbenchPart.class);
	public static final EnumProperty<RecoveryFurnacePart> RECOVERY_FURNACE_PART = EnumProperty.create("part", RecoveryFurnacePart.class);

	private MISCTWFBlockStateProperties() {
	}

	public static void init() {
	}
}
