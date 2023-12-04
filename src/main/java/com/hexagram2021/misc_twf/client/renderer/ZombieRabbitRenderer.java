package com.hexagram2021.misc_twf.client.renderer;

import com.hexagram2021.misc_twf.client.MISCTWFModelLayers;
import com.hexagram2021.misc_twf.client.model.ZombieRabbitModel;
import com.hexagram2021.misc_twf.common.entity.ZombieRabbitEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;

@OnlyIn(Dist.CLIENT)
public class ZombieRabbitRenderer extends MobRenderer<ZombieRabbitEntity, ZombieRabbitModel<ZombieRabbitEntity>> {
	private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(MODID, "textures/entity/zombie_rabbit.png");

	public ZombieRabbitRenderer(EntityRendererProvider.Context context) {
		super(context, new ZombieRabbitModel<>(context.bakeLayer(MISCTWFModelLayers.ZOMBIE_RABBIT)), 0.3F);
	}

	@Override
	public ResourceLocation getTextureLocation(ZombieRabbitEntity entity) {
		return TEXTURE_LOCATION;
	}

	@Override
	protected boolean isShaking(ZombieRabbitEntity entity) {
		return super.isShaking(entity) || entity.isConverting();
	}
}
