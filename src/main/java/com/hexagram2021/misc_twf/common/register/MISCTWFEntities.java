package com.hexagram2021.misc_twf.common.register;

import com.hexagram2021.misc_twf.common.entity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.animal.Pig;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;

public final class MISCTWFEntities {
	private static final DeferredRegister<EntityType<?>> REGISTER = DeferredRegister.create(ForgeRegistries.ENTITIES, MODID);

	public static final RegistryObject<EntityType<ZombieAnimalEntity<Pig>>> ZOMBIE_PIG = REGISTER.register(
			"zombie_pig", () -> EntityType.Builder.<ZombieAnimalEntity<Pig>>of(ZombieAnimalEntity::new, MobCategory.MONSTER)
					.sized(0.9F, 0.9F).clientTrackingRange(12).build(new ResourceLocation(MODID, "zombie_pig").toString())
	);

	private MISCTWFEntities() {
	}

	public static void init(IEventBus bus) {
		REGISTER.register(bus);
	}
}
