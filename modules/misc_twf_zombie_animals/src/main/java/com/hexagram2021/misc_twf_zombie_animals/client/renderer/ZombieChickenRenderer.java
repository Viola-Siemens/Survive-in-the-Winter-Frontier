package com.hexagram2021.misc_twf_zombie_animals.client.renderer;

import com.hexagram2021.misc_twf_zombie_animals.client.MISCTWFModelLayers;
import com.hexagram2021.misc_twf_zombie_animals.client.model.ZombieChickenModel;
import com.hexagram2021.misc_twf_zombie_animals.common.entity.ZombieChickenEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import static com.hexagram2021.misc_twf_zombie_animals.MiscTwfZombieAnimals.CONTENT_NAMESPACE;

/**
 * 僵尸鸡的实体渲染器喵~
 * 负责渲染僵尸鸡的模型和纹理，包含翅膀拍打动画和转化时的抖动效果喵~
 *
 * @author liudongyu
 */
@OnlyIn(Dist.CLIENT)
public class ZombieChickenRenderer extends MobRenderer<ZombieChickenEntity, ZombieChickenModel<ZombieChickenEntity>> {
	private static final ResourceLocation TEXTURE_LOCATION = ResourceLocation.fromNamespaceAndPath(CONTENT_NAMESPACE, "textures/entity/zombie_chicken.png");

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
