package com.hexagram2021.misc_twf.common;

import com.hexagram2021.misc_twf.common.entity.ZombieAnimalEntity;
import com.hexagram2021.misc_twf.common.entity.capability.CapabilityAnimal;
import com.hexagram2021.misc_twf.common.register.*;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class MISCTWFContent {
	public static void modConstruct(IEventBus bus) {
		initTags();

		MISCTWFFluids.init(bus);
		MISCTWFBlocks.init(bus);
		MISCTWFBlockEntities.init(bus);
		MISCTWFItems.init(bus);
		MISCTWFEntities.init(bus);
		MISCTWFRecipeTypes.init(bus);
		MISCTWFRecipeSerializers.init(bus);
		MISCTWFMobEffects.init(bus);
		MISCTWFMenuTypes.init(bus);
		MISCTWFTravelersBackpackTacOps.init(bus);
	}

	private static void initTags() {
		MISCTWFItemTags.init();
	}

	@SubscribeEvent
	public static void registerPotions(RegistryEvent.Register<Potion> event) {
		MISCTWFBrewingRecipes.init();
	}

	@SubscribeEvent
	public static void registerSounds(RegistryEvent.Register<SoundEvent> event) {
		MISCTWFSounds.init(event.getRegistry());
	}

	@SubscribeEvent
	public static void registerStructures(RegistryEvent.Register<StructureFeature<?>> event) {
		MISCTWFStructures.init(event.getRegistry()::register);
		MISCTWFStructurePieceTypes.init();
		MISCTWFConfiguredStructures.init();
		MISCTWFStructureSets.init();
	}

	@SubscribeEvent
	public static void registerCapabilities(RegisterCapabilitiesEvent event) {
		CapabilityAnimal.register(event::register);
	}

	@SubscribeEvent
	public static void onAttributeCreate(EntityAttributeCreationEvent event) {
		event.put(MISCTWFEntities.ZOMBIE_CHICKEN.get(), ZombieAnimalEntity.createAttributes(4.0D, 0.25D).build());
		event.put(MISCTWFEntities.ZOMBIE_COW.get(), ZombieAnimalEntity.createAttributes(10.0D, 0.25D).add(Attributes.ATTACK_DAMAGE, 3.0D).build());
		event.put(MISCTWFEntities.ZOMBIE_GOAT.get(), ZombieAnimalEntity.createAttributes(10.0D, 0.3D).build());
		event.put(MISCTWFEntities.ZOMBIE_PIG.get(), ZombieAnimalEntity.createAttributes(10.0D, 0.25D).build());
		event.put(MISCTWFEntities.ZOMBIE_POLAR_BEAR.get(), ZombieAnimalEntity.createAttributes(30.0D, 0.25D).add(Attributes.ATTACK_DAMAGE, 6.0D).build());
		event.put(MISCTWFEntities.ZOMBIE_RABBIT.get(), ZombieAnimalEntity.createAttributes(3.0D, 0.3D).build());
		event.put(MISCTWFEntities.ZOMBIE_SHEEP.get(), ZombieAnimalEntity.createAttributes(8.0D, 0.23D).build());
		event.put(MISCTWFEntities.ZOMBIE_WOLF.get(), ZombieAnimalEntity.createAttributes(8.0D, 0.3D).add(Attributes.ATTACK_DAMAGE, 2.0D).build());
	}
}
