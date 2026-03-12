package com.hexagram2021.misc_twf.client.renderer;

import com.hexagram2021.misc_twf.client.MISCTWFModelLayers;
import com.hexagram2021.misc_twf.client.model.ZombieWolfModel;
import com.hexagram2021.misc_twf.common.entity.ZombieAnimalEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Wolf;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;

/**
 * 僵尸狼的实体渲染器喵~
 * 负责渲染僵尸狼的模型和纹理，包含转化时的抖动效果喵~
 *
 * @author liudongyu
 */
@OnlyIn(Dist.CLIENT)
public class ZombieWolfRenderer extends MobRenderer<ZombieAnimalEntity<Wolf>, ZombieWolfModel<ZombieAnimalEntity<Wolf>>> {
	private static final ResourceLocation TEXTURE_LOCATION = ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/zombie_wolf.png");

	public ZombieWolfRenderer(EntityRendererProvider.Context context) {
		super(context, new ZombieWolfModel<>(context.bakeLayer(MISCTWFModelLayers.ZOMBIE_WOLF)), 0.5F);
	}

	@Override
	public ResourceLocation getTextureLocation(ZombieAnimalEntity<Wolf> entity) {
		return TEXTURE_LOCATION;
	}

	@Override
	protected boolean isShaking(ZombieAnimalEntity<Wolf> entity) {
		return super.isShaking(entity) || entity.isConverting();
	}
}
