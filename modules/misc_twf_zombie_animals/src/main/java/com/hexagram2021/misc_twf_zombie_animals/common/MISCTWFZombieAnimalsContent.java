package com.hexagram2021.misc_twf_zombie_animals.common;

import com.hexagram2021.misc_twf_zombie_animals.common.entity.ZombieAnimalEntity;
import com.hexagram2021.misc_twf_zombie_animals.common.register.MISCTWFEntities;
import com.hexagram2021.misc_twf_zombie_animals.common.register.MISCTWFSounds;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

import static com.hexagram2021.misc_twf_zombie_animals.MiscTwfZombieAnimals.MODID;

/**
 * 僵尸动物模块内容注册与初始化管理类喵~
 *
 * <p>负责模块内注册器的初始化（实体、音效）与僵尸动物实体属性创建喵~</p>
 *
 * @author liudongyu
 */
@EventBusSubscriber(modid = MODID)
public final class MISCTWFZombieAnimalsContent {
	/**
	 * 模块构造阶段的主入口方法喵~
	 *
	 * @param bus 模块事件总线喵~
	 */
	public static void modConstruct(IEventBus bus) {
		MISCTWFEntities.init(bus);
	}

	/**
	 * 监听通用注册事件，注册音效事件喵~
	 *
	 * @param event 注册事件喵~
	 */
	@SubscribeEvent
	public static void onRegister(RegisterEvent event) {
		event.register(Registries.SOUND_EVENT, MISCTWFSounds::init);
	}

	/**
	 * 创建僵尸动物实体的属性喵~
	 *
	 * @param event 实体属性创建事件喵~
	 */
	@SubscribeEvent
	public static void onAttributeCreate(EntityAttributeCreationEvent event) {
		event.put(MISCTWFEntities.ZOMBIE_CHICKEN.get(), ZombieAnimalEntity.createAttributes(4.0D, 0.25D).build());
		event.put(MISCTWFEntities.ZOMBIE_COW.get(), ZombieAnimalEntity.createAttributes(10.0D, 0.25D).add(Attributes.ATTACK_DAMAGE, 3.0D).build());
		event.put(MISCTWFEntities.ZOMBIE_GOAT.get(), ZombieAnimalEntity.createAttributes(10.0D, 0.3D).build());
		event.put(MISCTWFEntities.ZOMBIE_PIG.get(), ZombieAnimalEntity.createAttributes(10.0D, 0.25D).build());
		event.put(MISCTWFEntities.ZOMBIE_POLAR_BEAR.get(), ZombieAnimalEntity.createAttributes(30.0D, 0.25D).add(Attributes.ATTACK_DAMAGE, 6.0D).build());
		event.put(MISCTWFEntities.ZOMBIE_RABBIT.get(), ZombieAnimalEntity.createAttributes(3.0D, 0.3D).build());
		event.put(MISCTWFEntities.ZOMBIE_SHEEP.get(), ZombieAnimalEntity.createAttributes(8.0D, 0.23D).build());
		event.put(MISCTWFEntities.ZOMBIE_WOLF.get(), ZombieAnimalEntity.createAttributes(8.0D, 0.3D).add(Attributes.ATTACK_DAMAGE, 2.0D).build());
	}

	private MISCTWFZombieAnimalsContent() {
	}
}
