package com.hexagram2021.misc_twf_zombie_animals.client.renderer;

import com.hexagram2021.misc_twf_zombie_animals.client.MISCTWFModelLayers;
import com.hexagram2021.misc_twf_zombie_animals.client.model.ZombieSheepModel;
import com.hexagram2021.misc_twf_zombie_animals.common.entity.ZombieSheepEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import static com.hexagram2021.misc_twf_zombie_animals.MiscTwfZombieAnimals.CONTENT_NAMESPACE;

/**
 * 僵尸绵羊的实体渲染器喵~
 * 负责渲染僵尸绵羊的模型和纹理，包含转化时的抖动效果喵~
 *
 * @author liudongyu
 */
@OnlyIn(Dist.CLIENT)
public class ZombieSheepRenderer extends MobRenderer<ZombieSheepEntity, ZombieSheepModel<ZombieSheepEntity>> {
	private static final ResourceLocation TEXTURE_LOCATION = ResourceLocation.fromNamespaceAndPath(CONTENT_NAMESPACE, "textures/entity/zombie_sheep.png");

	public ZombieSheepRenderer(EntityRendererProvider.Context context) {
		super(context, new ZombieSheepModel<>(context.bakeLayer(MISCTWFModelLayers.ZOMBIE_SHEEP)), 0.7F);
	}

	@Override
	public ResourceLocation getTextureLocation(ZombieSheepEntity entity) {
		return TEXTURE_LOCATION;
	}

	@Override
	protected boolean isShaking(ZombieSheepEntity entity) {
		return super.isShaking(entity) || entity.isConverting();
	}
}
