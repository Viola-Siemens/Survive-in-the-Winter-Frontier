package com.hexagram2021.misc_twf_wildlife;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

/**
 * 农牧生态模块主类（阶段 0 工程骨架占位）喵~
 *
 * <p>模块 modid 为 {@code misc_twf_wildlife}；内容命名空间沿用 {@code misc_twf}（决策 D6）。
 * 内容域与迁移规划详见 {@code docs/MODULARIZATION.md} 5.2 / 8，业务代码按阶段 1 迁入喵~</p>
 *
 * @author liudongyu
 */
@Mod(MiscTwfWildlife.MODID)
public class MiscTwfWildlife {
	/** 模块 mod id（内容命名空间仍为 misc_twf）喵~ */
	public static final String MODID = "misc_twf_wildlife";

	/**
	 * 模块构造方法（骨架阶段仅预留接线点）喵~
	 *
	 * @param modBus       模块事件总线喵~
	 * @param modContainer 模块容器喵~
	 */
	public MiscTwfWildlife(IEventBus modBus, ModContainer modContainer) {
		// TODO 阶段 1：迁入农牧生态业务代码（动物尸体/粪便/产奶冷却/冬小麦与食物数据/随域微调）喵~
	}
}
