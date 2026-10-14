package com.hexagram2021.misc_twf_hearing.event;

import com.hexagram2021.misc_twf_hearing.MiscTwfHearing;
import com.hexagram2021.misc_twf_hearing.core.NoiseManager;
import com.hexagram2021.misc_twf_hearing.debug.HearingDebugState;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

/**
 * 噪声生命周期处理器喵~：负责周期清理与维度/服务器卸载时的释放。
 *
 * <p>严格遵守"不持久化"原则：维度卸载或服务器停止后，全部噪声立即消失喵~</p>
 *
 * @author liudongyu
 */
@EventBusSubscriber(modid = MiscTwfHearing.MODID)
public final class NoiseLifecycleHandler {
	/**
	 * 服务端 tick 结束后按间隔清理过期噪声与执行容量保护喵~
	 *
	 * @param event 服务端 tick 事件喵~
	 */
	@SubscribeEvent
	public static void onServerTick(ServerTickEvent.Post event) {
		for(NoiseManager manager : NoiseManager.all()) {
			manager.cleanup();
		}
	}

	/**
	 * 维度卸载时释放该维度的噪声管理器喵~
	 *
	 * @param event 维度事件喵~
	 */
	@SubscribeEvent
	public static void onLevelUnload(LevelEvent.Unload event) {
		if(event.getLevel() instanceof ServerLevel serverLevel) {
			NoiseManager.release(serverLevel);
		}
	}

	/**
	 * 服务器停止时释放所有噪声管理器喵~
	 *
	 * @param event 服务器停止事件喵~
	 */
	@SubscribeEvent
	public static void onServerStopped(ServerStoppedEvent event) {
		NoiseManager.releaseAll();
		HearingDebugState.clear();
	}

	private NoiseLifecycleHandler() {
	}
}
