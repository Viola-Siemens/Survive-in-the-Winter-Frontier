package com.hexagram2021.misc_twf_adventure.common.menu.slot;

import com.atsuishio.superbwarfare.item.ammo.AmmoBoxItem;
import com.atsuishio.superbwarfare.item.ammo.AmmoSupplierItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

/**
 * 弹药槽物品处理器，限制仅允许放入弹药物品喵~
 * 通过 TaC（永恒枪械工坊）的 IAmmo 接口检验物品是否为有效弹药喵~
 *
 * @author liudongyu
 */
public class BulletSlotItemHandler extends SlotItemHandler {
	public BulletSlotItemHandler(ItemStackHandler ammoHandler, int index, int xPosition, int yPosition) {
		super(ammoHandler, index, xPosition, yPosition);
	}

	@Override
	public boolean mayPlace(ItemStack stack) {
		return isValid(stack);
	}

	@Override
	public boolean mayPickup(Player playerIn) {
		return true;
	}

	public static boolean isValid(ItemStack stack) {
		// TaC 的 IAmmo 体系已由 Superb Warfare 取代：只接受 SW 弹药物品或弹药盒喵~
		return stack.getItem() instanceof AmmoSupplierItem || stack.getItem() instanceof AmmoBoxItem;
	}
}
