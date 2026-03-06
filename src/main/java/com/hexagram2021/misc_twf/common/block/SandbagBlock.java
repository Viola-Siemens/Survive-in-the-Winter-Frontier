package com.hexagram2021.misc_twf.common.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * 沙袋方块，可以被活塞推动时销毁喵~
 *
 * @author liudongyu
 */
@SuppressWarnings("deprecation")
public class SandbagBlock extends Block {
	public static final MapCodec<SandbagBlock> CODEC = simpleCodec(SandbagBlock::new);
	private static final VoxelShape SHAPE = Block.box(3.0D, 0.0D, 3.0D, 13.0D, 3.0D, 13.0D);

	public SandbagBlock(Properties props) {
		super(props);
	}

	@Override
	public VoxelShape getShape(BlockState blockState, BlockGetter level, BlockPos blockPos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	public PushReaction getPistonPushReaction(BlockState p_52814_) {
		return PushReaction.DESTROY;
	}

	@Override
	protected MapCodec<SandbagBlock> codec() {
		return CODEC;
	}
}
