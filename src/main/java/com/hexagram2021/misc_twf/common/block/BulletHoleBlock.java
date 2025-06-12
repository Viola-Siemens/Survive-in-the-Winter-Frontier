package com.hexagram2021.misc_twf.common.block;

import com.hexagram2021.misc_twf.common.register.MISCTWFBlockStateProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

@SuppressWarnings("deprecation")
public class BulletHoleBlock extends DirectionalBlock implements SimpleWaterloggedBlock {
	public static final IntegerProperty HOLES = MISCTWFBlockStateProperties.HOLES;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	private static final VoxelShape NORTH_AABB = Block.box(5, 5, 15, 11, 11, 16);
	private static final VoxelShape SOUTH_AABB = Block.box(5, 5, 0, 11, 11, 1);
	private static final VoxelShape EAST_AABB = Block.box(0, 5, 5, 1, 11, 11);
	private static final VoxelShape WEST_AABB = Block.box(15, 5, 5, 16, 11, 11);
	private static final VoxelShape UP_AABB = Block.box(5, 0, 5, 11, 1, 11);
	private static final VoxelShape DOWN_AABB = Block.box(5, 15, 5, 11, 16, 11);

	public BulletHoleBlock(Properties props) {
		super(props);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(HOLES, 1).setValue(WATERLOGGED, false));
	}

	@Override
	public VoxelShape getShape(BlockState blockState, BlockGetter level, BlockPos blockPos, CollisionContext context) {
		return switch(blockState.getValue(FACING)) {
			case NORTH -> NORTH_AABB;
			case SOUTH -> SOUTH_AABB;
			case EAST -> EAST_AABB;
			case WEST -> WEST_AABB;
			case DOWN -> DOWN_AABB;
			case UP -> UP_AABB;
		};
	}

	@Override
	public boolean canSurvive(BlockState blockState, LevelReader level, BlockPos blockPos) {
		Direction direction = blockState.getValue(FACING);
		BlockPos blockpos = blockPos.relative(direction.getOpposite());
		return level.getBlockState(blockpos).isFaceSturdy(level, blockpos, direction);
	}

	@Override
	public BlockState updateShape(BlockState blockState, Direction direction, BlockState neighbor, LevelAccessor level, BlockPos blockPos, BlockPos neighborPos) {
		if (blockState.getValue(WATERLOGGED)) {
			level.scheduleTick(blockPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
		}

		return direction == blockState.getValue(FACING).getOpposite() && !blockState.canSurvive(level, blockPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(blockState, direction, neighbor, level, blockPos, neighborPos);
	}

	private void decreaseEggs(Level level, BlockPos pos, BlockState blockState) {
		level.playSound(null, pos, SoundEvents.DEEPSLATE_STEP, SoundSource.BLOCKS, 0.7F, 0.9F + level.random.nextFloat() * 0.2F);
		int i = blockState.getValue(HOLES);
		if (i <= 1) {
			level.destroyBlock(pos, false);
		} else {
			level.setBlock(pos, blockState.setValue(HOLES, i - 1), 2);
			level.levelEvent(2001, pos, Block.getId(blockState));
		}
	}

	@Override
	public void playerDestroy(Level level, Player player, BlockPos pos, BlockState blockState, @Nullable BlockEntity blockEntity, ItemStack stack) {
		super.playerDestroy(level, player, pos, blockState, blockEntity, stack);
		this.decreaseEggs(level, pos, blockState);
	}

	@Override
	public boolean canBeReplaced(BlockState blockState, BlockPlaceContext context) {
		return (!context.isSecondaryUseActive() && context.getItemInHand().is(this.asItem()) && blockState.getValue(HOLES) < 3) || super.canBeReplaced(blockState, context);
	}

	@Override @Nullable
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		LevelAccessor levelaccessor = context.getLevel();
		BlockPos blockpos = context.getClickedPos();
		BlockState blockstate = context.getLevel().getBlockState(context.getClickedPos());
		return (blockstate.is(this) ? blockstate.setValue(HOLES, Math.min(3, blockstate.getValue(HOLES) + 1)) : this.defaultBlockState()).setValue(WATERLOGGED, levelaccessor.getFluidState(blockpos).getType() == Fluids.WATER).setValue(FACING, context.getClickedFace());
	}

	@Override
	public BlockState rotate(BlockState blockState, Rotation rotation) {
		return blockState.setValue(FACING, rotation.rotate(blockState.getValue(FACING)));
	}

	@Override
	public BlockState mirror(BlockState blockState, Mirror mirror) {
		return blockState.setValue(FACING, mirror.mirror(blockState.getValue(FACING)));
	}

	@Override
	public FluidState getFluidState(BlockState blockState) {
		return blockState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(blockState);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(WATERLOGGED, FACING, HOLES);
	}

	@Override
	public boolean isPathfindable(BlockState blockState, BlockGetter level, BlockPos blockPos, PathComputationType type) {
		return false;
	}

	@Override
	public PushReaction getPistonPushReaction(BlockState blockState) {
		return PushReaction.DESTROY;
	}
}
