package com.hexagram2021.misc_twf.common.register;

import com.hexagram2021.misc_twf.common.block.properties.MoldWorkbenchPart;
import com.hexagram2021.misc_twf.common.block.properties.RecoveryFurnacePart;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

/**
 * 本模组自定义方块状态属性的注册表喵~
 * <p>
 * 包含模具加工台（{@link MoldWorkbenchPart}）、回收炉（{@link RecoveryFurnacePart}）等多方块结构的部件属性，
 * 以及武装状态（{@link #ARMED}）和弹孔数量（{@link #HOLES}）等通用属性喵~
 *
 * @author liudongyu
 */
public final class MISCTWFBlockStateProperties {
	/** 模具加工台部件属性，标识当前方块在 3x2 多方块结构中的位置喵~ */
	public static final EnumProperty<MoldWorkbenchPart> MOLD_WORKBENCH_PART = EnumProperty.create("part", MoldWorkbenchPart.class);
	/** 回收炉部件属性，标识当前方块在 L 形多方块结构中的位置喵~ */
	public static final EnumProperty<RecoveryFurnacePart> RECOVERY_FURNACE_PART = EnumProperty.create("part", RecoveryFurnacePart.class);
	/** 武装状态属性，标识方块是否已安装动力臂等附加组件喵~ */
	public static final BooleanProperty ARMED = BooleanProperty.create("armed");
	/** 弹孔数量属性，取值范围为 1~3 喵~ */
	public static final IntegerProperty HOLES = IntegerProperty.create("holes", 1, 3);

	private MISCTWFBlockStateProperties() {
	}

	/** 空方法，用于触发类加载以完成静态字段初始化喵~ */
	public static void init() {
		// Lazy init
	}
}
