package com.hexagram2021.misc_twf_wildlife.common.register;

import com.hexagram2021.misc_twf_wildlife.common.block.entity.DeadAnimalBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.hexagram2021.misc_twf_wildlife.MiscTwfWildlife.CONTENT_NAMESPACE;

/**
 * 农牧生态模块方块实体类型注册类喵~
 *
 * @author liudongyu
 */
public final class MISCTWFBlockEntities {
	private static final DeferredRegister<BlockEntityType<?>> REGISTER = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, CONTENT_NAMESPACE);

	/** 动物尸体方块实体喵~ */
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DeadAnimalBlockEntity>> DEAD_ANIMAL = REGISTER.register("dead_animal", () -> BlockEntityType.Builder.of(
			(blockPos, blockState) -> new DeadAnimalBlockEntity(blockPos, blockState),
			MISCTWFBlocks.DeadAnimals.DEAD_CHICKEN.get(),
			MISCTWFBlocks.DeadAnimals.DEAD_COW.get(),
			MISCTWFBlocks.DeadAnimals.DEAD_GOAT.get(),
			MISCTWFBlocks.DeadAnimals.DEAD_HORSE.get(),
			MISCTWFBlocks.DeadAnimals.DEAD_PIG.get(),
			MISCTWFBlocks.DeadAnimals.DEAD_POLARBEAR.get(),
			MISCTWFBlocks.DeadAnimals.DEAD_RABBIT.get(),
			MISCTWFBlocks.DeadAnimals.DEAD_SHEEP.get(),
			MISCTWFBlocks.DeadAnimals.DEAD_WOLF.get()
	).build(null));

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
