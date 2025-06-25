package com.hexagram2021.misc_twf.client.renderer;

import com.hexagram2021.misc_twf.client.model.MonsterEggModel;
import com.hexagram2021.misc_twf.common.block.entity.MonsterEggBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;

public class MonsterEggRenderer extends GeoBlockRenderer<MonsterEggBlockEntity> {
    public MonsterEggRenderer(BlockEntityRendererProvider.Context rendererProvider) {
        super(rendererProvider, new MonsterEggModel());
    }

    @Override
    public RenderType getRenderType(MonsterEggBlockEntity animatable, float partialTick, PoseStack poseStack,
                                    MultiBufferSource bufferSource, VertexConsumer buffer, int packedLight,
                                    ResourceLocation texture) {
        return RenderType.entityTranslucent(getTextureLocation(animatable));
    }
}
