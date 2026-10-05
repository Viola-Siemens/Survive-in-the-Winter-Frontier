package com.hexagram2021.misc_twf_wildlife.common;

import com.hexagram2021.misc_twf_wildlife.common.register.*;
import net.neoforged.bus.api.IEventBus;

/**
 * 农牧生态模块内容注册与初始化管理类喵~
 *
 * <p>按依赖顺序初始化模块内注册器：标签 → 创造页 → 方块 → 方块实体 → 物品 → 附件 → 数据组件喵~</p>
 *
 * @author liudongyu
 */
public final class MISCTWFWildlifeContent {
	/**
	 * 模块构造阶段的主入口方法喵~
	 *
	 * @param bus 模块事件总线喵~
	 */
	public static void modConstruct(IEventBus bus) {
		MISCTWFEntityTags.init();

		MISCTWFCreativeModeTabs.init(bus);
		MISCTWFBlocks.init(bus);
		MISCTWFBlockEntities.init(bus);
		MISCTWFItems.init(bus);
		MISCTWFAttachmentTypes.init(bus);
		MISCTWFDataComponentTypes.init(bus);
	}

	private MISCTWFWildlifeContent() {
	}
}
