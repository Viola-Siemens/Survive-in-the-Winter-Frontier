package com.hexagram2021.misc_twf_hearing.event;

import com.hexagram2021.misc_twf_hearing.MiscTwfHearing;
import com.hexagram2021.misc_twf_hearing.api.HearingApi;
import com.hexagram2021.misc_twf_hearing.api.NoiseTypes;
import com.hexagram2021.misc_twf_hearing.common.config.MISCTWFHearingConfig;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.ExplosionEvent;

/**
 * 爆炸噪声处理器喵~：使用 NeoForge 通用 {@code ExplosionEvent.Detonate}，因此
 * 原版 TNT/苦力怕与 Superb Warfare 的手雷、火箭弹、C4、无人机等**全部爆炸物**一并覆盖，且无需 Mixin喵~
 *
 * @author liudongyu
 */
@EventBusSubscriber(modid = MiscTwfHearing.MODID)
public final class ExplosionNoiseHandler {
	/**
	 * 爆炸引爆时在爆炸中心产生一声巨响喵~
	 *
	 * @param event 爆炸引爆事件喵~
	 */
	@SubscribeEvent
	public static void onExplosionDetonate(ExplosionEvent.Detonate event) {
		if(!MISCTWFHearingConfig.ENABLE_EXPLOSION_NOISE.get()) {
			return;
		}
		if(!(event.getLevel() instanceof ServerLevel serverLevel)) {
			return;
		}
		HearingApi.emit(serverLevel, event.getExplosion().center(), NoiseTypes.EXPLOSION, null);
	}

	private ExplosionNoiseHandler() {
	}
}
