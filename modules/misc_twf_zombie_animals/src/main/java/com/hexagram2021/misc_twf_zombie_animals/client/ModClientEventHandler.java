package com.hexagram2021.misc_twf_zombie_animals.client;

import com.hexagram2021.misc_twf_zombie_animals.client.model.ZombieChickenModel;
import com.hexagram2021.misc_twf_zombie_animals.client.model.ZombieCowModel;
import com.hexagram2021.misc_twf_zombie_animals.client.model.ZombieGoatModel;
import com.hexagram2021.misc_twf_zombie_animals.client.model.ZombiePigModel;
import com.hexagram2021.misc_twf_zombie_animals.client.model.ZombiePolarBearModel;
import com.hexagram2021.misc_twf_zombie_animals.client.model.ZombieRabbitModel;
import com.hexagram2021.misc_twf_zombie_animals.client.model.ZombieSheepModel;
import com.hexagram2021.misc_twf_zombie_animals.client.model.ZombieWolfModel;
import com.hexagram2021.misc_twf_zombie_animals.client.renderer.ZombieChickenRenderer;
import com.hexagram2021.misc_twf_zombie_animals.client.renderer.ZombieCowRenderer;
import com.hexagram2021.misc_twf_zombie_animals.client.renderer.ZombieGoatRenderer;
import com.hexagram2021.misc_twf_zombie_animals.client.renderer.ZombiePigRenderer;
import com.hexagram2021.misc_twf_zombie_animals.client.renderer.ZombiePolarBearRenderer;
import com.hexagram2021.misc_twf_zombie_animals.client.renderer.ZombieRabbitRenderer;
import com.hexagram2021.misc_twf_zombie_animals.client.renderer.ZombieSheepRenderer;
import com.hexagram2021.misc_twf_zombie_animals.client.renderer.ZombieWolfRenderer;
import com.hexagram2021.misc_twf_zombie_animals.common.register.MISCTWFEntities;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

import static com.hexagram2021.misc_twf_zombie_animals.MiscTwfZombieAnimals.MODID;

/**
 * 僵尸动物模块客户端事件处理器喵~
 *
 * <p>负责注册僵尸动物实体的模型层定义与实体渲染器喵~</p>
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
	 * 注册实体渲染器喵~
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
	}

	private ModClientEventHandler() {
	}
}
