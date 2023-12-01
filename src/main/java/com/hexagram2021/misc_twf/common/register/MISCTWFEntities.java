package com.hexagram2021.misc_twf.common.register;

import com.hexagram2021.misc_twf.common.entity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;

public final class MISCTWFEntities {
	private static final DeferredRegister<EntityType<?>> REGISTER = DeferredRegister.create(ForgeRegistries.ENTITIES, MODID);

	public static final RegistryObject<EntityType<ZombieChickenEntity>> ZOMBIE_CHICKEN = REGISTER.register(
			"zombie_chicken", () -> EntityType.Builder.of(ZombieChickenEntity::new, MobCategory.MONSTER)
					.sized(0.4F, 0.7F).clientTrackingRange(12).build(new ResourceLocation(MODID, "zombie_chicken").toString())
	);
	public static final RegistryObject<EntityType<ZombieAnimalEntity<Cow>>> ZOMBIE_COW = REGISTER.register(
			"zombie_cow", () -> EntityType.Builder.of(getEntityFactory(
							EntityType.COW,
							MISCTWFSounds.ZOMBIE_COW_AMBIENT,
							MISCTWFSounds.ZOMBIE_COW_HURT,
							MISCTWFSounds.ZOMBIE_COW_DEATH,
							MISCTWFSounds.ZOMBIE_COW_STEP
					), MobCategory.MONSTER)
					.sized(0.9F, 1.4F).clientTrackingRange(12).build(new ResourceLocation(MODID, "zombie_cow").toString())
	);
	public static final RegistryObject<EntityType<ZombieGoatEntity>> ZOMBIE_GOAT = REGISTER.register(
			"zombie_goat", () -> EntityType.Builder.of(ZombieGoatEntity::new, MobCategory.MONSTER)
					.sized(0.9F, 1.3F).clientTrackingRange(12).build(new ResourceLocation(MODID, "zombie_goat").toString())
	);
	public static final RegistryObject<EntityType<ZombieAnimalEntity<Pig>>> ZOMBIE_PIG = REGISTER.register(
			"zombie_pig", () -> EntityType.Builder.of(getEntityFactory(
							EntityType.PIG,
							MISCTWFSounds.ZOMBIE_PIG_AMBIENT,
							MISCTWFSounds.ZOMBIE_PIG_HURT,
							MISCTWFSounds.ZOMBIE_PIG_DEATH,
							MISCTWFSounds.ZOMBIE_PIG_STEP
					), MobCategory.MONSTER)
					.sized(0.9F, 0.9F).clientTrackingRange(12).build(new ResourceLocation(MODID, "zombie_pig").toString())
	);
	public static final RegistryObject<EntityType<ZombiePolarBearEntity>> ZOMBIE_POLAR_BEAR = REGISTER.register(
			"zombie_polar_bear", () -> EntityType.Builder.of(ZombiePolarBearEntity::new, MobCategory.MONSTER).immuneTo(Blocks.POWDER_SNOW)
					.sized(1.4F, 1.4F).clientTrackingRange(12).build(new ResourceLocation(MODID, "zombie_polar_bear").toString())
	);
	public static final RegistryObject<EntityType<ZombieRabbitEntity>> ZOMBIE_RABBIT = REGISTER.register(
			"zombie_rabbit", () -> EntityType.Builder.of(ZombieRabbitEntity::new, MobCategory.MONSTER)
					.sized(0.4F, 0.5F).clientTrackingRange(10).build(new ResourceLocation(MODID, "zombie_rabbit").toString())
	);
	public static final RegistryObject<EntityType<ZombieSheepEntity>> ZOMBIE_SHEEP = REGISTER.register(
			"zombie_sheep", () -> EntityType.Builder.of(ZombieSheepEntity::new, MobCategory.MONSTER)
					.sized(0.9F, 1.3F).clientTrackingRange(12).build(new ResourceLocation(MODID, "zombie_sheep").toString())
	);
	public static final RegistryObject<EntityType<ZombieAnimalEntity<Wolf>>> ZOMBIE_WOLF = REGISTER.register(
			"zombie_wolf", () -> EntityType.Builder.of(getEntityFactory(
							EntityType.WOLF,
							MISCTWFSounds.ZOMBIE_WOLF_AMBIENT,
							MISCTWFSounds.ZOMBIE_WOLF_HURT,
							MISCTWFSounds.ZOMBIE_WOLF_DEATH,
							MISCTWFSounds.ZOMBIE_WOLF_STEP
					), MobCategory.MONSTER)
					.sized(0.6F, 0.85F).clientTrackingRange(12).build(new ResourceLocation(MODID, "zombie_wolf").toString())
	);

	private static <T extends Animal> EntityType.EntityFactory<ZombieAnimalEntity<T>> getEntityFactory(EntityType<T> animalEntityType, SoundEvent ambientSound, SoundEvent hurtSound, SoundEvent deathSound, SoundEvent stepSound) {
		return (entityType, level) -> new ZombieAnimalEntity<>(entityType, animalEntityType, level) {
			@Override
			protected SoundEvent getAmbientSound() {
				return ambientSound;
			}

			@Override
			protected SoundEvent getHurtSound(DamageSource damageSource) {
				return hurtSound;
			}

			@Override
			protected SoundEvent getDeathSound() {
				return deathSound;
			}

			@Override
			protected SoundEvent getStepSound() {
				return stepSound;
			}
		};
	}

	private MISCTWFEntities() {
	}

	public static void init(IEventBus bus) {
		REGISTER.register(bus);
	}
}
