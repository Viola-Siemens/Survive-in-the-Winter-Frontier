package com.hexagram2021.misc_twf_adventure;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

/**
 * 冒险与探索模块主类（阶段 0 工程骨架占位）喵~
 *
 * <p>模块 modid 为 {@code misc_twf_adventure}；内容命名空间沿用 {@code misc_twf}（决策 D6）。
 * 内容域与迁移规划详见 {@code docs/MODULARIZATION.md} 5.4 / 8，业务代码按阶段 1/3 迁入喵~</p>
 *
 * @author liudongyu
 */
@Mod(MiscTwfAdventure.MODID)
public class MiscTwfAdventure {
	/** 模块 mod id（内容命名空间仍为 misc_twf）喵~ */
	public static final String MODID = "misc_twf_adventure";

	/**
	 * 模块构造方法（骨架阶段仅预留接线点）喵~
	 *
	 * @param modBus       模块事件总线喵~
	 * @param modContainer 模块容器喵~
	 */
	public MiscTwfAdventure(IEventBus modBus, ModContainer modContainer) {
		// TODO 阶段 1/3：迁入冒险业务代码（生化医疗、背包×枪械联动、巢穴与怪物蛋、末世装饰；
		//  疫苗写入免疫走 misc_twf_zombie_animals 的公开 API，见文档 6.2-E7）喵~
	}
}
