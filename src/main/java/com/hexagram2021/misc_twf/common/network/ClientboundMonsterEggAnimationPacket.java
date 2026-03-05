package com.hexagram2021.misc_twf.common.network;

import com.hexagram2021.misc_twf.common.block.entity.MonsterEggBlockEntity;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Objects;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;

/**
 * 客户端方向的怪物蛋动画数据包喵~
 * 当服务端检测到怪物蛋附近有带"曝光"效果的实体时，会发送此数据包到客户端，触发怪物蛋的震动动画喵~
 *
 * @param x 怪物蛋方块的 X 坐标喵~
 * @param y 怪物蛋方块的 Y 坐标喵~
 * @param z 怪物蛋方块的 Z 坐标喵~
 *
 * @author liudongyu
 */
public record ClientboundMonsterEggAnimationPacket(int x, int y, int z) implements CustomPacketPayload, IMISCTWFPacket {
	public static final Type<ClientboundMonsterEggAnimationPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MODID, "monster_egg_animation"));
	public static final Codec<ClientboundMonsterEggAnimationPacket> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.INT.fieldOf("x").forGetter(ClientboundMonsterEggAnimationPacket::x),
			Codec.INT.fieldOf("y").forGetter(ClientboundMonsterEggAnimationPacket::y),
			Codec.INT.fieldOf("z").forGetter(ClientboundMonsterEggAnimationPacket::z)
	).apply(instance, ClientboundMonsterEggAnimationPacket::new));
	public static final StreamCodec<ByteBuf, ClientboundMonsterEggAnimationPacket> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.INT, ClientboundMonsterEggAnimationPacket::x,
			ByteBufCodecs.INT, ClientboundMonsterEggAnimationPacket::y,
			ByteBufCodecs.INT, ClientboundMonsterEggAnimationPacket::z,
			ClientboundMonsterEggAnimationPacket::new
	);

	@Override
    public void handle(IPayloadContext context) {
		if (context.player().level().isClientSide) {
			BlockPos blockPos = new BlockPos(this.x, this.y, this.z);
			BlockEntity blockEntity = Objects.requireNonNull(Minecraft.getInstance().level).getBlockEntity(blockPos);
			if (blockEntity instanceof MonsterEggBlockEntity monsterEggBlockEntity) {
				monsterEggBlockEntity.refreshAnimation();
			}
		}
    }

	@Override
	public Type<ClientboundMonsterEggAnimationPacket> type() {
		return TYPE;
	}
}
