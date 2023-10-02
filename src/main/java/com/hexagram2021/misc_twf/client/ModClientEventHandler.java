package com.hexagram2021.misc_twf.client;

import com.hexagram2021.misc_twf.client.model.NightVisionDeviceModel;
import com.hexagram2021.misc_twf.client.renderer.NightVisionDeviceRenderer;
import com.hexagram2021.misc_twf.client.screen.UltravioletLampScreen;
import com.hexagram2021.misc_twf.common.register.MISCTWFItems;
import com.hexagram2021.misc_twf.common.register.MISCTWFMenuTypes;
import net.minecraft.client.gui.screens.MenuScreens;
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
	}

	@SubscribeEvent
	public static void onClientSetup(final FMLClientSetupEvent event) {
		event.enqueueWork(() -> {
			registerContainersAndScreens();
			registerCuriosRenderers();
		});
	}

	private static void registerCuriosRenderers() {
		CuriosRendererRegistry.register(MISCTWFItems.NIGHT_VISION_DEVICE.get(), NightVisionDeviceRenderer::new);
	}

	private static void registerContainersAndScreens() {
		MenuScreens.register(MISCTWFMenuTypes.ULTRAVIOLET_LAMP_MENU.get(), UltravioletLampScreen::new);
	}
}
