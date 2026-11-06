package com.hexagram2021.misc_twf_adventure;

import com.hexagram2021.misc_twf_adventure.common.MISCTWFContent;
import com.hexagram2021.misc_twf_adventure.common.ModVanillaCompat;
import com.hexagram2021.misc_twf_adventure.common.config.MISCTWFCommonConfig;
import com.hexagram2021.misc_twf_adventure.common.register.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

/**
 * 冒险与探索模块主类喵~
 *
 * <p>模块 modid 为 {@code misc_twf_adventure}；内容命名空间沿用 {@code misc_twf}（决策 D6），
 * 因此注册 id、数据文件与资源路径均保持 {@code misc_twf:*} 不变喵~</p>
 *
 * <p>本类承担原单体主类 {@code SurviveInTheWinterFrontier} 的接线职责：注册全部 DeferredRegister、
 * 内容事件入口（酿造/属性/网络）以及与原版的通用兼容初始化喵~</p>
 *
 * @author liudongyu
 */
@Mod(MiscTwfAdventure.MODID)
public class MiscTwfAdventure {
	/** 模块 mod id（对外身份）喵~ */
	public static final String MODID = "misc_twf_adventure";
	/** 内容命名空间（注册 id 与资源统一使用的命名空间）喵~ */
	public static final String CONTENT_NAMESPACE = "misc_twf";

	/**
	 * 模块构造方法喵~
	 *
	 * @param modBus       模块事件总线喵~
	 * @param modContainer 模块容器喵~
	 */
	public MiscTwfAdventure(IEventBus modBus, ModContainer modContainer) {
		MISCTWFItems.init(modBus);
		MISCTWFFeatures.init(modBus);
		MISCTWFFluids.init(modBus);
		MISCTWFBlocks.init(modBus);
		MISCTWFBlockEntities.init(modBus);
		MISCTWFMenuTypes.init(modBus);
		MISCTWFRecipeTypes.init(modBus);
		MISCTWFRecipeSerializers.init(modBus);
		MISCTWFDataComponentTypes.init(modBus);
		MISCTWFCreativeModeTabs.init(modBus);
		MISCTWFAttributes.init(modBus);
		MISCTWFStructureTypes.init(modBus);
		MISCTWFTravelersBackpackTacOps.init(modBus);
		MISCTWFSkills.init(modBus);
		// 怪物蛋概率/听声参数与技能等级需求等玩法数值统一放在 SERVER 配置喵~
		modContainer.registerConfig(ModConfig.Type.SERVER, MISCTWFCommonConfig.getConfig());
		// 酿造配方、玩家属性修改与网络包处理器都挂在模块事件总线的静态处理器上喵~
		modBus.register(MISCTWFContent.class);
		modBus.addListener(this::setup);
		// TODO Phase B：MISCTWFCommonConfig 与 MISCTWFBlocks 在迁移前的根工程中已缺失（引用仍在），
		//  需按 1.18.2 源码/需求补齐后在此注册配置与方块，并补上创造页/方块物品的接线喵~
	}

	/**
	 * 通用配置阶段回调，注册与原版的兼容行为喵~
	 *
	 * @param event 通用配置事件喵~
	 */
	private void setup(final FMLCommonSetupEvent event) {
		event.enqueueWork(ModVanillaCompat::init);
	}
}