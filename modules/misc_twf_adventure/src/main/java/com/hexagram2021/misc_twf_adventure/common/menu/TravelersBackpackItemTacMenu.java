package com.hexagram2021.misc_twf_adventure.common.menu;

import com.hexagram2021.misc_twf_adventure.common.register.MISCTWFMenuTypes;
import com.tiviacz.travelersbackpack.capability.AttachmentUtils;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * 旅行背包物品形态的 TAC 弹药槽菜单喵~
 *
 * <p>1.21 的 Travelers Backpack 用 {@link BackpackWrapper} 描述背包状态，网络打开菜单时
 * 服务端通过 {@code writeClientSideData} 写入「界面 ID + 槽位索引」，客户端据此重建 wrapper喵~</p>
 *
 * @author liudongyu
 */
public class TravelersBackpackItemTacMenu extends AbstractTravelersBackpackTacMenu {
	/**
	 * 客户端网络构造喵~
	 *
	 * @param id        窗口 ID喵~
	 * @param inventory 玩家物品栏喵~
	 * @param extraData 服务端写入的附加数据喵~
	 */
	public TravelersBackpackItemTacMenu(int id, Inventory inventory, RegistryFriendlyByteBuf extraData) {
		this(id, inventory, decode(inventory, extraData));
	}

	/**
	 * 服务端构造喵~
	 *
	 * @param id        窗口 ID喵~
	 * @param inventory 玩家物品栏喵~
	 * @param wrapper   背包状态喵~
	 */
	public TravelersBackpackItemTacMenu(int id, Inventory inventory, BackpackWrapper wrapper) {
		super(MISCTWFMenuTypes.TRAVELERS_BACKPACK_ITEM_TAC_SLOT_MENU.get(), id, inventory, wrapper);
	}

	private static BackpackWrapper decode(Inventory inventory, RegistryFriendlyByteBuf data) {
		Player player = inventory.player;
		int screenId = data.readInt();
		int index = data.readInt();
		if(screenId == Reference.WEARABLE_SCREEN_ID) {
			BackpackWrapper worn = AttachmentUtils.getBackpackWrapper(player);
			if(worn != null) {
				worn.addUser(player);
				return worn;
			}
		}
		ItemStack stack = index >= 0 && index < inventory.items.size() ? inventory.items.get(index) : player.getMainHandItem();
		if(!(stack.getItem() instanceof TravelersBackpackItem)) {
			stack = AttachmentUtils.getWearingBackpack(player);
		}
		BackpackWrapper wrapper = new BackpackWrapper(stack, screenId, player, player.level(), index);
		wrapper.addUser(player);
		return wrapper;
	}
}