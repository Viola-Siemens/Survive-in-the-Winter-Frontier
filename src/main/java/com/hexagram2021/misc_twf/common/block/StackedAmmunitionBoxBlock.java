package com.hexagram2021.misc_twf.common.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

/**
 * 堆叠弹药箱方块喵~
 *
 * @author liudongyu
 */
public class StackedAmmunitionBoxBlock extends HorizontalDirectionalBlock {
	public static final MapCodec<StackedAmmunitionBoxBlock> CODEC = simpleCodec(StackedAmmunitionBoxBlock::new);

    /**
     * 构造一个堆叠弹药箱方块实例，默认朝向为北喵~
     *
     * @param properties 方块属性喵~
     */
    public StackedAmmunitionBoxBlock(Properties properties) {
        super(properties);
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
	protected MapCodec<StackedAmmunitionBoxBlock> codec() {
		return CODEC;
	}
}
