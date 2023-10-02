package com.hexagram2021.misc_twf.common.menu;

import com.hexagram2021.misc_twf.common.block.entity.UltravioletLampBlockEntity;
import com.hexagram2021.misc_twf.common.register.MISCTWFMenuTypes;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class UltravioletLampMenu extends AbstractContainerMenu {
	private static final int INV_SLOT_START = 1;
	private static final int INV_SLOT_END = 28;
	private static final int USE_ROW_SLOT_START = 28;
	private static final int USE_ROW_SLOT_END = 37;

	private final Container container;
	protected final Level level;

	public UltravioletLampMenu(int id, Inventory inventory) {
		this(id, inventory, new SimpleContainer(1));
	}

	public UltravioletLampMenu(int id, Inventory inventory, Container container) {
		super(MISCTWFMenuTypes.ULTRAVIOLET_LAMP_MENU.get(), id);
		this.container = container;
		this.level = inventory.player.level;

		this.addSlot(new Slot(container, 0, 30, 34) {
			@Override
			public boolean mayPlace(ItemStack itemStack) {
				return UltravioletLampBlockEntity.isBattery(itemStack);
			}
		});

		for(int i = 0; i < 3; ++i) {
			for(int j = 0; j < 9; ++j) {
				this.addSlot(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}

		for(int k = 0; k < 9; ++k) {
			this.addSlot(new Slot(inventory, k, 8 + k * 18, 142));
		}
	}

	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);
		if (slot.hasItem()) {
			ItemStack slotItem = slot.getItem();
			itemstack = slotItem.copy();
			if(index == UltravioletLampBlockEntity.SLOT_BATTERY) {
				if (!this.moveItemStackTo(slotItem, INV_SLOT_START, USE_ROW_SLOT_END, true)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.moveItemStackTo(slotItem, UltravioletLampBlockEntity.SLOT_BATTERY, UltravioletLampBlockEntity.SLOT_BATTERY + 1, false)) {
				return ItemStack.EMPTY;
			}

			if (slotItem.isEmpty()) {
				slot.set(ItemStack.EMPTY);
			} else {
				slot.setChanged();
			}

			if (slotItem.getCount() == itemstack.getCount()) {
				return ItemStack.EMPTY;
			}

			slot.onTake(player, slotItem);
		}
		return itemstack;
	}

	@Override
	public boolean stillValid(Player player) {
		return this.container.stillValid(player);
	}
}
