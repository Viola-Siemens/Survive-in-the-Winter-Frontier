package com.hexagram2021.misc_twf.common.network;

import com.hexagram2021.misc_twf.common.menu.AbstractTravelersBackpackTacMenu;
import com.hexagram2021.misc_twf.common.menu.container.TravelersBackpackTacContainer;
import com.hexagram2021.misc_twf.common.util.IAmmoBackpack;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackBaseMenu;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

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
			BlockPos.CODEC.optionalFieldOf("block_pos", null).forGetter(packet -> packet.blockPos)
	).apply(instance, ServerboundOpenTacBackpackPacket::new));

	public static final byte TYPE_BACKPACK_TO_TAC_SLOT = 1;
	public static final byte TYPE_TAC_SLOT_TO_BACKPACK = 2;

	private final byte type;
	private final byte screenId;

	@Nullable
	private final BlockPos blockPos;

	/**
	 * 构造打开 TAC 背包界面数据包（无方块位置）喵~
	 *
	 * @param type 界面切换类型，TYPE_BACKPACK_TO_TAC_SLOT 或 TYPE_TAC_SLOT_TO_BACKPACK 喵~
	 * @param screenId 界面 ID，用于区分背包来源（物品、穿戴或方块实体）喵~
	 */
	public ServerboundOpenTacBackpackPacket(byte type, byte screenId) {
		this(type, screenId, null);
	}

	/**
	 * 构造打开 TAC 背包界面数据包（带方块位置）喵~
	 *
	 * @param type 界面切换类型，TYPE_BACKPACK_TO_TAC_SLOT 或 TYPE_TAC_SLOT_TO_BACKPACK 喵~
	 * @param screenId 界面 ID，用于区分背包来源（物品、穿戴或方块实体）喵~
	 * @param blockPos 方块实体背包的位置，仅当 screenId 为 BLOCK_ENTITY_SCREEN_ID 时需要喵~
	 */
	public ServerboundOpenTacBackpackPacket(byte type, byte screenId, @Nullable BlockPos blockPos) {
		this.type = type;
		this.screenId = screenId;
		this.blockPos = blockPos;

		if(blockPos == null && screenId == Reference.BLOCK_ENTITY_SCREEN_ID) {
			throw new IllegalArgumentException(String.valueOf(screenId));
		}
	}

	@Override
	public void handle(IPayloadContext context) {
		context.enqueueWork(() -> {
			if(context.player() instanceof ServerPlayer serverPlayer) {
				MenuProvider toOpen = null;
				if(this.type == TYPE_BACKPACK_TO_TAC_SLOT && serverPlayer.containerMenu instanceof BackpackBaseMenu menu) {
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
						case Reference.BLOCK_ENTITY_SCREEN_ID -> serverPlayer.openMenu(toOpen, Objects.requireNonNull(this.blockPos));
						case Reference.ITEM_SCREEN_ID, Reference.WEARABLE_SCREEN_ID -> serverPlayer.openMenu(toOpen, buf -> buf.writeByte(this.screenId));
						default -> throw new IllegalStateException("Unknown Screen ID: " + this.screenId);
					}
				}
			}
		});
	}

	@Override
	public Type<ServerboundOpenTacBackpackPacket> type() {
		return TYPE;
	}
}
