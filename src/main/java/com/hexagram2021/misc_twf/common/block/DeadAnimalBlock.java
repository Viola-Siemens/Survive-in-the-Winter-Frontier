package com.hexagram2021.misc_twf.common.block;

import com.hexagram2021.misc_twf.common.block.entity.DeadAnimalBlockEntity;
import com.hexagram2021.misc_twf.common.register.MISCTWFBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;
import java.util.List;

@SuppressWarnings("deprecation")
public class DeadAnimalBlock extends BaseEntityBlock {
	private final List<ItemStack> loots;
	private final int rottenFlesh;

	public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

	public DeadAnimalBlock(List<ItemStack> loots, int rottenFlesh, Properties props) {
		super(props);
		this.loots = loots;
		this.rottenFlesh = rottenFlesh;
	}

	private static final TagKey<Item> KNIVES = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("forge", "tools/knives"));
	@Override
	public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player,
								 InteractionHand hand, BlockHitResult blockHitResult) {
		ItemStack itemStack = player.getItemInHand(hand);
		if(itemStack.is(KNIVES)) {
			BlockEntity blockEntity = level.getBlockEntity(blockPos);
			if(blockEntity instanceof DeadAnimalBlockEntity deadAnimalBlockEntity && !level.isClientSide) {
				ItemStack loot = deadAnimalBlockEntity.cutBody(
						level, blockPos,
						level.getRandom().nextDouble() < Math.tanh(EnchantmentHelper.getItemEnchantmentLevel(Enchantments.MOB_LOOTING, itemStack) / 10.0D)
				);
				if(!loot.isEmpty()) {
					Block.popResource(level, blockPos.above(), loot);
				}
			}
			return InteractionResult.CONSUME;
		}
		return InteractionResult.PASS;
	}

	@Override
	public RenderShape getRenderShape(BlockState blockState) {
		return RenderShape.MODEL;
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
	}

	@Override
	public BlockState rotate(BlockState blockState, Rotation rotation) {
		return blockState.setValue(FACING, rotation.rotate(blockState.getValue(FACING)));
	}

	@Override
	public BlockState mirror(BlockState blockState, Mirror mirror) {
		return blockState.rotate(mirror.getRotation(blockState.getValue(FACING)));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Override
	public DeadAnimalBlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
		return new DeadAnimalBlockEntity(blockPos, blockState, loots);
	}

	@Override @Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> type) {
		return level.isClientSide ? null : createTickerHelper(type, MISCTWFBlockEntities.DEAD_ANIMAL.get(), DeadAnimalBlockEntity::serverTick);
	}

	@Override
	public List<ItemStack> getDrops(BlockState blockState, LootContext.Builder builder) {
		ItemStack tool = builder.getOptionalParameter(LootContextParams.TOOL);
		BlockEntity blockEntity = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
		if(tool != null && tool.is(KNIVES) && blockEntity instanceof DeadAnimalBlockEntity deadAnimalBlockEntity) {
			return deadAnimalBlockEntity.loots();
		}
		return super.getDrops(blockState, builder);
	}

	public int rottenFlesh() {
		return this.rottenFlesh;
	}
}
