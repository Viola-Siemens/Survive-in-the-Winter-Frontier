package com.hexagram2021.misc_twf.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * 夜视仪装备的客户端模型类喵~
 * 定义夜视仪的几何形状，包含帽子、连接件、左右目镜等部件喵~
 * 用于 Curios 饰品槽的渲染喵~
 *
 * @author liudongyu
 */
@OnlyIn(Dist.CLIENT)
public class NightVisionDeviceModel extends Model {
	public final ModelPart root;
	public final ModelPart hat;
	public final ModelPart connection;
	public final ModelPart left;
	public final ModelPart right;

	/**
	 * 构造夜视仪模型，从根模型部件中解析出各子部件喵~
	 *
	 * @param root 根模型部件喵~
	 */
	public NightVisionDeviceModel(ModelPart root) {
		super(RenderType::entitySolid);
		this.root = root;
		this.hat = root.getChild("hat");
		this.connection = this.hat.getChild("connection");
		this.left = this.connection.getChild("left");
		this.right = this.connection.getChild("right");
	}

	/**
	 * 创建夜视仪的身体层定义，包含帽子、连接件和左右目镜的网格数据喵~
	 *
	 * @return 层定义喵~
	 */
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
	public void renderToBuffer(PoseStack transform, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
		this.root.render(transform, vertexConsumer, packedLight, packedOverlay, color);
	}
}
