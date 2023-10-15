package com.hexagram2021.misc_twf.common.menu;

import com.hexagram2021.misc_twf.common.register.MISCTWFMenuTypes;
import com.hexagram2021.misc_twf.common.util.IAmmoBackpack;
import com.tiviacz.travelersbackpack.blockentity.TravelersBackpackBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Objects;

public class TravelersBackpackBlockEntityTacMenu extends AbstractTravelersBackpackTacMenu {

	public TravelersBackpackBlockEntityTacMenu(int id, Inventory inventory, FriendlyByteBuf extraData) {
		this(id, inventory, (IAmmoBackpack)getBlockEntity(inventory, extraData));
	}

	public TravelersBackpackBlockEntityTacMenu(int id, Inventory inventory, IAmmoBackpack ammoBackpack) {
		super(MISCTWFMenuTypes.TRAVELERS_BACKPACK_BLOCK_ENTITY_TAC_SLOT_MENU.get(), id, inventory, ammoBackpack);
	}

	private static TravelersBackpackBlockEntity getBlockEntity(Inventory inventory, FriendlyByteBuf data) {
		Objects.requireNonNull(inventory, "playerInventory cannot be null");
		Objects.requireNonNull(data, "data cannot be null");
		BlockEntity blockEntityAtPos = inventory.player.level.getBlockEntity(data.readBlockPos());
		if (blockEntityAtPos instanceof TravelersBackpackBlockEntity blockEntity) {
			return blockEntity;
		} else {
			throw new IllegalStateException("Block Entity is not correct! " + blockEntityAtPos);
		}
	}

	@Override
	public boolean stillValid(Player player) {
		if(this.container instanceof TravelersBackpackBlockEntity blockEntity) {
			return blockEntity.isUsableByPlayer(player);
		}
		return false;
	}
}
