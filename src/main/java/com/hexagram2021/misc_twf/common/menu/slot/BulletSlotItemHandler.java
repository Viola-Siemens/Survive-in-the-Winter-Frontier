package com.hexagram2021.misc_twf.common.menu.slot;

import com.hexagram2021.misc_twf.common.util.IAmmoBackpack;
import com.tac.guns.item.IAmmo;
import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackContainer;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

public class BulletSlotItemHandler extends SlotItemHandler {
	private final Player player;
	private final IAmmoBackpack container;

	public BulletSlotItemHandler(Player player, IAmmoBackpack container, int index, int xPosition, int yPosition) {
		super(container.getAmmoHandler(), index, xPosition, yPosition);
		this.player = player;
		this.container = container;
	}

	@Override
	public boolean mayPlace(ItemStack stack) {
		return isValid(stack);
	}

	public static boolean isValid(ItemStack stack) {
		return stack.getItem() instanceof IAmmo;
	}

	@Override
	public void setChanged() {
		super.setChanged();
		if (this.container instanceof ITravelersBackpackContainer travelersBackpackContainer &&
				travelersBackpackContainer.getScreenID() == Reference.WEARABLE_SCREEN_ID) {
			CapabilityUtils.synchronise(this.player);
			CapabilityUtils.synchroniseToOthers(this.player);
		}
	}
}
