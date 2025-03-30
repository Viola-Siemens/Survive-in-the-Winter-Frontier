package com.hexagram2021.misc_twf.common.block.entity;

import com.google.common.collect.ImmutableList;
import com.hexagram2021.misc_twf.common.block.DeadAnimalBlock;
import com.hexagram2021.misc_twf.common.register.MISCTWFBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

public class DeadAnimalBlockEntity extends BlockEntity {
	private static final String TAG_LOOTS = "Loots";
	private static final String TAG_AGE = "Age";

	private final List<ItemStack> loots;
	private int age;

	public DeadAnimalBlockEntity(BlockPos blockPos, BlockState blockState) {
		this(MISCTWFBlockEntities.DEAD_ANIMAL.get(), blockPos, blockState, ImmutableList.of(
				new ItemStack(Items.ROTTEN_FLESH),
				new ItemStack(Items.BONE)
		));
	}
	public DeadAnimalBlockEntity(BlockPos blockPos, BlockState blockState, List<ItemStack> loots) {
		this(MISCTWFBlockEntities.DEAD_ANIMAL.get(), blockPos, blockState, loots);
	}
	public DeadAnimalBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState, List<ItemStack> loots) {
		super(blockEntityType, blockPos, blockState);
		this.loots = Lists.newArrayList(loots.iterator());
		this.age = 96000;
	}

	public static void serverTick(Level level, BlockPos blockPos, BlockState blockState, DeadAnimalBlockEntity blockEntity) {
		blockEntity.age -= 1;
		if(blockEntity.age <= 0) {
			if(blockState.getBlock() instanceof DeadAnimalBlock deadAnimalBlock) {
				Block.popResource(level, blockPos, new ItemStack(Items.ROTTEN_FLESH, deadAnimalBlock.rottenFlesh()));
			}
			level.removeBlock(blockPos, false);
		}
		blockEntity.setChanged();
	}

	@Override
	public void load(CompoundTag nbt) {
		if(nbt.contains(TAG_LOOTS, Tag.TAG_LIST)) {
			this.loots.clear();
			ListTag listTag = nbt.getList(TAG_LOOTS, Tag.TAG_COMPOUND);
			listTag.forEach(tag -> {
				if(tag instanceof CompoundTag compoundTag) {
					this.loots.add(ItemStack.of(compoundTag));
				}
			});
		}
		if(nbt.contains(TAG_AGE, Tag.TAG_ANY_NUMERIC)) {
			this.age = nbt.getInt(TAG_AGE);
		}
	}

	@Override
	protected void saveAdditional(CompoundTag nbt) {
		ListTag listTag = new ListTag();
		this.loots.forEach(itemStack -> listTag.add(itemStack.save(new CompoundTag())));
		nbt.put(TAG_LOOTS, listTag);
		nbt.putInt(TAG_AGE, this.age);
	}

	public ItemStack cutBody(Level level, BlockPos blockPos, boolean simulated) {
		if(this.loots.isEmpty()) {
			level.removeBlock(blockPos, false);
			return ItemStack.EMPTY;
		}

		int index = level.getRandom().nextInt(this.loots.size());
		ItemStack ret = this.loots.get(index);
		if(!simulated) {
			this.loots.remove(index);
			if(this.loots.isEmpty()) {
				level.removeBlock(blockPos, false);
			}
			this.setChanged();
		}
		return ret;
	}

	public List<ItemStack> loots() {
		return this.loots;
	}
}
