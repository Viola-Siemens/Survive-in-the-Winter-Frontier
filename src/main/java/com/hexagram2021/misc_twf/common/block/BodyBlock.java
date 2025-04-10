package com.hexagram2021.misc_twf.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BodyBlock extends HorizontalDirectionalBlock {
	protected final VoxelShape X_SHAPE;
	protected final VoxelShape Z_SHAPE;

	public BodyBlock(int xSize, int zSize, Properties props) {
		super(props);
		this.X_SHAPE = Block.box(8.0D - zSize, 0.0D, 8.0D - xSize, 8.0D + zSize, 10.0D, 8.0D + xSize);
		this.Z_SHAPE = Block.box(8.0D - xSize, 0.0D, 8.0D - zSize, 8.0D + xSize, 10.0D, 8.0D + zSize);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
	}

	@SuppressWarnings("deprecation")
	@Override
	public VoxelShape getShape(BlockState blockState, BlockGetter level, BlockPos blockPos, CollisionContext context) {
		return switch (blockState.getValue(FACING)) {
			case DOWN, UP, NORTH, SOUTH -> this.Z_SHAPE;
			case WEST, EAST -> this.X_SHAPE;
		};
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}
}
