package com.hexagram2021.misc_twf.client.renderer;

import com.hexagram2021.misc_twf.client.MISCTWFModelLayers;
import com.hexagram2021.misc_twf.client.model.ZombiePigModel;
import com.hexagram2021.misc_twf.common.entity.ZombieAnimalEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Pig;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;

@OnlyIn(Dist.CLIENT)
public class ZombiePigRenderer extends MobRenderer<ZombieAnimalEntity<Pig>, ZombiePigModel<ZombieAnimalEntity<Pig>>> {
	private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(MODID, "textures/entity/zombie_pig.png");

	public ZombiePigRenderer(EntityRendererProvider.Context context) {
		super(context, new ZombiePigModel<>(context.bakeLayer(MISCTWFModelLayers.ZOMBIE_PIG)), 0.7F);
	}

	@Override
	public ResourceLocation getTextureLocation(ZombieAnimalEntity<Pig> entity) {
		return TEXTURE_LOCATION;
	}

	@Override
	protected boolean isShaking(ZombieAnimalEntity<Pig> entity) {
		return super.isShaking(entity) || entity.isConverting();
	}
}
