package com.hexagram2021.misc_twf_industry.client;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import static com.hexagram2021.misc_twf_industry.MiscTwfIndustry.CONTENT_NAMESPACE;

/**
 * 工业生态模块客户端模型层注册类喵~
 *
 * @author liudongyu
 */
@OnlyIn(Dist.CLIENT)
public final class MISCTWFModelLayers {
	/** 夜视仪模型层喵~ */
	public static final ModelLayerLocation NIGHT_VISION_DEVICE = new ModelLayerLocation(
			ResourceLocation.fromNamespaceAndPath(CONTENT_NAMESPACE, "night_vision_device"), "main"
	);

	private MISCTWFModelLayers() {
	}
}
