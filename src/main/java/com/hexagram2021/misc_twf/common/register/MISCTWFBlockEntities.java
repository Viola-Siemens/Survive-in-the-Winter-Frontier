package com.hexagram2021.misc_twf.common.register;

import com.google.common.collect.ImmutableSet;
import com.hexagram2021.misc_twf.common.block.entity.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;

@SuppressWarnings("ConstantConditions")
public final class MISCTWFBlockEntities {
	private static final DeferredRegister<BlockEntityType<?>> REGISTER = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, MODID);

	public static final RegistryObject<BlockEntityType<UltravioletLampBlockEntity>> ULTRAVIOLET_LAMP = REGISTER.register("ultraviolet_lamp", () -> new BlockEntityType<>(
			UltravioletLampBlockEntity::new, ImmutableSet.of(MISCTWFBlocks.ULTRAVIOLET_LAMP.get()), null
	));
	public static final RegistryObject<BlockEntityType<MoldDetacherBlockEntity>> MOLD_DETACHER = REGISTER.register("mold_detacher", () -> new BlockEntityType<>(
			MoldDetacherBlockEntity::new, ImmutableSet.of(MISCTWFBlocks.MOLD_DETACHER.get()), null
	));
	public static final RegistryObject<BlockEntityType<MoldWorkbenchBlockEntity>> MOLD_WORKBENCH = REGISTER.register("mold_workbench", () -> new BlockEntityType<>(
			MoldWorkbenchBlockEntity::new, ImmutableSet.of(MISCTWFBlocks.MOLD_WORKBENCH.get()), null
	));
	public static final RegistryObject<BlockEntityType<MutantPotionCauldronBlockEntity>> MUTANT_POTION_CAULDRON = REGISTER.register("mutant_potion_cauldron", () -> new BlockEntityType<>(
			MutantPotionCauldronBlockEntity::new, ImmutableSet.of(MISCTWFBlocks.MUTANT_POTION_CAULDRON.get()), null
	));
	public static final RegistryObject<BlockEntityType<MonsterEggBlockEntity>> MONSTER_EGG = REGISTER.register("monster_egg", () -> new BlockEntityType<>(
			MonsterEggBlockEntity::new, ImmutableSet.of(MISCTWFBlocks.MONSTER_EGG.get()), null
	));
	public static final RegistryObject<BlockEntityType<DeadAnimalBlockEntity>> DEAD_ANIMAL = REGISTER.register("dead_animal", () -> new BlockEntityType<>(
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

	private MISCTWFBlockEntities() {
	}

	public static void init(IEventBus bus) {
		REGISTER.register(bus);
	}
}
