package com.hexagram2021.misc_twf.common.block.entity;

import com.hexagram2021.misc_twf.common.menu.MoldWorkbenchMenu;
import com.hexagram2021.misc_twf.common.recipe.MoldWorkbenchRecipe;
import com.hexagram2021.misc_twf.common.register.MISCTWFBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

import javax.annotation.Nullable;

public class MoldWorkbenchBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer, StackedContentsCompatible {
	public static final int SLOT_INPUT = 0;
	public static final int SLOT_RESULT = 1;
	private static final int[] SLOTS_FOR_UP = new int[]{0};
	private static final int[] SLOTS_FOR_DOWN = new int[]{1};
	private static final int[] SLOTS_FOR_SIDES = new int[]{0, 1};
	public static final int NUM_SLOTS = 2;
	public static final int DATA_SLOTS = 2;

	protected NonNullList<ItemStack> items;
	int workProgress;
	int workTotalTime;
	@Nullable
	private ResourceLocation recipeUsed = null;

	private final ContainerData dataAccess = new ContainerData() {
		@Override
		public int get(int index) {
			switch (index) {
				case 0 -> {
					return MoldWorkbenchBlockEntity.this.workProgress;
				}
				case 1 -> {
					return MoldWorkbenchBlockEntity.this.workTotalTime;
				}
				default -> {
					return 0;
				}
			}
		}

		@Override
		public void set(int index, int value) {
			switch (index) {
				case 0 -> MoldWorkbenchBlockEntity.this.workProgress = value;
				case 1 -> MoldWorkbenchBlockEntity.this.workTotalTime = value;
			}

		}

		@Override
		public int getCount() {
			return MoldWorkbenchBlockEntity.DATA_SLOTS;
		}
	};

	public MoldWorkbenchBlockEntity(BlockPos blockPos, BlockState blockState) {
		super(MISCTWFBlockEntities.MOLD_WORKBENCH.get(), blockPos, blockState);
		this.items = NonNullList.withSize(NUM_SLOTS, ItemStack.EMPTY);
	}

	@Override
	protected Component getDefaultName() {
		return new TranslatableComponent("container.misc_twf.mold_workbench");
	}

	@Override
	protected MoldWorkbenchMenu createMenu(int id, Inventory inventory) {
		return new MoldWorkbenchMenu(id, inventory, this, this.dataAccess);
	}

	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
		ContainerHelper.loadAllItems(nbt, this.items);
		this.workProgress = nbt.getInt("WorkProgress");
		this.workTotalTime = nbt.getInt("WorkTotalTime");
		if(nbt.contains("RecipeUsed", Tag.TAG_STRING)) {
			this.recipeUsed = new ResourceLocation(nbt.getString("RecipeUsed"));
		}
	}

	@Override
	protected void saveAdditional(CompoundTag nbt) {
		super.saveAdditional(nbt);
		ContainerHelper.saveAllItems(nbt, this.items);
		nbt.putInt("WorkProgress", this.workProgress);
		nbt.putInt("WorkTotalTime", this.workTotalTime);
		if(this.recipeUsed != null) {
			nbt.putString("RecipeUsed", this.recipeUsed.toString());
		}
	}

	public static void serverTick(Level level, BlockPos blockPos, BlockState blockState, MoldWorkbenchBlockEntity blockEntity) {
		if(blockEntity.workTotalTime > 0) {
			if(blockEntity.recipeUsed != null) {
				ItemStack result = blockEntity.getItem(SLOT_RESULT);
				Recipe<?> recipe = level.getRecipeManager().byKey(blockEntity.recipeUsed).orElse(null);
				boolean resultEmpty = result.isEmpty();
				if(recipe != null && (resultEmpty || ItemStack.isSameItemSameTags(result, recipe.getResultItem()))) {
					blockEntity.workProgress += 1;
					if (blockEntity.workProgress < blockEntity.workTotalTime) {
						return;
					}
					if (recipe instanceof MoldWorkbenchRecipe moldWorkbenchRecipe && moldWorkbenchRecipe.matches(blockEntity, level)) {
						blockEntity.getItem(SLOT_INPUT).shrink(1);
						if(resultEmpty) {
							blockEntity.setItem(SLOT_RESULT, moldWorkbenchRecipe.assemble(blockEntity));
						} else {
							blockEntity.getItem(SLOT_RESULT).grow(1);
						}
					}
				}
				blockEntity.recipeUsed = null;
			}
			blockEntity.workProgress = blockEntity.workTotalTime = 0;
		}
	}

	@Override
	public int[] getSlotsForFace(Direction direction) {
		if (direction == Direction.DOWN) {
			return SLOTS_FOR_DOWN;
		}
		if(direction == Direction.UP) {
			return SLOTS_FOR_UP;
		}
		return SLOTS_FOR_SIDES;
	}

	@Override
	public boolean canPlaceItemThroughFace(int index, ItemStack itemStack, @Nullable Direction direction) {
		return this.canPlaceItem(index, itemStack);
	}

	@Override
	public boolean canTakeItemThroughFace(int index, ItemStack itemStack, Direction direction) {
		return true;
	}

	@Override
	public int getContainerSize() {
		return this.items.size();
	}

	@Override
	public boolean isEmpty() {
		return this.items.stream().allMatch(ItemStack::isEmpty);
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
		if(index == SLOT_INPUT) {
			this.setChanged();
		}
	}

	@Override
	public boolean stillValid(Player player) {
		assert this.level != null;
		if (this.level.getBlockEntity(this.worldPosition) != this) {
			return false;
		}
		return player.distanceToSqr((double)this.worldPosition.getX() + 0.5, (double)this.worldPosition.getY() + 0.5, (double)this.worldPosition.getZ() + 0.5) <= 64.0;
	}

	@Override
	public boolean canPlaceItem(int index, ItemStack itemStack) {
		return index == SLOT_INPUT;
	}

	@Override
	public void clearContent() {
		this.items.clear();
	}

	public void setRecipeUsed(@Nullable Recipe<?> recipe) {
		if (recipe == null) {
			this.recipeUsed = null;
		} else {
			this.recipeUsed = recipe.getId();
		}
	}

	public void startWorking(int totalTime) {
		this.workProgress = 0;
		this.workTotalTime = totalTime;
	}

	// Forge start
	LazyOptional<? extends IItemHandler>[] handlers = SidedInvWrapper.create(this, Direction.UP, Direction.DOWN, Direction.NORTH);

	@Override
	public void fillStackedContents(StackedContents contents) {
		this.items.forEach(contents::accountStack);
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
		if (!this.remove && facing != null && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			if (facing == Direction.UP) {
				return this.handlers[0].cast();
			} else {
				return facing == Direction.DOWN ? this.handlers[1].cast() : this.handlers[2].cast();
			}
		} else {
			return super.getCapability(capability, facing);
		}
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
