package com.hexagram2021.misc_twf.common.register;

import com.google.common.collect.ImmutableSet;
import com.hexagram2021.misc_twf.common.block.entity.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;

/**
 * 方块实体类型注册类，管理模组中所有方块实体类型的注册喵~
 * 方块实体用于为方块添加额外的数据存储、逻辑处理和渲染功能喵~
 *
 * @author liudongyu
 */
@SuppressWarnings("ConstantConditions")
public final class MISCTWFBlockEntities {
	private static final DeferredRegister<BlockEntityType<?>> REGISTER = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MODID);

	/**
	 * 强紫外线照射灯方块实体类型，用于管理紫外线灯的能量存储和工作状态喵~
	 */
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<UltravioletLampBlockEntity>> ULTRAVIOLET_LAMP = REGISTER.register("ultraviolet_lamp", () -> new BlockEntityType<>(
			UltravioletLampBlockEntity::new, ImmutableSet.of(MISCTWFBlocks.ULTRAVIOLET_LAMP.get()), null
	));

	/**
	 * 模具分离器方块实体类型，用于自动剥离子弹模具中的弹药喵~
	 */
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MoldDetacherBlockEntity>> MOLD_DETACHER = REGISTER.register("mold_detacher", () -> new BlockEntityType<>(
			MoldDetacherBlockEntity::new, ImmutableSet.of(MISCTWFBlocks.MOLD_DETACHER.get()), null
	));

	/**
	 * 回收炉方块实体类型，用于回收和熔炼物品喵~
	 */
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RecoveryFurnaceBlockEntity>> RECOVERY_FURNACE = REGISTER.register("recovery_furnace", () -> new BlockEntityType<>(
			RecoveryFurnaceBlockEntity::new, ImmutableSet.of(MISCTWFBlocks.RECOVERY_FURNACE.get()), null
	));

	/**
	 * 模具加工台方块实体类型，用于制作和加工子弹模具喵~
	 */
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MoldWorkbenchBlockEntity>> MOLD_WORKBENCH = REGISTER.register("mold_workbench", () -> new BlockEntityType<>(
			MoldWorkbenchBlockEntity::new, ImmutableSet.of(MISCTWFBlocks.MOLD_WORKBENCH.get()), null
	));

	/**
	 * 装变异药品的炼药锅方块实体类型，用于制作变异药剂喵~
	 */
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MutantPotionCauldronBlockEntity>> MUTANT_POTION_CAULDRON = REGISTER.register("mutant_potion_cauldron", () -> new BlockEntityType<>(
			MutantPotionCauldronBlockEntity::new, ImmutableSet.of(MISCTWFBlocks.MUTANT_POTION_CAULDRON.get()), null
	));

	/**
	 * 怪物蛋方块实体类型，用于孵化怪物生物喵~
	 */
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MonsterEggBlockEntity>> MONSTER_EGG = REGISTER.register("monster_egg", () -> new BlockEntityType<>(
			MonsterEggBlockEntity::new, ImmutableSet.of(MISCTWFBlocks.MONSTER_EGG.get()), null
	));

	/**
	 * 死亡动物方块实体类型，用于存储动物尸体的数据和渲染信息喵~
	 * 支持多种动物类型，包括鸡、牛、山羊、马、猪、北极熊、兔子、绵羊和狼喵~
	 */
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DeadAnimalBlockEntity>> DEAD_ANIMAL = REGISTER.register("dead_animal", () -> new BlockEntityType<>(
			DeadAnimalBlockEntity::new, ImmutableSet.of(
					MISCTWFBlocks.DeadAnimals.DEAD_CHICKEN.get(),
					MISCTWFBlocks.DeadAnimals.DEAD_COW.get(),
					MISCTWFBlocks.DeadAnimals.DEAD_GOAT.get(),
					MISCTWFBlocks.DeadAnimals.DEAD_HORSE.get(),
					MISCTWFBlocks.DeadAnimals.DEAD_PIG.get(),
					MISCTWFBlocks.DeadAnimals.DEAD_POLARBEAR.get(),
					MISCTWFBlocks.DeadAnimals.DEAD_RABBIT.get(),
					MISCTWFBlocks.DeadAnimals.DEAD_SHEEP.get(),
					MISCTWFBlocks.DeadAnimals.DEAD_WOLF.get()
			), null
	));

	/**
	 * 初始化并注册所有方块实体类型到事件总线喵~
	 *
	 * @param bus NeoForge 事件总线喵~
	 */
	public static void init(IEventBus bus) {
		REGISTER.register(bus);
	}

	private MISCTWFBlockEntities() {
	}
}
