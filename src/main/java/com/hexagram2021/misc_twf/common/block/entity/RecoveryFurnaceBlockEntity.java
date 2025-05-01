package com.hexagram2021.misc_twf.common.block.entity;

import com.google.common.collect.Lists;
import com.hexagram2021.misc_twf.common.menu.RecoveryFurnaceMenu;
import com.hexagram2021.misc_twf.common.recipe.RecoveryFurnaceRecipe;
import com.hexagram2021.misc_twf.common.register.MISCTWFBlockEntities;
import com.hexagram2021.misc_twf.common.register.MISCTWFRecipeTypes;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.RecipeHolder;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

import javax.annotation.Nullable;
import java.util.List;

public class RecoveryFurnaceBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer, RecipeHolder, StackedContentsCompatible {
	public static final int SLOT_INPUT = 0;
	public static final int SLOT_FUEL = 1;
	public static final int SLOT_RESULT_START = 2;
	public static final int SLOT_RESULT_END = 6;
	public static final int NUM_SLOTS = 6;
	public static final int DATA_LIT_TIME = 0;
	public static final int DATA_LIT_DURATION = 1;
	public static final int DATA_RECOVERING_PROGRESS = 2;
	public static final int DATA_RECOVERING_TOTAL_TIME = 3;
	public static final int DATA_SLOTS = 4;

	private static final int[] SLOTS_FOR_UP = new int[]{0, 1};
	private static final int[] SLOTS_FOR_DOWN = new int[]{5, 4, 3, 2};
	private static final int[] SLOTS_FOR_SIDES = new int[]{1, 2, 3, 4, 5};

	protected NonNullList<ItemStack> items;
	int litTime;
	int litDuration;
	int recoveringProgress;
	int recoveringTotalTime;
	private final Object2IntOpenHashMap<ResourceLocation> recipesUsed = new Object2IntOpenHashMap<>();

	protected final ContainerData dataAccess = new ContainerData() {
		@Override
		public int get(int index) {
			return switch (index) {
				case DATA_LIT_TIME -> RecoveryFurnaceBlockEntity.this.litTime;
				case DATA_LIT_DURATION -> RecoveryFurnaceBlockEntity.this.litDuration;
				case DATA_RECOVERING_PROGRESS -> RecoveryFurnaceBlockEntity.this.recoveringProgress;
				case DATA_RECOVERING_TOTAL_TIME -> RecoveryFurnaceBlockEntity.this.recoveringTotalTime;
				default -> 0;
			};
		}

		@Override
		public void set(int index, int value) {
			switch (index) {
				case DATA_LIT_TIME -> RecoveryFurnaceBlockEntity.this.litTime = value;
				case DATA_LIT_DURATION -> RecoveryFurnaceBlockEntity.this.litDuration = value;
				case DATA_RECOVERING_PROGRESS -> RecoveryFurnaceBlockEntity.this.recoveringProgress = value;
				case DATA_RECOVERING_TOTAL_TIME -> RecoveryFurnaceBlockEntity.this.recoveringTotalTime = value;
			}

		}

		@Override
		public int getCount() {
			return DATA_SLOTS;
		}
	};

	public RecoveryFurnaceBlockEntity(BlockPos blockPos, BlockState blockState) {
		super(MISCTWFBlockEntities.RECOVERY_FURNACE.get(), blockPos, blockState);
		this.items = NonNullList.withSize(NUM_SLOTS, ItemStack.EMPTY);
	}

	@Override
	protected Component getDefaultName() {
		return new TranslatableComponent("container.misc_twf.recovery_furnace");
	}

	@Override
	protected RecoveryFurnaceMenu createMenu(int id, Inventory inventory) {
		return new RecoveryFurnaceMenu(id, inventory, this, this.dataAccess);
	}

	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
		ContainerHelper.loadAllItems(nbt, this.items);
		this.litTime = nbt.getInt("LitTime");
		this.litDuration = nbt.getInt("LitDuration");
		this.recoveringProgress = nbt.getInt("RecoveringProgress");
		this.recoveringTotalTime = nbt.getInt("RecoveringTotalTime");
	}

	@Override
	protected void saveAdditional(CompoundTag nbt) {
		super.saveAdditional(nbt);
		ContainerHelper.saveAllItems(nbt, this.items);
		nbt.putInt("LitTime", this.litTime);
		nbt.putInt("LitDuration", this.litDuration);
		nbt.putInt("RecoveringProgress", this.recoveringProgress);
		nbt.putInt("RecoveringTotalTime", this.recoveringTotalTime);
	}

	private boolean isLit() {
		return this.litTime > 0;
	}

	public static void serverTick(Level level, BlockPos blockPos, BlockState blockState, RecoveryFurnaceBlockEntity blockEntity) {
		boolean lit = blockEntity.isLit();
		boolean changed = false;
		if (blockEntity.isLit()) {
			--blockEntity.litTime;
		}

		ItemStack itemstack = blockEntity.items.get(SLOT_FUEL);
		if (!blockEntity.isLit() && (itemstack.isEmpty() || blockEntity.items.get(SLOT_INPUT).isEmpty())) {
			if (!blockEntity.isLit() && blockEntity.recoveringProgress > 0) {
				blockEntity.recoveringProgress = Mth.clamp(blockEntity.recoveringProgress - 2, 0, blockEntity.recoveringTotalTime);
			}
		} else {
			RecoveryFurnaceRecipe recipe = level.getRecipeManager().getRecipeFor(MISCTWFRecipeTypes.RECOVERY_FURNACE.get(), blockEntity, level).orElse(null);
			int i = blockEntity.getMaxStackSize();
			if (!blockEntity.isLit() && blockEntity.canBurn(recipe, blockEntity.items, i)) {
				blockEntity.litTime = blockEntity.getBurnDuration(itemstack);
				blockEntity.litDuration = blockEntity.litTime;
				if (blockEntity.isLit()) {
					changed = true;
					if (itemstack.hasContainerItem()) {
						blockEntity.items.set(SLOT_FUEL, itemstack.getContainerItem());
					} else if (!itemstack.isEmpty()) {
						itemstack.shrink(1);
						if (itemstack.isEmpty()) {
							blockEntity.items.set(SLOT_FUEL, itemstack.getContainerItem());
						}
					}
				}
			}

			if (blockEntity.isLit() && blockEntity.canBurn(recipe, blockEntity.items, i)) {
				++blockEntity.recoveringProgress;
				if (blockEntity.recoveringProgress == blockEntity.recoveringTotalTime) {
					blockEntity.recoveringProgress = 0;
					blockEntity.recoveringTotalTime = getTotalCookTime(level, blockEntity);
					if (blockEntity.burn(level, recipe, blockEntity.items, i)) {
						blockEntity.setRecipeUsed(recipe);
					}

					changed = true;
				}
			} else {
				blockEntity.recoveringProgress = 0;
			}
		}

		if (lit != blockEntity.isLit()) {
			changed = true;
		}

		if (changed) {
			setChanged(level, blockPos, blockState);
		}
	}

	public boolean canBurn(@Nullable RecoveryFurnaceRecipe recipe, NonNullList<ItemStack> items, int maxCount) {
		if (!items.get(SLOT_INPUT).isEmpty() && recipe != null) {
			List<ItemStack> results = recipe.assembleAll(this);
			if(results.isEmpty()) {
				return false;
			}
			for(ItemStack result: results) {
				if (result.isEmpty()) {
					return false;
				}
				int count = result.getCount();
				for(int i = SLOT_RESULT_START; i < SLOT_RESULT_END; ++i) {
					ItemStack slotItem = items.get(i);
					if (slotItem.isEmpty()) {
						count = 0;
						break;
					}
					if (slotItem.sameItem(result)) {
						int minMaxCount = Math.min(maxCount, slotItem.getMaxStackSize());
						if (slotItem.getCount() + count <= minMaxCount) {
							count = 0;
							break;
						}
						count -= minMaxCount - slotItem.getCount();
					}
				}
				if(count > 0) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	private boolean burn(Level level, @Nullable RecoveryFurnaceRecipe recipe, NonNullList<ItemStack> items, int maxCount) {
		if (recipe != null && this.canBurn(recipe, items, maxCount)) {
			ItemStack input = items.get(SLOT_INPUT);
			List<ItemStack> results = recipe.assembleAll(this);
			for(ItemStack result: results) {
				int count = result.getCount();
				for (int i = SLOT_RESULT_START; i < SLOT_RESULT_END; ++i) {
					ItemStack slotItem = items.get(i);
					if (slotItem.isEmpty()) {
						items.set(i, result.copy());
						count = 0;
						break;
					}
					if (slotItem.sameItem(result)) {
						int minMaxCount = Math.min(maxCount, slotItem.getMaxStackSize());
						if (slotItem.getCount() + count <= minMaxCount) {
							slotItem.grow(count);
							count = 0;
							break;
						}
						int grow = minMaxCount - slotItem.getCount();
						slotItem.grow(grow);
						count -= grow;
					}
				}
				if (count > 0) {
					Block.popResource(level, this.worldPosition, new ItemStack(result.getItem(), count));
				}
			}
			input.shrink(1);
			return true;
		}
		return false;
	}

	protected int getBurnDuration(ItemStack fuel) {
		if (fuel.isEmpty()) {
			return 0;
		}
		return ForgeHooks.getBurnTime(fuel, MISCTWFRecipeTypes.RECOVERY_FURNACE.get());
	}

	private static int getTotalCookTime(Level level, Container container) {
		return level.getRecipeManager().getRecipeFor(MISCTWFRecipeTypes.RECOVERY_FURNACE.get(), container, level).map(RecoveryFurnaceRecipe::recoveringTime).orElse(200);
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

	@Override
	public void setRecipeUsed(@Nullable Recipe<?> recipe) {
		if (recipe != null) {
			ResourceLocation resourcelocation = recipe.getId();
			this.recipesUsed.addTo(resourcelocation, 1);
		}

	}

	@Override @Nullable
	public Recipe<?> getRecipeUsed() {
		return null;
	}

	@Override
	public void awardUsedRecipes(Player player) {
	}

	public void awardUsedRecipesAndPopExperience(ServerPlayer player) {
		List<Recipe<?>> list = this.getRecipesToAwardAndPopExperience(player.getLevel(), player.position());
		player.awardRecipes(list);
		this.recipesUsed.clear();
	}

	public List<Recipe<?>> getRecipesToAwardAndPopExperience(ServerLevel level, Vec3 position) {
		List<Recipe<?>> list = Lists.newArrayList();

		for (Object2IntMap.Entry<ResourceLocation> entry : this.recipesUsed.object2IntEntrySet()) {
			level.getRecipeManager().byKey(entry.getKey()).ifPresent(recipe -> {
				list.add(recipe);
				createExperience(level, position, entry.getIntValue(), ((RecoveryFurnaceRecipe) recipe).experience());
			});
		}

		return list;
	}

	private static void createExperience(ServerLevel level, Vec3 position, int count, float experience) {
		int i = Mth.floor((float)count * experience);
		float f = Mth.frac((float)count * experience);
		if (f != 0.0F && Math.random() < (double)f) {
			++i;
		}

		ExperienceOrb.award(level, position, i);
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
