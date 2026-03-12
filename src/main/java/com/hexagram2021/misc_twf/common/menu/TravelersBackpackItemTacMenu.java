package com.hexagram2021.misc_twf.common.menu;

import com.hexagram2021.misc_twf.common.register.MISCTWFMenuTypes;
import com.hexagram2021.misc_twf.common.util.IAmmoBackpack;
import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.inventory.BackpackContainer;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.inventory.TravelersBackpackContainer;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

/**
 * 旅行背包物品的 TAC 弹药槽菜单喵~
 * 当旅行背包以物品形式（手持或穿戴）使用时，使用此菜单管理弹药槽交互喵~
 * 支持从主手、穿戴栏或其他玩家的穿戴栏获取背包数据喵~
 *
 * @author liudongyu
 */
public class TravelersBackpackItemTacMenu extends AbstractTravelersBackpackTacMenu {

	public TravelersBackpackItemTacMenu(int id, Inventory inventory, FriendlyByteBuf extraData) {
		this(id, inventory, createInventory(inventory, extraData));
	}

	public TravelersBackpackItemTacMenu(int id, Inventory inventory, BackpackWrapper backpackWrapper) {
		super(MISCTWFMenuTypes.TRAVELERS_BACKPACK_ITEM_TAC_SLOT_MENU.get(), id, inventory, backpackWrapper);
	}

	private static BackpackContainer createInventory(Inventory inventory, FriendlyByteBuf data) {
		Objects.requireNonNull(inventory, "playerInventory cannot be null");
		Objects.requireNonNull(data, "data cannot be null");
		byte screenID = data.readByte();
		ItemStack stack;
		if (screenID == Reference.ITEM_SCREEN_ID) {
			stack = inventory.player.getItemBySlot(EquipmentSlot.MAINHAND);
		} else if (data.writerIndex() == 5) {
			int entityId = data.readInt();
			stack = CapabilityUtils.getWearingBackpack((Player)inventory.player.level.getEntity(entityId));
			if (stack.getItem() instanceof TravelersBackpackItem) {
				return Objects.requireNonNull(CapabilityUtils.getBackpackInv((Player) inventory.player.level.getEntity(entityId)));
			}
		} else {
			stack = CapabilityUtils.getWearingBackpack(inventory.player);
		}

		if (stack.getItem() instanceof TravelersBackpackItem) {
			if (screenID == Reference.WEARABLE_SCREEN_ID) {
				return Objects.requireNonNull(CapabilityUtils.getBackpackInv(inventory.player));
			}

			if (screenID == Reference.ITEM_SCREEN_ID) {
				return new TravelersBackpackContainer(stack, inventory.player, screenID);
			}
		}

		throw new IllegalStateException("ItemStack is not correct! " + stack);
	}
}
