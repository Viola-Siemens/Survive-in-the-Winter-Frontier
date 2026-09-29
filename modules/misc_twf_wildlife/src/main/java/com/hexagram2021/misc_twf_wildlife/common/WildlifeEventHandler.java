package com.hexagram2021.misc_twf_wildlife.common;

import com.hexagram2021.misc_twf_wildlife.common.entity.capability.PoopingAnimal;
import com.hexagram2021.misc_twf_wildlife.common.register.MISCTWFAttachmentTypes;
import com.hexagram2021.misc_twf_wildlife.common.register.MISCTWFEntityTags;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import static com.hexagram2021.misc_twf_wildlife.MiscTwfWildlife.MODID;

/**
 * 农牧生态模块运行时事件处理器喵~
 *
 * <p>处理可排便动物的排泄计时与排泄行为喵~</p>
 *
 * @author liudongyu
 */
@EventBusSubscriber(modid = MODID)
public final class WildlifeEventHandler {
	/**
	 * 处理生物实体的每 tick 更新，管理排泄计时器与排泄行为喵~
	 *
	 * @param event 实体 tick 事件喵~
	 */
	@SubscribeEvent
	public static void onLivingTick(EntityTickEvent.Post event) {
		if(event.getEntity() instanceof LivingEntity livingEntity && !livingEntity.level().isClientSide && livingEntity.getType().is(MISCTWFEntityTags.POOPING_ANIMALS)) {
			PoopingAnimal poopingAnimal = livingEntity.getData(MISCTWFAttachmentTypes.POOPING);
			int remainingTicks = poopingAnimal.getPoopingRemainingTicks();
			if(remainingTicks < 0) {
				poopingAnimal.resetPoopingTicks(livingEntity);
			} else if(remainingTicks > 0) {
				poopingAnimal.setPoopingRemainingTicks(remainingTicks - 1);
			} else {
				poopingAnimal.poop(livingEntity);
			}
		}
	}

	private WildlifeEventHandler() {
	}
}
