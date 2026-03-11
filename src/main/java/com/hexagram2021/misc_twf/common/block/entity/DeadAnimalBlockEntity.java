package com.hexagram2021.misc_twf.common.block.entity;

import com.google.common.collect.Lists;
import com.hexagram2021.misc_twf.common.block.DeadAnimalBlock;
import com.hexagram2021.misc_twf.common.register.MISCTWFBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Collection;
import java.util.List;

/**
 * 动物尸体方块实体,存储尸体的掉落物和存在时间喵~
 *
 * <p>玩家可以使用刀具从尸体上获取肉类和骨头,尸体会在一定时间后自动消失并掉落腐肉喵~</p>
 *
 * @author liudongyu
 */
public class DeadAnimalBlockEntity extends BlockEntity {
	/** NBT 标签键,用于存储掉落物列表喵~ */
	public static final String TAG_LOOTS = "Loots";
	/** NBT 标签键,用于存储尸体的剩余存在时间喵~ */
	public static final String TAG_AGE = "Age";

	/** 尸体的掉落物列表,每次切割会随机移除一个物品喵~ */
	private final List<ItemStack> loots;
	/** 尸体的剩余存在时间(游戏刻),时间耗尽后尸体消失喵~ */
	private int age;

	/**
	 * 使用默认掉落物(腐肉和骨头)构造动物尸体方块实体喵~
	 *
	 * @param blockPos 方块位置喵~
	 * @param blockState 方块状态喵~
	 */
	public DeadAnimalBlockEntity(BlockPos blockPos, BlockState blockState) {
		this(MISCTWFBlockEntities.DEAD_ANIMAL.get(), blockPos, blockState, List.of(
				new ItemStack(Items.ROTTEN_FLESH),
				new ItemStack(Items.BONE)
		));
	}
	/**
	 * 使用指定掉落物构造动物尸体方块实体喵~
	 *
	 * @param blockPos 方块位置喵~
	 * @param blockState 方块状态喵~
	 * @param loots 掉落物列表喵~
	 */
	public DeadAnimalBlockEntity(BlockPos blockPos, BlockState blockState, List<ItemStack> loots) {
		this(MISCTWFBlockEntities.DEAD_ANIMAL.get(), blockPos, blockState, loots);
	}
	/**
	 * 使用指定方块实体类型和掉落物构造动物尸体方块实体喵~
	 *
	 * @param blockEntityType 方块实体类型喵~
	 * @param blockPos 方块位置喵~
	 * @param blockState 方块状态喵~
	 * @param loots 掉落物列表喵~
	 */
	public DeadAnimalBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState, List<ItemStack> loots) {
		super(blockEntityType, blockPos, blockState);
		this.loots = Lists.newArrayList(loots.iterator());
		this.age = 96000;
	}

	/**
	 * 服务端 Tick 方法,每 Tick 减少尸体的存在时间,时间耗尽后移除尸体并掉落腐肉喵~
	 *
	 * @param level 世界对象喵~
	 * @param blockPos 方块位置喵~
	 * @param blockState 方块状态喵~
	 * @param blockEntity 方块实体喵~
	 */
	public static void serverTick(Level level, BlockPos blockPos, BlockState blockState, DeadAnimalBlockEntity blockEntity) {
		blockEntity.age -= 1;
		if(blockEntity.age <= 0) {
			// 时间耗尽,掉落腐肉并移除方块喵~
			if(blockState.getBlock() instanceof DeadAnimalBlock deadAnimalBlock) {
				Block.popResource(level, blockPos, new ItemStack(Items.ROTTEN_FLESH, deadAnimalBlock.rottenFlesh()));
			}
			level.removeBlock(blockPos, false);
		}
		blockEntity.setChanged();
	}

	@Override
	public void loadAdditional(CompoundTag nbt, HolderLookup.Provider provider) {
		super.loadAdditional(nbt, provider);
		// 从 NBT 加载掉落物列表喵~
		if(nbt.contains(TAG_LOOTS, Tag.TAG_LIST)) {
			this.loots.clear();
			ListTag listTag = nbt.getList(TAG_LOOTS, Tag.TAG_COMPOUND);
			listTag.forEach(tag -> {
				if(tag instanceof CompoundTag compoundTag) {
					this.loots.add(ItemStack.CODEC.parse(NbtOps.INSTANCE, compoundTag).getOrThrow());
				}
			});
		}
		// 从 NBT 加载剩余存在时间喵~
		if(nbt.contains(TAG_AGE, Tag.TAG_ANY_NUMERIC)) {
			this.age = nbt.getInt(TAG_AGE);
		}
	}

	@Override
	protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider provider) {
		super.saveAdditional(nbt, provider);
		// 保存掉落物列表到 NBT 喵~
		ListTag listTag = new ListTag();
		this.loots.forEach(itemStack -> listTag.add(itemStack.save(provider)));
		nbt.put(TAG_LOOTS, listTag);
		// 保存剩余存在时间到 NBT 喵~
		nbt.putInt(TAG_AGE, this.age);
	}

	/**
	 * 切割尸体,随机获取一个掉落物喵~
	 *
	 * @param level 世界对象喵~
	 * @param blockPos 方块位置喵~
	 * @param simulated 是否为模拟模式(true 时不会实际移除掉落物)喵~
	 * @return 获得的掉落物,如果尸体已空则返回空物品堆喵~
	 */
	public ItemStack cutBody(Level level, BlockPos blockPos, boolean simulated) {
		if(this.loots.isEmpty()) {
			// 掉落物已空,移除尸体方块喵~
			level.removeBlock(blockPos, false);
			return ItemStack.EMPTY;
		}

		// 随机选择一个掉落物喵~
		int index = level.getRandom().nextInt(this.loots.size());
		ItemStack ret = this.loots.get(index).copy();
		if(!simulated) {
			// 非模拟模式,实际移除掉落物喵~
			this.loots.remove(index);
			if(this.loots.isEmpty()) {
				// 所有掉落物已被取出,移除尸体方块喵~
				level.removeBlock(blockPos, false);
			}
			this.setChanged();
		}
		return ret;
	}

	/**
	 * 获取尸体的掉落物列表喵~
	 *
	 * @return 掉落物列表喵~
	 */
	public List<ItemStack> loots() {
		return this.loots;
	}
	/**
	 * 设置尸体的掉落物列表喵~
	 *
	 * @param loots 新的掉落物集合喵~
	 */
	public void setLoots(Collection<ItemStack> loots) {
		this.loots.clear();
		this.loots.addAll(loots);
	}

	/**
	 * 设置尸体的剩余存在时间喵~
	 *
	 * @param age 新的存在时间(游戏刻)喵~
	 */
	public void setAge(int age) {
		this.age = age;
	}
}
