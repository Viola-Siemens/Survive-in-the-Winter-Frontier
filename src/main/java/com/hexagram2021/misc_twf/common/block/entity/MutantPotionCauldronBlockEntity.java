package com.hexagram2021.misc_twf.common.block.entity;

import com.hexagram2021.misc_twf.common.register.MISCTWFBlockEntities;
import com.hexagram2021.misc_twf.common.register.MISCTWFItems;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class MutantPotionCauldronBlockEntity extends BlockEntity {
	public static final int FLAG_SECOND_BRAIN_CORE = 0x4;
	public static final int FLAG_GOLDEN_APPLE = 0x2;
	public static final int FLAG_SUGAR = 0x1;
	public static final int FLAG_COMPLETE = FLAG_SECOND_BRAIN_CORE | FLAG_GOLDEN_APPLE | FLAG_SUGAR;

	private int flag = 0;

	public MutantPotionCauldronBlockEntity(BlockPos blockPos, BlockState blockState) {
		super(MISCTWFBlockEntities.MUTANT_POTION_CAULDRON.get(), blockPos, blockState);
	}

	public void appendFlag(int append) {
		// assert (append & (-append)) == append;
		this.flag |= append;
		this.setChanged();
	}

	public int getFlag() {
		return this.flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
		this.setChanged();
	}

	public boolean containsFlag(int flag) {
		// assert (flag & (-flag)) == flag;
		return (this.flag & flag) == flag;
	}

	public boolean isComplete() {
		return (this.flag & FLAG_COMPLETE) == FLAG_COMPLETE;
	}

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

	@Override
	protected void saveAdditional(CompoundTag nbt) {
		super.saveAdditional(nbt);
		nbt.putInt("Flag", this.flag);
	}

	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		if(nbt.contains("Flag", Tag.TAG_INT)) {
			this.flag = nbt.getInt("Flag");
		}
	}

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public CompoundTag getUpdateTag() {
		return this.saveWithoutMetadata();
	}
}
