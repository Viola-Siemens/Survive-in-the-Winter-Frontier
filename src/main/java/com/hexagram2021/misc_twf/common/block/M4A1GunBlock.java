package com.hexagram2021.misc_twf.common.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Map;

public class M4A1GunBlock extends GunBlock{

    public static final Map<Direction, VoxelShape> SHAPES = Maps.newEnumMap(ImmutableMap.of(
            Direction.EAST, Block.box(4.5, 0, 1, 11.5, 6, 15),
            Direction.WEST, Block.box(4.5, 0, 1, 11.5, 6, 15),
            Direction.SOUTH,  Block.box(1, 0, 4.5, 15, 6, 11.5),
            Direction.NORTH,  Block.box(1, 0, 4.5, 15, 6, 11.5)
    ));

    public M4A1GunBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
        return SHAPES.get(state.getValue(FACING));
    }
}
