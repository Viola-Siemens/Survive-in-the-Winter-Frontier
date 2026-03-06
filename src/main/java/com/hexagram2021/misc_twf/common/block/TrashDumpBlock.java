package com.hexagram2021.misc_twf.common.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

/**
 * 垃圾堆方块喵~
 *
 * @author liudongyu
 */
public class TrashDumpBlock extends HorizontalDirectionalBlock {
	public static final MapCodec<TrashDumpBlock> CODEC = simpleCodec(TrashDumpBlock::new);

	public TrashDumpBlock(Properties props) {
		super(props);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
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
	protected MapCodec<TrashDumpBlock> codec() {
		return CODEC;
	}
}
