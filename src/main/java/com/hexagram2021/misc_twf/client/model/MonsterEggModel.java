package com.hexagram2021.misc_twf.client.model;

import com.hexagram2021.misc_twf.SurviveInTheWinterFrontier;
import com.hexagram2021.misc_twf.common.block.entity.MonsterEggBlockEntity;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class MonsterEggModel extends AnimatedGeoModel<MonsterEggBlockEntity> {

    @Override
    public ResourceLocation getModelLocation(MonsterEggBlockEntity object) {
        return new ResourceLocation(SurviveInTheWinterFrontier.MODID, "geo/monster_egg.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(MonsterEggBlockEntity object) {
        return new ResourceLocation(SurviveInTheWinterFrontier.MODID, "textures/block/monster_egg.png");
    }

    @Override
    public ResourceLocation getAnimationFileLocation(MonsterEggBlockEntity animatable) {
        return new ResourceLocation(SurviveInTheWinterFrontier.MODID, "animations/monster_egg.animation.json");
    }
}
