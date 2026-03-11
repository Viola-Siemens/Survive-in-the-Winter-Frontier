package com.hexagram2021.misc_twf.common.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * 小型垃圾袋方块喵~
 *
 * @author liudongyu
 */
public class TinyTrashBagBlock extends Block {
	public static final MapCodec<TinyTrashBagBlock> CODEC = simpleCodec(TinyTrashBagBlock::new);

	protected static final VoxelShape SHAPE = Block.box(6, 0, 5.5, 10, 4, 10.5);

	/**
	 * 构造一个小型垃圾袋方块实例喵~
	 *
	 * @param props 方块属性喵~
	 */
	public TinyTrashBagBlock(Properties props) {
		super(props);
	}

	@Override
	public VoxelShape getShape(BlockState blockState, BlockGetter level, BlockPos blockPos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	protected MapCodec<TinyTrashBagBlock> codec() {
		return CODEC;
	}
}
