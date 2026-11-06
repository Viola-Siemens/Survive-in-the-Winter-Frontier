package com.hexagram2021.misc_twf_adventure.common.menu;

import com.hexagram2021.misc_twf_adventure.common.register.MISCTWFMenuTypes;
import com.tiviacz.travelersbackpack.blockentity.BackpackBlockEntity;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Objects;

/**
 * 旅行背包方块实体形态的 TAC 弹药槽菜单喵~
 *
 * @author liudongyu
 */
public class TravelersBackpackBlockEntityTacMenu extends AbstractTravelersBackpackTacMenu {
	private final ContainerLevelAccess access;

	/**
	 * 客户端网络构造喵~
	 *
	 * @param id        窗口 ID喵~
	 * @param inventory 玩家物品栏喵~
	 * @param extraData 服务端写入的附加数据喵~
	 */
	public TravelersBackpackBlockEntityTacMenu(int id, Inventory inventory, RegistryFriendlyByteBuf extraData) {
		this(id, inventory, decode(inventory, extraData));
	}

	/**
	 * 服务端构造喵~
	 *
	 * @param id        窗口 ID喵~
	 * @param inventory 玩家物品栏喵~
	 * @param wrapper   背包状态喵~
	 */
	public TravelersBackpackBlockEntityTacMenu(int id, Inventory inventory, BackpackWrapper wrapper) {
		super(MISCTWFMenuTypes.TRAVELERS_BACKPACK_BLOCK_ENTITY_TAC_SLOT_MENU.get(), id, inventory, wrapper);
		this.access = ContainerLevelAccess.create(inventory.player.level(), wrapper.getBackpackPos());
	}

	private static BackpackWrapper decode(Inventory inventory, RegistryFriendlyByteBuf data) {
		Objects.requireNonNull(inventory, "playerInventory cannot be null");
		Objects.requireNonNull(data, "data cannot be null");
		data.readInt();
		BlockPos blockPos = data.readBlockPos();
		BlockEntity blockEntityAtPos = inventory.player.level().getBlockEntity(blockPos);
		if(blockEntityAtPos instanceof BackpackBlockEntity blockEntity) {
			return blockEntity.getWrapper();
		}
		throw new IllegalStateException("Block Entity is not correct! " + blockEntityAtPos);
	}

	@Override
	public boolean stillValid(Player player) {
		return this.access.evaluate((level, blockPos) -> player.canInteractWithBlock(blockPos, 4.0F), true);
	}
}