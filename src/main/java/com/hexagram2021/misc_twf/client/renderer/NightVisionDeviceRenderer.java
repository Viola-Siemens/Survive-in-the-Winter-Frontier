package com.hexagram2021.misc_twf.client.renderer;

import com.hexagram2021.misc_twf.client.MISCTWFModelLayers;
import com.hexagram2021.misc_twf.client.model.NightVisionDeviceModel;
import com.hexagram2021.misc_twf.common.item.NightVisionDeviceItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

import java.util.Objects;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;

@OnlyIn(Dist.CLIENT)
public class NightVisionDeviceRenderer implements ICurioRenderer {
	private final NightVisionDeviceModel model;

	public NightVisionDeviceRenderer() {
		this.model = new NightVisionDeviceModel(Minecraft.getInstance().getEntityModels().bakeLayer(MISCTWFModelLayers.NIGHT_VISION_DEVICE));
	}

	@Override
	public <T extends LivingEntity, M extends EntityModel<T>> void render(ItemStack stack, SlotContext slotContext,
																		  PoseStack transform, RenderLayerParent<T, M> renderLayerParent,MultiBufferSource renderTypeBuffer,
																		  int light, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks,
																		  float netHeadYaw, float headPitch) {
		if(stack.getItem() instanceof NightVisionDeviceItem curiosItem) {
			followBodyRotations(slotContext.entity(), this.model);
			VertexConsumer vertexConsumer = renderTypeBuffer.getBuffer(RenderType.entityCutout(new ResourceLocation(
					MODID, "textures/models/" + Objects.requireNonNull(curiosItem.getRegistryName()).getPath() + ".png"
			)));
			this.model.renderToBuffer(transform, vertexConsumer, light, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
		}
	}

	@SuppressWarnings("unchecked")
	static void followBodyRotations(LivingEntity livingEntity, NightVisionDeviceModel model) {

		EntityRenderer<? super LivingEntity> render = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(livingEntity);

		if (render instanceof LivingEntityRenderer) {
			LivingEntityRenderer<LivingEntity, EntityModel<LivingEntity>> livingRenderer = (LivingEntityRenderer<LivingEntity, EntityModel<LivingEntity>>) render;
			EntityModel<LivingEntity> entityModel = livingRenderer.getModel();

			if (entityModel instanceof HumanoidModel<LivingEntity> bipedModel) {
				model.hat.copyFrom(bipedModel.hat);
			}
		}
	}
}
