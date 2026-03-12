package com.hexagram2021.misc_twf.common.menu.container;

import com.hexagram2021.misc_twf.common.menu.TravelersBackpackBlockEntityTacMenu;
import com.hexagram2021.misc_twf.common.menu.TravelersBackpackItemTacMenu;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

/**
 * 旅行背包 TAC 弹药槽的菜单提供者喵~
 * 根据 screenId 创建对应的菜单实例（物品菜单或方块实体菜单）喵~
 *
 * @param backpackWrapper 背包数据包装器喵~
 * @param screenId 界面 ID，区分物品、穿戴或方块实体来源喵~
 * @author liudongyu
 */
public record TravelersBackpackTacContainer(BackpackWrapper backpackWrapper, byte screenId) implements MenuProvider {
	@Override
	public Component getDisplayName() {
		return Component.translatable("item.misc_twf.travelers_backpack_tac_slot");
	}

	@Override
	public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
		return switch (this.screenId) {
			case Reference.ITEM_SCREEN_ID, Reference.WEARABLE_SCREEN_ID ->
					new TravelersBackpackItemTacMenu(id, inventory, this.backpackWrapper);
			case Reference.BLOCK_ENTITY_SCREEN_ID ->
					new TravelersBackpackBlockEntityTacMenu(id, inventory, this.backpackWrapper);
			default -> throw new IllegalStateException("Unknown Screen ID: " + this.screenId);
		};
	}
}
