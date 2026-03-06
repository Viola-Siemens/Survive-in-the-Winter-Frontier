package com.hexagram2021.misc_twf.common.block.entity;

import com.hexagram2021.misc_twf.common.register.MISCTWFBlockEntities;
import com.hexagram2021.misc_twf.common.register.MISCTWFItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 突变药水炼药锅方块实体喵~
 * 用于记录玩家已投入的合成材料，通过位标记的方式存储喵~
 * 合成深渊病毒疫苗需要三种材料：第二脑核心、金苹果和糖喵~
 *
 * @author liudongyu
 */
public class MutantPotionCauldronBlockEntity extends BlockEntity {
	/** 第二脑核心的位标记（0x4 = 0b100）喵~ */
	public static final int FLAG_SECOND_BRAIN_CORE = 0x4;
	/** 金苹果的位标记（0x2 = 0b010）喵~ */
	public static final int FLAG_GOLDEN_APPLE = 0x2;
	/** 糖的位标记（0x1 = 0b001）喵~ */
	public static final int FLAG_SUGAR = 0x1;
	/** 合成完成的位标记（所有材料的按位或）喵~ */
	public static final int FLAG_COMPLETE = FLAG_SECOND_BRAIN_CORE | FLAG_GOLDEN_APPLE | FLAG_SUGAR;

	/** 当前已投入的材料标记喵~ */
	private int flag = 0;

	/**
	 * 构造函数，创建一个突变药水炼药锅方块实体喵~
	 *
	 * @param blockPos 方块位置喵~
	 * @param blockState 方块状态喵~
	 */
	public MutantPotionCauldronBlockEntity(BlockPos blockPos, BlockState blockState) {
		super(MISCTWFBlockEntities.MUTANT_POTION_CAULDRON.get(), blockPos, blockState);
	}

	/**
	 * 添加一个材料标记喵~
	 *
	 * @param append 要添加的标记（必须是 2 的幂次，即只有一位为 1）喵~
	 */
	public void appendFlag(int append) {
		// assert (append & (-append)) == append
		// 使用按位或将新标记添加到现有标记中喵~
		this.flag |= append;
		this.setChanged();
	}

	/**
	 * 获取当前的材料标记喵~
	 *
	 * @return 当前标记值喵~
	 */
	public int getFlag() {
		return this.flag;
	}

	/**
	 * 设置材料标记喵~
	 *
	 * @param flag 新的标记值喵~
	 */
	public void setFlag(int flag) {
		this.flag = flag;
		this.setChanged();
	}

	/**
	 * 检查是否包含指定的材料标记喵~
	 *
	 * @param flag 要检查的标记喵~
	 * @return 如果包含该标记则返回 true 喵~
	 */
	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	public boolean containsFlag(int flag) {
		// assert (flag & (-flag)) == flag
		// 使用按位与检查是否包含指定标记喵~
		return (this.flag & flag) == flag;
	}

	/**
	 * 检查是否所有材料都已投入完成喵~
	 *
	 * @return 如果三种材料都已投入则返回 true 喵~
	 */
	public boolean isComplete() {
		return (this.flag & FLAG_COMPLETE) == FLAG_COMPLETE;
	}

	/**
	 * 根据物品堆获取对应的材料标记喵~
	 *
	 * @param itemStack 物品堆喵~
	 * @return 物品对应的标记，如果不是合成材料则返回 0 喵~
	 */
	public static int getFlag(ItemStack itemStack) {
		if(itemStack.is(MISCTWFItems.Materials.SECOND_BRAIN_CORE.get())) {
			return FLAG_SECOND_BRAIN_CORE;
		}
		if(itemStack.is(Items.GOLDEN_APPLE)) {
			return FLAG_GOLDEN_APPLE;
		}
		if(itemStack.is(Items.SUGAR)) {
			return FLAG_SUGAR;
		}
		return 0;
	}

	/**
	 * 保存方块实体数据到 NBT 喵~
	 *
	 * @param nbt NBT 标签喵~
	 * @param provider 注册表提供者喵~
	 */
	@Override
	protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider provider) {
		super.saveAdditional(nbt, provider);
		nbt.putInt("Flag", this.flag);
	}

	/**
	 * 从 NBT 加载方块实体数据喵~
	 *
	 * @param nbt NBT 标签喵~
	 * @param provider 注册表提供者喵~
	 */
	@Override
	public void loadAdditional(CompoundTag nbt, HolderLookup.Provider provider) {
		super.loadAdditional(nbt, provider);
		if(nbt.contains("Flag", Tag.TAG_INT)) {
			this.flag = nbt.getInt("Flag");
		}
	}

	/**
	 * 获取用于客户端同步的数据包喵~
	 *
	 * @return 客户端方块实体数据包喵~
	 */
	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	/**
	 * 获取用于客户端同步的更新标签喵~
	 *
	 * @param provider 注册表提供者喵~
	 * @return 更新 NBT 标签喵~
	 */
	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
		return this.saveWithoutMetadata(provider);
	}
}
