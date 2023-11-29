package com.hexagram2021.misc_twf.client.model;

import com.hexagram2021.misc_twf.common.entity.ZombieAnimalEntity;
import net.minecraft.client.model.QuadrupedModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Cow;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ZombieCowModel<T extends ZombieAnimalEntity<Cow>> extends QuadrupedModel<T> {
	public ZombieCowModel(ModelPart root) {
		super(root, false, 10.0F, 4.0F, 2.0F, 2.0F, 24);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition root = meshdefinition.getRoot();
		root.addOrReplaceChild(
				"head",
				CubeListBuilder.create()
						.texOffs(0, 0).addBox(-4.0F, -4.0F, -6.0F, 8.0F, 8.0F, 6.0F)
						.texOffs(22, 0).addBox("right_horn", -5.0F, -5.0F, -4.0F, 1.0F, 3.0F, 1.0F)
						.texOffs(22, 0).addBox("left_horn", 4.0F, -5.0F, -4.0F, 1.0F, 3.0F, 1.0F),
				PartPose.offset(0.0F, 4.0F, -8.0F)
		);
		root.addOrReplaceChild(
				"body",
				CubeListBuilder.create()
						.texOffs(18, 4).addBox(-6.0F, -10.0F, -7.0F, 12.0F, 18.0F, 10.0F)
						.texOffs(52, 0).addBox(-2.0F, 2.0F, -8.0F, 4.0F, 6.0F, 1.0F),
				PartPose.offsetAndRotation(0.0F, 5.0F, 2.0F, Mth.HALF_PI, 0.0F, 0.0F)
		);
		CubeListBuilder legBuilder = CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F);
		root.addOrReplaceChild("right_hind_leg", legBuilder, PartPose.offset(-4.0F, 12.0F, 7.0F));
		root.addOrReplaceChild("left_hind_leg", legBuilder, PartPose.offset(4.0F, 12.0F, 7.0F));
		root.addOrReplaceChild("right_front_leg", legBuilder, PartPose.offset(-4.0F, 12.0F, -6.0F));
		root.addOrReplaceChild("left_front_leg", legBuilder, PartPose.offset(4.0F, 12.0F, -6.0F));
		return LayerDefinition.create(meshdefinition, 64, 32);
	}
}
