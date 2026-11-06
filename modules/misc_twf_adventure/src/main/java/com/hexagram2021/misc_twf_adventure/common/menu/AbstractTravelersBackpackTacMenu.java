package com.hexagram2021.misc_twf_adventure.common.menu;

import com.hexagram2021.misc_twf_adventure.common.menu.slot.BulletSlotItemHandler;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackBaseMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * 旅行背包 TAC 弹药槽菜单的抽象基类喵~
 *
 * <p>1.21 的 Travelers Backpack 已把背包状态统一到 {@link BackpackWrapper}，因此本类直接沿用
 * {@link BackpackBaseMenu} 的完整槽位布局（背包库存/工具/升级/玩家背包），只在末尾追加 9 格弹药槽喵~</p>
 *
 * @author liudongyu
 */
public abstract class AbstractTravelersBackpackTacMenu extends BackpackBaseMenu {
	private final BackpackAmmoHandler ammoHandler;
	private final int ammoStart;
	private final int ammoEnd;

	protected AbstractTravelersBackpackTacMenu(MenuType<?> menuType, int id, Inventory inventory, BackpackWrapper wrapper) {
		super(menuType, id, inventory, wrapper);
		this.ammoHandler = new BackpackAmmoHandler(wrapper);
		this.ammoStart = this.slots.size();
		for(int i = 0; i < BackpackAmmoHandler.SLOTS; i++) {
			this.addSlot(new BulletSlotItemHandler(this.ammoHandler, i, 8 + i * 18, 17));
		}
		this.ammoEnd = this.slots.size();
	}

	/**
	 * 获取弹药槽处理器喵~
	 *
	 * @return 弹药槽处理器喵~
	 */
	public BackpackAmmoHandler getAmmoHandler() {
		return this.ammoHandler;
	}

	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		Slot slot = this.getSlot(index);
		if(slot == null || !slot.hasItem()) {
			return ItemStack.EMPTY;
		}
		ItemStack stack = slot.getItem();
		ItemStack result = stack.copy();
		if(index >= this.ammoStart && index < this.ammoEnd) {
			if(!this.moveItemStackTo(stack, this.PLAYER_INV_START, this.PLAYER_HOT_END, true)) {
				return ItemStack.EMPTY;
			}
		} else if(index >= this.PLAYER_INV_START && index < this.PLAYER_HOT_END) {
			// 优先放进弹药槽，其次放回背包库存喵~
			if(!this.moveItemStackTo(stack, this.ammoStart, this.ammoEnd, false)
					&& !this.moveItemStackTo(stack, this.BACKPACK_INV_START, this.BACKPACK_INV_END, false)) {
				return ItemStack.EMPTY;
			}
		} else if(!this.moveItemStackTo(stack, this.PLAYER_INV_START, this.PLAYER_HOT_END, true)) {
			return ItemStack.EMPTY;
		}
		if(stack.isEmpty()) {
			slot.set(ItemStack.EMPTY);
		} else {
			slot.setChanged();
		}
		if(stack.getCount() == result.getCount()) {
			return ItemStack.EMPTY;
		}
		slot.onTake(player, stack);
		return result;
	}
}