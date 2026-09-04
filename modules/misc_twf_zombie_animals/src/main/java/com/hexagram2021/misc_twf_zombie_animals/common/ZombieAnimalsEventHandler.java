package com.hexagram2021.misc_twf_zombie_animals.common;

import com.hexagram2021.misc_twf_zombie_animals.common.entity.ZombieGoatEntity;
import com.hexagram2021.misc_twf_zombie_animals.common.entity.ZombieSheepEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.animal.goat.GoatAi;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingConversionEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

import static com.hexagram2021.misc_twf_zombie_animals.MiscTwfZombieAnimals.MODID;

/**
 * 僵尸动物模块的运行时事件处理器喵~
 *
 * <p>处理僵尸山羊冲撞击退与绵羊转化保色等随模块域事件喵~</p>
 *
 * @author liudongyu
 */
@EventBusSubscriber(modid = MODID)
public final class ZombieAnimalsEventHandler {
	/**
	 * 生物受伤事件：应用僵尸山羊的冲撞击退喵~
	 *
	 * @param event 生物伤害事件喵~
	 */
	@SubscribeEvent
	public static void onLivingHurt(LivingDamageEvent.Pre event) {
		LivingEntity livingEntity = event.getEntity();
		if(event.getSource().getEntity() instanceof ZombieGoatEntity goat) {
			Vec3 direction = goat.position().subtract(livingEntity.position()).normalize();
			double multiplier = goat.isBaby() ? GoatAi.BABY_RAM_KNOCKBACK_FORCE : GoatAi.ADULT_RAM_KNOCKBACK_FORCE;
			livingEntity.knockback(direction.x * multiplier, direction.y * multiplier, direction.z * multiplier);
		}
	}

	/**
	 * 生物转化完成事件：保持绵羊羊毛颜色喵~
	 *
	 * @param event 生物转化事件喵~
	 */
	@SubscribeEvent
	public static void onLivingConvert(LivingConversionEvent.Post event) {
		if(event.getEntity() instanceof Sheep sheep && event.getOutcome() instanceof ZombieSheepEntity zombieSheep) {
			zombieSheep.setColor(sheep.getColor());
		}
	}

	private ZombieAnimalsEventHandler() {
	}
}
