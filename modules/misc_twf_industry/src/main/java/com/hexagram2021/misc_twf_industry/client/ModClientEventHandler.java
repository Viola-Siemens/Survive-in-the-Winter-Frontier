package com.hexagram2021.misc_twf_industry.client;

import com.hexagram2021.misc_twf_industry.client.model.NightVisionDeviceModel;
import com.hexagram2021.misc_twf_industry.client.renderer.NightVisionDeviceRenderer;
import com.hexagram2021.misc_twf_industry.client.screen.MoldWorkbenchScreen;
import com.hexagram2021.misc_twf_industry.client.screen.RecoveryFurnaceScreen;
import com.hexagram2021.misc_twf_industry.client.screen.UltravioletLampScreen;
import com.hexagram2021.misc_twf_industry.common.register.MISCTWFItems;
import com.hexagram2021.misc_twf_industry.common.register.MISCTWFMenuTypes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

import static com.hexagram2021.misc_twf_industry.MiscTwfIndustry.MODID;

/**
 * 工业生态模块客户端事件处理器喵~
 *
 * @author liudongyu
 */
@EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
public final class ModClientEventHandler {
	/**
	 * 注册实体模型层定义喵~
	 *
	 * @param event 模型层注册事件喵~
	 */
	@SubscribeEvent
	public static void onRegisterLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
		event.registerLayerDefinition(MISCTWFModelLayers.NIGHT_VISION_DEVICE, NightVisionDeviceModel::createBodyLayer);
	}

	/**
	 * 客户端设置事件，注册 Curios 饰品渲染器喵~
	 *
	 * @param event 客户端设置事件喵~
	 */
	@SubscribeEvent
	public static void onClientSetup(final FMLClientSetupEvent event) {
		event.enqueueWork(() -> CuriosRendererRegistry.register(MISCTWFItems.NIGHT_VISION_DEVICE.get(), NightVisionDeviceRenderer::new));
	}

	/**
	 * 注册容器菜单对应的屏幕喵~
	 *
	 * @param event 菜单屏幕注册事件喵~
	 */
	@SubscribeEvent
	public static void registerContainersAndScreens(RegisterMenuScreensEvent event) {
		event.register(MISCTWFMenuTypes.ULTRAVIOLET_LAMP_MENU.get(), UltravioletLampScreen::new);
		event.register(MISCTWFMenuTypes.MOLD_WORKBENCH_MENU.get(), MoldWorkbenchScreen::new);
		event.register(MISCTWFMenuTypes.RECOVERY_FURNACE_MENU.get(), RecoveryFurnaceScreen::new);
	}

	private ModClientEventHandler() {
	}
}
