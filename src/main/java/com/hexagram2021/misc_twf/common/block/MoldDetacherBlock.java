package com.hexagram2021.misc_twf.common.block;

import com.hexagram2021.misc_twf.common.block.entity.MoldDetacherBlockEntity;
import com.hexagram2021.misc_twf.common.register.MISCTWFBlockEntities;
import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

@SuppressWarnings("deprecation")
public class MoldDetacherBlock extends HorizontalKineticBlock implements IBE<MoldDetacherBlockEntity> {

	public MoldDetacherBlock(Properties props) {
		super(props);
	}

	@Override
	public VoxelShape getShape(BlockState blockState, BlockGetter level, BlockPos blockPos, CollisionContext context) {
		return Block.box(0.0D, 2.0D, 0.0D, 16.0D, 16.0D, 16.0D);
	}

	@Override
	public boolean isPathfindable(BlockState blockState, BlockGetter level, BlockPos blockPos, PathComputationType type) {
		return false;
	}

	@Override
	public RenderShape getRenderShape(BlockState blockState) {
		return RenderShape.MODEL;
	}

	@Override
	public PushReaction getPistonPushReaction(BlockState blockState) {
		return PushReaction.DESTROY;
	}

	@Override
	public void onRemove(BlockState blockState, Level level, BlockPos blockPos, BlockState newState, boolean v) {
		if (!blockState.is(newState.getBlock())) {
			BlockEntity blockentity = level.getBlockEntity(blockPos);
			if (blockentity instanceof MoldDetacherBlockEntity moldDetacherBlockEntity) {
				if (level instanceof ServerLevel) {
					Containers.dropContents(level, blockPos, moldDetacherBlockEntity);
				}

				level.updateNeighbourForOutputSignal(blockPos, this);
			}

			super.onRemove(blockState, level, blockPos, newState, v);
		}
	}

	@Override
	public Class<MoldDetacherBlockEntity> getBlockEntityClass() {
		return MoldDetacherBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends MoldDetacherBlockEntity> getBlockEntityType() {
		return MISCTWFBlockEntities.MOLD_DETACHER.get();
	}

	@Override
	public Direction.Axis getRotationAxis(BlockState blockState) {
		return blockState.getValue(HORIZONTAL_FACING).getAxis();
	}

	@Override
	public SpeedLevel getMinimumRequiredSpeedLevel() {
		return SpeedLevel.SLOW;
	}

	@Override
	public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
		return face == state.getValue(HORIZONTAL_FACING);
	}
}
