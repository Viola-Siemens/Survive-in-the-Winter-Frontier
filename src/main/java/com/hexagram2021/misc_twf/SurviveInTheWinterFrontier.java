package com.hexagram2021.misc_twf;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;

/**
 * 模组主类，负责模组的初始化和生命周期管理喵~
 *
 * @author liudongyu
 */
@Mod(SurviveInTheWinterFrontier.MODID)
public class SurviveInTheWinterFrontier {
	/** 模组 ID 喵~ */
	public static final String MODID = "misc_twf";

	/**
	 * 模组构造方法，注册内容、配置和事件监听喵~
	 *
	 * @param modBus       模组事件总线喵~
	 * @param modContainer 模组容器喵~
	 */
	public SurviveInTheWinterFrontier(IEventBus modBus, ModContainer modContainer) {
		modBus.addListener(this::setup);
		NeoForge.EVENT_BUS.addListener(this::serverStarted);
		NeoForge.EVENT_BUS.register(this);
	}
}
