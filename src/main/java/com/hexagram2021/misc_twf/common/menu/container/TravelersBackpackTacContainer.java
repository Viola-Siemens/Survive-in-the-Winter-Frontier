package com.hexagram2021.misc_twf.common.menu.container;

import com.hexagram2021.misc_twf.common.menu.TravelersBackpackBlockEntityTacMenu;
import com.hexagram2021.misc_twf.common.menu.TravelersBackpackItemTacMenu;
import com.hexagram2021.misc_twf.common.util.IAmmoBackpack;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

public record TravelersBackpackTacContainer(IAmmoBackpack ammoBackpack, byte screenId) implements MenuProvider {
	@Override
	public Component getDisplayName() {
		return new TranslatableComponent("item.misc_twf.travelers_backpack_tac_slot");
	}

	@Override
	public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
		return switch (this.screenId) {
			case Reference.ITEM_SCREEN_ID, Reference.WEARABLE_SCREEN_ID ->
					new TravelersBackpackItemTacMenu(id, inventory, this.ammoBackpack);
			case Reference.BLOCK_ENTITY_SCREEN_ID ->
					new TravelersBackpackBlockEntityTacMenu(id, inventory, this.ammoBackpack);
			default -> throw new IllegalStateException("Unknown Screen ID: " + this.screenId);
		};
	}
}
