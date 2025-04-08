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
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SickbedBlock extends HorizontalDirectionalBlock {
	protected static final VoxelShape X_SHAPE = Shapes.or(Block.box(-10, 0, -1, 26, 15, 17), Block.box(-13, 0, -2, -10, 20, 18), Block.box(26, 0, -2, 29, 20, 18));
	protected static final VoxelShape Z_SHAPE = Shapes.or(Block.box(-1, 0, -10, 17, 15, 26), Block.box(-2, 0, -13, 18, 20, -10), Block.box(-2, 0, 26, 18, 20, 29));

	public SickbedBlock(Properties props) {
		super(props);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
	}

	@SuppressWarnings("deprecation")
	@Override
	public VoxelShape getShape(BlockState blockState, BlockGetter level, BlockPos blockPos, CollisionContext context) {
		return switch (blockState.getValue(FACING)) {
			case DOWN, UP, NORTH, SOUTH -> Z_SHAPE;
			case WEST, EAST -> X_SHAPE;
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
