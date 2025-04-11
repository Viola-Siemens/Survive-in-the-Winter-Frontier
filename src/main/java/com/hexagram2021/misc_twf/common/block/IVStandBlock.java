package com.hexagram2021.misc_twf.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

@SuppressWarnings("deprecation")
public class IVStandBlock extends Block {
	public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;
	public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;

	private static final VoxelShape LOWER_SHAPE = Shapes.or(Block.box(7.5D, 0.0D, 7.5D, 8.5D, 16.0D, 8.5D), Block.box(2.0D, 0.0D, 7.5D, 14.0D, 1.0D, 8.5D), Block.box(7.5D, 0.0D, 2.0D, 8.5D, 1.0D, 14.0D));
	private static final VoxelShape UPPER_X_SHAPE = Shapes.or(Block.box(7.5D, 0.0D, 7.5D, 8.5D, 16.0D, 8.5D), Block.box(2.0D, 10.0D, 7.5D, 14.0D, 16.0D, 8.5D));
	private static final VoxelShape UPPER_Z_SHAPE = Shapes.or(Block.box(7.5D, 0.0D, 7.5D, 8.5D, 16.0D, 8.5D), Block.box(7.5D, 10.0D, 2.0D, 8.5D, 16.0D, 14.0D));

	public IVStandBlock(BlockBehaviour.Properties props) {
		super(props);
		this.registerDefaultState(this.stateDefinition.any().setValue(AXIS, Direction.Axis.X).setValue(HALF, DoubleBlockHalf.LOWER));
	}

	@Override
	public VoxelShape getShape(BlockState blockState, BlockGetter level, BlockPos blockPos, CollisionContext context) {
		if(blockState.getValue(HALF) == DoubleBlockHalf.LOWER) {
			return LOWER_SHAPE;
		}
		return switch (blockState.getValue(AXIS)) {
			case X -> UPPER_X_SHAPE;
			case Z -> UPPER_Z_SHAPE;
			default -> super.getShape(blockState, level, blockPos, context);
		};
	}

	@Override
	public BlockState updateShape(BlockState blockState, Direction direction, BlockState neighbor, LevelAccessor level, BlockPos blockPos, BlockPos neighborPos) {
		DoubleBlockHalf half = blockState.getValue(HALF);
		if (direction.getAxis() == Direction.Axis.Y && half == DoubleBlockHalf.LOWER == (direction == Direction.UP)) {
			return neighbor.is(this) && neighbor.getValue(HALF) != half ? blockState.setValue(AXIS, neighbor.getValue(AXIS)) : Blocks.AIR.defaultBlockState();
		}
		return half == DoubleBlockHalf.LOWER && direction == Direction.DOWN && !blockState.canSurvive(level, blockPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(blockState, direction, neighbor, level, blockPos, neighborPos);
	}

	@Override
	public void playerWillDestroy(Level level, BlockPos blockPos, BlockState blockState, Player player) {
		if (!level.isClientSide && player.isCreative()) {
			DoublePlantBlock.preventCreativeDropFromBottomPart(level, blockPos, blockState, player);
		}

		super.playerWillDestroy(level, blockPos, blockState, player);
	}

	@Override
	public boolean isPathfindable(BlockState blockState, BlockGetter level, BlockPos blockPos, PathComputationType type) {
		return false;
	}

	@Override @Nullable
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		BlockPos clickedPos = context.getClickedPos();
		Level level = context.getLevel();
		if (clickedPos.getY() < level.getMaxBuildHeight() - 1 && level.getBlockState(clickedPos.above()).canBeReplaced(context)) {
			return this.defaultBlockState().setValue(AXIS, context.getHorizontalDirection().getAxis()).setValue(HALF, DoubleBlockHalf.LOWER);
		}
		return null;
	}

	@Override
	public void setPlacedBy(Level level, BlockPos blockPos, BlockState blockState, @Nullable LivingEntity livingEntity, ItemStack itemStack) {
		level.setBlock(blockPos.above(), blockState.setValue(HALF, DoubleBlockHalf.UPPER), Block.UPDATE_ALL);
	}

	@Override
	public boolean canSurvive(BlockState blockState, LevelReader level, BlockPos blockPos) {
		BlockPos blockpos = blockPos.below();
		BlockState blockstate = level.getBlockState(blockpos);
		return blockState.getValue(HALF) == DoubleBlockHalf.LOWER ? blockstate.isFaceSturdy(level, blockpos, Direction.UP) : blockstate.is(this);
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
	public long getSeed(BlockState blockState, BlockPos blockPos) {
		return Mth.getSeed(blockPos.getX(), blockPos.below(blockState.getValue(HALF) == DoubleBlockHalf.LOWER ? 0 : 1).getY(), blockPos.getZ());
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(HALF, AXIS);
	}
}
