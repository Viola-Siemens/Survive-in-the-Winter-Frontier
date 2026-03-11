package com.hexagram2021.misc_twf.common.block;

import com.hexagram2021.misc_twf.common.block.entity.RecoveryFurnaceBlockEntity;
import com.hexagram2021.misc_twf.common.block.properties.RecoveryFurnacePart;
import com.hexagram2021.misc_twf.common.register.MISCTWFBlockEntities;
import com.hexagram2021.misc_twf.common.register.MISCTWFBlockStateProperties;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

/**
 * 回收炉方块喵~
 * <p>
 * 这是一个 L 形多方块结构（底部 + 顶部 + 前方，共 3 个方块），继承自 {@link HorizontalDirectionalBlock}
 * 并实现 {@link EntityBlock} 接口。玩家可以使用回收炉将物品熔炼回收喵~
 * <p>
 * 结构中 {@link RecoveryFurnacePart#BOTTOM BOTTOM} 为主方块（持有方块实体），
 * {@link RecoveryFurnacePart#TOP TOP} 位于正上方，
 * {@link RecoveryFurnacePart#FRONT FRONT} 位于朝向方向的同层位置。
 * 使用回收炉会激怒附近的猪灵喵~
 *
 * @see RecoveryFurnacePart
 * @see RecoveryFurnaceBlockEntity
 * @author liudongyu
 */
@SuppressWarnings("deprecation")
public class RecoveryFurnaceBlock extends HorizontalDirectionalBlock implements EntityBlock {
	/** 本方块的编解码器喵~ */
	public static final MapCodec<RecoveryFurnaceBlock> CODEC = simpleCodec(RecoveryFurnaceBlock::new);
	/** 方块状态属性：多方块结构中的部件位置喵~ */
	public static final EnumProperty<RecoveryFurnacePart> PART = MISCTWFBlockStateProperties.RECOVERY_FURNACE_PART;
	/** 方块状态属性：回收炉是否处于打开状态喵~ */
	public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
	/** 方块状态属性：回收炉是否正在燃烧喵~ */
	public static final BooleanProperty LIT = BlockStateProperties.LIT;

	public RecoveryFurnaceBlock(Properties props) {
		super(props);
		this.registerDefaultState(this.stateDefinition.any().setValue(PART, RecoveryFurnacePart.BOTTOM).setValue(OPEN, false).setValue(LIT, false).setValue(FACING, Direction.NORTH));
	}

	@Override
	public ItemInteractionResult useItemOn(ItemStack itemStack, BlockState blockState, Level level, BlockPos blockPos,
										   Player player, InteractionHand hand, BlockHitResult result) {
		if (level.isClientSide) {
			return ItemInteractionResult.SUCCESS;
		}
		RecoveryFurnacePart part = blockState.getValue(PART);
		Direction facing = blockState.getValue(FACING);
		BlockPos back = blockPos.relative(facing.getOpposite());
		switch (part) {
			case FRONT -> blockPos = back;
			case TOP -> blockPos = blockPos.below();
		}
		if(level.getBlockEntity(blockPos) instanceof RecoveryFurnaceBlockEntity recoveryFurnace) {
			player.openMenu(recoveryFurnace);
			PiglinAi.angerNearbyPiglins(player, true);
		}
		return ItemInteractionResult.CONSUME;
	}

	@Override
	public BlockState updateShape(BlockState blockState, Direction direction, BlockState neighbor, LevelAccessor level, BlockPos blockPos, BlockPos neighborPos) {
		RecoveryFurnacePart part = blockState.getValue(PART);
		Direction facing = blockState.getValue(FACING);
		Direction.Axis axis = direction.getAxis();
		RecoveryFurnacePart target;
		if(axis == Direction.Axis.Y) {
			target = part.moveVertical(direction.getStepY());
		} else if(axis == facing.getAxis()) {
			int move = (direction.getStepX() + direction.getStepZ()) * (facing.getStepX() + facing.getStepZ());
			target = part.moveHorizontal(move);
		} else {
			target = null;
		}
		if(target != null) {
			return neighbor.is(this) && neighbor.getValue(PART) == target ? blockState.setValue(FACING, neighbor.getValue(FACING)).setValue(OPEN, neighbor.getValue(OPEN)).setValue(LIT, neighbor.getValue(LIT)) : Blocks.AIR.defaultBlockState();
		}
		return super.updateShape(blockState, direction, neighbor, level, blockPos, neighborPos);
	}

	@Override
	public BlockState playerWillDestroy(Level level, BlockPos blockPos, BlockState blockState, Player player) {
		if (!level.isClientSide && player.isCreative()) {
			RecoveryFurnacePart part = blockState.getValue(PART);
			Direction facing = blockState.getValue(FACING);
			BlockPos front = blockPos.relative(facing);
			BlockPos back = blockPos.relative(facing.getOpposite());
			BlockPos[] toRemove = new BlockPos[2];
			switch (part) {
				case TOP -> {
					toRemove[0] = this.chainedDestroyPosOrNull(level, blockPos.below(), RecoveryFurnacePart.BOTTOM);
					toRemove[1] = this.chainedDestroyPosOrNull(level, front.below(), RecoveryFurnacePart.FRONT);
				}
				case BOTTOM -> {
					toRemove[0] = this.chainedDestroyPosOrNull(level, blockPos.above(), RecoveryFurnacePart.TOP);
					toRemove[1] = this.chainedDestroyPosOrNull(level, front, RecoveryFurnacePart.FRONT);
				}
				case FRONT -> {
					toRemove[0] = this.chainedDestroyPosOrNull(level, back, RecoveryFurnacePart.BOTTOM);
					toRemove[1] = this.chainedDestroyPosOrNull(level, back.above(), RecoveryFurnacePart.TOP);
				}
			}
			for(BlockPos pos: toRemove) {
				if(pos != null) {
					level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_SUPPRESS_DROPS | Block.UPDATE_ALL);
					level.levelEvent(player, LevelEvent.PARTICLES_DESTROY_BLOCK, pos, Block.getId(level.getBlockState(pos)));
				}
			}
		}
		return super.playerWillDestroy(level, blockPos, blockState, player);
	}

	@Nullable
	private BlockPos chainedDestroyPosOrNull(Level level, BlockPos blockPos, RecoveryFurnacePart expected) {
		BlockState state = level.getBlockState(blockPos);
		return state.is(this) && state.getValue(PART) == expected ? blockPos : null;
	}

	@Override @Nullable
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		Direction direction = context.getHorizontalDirection().getOpposite();
		BlockPos clickedPos = context.getClickedPos();
		BlockPos front = clickedPos.relative(direction);
		BlockPos up = clickedPos.above();
		Level level = context.getLevel();
		if(level.getBlockState(front).canBeReplaced(context) && level.getWorldBorder().isWithinBounds(front) &&
				level.getBlockState(up).canBeReplaced(context) && level.getWorldBorder().isWithinBounds(up)) {
			return this.defaultBlockState().setValue(FACING, direction);
		}
		return null;
	}

	private static final VoxelShape TOP_SHAPE = Shapes.or(Block.box(2.5D, 10.0D, 2.5D, 13.5D, 27.0D, 13.5D), Block.box(2.0D, 7.5D, 2.0D, 14.0D, 10.0D, 14.0D), Block.box(1.5D, 5.0D, 1.5D, 14.5D, 7.5D, 14.5D), Block.box(1.0D, 2.5D, 1.0D, 15.0D, 5.0D, 15.0D), Block.box(0.5D, 0.0D, 0.5D, 15.5D, 2.5D, 15.5D));
	private static final VoxelShape FRONT_SHAPE = Shapes.join(Block.box(0.0D, 0.0D, 0.0D, 16.0D, 5.0D, 16.0D), Block.box(1.0D, 2.0D, 1.0D, 15.0D, 5.0D, 15.0D), BooleanOp.ONLY_FIRST);

	@Override
	public VoxelShape getShape(BlockState blockState, BlockGetter level, BlockPos blockPos, CollisionContext context) {
		return switch (blockState.getValue(PART)) {
			case TOP -> TOP_SHAPE;
			case FRONT -> FRONT_SHAPE;
			default -> super.getShape(blockState, level, blockPos, context);
		};
	}

	@Override
	public PushReaction getPistonPushReaction(BlockState blockState) {
		return PushReaction.BLOCK;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, PART, OPEN, LIT);
	}

	@Override @Nullable
	public RecoveryFurnaceBlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
		if(blockState.getValue(PART) == RecoveryFurnacePart.BOTTOM) {
			return new RecoveryFurnaceBlockEntity(blockPos, blockState);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override @Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> blockEntityType) {
		if(blockEntityType == MISCTWFBlockEntities.RECOVERY_FURNACE.get()) {
			if(level.isClientSide) {
				BlockEntityTicker<RecoveryFurnaceBlockEntity> ticker = RecoveryFurnaceBlockEntity::clientTick;
				return (BlockEntityTicker<T>)ticker;
			}
			BlockEntityTicker<RecoveryFurnaceBlockEntity> ticker = RecoveryFurnaceBlockEntity::serverTick;
			return (BlockEntityTicker<T>)ticker;
		}
		return null;
	}

	@Override
	public void setPlacedBy(Level level, BlockPos blockPos, BlockState blockState, @Nullable LivingEntity livingEntity, ItemStack itemStack) {
		super.setPlacedBy(level, blockPos, blockState, livingEntity, itemStack);
		if (!level.isClientSide) {
			Direction facing = blockState.getValue(FACING);
			level.setBlock(blockPos.above(), blockState.setValue(PART, RecoveryFurnacePart.TOP), Block.UPDATE_ALL);
			level.setBlock(blockPos.relative(facing), blockState.setValue(PART, RecoveryFurnacePart.FRONT), Block.UPDATE_ALL);
			level.blockUpdated(blockPos, Blocks.AIR);
			blockState.updateNeighbourShapes(level, blockPos, Block.UPDATE_ALL);
		}
	}

	@Override
	public boolean canSurvive(BlockState blockState, LevelReader level, BlockPos blockPos) {
		BlockPos blockpos = blockPos.below();
		BlockState below = level.getBlockState(blockpos);
		RecoveryFurnacePart part = below.hasProperty(PART) ? below.getValue(PART) : null;
		return blockState.getValue(PART).canSurviveOn(part);
	}

	@Override
	public long getSeed(BlockState blockState, BlockPos blockPos) {
		Direction facing = blockState.getValue(FACING);
		BlockPos main = switch (blockState.getValue(PART)) {
			case BOTTOM -> blockPos;
			case TOP -> blockPos.below();
			case FRONT -> blockPos.relative(facing.getOpposite());
		};
		return Mth.getSeed(main.getX(), main.getY(), main.getZ());
	}

	@Override
	public void tick(BlockState blockState, ServerLevel level, BlockPos blockPos, RandomSource random) {
		RecoveryFurnacePart part = blockState.getValue(PART);
		Direction facing = blockState.getValue(FACING);
		BlockPos back = blockPos.relative(facing.getOpposite());
		switch (part) {
			case FRONT -> blockPos = back;
			case TOP -> blockPos = blockPos.below();
		}
		BlockEntity blockentity = level.getBlockEntity(blockPos);
		if (blockentity instanceof RecoveryFurnaceBlockEntity recoveryFurnaceBlockEntity) {
			recoveryFurnaceBlockEntity.recheckOpen();
		}
	}

	@Override
	public boolean isPathfindable(BlockState blockState, PathComputationType type) {
		return false;
	}

	@Override
	public MapCodec<RecoveryFurnaceBlock> codec() {
		return CODEC;
	}
}
