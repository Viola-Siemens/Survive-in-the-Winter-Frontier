package com.hexagram2021.misc_twf_adventure.common.network;

import com.hexagram2021.misc_twf_adventure.common.menu.AbstractTravelersBackpackTacMenu;
import com.hexagram2021.misc_twf_adventure.common.menu.BackpackAmmoHandler;
import com.hexagram2021.misc_twf_adventure.common.menu.container.TravelersBackpackTacContainer;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tiviacz.travelersbackpack.inventory.BackpackContainer;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackBaseMenu;
import com.tiviacz.travelersbackpack.util.Reference;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Optional;

import static com.hexagram2021.misc_twf_adventure.MiscTwfAdventure.CONTENT_NAMESPACE;

/**
 * 服务端方向的「切换 TAC 弹药槽界面」数据包喵~
 *
 * <p>1.21 的 Travelers Backpack 用 {@code BackpackWrapper} 描述背包状态，本包只在
 * 背包界面与 TAC 弹药槽界面之间切换，所有附加数据由 {@link TravelersBackpackTacContainer#writeClientSideData}
 * 写入客户端喵~</p>
 *
 * @author liudongyu
 */
public class ServerboundOpenTacBackpackPacket implements CustomPacketPayload, IMISCTWFPacket {
	public static final Type<ServerboundOpenTacBackpackPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(CONTENT_NAMESPACE, "open_tac_backpack"));

	public static final Codec<ServerboundOpenTacBackpackPacket> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.BYTE.fieldOf("type").forGetter(packet -> packet.type),
			Codec.INT.fieldOf("screen_id").forGetter(packet -> packet.screenId),
			BlockPos.CODEC.optionalFieldOf("block_pos").forGetter(packet -> packet.blockPos)
	).apply(instance, ServerboundOpenTacBackpackPacket::new));
	public static final StreamCodec<ByteBuf, ServerboundOpenTacBackpackPacket> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.BYTE, packet -> packet.type,
			ByteBufCodecs.INT, packet -> packet.screenId,
			ByteBufCodecs.optional(BlockPos.STREAM_CODEC), packet -> packet.blockPos,
			ServerboundOpenTacBackpackPacket::new
	);

	/** 从背包界面切换到 TAC 弹药槽界面喵~ */
	public static final byte TYPE_BACKPACK_TO_TAC_SLOT = 1;
	/** 从 TAC 弹药槽界面切换回背包界面喵~ */
	public static final byte TYPE_TAC_SLOT_TO_BACKPACK = 2;

	private final byte type;
	private final int screenId;

	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	private final Optional<BlockPos> blockPos;

	/**
	 * 构造不带方块位置的数据包喵~
	 *
	 * @param type     切换类型喵~
	 * @param screenId 界面 ID喵~
	 */
	public ServerboundOpenTacBackpackPacket(byte type, int screenId) {
		this(type, screenId, Optional.empty());
	}

	/**
	 * 构造带方块位置的数据包喵~
	 *
	 * @param type     切换类型喵~
	 * @param screenId 界面 ID喵~
	 * @param blockPos 方块实体背包的位置喵~
	 */
	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	public ServerboundOpenTacBackpackPacket(byte type, int screenId, Optional<BlockPos> blockPos) {
		this.type = type;
		this.screenId = screenId;
		this.blockPos = blockPos;
		if(blockPos.isEmpty() && screenId == Reference.BLOCK_ENTITY_SCREEN_ID) {
			throw new IllegalArgumentException(String.valueOf(screenId));
		}
	}

	@Override
	public void handle(IPayloadContext context) {
		context.enqueueWork(() -> {
			if(!(context.player() instanceof ServerPlayer serverPlayer)) {
				return;
			}
			if(this.type == TYPE_BACKPACK_TO_TAC_SLOT && serverPlayer.containerMenu instanceof BackpackBaseMenu menu) {
				BackpackWrapper wrapper = menu.getWrapper();
				if(!BackpackAmmoHandler.canStoreAmmo(wrapper)) {
					return;
				}
				MenuProvider provider = this.screenId == Reference.BLOCK_ENTITY_SCREEN_ID
						? new TravelersBackpackTacContainer(wrapper, this.screenId, this.blockPos.orElse(null), -1)
						: new TravelersBackpackTacContainer(wrapper, this.screenId, null, wrapper.getBackpackSlotIndex());
				// 菜单提供者会把「界面 ID + 位置/索引」写入客户端数据喵~
				serverPlayer.openMenu(provider);
			} else if(this.type == TYPE_TAC_SLOT_TO_BACKPACK
					&& serverPlayer.containerMenu instanceof AbstractTravelersBackpackTacMenu menu) {
				BackpackWrapper wrapper = menu.getWrapper();
				BackpackContainer.openBackpack(serverPlayer, wrapper.getBackpackStack(),
						wrapper.getScreenID(), wrapper.getBackpackSlotIndex());
			}
		});
	}

	@Override
	public Type<ServerboundOpenTacBackpackPacket> type() {
		return TYPE;
	}
}