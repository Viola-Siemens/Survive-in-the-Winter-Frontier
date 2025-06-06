package com.hexagram2021.misc_twf.common.block;

import com.google.common.collect.Maps;
import com.hexagram2021.misc_twf.common.block.entity.MoldWorkbenchBlockEntity;
import com.hexagram2021.misc_twf.common.block.properties.MoldWorkbenchPart;
import com.hexagram2021.misc_twf.common.register.MISCTWFBlockEntities;
import com.hexagram2021.misc_twf.common.register.MISCTWFBlockStateProperties;
import com.simibubi.create.content.decoration.bracket.BracketedBlockEntityBehaviour;
import com.simibubi.create.content.equipment.wrench.IWrenchableWithBracket;
import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
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
import java.util.Map;
import java.util.Optional;

@SuppressWarnings("deprecation")
public class MoldWorkbenchBlock extends HorizontalKineticBlock implements IBE<MoldWorkbenchBlockEntity>, IWrenchableWithBracket {
	public static final EnumProperty<MoldWorkbenchPart> PART = MISCTWFBlockStateProperties.MOLD_WORKBENCH_PART;
	public static final BooleanProperty ARMED = MISCTWFBlockStateProperties.ARMED;
	private static final Map<Direction, Map<MoldWorkbenchPart, VoxelShape>> SHAPE_BY_PART = Util.make(() -> {
		Map<Direction, Map<MoldWorkbenchPart, VoxelShape>> ret = Maps.newEnumMap(Direction.class);
		VoxelShape baseBlock = Shapes.block();
		VoxelShape halfBlock = Block.box(0.0D, 8.0D, 0.0D, 16.0D, 16.0D, 16.0D);
		ret.put(Direction.NORTH, Util.make(() -> {
			Map<MoldWorkbenchPart, VoxelShape> north = Maps.newEnumMap(MoldWorkbenchPart.class);
			VoxelShape baseUpBottomShape = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);
			VoxelShape baseUpBackShape = Block.box(0.0D, 0.0D, 14.0D, 16.0D, 16.0D, 16.0D);
			VoxelShape baseUpShape = Shapes.or(baseUpBottomShape, baseUpBackShape);
			north.put(MoldWorkbenchPart.LEFT_UP, Shapes.or(baseUpShape, Block.box(2.0D, 2.0D, 2.0D, 14.0D, 6.0D, 14.0)));
			north.put(MoldWorkbenchPart.UP, baseUpBackShape);
			north.put(MoldWorkbenchPart.RIGHT_UP, Shapes.or(baseUpShape, Block.box(0.0D, 2.0D, 8.0D, 6.0D, 7.0D, 14.0D)));
			north.put(MoldWorkbenchPart.LEFT_BOTTOM, Shapes.join(baseBlock, Shapes.or(Block.box(0.0D, 0.0D, 0.0D, 12.0D, 8.0D, 16.0D), Block.box(12.0D, 0.0D, 4.0D, 16.0D, 8.0D, 12.0D)), BooleanOp.ONLY_FIRST));
			north.put(MoldWorkbenchPart.BOTTOM, halfBlock);
			north.put(MoldWorkbenchPart.RIGHT_BOTTOM, Shapes.join(baseBlock, Shapes.or(Block.box(4.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D), Block.box(0.0D, 0.0D, 4.0D, 4.0D, 8.0D, 12.0D)), BooleanOp.ONLY_FIRST));
			return north;
		}));
		ret.put(Direction.SOUTH, Util.make(() -> {
			Map<MoldWorkbenchPart, VoxelShape> north = Maps.newEnumMap(MoldWorkbenchPart.class);
			VoxelShape baseUpBottomShape = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);
			VoxelShape baseUpBackShape = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 2.0D);
			VoxelShape baseUpShape = Shapes.or(baseUpBottomShape, baseUpBackShape);
			north.put(MoldWorkbenchPart.LEFT_UP, Shapes.or(baseUpShape, Block.box(2.0D, 2.0D, 2.0D, 14.0D, 6.0D, 14.0)));
			north.put(MoldWorkbenchPart.UP, baseUpBackShape);
			north.put(MoldWorkbenchPart.RIGHT_UP, Shapes.or(baseUpShape, Block.box(10.0D, 2.0D, 2.0D, 16.0D, 7.0D, 8.0)));
			north.put(MoldWorkbenchPart.LEFT_BOTTOM, Shapes.join(baseBlock, Shapes.or(Block.box(4.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D), Block.box(0.0D, 0.0D, 4.0D, 4.0D, 8.0D, 12.0D)), BooleanOp.ONLY_FIRST));
			north.put(MoldWorkbenchPart.BOTTOM, halfBlock);
			north.put(MoldWorkbenchPart.RIGHT_BOTTOM, Shapes.join(baseBlock, Shapes.or(Block.box(0.0D, 0.0D, 0.0D, 12.0D, 8.0D, 16.0D), Block.box(12.0D, 0.0D, 4.0D, 16.0D, 8.0D, 12.0D)), BooleanOp.ONLY_FIRST));
			return north;
		}));
		ret.put(Direction.WEST, Util.make(() -> {
			Map<MoldWorkbenchPart, VoxelShape> north = Maps.newEnumMap(MoldWorkbenchPart.class);
			VoxelShape baseUpBottomShape = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);
			VoxelShape baseUpBackShape = Block.box(14.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
			VoxelShape baseUpShape = Shapes.or(baseUpBottomShape, baseUpBackShape);
			north.put(MoldWorkbenchPart.LEFT_UP, Shapes.or(baseUpShape, Block.box(2.0D, 2.0D, 2.0D, 14.0D, 6.0D, 14.0)));
			north.put(MoldWorkbenchPart.UP, baseUpBackShape);
			north.put(MoldWorkbenchPart.RIGHT_UP, Shapes.or(baseUpShape, Block.box(8.0D, 2.0D, 10.0D, 14.0D, 7.0D, 16.0)));
			north.put(MoldWorkbenchPart.LEFT_BOTTOM, Shapes.join(baseBlock, Shapes.or(Block.box(0.0D, 0.0D, 4.0D, 16.0D, 8.0D, 16.0D), Block.box(4.0D, 0.0D, 0.0D, 12.0D, 8.0D, 4.0D)), BooleanOp.ONLY_FIRST));
			north.put(MoldWorkbenchPart.BOTTOM, halfBlock);
			north.put(MoldWorkbenchPart.RIGHT_BOTTOM, Shapes.join(baseBlock, Shapes.or(Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 12.0D), Block.box(4.0D, 0.0D, 12.0D, 12.0D, 8.0D, 16.0D)), BooleanOp.ONLY_FIRST));
			return north;
		}));
		ret.put(Direction.EAST, Util.make(() -> {
			Map<MoldWorkbenchPart, VoxelShape> north = Maps.newEnumMap(MoldWorkbenchPart.class);
			VoxelShape baseUpBottomShape = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);
			VoxelShape baseUpBackShape = Block.box(0.0D, 0.0D, 0.0D, 2.0D, 16.0D, 16.0D);
			VoxelShape baseUpShape = Shapes.or(baseUpBottomShape, baseUpBackShape);
			north.put(MoldWorkbenchPart.LEFT_UP, Shapes.or(baseUpShape, Block.box(2.0D, 2.0D, 2.0D, 14.0D, 6.0D, 14.0)));
			north.put(MoldWorkbenchPart.UP, baseUpBackShape);
			north.put(MoldWorkbenchPart.RIGHT_UP, Shapes.or(baseUpShape, Block.box(2.0D, 2.0D, 0.0D, 8.0D, 7.0D, 6.0)));
			north.put(MoldWorkbenchPart.LEFT_BOTTOM, Shapes.join(baseBlock, Shapes.or(Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 12.0D), Block.box(4.0D, 0.0D, 12.0D, 12.0D, 8.0D, 16.0D)), BooleanOp.ONLY_FIRST));
			north.put(MoldWorkbenchPart.BOTTOM, halfBlock);
			north.put(MoldWorkbenchPart.RIGHT_BOTTOM, Shapes.join(baseBlock, Shapes.or(Block.box(0.0D, 0.0D, 4.0D, 16.0D, 8.0D, 16.0D), Block.box(4.0D, 0.0D, 0.0D, 12.0D, 8.0D, 4.0D)), BooleanOp.ONLY_FIRST));
			return north;
		}));
		return ret;
	});

	public MoldWorkbenchBlock(Properties props) {
		super(props);
		this.registerDefaultState(this.stateDefinition.any().setValue(PART, MoldWorkbenchPart.BOTTOM).setValue(ARMED, false).setValue(HORIZONTAL_FACING, Direction.NORTH));
	}

	@Override
	public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand hand, BlockHitResult result) {
		if (level.isClientSide) {
			return InteractionResult.SUCCESS;
		}
		MoldWorkbenchPart part = blockState.getValue(PART);
		Direction facing = blockState.getValue(HORIZONTAL_FACING);
		BlockPos left = blockPos.relative(facing.getClockWise());
		BlockPos right = blockPos.relative(facing.getCounterClockWise());
		switch (part) {
			case LEFT_UP -> blockPos = right.below();
			case UP -> blockPos = blockPos.below();
			case RIGHT_UP -> blockPos = left.below();
			case LEFT_BOTTOM -> blockPos = right;
			case RIGHT_BOTTOM -> blockPos = left;
		}
		if(level.getBlockEntity(blockPos) instanceof MoldWorkbenchBlockEntity moldWorkbench) {
			player.openMenu(moldWorkbench);
			//player.awardStat
		}
		return InteractionResult.CONSUME;
	}

	@Override
	public BlockState updateShape(BlockState blockState, Direction direction, BlockState neighbor, LevelAccessor level, BlockPos blockPos, BlockPos neighborPos) {
		MoldWorkbenchPart part = blockState.getValue(PART);
		Direction facing = blockState.getValue(HORIZONTAL_FACING);
		Direction right = facing.getCounterClockWise();
		Direction.Axis axis = direction.getAxis();
		MoldWorkbenchPart target;
		if(axis == Direction.Axis.Y) {
			target = part.moveVertical(direction.getStepY());
		} else if(axis == right.getAxis()) {
			int move = (direction.getStepX() + direction.getStepZ()) * (right.getStepX() + right.getStepZ());
			target = part.moveHorizontal(move);
		} else {
			target = null;
		}
		if(target != null) {
			return neighbor.is(this) && neighbor.getValue(PART) == target ?
					blockState.setValue(ARMED, neighbor.getValue(ARMED))
							.setValue(HORIZONTAL_FACING, neighbor.getValue(HORIZONTAL_FACING)) :
					Blocks.AIR.defaultBlockState();
		}
		return super.updateShape(blockState, direction, neighbor, level, blockPos, neighborPos);
	}

	@Override
	public void playerWillDestroy(Level level, BlockPos blockPos, BlockState blockState, Player player) {
		if (!level.isClientSide && player.isCreative()) {
			MoldWorkbenchPart part = blockState.getValue(PART);
			Direction facing = blockState.getValue(HORIZONTAL_FACING);
			BlockPos left = blockPos.relative(facing.getClockWise());
			BlockPos right = blockPos.relative(facing.getCounterClockWise());
			BlockPos leftLeft = blockPos.relative(facing.getClockWise(), 2);
			BlockPos rightRight = blockPos.relative(facing.getCounterClockWise(), 2);
			BlockPos[] toRemove = new BlockPos[5];
			switch (part) {
				case LEFT_UP -> {
					toRemove[0] = this.chainedDestroyPosOrNull(level, blockPos.below(), MoldWorkbenchPart.LEFT_BOTTOM);
					toRemove[1] = this.chainedDestroyPosOrNull(level, right, MoldWorkbenchPart.UP);
					toRemove[2] = this.chainedDestroyPosOrNull(level, right.below(), MoldWorkbenchPart.BOTTOM);
					toRemove[3] = this.chainedDestroyPosOrNull(level, rightRight, MoldWorkbenchPart.RIGHT_UP);
					toRemove[4] = this.chainedDestroyPosOrNull(level, rightRight.below(), MoldWorkbenchPart.RIGHT_BOTTOM);
				}
				case UP -> {
					toRemove[0] = this.chainedDestroyPosOrNull(level, blockPos.below(), MoldWorkbenchPart.BOTTOM);
					toRemove[1] = this.chainedDestroyPosOrNull(level, left, MoldWorkbenchPart.LEFT_UP);
					toRemove[2] = this.chainedDestroyPosOrNull(level, left.below(), MoldWorkbenchPart.LEFT_BOTTOM);
					toRemove[3] = this.chainedDestroyPosOrNull(level, right, MoldWorkbenchPart.RIGHT_UP);
					toRemove[4] = this.chainedDestroyPosOrNull(level, right.below(), MoldWorkbenchPart.RIGHT_BOTTOM);
				}
				case RIGHT_UP -> {
					toRemove[0] = this.chainedDestroyPosOrNull(level, blockPos.below(), MoldWorkbenchPart.RIGHT_BOTTOM);
					toRemove[1] = this.chainedDestroyPosOrNull(level, left, MoldWorkbenchPart.UP);
					toRemove[2] = this.chainedDestroyPosOrNull(level, left.below(), MoldWorkbenchPart.BOTTOM);
					toRemove[3] = this.chainedDestroyPosOrNull(level, leftLeft, MoldWorkbenchPart.LEFT_UP);
					toRemove[4] = this.chainedDestroyPosOrNull(level, leftLeft.below(), MoldWorkbenchPart.LEFT_BOTTOM);
				}
				case LEFT_BOTTOM -> {
					toRemove[0] = this.chainedDestroyPosOrNull(level, blockPos.above(), MoldWorkbenchPart.LEFT_UP);
					toRemove[1] = this.chainedDestroyPosOrNull(level, right, MoldWorkbenchPart.BOTTOM);
					toRemove[2] = this.chainedDestroyPosOrNull(level, right.above(), MoldWorkbenchPart.UP);
					toRemove[3] = this.chainedDestroyPosOrNull(level, rightRight, MoldWorkbenchPart.RIGHT_BOTTOM);
					toRemove[4] = this.chainedDestroyPosOrNull(level, rightRight.above(), MoldWorkbenchPart.RIGHT_UP);
				}
				case BOTTOM -> {
					toRemove[0] = this.chainedDestroyPosOrNull(level, blockPos.above(), MoldWorkbenchPart.UP);
					toRemove[1] = this.chainedDestroyPosOrNull(level, left, MoldWorkbenchPart.LEFT_BOTTOM);
					toRemove[2] = this.chainedDestroyPosOrNull(level, left.above(), MoldWorkbenchPart.LEFT_UP);
					toRemove[3] = this.chainedDestroyPosOrNull(level, right, MoldWorkbenchPart.RIGHT_BOTTOM);
					toRemove[4] = this.chainedDestroyPosOrNull(level, right.above(), MoldWorkbenchPart.RIGHT_UP);
				}
				case RIGHT_BOTTOM -> {
					toRemove[0] = this.chainedDestroyPosOrNull(level, blockPos.above(), MoldWorkbenchPart.RIGHT_UP);
					toRemove[1] = this.chainedDestroyPosOrNull(level, left, MoldWorkbenchPart.BOTTOM);
					toRemove[2] = this.chainedDestroyPosOrNull(level, left.above(), MoldWorkbenchPart.UP);
					toRemove[3] = this.chainedDestroyPosOrNull(level, leftLeft, MoldWorkbenchPart.LEFT_BOTTOM);
					toRemove[4] = this.chainedDestroyPosOrNull(level, leftLeft.above(), MoldWorkbenchPart.LEFT_UP);
				}
			}
			for(BlockPos pos: toRemove) {
				if(pos != null) {
					level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_SUPPRESS_DROPS | Block.UPDATE_ALL);
					level.levelEvent(player, LevelEvent.PARTICLES_DESTROY_BLOCK, pos, Block.getId(level.getBlockState(pos)));
				}
			}
		}
		super.playerWillDestroy(level, blockPos, blockState, player);
	}

	@Nullable
	private BlockPos chainedDestroyPosOrNull(Level level, BlockPos blockPos, MoldWorkbenchPart expected) {
		BlockState state = level.getBlockState(blockPos);
		return state.is(this) && state.getValue(PART) == expected ? blockPos : null;
	}

	@Override @Nullable
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		Direction direction = context.getHorizontalDirection().getOpposite();
		BlockPos clickedPos = context.getClickedPos();
		BlockPos left = clickedPos.relative(direction.getClockWise());
		BlockPos right = clickedPos.relative(direction.getCounterClockWise());
		BlockPos up = clickedPos.above();
		BlockPos leftUp = left.above();
		BlockPos rightUp = right.above();
		Level level = context.getLevel();
		if(level.getBlockState(left).canBeReplaced(context) && level.getWorldBorder().isWithinBounds(left) &&
				level.getBlockState(right).canBeReplaced(context) && level.getWorldBorder().isWithinBounds(right) &&
				level.getBlockState(up).canBeReplaced(context) && level.getWorldBorder().isWithinBounds(up) &&
				level.getBlockState(leftUp).canBeReplaced(context) && level.getWorldBorder().isWithinBounds(leftUp) &&
				level.getBlockState(rightUp).canBeReplaced(context) && level.getWorldBorder().isWithinBounds(rightUp)) {
			return this.defaultBlockState().setValue(HORIZONTAL_FACING, direction);
		}
		return null;
	}

	@Override @Nullable
	public Direction getPreferredHorizontalFacing(BlockPlaceContext context) {
		return null;
	}

	@Override
	public VoxelShape getShape(BlockState blockState, BlockGetter level, BlockPos blockPos, CollisionContext context) {
		return SHAPE_BY_PART.get(blockState.getValue(HORIZONTAL_FACING)).get(blockState.getValue(PART));
	}

	@Override
	public PushReaction getPistonPushReaction(BlockState blockState) {
		return PushReaction.BLOCK;
	}

	@Override
	public void onRemove(BlockState blockState, Level level, BlockPos blockPos, BlockState newState, boolean v) {
		if (!blockState.is(newState.getBlock())) {
			BlockEntity blockentity = level.getBlockEntity(blockPos);
			if (blockentity instanceof MoldWorkbenchBlockEntity moldWorkbenchBlockEntity) {
				if (level instanceof ServerLevel) {
					Containers.dropContents(level, blockPos, moldWorkbenchBlockEntity);
				}

				level.updateNeighbourForOutputSignal(blockPos, this);
			}

			super.onRemove(blockState, level, blockPos, newState, v);
		}
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(ARMED, PART);
	}

	@Override
	public void setPlacedBy(Level level, BlockPos blockPos, BlockState blockState, @Nullable LivingEntity livingEntity, ItemStack itemStack) {
		super.setPlacedBy(level, blockPos, blockState, livingEntity, itemStack);
		if (!level.isClientSide) {
			Direction facing = blockState.getValue(HORIZONTAL_FACING);
			BlockPos left = blockPos.relative(facing.getClockWise());
			BlockPos right = blockPos.relative(facing.getCounterClockWise());
			level.setBlock(left, blockState.setValue(PART, MoldWorkbenchPart.LEFT_BOTTOM), Block.UPDATE_ALL);
			level.setBlock(right, blockState.setValue(PART, MoldWorkbenchPart.RIGHT_BOTTOM), Block.UPDATE_ALL);
			level.setBlock(blockPos.above(), blockState.setValue(PART, MoldWorkbenchPart.UP), Block.UPDATE_ALL);
			level.setBlock(left.above(), blockState.setValue(PART, MoldWorkbenchPart.LEFT_UP), Block.UPDATE_ALL);
			level.setBlock(right.above(), blockState.setValue(PART, MoldWorkbenchPart.RIGHT_UP), Block.UPDATE_ALL);
			level.blockUpdated(blockPos, Blocks.AIR);
			blockState.updateNeighbourShapes(level, blockPos, Block.UPDATE_ALL);
		}
		if (itemStack.hasCustomHoverName()) {
			BlockEntity blockentity = level.getBlockEntity(blockPos);
			if (blockentity instanceof MoldWorkbenchBlockEntity moldWorkbenchBlockEntity) {
				moldWorkbenchBlockEntity.setCustomName(itemStack.getHoverName());
			}
		}
	}

	@Override
	public boolean canSurvive(BlockState blockState, LevelReader level, BlockPos blockPos) {
		BlockPos blockpos = blockPos.below();
		BlockState below = level.getBlockState(blockpos);
		MoldWorkbenchPart part = below.hasProperty(PART) ? below.getValue(PART) : null;
		return blockState.getValue(PART).canSurviveOn(part);
	}

	@Override
	public long getSeed(BlockState blockState, BlockPos blockPos) {
		Direction facing = blockState.getValue(HORIZONTAL_FACING);
		BlockPos main = switch (blockState.getValue(PART)) {
			case LEFT_UP -> blockPos.relative(facing.getCounterClockWise()).below();
			case UP -> blockPos.below();
			case RIGHT_UP -> blockPos.relative(facing.getClockWise()).below();
			case LEFT_BOTTOM -> blockPos.relative(facing.getCounterClockWise());
			case BOTTOM -> blockPos;
			case RIGHT_BOTTOM -> blockPos.relative(facing.getClockWise());
		};
		return Mth.getSeed(main.getX(), main.getY(), main.getZ());
	}

	@Override
	public boolean isPathfindable(BlockState blockState, BlockGetter level, BlockPos blockPos, PathComputationType type) {
		return false;
	}

	@Override
	public Class<MoldWorkbenchBlockEntity> getBlockEntityClass() {
		return MoldWorkbenchBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends MoldWorkbenchBlockEntity> getBlockEntityType() {
		return MISCTWFBlockEntities.MOLD_WORKBENCH.get();
	}

	@Override
	public Direction.Axis getRotationAxis(BlockState blockState) {
		return blockState.getValue(HORIZONTAL_FACING).getClockWise().getAxis();
	}

	@Override
	public SpeedLevel getMinimumRequiredSpeedLevel() {
		return SpeedLevel.SLOW;
	}

	@Override
	public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
		return state.getValue(ARMED) && face.getAxis() == state.getValue(HORIZONTAL_FACING).getClockWise().getAxis();
	}

	@Override
	public Optional<ItemStack> removeBracket(BlockGetter world, BlockPos pos, boolean inOnReplacedContext) {
		BracketedBlockEntityBehaviour behaviour = BlockEntityBehaviour.get(world, pos, BracketedBlockEntityBehaviour.TYPE);
		if (behaviour == null) {
			return Optional.empty();
		} else {
			BlockState bracket = behaviour.removeBracket(inOnReplacedContext);
			return bracket == null ? Optional.empty() : Optional.of(new ItemStack(bracket.getBlock()));
		}
	}

	@Override @Nullable
	public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
		return switch(blockState.getValue(PART)) {
			case BOTTOM, LEFT_BOTTOM -> IBE.super.newBlockEntity(blockPos, blockState);
			default -> null;
		};
	}
}
