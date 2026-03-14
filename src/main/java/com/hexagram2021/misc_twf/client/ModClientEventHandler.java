package com.hexagram2021.misc_twf.client;

import com.hexagram2021.misc_twf.client.model.*;
import com.hexagram2021.misc_twf.client.renderer.*;
import com.hexagram2021.misc_twf.common.register.*;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
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
	}

	/**
	 * 注册实体和方块实体的渲染器喵~
	 * 包括所有僵尸动物实体和怪物蛋方块实体的渲染器喵~
	 *
	 * @param event 渲染器注册事件喵~
	 */
	@SubscribeEvent
	public static void onRegisterRenderer(EntityRenderersEvent.RegisterRenderers event) {
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
		event.enqueueWork(ModClientEventHandler::registerCuriosRenderers);
	}

	/**
	 * 注册流体贴图材质喵~
	 * @param event 注册客户端扩展事件喵~
	 */
	@SubscribeEvent
	public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
		event.registerFluidType(new IClientFluidTypeExtensions() {
			@Override
			public ResourceLocation getStillTexture() {
				return ResourceLocation.fromNamespaceAndPath(MODID, "block/fluid/blood_still");
			}

			@Override
			public ResourceLocation getFlowingTexture() {
				return ResourceLocation.fromNamespaceAndPath(MODID, "block/fluid/blood_flowing");
			}
		}, MISCTWFFluids.BLOOD_FLUID.type());
	}

	/**
	 * 注册 Curios 饰品的渲染器喵~
	 */
	private static void registerCuriosRenderers() {
		CuriosRendererRegistry.register(MISCTWFItems.NIGHT_VISION_DEVICE.get(), NightVisionDeviceRenderer::new);
	}

	private ModClientEventHandler() {
	}
}
