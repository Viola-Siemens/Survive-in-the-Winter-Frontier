package com.hexagram2021.misc_twf_adventure.common.menu.container;

import com.hexagram2021.misc_twf_adventure.common.menu.TravelersBackpackBlockEntityTacMenu;
import com.hexagram2021.misc_twf_adventure.common.menu.TravelersBackpackItemTacMenu;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.Nullable;

/**
 * 旅行背包 TAC 弹药槽的菜单提供者喵~
 *
 * <p>打开时把「界面 ID」以及「方块位置或槽位索引」写入客户端数据，供菜单在客户端重建背包状态喵~</p>
 *
 * @param backpackWrapper 背包状态喵~
 * @param screenId        界面 ID（物品/穿戴/方块实体）喵~
 * @param blockPos        方块实体位置；仅方块实体界面使用喵~
 * @param entityId        物品槽位索引；仅物品/穿戴界面使用喵~
 * @author liudongyu
 */
public record TravelersBackpackTacContainer(BackpackWrapper backpackWrapper, int screenId,
                                            @Nullable BlockPos blockPos, int entityId) implements MenuProvider {
	@Override
	public Component getDisplayName() {
		return Component.translatable("item.misc_twf.travelers_backpack_tac_slot");
	}

	@Override
	public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
		return switch(this.screenId) {
			case Reference.ITEM_SCREEN_ID, Reference.WEARABLE_SCREEN_ID ->
					new TravelersBackpackItemTacMenu(id, inventory, this.backpackWrapper);
			case Reference.BLOCK_ENTITY_SCREEN_ID ->
					new TravelersBackpackBlockEntityTacMenu(id, inventory, this.backpackWrapper);
			default -> throw new IllegalStateException("Unknown Screen ID: " + this.screenId);
		};
	}

	@Override
	public void writeClientSideData(AbstractContainerMenu menu, RegistryFriendlyByteBuf buffer) {
		buffer.writeInt(this.screenId);
		if(this.screenId == Reference.BLOCK_ENTITY_SCREEN_ID && this.blockPos != null) {
			buffer.writeBlockPos(this.blockPos);
		} else {
			buffer.writeInt(this.entityId);
		}
	}
}