package com.hexagram2021.misc_twf.common.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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
 * 尸体方块，支持自定义尺寸，可以用剑快速破坏喵~
 *
 * @author liudongyu
 */
public class BodyBlock extends HorizontalDirectionalBlock {
	public static final MapCodec<BodyBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			Codec.INT.fieldOf("x_size").forGetter(b -> b.xSize),
			Codec.INT.fieldOf("z_size").forGetter(b -> b.zSize),
			propertiesCodec()
	).apply(instance, BodyBlock::new));

	protected final int xSize;
	protected final int zSize;
	protected final VoxelShape xShape;
	protected final VoxelShape zShape;

	public BodyBlock(int xSize, int zSize, Properties props) {
		super(props);
		this.xSize = xSize;
		this.zSize = zSize;
		this.xShape = Block.box(8.0D - zSize, 0.0D, 8.0D - xSize, 8.0D + zSize, 10.0D, 8.0D + xSize);
		this.zShape = Block.box(8.0D - xSize, 0.0D, 8.0D - zSize, 8.0D + xSize, 10.0D, 8.0D + zSize);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
	}

	@Override
	public VoxelShape getShape(BlockState blockState, BlockGetter level, BlockPos blockPos, CollisionContext context) {
		return switch (blockState.getValue(FACING)) {
			case DOWN, UP, NORTH, SOUTH -> this.zShape;
			case WEST, EAST -> this.xShape;
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
	protected MapCodec<BodyBlock> codec() {
		return CODEC;
	}
}
