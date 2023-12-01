package com.hexagram2021.misc_twf.client.model;

import com.google.common.collect.ImmutableList;
import com.hexagram2021.misc_twf.common.entity.ZombieRabbitEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
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
public class ZombieRabbitModel<T extends ZombieRabbitEntity> extends EntityModel<T> {
	private static final float REAR_JUMP_ANGLE = 50.0F;
	private static final float FRONT_JUMP_ANGLE = -40.0F;
	private final ModelPart leftRearFoot;
	private final ModelPart rightRearFoot;
	private final ModelPart leftHaunch;
	private final ModelPart rightHaunch;
	private final ModelPart body;
	private final ModelPart leftFrontLeg;
	private final ModelPart rightFrontLeg;
	private final ModelPart head;
	private final ModelPart rightEar;
	private final ModelPart leftEar;
	private final ModelPart tail;
	private final ModelPart nose;
	private float jumpRotation;
	private static final float NEW_SCALE = 0.6F;

	public ZombieRabbitModel(ModelPart root) {
		this.leftRearFoot = root.getChild("left_hind_foot");
		this.rightRearFoot = root.getChild("right_hind_foot");
		this.leftHaunch = root.getChild("left_haunch");
		this.rightHaunch = root.getChild("right_haunch");
		this.body = root.getChild("body");
		this.leftFrontLeg = root.getChild("left_front_leg");
		this.rightFrontLeg = root.getChild("right_front_leg");
		this.head = root.getChild("head");
		this.rightEar = root.getChild("right_ear");
		this.leftEar = root.getChild("left_ear");
		this.tail = root.getChild("tail");
		this.nose = root.getChild("nose");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();
		partdefinition.addOrReplaceChild(
				"left_hind_foot",
				CubeListBuilder.create().texOffs(26, 24).addBox(-1.0F, 5.5F, -3.7F, 2.0F, 1.0F, 7.0F),
				PartPose.offset(3.0F, 17.5F, 3.7F)
		);
		partdefinition.addOrReplaceChild(
				"right_hind_foot",
				CubeListBuilder.create().texOffs(8, 24).addBox(-1.0F, 5.5F, -3.7F, 2.0F, 1.0F, 7.0F),
				PartPose.offset(-3.0F, 17.5F, 3.7F)
		);
		partdefinition.addOrReplaceChild(
				"left_haunch",
				CubeListBuilder.create().texOffs(30, 15).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 4.0F, 5.0F),
				PartPose.offsetAndRotation(3.0F, 17.5F, 3.7F, -Mth.PI / 9.0F, 0.0F, 0.0F)
		);
		partdefinition.addOrReplaceChild(
				"right_haunch",
				CubeListBuilder.create().texOffs(16, 15).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 4.0F, 5.0F),
				PartPose.offsetAndRotation(-3.0F, 17.5F, 3.7F, -Mth.PI / 9.0F, 0.0F, 0.0F)
		);
		partdefinition.addOrReplaceChild(
				"body",
				CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -2.0F, -10.0F, 6.0F, 5.0F, 10.0F),
				PartPose.offsetAndRotation(0.0F, 19.0F, 8.0F, -Mth.PI / 9.0F, 0.0F, 0.0F)
		);
		partdefinition.addOrReplaceChild(
				"left_front_leg",
				CubeListBuilder.create().texOffs(8, 15).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 7.0F, 2.0F),
				PartPose.offsetAndRotation(3.0F, 17.0F, -1.0F, -Mth.PI / 18.0F, 0.0F, 0.0F)
		);
		partdefinition.addOrReplaceChild(
				"right_front_leg",
				CubeListBuilder.create().texOffs(0, 15).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 7.0F, 2.0F),
				PartPose.offsetAndRotation(-3.0F, 17.0F, -1.0F, -Mth.PI / 18.0F, 0.0F, 0.0F)
		);
		partdefinition.addOrReplaceChild(
				"head",
				CubeListBuilder.create().texOffs(32, 0).addBox(-2.5F, -4.0F, -5.0F, 5.0F, 4.0F, 5.0F),
				PartPose.offset(0.0F, 16.0F, -1.0F)
		);
		partdefinition.addOrReplaceChild(
				"right_ear",
				CubeListBuilder.create().texOffs(52, 0).addBox(-2.5F, -9.0F, -1.0F, 2.0F, 5.0F, 1.0F),
				PartPose.offsetAndRotation(0.0F, 16.0F, -1.0F, 0.0F, -Mth.PI / 12.0F, 0.0F)
		);
		partdefinition.addOrReplaceChild(
				"left_ear",
				CubeListBuilder.create().texOffs(58, 0).addBox(0.5F, -9.0F, -1.0F, 2.0F, 5.0F, 1.0F),
				PartPose.offsetAndRotation(0.0F, 16.0F, -1.0F, 0.0F, Mth.PI / 12.0F, 0.0F)
		);
		partdefinition.addOrReplaceChild(
				"tail",
				CubeListBuilder.create().texOffs(52, 6).addBox(-1.5F, -1.5F, 0.0F, 3.0F, 3.0F, 2.0F),
				PartPose.offsetAndRotation(0.0F, 20.0F, 7.0F, -Mth.PI / 9.0F, 0.0F, 0.0F)
		);
		partdefinition.addOrReplaceChild(
				"nose",
				CubeListBuilder.create().texOffs(32, 9).addBox(-0.5F, -2.5F, -5.5F, 1.0F, 1.0F, 1.0F),
				PartPose.offset(0.0F, 16.0F, -1.0F)
		);
		return LayerDefinition.create(meshdefinition, 64, 32);
	}

	public void renderToBuffer(PoseStack transform, VertexConsumer consumer, int x, int y, float r, float g, float b, float a) {
		if (this.young) {
			transform.pushPose();
			transform.scale(0.56666666F, 0.56666666F, 0.56666666F);
			transform.translate(0.0D, 1.375D, 0.125D);
			ImmutableList.of(this.head, this.leftEar, this.rightEar, this.nose)
					.forEach(modelPart -> modelPart.render(transform, consumer, x, y, r, g, b, a));
			transform.popPose();
			transform.pushPose();
			transform.scale(0.4F, 0.4F, 0.4F);
			transform.translate(0.0D, 2.25D, 0.0D);
			ImmutableList.of(this.leftRearFoot, this.rightRearFoot, this.leftHaunch, this.rightHaunch, this.body, this.leftFrontLeg, this.rightFrontLeg, this.tail)
					.forEach(modelPart -> modelPart.render(transform, consumer, x, y, r, g, b, a));
			transform.popPose();
		} else {
			transform.pushPose();
			transform.scale(NEW_SCALE, NEW_SCALE, NEW_SCALE);
			transform.translate(0.0D, 1.0D, 0.0D);
			ImmutableList.of(this.leftRearFoot, this.rightRearFoot, this.leftHaunch, this.rightHaunch, this.body, this.leftFrontLeg, this.rightFrontLeg, this.head, this.rightEar, this.leftEar, this.tail, this.nose)
					.forEach(modelPart -> modelPart.render(transform, consumer, x, y, r, g, b, a));
			transform.popPose();
		}

	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		float tick = ageInTicks - (float)entity.tickCount;
		this.nose.xRot = headPitch * Mth.DEG_TO_RAD;
		this.head.xRot = headPitch * Mth.DEG_TO_RAD;
		this.rightEar.xRot = headPitch * Mth.DEG_TO_RAD;
		this.leftEar.xRot = headPitch * Mth.DEG_TO_RAD;
		this.nose.yRot = netHeadYaw * Mth.DEG_TO_RAD;
		this.head.yRot = netHeadYaw * Mth.DEG_TO_RAD;
		this.rightEar.yRot = this.nose.yRot - Mth.PI / 12.0F;
		this.leftEar.yRot = this.nose.yRot + Mth.PI / 12.0F;
		this.jumpRotation = Mth.sin(entity.getJumpCompletion(tick) * Mth.PI);
		this.leftHaunch.xRot = (this.jumpRotation * REAR_JUMP_ANGLE - 21.0F) * Mth.DEG_TO_RAD;
		this.rightHaunch.xRot = (this.jumpRotation * REAR_JUMP_ANGLE - 21.0F) * Mth.DEG_TO_RAD;
		this.leftRearFoot.xRot = this.jumpRotation * REAR_JUMP_ANGLE * Mth.DEG_TO_RAD;
		this.rightRearFoot.xRot = this.jumpRotation * REAR_JUMP_ANGLE * Mth.DEG_TO_RAD;
		this.leftFrontLeg.xRot = (this.jumpRotation * FRONT_JUMP_ANGLE - 11.0F) * Mth.DEG_TO_RAD;
		this.rightFrontLeg.xRot = (this.jumpRotation * FRONT_JUMP_ANGLE - 11.0F) * Mth.DEG_TO_RAD;
	}

	@Override
	public void prepareMobModel(T entity, float limbSwing, float limbSwingAmount, float tick) {
		super.prepareMobModel(entity, limbSwing, limbSwingAmount, tick);
		this.jumpRotation = Mth.sin(entity.getJumpCompletion(tick) * (float)Math.PI);
	}
}
