package com.hexagram2021.misc_twf.common.block;

import com.mojang.serialization.MapCodec;
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

/**
 * 轮椅方块喵~
 *
 * @author liudongyu
 */
public class WheelchairBlock extends HorizontalDirectionalBlock {
	public static final MapCodec<WheelchairBlock> CODEC = simpleCodec(WheelchairBlock::new);
	protected static final VoxelShape NORTH_SHAPE = Shapes.or(
			Block.box(0.0D, 0.0D, -6.0D, 16.0D, 10.0D, 18.0D),
			Block.box(1.0D, 10.0D, 2.0D, 15.0D, 15.0D, 10.0D),
			Block.box(2.0D, 15.0D, 10.0D, 14.0D, 19.0D, 16.0D)
	);
	protected static final VoxelShape SOUTH_SHAPE = Shapes.or(
			Block.box(0.0D, 0.0D, -2.0D, 16.0D, 10.0D, 22.0D),
			Block.box(1.0D, 10.0D, 6.0D, 15.0D, 15.0D, 14.0D),
			Block.box(2.0D, 15.0D, 0.0D, 14.0D, 19.0D, 6.0D)
	);
	protected static final VoxelShape WEST_SHAPE = Shapes.or(
			Block.box(-6.0D, 0.0D, 0.0D, 18.0D, 10.0D, 16.0D),
			Block.box(2.0D, 10.0D, 1.0D, 10.0D, 15.0D, 15.0D),
			Block.box(10.0D, 15.0D, 2.0D, 16.0D, 19.0D, 14.0D)
	);
	protected static final VoxelShape EAST_SHAPE = Shapes.or(
			Block.box(-2.0D, 0.0D, 0.0D, 22.0D, 10.0D, 16.0D),
			Block.box(6.0D, 10.0D, 1.0D, 14.0D, 15.0D, 15.0D),
			Block.box(0.0D, 15.0D, 2.0D, 6.0D, 19.0D, 14.0D)
	);

	/**
	 * 构造轮椅方块，并设置默认朝向为北喵~
	 *
	 * @param props 方块属性喵~
	 */
	public WheelchairBlock(Properties props) {
		super(props);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
	}

	@Override
	public VoxelShape getShape(BlockState blockState, BlockGetter level, BlockPos blockPos, CollisionContext context) {
		return switch (blockState.getValue(FACING)) {
			case NORTH -> NORTH_SHAPE;
			case SOUTH -> SOUTH_SHAPE;
			case WEST -> WEST_SHAPE;
			case EAST -> EAST_SHAPE;
			default -> super.getShape(blockState, level, blockPos, context);
		};
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Override
	protected MapCodec<WheelchairBlock> codec() {
		return CODEC;
	}
}
