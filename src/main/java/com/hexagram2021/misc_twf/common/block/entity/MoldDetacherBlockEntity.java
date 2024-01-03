package com.hexagram2021.misc_twf.common.block.entity;

import com.hexagram2021.misc_twf.common.recipe.MoldDetacherRecipe;
import com.hexagram2021.misc_twf.common.register.MISCTWFBlockEntities;
import com.hexagram2021.misc_twf.common.register.MISCTWFBlocks;
import com.hexagram2021.misc_twf.common.register.MISCTWFRecipeTypes;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Optional;

public class MoldDetacherBlockEntity extends KineticBlockEntity implements Container, WorldlyContainer, StackedContentsCompatible {
	public static final int SLOT_INPUT = 0;
	public static final int SLOT_RESULT1 = 1;
	public static final int SLOT_RESULT2 = 2;
	public static final int SLOT_RESULT3 = 3;
	public static final int MAX_RESULT_COUNT = 3;
	private static final int[] SLOTS_FOR_UP = new int[]{SLOT_INPUT};
	private static final int[] SLOTS_FOR_DOWN = new int[]{SLOT_RESULT3, SLOT_RESULT2, SLOT_RESULT1};
	private static final int[] SLOTS_FOR_SIDES = new int[]{SLOT_RESULT3, SLOT_RESULT2, SLOT_RESULT1, SLOT_INPUT};

	protected NonNullList<ItemStack> items = NonNullList.withSize(4, ItemStack.EMPTY);

	private static final int MAX_REMAINING_TIME = 2000;
	private float remainingTime = MAX_REMAINING_TIME;

	public MoldDetacherBlockEntity(BlockPos blockPos, BlockState blockState) {
		this(MISCTWFBlockEntities.MOLD_DETACHER.get(), blockPos, blockState);
	}
	protected MoldDetacherBlockEntity(BlockEntityType<MoldDetacherBlockEntity> type, BlockPos blockPos, BlockState blockState) {
		super(type, blockPos, blockState);
	}

	@Override
	public void read(CompoundTag nbt, boolean clientPacket) {
		super.read(nbt, clientPacket);
		this.remainingTime = nbt.getFloat("RemainingTime");
		this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
		ContainerHelper.loadAllItems(nbt, this.items);
	}

	@Override
	public void write(CompoundTag nbt, boolean clientPacket) {
		super.write(nbt, clientPacket);
		nbt.putFloat("RemainingTime", this.remainingTime);
		ContainerHelper.saveAllItems(nbt, this.items);
	}

	private static boolean isInput(@Nullable Level level, ItemStack itemStack) {
		if(level == null) {
			return false;
		}
		return level.getRecipeManager().getRecipeFor(MISCTWFRecipeTypes.MOLD_DETACHER.get(), new SimpleContainer(itemStack), level).isPresent();
	}

	private boolean canDetach() {
		assert this.level != null;
		return this.level.getRecipeManager()
				.getRecipeFor(MISCTWFRecipeTypes.MOLD_DETACHER.get(), this, this.level)
				.filter(
						moldDetacherRecipe -> moldDetacherRecipe.results().stream()
								.mapToInt(
										itemStack -> this.items.stream().noneMatch(
												slot -> ItemStack.isSameItemSameTags(itemStack, slot) && itemStack.getCount() + slot.getCount() <= slot.getMaxStackSize()
										) ? 1 : 0
								).sum() - this.items.stream().filter(ItemStack::isEmpty).count() <= 0
				).isPresent();
	}

	private void detach() {
		assert this.level != null;
		Optional<MoldDetacherRecipe> recipe = this.level.getRecipeManager()
				.getRecipeFor(MISCTWFRecipeTypes.MOLD_DETACHER.get(), new SimpleContainer(this.items.get(0)), this.level);
		if(recipe.isEmpty()) {
			return;
		}
		this.items.get(0).shrink(
				Arrays.stream(recipe.get().input().getItems())
						.filter(itemStack -> ItemStack.isSameItemSameTags(itemStack, this.items.get(0)))
						.findFirst().map(ItemStack::getCount).orElse(1)
		);
		NonNullList<ItemStack> results = recipe.get().results();
		results.forEach(itemStack -> {
			ItemStack remaining = itemStack.copy();
			for(int i = this.items.size() - 1; i >= 0; --i) {
				ItemStack slot = this.items.get(i);
				if(ItemStack.isSameItemSameTags(slot, remaining)) {
					int count = remaining.split(slot.getMaxStackSize() - slot.getCount()).getCount() + slot.getCount();
					slot.setCount(count);
					if(remaining.isEmpty()) {
						break;
					}
				} else if(slot.isEmpty()) {
					this.items.set(i, remaining.copy());
					remaining.setCount(0);
					break;
				}
			}
			if(!remaining.isEmpty()) {
				this.level.addFreshEntity(new ItemEntity(this.level, this.worldPosition.getX(), this.worldPosition.getY(), this.worldPosition.getZ(), remaining));
			}
		});
		this.setChanged();
	}

	@Override
	public void tick() {
		super.tick();
		assert this.level != null;
		if(this.level.isClientSide) {
			return;
		}
		if(this.isSpeedRequirementFulfilled()) {
			if(this.canDetach()) {
				this.remainingTime -= Math.abs(this.getSpeed());
				if(this.remainingTime <= 0) {
					this.remainingTime = MAX_REMAINING_TIME;
					this.detach();
				}
			} else {
				this.remainingTime = MAX_REMAINING_TIME;
			}
		}
	}

	@Override
	protected Block getStressConfigKey() {
		return MISCTWFBlocks.MOLD_DETACHER.get();
	}

	@Override
	public float calculateStressApplied() {
		float impact = 16.0F;
		this.lastStressApplied = impact;
		return impact;
	}

	@Override
	public int getContainerSize() {
		return this.items.size();
	}

	@Override
	public boolean isEmpty() {
		for(ItemStack itemstack : this.items) {
			if (!itemstack.isEmpty()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public ItemStack getItem(int index) {
		return this.items.get(index);
	}

	@Override
	public ItemStack removeItem(int index, int count) {
		return ContainerHelper.removeItem(this.items, index, count);
	}

	@Override
	public ItemStack removeItemNoUpdate(int index) {
		return ContainerHelper.takeItem(this.items, index);
	}

	@Override
	public void setItem(int index, ItemStack itemStack) {
		this.items.set(index, itemStack);
		if (itemStack.getCount() > this.getMaxStackSize()) {
			itemStack.setCount(this.getMaxStackSize());
		}
		this.setChanged();
	}

	@SuppressWarnings("ConstantConditions")
	@Override
	public boolean stillValid(Player player) {
		if (this.level.getBlockEntity(this.worldPosition) != this) {
			return false;
		} else {
			return player.distanceToSqr((double)this.worldPosition.getX() + 0.5D, (double)this.worldPosition.getY() + 0.5D, (double)this.worldPosition.getZ() + 0.5D) <= 64.0D;
		}
	}

	@Override
	public boolean canPlaceItem(int index, ItemStack itemStack) {
		if(index != SLOT_INPUT) {
			return false;
		}
		return isInput(this.level, itemStack);
	}

	@Override
	public int[] getSlotsForFace(Direction direction) {
		if (direction == Direction.DOWN) {
			return SLOTS_FOR_DOWN;
		}
		return direction == Direction.UP ? SLOTS_FOR_UP : SLOTS_FOR_SIDES;
	}

	@Override
	public boolean canPlaceItemThroughFace(int index, ItemStack itemStack, @Nullable Direction direction) {
		return this.canPlaceItem(index, itemStack);
	}

	@Override
	public boolean canTakeItemThroughFace(int index, ItemStack itemStack, Direction direction) {
		if(direction == Direction.UP || direction == Direction.DOWN) {
			return true;
		}
		return index != 0 || !this.isSpeedRequirementFulfilled();
	}

	@Override
	public void clearContent() {
		this.items.clear();
	}

	@Override
	public void fillStackedContents(StackedContents contents) {
		for(ItemStack itemstack : this.items) {
			contents.accountStack(itemstack);
		}
	}

	LazyOptional<? extends IItemHandler>[] handlers = SidedInvWrapper.create(this, Direction.UP, Direction.DOWN, Direction.NORTH);

	@Override @NotNull
	public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
		if (!this.remove && facing != null && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			if (facing == Direction.UP) {
				return this.handlers[0].cast();
			}
			if (facing == Direction.DOWN) {
				return this.handlers[1].cast();
			}
			return this.handlers[2].cast();
		}
		return super.getCapability(capability, facing);
	}

	@Override
	public void invalidateCaps() {
		super.invalidateCaps();
		for (LazyOptional<? extends IItemHandler> handler : this.handlers) {
			handler.invalidate();
		}
	}

	@Override
	public void reviveCaps() {
		super.reviveCaps();
		this.handlers = SidedInvWrapper.create(this, Direction.UP, Direction.DOWN, Direction.NORTH);
	}
}
