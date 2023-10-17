package com.hexagram2021.misc_twf.common.network;

import com.hexagram2021.misc_twf.common.menu.AbstractTravelersBackpackTacMenu;
import com.hexagram2021.misc_twf.common.menu.container.TravelersBackpackTacContainer;
import com.hexagram2021.misc_twf.common.util.IAmmoBackpack;
import com.tiviacz.travelersbackpack.inventory.menu.TravelersBackpackBaseMenu;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class ServerboundOpenTacBackpackPacket implements IMISCTWFPacket {
	public static final byte TYPE_BACKPACK_TO_TAC_SLOT = 1;
	public static final byte TYPE_TAC_SLOT_TO_BACKPACK = 2;

	private final byte type;
	private final byte screenId;

	@Nullable
	private final BlockPos blockPos;

	public ServerboundOpenTacBackpackPacket(byte type, byte screenId) {
		this.type = type;
		this.screenId = screenId;
		assert screenId != Reference.BLOCK_ENTITY_SCREEN_ID;
		this.blockPos = null;
	}

	public ServerboundOpenTacBackpackPacket(byte type, byte screenId, BlockPos blockPos) {
		this.type = type;
		this.screenId = screenId;
		this.blockPos = blockPos;
	}

	public ServerboundOpenTacBackpackPacket(FriendlyByteBuf buf) {
		this.type = buf.readByte();
		this.screenId = buf.readByte();
		if(this.screenId == Reference.BLOCK_ENTITY_SCREEN_ID) {
			this.blockPos = buf.readBlockPos();
		} else {
			this.blockPos = null;
		}
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeByte(this.type);
		buf.writeByte(this.screenId);
		if(this.screenId == Reference.BLOCK_ENTITY_SCREEN_ID) {
			buf.writeBlockPos(Objects.requireNonNull(this.blockPos));
		}
	}

	@Override
	public void handle(NetworkEvent.Context context) {
		context.enqueueWork(() -> {
			ServerPlayer serverPlayer = context.getSender();
			if(serverPlayer != null) {
				MenuProvider toOpen = null;
				if(this.type == TYPE_BACKPACK_TO_TAC_SLOT && serverPlayer.containerMenu instanceof TravelersBackpackBaseMenu menu) {
					if (menu.container instanceof IAmmoBackpack ammoBackpack && ammoBackpack.canStoreAmmo()) {
						toOpen = new TravelersBackpackTacContainer(ammoBackpack, this.screenId);
					}
				} else if(this.type == TYPE_TAC_SLOT_TO_BACKPACK && serverPlayer.containerMenu instanceof AbstractTravelersBackpackTacMenu menu) {
					if(menu.container instanceof MenuProvider menuProvider) {
						toOpen = menuProvider;
					}
				}
				if(toOpen != null) {
					switch (this.screenId) {
						case Reference.BLOCK_ENTITY_SCREEN_ID -> NetworkHooks.openGui(serverPlayer, toOpen, Objects.requireNonNull(this.blockPos));
						case Reference.ITEM_SCREEN_ID, Reference.WEARABLE_SCREEN_ID -> NetworkHooks.openGui(serverPlayer, toOpen, buf -> buf.writeByte(this.screenId));
						default -> throw new IllegalStateException("Unknown Screen ID: " + this.screenId);
					}
				}
			}
		});
	}
}
