package com.hexagram2021.misc_twf.common;

import com.hexagram2021.misc_twf.common.entity.ZombieAnimalEntity;
import com.hexagram2021.misc_twf.common.entity.capability.CapabilityAnimal;
import com.hexagram2021.misc_twf.common.register.*;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;

@EventBusSubscriber(modid = MODID)
public class MISCTWFContent {
	public static void modConstruct(IEventBus bus) {
		initTags();

		MISCTWFAttachmentTypes.init(bus);
		MISCTWFAttributes.init(bus);
		MISCTWFBlockStateProperties.init();
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

		MISCTWFSkills.init(bus);
	}

	private static void initTags() {
		MISCTWFItemTags.init();
	}

	@SubscribeEvent
	public static void onRegister(RegisterEvent event) {

	}

	public static void registerPotions(RegistryEvent.Register<Potion> event) {
		MISCTWFBrewingRecipes.init();
	}

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
	public static void registerFeatures(RegistryEvent.Register<Feature<?>> event) {
		MISCTWFFeatures.init(event.getRegistry()::register);
		MISCTWFConfiguredFeatures.init();
		MISCTWFPlacedFeatures.init();
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

	@SubscribeEvent
	public static void onModifyDefaultAttributes(EntityAttributeModificationEvent event) {
		event.add(EntityType.PLAYER, MISCTWFAttributes.GUN_MASTERY, 0.0);
	}
}
