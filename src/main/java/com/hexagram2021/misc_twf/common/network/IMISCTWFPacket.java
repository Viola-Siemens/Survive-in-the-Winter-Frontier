package com.hexagram2021.misc_twf.common.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public interface IMISCTWFPacket {
	void write(FriendlyByteBuf buf);
	void handle(NetworkEvent.Context context);
}
