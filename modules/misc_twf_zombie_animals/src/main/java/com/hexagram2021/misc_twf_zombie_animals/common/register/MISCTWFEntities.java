package com.hexagram2021.misc_twf_zombie_animals.common.register;

import com.hexagram2021.misc_twf_zombie_animals.common.entity.*;
import net.minecraft.core.registries.Registries;
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
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.hexagram2021.misc_twf_zombie_animals.MiscTwfZombieAnimals.CONTENT_NAMESPACE;

/**
 * 模组实体类型注册类，负责注册所有自定义的实体类型喵~
 * 包含各种僵尸化动物实体喵~
 *
 * @author liudongyu
 */
public final class MISCTWFEntities {
	private static final DeferredRegister<EntityType<?>> REGISTER = DeferredRegister.create(Registries.ENTITY_TYPE, CONTENT_NAMESPACE);

	/**
	 * 僵尸鸡实体类型喵~
	 */
	public static final DeferredHolder<EntityType<?>, EntityType<ZombieChickenEntity>> ZOMBIE_CHICKEN = REGISTER.register(
			"zombie_chicken", () -> EntityType.Builder.of(ZombieChickenEntity::new, MobCategory.MONSTER)
					.sized(0.4F, 0.7F).clientTrackingRange(12).build(ResourceLocation.fromNamespaceAndPath(CONTENT_NAMESPACE, "zombie_chicken").toString())
	);
	/**
	 * 僵尸牛实体类型喵~
	 */
	public static final DeferredHolder<EntityType<?>, EntityType<ZombieAnimalEntity<Cow>>> ZOMBIE_COW = REGISTER.register(
			"zombie_cow", () -> EntityType.Builder.of(getEntityFactory(
							EntityType.COW,
							MISCTWFSounds.ZOMBIE_COW_AMBIENT,
							MISCTWFSounds.ZOMBIE_COW_HURT,
							MISCTWFSounds.ZOMBIE_COW_DEATH,
							MISCTWFSounds.ZOMBIE_COW_STEP
					), MobCategory.MONSTER)
					.sized(0.9F, 1.4F).clientTrackingRange(12).build(ResourceLocation.fromNamespaceAndPath(CONTENT_NAMESPACE, "zombie_cow").toString())
	);
	/**
	 * 僵尸山羊实体类型喵~
	 */
	public static final DeferredHolder<EntityType<?>, EntityType<ZombieGoatEntity>> ZOMBIE_GOAT = REGISTER.register(
			"zombie_goat", () -> EntityType.Builder.of(ZombieGoatEntity::new, MobCategory.MONSTER)
					.sized(0.9F, 1.3F).clientTrackingRange(12).build(ResourceLocation.fromNamespaceAndPath(CONTENT_NAMESPACE, "zombie_goat").toString())
	);
	/**
	 * 僵尸猪实体类型喵~
	 */
	public static final DeferredHolder<EntityType<?>, EntityType<ZombieAnimalEntity<Pig>>> ZOMBIE_PIG = REGISTER.register(
			"zombie_pig", () -> EntityType.Builder.of(getEntityFactory(
							EntityType.PIG,
							MISCTWFSounds.ZOMBIE_PIG_AMBIENT,
							MISCTWFSounds.ZOMBIE_PIG_HURT,
							MISCTWFSounds.ZOMBIE_PIG_DEATH,
							MISCTWFSounds.ZOMBIE_PIG_STEP
					), MobCategory.MONSTER)
					.sized(0.9F, 0.9F).clientTrackingRange(12).build(ResourceLocation.fromNamespaceAndPath(CONTENT_NAMESPACE, "zombie_pig").toString())
	);
	/**
	 * 僵尸北极熊实体类型，免疫细雪伤害喵~
	 */
	public static final DeferredHolder<EntityType<?>, EntityType<ZombiePolarBearEntity>> ZOMBIE_POLAR_BEAR = REGISTER.register(
			"zombie_polar_bear", () -> EntityType.Builder.of(ZombiePolarBearEntity::new, MobCategory.MONSTER).immuneTo(Blocks.POWDER_SNOW)
					.sized(1.4F, 1.4F).clientTrackingRange(12).build(ResourceLocation.fromNamespaceAndPath(CONTENT_NAMESPACE, "zombie_polar_bear").toString())
	);
	/**
	 * 僵尸兔实体类型喵~
	 */
	public static final DeferredHolder<EntityType<?>, EntityType<ZombieRabbitEntity>> ZOMBIE_RABBIT = REGISTER.register(
			"zombie_rabbit", () -> EntityType.Builder.of(ZombieRabbitEntity::new, MobCategory.MONSTER)
					.sized(0.4F, 0.5F).clientTrackingRange(10).build(ResourceLocation.fromNamespaceAndPath(CONTENT_NAMESPACE, "zombie_rabbit").toString())
	);
	/**
	 * 僵尸羊实体类型喵~
	 */
	public static final DeferredHolder<EntityType<?>, EntityType<ZombieSheepEntity>> ZOMBIE_SHEEP = REGISTER.register(
			"zombie_sheep", () -> EntityType.Builder.of(ZombieSheepEntity::new, MobCategory.MONSTER)
					.sized(0.9F, 1.3F).clientTrackingRange(12).build(ResourceLocation.fromNamespaceAndPath(CONTENT_NAMESPACE, "zombie_sheep").toString())
	);
	/**
	 * 僵尸狼实体类型喵~
	 */
	public static final DeferredHolder<EntityType<?>, EntityType<ZombieAnimalEntity<Wolf>>> ZOMBIE_WOLF = REGISTER.register(
			"zombie_wolf", () -> EntityType.Builder.of(getEntityFactory(
							EntityType.WOLF,
							MISCTWFSounds.ZOMBIE_WOLF_AMBIENT,
							MISCTWFSounds.ZOMBIE_WOLF_HURT,
							MISCTWFSounds.ZOMBIE_WOLF_DEATH,
							MISCTWFSounds.ZOMBIE_WOLF_STEP
					), MobCategory.MONSTER)
					.sized(0.6F, 0.85F).clientTrackingRange(12).build(ResourceLocation.fromNamespaceAndPath(CONTENT_NAMESPACE, "zombie_wolf").toString())
	);

	/**
	 * 创建带有自定义音效的僵尸动物实体工厂喵~
	 *
	 * @param animalEntityType 原始动物实体类型喵~
	 * @param ambientSound 环境音效喵~
	 * @param hurtSound 受伤音效喵~
	 * @param deathSound 死亡音效喵~
	 * @param stepSound 脚步音效喵~
	 * @param <T> 动物实体类型喵~
	 * @return 实体工厂实例喵~
	 */
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

	/**
	 * 初始化并注册所有实体类型到事件总线喵~
	 *
	 * @param bus NeoForge 事件总线喵~
	 */
	public static void init(IEventBus bus) {
		REGISTER.register(bus);
	}
}
