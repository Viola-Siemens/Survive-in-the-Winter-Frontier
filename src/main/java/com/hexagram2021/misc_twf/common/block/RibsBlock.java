package com.hexagram2021.misc_twf.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

@SuppressWarnings("deprecation")
public class RibsBlock extends Block {
	protected static final VoxelShape AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D);
	public RibsBlock(Properties props) {
		super(props);
	}

	@Override
	public VoxelShape getShape(BlockState blockState, BlockGetter level, BlockPos blockPos, CollisionContext context) {
		return AABB;
	}

	@Override
	public BlockState updateShape(BlockState blockState, Direction direction, BlockState oldState, LevelAccessor level, BlockPos blockPos, BlockPos neighbor) {
		return !blockState.canSurvive(level, blockPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(blockState, direction, oldState, level, blockPos, neighbor);
	}

	@Override
	public boolean canSurvive(BlockState blockState, LevelReader level, BlockPos blockPos) {
		return canSupportRigidBlock(level, blockPos.below());
	}
}
