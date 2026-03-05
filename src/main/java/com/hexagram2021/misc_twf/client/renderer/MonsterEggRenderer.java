package com.hexagram2021.misc_twf.client.renderer;

import com.hexagram2021.misc_twf.client.model.MonsterEggModel;
import com.hexagram2021.misc_twf.common.block.entity.MonsterEggBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

/**
 * 怪物蛋方块实体的 GeckoLib 渲染器喵~
 * 负责使用 GeckoLib 渲染引擎渲染怪物蛋的 3D 模型和动画喵~
 *
 * @author liudongyu
 */
public class MonsterEggRenderer extends GeoBlockRenderer<MonsterEggBlockEntity> {
	/**
	 * 构造怪物蛋渲染器喵~
	 *
	 * @param rendererProvider 方块实体渲染器提供者上下文喵~
	 */
	public MonsterEggRenderer(BlockEntityRendererProvider.Context rendererProvider) {
        super(new MonsterEggModel());
    }
}
