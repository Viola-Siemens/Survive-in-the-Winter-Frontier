package com.hexagram2021.misc_twf.common.data_component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * 旅行者背包枪械升级数据组件喵~
 * <p>
 * 该数据组件用于存储旅行者背包的枪械升级状态和弹药库存信息喵~
 * 当背包升级为枪械背包后，可以存储额外的弹药物品喵~
 * </p>
 *
 * @param upgradedToTac 是否已升级为枪械背包喵~
 * @param ammoInventory 弹药库存列表，存储弹药物品栈喵~
 *
 * @author liudongyu
 */
public record TravelersBackpackTacData(boolean upgradedToTac, List<ItemStack> ammoInventory) {
	/**
	 * 空的旅行者背包枪械数据，表示未升级且无弹药库存喵~
	 */
	public static final TravelersBackpackTacData EMPTY = new TravelersBackpackTacData(false, List.of());

	/**
	 * 用于持久化序列化的编解码器喵~
	 */
	public static final Codec<TravelersBackpackTacData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.BOOL.fieldOf("upgraded_to_tac").forGetter(TravelersBackpackTacData::upgradedToTac),
			ItemStack.CODEC.listOf().fieldOf("ammo_inventory").forGetter(TravelersBackpackTacData::ammoInventory)
	).apply(instance, TravelersBackpackTacData::new));

	/**
	 * 用于网络传输的流式编解码器喵~
	 */
	public static final StreamCodec<RegistryFriendlyByteBuf, TravelersBackpackTacData> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.BOOL, TravelersBackpackTacData::upgradedToTac,
			ItemStack.LIST_STREAM_CODEC, TravelersBackpackTacData::ammoInventory,
			TravelersBackpackTacData::new
	);
}
