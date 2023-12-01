package com.hexagram2021.misc_twf.client.model;

import com.hexagram2021.misc_twf.common.entity.ZombiePolarBearEntity;
import net.minecraft.client.model.QuadrupedModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ZombiePolarBearModel<T extends ZombiePolarBearEntity> extends QuadrupedModel<T> {
	public ZombiePolarBearModel(ModelPart root) {
		super(root, true, 16.0F, 4.0F, 2.25F, 2.0F, 24);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition root = meshdefinition.getRoot();
		root.addOrReplaceChild(
				"head",
				CubeListBuilder.create()
						.texOffs(0, 0).addBox(-3.5F, -3.0F, -3.0F, 7.0F, 7.0F, 7.0F)
						.texOffs(0, 44).addBox("mouth", -2.5F, 1.0F, -6.0F, 5.0F, 3.0F, 3.0F)
						.texOffs(26, 0).addBox("right_ear", -4.5F, -4.0F, -1.0F, 2.0F, 2.0F, 1.0F)
						.texOffs(26, 0).mirror().addBox("left_ear", 2.5F, -4.0F, -1.0F, 2.0F, 2.0F, 1.0F),
				PartPose.offset(0.0F, 10.0F, -16.0F)
		);
		root.addOrReplaceChild(
				"body",
				CubeListBuilder.create()
						.texOffs(0, 19).addBox(-5.0F, -13.0F, -7.0F, 14.0F, 14.0F, 11.0F)
						.texOffs(39, 0).addBox(-4.0F, -25.0F, -7.0F, 12.0F, 12.0F, 10.0F),
				PartPose.offsetAndRotation(-2.0F, 9.0F, 12.0F, Mth.HALF_PI, 0.0F, 0.0F)
		);
		CubeListBuilder hindLegBuilder = CubeListBuilder.create().texOffs(50, 22).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 10.0F, 8.0F);
		root.addOrReplaceChild("right_hind_leg", hindLegBuilder, PartPose.offset(-4.5F, 14.0F, 6.0F));
		root.addOrReplaceChild("left_hind_leg", hindLegBuilder, PartPose.offset(4.5F, 14.0F, 6.0F));
		CubeListBuilder frontLegBuilder = CubeListBuilder.create().texOffs(50, 40).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 10.0F, 6.0F);
		root.addOrReplaceChild("right_front_leg", frontLegBuilder, PartPose.offset(-3.5F, 14.0F, -8.0F));
		root.addOrReplaceChild("left_front_leg", frontLegBuilder, PartPose.offset(3.5F, 14.0F, -8.0F));
		return LayerDefinition.create(meshdefinition, 128, 64);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
		float alpha = ageInTicks - (float)entity.tickCount;
		float scale = entity.getStandingAnimationScale(alpha);
		scale *= scale;
		float inverted = 1.0F - scale;
		this.body.xRot = Mth.HALF_PI - scale * Mth.PI * 0.35F;
		this.body.y = 9.0F * inverted + 11.0F * scale;
		this.rightFrontLeg.y = 14.0F * inverted - 6.0F * scale;
		this.rightFrontLeg.z = -8.0F * inverted - 4.0F * scale;
		this.rightFrontLeg.xRot -= scale * Mth.PI * 0.45F;
		this.leftFrontLeg.y = this.rightFrontLeg.y;
		this.leftFrontLeg.z = this.rightFrontLeg.z;
		this.leftFrontLeg.xRot -= scale * Mth.PI * 0.45F;
		if (this.young) {
			this.head.y = 10.0F * inverted - 9.0F * scale;
			this.head.z = -16.0F * inverted - 7.0F * scale;
		} else {
			this.head.y = 10.0F * inverted - 14.0F * scale;
			this.head.z = -16.0F * inverted - 3.0F * scale;
		}

		this.head.xRot += scale * Mth.PI * 0.15F;
	}
}
