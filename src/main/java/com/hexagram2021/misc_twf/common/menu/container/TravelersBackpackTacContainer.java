package com.hexagram2021.misc_twf.common.menu.container;

import com.hexagram2021.misc_twf.common.menu.TravelersBackpackBlockEntityTacMenu;
import com.hexagram2021.misc_twf.common.util.IAmmoBackpack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

public record TravelersBackpackTacContainer(IAmmoBackpack ammoBackpack) implements MenuProvider {

	@Override
	public Component getDisplayName() {
		return new TranslatableComponent("item.misc_twf.travelers_backpack_tac_slot");
	}

	@Override
	public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
		return new TravelersBackpackBlockEntityTacMenu(id, inventory, this.ammoBackpack);
	}
}
