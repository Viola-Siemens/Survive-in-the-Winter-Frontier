package com.hexagram2021.misc_twf.common.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * 废纸方块喵~
 *
 * @author liudongyu
 */
public class WastepaperBlock extends Block {
	public static final MapCodec<WastepaperBlock> CODEC = simpleCodec(WastepaperBlock::new);

	private static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);

	public WastepaperBlock(Properties props) {
		super(props);
	}

	@SuppressWarnings("deprecation")
	@Override
	public VoxelShape getShape(BlockState blockState, BlockGetter level, BlockPos blockPos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	protected MapCodec<WastepaperBlock> codec() {
		return CODEC;
	}
}
