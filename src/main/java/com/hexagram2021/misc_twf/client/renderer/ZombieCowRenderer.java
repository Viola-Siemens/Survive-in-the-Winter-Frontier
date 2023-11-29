package com.hexagram2021.misc_twf.client.renderer;

import com.hexagram2021.misc_twf.client.MISCTWFModelLayers;
import com.hexagram2021.misc_twf.client.model.ZombieCowModel;
import com.hexagram2021.misc_twf.client.model.ZombieSheepModel;
import com.hexagram2021.misc_twf.common.entity.ZombieAnimalEntity;
import com.hexagram2021.misc_twf.common.entity.ZombieSheepEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Cow;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;

@OnlyIn(Dist.CLIENT)
public class ZombieCowRenderer extends MobRenderer<ZombieAnimalEntity<Cow>, ZombieCowModel<ZombieAnimalEntity<Cow>>> {
	private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(MODID, "textures/entity/zombie_cow.png");

	public ZombieCowRenderer(EntityRendererProvider.Context context) {
		super(context, new ZombieCowModel<>(context.bakeLayer(MISCTWFModelLayers.ZOMBIE_COW)), 0.7F);
	}

	@Override
	public ResourceLocation getTextureLocation(ZombieAnimalEntity<Cow> entity) {
		return TEXTURE_LOCATION;
	}
}
