package com.hexagram2021.misc_twf_zombie_animals.client.renderer;

import com.hexagram2021.misc_twf_zombie_animals.client.MISCTWFModelLayers;
import com.hexagram2021.misc_twf_zombie_animals.client.model.ZombieCowModel;
import com.hexagram2021.misc_twf_zombie_animals.common.entity.ZombieAnimalEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Cow;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import static com.hexagram2021.misc_twf_zombie_animals.MiscTwfZombieAnimals.CONTENT_NAMESPACE;

/**
 * 僵尸牛的实体渲染器喵~
 * 负责渲染僵尸牛的模型和纹理，包含转化时的抖动效果喵~
 *
 * @author liudongyu
 */
@OnlyIn(Dist.CLIENT)
public class ZombieCowRenderer extends MobRenderer<ZombieAnimalEntity<Cow>, ZombieCowModel<ZombieAnimalEntity<Cow>>> {
	private static final ResourceLocation TEXTURE_LOCATION = ResourceLocation.fromNamespaceAndPath(CONTENT_NAMESPACE, "textures/entity/zombie_cow.png");

	public ZombieCowRenderer(EntityRendererProvider.Context context) {
		super(context, new ZombieCowModel<>(context.bakeLayer(MISCTWFModelLayers.ZOMBIE_COW)), 0.7F);
	}

	@Override
	public ResourceLocation getTextureLocation(ZombieAnimalEntity<Cow> entity) {
		return TEXTURE_LOCATION;
	}

	@Override
	protected boolean isShaking(ZombieAnimalEntity<Cow> entity) {
		return super.isShaking(entity) || entity.isConverting();
	}
}
