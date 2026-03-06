package com.hexagram2021.misc_twf.common.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * 白色垃圾袋方块喵~
 *
 * @author liudongyu
 */
public class TrashBagBlock extends Block {
	public static final MapCodec<TrashBagBlock> CODEC = simpleCodec(TrashBagBlock::new);

	protected static final VoxelShape SHAPE = Block.box(4, 0, 4, 12, 8, 12);

	public TrashBagBlock(Properties props) {
		super(props);
	}

	@SuppressWarnings("deprecation")
	@Override
	public VoxelShape getShape(BlockState blockState, BlockGetter level, BlockPos blockPos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	protected MapCodec<TrashBagBlock> codec() {
		return CODEC;
	}
}
