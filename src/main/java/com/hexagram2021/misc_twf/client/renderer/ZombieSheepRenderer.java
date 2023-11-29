package com.hexagram2021.misc_twf.client.renderer;

import com.hexagram2021.misc_twf.client.MISCTWFModelLayers;
import com.hexagram2021.misc_twf.client.model.ZombieSheepModel;
import com.hexagram2021.misc_twf.common.entity.ZombieSheepEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;

@OnlyIn(Dist.CLIENT)
public class ZombieSheepRenderer extends MobRenderer<ZombieSheepEntity, ZombieSheepModel<ZombieSheepEntity>> {
	private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(MODID, "textures/entity/zombie_sheep.png");

	public ZombieSheepRenderer(EntityRendererProvider.Context context) {
		super(context, new ZombieSheepModel<>(context.bakeLayer(MISCTWFModelLayers.ZOMBIE_SHEEP)), 0.7F);
	}

	@Override
	public ResourceLocation getTextureLocation(ZombieSheepEntity entity) {
		return TEXTURE_LOCATION;
	}
}
