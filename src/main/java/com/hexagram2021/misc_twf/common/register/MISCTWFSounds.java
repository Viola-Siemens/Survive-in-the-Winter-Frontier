package com.hexagram2021.misc_twf.common.register;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.HashMap;
import java.util.Map;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;

public class MISCTWFSounds {
	private static final Map<ResourceLocation, SoundEvent> registeredEvents = new HashMap<>();

	public static final SoundEvent ZOMBIE_CHICKEN_AMBIENT = registerSound("entity.zombie_chicken.ambient");
	public static final SoundEvent ZOMBIE_CHICKEN_HURT = registerSound("entity.zombie_chicken.hurt");
	public static final SoundEvent ZOMBIE_CHICKEN_DEATH = registerSound("entity.zombie_chicken.death");
	public static final SoundEvent ZOMBIE_CHICKEN_STEP = registerSound("entity.zombie_chicken.step");
	public static final SoundEvent ZOMBIE_COW_AMBIENT = registerSound("entity.zombie_cow.ambient");
	public static final SoundEvent ZOMBIE_COW_HURT = registerSound("entity.zombie_cow.hurt");
	public static final SoundEvent ZOMBIE_COW_DEATH = registerSound("entity.zombie_cow.death");
	public static final SoundEvent ZOMBIE_COW_STEP = registerSound("entity.zombie_cow.step");
	public static final SoundEvent ZOMBIE_GOAT_AMBIENT = registerSound("entity.zombie_goat.ambient");
	public static final SoundEvent ZOMBIE_GOAT_HURT = registerSound("entity.zombie_goat.hurt");
	public static final SoundEvent ZOMBIE_GOAT_DEATH = registerSound("entity.zombie_goat.death");
	public static final SoundEvent ZOMBIE_GOAT_STEP = registerSound("entity.zombie_goat.step");
	public static final SoundEvent ZOMBIE_PIG_AMBIENT = registerSound("entity.zombie_pig.ambient");
	public static final SoundEvent ZOMBIE_PIG_HURT = registerSound("entity.zombie_pig.hurt");
	public static final SoundEvent ZOMBIE_PIG_DEATH = registerSound("entity.zombie_pig.death");
	public static final SoundEvent ZOMBIE_PIG_STEP = registerSound("entity.zombie_pig.step");
	public static final SoundEvent ZOMBIE_POLAR_BEAR_AMBIENT = registerSound("entity.zombie_polar_bear.ambient");
	public static final SoundEvent ZOMBIE_POLAR_BEAR_HURT = registerSound("entity.zombie_polar_bear.hurt");
	public static final SoundEvent ZOMBIE_POLAR_BEAR_DEATH = registerSound("entity.zombie_polar_bear.death");
	public static final SoundEvent ZOMBIE_POLAR_BEAR_STEP = registerSound("entity.zombie_polar_bear.step");
	public static final SoundEvent ZOMBIE_RABBIT_AMBIENT = registerSound("entity.zombie_rabbit.ambient");
	public static final SoundEvent ZOMBIE_RABBIT_HURT = registerSound("entity.zombie_rabbit.hurt");
	public static final SoundEvent ZOMBIE_RABBIT_DEATH = registerSound("entity.zombie_rabbit.death");
	public static final SoundEvent ZOMBIE_RABBIT_STEP = registerSound("entity.zombie_rabbit.step");
	public static final SoundEvent ZOMBIE_SHEEP_AMBIENT = registerSound("entity.zombie_sheep.ambient");
	public static final SoundEvent ZOMBIE_SHEEP_HURT = registerSound("entity.zombie_sheep.hurt");
	public static final SoundEvent ZOMBIE_SHEEP_DEATH = registerSound("entity.zombie_sheep.death");
	public static final SoundEvent ZOMBIE_SHEEP_STEP = registerSound("entity.zombie_sheep.step");
	public static final SoundEvent ZOMBIE_WOLF_AMBIENT = registerSound("entity.zombie_wolf.ambient");
	public static final SoundEvent ZOMBIE_WOLF_HURT = registerSound("entity.zombie_wolf.hurt");
	public static final SoundEvent ZOMBIE_WOLF_DEATH = registerSound("entity.zombie_wolf.death");
	public static final SoundEvent ZOMBIE_WOLF_STEP = registerSound("entity.zombie_wolf.step");

	private static SoundEvent registerSound(String name) {
		ResourceLocation location = new ResourceLocation(MODID, name);
		SoundEvent event = new SoundEvent(location);
		registeredEvents.put(location, event);
		return event;
	}

	public static void init(IForgeRegistry<SoundEvent> registry) {
		registeredEvents.forEach((id, sound) -> {
			sound.setRegistryName(id);
			registry.register(sound);
		});
	}
}
