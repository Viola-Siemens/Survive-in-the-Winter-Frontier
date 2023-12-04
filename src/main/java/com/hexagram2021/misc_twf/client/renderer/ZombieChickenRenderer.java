package com.hexagram2021.misc_twf.client.renderer;

import com.hexagram2021.misc_twf.client.MISCTWFModelLayers;
import com.hexagram2021.misc_twf.client.model.ZombieChickenModel;
import com.hexagram2021.misc_twf.common.entity.ZombieChickenEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;

@OnlyIn(Dist.CLIENT)
public class ZombieChickenRenderer extends MobRenderer<ZombieChickenEntity, ZombieChickenModel<ZombieChickenEntity>> {
	private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(MODID, "textures/entity/zombie_chicken.png");

	public ZombieChickenRenderer(EntityRendererProvider.Context context) {
		super(context, new ZombieChickenModel<>(context.bakeLayer(MISCTWFModelLayers.ZOMBIE_CHICKEN)), 0.3F);
	}

	@Override
	public ResourceLocation getTextureLocation(ZombieChickenEntity entity) {
		return TEXTURE_LOCATION;
	}

	@Override
	protected float getBob(ZombieChickenEntity entity, float partialTick) {
		float flap = Mth.lerp(partialTick, entity.oFlap, entity.flap);
		float flapSpeed = Mth.lerp(partialTick, entity.oFlapSpeed, entity.flapSpeed);
		return (Mth.sin(flap) + 1.0F) * flapSpeed;
	}

	@Override
	protected boolean isShaking(ZombieChickenEntity entity) {
		return super.isShaking(entity) || entity.isConverting();
	}
}
