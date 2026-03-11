package com.hexagram2021.misc_twf.common.block.entity;

import com.hexagram2021.misc_twf.common.block.MoldDetacherBlock;
import com.hexagram2021.misc_twf.common.recipe.MoldDetacherRecipe;
import com.hexagram2021.misc_twf.common.register.MISCTWFBlockEntities;
import com.hexagram2021.misc_twf.common.register.MISCTWFBlocks;
import com.hexagram2021.misc_twf.common.register.MISCTWFRecipeTypes;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Optional;

/**
 * 模具分离器方块实体,用于自动将完成工序的子弹模具分离为子弹和空模具喵~
 *
 * <p>模具分离器是一个机械动力设备,需要接收旋转应力才能工作喵~
 * 它可以自动处理输入的子弹模具,将其分离为子弹和空模具输出喵~</p>
 *
 * @author liudongyu
 */
public class MoldDetacherBlockEntity extends KineticBlockEntity implements Container, WorldlyContainer, StackedContentsCompatible {
	/** 输入槽位索引喵~ */
	public static final int SLOT_INPUT = 0;
	/** 第一个输出槽位索引喵~ */
	public static final int SLOT_RESULT1 = 1;
	/** 第二个输出槽位索引喵~ */
	public static final int SLOT_RESULT2 = 2;
	/** 第三个输出槽位索引��~ */
	public static final int SLOT_RESULT3 = 3;
	/** 最大输出槽位数量喵~ */
	public static final int MAX_RESULT_COUNT = 3;
	/** 从上方可访问的槽位索引数组喵~ */
	private static final int[] SLOTS_FOR_UP = new int[]{SLOT_INPUT};
	/** 从下方可访问的槽位索引数组(输出槽位)喵~ */
	private static final int[] SLOTS_FOR_DOWN = new int[]{SLOT_RESULT3, SLOT_RESULT2, SLOT_RESULT1};
	/** 从侧面可访问的槽位索引数组喵~ */
	private static final int[] SLOTS_FOR_SIDES = new int[]{SLOT_RESULT3, SLOT_RESULT2, SLOT_RESULT1, SLOT_INPUT};

	/** 物品槽位列表(1 个输入槽 + 3 个输出槽)喵~ */
	protected NonNullList<ItemStack> items = NonNullList.withSize(4, ItemStack.EMPTY);

	/** 最大加工时间(游戏刻)喵~ */
	private static final int MAX_REMAINING_TIME = 2000;
	/** 剩余加工时间(游戏刻)喵~ */
	private float remainingTime = MAX_REMAINING_TIME;

	/**
	 * 构造模具分离器方块实体喵~
	 *
	 * @param blockPos 方块位置喵~
	 * @param blockState 方块状态喵~
	 */
	public MoldDetacherBlockEntity(BlockPos blockPos, BlockState blockState) {
		this(MISCTWFBlockEntities.MOLD_DETACHER.get(), blockPos, blockState);
	}
	/**
	 * 使用指定方块实体类型构造模具分离器方块实体喵~
	 *
	 * @param type 方块实体类型喵~
	 * @param blockPos 方块位置喵~
	 * @param blockState 方块状态喵~
	 */
	protected MoldDetacherBlockEntity(BlockEntityType<MoldDetacherBlockEntity> type, BlockPos blockPos, BlockState blockState) {
		super(type, blockPos, blockState);
	}

	@Override
	public void read(CompoundTag nbt, HolderLookup.Provider provider, boolean clientPacket) {
		super.read(nbt, provider, clientPacket);
		this.remainingTime = nbt.getFloat("RemainingTime");
		this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
		ContainerHelper.loadAllItems(nbt, this.items, provider);
	}

	@Override
	public void write(CompoundTag nbt, HolderLookup.Provider provider, boolean clientPacket) {
		super.write(nbt, provider, clientPacket);
		nbt.putFloat("RemainingTime", this.remainingTime);
		ContainerHelper.saveAllItems(nbt, this.items, provider);
	}

	/**
	 * 检查物品是否为有效的模具分离器输入(即是否存在对应的分离配方)喵~
	 *
	 * @param level 世界对象,为 null 时返回 false 喵~
	 * @param itemStack 要检查的物品喵~
	 * @return 如果存在对应配方则返�� true,否则返回 false 喵~
	 */
	private static boolean isInput(@Nullable Level level, ItemStack itemStack) {
		if(level == null) {
			return false;
		}
		return level.getRecipeManager().getRecipeFor(MISCTWFRecipeTypes.MOLD_DETACHER.get(), new SingleRecipeInput(itemStack), level).isPresent();
	}

	/**
	 * 检查当前是否可以执行分离操作喵~
	 *
	 * <p>需要满足以下条件喵~:</p>
	 * <ul>
	 *     <li>输入槽有物品喵~</li>
	 *     <li>存在对应的配方喵~</li>
	 *     <li>输出槽有足够空间容纳所有产物喵~</li>
	 * </ul>
	 *
	 * @return 如果可以分离则返回 true,否则返回 false 喵~
	 */
	private boolean canDetach() {
		assert this.level != null;
		return this.level.getRecipeManager()
				.getRecipeFor(MISCTWFRecipeTypes.MOLD_DETACHER.get(), new SingleRecipeInput(this.items.getFirst()), this.level)
				.filter(
						moldDetacherRecipe -> moldDetacherRecipe.value().results().stream()
								.mapToInt(
										itemStack -> this.items.stream().noneMatch(
												slot -> ItemStack.isSameItemSameComponents(itemStack, slot) && itemStack.getCount() + slot.getCount() <= slot.getMaxStackSize()
										) ? 1 : 0
								).sum() - this.items.stream().filter(ItemStack::isEmpty).count() <= 0
				).isPresent();
	}

	/**
	 * 执行分离操作,将输入的模具分离为子弹和空模具喵~
	 *
	 * <p>分离后的产物会优先放入已有的相同物品堆,然后放入空槽位喵~
	 * 如果所有输出槽都满了,多余的产物会掉落到世界中喵~</p>
	 */
	private void detach() {
		assert this.level != null;
		Optional<RecipeHolder<MoldDetacherRecipe>> recipe = this.level.getRecipeManager()
				.getRecipeFor(MISCTWFRecipeTypes.MOLD_DETACHER.get(), new SingleRecipeInput(this.items.getFirst()), this.level);
		if(recipe.isEmpty()) {
			return;
		}
		this.items.getFirst().shrink(
				Arrays.stream(recipe.get().value().input().getItems())
						.filter(itemStack -> ItemStack.isSameItemSameComponents(itemStack, this.items.getFirst()))
						.findFirst().map(ItemStack::getCount).orElse(1)
		);
		NonNullList<ItemStack> results = recipe.get().value().results();
		results.forEach(itemStack -> {
			ItemStack remaining = itemStack.copy();
			for(int i = this.items.size() - 1; i >= 0; --i) {
				ItemStack slot = this.items.get(i);
				if(ItemStack.isSameItemSameComponents(slot, remaining)) {
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
		BlockState blockState = this.getBlockState();
		boolean triggered = blockState.getValue(MoldDetacherBlock.TRIGGERED);
		// 检查是否满足速度要求(即是否接收到足够的旋转应力)喵~
		if(this.isSpeedRequirementFulfilled()) {
			if(this.canDetach()) {
				// 根据旋转速度递减剩余时间喵~
				this.remainingTime -= Math.abs(this.getSpeed());
				if(this.remainingTime <= 0) {
					// 时间耗尽,执行分离操作并重置时间喵~
					this.remainingTime = MAX_REMAINING_TIME;
					this.detach();
				}
			} else {
				// 无法分离,重置时间喵~
				this.remainingTime = MAX_REMAINING_TIME;
			}
			if(!triggered) {
				// 更新方块状态为触发状态喵~
				this.level.setBlock(this.getBlockPos(), blockState.setValue(MoldDetacherBlock.TRIGGERED, true), Block.UPDATE_ALL);
			}
		} else if(triggered) {
			// 速度不足,更新方块状态���未触发状态喵~
			this.level.setBlock(this.getBlockPos(), blockState.setValue(MoldDetacherBlock.TRIGGERED, false), Block.UPDATE_ALL);
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
}
