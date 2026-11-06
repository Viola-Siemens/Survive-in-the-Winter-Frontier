package com.hexagram2021.misc_twf_adventure.event;

import com.hexagram2021.misc_twf_adventure.MiscTwfAdventure;
import com.hexagram2021.misc_twf_adventure.common.block.entity.MonsterEggBlockEntity;
import com.hexagram2021.misc_twf_hearing.api.NoiseEmitEvent;
import com.hexagram2021.misc_twf_hearing.api.NoiseSnapshot;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

/**
 * 怪物蛋的循声事件处理器喵~
 *
 * <p>替代原先对 Sona `EXPOSURE` 效果的轮询：由循声模块的 {@link NoiseEmitEvent.Post} 事件驱动，
 * 把噪声位置与强度写入附近的怪物蛋方块实体，后续破壳概率与动画仍由方块实体自身 tick 处理喵~</p>
 *
 * @author liudongyu
 */
@EventBusSubscriber(modid = MiscTwfAdventure.MODID)
public final class MonsterEggHearingHandler {
	/**
	 * 噪声写入索引后通知附近的怪物蛋喵~
	 *
	 * @param event 噪声写入后事件喵~
	 */
	@SubscribeEvent
	public static void onNoiseEmitted(NoiseEmitEvent.Post event) {
		NoiseSnapshot snapshot = event.getSnapshot();
		MonsterEggBlockEntity.onNoiseHeard(event.getLevel(), snapshot.position(), snapshot.strength());
	}

	private MonsterEggHearingHandler() {
	}
}