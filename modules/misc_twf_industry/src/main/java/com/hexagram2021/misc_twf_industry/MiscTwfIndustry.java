package com.hexagram2021.misc_twf_industry;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

/**
 * 工业生态模块主类（阶段 0 工程骨架占位）喵~
 *
 * <p>模块 modid 为 {@code misc_twf_industry}；内容命名空间沿用 {@code misc_twf}（决策 D6）。
 * 内容域与迁移规划详见 {@code docs/MODULARIZATION.md} 5.3 / 8，业务代码按阶段 2 迁入喵~</p>
 *
 * @author liudongyu
 */
@Mod(MiscTwfIndustry.MODID)
public class MiscTwfIndustry {
	/** 模块 mod id（内容命名空间仍为 misc_twf）喵~ */
	public static final String MODID = "misc_twf_industry";

	/**
	 * 模块构造方法（骨架阶段仅预留接线点）喵~
	 *
	 * @param modBus       模块事件总线喵~
	 * @param modContainer 模块容器喵~
	 */
	public MiscTwfIndustry(IEventBus modBus, ModContainer modContainer) {
		// TODO 阶段 2：迁入工业生态业务代码（能源装备与紫外线灯、回收炉、弹药模具工业；灯坐标存档拆分）喵~
	}
}
