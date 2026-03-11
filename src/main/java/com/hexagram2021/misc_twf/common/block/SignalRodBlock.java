package com.hexagram2021.misc_twf.common.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * 信号棒方块，可以被活塞推动时销毁喵~
 *
 * @author liudongyu
 */
public class SignalRodBlock extends Block {
	public static final MapCodec<SignalRodBlock> CODEC = simpleCodec(SignalRodBlock::new);

    private static final VoxelShape SHAPE = Block.box(3.0D, 0.0D, 3.0D, 13.0D, 3.0D, 13.0D);

    /**
     * 构造一个信号棒方块实例喵~
     *
     * @param prop 方块属性喵~
     */
    public SignalRodBlock(Properties prop) {
        super(prop);
    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter level, BlockPos blockPos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.DESTROY;
    }

	@Override
	protected MapCodec<SignalRodBlock> codec() {
		return CODEC;
	}
}
