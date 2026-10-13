package com.hexagram2021.misc_twf_hearing;

import com.hexagram2021.misc_twf_hearing.common.config.MISCTWFHearingConfig;
import com.hexagram2021.misc_twf_hearing.common.register.MISCTWFHearingAttachments;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;

/**
 * 循声模块主类喵~
 *
 * <p>模块 modid 为 {@code misc_twf_hearing}，对外作为独立 Mod 安装；内容命名空间沿用 {@code misc_twf}
 * （决策 D6），本模块 P0 不注册任何物品/方块/实体/音效，只提供噪声索引、循声 Goal 与公开 API喵~</p>
 *
 * <p>职责边界：只告诉怪物"哪里响了"，绝不接管 Target、视觉、寻路与攻击（见 docs/MODULARIZATION.md 第 11 章）喵~</p>
 *
 * @author liudongyu
 */
@Mod(MiscTwfHearing.MODID)
public class MiscTwfHearing {
	/** 模块 mod id（对外身份）喵~ */
	public static final String MODID = "misc_twf_hearing";
	/** 内容命名空间（噪声类型 id 等统一使用的命名空间）喵~ */
	public static final String CONTENT_NAMESPACE = "misc_twf";

	/**
	 * 模块构造方法喵~
	 *
	 * @param modBus       模块事件总线喵~
	 * @param modContainer 模块容器喵~
	 */
	public MiscTwfHearing(IEventBus modBus, ModContainer modContainer) {
		// 注册"循声 Goal 已注入"的非持久化 Attachment 喵~
		MISCTWFHearingAttachments.init(modBus);
		// 循声参数全部为服务端权威数据，故注册为 SERVER 配置喵~
		modContainer.registerConfig(ModConfig.Type.SERVER, MISCTWFHearingConfig.getConfig());
	}
}
