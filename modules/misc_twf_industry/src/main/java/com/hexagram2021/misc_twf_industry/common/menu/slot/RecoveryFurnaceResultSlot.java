package com.hexagram2021.misc_twf_industry.common.menu.slot;

import com.hexagram2021.misc_twf_industry.common.block.entity.RecoveryFurnaceBlockEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.EventHooks;

/**
 * 回收炉产物槽位
 *
 * @author liudongyu
 */
public class RecoveryFurnaceResultSlot extends Slot {
	private final Player player;
	private int removeCount;

	/**
	 * 构造函数
	 * @param player 玩家
	 * @param container 回收炉方块实体
	 * @param index 槽位下标
	 * @param x 横坐标
	 * @param y 纵坐标
	 */
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
		itemStack.onCraftedBy(this.player.level(), this.player, this.removeCount);
		if (this.player instanceof ServerPlayer serverPlayer && this.container instanceof RecoveryFurnaceBlockEntity recoveryFurnace) {
			recoveryFurnace.awardUsedRecipesAndPopExperience(serverPlayer);
		}

		this.removeCount = 0;
		EventHooks.firePlayerSmeltedEvent(this.player, itemStack);
	}
}
