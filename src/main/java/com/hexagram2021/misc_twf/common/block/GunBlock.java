package com.hexagram2021.misc_twf.common.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
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
import net.minecraftforge.common.ToolActions;

import java.util.Map;

public abstract class GunBlock extends HorizontalDirectionalBlock {

    public GunBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public abstract VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext context);

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
        if(player.getMainHandItem().canPerformAction(ToolActions.SWORD_DIG)) {
            return 1.0F;
        }
        return super.getDestroyProgress(blockState, player, level, blockPos);
    }
}
