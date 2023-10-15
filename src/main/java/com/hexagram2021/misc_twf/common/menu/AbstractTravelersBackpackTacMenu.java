package com.hexagram2021.misc_twf.common.menu;

import com.hexagram2021.misc_twf.common.menu.slot.BulletSlotItemHandler;
import com.hexagram2021.misc_twf.common.util.IAmmoBackpack;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractTravelersBackpackTacMenu extends AbstractContainerMenu {
	protected final Inventory inventory;
	protected final IAmmoBackpack container;

	protected static final int CONTAINER_START = 0;
	protected static final int CONTAINER_END = 9;
	protected static final int INV_START = 9;
	protected static final int INV_END = 45;

	protected AbstractTravelersBackpackTacMenu(@Nullable MenuType<?> menuType, int id, Inventory inventory, IAmmoBackpack ammoBackpack) {
		super(menuType, id);
		this.inventory = inventory;
		this.container = ammoBackpack;

		for(int k = 0; k < 9; ++k) {
			this.addSlot(new BulletSlotItemHandler(inventory.player, ammoBackpack, 0, , ));
		}

		for(int i = 0; i < 3; ++i) {
			for(int j = 0; j < 9; ++j) {
				this.addSlot(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}

		for(int k = 0; k < 9; ++k) {
			this.addSlot(new Slot(inventory, k, 8 + k * 18, 142));
		}
	}
}
