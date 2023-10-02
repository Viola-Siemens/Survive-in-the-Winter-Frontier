package com.hexagram2021.misc_twf.common.register;

import com.google.common.collect.ImmutableSet;
import com.hexagram2021.misc_twf.common.block.entity.UltravioletLampBlockEntity;
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

	private MISCTWFBlockEntities() {
	}

	public static void init(IEventBus bus) {
		REGISTER.register(bus);
	}
}
