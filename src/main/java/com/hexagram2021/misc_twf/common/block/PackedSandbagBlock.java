package com.hexagram2021.misc_twf.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

@SuppressWarnings("deprecation")
public class PackedSandbagBlock extends Block {
	public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;

	private static final VoxelShape X_SHAPE = Block.box(0.0D, 0.0D, 4.0D, 16.0D, 13.0D, 12.0D);
	private static final VoxelShape Z_SHAPE = Block.box(4.0D, 0.0D, 0.0D, 12.0D, 13.0D, 16.0D);

	public PackedSandbagBlock(Properties props) {
		super(props);
		this.registerDefaultState(this.stateDefinition.any().setValue(AXIS, Direction.Axis.X));
	}

	@Override
	public VoxelShape getShape(BlockState blockState, BlockGetter level, BlockPos blockPos, CollisionContext context) {
		return switch (blockState.getValue(AXIS)) {
			case X -> X_SHAPE;
			case Z -> Z_SHAPE;
			default -> super.getShape(blockState, level, blockPos, context);
		};
	}

	@Override @Nullable
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		BlockPos clickedPos = context.getClickedPos();
		Level level = context.getLevel();
		if (clickedPos.getY() < level.getMaxBuildHeight() - 1 && level.getBlockState(clickedPos.above()).canBeReplaced(context)) {
			return this.defaultBlockState().setValue(AXIS, context.getHorizontalDirection().getAxis());
		}
		return null;
	}

	@Override
	public PushReaction getPistonPushReaction(BlockState p_52814_) {
		return PushReaction.DESTROY;
	}

	@Override
	public BlockState rotate(BlockState blockState, Rotation rotation) {
		return switch (rotation) {
			case COUNTERCLOCKWISE_90, CLOCKWISE_90 -> switch (blockState.getValue(AXIS)) {
				case Z -> blockState.setValue(AXIS, Direction.Axis.X);
				case X -> blockState.setValue(AXIS, Direction.Axis.Z);
				default -> blockState;
			};
			default -> blockState;
		};
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(AXIS);
	}
}
