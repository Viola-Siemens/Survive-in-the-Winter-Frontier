package com.hexagram2021.misc_twf.client.model;

import com.hexagram2021.misc_twf.common.entity.ZombieAnimalEntity;
import net.minecraft.client.model.QuadrupedModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.animal.Pig;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ZombiePigModel<T extends ZombieAnimalEntity<Pig>> extends QuadrupedModel<T> {
	public ZombiePigModel(ModelPart root) {
		super(root, false, 4.0F, 4.0F, 2.0F, 2.0F, 24);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = QuadrupedModel.createBodyMesh(6, CubeDeformation.NONE);
		PartDefinition partdefinition = meshdefinition.getRoot();
		partdefinition.addOrReplaceChild(
				"head",
				CubeListBuilder.create()
						.texOffs(0, 0).addBox(-4.0F, -4.0F, -8.0F, 8.0F, 8.0F, 8.0F)
						.texOffs(16, 16).addBox(-2.0F, 0.0F, -9.0F, 4.0F, 3.0F, 1.0F),
				PartPose.offset(0.0F, 12.0F, -6.0F)
		);
		return LayerDefinition.create(meshdefinition, 64, 32);
	}
}
