package com.hexagram2021.misc_twf.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.FrontAndTop;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

@SuppressWarnings("deprecation")
public class ExplosiveJerricanBlock extends Block {
	protected static final VoxelShape X_SHAPE = Block.box(0, 2, 2, 22, 14, 14);
	protected static final VoxelShape Y_SHAPE = Block.box(2, 0, 2, 14, 22, 14);
	protected static final VoxelShape Z_SHAPE = Block.box(2, 2, 0, 14, 14, 22);
	protected static final VoxelShape X_SHAPE_INV = Block.box(-6, 2, 2, 16, 14, 14);
	protected static final VoxelShape Z_SHAPE_INV = Block.box(2, 2, -6, 14, 14, 16);

	public static final EnumProperty<FrontAndTop> ORIENTATION = BlockStateProperties.ORIENTATION;

	public ExplosiveJerricanBlock(Properties props) {
		super(props);
		this.registerDefaultState(this.stateDefinition.any().setValue(ORIENTATION, FrontAndTop.NORTH_UP));
	}

	@Override
	public VoxelShape getShape(BlockState blockState, BlockGetter level, BlockPos blockPos, CollisionContext context) {
		Direction top = blockState.getValue(ORIENTATION).top();
		return switch (top) {
			case DOWN, UP -> Y_SHAPE;
			case NORTH -> Z_SHAPE_INV;
			case SOUTH -> Z_SHAPE;
			case WEST -> X_SHAPE_INV;
			case EAST -> X_SHAPE;
		};
	}

	@Override
	public BlockState rotate(BlockState blockState, Rotation rotation) {
		return blockState.setValue(ORIENTATION, rotation.rotation().rotate(blockState.getValue(ORIENTATION)));
	}

	@Override
	public BlockState mirror(BlockState p_54238_, Mirror mirror) {
		return p_54238_.setValue(ORIENTATION, mirror.rotation().rotate(p_54238_.getValue(ORIENTATION)));
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		Direction front = context.getNearestLookingDirection().getOpposite();
		Direction top = front.getAxis() == Direction.Axis.Y ? context.getHorizontalDirection().getOpposite() : Direction.UP;
		return this.defaultBlockState().setValue(ORIENTATION, FrontAndTop.fromFrontAndTop(front, top));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(ORIENTATION);
	}
}
