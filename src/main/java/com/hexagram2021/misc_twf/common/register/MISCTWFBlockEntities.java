package com.hexagram2021.misc_twf.common.register;

import com.google.common.collect.ImmutableSet;
import com.hexagram2021.misc_twf.common.block.entity.MonsterEggBlockEntity;
import com.hexagram2021.misc_twf.common.block.entity.MutantPotionCauldronBlockEntity;
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
