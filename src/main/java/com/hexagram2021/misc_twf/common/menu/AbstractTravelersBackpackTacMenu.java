package com.hexagram2021.misc_twf.common.menu;

import com.hexagram2021.misc_twf.common.menu.slot.BulletSlotItemHandler;
import com.hexagram2021.misc_twf.common.util.IAmmoBackpack;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackContainer;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractTravelersBackpackTacMenu extends AbstractContainerMenu {
	protected final Inventory inventory;
	public final IAmmoBackpack container;

	protected static final int CONTAINER_START = 0;
	protected static final int CONTAINER_END = 9;
	protected static final int INV_START = 9;
	protected static final int INV_END = 45;

	protected AbstractTravelersBackpackTacMenu(@Nullable MenuType<?> menuType, int id, Inventory inventory, IAmmoBackpack ammoBackpack) {
		super(menuType, id);
		this.inventory = inventory;
		this.container = ammoBackpack;

		for(int i = 0; i < 9; ++i) {
			this.addSlot(new BulletSlotItemHandler(inventory.player, ammoBackpack, i, 8 + i * 18, 17));
		}

		for(int i = 0; i < 3; ++i) {
			for(int j = 0; j < 9; ++j) {
				this.addSlot(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 61 + i * 18));
			}
		}

		for(int i = 0; i < 9; ++i) {
			this.addSlot(new Slot(inventory, i, 8 + i * 18, 119));
		}
	}

	@SuppressWarnings("ConstantConditions")
	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		Slot slot = this.getSlot(index);
		ItemStack result = ItemStack.EMPTY;
		if (slot != null && slot.hasItem()) {
			ItemStack stack = slot.getItem();
			result = stack.copy();
			if(index >= CONTAINER_START && index < CONTAINER_END) {
				if(!this.moveItemStackTo(stack, INV_START, INV_END, true)) {
					return ItemStack.EMPTY;
				}
			} else if(!this.moveItemStackTo(stack, CONTAINER_START, CONTAINER_END, false)) {
				return ItemStack.EMPTY;
			}
			if(stack.isEmpty()) {
				slot.set(ItemStack.EMPTY);
			} else {
				slot.setChanged();
			}

			if (stack.getCount() == result.getCount()) {
				return ItemStack.EMPTY;
			}

			slot.onTake(player, stack);
		}
		return result;
	}

	@Override
	public void removed(Player player) {
		if(this.container instanceof ITravelersBackpackContainer container) {
			if(container.getScreenID() == Reference.BLOCK_ENTITY_SCREEN_ID) {
				container.setUsingPlayer(null);
			} else {
				container.setDataChanged((byte)10);
			}
		}
		super.removed(player);
	}

	@Override
	public boolean stillValid(Player player) {
		return true;
	}
}
