package com.hexagram2021.misc_twf.common.network;

import com.hexagram2021.misc_twf.common.block.entity.MonsterEggBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

public class MonsterEggAnimationPacket implements IMISCTWFPacket{

    private final int x;
    private final int y;
    private final int z;

    public MonsterEggAnimationPacket(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }



    public MonsterEggAnimationPacket(FriendlyByteBuf buf) {
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
    }

    @Override
    public void handle(NetworkEvent.Context context) {

        context.enqueueWork(() -> {
            if (!context.getDirection().getReceptionSide().isServer()) {
                BlockPos blockPos = new BlockPos(x, y, z);
                BlockEntity blockEntity = Minecraft.getInstance().level.getBlockEntity(blockPos);
                if (blockEntity instanceof MonsterEggBlockEntity monsterEggBlockEntity) {
                    monsterEggBlockEntity.refreshAnimation();
                }
            }
        });
        context.setPacketHandled(true);
    }
}
