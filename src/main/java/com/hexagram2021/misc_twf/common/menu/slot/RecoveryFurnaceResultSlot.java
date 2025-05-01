package com.hexagram2021.misc_twf.common.menu.slot;

import com.hexagram2021.misc_twf.common.block.entity.RecoveryFurnaceBlockEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.ForgeEventFactory;

public class RecoveryFurnaceResultSlot extends Slot {
	private final Player player;
	private int removeCount;

	public RecoveryFurnaceResultSlot(Player player, Container container, int index, int x, int y) {
		super(container, index, x, y);
		this.player = player;
	}

	@Override
	public boolean mayPlace(ItemStack itemStack) {
		return false;
	}

	@Override
	public ItemStack remove(int count) {
		if (this.hasItem()) {
			this.removeCount += Math.min(count, this.getItem().getCount());
		}

		return super.remove(count);
	}

	@Override
	public void onTake(Player player, ItemStack itemStack) {
		this.checkTakeAchievements(itemStack);
		super.onTake(player, itemStack);
	}

	@Override
	protected void onQuickCraft(ItemStack itemStack, int count) {
		this.removeCount += count;
		this.checkTakeAchievements(itemStack);
	}

	@Override
	protected void checkTakeAchievements(ItemStack itemStack) {
		itemStack.onCraftedBy(this.player.level, this.player, this.removeCount);
		if (this.player instanceof ServerPlayer && this.container instanceof RecoveryFurnaceBlockEntity recoveryFurnace) {
			recoveryFurnace.awardUsedRecipesAndPopExperience((ServerPlayer)this.player);
		}

		this.removeCount = 0;
		ForgeEventFactory.firePlayerSmeltedEvent(this.player, itemStack);
	}
}
