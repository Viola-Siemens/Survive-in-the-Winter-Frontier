package com.hexagram2021.misc_twf.common.menu;

import com.google.common.collect.Lists;
import com.hexagram2021.misc_twf.common.block.entity.MoldWorkbenchBlockEntity;
import com.hexagram2021.misc_twf.common.recipe.MoldWorkbenchRecipe;
import com.hexagram2021.misc_twf.common.register.MISCTWFMenuTypes;
import com.hexagram2021.misc_twf.common.register.MISCTWFRecipeTypes;
import com.simibubi.create.AllBlocks;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;

public class MoldWorkbenchMenu extends AbstractContainerMenu {
	public static final int INV_SLOT_START = 3;
	public static final int INV_SLOT_END = 30;
	public static final int USE_ROW_SLOT_START = 30;
	public static final int USE_ROW_SLOT_END = 39;
	public static final int DATA_RECIPE_INDEX = 2;
	private final Container container;
	private final ContainerData containerData;
	protected final Level level;
	private final Slot inputSlot;
	public final Slot mechanicalArmSlot;
	private ItemStack input = ItemStack.EMPTY;
	private final List<MoldWorkbenchRecipe> recipes = Lists.newArrayList();
	Runnable slotUpdateListener;

	public MoldWorkbenchMenu(int id, Inventory inventory) {
		this(id, inventory, new SimpleContainer(MoldWorkbenchBlockEntity.NUM_SLOTS), new SimpleContainerData(MoldWorkbenchBlockEntity.DATA_SLOTS));
	}
	public MoldWorkbenchMenu(int id, Inventory inventory, Container container, ContainerData containerData) {
		super(MISCTWFMenuTypes.MOLD_WORKBENCH_MENU.get(), id);
		checkContainerSize(container, MoldWorkbenchBlockEntity.NUM_SLOTS);
		checkContainerDataCount(containerData, MoldWorkbenchBlockEntity.DATA_SLOTS);
		this.container = container;
		this.containerData = containerData;
		this.level = inventory.player.level;
		this.slotUpdateListener = () -> {
		};

		this.inputSlot = this.addSlot(new Slot(container, MoldWorkbenchBlockEntity.SLOT_INPUT, 20, 33) {
			@Override
			public void setChanged() {
				super.setChanged();
				MoldWorkbenchMenu.this.slotsChanged(MoldWorkbenchMenu.this.container);
				MoldWorkbenchMenu.this.slotUpdateListener.run();
			}
		});
		this.addSlot(new Slot(container, MoldWorkbenchBlockEntity.SLOT_RESULT, 143, 33) {
			@Override
			public boolean mayPlace(ItemStack itemStack) {
				return false;
			}
		});
		this.mechanicalArmSlot = this.addSlot(new Slot(container, MoldWorkbenchBlockEntity.SLOT_MECHANICAL_ARM, 147, 8) {
			@Override
			public boolean mayPlace(ItemStack itemStack) {
				return AllBlocks.MECHANICAL_ARM.isIn(itemStack);
			}

			@Override
			public void setChanged() {
				super.setChanged();
				if(this.container instanceof MoldWorkbenchBlockEntity moldWorkbenchBlockEntity) {
					moldWorkbenchBlockEntity.mechanicalArmSlotChange(AllBlocks.MECHANICAL_ARM.isIn(this.getItem()));
				}
			}
		});

		for(int i = 0; i < 3; ++i) {
			for(int j = 0; j < 9; ++j) {
				this.addSlot(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}
		for(int i = 0; i < 9; ++i) {
			this.addSlot(new Slot(inventory, i, 8 + i * 18, 142));
		}
		this.addDataSlots(containerData);

		ItemStack input = container.getItem(MoldWorkbenchBlockEntity.SLOT_INPUT);
		if(!input.isEmpty()) {
			this.setupAllWorkbenchRecipes(container, input);
		}
	}

	public int getSelectedRecipeIndex() {
		return this.containerData.get(DATA_RECIPE_INDEX);
	}

	public List<MoldWorkbenchRecipe> getRecipes() {
		return this.recipes;
	}

	public int getNumRecipes() {
		return this.recipes.size();
	}

	public boolean hasInputItem() {
		return this.inputSlot.hasItem() && !this.recipes.isEmpty();
	}

	@Override
	public boolean stillValid(Player player) {
		return this.container.stillValid(player);
	}

	@Override
	public boolean clickMenuButton(Player player, int index) {
		if (this.isValidRecipeIndex(index)) {
			this.containerData.set(DATA_RECIPE_INDEX, index);
			this.setupResultSlot();
		}

		return true;
	}

	protected boolean isValidRecipeIndex(int index) {
		return index >= 0 && index < this.recipes.size();
	}

	@Override
	public void slotsChanged(Container container) {
		super.slotsChanged(container);
		ItemStack inputItem = this.inputSlot.getItem();
		if (!inputItem.is(this.input.getItem())) {
			this.input = inputItem.copy();
			this.setupRecipeList(container, inputItem);
		}
	}

	private void setupRecipeList(Container container, ItemStack itemStack) {
		this.containerData.set(DATA_RECIPE_INDEX, -1);
		this.setupAllWorkbenchRecipes(container, itemStack);
		this.broadcastChanges();
	}

	private void setupAllWorkbenchRecipes(Container container, ItemStack itemStack) {
		this.recipes.clear();
		if (!itemStack.isEmpty()) {
			this.recipes.addAll(this.level.getRecipeManager().getRecipesFor(MISCTWFRecipeTypes.MOLD_WORKBENCH.get(), container, this.level));
		}
	}

	void setupResultSlot() {
		if(this.container instanceof MoldWorkbenchBlockEntity moldWorkbenchBlockEntity) {
			MoldWorkbenchRecipe recipe = this.recipes.get(this.containerData.get(DATA_RECIPE_INDEX));
			moldWorkbenchBlockEntity.setRecipeUsed(recipe);
			moldWorkbenchBlockEntity.startWorking(recipe.workingTime());
		}
		this.broadcastChanges();
	}

	@SuppressWarnings("ConstantValue")
	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		ItemStack ret = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);
		if(slot != null && slot.hasItem()) {
			ItemStack slotItem = slot.getItem();
			ret = slotItem.copy();
			if (index == MoldWorkbenchBlockEntity.SLOT_RESULT) {
				if (!this.moveItemStackTo(slotItem, INV_SLOT_START, USE_ROW_SLOT_END, true)) {
					return ItemStack.EMPTY;
				}

				slot.onQuickCraft(slotItem, ret);
			} else if(index == MoldWorkbenchBlockEntity.SLOT_INPUT || index == MoldWorkbenchBlockEntity.SLOT_MECHANICAL_ARM) {
				if (!this.moveItemStackTo(slotItem, INV_SLOT_START, USE_ROW_SLOT_END, false)) {
					return ItemStack.EMPTY;
				}
			} else if(this.canWorkOn(slotItem)) {
				if (!this.moveItemStackTo(slotItem, MoldWorkbenchBlockEntity.SLOT_INPUT, MoldWorkbenchBlockEntity.SLOT_INPUT + 1, false)) {
					return ItemStack.EMPTY;
				}
			} else if(this.mechanicalArmSlot.mayPlace(slotItem)) {
				if (!this.moveItemStackTo(slotItem, MoldWorkbenchBlockEntity.SLOT_MECHANICAL_ARM, MoldWorkbenchBlockEntity.SLOT_MECHANICAL_ARM + 1, false)) {
					return ItemStack.EMPTY;
				}
			} else if(index >= INV_SLOT_START && index < INV_SLOT_END) {
				if (!this.moveItemStackTo(slotItem, USE_ROW_SLOT_START, USE_ROW_SLOT_END, false)) {
					return ItemStack.EMPTY;
				}
			} else if(index >= USE_ROW_SLOT_START && index < USE_ROW_SLOT_END) {
				if (!this.moveItemStackTo(slotItem, INV_SLOT_START, INV_SLOT_END, false)) {
					return ItemStack.EMPTY;
				}
			}
			if (slotItem.isEmpty()) {
				slot.set(ItemStack.EMPTY);
			} else {
				slot.setChanged();
			}

			if (slotItem.getCount() == ret.getCount()) {
				return ItemStack.EMPTY;
			}

			slot.onTake(player, slotItem);
		}

		return ret;
	}

	protected boolean canWorkOn(ItemStack itemStack) {
		return this.level.getRecipeManager().getRecipeFor(MISCTWFRecipeTypes.MOLD_WORKBENCH.get(), new SimpleContainer(itemStack), this.level).isPresent();
	}

	public void registerUpdateListener(Runnable runnable) {
		this.slotUpdateListener = runnable;
	}

	public static final int PROGRESS_BAR_LENGTH = 18;
	public int getWorkingProgress() {
		int totalTime = this.containerData.get(1);
		return totalTime == 0 ? 0 : this.containerData.get(0) * PROGRESS_BAR_LENGTH / totalTime;
	}
}
