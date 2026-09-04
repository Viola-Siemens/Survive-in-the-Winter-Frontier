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
public final class MISCTWFModelLayers {
	/** 夜视仪模型层喵~ */
	public static final ModelLayerLocation NIGHT_VISION_DEVICE = register("night_vision_device");

	private static ModelLayerLocation register(String name) {
		return register(name, "main");
	}

	@SuppressWarnings("SameParameterValue")
	private static ModelLayerLocation register(String name, String layer) {
		return new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(MODID, name), layer);
	}

	private MISCTWFModelLayers() {
	}
}
