package com.hexagram2021.misc_twf_zombie_animals.client.renderer;

import com.hexagram2021.misc_twf_zombie_animals.client.MISCTWFModelLayers;
import com.hexagram2021.misc_twf_zombie_animals.client.model.ZombiePolarBearModel;
import com.hexagram2021.misc_twf_zombie_animals.common.entity.ZombiePolarBearEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import static com.hexagram2021.misc_twf_zombie_animals.MiscTwfZombieAnimals.CONTENT_NAMESPACE;

/**
 * 僵尸北极熊的实体渲染器喵~
 * 负责渲染僵尸北极熊的模型和纹理，包含 1.25 倍体型缩放和转化时的抖动效果喵~
 *
 * @author liudongyu
 */
@OnlyIn(Dist.CLIENT)
public class ZombiePolarBearRenderer extends MobRenderer<ZombiePolarBearEntity, ZombiePolarBearModel<ZombiePolarBearEntity>> {
	private static final ResourceLocation TEXTURE_LOCATION = ResourceLocation.fromNamespaceAndPath(CONTENT_NAMESPACE, "textures/entity/zombie_polar_bear.png");

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

	@Override
	protected boolean isShaking(ZombiePolarBearEntity entity) {
		return super.isShaking(entity) || entity.isConverting();
	}
}
