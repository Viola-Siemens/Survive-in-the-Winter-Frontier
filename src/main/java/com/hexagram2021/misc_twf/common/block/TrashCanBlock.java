package com.hexagram2021.misc_twf.common.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * 垃圾桶方块，支持六面朝向放置喵~
 *
 * @author liudongyu
 */
public class TrashCanBlock extends Block {
	public static final MapCodec<TrashCanBlock> CODEC = simpleCodec(TrashCanBlock::new);
	protected static final VoxelShape X_SHAPE = Shapes.or(Block.box(5.25, 0, 3, 20.25, 10, 13), Block.box(-9.5, 0, -3.5, 5.5, 1, 11.5));
	protected static final VoxelShape Y_SHAPE = Block.box(3, 0, 3, 13, 16, 13);
	protected static final VoxelShape Z_SHAPE = Shapes.or(Block.box(3, 0, 5.25, 13, 10, 20.25), Block.box(4.5, 0, -9.5, 19.5, 1, 5.5));
	protected static final VoxelShape X_SHAPE_INV = Shapes.or(Block.box(-4.25, 0, 3, 10.75, 10, 13), Block.box(10.5, 0, 4.5, 25.5, 1, 19.5));
	protected static final VoxelShape Z_SHAPE_INV = Shapes.or(Block.box(3, 0, -4.25, 13, 10, 10.75), Block.box(-3.5, 0, 10.5, 11.5, 1, 25.5));

	public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING_HOPPER;

	/**
	 * 构造垃圾桶方块，并设置默认朝向为向下喵~
	 *
	 * @param props 方块属性喵~
	 */
	public TrashCanBlock(Properties props) {
		super(props);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.DOWN));
	}

	@Override
	public VoxelShape getShape(BlockState blockState, BlockGetter level, BlockPos blockPos, CollisionContext context) {
		return switch (blockState.getValue(FACING)) {
			case DOWN, UP -> Y_SHAPE;
			case NORTH -> Z_SHAPE_INV;
			case SOUTH -> Z_SHAPE;
			case WEST -> X_SHAPE_INV;
			case EAST -> X_SHAPE;
		};
	}

	@Override
	public BlockState rotate(BlockState blockState, Rotation rotation) {
		return blockState.setValue(FACING, rotation.rotation().rotate(blockState.getValue(FACING)));
	}

	@Override
	public BlockState mirror(BlockState blockState, Mirror mirror) {
		return blockState.setValue(FACING, mirror.rotation().rotate(blockState.getValue(FACING)));
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		Direction front = context.getNearestLookingDirection();
		if(front == Direction.UP) {
			front = context.getHorizontalDirection();
		}
		return this.defaultBlockState().setValue(FACING, front);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Override
	protected MapCodec<TrashCanBlock> codec() {
		return CODEC;
	}
}
