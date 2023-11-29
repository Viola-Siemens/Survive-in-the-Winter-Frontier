package com.hexagram2021.misc_twf.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class NightVisionDeviceModel extends Model {
	public final ModelPart root;
	public final ModelPart hat;
	public final ModelPart connection;
	public final ModelPart left;
	public final ModelPart right;

	public NightVisionDeviceModel(ModelPart root) {
		super(RenderType::entitySolid);
		this.root = root;
		this.hat = root.getChild("hat");
		this.connection = this.hat.getChild("connection");
		this.left = this.connection.getChild("left");
		this.right = this.connection.getChild("right");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();

		PartDefinition hat = root.addOrReplaceChild(
				"hat", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.5F)),
				PartPose.ZERO
		);
		PartDefinition connection = hat.addOrReplaceChild(
				"connection", CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, -5.0F, -8.0F, 1.0F, 2.0F, 3.0F, CubeDeformation.NONE),
				PartPose.ZERO
		);
		connection.addOrReplaceChild(
				"left", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -7.0F, -10.0F, 6.0F, 6.0F, 8.0F, new CubeDeformation(-1.5F)),
				PartPose.ZERO
		);
		connection.addOrReplaceChild(
				"right", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -7.0F, -10.0F, 6.0F, 6.0F, 8.0F, new CubeDeformation(-1.5F)),
				PartPose.ZERO
		);
		return LayerDefinition.create(mesh, 64, 16);
	}

	@Override
	public void renderToBuffer(PoseStack transform, VertexConsumer vertexConsumer, int packedLight, int packedOverlay,
							   float r, float g, float b, float a) {
		this.root.render(transform, vertexConsumer, packedLight, packedOverlay, r, g, b, a);
	}
}
