package com.hexagram2021.misc_twf.common.block.entity;

import com.hexagram2021.misc_twf.common.block.MoldWorkbenchBlock;
import com.hexagram2021.misc_twf.common.block.properties.MoldWorkbenchPart;
import com.hexagram2021.misc_twf.common.menu.MoldWorkbenchMenu;
import com.hexagram2021.misc_twf.common.recipe.MoldWorkbenchRecipe;
import com.hexagram2021.misc_twf.common.register.MISCTWFBlockEntities;
import com.hexagram2021.misc_twf.common.register.MISCTWFBlocks;
import com.simibubi.create.content.contraptions.ITransformableBlockEntity;
import com.simibubi.create.content.contraptions.StructureTransform;
import com.simibubi.create.content.decoration.bracket.BracketedBlockEntityBehaviour;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.simpleRelays.AbstractSimpleShaftBlock;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

import javax.annotation.Nullable;
import java.util.List;

@SuppressWarnings("Convert2MethodRef")
public class MoldWorkbenchBlockEntity extends KineticBlockEntity implements Container, MenuProvider, Nameable, WorldlyContainer, StackedContentsCompatible, ITransformableBlockEntity {
	public static final int SLOT_INPUT = 0;
	public static final int SLOT_RESULT = 1;
	public static final int SLOT_MECHANICAL_ARM = 2;
	private static final int[] SLOTS_FOR_UP = new int[]{0, 2};
	private static final int[] SLOTS_FOR_DOWN = new int[]{1};
	private static final int[] SLOTS_FOR_SIDES = new int[]{0, 1};
	public static final int NUM_SLOTS = 3;
	public static final int DATA_SLOTS = 3;

	@Nullable
	private Component name;
	protected NonNullList<ItemStack> items;
	int workProgress;
	int workTotalTime;
	int recipeIndex = -1;
	@Nullable
	private ResourceLocation recipeUsed = null;

	private final ContainerData dataAccess = new ContainerData() {
		@Override
		public int get(int index) {
			return switch (index) {
				case 0 -> MoldWorkbenchBlockEntity.this.workProgress;
				case 1 -> MoldWorkbenchBlockEntity.this.workTotalTime;
				case 2 -> MoldWorkbenchBlockEntity.this.recipeIndex;
				default -> 0;
			};
		}

		@Override
		public void set(int index, int value) {
			switch (index) {
				case 0 -> MoldWorkbenchBlockEntity.this.workProgress = value;
				case 1 -> MoldWorkbenchBlockEntity.this.workTotalTime = value;
				case 2 -> {
					MoldWorkbenchBlockEntity.this.recipeIndex = value;
					if(value < 0) {
						MoldWorkbenchBlockEntity.this.recipeUsed = null;
					}
				}
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

	public void setCustomName(Component customName) {
		this.name = customName;
	}

	@Override
	public Component getName() {
		return this.name != null ? this.name : this.getDefaultName();
	}

	@Override
	public Component getDisplayName() {
		return this.getName();
	}

	@Override @Nullable
	public Component getCustomName() {
		return this.name;
	}

	protected Component getDefaultName() {
		return new TranslatableComponent("container.misc_twf.mold_workbench");
	}

	@Override
	public MoldWorkbenchMenu createMenu(int id, Inventory inventory, Player player) {
		return new MoldWorkbenchMenu(id, inventory, this, this.dataAccess);
	}

	@Override
	public void read(CompoundTag nbt, boolean clientPacket) {
		super.read(nbt, clientPacket);
		if (nbt.contains("CustomName", Tag.TAG_STRING)) {
			this.name = Component.Serializer.fromJson(nbt.getString("CustomName"));
		}
		this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
		ContainerHelper.loadAllItems(nbt, this.items);
		this.workProgress = nbt.getInt("WorkProgress");
		this.workTotalTime = nbt.getInt("WorkTotalTime");
		this.recipeIndex = nbt.getInt("RecipeIndex");
		if(nbt.contains("RecipeUsed", Tag.TAG_STRING)) {
			this.recipeUsed = new ResourceLocation(nbt.getString("RecipeUsed"));
		}
	}

	@Override
	public void write(CompoundTag nbt, boolean clientPacket) {
		super.write(nbt, clientPacket);
		if (this.name != null) {
			nbt.putString("CustomName", Component.Serializer.toJson(this.name));
		}
		ContainerHelper.saveAllItems(nbt, this.items);
		nbt.putInt("WorkProgress", this.workProgress);
		nbt.putInt("WorkTotalTime", this.workTotalTime);
		nbt.putInt("RecipeIndex", this.recipeIndex);
		if(this.recipeUsed != null) {
			nbt.putString("RecipeUsed", this.recipeUsed.toString());
		}
	}

	@Override
	public void tick() {
		super.tick();
		if(this.getBlockState().getValue(MoldWorkbenchBlock.PART) != MoldWorkbenchPart.BOTTOM) {
			return;
		}
		assert this.level != null;
		if(this.workTotalTime > 0) {
			ResourceLocation recipeUsed = this.recipeUsed;
			if(recipeUsed != null) {
				ItemStack input = this.getItem(SLOT_INPUT);
				ItemStack result = this.getItem(SLOT_RESULT);
				Recipe<?> recipe = this.level.getRecipeManager().byKey(recipeUsed).orElse(null);
				boolean resultEmpty = result.isEmpty();
				if(recipe != null && (resultEmpty || ItemStack.isSameItemSameTags(result, recipe.getResultItem()))) {
					this.workProgress += 1;
					if (this.workProgress < this.workTotalTime) {
						return;
					}
					if (recipe instanceof MoldWorkbenchRecipe moldWorkbenchRecipe && moldWorkbenchRecipe.matches(this, this.level)) {
						input.shrink(1);
						if(resultEmpty) {
							this.setItem(SLOT_RESULT, moldWorkbenchRecipe.assemble(this));
						} else {
							result.grow(1);
						}
					}
				}
				if(this.isSpeedRequirementFulfilled() && !input.isEmpty()) {
					this.workProgress = 0;
					this.setChanged();
					return;
				}
				this.recipeIndex = -1;
				this.recipeUsed = null;
			}
			this.workProgress = this.workTotalTime = 0;
			this.setChanged();
		}
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
		behaviours.add(new BracketedBlockEntityBehaviour(this, state -> {
			Block block = state.getBlock();
			return block instanceof AbstractSimpleShaftBlock || block instanceof MoldWorkbenchBlock;
		}));
		super.addBehaviours(behaviours);
	}

	@Override
	public void transform(StructureTransform transform) {
		BracketedBlockEntityBehaviour bracketBehaviour = this.getBehaviour(BracketedBlockEntityBehaviour.TYPE);
		if (bracketBehaviour != null) {
			bracketBehaviour.transformBracket(transform);
		}
	}

	@Override
	protected Block getStressConfigKey() {
		return MISCTWFBlocks.MOLD_WORKBENCH.get();
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
	public void clearContent() {
		this.items.clear();
	}

	@Override
	public void fillStackedContents(StackedContents contents) {
		this.items.forEach(contents::accountStack);
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

	public void mechanicalArmSlotChange(boolean hasMechanicalArm) {
		if(this.level instanceof ServerLevel) {
			this.level.setBlock(this.worldPosition, this.getBlockState().setValue(MoldWorkbenchBlock.ARMED, hasMechanicalArm), Block.UPDATE_ALL);
		}
	}

	// Forge start
	LazyOptional<?> itemHandler = LazyOptional.of(() -> this.createUnSidedHandler());
	LazyOptional<? extends IItemHandler>[] handlers = SidedInvWrapper.create(this, Direction.UP, Direction.DOWN, Direction.NORTH);

	protected IItemHandler createUnSidedHandler() {
		return new InvWrapper(this);
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
		if (!this.remove) {
			if(facing != null && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
				if (facing == Direction.UP) {
					return this.handlers[0].cast();
				}
				return facing == Direction.DOWN ? this.handlers[1].cast() : this.handlers[2].cast();
			}
			if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
				return this.itemHandler.cast();
			}
		}
		return super.getCapability(capability, facing);
	}

	@Override
	public void invalidateCaps() {
		super.invalidateCaps();
		for (LazyOptional<? extends IItemHandler> handler : this.handlers) {
			handler.invalidate();
		}
		this.itemHandler.invalidate();
	}

	@Override
	public void reviveCaps() {
		super.reviveCaps();
		this.handlers = SidedInvWrapper.create(this, Direction.UP, Direction.DOWN, Direction.NORTH);
		this.itemHandler = LazyOptional.of(() -> this.createUnSidedHandler());
	}
}
