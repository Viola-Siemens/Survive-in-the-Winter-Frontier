package com.hexagram2021.misc_twf_zombie_animals;

import com.hexagram2021.misc_twf_zombie_animals.common.MISCTWFZombieAnimalsContent;
import com.hexagram2021.misc_twf_zombie_animals.common.config.MISCTWFZombieAnimalsConfig;
import com.hexagram2021.misc_twf_zombie_animals.server.MISCTWFImmunitySavedData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartedEvent;

/**
 * 僵尸动物模块主类喵~
 *
 * <p>模块 modid 为 {@code misc_twf_zombie_animals}，对外作为独立 Mod 安装；
 * 内容命名空间沿用 {@code misc_twf}（决策 D6），注册 id 与资源路径不做迁移喵~</p>
 *
 * @author liudongyu
 */
@Mod(MiscTwfZombieAnimals.MODID)
public class MiscTwfZombieAnimals {
	/** 模块 mod id（对外身份）喵~ */
	public static final String MODID = "misc_twf_zombie_animals";
	/** 内容命名空间（注册 id 与资源统一使用的命名空间）喵~ */
	public static final String CONTENT_NAMESPACE = "misc_twf";

	/**
	 * 模块构造方法喵~
	 *
	 * @param modBus       模块事件总线喵~
	 * @param modContainer 模块容器喵~
	 */
	public MiscTwfZombieAnimals(IEventBus modBus, ModContainer modContainer) {
		MISCTWFZombieAnimalsContent.modConstruct(modBus);

		modContainer.registerConfig(ModConfig.Type.COMMON, MISCTWFZombieAnimalsConfig.getConfig());

		NeoForge.EVENT_BUS.addListener(this::serverStarted);
	}

	/**
	 * 服务端启动完成后加载本模块的免疫存档喵~
	 *
	 * @param event 服务端启动完成事件喵~
	 */
	private void serverStarted(final ServerStartedEvent event) {
		ServerLevel world = event.getServer().getLevel(Level.OVERWORLD);
		if(world == null || world.isClientSide) {
			return;
		}
		MISCTWFImmunitySavedData worldData = world.getDataStorage().computeIfAbsent(
				new SavedData.Factory<>(MISCTWFImmunitySavedData::new, MISCTWFImmunitySavedData::new),
				MISCTWFImmunitySavedData.SAVED_DATA_NAME
		);
		MISCTWFImmunitySavedData.setInstance(worldData);
	}
}
