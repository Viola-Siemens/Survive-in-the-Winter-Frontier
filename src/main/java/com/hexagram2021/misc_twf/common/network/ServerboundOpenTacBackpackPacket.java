package com.hexagram2021.misc_twf.common.network;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tiviacz.travelersbackpack.util.Reference;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Optional;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;

/**
 * 服务端方向的打开 TAC 背包界面数据包喵~
 * 用于在客户端请求服务端打开旅行者背包的 TAC 弹药槽界面喵~
 * 支持背包到 TAC 槽界面和 TAC 槽到背包界面两个方向的切换喵~
 *
 * @author liudongyu
 */
public class ServerboundOpenTacBackpackPacket implements CustomPacketPayload, IMISCTWFPacket {
	public static final Type<ServerboundOpenTacBackpackPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MODID, "open_tac_backpack"));

	public static final Codec<ServerboundOpenTacBackpackPacket> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.BYTE.fieldOf("type").forGetter(packet -> packet.type),
			Codec.BYTE.fieldOf("screen_id").forGetter(packet -> packet.screenId),
			BlockPos.CODEC.optionalFieldOf("block_pos").forGetter(packet -> packet.blockPos)
	).apply(instance, ServerboundOpenTacBackpackPacket::new));
	public static final StreamCodec<ByteBuf, ServerboundOpenTacBackpackPacket> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.BYTE, packet -> packet.type,
			ByteBufCodecs.BYTE, packet -> packet.screenId,
			ByteBufCodecs.optional(BlockPos.STREAM_CODEC), packet -> packet.blockPos,
			ServerboundOpenTacBackpackPacket::new
	);

	public static final byte TYPE_BACKPACK_TO_TAC_SLOT = 1;
	public static final byte TYPE_TAC_SLOT_TO_BACKPACK = 2;

	private final byte type;
	private final byte screenId;

	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	private final Optional<BlockPos> blockPos;

	/**
	 * 构造打开 TAC 背包界面数据包（无方块位置）喵~
	 *
	 * @param type 界面切换类型，TYPE_BACKPACK_TO_TAC_SLOT 或 TYPE_TAC_SLOT_TO_BACKPACK 喵~
	 * @param screenId 界面 ID，用于区分背包来源（物品、穿戴或方块实体）喵~
	 */
	public ServerboundOpenTacBackpackPacket(byte type, byte screenId) {
		this(type, screenId, Optional.empty());
	}

	/**
	 * 构造打开 TAC 背包界面数据包（带方块位置）喵~
	 *
	 * @param type 界面切换类型，TYPE_BACKPACK_TO_TAC_SLOT 或 TYPE_TAC_SLOT_TO_BACKPACK 喵~
	 * @param screenId 界面 ID，用于区分背包来源（物品、穿戴或方块实体）喵~
	 * @param blockPos 方块实体背包的位置，仅当 screenId 为 BLOCK_ENTITY_SCREEN_ID 时需要喵~
	 */
	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	public ServerboundOpenTacBackpackPacket(byte type, byte screenId, Optional<BlockPos> blockPos) {
		this.type = type;
		this.screenId = screenId;
		this.blockPos = blockPos;

		if(blockPos.isEmpty() && screenId == Reference.BLOCK_ENTITY_SCREEN_ID) {
			throw new IllegalArgumentException(String.valueOf(screenId));
		}
	}

	@Override
	public void handle(IPayloadContext context) {
	}

	@Override
	public Type<ServerboundOpenTacBackpackPacket> type() {
		return TYPE;
	}
}
