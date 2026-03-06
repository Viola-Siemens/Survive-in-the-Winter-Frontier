package com.hexagram2021.misc_twf.common.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.ItemAbilities;

/**
 * 钉在墙上的尸体方块，可以用剑快速破坏喵~
 *
 * @author liudongyu
 */
@SuppressWarnings("deprecation")
public class NailedBodyBlock extends HorizontalDirectionalBlock {
	public static final MapCodec<NailedBodyBlock> CODEC = simpleCodec(NailedBodyBlock::new);
	private static final VoxelShape NORTH_SHAPE = Block.box(2.0D, -14.0D, 12.0D, 14.0D, 16.0D, 16.0D);
	private static final VoxelShape SOUTH_SHAPE = Block.box(2.0D, -14.0D, 0.0D, 14.0D, 16.0D, 4.0D);
	private static final VoxelShape WEST_SHAPE = Block.box(12.0D, -14.0D, 2.0D, 16.0D, 16.0D, 14.0D);
	private static final VoxelShape EAST_SHAPE = Block.box(0.0D, -14.0D, 2.0D, 4.0D, 16.0D, 14.0D);

	public NailedBodyBlock(Properties props) {
		super(props);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
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
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Override
	public float getDestroyProgress(BlockState blockState, Player player, BlockGetter level, BlockPos blockPos) {
		if(player.getMainHandItem().canPerformAction(ItemAbilities.SWORD_DIG)) {
			return 1.0F;
		}
		return super.getDestroyProgress(blockState, player, level, blockPos);
	}

	@Override
	protected MapCodec<NailedBodyBlock> codec() {
		return CODEC;
	}
}
