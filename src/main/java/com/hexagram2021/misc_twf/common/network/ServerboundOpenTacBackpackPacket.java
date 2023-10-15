package com.hexagram2021.misc_twf.common.network;

import com.hexagram2021.misc_twf.common.menu.container.TravelersBackpackTacContainer;
import com.hexagram2021.misc_twf.common.util.IAmmoBackpack;
import com.tiviacz.travelersbackpack.inventory.menu.TravelersBackpackItemMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

public class ServerboundOpenTacBackpackPacket implements IMISCTWFPacket {
	public ServerboundOpenTacBackpackPacket() {
	}

	public ServerboundOpenTacBackpackPacket(FriendlyByteBuf buf) {
	}

	@Override
	public void write(FriendlyByteBuf buf) {
	}

	@Override
	public void handle(NetworkEvent.Context context) {
		context.enqueueWork(() -> {
			ServerPlayer serverPlayer = context.getSender();
			if(serverPlayer != null && serverPlayer.containerMenu instanceof TravelersBackpackItemMenu menu) {
				if(menu.container instanceof IAmmoBackpack ammoBackpack && ammoBackpack.canStoreAmmo()) {
					serverPlayer.openMenu(new TravelersBackpackTacContainer(ammoBackpack));
				}
			}
		});
	}
}
