package com.hexagram2021.misc_twf.client.renderer;

import com.hexagram2021.misc_twf.client.MISCTWFModelLayers;
import com.hexagram2021.misc_twf.client.model.ZombieGoatModel;
import com.hexagram2021.misc_twf.common.entity.ZombieGoatEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;

@OnlyIn(Dist.CLIENT)
public class ZombieGoatRenderer extends MobRenderer<ZombieGoatEntity, ZombieGoatModel<ZombieGoatEntity>> {
	private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(MODID, "textures/entity/zombie_goat.png");

	public ZombieGoatRenderer(EntityRendererProvider.Context context) {
		super(context, new ZombieGoatModel<>(context.bakeLayer(MISCTWFModelLayers.ZOMBIE_GOAT)), 0.7F);
	}

	@Override
	public ResourceLocation getTextureLocation(ZombieGoatEntity entity) {
		return TEXTURE_LOCATION;
	}

	@Override
	protected boolean isShaking(ZombieGoatEntity entity) {
		return super.isShaking(entity) || entity.isConverting();
	}
}
