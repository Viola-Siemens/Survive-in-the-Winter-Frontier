package com.hexagram2021.misc_twf.client;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;

public class MISCTWFModelLayers {
	public static final ModelLayerLocation NIGHT_VISION_DEVICE = register("night_vision_device");

	@SuppressWarnings("SameParameterValue")
	private static ModelLayerLocation register(String name) {
		return register(name, "main");
	}

	@SuppressWarnings("SameParameterValue")
	private static ModelLayerLocation register(String name, String layer) {
		return new ModelLayerLocation(new ResourceLocation(MODID, name), layer);
	}
}
