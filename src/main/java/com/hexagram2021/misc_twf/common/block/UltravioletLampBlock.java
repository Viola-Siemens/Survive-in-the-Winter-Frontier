package com.hexagram2021.misc_twf.common.block;

import com.hexagram2021.misc_twf.common.block.entity.UltravioletLampBlockEntity;
import com.hexagram2021.misc_twf.common.register.MISCTWFBlockEntities;
import com.hexagram2021.misc_twf.server.MISCTWFSavedData;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

/**
 * 紫外线灯方块，支持六面朝向放置，可点亮以产生消毒效果喵~
 * <p>
 * 该方块是一个方块实体，内部可放置蓄电池以供电喵~
 * 点亮后会在周围产生末地烛粒子效果喵~
 *
 * @author liudongyu
 */
public class UltravioletLampBlock extends BaseEntityBlock {
	public static final MapCodec<UltravioletLampBlock> CODEC = simpleCodec(UltravioletLampBlock::new);
	protected static final double AABB_MIN = 6.0D;
	protected static final double AABB_MAX = 10.0D;
	protected static final VoxelShape X_AXIS_AABB = Block.box(0.0D, AABB_MIN, AABB_MIN, 16.0D, AABB_MAX, AABB_MAX);
	protected static final VoxelShape Y_AXIS_AABB = Block.box(AABB_MIN, 0.0D, AABB_MIN, AABB_MAX, 16.0D, AABB_MAX);
	protected static final VoxelShape Z_AXIS_AABB = Block.box(AABB_MIN, AABB_MIN, 0.0D, AABB_MAX, AABB_MAX, 16.0D);

	public static final DirectionProperty FACING = BlockStateProperties.FACING;
	public static final BooleanProperty LIT = BlockStateProperties.LIT;

	/**
	 * 构造紫外线灯方块，并设置默认朝向为向上、默认未点亮喵~
	 *
	 * @param props 方块属性喵~
	 */
	public UltravioletLampBlock(BlockBehaviour.Properties props) {
		super(props);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.UP).setValue(LIT, false));
	}

	@Override
	public VoxelShape getShape(BlockState blockState, BlockGetter level, BlockPos blockPos, CollisionContext context) {
		return switch (blockState.getValue(FACING).getAxis()) {
			case X -> X_AXIS_AABB;
			case Z -> Z_AXIS_AABB;
			case Y -> Y_AXIS_AABB;
		};
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
	public boolean isPathfindable(BlockState blockState, PathComputationType type) {
		return false;
	}

	@Override
	public RenderShape getRenderShape(BlockState blockState) {
		return RenderShape.MODEL;
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		Direction direction = context.getClickedFace();
		BlockState blockstate = context.getLevel().getBlockState(context.getClickedPos().relative(direction.getOpposite()));
		return blockstate.is(this) && blockstate.getValue(FACING) == direction ? this.defaultBlockState().setValue(FACING, direction.getOpposite()) : this.defaultBlockState().setValue(FACING, direction);
	}

	@Override
	public void animateTick(BlockState blockState, Level level, BlockPos blockPos, RandomSource random) {
		if(blockState.getValue(LIT)) {
			Direction direction = blockState.getValue(FACING);
			double x = blockPos.getX() + 0.55D - (random.nextDouble() * 0.1D);
			double y = blockPos.getY() + 0.55D - (random.nextDouble() * 0.1D);
			double z = blockPos.getZ() + 0.55D - (random.nextDouble() * 0.1D);
			double d = 0.4D - (random.nextDouble() + random.nextDouble()) * 0.4D;
			if (random.nextInt(4) == 0) {
				level.addParticle(
						ParticleTypes.END_ROD,
						x + direction.getStepX() * d, y + direction.getStepY() * d, z + direction.getStepZ() * d,
						random.nextGaussian() * 0.005D, random.nextGaussian() * 0.005D, random.nextGaussian() * 0.005D
				);
			}
		}
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING).add(LIT);
	}

	@Override
	public PushReaction getPistonPushReaction(BlockState blockState) {
		return PushReaction.DESTROY;
	}

	@Override
	public void onRemove(BlockState blockState, Level level, BlockPos blockPos, BlockState newState, boolean v) {
		if (!blockState.is(newState.getBlock())) {
			if(blockState.getValue(LIT)) {
				MISCTWFSavedData.destroyLamp(GlobalPos.of(level.dimension(), blockPos));
			}
			BlockEntity blockentity = level.getBlockEntity(blockPos);
			if (blockentity instanceof UltravioletLampBlockEntity ultravioletLampBlockEntity) {
				if (level instanceof ServerLevel) {
					Containers.dropContents(level, blockPos, ultravioletLampBlockEntity);
				}

				level.updateNeighbourForOutputSignal(blockPos, this);
			}

			super.onRemove(blockState, level, blockPos, newState, v);
		}
	}

	@Override
	public ItemInteractionResult useItemOn(ItemStack itemStack, BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand hand, BlockHitResult result) {
		if (level.isClientSide) {
			return ItemInteractionResult.SUCCESS;
		}
		this.openContainer(level, blockPos, player);
		return ItemInteractionResult.CONSUME;
	}

	/**
	 * 打开紫外线灯的容器界面喵~
	 *
	 * @param level 当前世界喵~
	 * @param blockPos 方块位置喵~
	 * @param player 交互的玩家喵~
	 */
	protected void openContainer(Level level, BlockPos blockPos, Player player) {
		BlockEntity blockentity = level.getBlockEntity(blockPos);
		if (blockentity instanceof UltravioletLampBlockEntity) {
			player.openMenu((MenuProvider)blockentity);
		}
	}

	@Override @Nullable
	public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
		return new UltravioletLampBlockEntity(blockPos, blockState);
	}

	@Override @Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> blockEntityType) {
		return level.isClientSide ? null : createTickerHelper(blockEntityType, MISCTWFBlockEntities.ULTRAVIOLET_LAMP.get(), UltravioletLampBlockEntity::serverTick);
	}

	@Override
	protected MapCodec<UltravioletLampBlock> codec() {
		return CODEC;
	}
}
