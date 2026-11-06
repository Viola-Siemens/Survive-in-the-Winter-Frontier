package com.hexagram2021.misc_twf_adventure.client;

import com.hexagram2021.misc_twf_adventure.client.renderer.MonsterEggRenderer;
import com.hexagram2021.misc_twf_adventure.client.screen.TravelersBackpackTacScreen;
import com.hexagram2021.misc_twf_adventure.common.register.MISCTWFBlockEntities;
import com.hexagram2021.misc_twf_adventure.common.register.MISCTWFFluids;
import com.hexagram2021.misc_twf_adventure.common.register.MISCTWFMenuTypes;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

import static com.hexagram2021.misc_twf_adventure.MiscTwfAdventure.CONTENT_NAMESPACE;
import static com.hexagram2021.misc_twf_adventure.MiscTwfAdventure.MODID;

/**
 * 客户端事件处理器喵~
 * 负责注册客户端专用的渲染器、模型层、屏幕等内容喵~
 *
 * @author liudongyu
 */
@EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
public final class ModClientEventHandler {
	/**
	 * 注册实体和方块实体的渲染器喵~
	 * 注册怪物蛋方块实体等渲染器喵~
	 *
	 * @param event 渲染器注册事件喵~
	 */
	@SubscribeEvent
	public static void onRegisterRenderer(EntityRenderersEvent.RegisterRenderers event) {
		event.registerBlockEntityRenderer(MISCTWFBlockEntities.MONSTER_EGG.get(), MonsterEggRenderer::new);
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
				return ResourceLocation.fromNamespaceAndPath(CONTENT_NAMESPACE, "block/fluid/blood_still");
			}

			@Override
			public ResourceLocation getFlowingTexture() {
				return ResourceLocation.fromNamespaceAndPath(CONTENT_NAMESPACE, "block/fluid/blood_flowing");
			}
		}, MISCTWFFluids.BLOOD_FLUID.type());
	}


	/**
	 * 注册容器菜单对应的屏幕喵~
	 *
	 * @param event 菜单屏幕注册事件喵~
	 */
	@SubscribeEvent
	private static void registerContainersAndScreens(RegisterMenuScreensEvent event) {
		event.register(MISCTWFMenuTypes.TRAVELERS_BACKPACK_BLOCK_ENTITY_TAC_SLOT_MENU.get(), TravelersBackpackTacScreen::new);
		event.register(MISCTWFMenuTypes.TRAVELERS_BACKPACK_ITEM_TAC_SLOT_MENU.get(), TravelersBackpackTacScreen::new);
	}

	private ModClientEventHandler() {
	}
}
