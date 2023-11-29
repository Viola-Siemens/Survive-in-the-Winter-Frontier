package com.hexagram2021.misc_twf.client.renderer;

import com.hexagram2021.misc_twf.client.MISCTWFModelLayers;
import com.hexagram2021.misc_twf.client.model.ZombiePolarBearModel;
import com.hexagram2021.misc_twf.common.entity.ZombiePolarBearEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;

@OnlyIn(Dist.CLIENT)
public class ZombiePolarBearRenderer extends MobRenderer<ZombiePolarBearEntity, ZombiePolarBearModel<ZombiePolarBearEntity>> {
	private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(MODID, "textures/entity/zombie_polar_bear.png");

	public ZombiePolarBearRenderer(EntityRendererProvider.Context context) {
		super(context, new ZombiePolarBearModel<>(context.bakeLayer(MISCTWFModelLayers.ZOMBIE_POLAR_BEAR)), 0.9F);
	}

	@Override
	public ResourceLocation getTextureLocation(ZombiePolarBearEntity entity) {
		return TEXTURE_LOCATION;
	}

	@Override
	protected void scale(ZombiePolarBearEntity entity, PoseStack transform, float tick) {
		transform.scale(1.25F, 1.25F, 1.25F);
		super.scale(entity, transform, tick);
	}
}
