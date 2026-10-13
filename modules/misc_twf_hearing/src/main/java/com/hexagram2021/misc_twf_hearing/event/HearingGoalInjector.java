package com.hexagram2021.misc_twf_hearing.event;

import com.hexagram2021.misc_twf_hearing.MiscTwfHearing;
import com.hexagram2021.misc_twf_hearing.ai.HearNoiseGoal;
import com.hexagram2021.misc_twf_hearing.ai.HearingProfile;
import com.hexagram2021.misc_twf_hearing.common.config.MISCTWFHearingConfig;
import com.hexagram2021.misc_twf_hearing.common.register.MISCTWFHearingAttachments;
import com.hexagram2021.misc_twf_hearing.common.register.MISCTWFHearingEntityTags;
import net.minecraft.world.entity.Mob;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

/**
 * 循声 Goal 注入器喵~：实体进入维度时按数据 Tag 决定是否授予听觉。
 *
 * <p>注入使用 {@code goalSelector.addGoal(priority, ...)}，**绝不触碰 targetSelector**；
 * 幂等性由非持久化 Attachment 保证（实体从存档载入时同样会触发本事件，因此不会漏注入）喵~</p>
 *
 * @author liudongyu
 */
@EventBusSubscriber(modid = MiscTwfHearing.MODID)
public final class HearingGoalInjector {
	/**
	 * 实体进入维度时尝试注入循声 Goal 喵~
	 *
	 * @param event 实体进入维度事件喵~
	 */
	@SubscribeEvent
	public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
		if(event.getLevel().isClientSide() || !(event.getEntity() instanceof Mob mob)) {
			return;
		}
		if(!mob.getType().is(MISCTWFHearingEntityTags.HEARING_MOBS)
				|| mob.getType().is(MISCTWFHearingEntityTags.HEARING_BLACKLIST)) {
			return;
		}
		if(Boolean.TRUE.equals(mob.getData(MISCTWFHearingAttachments.HEARING_GOAL_ATTACHED))) {
			return;
		}
		HearingProfile profile = HearingProfile.of(mob.getType());
		mob.goalSelector.addGoal(MISCTWFHearingConfig.GOAL_PRIORITY.get(), new HearNoiseGoal(mob, profile));
		mob.setData(MISCTWFHearingAttachments.HEARING_GOAL_ATTACHED, Boolean.TRUE);
	}

	private HearingGoalInjector() {
	}
}
