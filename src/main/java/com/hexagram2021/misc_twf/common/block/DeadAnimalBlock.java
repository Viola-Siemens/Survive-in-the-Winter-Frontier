package com.hexagram2021.misc_twf.common.block;

import com.google.common.collect.Lists;
import com.hexagram2021.misc_twf.common.block.entity.DeadAnimalBlockEntity;
import com.hexagram2021.misc_twf.common.data_component.DeadAnimalData;
import com.hexagram2021.misc_twf.common.register.MISCTWFBlockEntities;
import com.hexagram2021.misc_twf.common.register.MISCTWFDataComponentTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.enchantment.Enchantment;
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
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

/**
 * 动物尸体方块，可以用刀具切割获取战利品喵~
 *
 * @author liudongyu
 */
@SuppressWarnings("deprecation")
public class DeadAnimalBlock extends BaseEntityBlock {
	public static final MapCodec<DeadAnimalBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			ItemStack.CODEC.listOf().<Supplier<List<ItemStack>>>xmap(list -> () -> list, Supplier::get)
					.fieldOf("loots_supplier").forGetter(deadAnimal -> deadAnimal.lootsSupplier),
			Codec.INT.fieldOf("rotten_flesh").forGetter(deadAnimal -> deadAnimal.rottenFlesh),
			propertiesCodec()
	).apply(instance, DeadAnimalBlock::new));
	private final Supplier<List<ItemStack>> lootsSupplier;
	private final int rottenFlesh;

	public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

	/**
	 * 构造动物尸体方块
	 * @param lootsSupplier	战利品
	 * @param rottenFlesh	完全腐烂后掉落的腐肉数量
	 * @param props			方块属性
	 */
	public DeadAnimalBlock(Supplier<List<ItemStack>> lootsSupplier, int rottenFlesh, Properties props) {
		super(props);
		this.lootsSupplier = lootsSupplier;
		this.rottenFlesh = rottenFlesh;
	}

	private static final TagKey<Item> KNIVES = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("forge", "tools/knives"));

	@Override
	public ItemInteractionResult useItemOn(ItemStack itemStack, BlockState blockState, Level level, BlockPos blockPos,
										   Player player, InteractionHand hand, BlockHitResult blockHitResult) {
		if(itemStack.is(KNIVES)) {
			BlockEntity blockEntity = level.getBlockEntity(blockPos);
			if(blockEntity instanceof DeadAnimalBlockEntity deadAnimalBlockEntity && !level.isClientSide) {
				Holder<Enchantment> looting = player.level().registryAccess()
						.lookupOrThrow(Registries.ENCHANTMENT)
						.getOrThrow(Enchantments.LOOTING);
				ItemStack loot = deadAnimalBlockEntity.cutBody(
						level, blockPos,
						level.getRandom().nextDouble() < itemStack.getEnchantmentLevel(looting) / 10.0D
				);
				if(!loot.isEmpty()) {
					Block.popResource(level, blockPos.above(), loot);
				}
			}
			return ItemInteractionResult.CONSUME;
		}
		return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
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
		return new DeadAnimalBlockEntity(blockPos, blockState, this.lootsSupplier.get());
	}

	@Override @Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> type) {
		return level.isClientSide ? null : createTickerHelper(type, MISCTWFBlockEntities.DEAD_ANIMAL.get(), DeadAnimalBlockEntity::serverTick);
	}

	@Override
	public List<ItemStack> getDrops(BlockState blockState, LootParams.Builder builder) {
		Entity entity = builder.getOptionalParameter(LootContextParams.THIS_ENTITY);
		ItemStack tool = builder.getOptionalParameter(LootContextParams.TOOL);
		BlockEntity blockEntity = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
		if(entity != null && tool != null && tool.is(KNIVES) && blockEntity instanceof DeadAnimalBlockEntity deadAnimalBlockEntity) {
			List<ItemStack> list = Lists.newArrayList();
			int i = 0;
			int cnt = 0;
			List<ItemStack> originLoots = deadAnimalBlockEntity.loots();
			Holder<Enchantment> looting = builder.getLevel().registryAccess()
					.lookupOrThrow(Registries.ENCHANTMENT)
					.getOrThrow(Enchantments.LOOTING);
			double possibility = Math.tanh(EnchantmentHelper.getItemEnchantmentLevel(looting, tool) / 10.0D);
			while(i < originLoots.size()) {
				list.add(originLoots.get(i).copy());
				if(entity.level().random.nextDouble() >= possibility || cnt >= 256) {
					i += 1;
					cnt = 0;
				} else {
					cnt += 1;
				}
			}
			return list;
		}
		return super.getDrops(blockState, builder);
	}

	@Override
	public void setPlacedBy(Level level, BlockPos blockPos, BlockState blockState, @Nullable LivingEntity livingEntity, ItemStack itemStack) {
		super.setPlacedBy(level, blockPos, blockState, livingEntity, itemStack);
		if(level.getBlockEntity(blockPos) instanceof DeadAnimalBlockEntity deadAnimalBlockEntity) {
			DeadAnimalData deadAnimalData = itemStack.get(MISCTWFDataComponentTypes.DEAD_ANIMAL_DATA);
			if(deadAnimalData != null) {
				deadAnimalBlockEntity.setLoots(deadAnimalData.loots());
				deadAnimalBlockEntity.setAge(deadAnimalData.age());
			}
		}
	}

	/**
	 * 获取尸体完全腐烂后掉落的腐肉数量
	 * @return 腐肉数量
	 */
	public int rottenFlesh() {
		return this.rottenFlesh;
	}

	@Override
	protected MapCodec<DeadAnimalBlock> codec() {
		return CODEC;
	}
}
