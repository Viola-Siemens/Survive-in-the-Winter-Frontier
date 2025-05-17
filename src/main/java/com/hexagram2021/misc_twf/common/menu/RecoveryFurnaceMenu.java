package com.hexagram2021.misc_twf.common.menu;

import com.hexagram2021.misc_twf.common.block.entity.RecoveryFurnaceBlockEntity;
import com.hexagram2021.misc_twf.common.menu.slot.RecoveryFurnaceResultSlot;
import com.hexagram2021.misc_twf.common.register.MISCTWFMenuTypes;
import com.hexagram2021.misc_twf.common.register.MISCTWFRecipeBookTypes;
import com.hexagram2021.misc_twf.common.register.MISCTWFRecipeTypes;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeHooks;

public class RecoveryFurnaceMenu extends RecipeBookMenu<Container> {
	public static final int INV_SLOT_START = 6;
	public static final int INV_SLOT_END = 33;
	public static final int USE_ROW_SLOT_START = 33;
	public static final int USE_ROW_SLOT_END = 42;
	private final Container container;
	private final ContainerData containerData;
	protected final Level level;
	Runnable slotUpdateListener;

	public RecoveryFurnaceMenu(int id, Inventory inventory) {
		this(id, inventory, new SimpleContainer(RecoveryFurnaceBlockEntity.NUM_SLOTS), new SimpleContainerData(RecoveryFurnaceBlockEntity.DATA_SLOTS));
	}
	public RecoveryFurnaceMenu(int id, Inventory inventory, Container container, ContainerData containerData) {
		super(MISCTWFMenuTypes.RECOVERY_FURNACE.get(), id);
		checkContainerSize(container, RecoveryFurnaceBlockEntity.NUM_SLOTS);
		checkContainerDataCount(containerData, RecoveryFurnaceBlockEntity.DATA_SLOTS);
		this.container = container;
		this.containerData = containerData;
		container.startOpen(inventory.player);
		this.level = inventory.player.level;
		this.slotUpdateListener = () -> {
		};

		this.addSlot(new Slot(container, RecoveryFurnaceBlockEntity.SLOT_INPUT, 33, 17));
		this.addSlot(new Slot(container, RecoveryFurnaceBlockEntity.SLOT_FUEL, 33, 52) {
			@Override
			public boolean mayPlace(ItemStack itemStack) {
				return RecoveryFurnaceMenu.this.isFuel(itemStack);
			}
		});
		for(int i = RecoveryFurnaceBlockEntity.SLOT_RESULT_START; i < RecoveryFurnaceBlockEntity.SLOT_RESULT_END; ++i) {
			int index = i - RecoveryFurnaceBlockEntity.SLOT_RESULT_START;
			this.addSlot(new RecoveryFurnaceResultSlot(inventory.player, container, i, 115 + 18 * (index % 2), 26 + 18 * (index / 2)));
		}

		for(int i = 0; i < 3; ++i) {
			for(int j = 0; j < 9; ++j) {
				this.addSlot(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}
		for(int i = 0; i < 9; ++i) {
			this.addSlot(new Slot(inventory, i, 8 + i * 18, 142));
		}
		this.addDataSlots(containerData);
	}

	protected boolean canSmelt(ItemStack itemStack) {
		return this.level.getRecipeManager().getRecipeFor(MISCTWFRecipeTypes.RECOVERY_FURNACE.get(), new SimpleContainer(itemStack), this.level).isPresent();
	}

	protected boolean isFuel(ItemStack itemStack) {
		return ForgeHooks.getBurnTime(itemStack, MISCTWFRecipeTypes.RECOVERY_FURNACE.get()) > 0;
	}

	public Container getContainer() {
		return this.container;
	}

	@Override
	public void fillCraftSlotsStackedContents(StackedContents contents) {
		if (this.container instanceof StackedContentsCompatible) {
			((StackedContentsCompatible)this.container).fillStackedContents(contents);
		}
	}

	@Override
	public void clearCraftingContent() {
		this.getSlot(RecoveryFurnaceBlockEntity.SLOT_INPUT).set(ItemStack.EMPTY);
		for(int i = RecoveryFurnaceBlockEntity.SLOT_RESULT_START; i < RecoveryFurnaceBlockEntity.SLOT_RESULT_END; ++i) {
			this.getSlot(i).set(ItemStack.EMPTY);
		}
	}

	@Override
	public boolean recipeMatches(Recipe<? super Container> recipe) {
		return recipe.matches(this.container, this.level);
	}

	@Override
	public int getResultSlotIndex() {
		return 2;
	}

	@Override
	public int getGridWidth() {
		return 1;
	}

	@Override
	public int getGridHeight() {
		return 1;
	}

	@Override
	public int getSize() {
		return 3;
	}

	@Override
	public boolean stillValid(Player player) {
		return this.container.stillValid(player);
	}

	@SuppressWarnings("ConstantValue")
	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		ItemStack ret = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);
		if (slot != null && slot.hasItem()) {
			ItemStack slotItem = slot.getItem();
			ret = slotItem.copy();
			if (index >= RecoveryFurnaceBlockEntity.SLOT_RESULT_START && index < RecoveryFurnaceBlockEntity.SLOT_RESULT_END) {
				if (!this.moveItemStackTo(slotItem, INV_SLOT_START, USE_ROW_SLOT_END, true)) {
					return ItemStack.EMPTY;
				}
				slot.onQuickCraft(slotItem, ret);
			} else if (index == 1 || index == 0) {
				if (!this.moveItemStackTo(slotItem, INV_SLOT_START, USE_ROW_SLOT_END, false)) {
					return ItemStack.EMPTY;
				}
			} else if (this.canSmelt(slotItem)) {
				if (!this.moveItemStackTo(slotItem, RecoveryFurnaceBlockEntity.SLOT_INPUT, RecoveryFurnaceBlockEntity.SLOT_INPUT + 1, false)) {
					return ItemStack.EMPTY;
				}
			} else if (this.isFuel(slotItem)) {
				if (!this.moveItemStackTo(slotItem, RecoveryFurnaceBlockEntity.SLOT_FUEL, RecoveryFurnaceBlockEntity.SLOT_FUEL + 1, false)) {
					return ItemStack.EMPTY;
				}
			} else if (index >= INV_SLOT_START && index < INV_SLOT_END) {
				if (!this.moveItemStackTo(slotItem, USE_ROW_SLOT_START, USE_ROW_SLOT_END, false)) {
					return ItemStack.EMPTY;
				}
			} else if (index >= USE_ROW_SLOT_START && index < USE_ROW_SLOT_END) {
				if(!this.moveItemStackTo(slotItem, INV_SLOT_START, INV_SLOT_END, false)) {
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

	public int getBurnProgress() {
		int progress = this.containerData.get(RecoveryFurnaceBlockEntity.DATA_RECOVERING_PROGRESS);
		int total = this.containerData.get(RecoveryFurnaceBlockEntity.DATA_RECOVERING_TOTAL_TIME);
		return total != 0 && progress != 0 ? progress * 24 / total : 0;
	}

	public int getLitProgress() {
		int duration = this.containerData.get(RecoveryFurnaceBlockEntity.DATA_LIT_DURATION);
		if (duration == 0) {
			duration = 200;
		}

		return this.containerData.get(RecoveryFurnaceBlockEntity.DATA_LIT_TIME) * 16 / duration;
	}

	public boolean isLit() {
		return this.containerData.get(RecoveryFurnaceBlockEntity.DATA_LIT_TIME) > 0;
	}

	@Override
	public void removed(Player player) {
		super.removed(player);
		this.container.stopOpen(player);
	}

	@Override
	public RecipeBookType getRecipeBookType() {
		return MISCTWFRecipeBookTypes.RECOVER_FURNACE;
	}

	@Override
	public boolean shouldMoveToInventory(int index) {
		return index != RecoveryFurnaceBlockEntity.SLOT_FUEL;
	}
}
