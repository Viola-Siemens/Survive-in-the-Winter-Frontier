package com.hexagram2021.misc_twf.common.register;

import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

/**
 * 本模组自定义方块状态属性的注册表喵~
 * <p>
 * 包含武装状态与弹孔数量等通用方块状态属性，
 * 以及武装状态（{@link #ARMED}）和弹孔数量（{@link #HOLES}）等通用属性喵~
 *
 * @author liudongyu
 */
public final class MISCTWFBlockStateProperties {
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
