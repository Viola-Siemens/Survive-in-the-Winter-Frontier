package com.hexagram2021.misc_twf.client;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;

/**
 * 模组客户端模型层注册类，定义所有自定义实体和装备的模型层位置喵~
 * 包含夜视仪和各种僵尸动物实体的模型层喵~
 *
 * @author liudongyu
 */
@OnlyIn(Dist.CLIENT)
public class MISCTWFModelLayers {
	/** 夜视仪模型层喵~ */
	public static final ModelLayerLocation NIGHT_VISION_DEVICE = register("night_vision_device");

	/** 僵尸鸡模型层喵~ */
	public static final ModelLayerLocation ZOMBIE_CHICKEN = register("zombie_chicken");
	/** 僵尸牛模型层喵~ */
	public static final ModelLayerLocation ZOMBIE_COW = register("zombie_cow");
	/** 僵尸山羊模型层喵~ */
	public static final ModelLayerLocation ZOMBIE_GOAT = register("zombie_goat");
	/** 僵尸猪模型层喵~ */
	public static final ModelLayerLocation ZOMBIE_PIG = register("zombie_pig");
	/** 僵尸北极熊模型层喵~ */
	public static final ModelLayerLocation ZOMBIE_POLAR_BEAR = register("zombie_polar_bear");
	/** 僵尸兔子模型层喵~ */
	public static final ModelLayerLocation ZOMBIE_RABBIT = register("zombie_rabbit");
	/** 僵尸绵羊模型层喵~ */
	public static final ModelLayerLocation ZOMBIE_SHEEP = register("zombie_sheep");
	/** 僵尸狼模型层喵~ */
	public static final ModelLayerLocation ZOMBIE_WOLF = register("zombie_wolf");

	private static ModelLayerLocation register(String name) {
		return register(name, "main");
	}

	@SuppressWarnings("SameParameterValue")
	private static ModelLayerLocation register(String name, String layer) {
		return new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(MODID, name), layer);
	}
}
