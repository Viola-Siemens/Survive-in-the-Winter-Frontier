package com.hexagram2021.misc_twf.client.model;

import com.hexagram2021.misc_twf.SurviveInTheWinterFrontier;
import com.hexagram2021.misc_twf.common.block.entity.MonsterEggBlockEntity;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.model.GeoModel;

/**
 * 怪物蛋方块实体的 GeckoLib 3D 模型类喵~
 * 提供怪物蛋的几何模型、纹理和动画资源的访问喵~
 * 使用半透明渲染类型，以支持怪物蛋的透明材质效果喵~
 *
 * @author liudongyu
 */
@OnlyIn(Dist.CLIENT)
public class MonsterEggModel extends GeoModel<MonsterEggBlockEntity> {
	/**
	 * 获取怪物蛋的几何模型资源位置喵~
	 *
	 * @param object 怪物蛋方块实体喵~
	 * @return 模型 JSON 文件的资源位置喵~
	 */
	@Override
    public ResourceLocation getModelResource(MonsterEggBlockEntity object) {
        return ResourceLocation.fromNamespaceAndPath(SurviveInTheWinterFrontier.MODID, "geo/monster_egg.geo.json");
    }

	/**
	 * 获取怪物蛋的纹理资源位置喵~
	 *
	 * @param object 怪物蛋方块实体喵~
	 * @return 纹理 PNG 文件的资源位置喵~
	 */
	@Override
    public ResourceLocation getTextureResource(MonsterEggBlockEntity object) {
        return ResourceLocation.fromNamespaceAndPath(SurviveInTheWinterFrontier.MODID, "textures/block/monster_egg.png");
    }

	/**
	 * 获取怪物蛋的动画资源位置喵~
	 *
	 * @param animatable 怪物蛋方块实体喵~
	 * @return 动画 JSON 文件的资源位置喵~
	 */
	@Override
    public ResourceLocation getAnimationResource(MonsterEggBlockEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(SurviveInTheWinterFrontier.MODID, "animations/monster_egg.animation.json");
    }

	/**
	 * 获取怪物蛋的渲染类型喵~
	 * 使用实体半透明渲染类型，以支持透明材质的正确显示喵~
	 *
	 * @param animatable 怪物蛋方块实体喵~
	 * @param texture 纹理资源位置喵~
	 * @return 半透明实体渲染类型喵~
	 */
	@Override @Nullable
	public RenderType getRenderType(MonsterEggBlockEntity animatable, ResourceLocation texture) {
		return RenderType.entityTranslucent(texture);
	}
}
