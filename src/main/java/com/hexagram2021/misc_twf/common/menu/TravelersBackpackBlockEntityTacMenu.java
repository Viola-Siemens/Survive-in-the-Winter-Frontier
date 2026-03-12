package com.hexagram2021.misc_twf.common.menu;

import com.hexagram2021.misc_twf.common.register.MISCTWFMenuTypes;
import com.tiviacz.travelersbackpack.blockentity.BackpackBlockEntity;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Objects;

/**
 * 旅行背包方块实体的 TAC 弹药槽菜单喵~
 * 当旅行背包以方块实体形式放置在世界中时，使用此菜单管理弹药槽交互喵~
 * 包含基于方块位置的有效性检查，确保玩家在交互范围内喵~
 *
 * @author liudongyu
 */
public class TravelersBackpackBlockEntityTacMenu extends AbstractTravelersBackpackTacMenu {
	private final ContainerLevelAccess access;

	public TravelersBackpackBlockEntityTacMenu(int id, Inventory inventory, FriendlyByteBuf extraData) {
		this(id, inventory, getBlockEntity(inventory, extraData).getWrapper());
	}

	public TravelersBackpackBlockEntityTacMenu(int id, Inventory inventory, BackpackWrapper wrapper) {
		super(MISCTWFMenuTypes.TRAVELERS_BACKPACK_BLOCK_ENTITY_TAC_SLOT_MENU.get(), id, inventory, wrapper);
		this.access = ContainerLevelAccess.create(this.player.level(), this.getWrapper().getBackpackPos());
	}

	private static BackpackBlockEntity getBlockEntity(Inventory inventory, FriendlyByteBuf data) {
		Objects.requireNonNull(inventory, "playerInventory cannot be null");
		Objects.requireNonNull(data, "data cannot be null");
		BlockEntity blockEntityAtPos = inventory.player.level().getBlockEntity(data.readBlockPos());
		if (blockEntityAtPos instanceof BackpackBlockEntity blockEntity) {
			return blockEntity;
		}
		throw new IllegalStateException("Block Entity is not correct! " + blockEntityAtPos);
	}

	@Override
	public boolean stillValid(Player player) {
		return this.access.evaluate((level, blockPos) -> player.canInteractWithBlock(blockPos, 4.0F), true);
	}
}
