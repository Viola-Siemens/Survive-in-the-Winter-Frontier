package com.hexagram2021.misc_twf.common.block;

import com.mojang.serialization.MapCodec;
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
import net.minecraft.world.level.block.*;
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

/**
 * 轮式担架方块，是一个双层方块，支持水平四方向放置喵~
 * <p>
 * 上半部分包含担架扶手的碰撞箱，下半部分包含担架主体和轮子的碰撞箱喵~
 *
 * @author liudongyu
 */
@SuppressWarnings("deprecation")
public class WheeledStretcherBlock extends HorizontalDirectionalBlock {
	public static final MapCodec<WheeledStretcherBlock> CODEC = simpleCodec(WheeledStretcherBlock::new);
	public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;

	private static final VoxelShape UPPER_NORTH_SHAPE = Shapes.or(Block.box(15.0D, 1.0D, 23.0D, 16.0D, 23.0D, 24.0D), Block.box(11.5D, 19.0D, 23.0D, 19.5D, 23.0D, 24.0D));
	private static final VoxelShape UPPER_SOUTH_SHAPE = Shapes.or(Block.box(0.0D, 1.0D, -8.0D, 1.0D, 23.0D, -7.0D), Block.box(-3.5D, 19.0D, -8.0D, 4.5D, 23.0D, -7.0D));
	private static final VoxelShape UPPER_WEST_SHAPE = Shapes.or(Block.box(23.0D, 1.0D, 0.0D, 24.0D, 23.0D, 1.0D), Block.box(23.0D, 19.0D, -3.5D, 24.0D, 23.0D, 4.5D));
	private static final VoxelShape UPPER_EAST_SHAPE = Shapes.or(Block.box(-8.0D, 1.0D, 15.0D, -7.0D, 23.0D, 16.0D), Block.box(-8.0D, 19.0D, 11.5D, -7.0D, 23.0D, 19.5D));
	private static final VoxelShape LOWER_X_SHAPE = Block.box(-10.0D, 0.0D, 0.0D, 26.0D, 20.0D, 16.0D);
	private static final VoxelShape LOWER_Z_SHAPE = Block.box(0.0D, 0.0D, -10.0D, 16.0D, 20.0D, 26.0D);

	/**
	 * 构造轮式担架方块，并设置默认朝向为北、默认为下半部分喵~
	 *
	 * @param props 方块属性喵~
	 */
	public WheeledStretcherBlock(Properties props) {
		super(props);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(HALF, DoubleBlockHalf.LOWER));
	}

	@Override
	public VoxelShape getShape(BlockState blockState, BlockGetter level, BlockPos blockPos, CollisionContext context) {
		if(blockState.getValue(HALF) == DoubleBlockHalf.UPPER) {
			return switch (blockState.getValue(FACING)) {
				case NORTH -> UPPER_NORTH_SHAPE;
				case SOUTH -> UPPER_SOUTH_SHAPE;
				case WEST -> UPPER_WEST_SHAPE;
				case EAST -> UPPER_EAST_SHAPE;
				default -> super.getShape(blockState, level, blockPos, context);
			};
		}
		return switch (blockState.getValue(FACING).getAxis()) {
			case X -> LOWER_X_SHAPE;
			case Z -> LOWER_Z_SHAPE;
			default -> super.getShape(blockState, level, blockPos, context);
		};
	}

	@Override
	public BlockState updateShape(BlockState blockState, Direction direction, BlockState neighbor, LevelAccessor level, BlockPos blockPos, BlockPos neighborPos) {
		DoubleBlockHalf half = blockState.getValue(HALF);
		if (direction.getAxis() == Direction.Axis.Y && half == DoubleBlockHalf.LOWER == (direction == Direction.UP)) {
			return neighbor.is(this) && neighbor.getValue(HALF) != half ? blockState.setValue(FACING, neighbor.getValue(FACING)) : Blocks.AIR.defaultBlockState();
		}
		return half == DoubleBlockHalf.LOWER && direction == Direction.DOWN && !blockState.canSurvive(level, blockPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(blockState, direction, neighbor, level, blockPos, neighborPos);
	}

	@Override
	public BlockState playerWillDestroy(Level level, BlockPos blockPos, BlockState blockState, Player player) {
		if (!level.isClientSide) {
			if(player.isCreative()) {
				DoublePlantBlock.preventDropFromBottomPart(level, blockPos, blockState, player);
			} else {
				dropResources(blockState, level, blockPos, null, player, player.getMainHandItem());
			}
		}

		return super.playerWillDestroy(level, blockPos, blockState, player);
	}

	@Override
	public boolean isPathfindable(BlockState blockState, PathComputationType type) {
		return false;
	}

	@Override @Nullable
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		BlockPos clickedPos = context.getClickedPos();
		Level level = context.getLevel();
		if (clickedPos.getY() < level.getMaxBuildHeight() - 1 && level.getBlockState(clickedPos.above()).canBeReplaced(context)) {
			return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection()).setValue(HALF, DoubleBlockHalf.LOWER);
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
	public PushReaction getPistonPushReaction(BlockState blockState) {
		return PushReaction.BLOCK;
	}

	@Override
	public long getSeed(BlockState blockState, BlockPos blockPos) {
		return Mth.getSeed(blockPos.getX(), blockPos.below(blockState.getValue(HALF) == DoubleBlockHalf.LOWER ? 0 : 1).getY(), blockPos.getZ());
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(HALF, FACING);
	}

	@Override
	protected MapCodec<WheeledStretcherBlock> codec() {
		return CODEC;
	}
}
