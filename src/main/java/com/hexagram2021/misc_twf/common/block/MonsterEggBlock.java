package com.hexagram2021.misc_twf.common.block;

import com.hexagram2021.misc_twf.common.block.entity.MonsterEggBlockEntity;
import com.hexagram2021.misc_twf.common.config.MISCTWFCommonConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

@SuppressWarnings("deprecation")
public class MonsterEggBlock extends BaseEntityBlock {
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	protected static final VoxelShape SHAPE = Block.box(3, 0, 3, 13, 12, 13);

	public MonsterEggBlock(Properties props) {
		super(props);
		this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, Boolean.FALSE));
	}

	@Override
	public RenderShape getRenderShape(BlockState blockState) {
		return RenderShape.MODEL;
	}

	@Override @Nullable
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		Level level = context.getLevel();
		BlockPos clickedPos = context.getClickedPos();
		FluidState fluidstate = level.getFluidState(clickedPos);

		BlockState blockstate = this.defaultBlockState().setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
		if (!level.getBlockState(clickedPos.below()).isAir()) {
			return blockstate;
		}

		return null;
	}

	@Override
	public boolean canSurvive(BlockState blockState, LevelReader level, BlockPos pos) {
		BlockPos belowPos = pos.below();
		BlockState belowState = level.getBlockState(belowPos);
		return belowState.isFaceSturdy(level, belowPos, Direction.UP);
	}

	@Override
	public BlockState updateShape(BlockState blockState, Direction direction, BlockState neighbor, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
		return !blockState.canSurvive(level, pos) ? Blocks.AIR.defaultBlockState() : super.updateShape(blockState, direction, neighbor, level, pos, neighborPos);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(WATERLOGGED);
	}

	@Override
	public VoxelShape getShape(BlockState blockState, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	public void fallOn(Level level, BlockState blockState, BlockPos pos, Entity entity, float fallDistance) {
		super.fallOn(level, blockState, pos, entity, fallDistance);
		if (fallDistance > 1 && level.random.nextInt() < MISCTWFCommonConfig.POSSIBILITY_FALL_DESTROY_EGG.get()) {
			this.destroyEgg(level, pos, entity);
		}
	}

	@Override
	public void stepOn(Level level, BlockPos pos, BlockState blockState, Entity entity) {
		if (level.random.nextInt() < MISCTWFCommonConfig.POSSIBILITY_STEP_DESTROY_EGG.get()) {
			this.destroyEgg(level, pos, entity);
		}
	}

	@Override
	public void entityInside(BlockState blockState, Level level, BlockPos pos, Entity entity) {
		if (level.random.nextInt() < MISCTWFCommonConfig.POSSIBILITY_STEP_DESTROY_EGG.get()) {
			this.destroyEgg(level, pos, entity);
		}
	}

	@Override
	public void playerDestroy(Level level, Player player, BlockPos pos, BlockState blockState, @Nullable BlockEntity blockEntity, ItemStack stack) {
		super.playerDestroy(level, player, pos, blockState, blockEntity, stack);
		if(blockEntity instanceof MonsterEggBlockEntity monsterEggBlockEntity) {
			this.destroyEgg(level, pos, monsterEggBlockEntity);
			level.playSound(null, pos, SoundEvents.TURTLE_EGG_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
		}
	}

	private void destroyEgg(Level level, BlockPos pos, Entity entity) {
		if (!level.isClientSide) {
			if(level.getBlockEntity(pos) instanceof MonsterEggBlockEntity monsterEggBlockEntity) {
				this.destroyEgg(level, pos, monsterEggBlockEntity);
			}
			level.playSound(null, pos, SoundEvents.TURTLE_EGG_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
			level.destroyBlock(pos, false, entity);
		}
	}

	private void destroyEgg(Level level, BlockPos pos, MonsterEggBlockEntity monsterEggBlockEntity) {
		Entity monster = monsterEggBlockEntity.createMonster(level);
		if (monster != null) {
			monster.setPosRaw(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
			level.addFreshEntity(monster);
		}
	}

	@Override
	public FluidState getFluidState(BlockState blockState) {
		return blockState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(true) : super.getFluidState(blockState);
	}

	@Override @Nullable
	public MonsterEggBlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
		return new MonsterEggBlockEntity(blockPos, blockState);
	}
}
