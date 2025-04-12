package com.hexagram2021.misc_twf.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TinyTrashBagBlock extends Block {
	protected static final VoxelShape SHAPE = Block.box(6, 0, 5.5, 10, 4, 10.5);

	public TinyTrashBagBlock(Properties props) {
		super(props);
	}

	@SuppressWarnings("deprecation")
	@Override
	public VoxelShape getShape(BlockState blockState, BlockGetter level, BlockPos blockPos, CollisionContext context) {
		return SHAPE;
	}
}
