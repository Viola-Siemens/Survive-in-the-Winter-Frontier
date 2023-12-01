package com.hexagram2021.misc_twf.client;

import com.hexagram2021.misc_twf.client.model.*;
import com.hexagram2021.misc_twf.client.renderer.*;
import com.hexagram2021.misc_twf.client.screen.TravelersBackpackTacScreen;
import com.hexagram2021.misc_twf.client.screen.UltravioletLampScreen;
import com.hexagram2021.misc_twf.common.register.MISCTWFBlocks;
import com.hexagram2021.misc_twf.common.register.MISCTWFEntities;
import com.hexagram2021.misc_twf.common.register.MISCTWFItems;
import com.hexagram2021.misc_twf.common.register.MISCTWFMenuTypes;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModClientEventHandler {
	@SubscribeEvent
	public static void onRegisterLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
		event.registerLayerDefinition(MISCTWFModelLayers.NIGHT_VISION_DEVICE, NightVisionDeviceModel::createBodyLayer);

		event.registerLayerDefinition(MISCTWFModelLayers.ZOMBIE_CHICKEN, ZombieChickenModel::createBodyLayer);
		event.registerLayerDefinition(MISCTWFModelLayers.ZOMBIE_COW, ZombieCowModel::createBodyLayer);
		event.registerLayerDefinition(MISCTWFModelLayers.ZOMBIE_POLAR_BEAR, ZombiePolarBearModel::createBodyLayer);
		event.registerLayerDefinition(MISCTWFModelLayers.ZOMBIE_RABBIT, ZombieRabbitModel::createBodyLayer);
		event.registerLayerDefinition(MISCTWFModelLayers.ZOMBIE_SHEEP, ZombieSheepModel::createBodyLayer);
	}

	@SubscribeEvent
	public static void onRegisterRenderer(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(MISCTWFEntities.ZOMBIE_CHICKEN.get(), ZombieChickenRenderer::new);
		event.registerEntityRenderer(MISCTWFEntities.ZOMBIE_COW.get(), ZombieCowRenderer::new);
		event.registerEntityRenderer(MISCTWFEntities.ZOMBIE_POLAR_BEAR.get(), ZombiePolarBearRenderer::new);
		event.registerEntityRenderer(MISCTWFEntities.ZOMBIE_RABBIT.get(), ZombieRabbitRenderer::new);
		event.registerEntityRenderer(MISCTWFEntities.ZOMBIE_SHEEP.get(), ZombieSheepRenderer::new);
	}

	@SubscribeEvent
	public static void onClientSetup(final FMLClientSetupEvent event) {
		event.enqueueWork(() -> {
			registerRenderLayers();
			registerContainersAndScreens();
			registerCuriosRenderers();
		});
	}

	private static void registerRenderLayers() {
		ItemBlockRenderTypes.setRenderLayer(MISCTWFBlocks.ULTRAVIOLET_LAMP.get(), RenderType.translucent());
	}

	private static void registerCuriosRenderers() {
		CuriosRendererRegistry.register(MISCTWFItems.NIGHT_VISION_DEVICE.get(), NightVisionDeviceRenderer::new);
	}

	private static void registerContainersAndScreens() {
		MenuScreens.register(MISCTWFMenuTypes.ULTRAVIOLET_LAMP_MENU.get(), UltravioletLampScreen::new);
		MenuScreens.register(MISCTWFMenuTypes.TRAVELERS_BACKPACK_BLOCK_ENTITY_TAC_SLOT_MENU.get(), TravelersBackpackTacScreen::new);
		MenuScreens.register(MISCTWFMenuTypes.TRAVELERS_BACKPACK_ITEM_TAC_SLOT_MENU.get(), TravelersBackpackTacScreen::new);
	}
}
