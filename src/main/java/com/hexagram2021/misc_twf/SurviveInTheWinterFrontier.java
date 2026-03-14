package com.hexagram2021.misc_twf;

import com.hexagram2021.misc_twf.common.MISCTWFContent;
import com.hexagram2021.misc_twf.common.ModVanillaCompat;
import com.hexagram2021.misc_twf.common.config.MISCTWFCommonConfig;
import com.hexagram2021.misc_twf.common.register.MISCTWFRecipeBookTypes;
import com.hexagram2021.misc_twf.server.MISCTWFSavedData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartedEvent;

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
		MISCTWFContent.modConstruct(modBus);

		modContainer.registerConfig(ModConfig.Type.COMMON, MISCTWFCommonConfig.getConfig());

		modBus.addListener(this::setup);
		NeoForge.EVENT_BUS.addListener(this::serverStarted);
	}

	/**
	 * 通用配置阶段回调，初始化原版兼容层和配方书类型喵~
	 *
	 * @param event 通用配置事件喵~
	 */
	private void setup(final FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
			ModVanillaCompat.init();
			MISCTWFRecipeBookTypes.init();
		});
	}

	/**
	 * 服务端启动完成后的回调，初始化存档数据喵~
	 *
	 * @param event 服务端启动完成事件喵~
	 */
	public void serverStarted(final ServerStartedEvent event) {
		ServerLevel world = event.getServer().getLevel(Level.OVERWORLD);
		event.getServer().levelKeys().forEach(level -> MISCTWFSavedData.dimensions.add(level.location()));
		assert world != null;
		if(!world.isClientSide) {
			MISCTWFSavedData worldData = world.getDataStorage().computeIfAbsent(
					new SavedData.Factory<>(MISCTWFSavedData::new, MISCTWFSavedData::new),
					MISCTWFSavedData.SAVED_DATA_NAME
			);
			MISCTWFSavedData.setInstance(worldData);
		}
	}
}
