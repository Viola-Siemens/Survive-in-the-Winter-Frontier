package com.hexagram2021.misc_twf_zombie_animals.client.renderer;

import com.hexagram2021.misc_twf_zombie_animals.client.MISCTWFModelLayers;
import com.hexagram2021.misc_twf_zombie_animals.client.model.ZombieRabbitModel;
import com.hexagram2021.misc_twf_zombie_animals.common.entity.ZombieRabbitEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import static com.hexagram2021.misc_twf_zombie_animals.MiscTwfZombieAnimals.CONTENT_NAMESPACE;

/**
 * 僵尸兔子的实体渲染器喵~
 * 负责渲染僵尸兔子的模型和纹理，包含转化时的抖动效果喵~
 *
 * @author liudongyu
 */
@OnlyIn(Dist.CLIENT)
public class ZombieRabbitRenderer extends MobRenderer<ZombieRabbitEntity, ZombieRabbitModel<ZombieRabbitEntity>> {
	private static final ResourceLocation TEXTURE_LOCATION = ResourceLocation.fromNamespaceAndPath(CONTENT_NAMESPACE, "textures/entity/zombie_rabbit.png");

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
