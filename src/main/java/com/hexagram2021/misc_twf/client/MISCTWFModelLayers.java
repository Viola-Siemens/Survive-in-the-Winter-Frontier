package com.hexagram2021.misc_twf.client;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;

@OnlyIn(Dist.CLIENT)
public class MISCTWFModelLayers {
	public static final ModelLayerLocation NIGHT_VISION_DEVICE = register("night_vision_device");

	public static final ModelLayerLocation ZOMBIE_CHICKEN = register("zombie_chicken");
	public static final ModelLayerLocation ZOMBIE_COW = register("zombie_cow");
	public static final ModelLayerLocation ZOMBIE_GOAT = register("zombie_goat");
	public static final ModelLayerLocation ZOMBIE_PIG = register("zombie_pig");
	public static final ModelLayerLocation ZOMBIE_POLAR_BEAR = register("zombie_polar_bear");
	public static final ModelLayerLocation ZOMBIE_RABBIT = register("zombie_rabbit");
	public static final ModelLayerLocation ZOMBIE_SHEEP = register("zombie_sheep");
	public static final ModelLayerLocation ZOMBIE_WOLF = register("zombie_wolf");

	private static ModelLayerLocation register(String name) {
		return register(name, "main");
	}

	@SuppressWarnings("SameParameterValue")
	private static ModelLayerLocation register(String name, String layer) {
		return new ModelLayerLocation(new ResourceLocation(MODID, name), layer);
	}
}
