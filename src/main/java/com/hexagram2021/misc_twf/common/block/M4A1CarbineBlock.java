package com.hexagram2021.misc_twf.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

@SuppressWarnings("deprecation")
public class M4A1CarbineBlock extends HorizontalDirectionalBlock {
	protected static final VoxelShape X_SHAPE = Block.box(-1, 0, 5, 17, 2, 11);
	protected static final VoxelShape Z_SHAPE = Block.box(5, 0, -1, 11, 2, 17);

	public M4A1CarbineBlock(Properties props) {
		super(props);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
	}

	@Override
	public VoxelShape getShape(BlockState blockState, BlockGetter level, BlockPos blockPos, CollisionContext context) {
		return switch (blockState.getValue(FACING)) {
			case DOWN, UP, NORTH, SOUTH -> Z_SHAPE;
			case WEST, EAST -> X_SHAPE;
		};
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getCounterClockWise());
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Override
	public PushReaction getPistonPushReaction(BlockState blockState) {
		return PushReaction.DESTROY;
	}
}
