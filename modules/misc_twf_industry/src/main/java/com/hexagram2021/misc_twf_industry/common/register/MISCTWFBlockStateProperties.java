package com.hexagram2021.misc_twf_industry.common.register;

import com.hexagram2021.misc_twf_industry.common.block.properties.MoldWorkbenchPart;
import com.hexagram2021.misc_twf_industry.common.block.properties.RecoveryFurnacePart;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;

/**
 * 工业生态模块自定义方块状态属性喵~
 *
 * @author liudongyu
 */
public final class MISCTWFBlockStateProperties {
	/** 模具加工台部件属性，标识当前方块在 3x2 多方块结构中的位置喵~ */
	public static final EnumProperty<MoldWorkbenchPart> MOLD_WORKBENCH_PART = EnumProperty.create("part", MoldWorkbenchPart.class);
	/** 武装状态属性，标识方块是否已安装动力臂等附加组件喵~ */
	public static final BooleanProperty ARMED = BooleanProperty.create("armed");
	/** 回收炉部件属性，标识当前方块在 L 形多方块结构中的位置喵~ */
	public static final EnumProperty<RecoveryFurnacePart> RECOVERY_FURNACE_PART = EnumProperty.create("part", RecoveryFurnacePart.class);

	private MISCTWFBlockStateProperties() {
	}

	/** 空方法，用于触发类加载以完成静态字段初始化喵~ */
	public static void init() {
		// Lazy init
	}
}
