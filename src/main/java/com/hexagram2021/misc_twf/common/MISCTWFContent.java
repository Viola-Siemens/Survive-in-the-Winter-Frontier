package com.hexagram2021.misc_twf.common;

import com.hexagram2021.misc_twf.common.register.*;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class MISCTWFContent {
	public static void modConstruct(IEventBus bus) {
		initTags();

		MISCTWFBlocks.init(bus);
		MISCTWFBlockEntities.init(bus);
		MISCTWFItems.init(bus);
		MISCTWFMobEffects.init(bus);
		MISCTWFMenuTypes.init(bus);
	}

	private static void initTags() {
		MISCTWFItemTags.init();
	}

	@SubscribeEvent
	public static void registerPotions(RegistryEvent.Register<Potion> event) {
		MISCTWFBrewingRecipes.init();
	}
}
