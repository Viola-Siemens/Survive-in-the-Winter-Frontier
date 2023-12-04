package com.hexagram2021.misc_twf.client.model;

import com.google.common.collect.ImmutableList;
import com.hexagram2021.misc_twf.common.entity.ZombieAnimalEntity;
import net.minecraft.client.model.ColorableAgeableListModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ZombieWolfModel<T extends ZombieAnimalEntity<Wolf>> extends ColorableAgeableListModel<T> {
	private final ModelPart head;
	private final ModelPart body;
	private final ModelPart rightHindLeg;
	private final ModelPart leftHindLeg;
	private final ModelPart rightFrontLeg;
	private final ModelPart leftFrontLeg;
	private final ModelPart tail;
	private final ModelPart upperBody;

	public ZombieWolfModel(ModelPart root) {
		this.head = root.getChild("head");
		this.body = root.getChild("body");
		this.upperBody = root.getChild("upper_body");
		this.rightHindLeg = root.getChild("right_hind_leg");
		this.leftHindLeg = root.getChild("left_hind_leg");
		this.rightFrontLeg = root.getChild("right_front_leg");
		this.leftFrontLeg = root.getChild("left_front_leg");
		this.tail = root.getChild("tail");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition root = meshdefinition.getRoot();
		PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(-1.0F, 13.5F, -7.0F));
		head.addOrReplaceChild(
				"real_head",
				CubeListBuilder.create()
						.texOffs(0, 0).addBox(-2.0F, -3.0F, -2.0F, 6.0F, 6.0F, 4.0F)
						.texOffs(16, 14).addBox(-2.0F, -5.0F, 0.0F, 2.0F, 2.0F, 1.0F)
						.texOffs(16, 14).addBox(2.0F, -5.0F, 0.0F, 2.0F, 2.0F, 1.0F)
						.texOffs(0, 10).addBox(-0.5F, 0.0F, -5.0F, 3.0F, 3.0F, 4.0F),
				PartPose.ZERO
		);
		root.addOrReplaceChild(
				"body",
				CubeListBuilder.create()
						.texOffs(18, 14).addBox(-3.0F, -2.0F, -3.0F, 6.0F, 9.0F, 6.0F),
				PartPose.offsetAndRotation(0.0F, 14.0F, 2.0F, Mth.HALF_PI, 0.0F, 0.0F)
		);
		root.addOrReplaceChild(
				"upper_body",
				CubeListBuilder.create().texOffs(21, 0).addBox(-3.0F, -3.0F, -3.0F, 8.0F, 6.0F, 7.0F),
				PartPose.offsetAndRotation(-1.0F, 14.0F, -3.0F, Mth.HALF_PI, 0.0F, 0.0F)
		);

		CubeListBuilder legBuilder = CubeListBuilder.create().texOffs(0, 18).addBox(0.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F);
		root.addOrReplaceChild("right_hind_leg", legBuilder, PartPose.offset(-2.5F, 16.0F, 7.0F));
		root.addOrReplaceChild("left_hind_leg", legBuilder, PartPose.offset(0.5F, 16.0F, 7.0F));
		root.addOrReplaceChild("right_front_leg", legBuilder, PartPose.offset(-2.5F, 16.0F, -4.0F));
		root.addOrReplaceChild("left_front_leg", legBuilder, PartPose.offset(0.5F, 16.0F, -4.0F));
		PartDefinition tail = root.addOrReplaceChild("tail", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.0F, 12.0F, 8.0F, Mth.PI / 5F, 0.0F, 0.0F));
		tail.addOrReplaceChild("real_tail", CubeListBuilder.create().texOffs(9, 18).addBox(0.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F), PartPose.ZERO);
		return LayerDefinition.create(meshdefinition, 64, 32);
	}

	@Override
	protected Iterable<ModelPart> headParts() {
		return ImmutableList.of(this.head);
	}

	@Override
	protected Iterable<ModelPart> bodyParts() {
		return ImmutableList.of(this.body, this.rightHindLeg, this.leftHindLeg, this.rightFrontLeg, this.leftFrontLeg, this.tail, this.upperBody);
	}

	@Override
	public void prepareMobModel(T entity, float limbSwing, float limbSwingAmount, float tick) {
		this.tail.yRot = 0.0F;
		this.body.setPos(0.0F, 14.0F, 2.0F);
		this.body.xRot = Mth.HALF_PI;
		this.upperBody.setPos(-1.0F, 14.0F, -3.0F);
		this.upperBody.xRot = this.body.xRot;
		this.tail.setPos(-1.0F, 12.0F, 8.0F);
		this.rightHindLeg.setPos(-2.5F, 16.0F, 7.0F);
		this.leftHindLeg.setPos(0.5F, 16.0F, 7.0F);
		this.rightFrontLeg.setPos(-2.5F, 16.0F, -4.0F);
		this.leftFrontLeg.setPos(0.5F, 16.0F, -4.0F);
		this.rightHindLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
		this.leftHindLeg.xRot = Mth.cos(limbSwing * 0.6662F + Mth.PI) * 1.4F * limbSwingAmount;
		this.rightFrontLeg.xRot = Mth.cos(limbSwing * 0.6662F + Mth.PI) * 1.4F * limbSwingAmount;
		this.leftFrontLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.head.xRot = headPitch * Mth.DEG_TO_RAD;
		this.head.yRot = netHeadYaw * Mth.DEG_TO_RAD;
		this.tail.xRot = ageInTicks;
	}
}
