package com.hexagram2021.misc_twf.client;

import com.hexagram2021.misc_twf.client.model.*;
import com.hexagram2021.misc_twf.client.renderer.*;
import com.hexagram2021.misc_twf.client.screen.*;
import com.hexagram2021.misc_twf.common.infrastructure.compat.ModCreateCompat;
import com.hexagram2021.misc_twf.common.register.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;

/**
 * 客户端事件处理器喵~
 * 负责注册客户端专用的渲染器、模型层、屏幕等内容喵~
 *
 * @author liudongyu
 */
@EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
public final class ModClientEventHandler {
	/**
	 * 注册实体模型层定义喵~
	 * 包括夜视设备饰品和所有僵尸动物实体的模型层喵~
	 *
	 * @param event 模型层注册事件喵~
	 */
	@SubscribeEvent
	public static void onRegisterLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
		event.registerLayerDefinition(MISCTWFModelLayers.NIGHT_VISION_DEVICE, NightVisionDeviceModel::createBodyLayer);

		event.registerLayerDefinition(MISCTWFModelLayers.ZOMBIE_CHICKEN, ZombieChickenModel::createBodyLayer);
		event.registerLayerDefinition(MISCTWFModelLayers.ZOMBIE_COW, ZombieCowModel::createBodyLayer);
		event.registerLayerDefinition(MISCTWFModelLayers.ZOMBIE_GOAT, ZombieGoatModel::createBodyLayer);
		event.registerLayerDefinition(MISCTWFModelLayers.ZOMBIE_PIG, ZombiePigModel::createBodyLayer);
		event.registerLayerDefinition(MISCTWFModelLayers.ZOMBIE_POLAR_BEAR, ZombiePolarBearModel::createBodyLayer);
		event.registerLayerDefinition(MISCTWFModelLayers.ZOMBIE_RABBIT, ZombieRabbitModel::createBodyLayer);
		event.registerLayerDefinition(MISCTWFModelLayers.ZOMBIE_SHEEP, ZombieSheepModel::createBodyLayer);
		event.registerLayerDefinition(MISCTWFModelLayers.ZOMBIE_WOLF, ZombieWolfModel::createBodyLayer);
	}

	/**
	 * 注册实体和方块实体的渲染器喵~
	 * 包括所有僵尸动物实体和怪物蛋方块实体的渲染器喵~
	 *
	 * @param event 渲染器注册事件喵~
	 */
	@SubscribeEvent
	public static void onRegisterRenderer(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(MISCTWFEntities.ZOMBIE_CHICKEN.get(), ZombieChickenRenderer::new);
		event.registerEntityRenderer(MISCTWFEntities.ZOMBIE_COW.get(), ZombieCowRenderer::new);
		event.registerEntityRenderer(MISCTWFEntities.ZOMBIE_GOAT.get(), ZombieGoatRenderer::new);
		event.registerEntityRenderer(MISCTWFEntities.ZOMBIE_PIG.get(), ZombiePigRenderer::new);
		event.registerEntityRenderer(MISCTWFEntities.ZOMBIE_POLAR_BEAR.get(), ZombiePolarBearRenderer::new);
		event.registerEntityRenderer(MISCTWFEntities.ZOMBIE_RABBIT.get(), ZombieRabbitRenderer::new);
		event.registerEntityRenderer(MISCTWFEntities.ZOMBIE_SHEEP.get(), ZombieSheepRenderer::new);
		event.registerEntityRenderer(MISCTWFEntities.ZOMBIE_WOLF.get(), ZombieWolfRenderer::new);
		event.registerBlockEntityRenderer(MISCTWFBlockEntities.MONSTER_EGG.get(), MonsterEggRenderer::new);
	}

	/**
	 * 客户端设置事件喵~
	 * 在客户端初始化时注册模组兼容和 Curios 渲染器喵~
	 *
	 * @param event 客户端设置事件喵~
	 */
	@SubscribeEvent
	public static void onClientSetup(final FMLClientSetupEvent event) {
		ModCreateCompat.register();
		event.enqueueWork(ModClientEventHandler::registerCuriosRenderers);
	}

	/**
	 * 注册 Curios 饰品的渲染器喵~
	 */
	private static void registerCuriosRenderers() {
		CuriosRendererRegistry.register(MISCTWFItems.NIGHT_VISION_DEVICE.get(), NightVisionDeviceRenderer::new);
	}

	/**
	 * 注册容器菜单对应的屏幕喵~
	 *
	 * @param event 菜单屏幕注册事件喵~
	 */
	@SubscribeEvent
	private static void registerContainersAndScreens(RegisterMenuScreensEvent event) {
		event.register(MISCTWFMenuTypes.ULTRAVIOLET_LAMP_MENU.get(), UltravioletLampScreen::new);
		event.register(MISCTWFMenuTypes.TRAVELERS_BACKPACK_BLOCK_ENTITY_TAC_SLOT_MENU.get(), TravelersBackpackTacScreen::new);
		event.register(MISCTWFMenuTypes.TRAVELERS_BACKPACK_ITEM_TAC_SLOT_MENU.get(), TravelersBackpackTacScreen::new);
		event.register(MISCTWFMenuTypes.MOLD_WORKBENCH_MENU.get(), MoldWorkbenchScreen::new);
		event.register(MISCTWFMenuTypes.RECOVERY_FURNACE.get(), RecoveryFurnaceScreen::new);
	}

	private ModClientEventHandler() {
	}
}
