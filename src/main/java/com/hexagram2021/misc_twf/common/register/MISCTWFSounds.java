package com.hexagram2021.misc_twf.common.register;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.HashMap;
import java.util.Map;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;

/**
 * 模组自定义音效注册类喵~
 * 管理所有僵尸动物实体的环境音、受伤音、死亡音和脚步音喵~
 *
 * @author liudongyu
 */
public class MISCTWFSounds {
	/**
	 * 用于存储所有待注册的音效事件喵~
	 */
	private static final Map<ResourceLocation, SoundEvent> REGISTERED_EVENTS = new HashMap<>();

	/** 僵尸鸡的环境音喵~ */
	public static final SoundEvent ZOMBIE_CHICKEN_AMBIENT = registerSound("entity.zombie_chicken.ambient");
	/** 僵尸鸡的受伤音喵~ */
	public static final SoundEvent ZOMBIE_CHICKEN_HURT = registerSound("entity.zombie_chicken.hurt");
	/** 僵尸鸡的死亡音喵~ */
	public static final SoundEvent ZOMBIE_CHICKEN_DEATH = registerSound("entity.zombie_chicken.death");
	/** 僵尸鸡的脚步音喵~ */
	public static final SoundEvent ZOMBIE_CHICKEN_STEP = registerSound("entity.zombie_chicken.step");
	/** 僵尸牛的环境音喵~ */
	public static final SoundEvent ZOMBIE_COW_AMBIENT = registerSound("entity.zombie_cow.ambient");
	/** 僵尸牛的受伤音喵~ */
	public static final SoundEvent ZOMBIE_COW_HURT = registerSound("entity.zombie_cow.hurt");
	/** 僵尸牛的死亡音喵~ */
	public static final SoundEvent ZOMBIE_COW_DEATH = registerSound("entity.zombie_cow.death");
	/** 僵尸牛的脚步音喵~ */
	public static final SoundEvent ZOMBIE_COW_STEP = registerSound("entity.zombie_cow.step");
	/** 僵尸山羊的环境音喵~ */
	public static final SoundEvent ZOMBIE_GOAT_AMBIENT = registerSound("entity.zombie_goat.ambient");
	/** 僵尸山羊的受伤音喵~ */
	public static final SoundEvent ZOMBIE_GOAT_HURT = registerSound("entity.zombie_goat.hurt");
	/** 僵尸山羊的死亡音喵~ */
	public static final SoundEvent ZOMBIE_GOAT_DEATH = registerSound("entity.zombie_goat.death");
	/** 僵尸山羊的脚步音喵~ */
	public static final SoundEvent ZOMBIE_GOAT_STEP = registerSound("entity.zombie_goat.step");
	/** 僵尸猪的环境音喵~ */
	public static final SoundEvent ZOMBIE_PIG_AMBIENT = registerSound("entity.zombie_pig.ambient");
	/** 僵尸猪的受伤音喵~ */
	public static final SoundEvent ZOMBIE_PIG_HURT = registerSound("entity.zombie_pig.hurt");
	/** 僵尸猪的死亡音喵~ */
	public static final SoundEvent ZOMBIE_PIG_DEATH = registerSound("entity.zombie_pig.death");
	/** 僵尸猪的脚步音喵~ */
	public static final SoundEvent ZOMBIE_PIG_STEP = registerSound("entity.zombie_pig.step");
	/** 僵尸北极熊的环境音喵~ */
	public static final SoundEvent ZOMBIE_POLAR_BEAR_AMBIENT = registerSound("entity.zombie_polar_bear.ambient");
	/** 僵尸北极熊的受伤音喵~ */
	public static final SoundEvent ZOMBIE_POLAR_BEAR_HURT = registerSound("entity.zombie_polar_bear.hurt");
	/** 僵尸北极熊的死亡音喵~ */
	public static final SoundEvent ZOMBIE_POLAR_BEAR_DEATH = registerSound("entity.zombie_polar_bear.death");
	/** 僵尸北极熊的脚步音喵~ */
	public static final SoundEvent ZOMBIE_POLAR_BEAR_STEP = registerSound("entity.zombie_polar_bear.step");
	/** 僵尸兔子的环境音喵~ */
	public static final SoundEvent ZOMBIE_RABBIT_AMBIENT = registerSound("entity.zombie_rabbit.ambient");
	/** 僵尸兔子的受伤音喵~ */
	public static final SoundEvent ZOMBIE_RABBIT_HURT = registerSound("entity.zombie_rabbit.hurt");
	/** 僵尸兔子的死亡音喵~ */
	public static final SoundEvent ZOMBIE_RABBIT_DEATH = registerSound("entity.zombie_rabbit.death");
	/** 僵尸兔子的脚步音喵~ */
	public static final SoundEvent ZOMBIE_RABBIT_STEP = registerSound("entity.zombie_rabbit.step");
	/** 僵尸绵羊的环境音喵~ */
	public static final SoundEvent ZOMBIE_SHEEP_AMBIENT = registerSound("entity.zombie_sheep.ambient");
	/** 僵尸绵羊的受伤音喵~ */
	public static final SoundEvent ZOMBIE_SHEEP_HURT = registerSound("entity.zombie_sheep.hurt");
	/** 僵尸绵羊的死亡音喵~ */
	public static final SoundEvent ZOMBIE_SHEEP_DEATH = registerSound("entity.zombie_sheep.death");
	/** 僵尸绵羊的脚步音喵~ */
	public static final SoundEvent ZOMBIE_SHEEP_STEP = registerSound("entity.zombie_sheep.step");
	/** 僵尸狼的环境音喵~ */
	public static final SoundEvent ZOMBIE_WOLF_AMBIENT = registerSound("entity.zombie_wolf.ambient");
	/** 僵尸狼的受伤音喵~ */
	public static final SoundEvent ZOMBIE_WOLF_HURT = registerSound("entity.zombie_wolf.hurt");
	/** 僵尸狼的死亡音喵~ */
	public static final SoundEvent ZOMBIE_WOLF_DEATH = registerSound("entity.zombie_wolf.death");
	/** 僵尸狼的脚步音喵~ */
	public static final SoundEvent ZOMBIE_WOLF_STEP = registerSound("entity.zombie_wolf.step");

	/**
	 * 注册单个音效事件喵~
	 *
	 * @param name 音效的资源路径名称喵~
	 * @return 创建的音效事件喵~
	 */
	private static SoundEvent registerSound(String name) {
		ResourceLocation location = ResourceLocation.fromNamespaceAndPath(MODID, name);
		SoundEvent event = SoundEvent.createVariableRangeEvent(location);
		REGISTERED_EVENTS.put(location, event);
		return event;
	}

	/**
	 * 将所有音效事件注册到游戏中喵~
	 *
	 * @param registerHelper 注册辅助器喵~
	 */
	public static void init(RegisterEvent.RegisterHelper<SoundEvent> registerHelper) {
		REGISTERED_EVENTS.forEach(registerHelper::register);
	}
}
