package com.hexagram2021.misc_twf.common.data_component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * 动物尸体数据组件喵~
 *
 * @param loots 动物尸体物品列表
 * @param age 动物尸体的存活时间
 */
public record DeadAnimalData(List<ItemStack> loots, int age) {
	/**
	 * 动物尸体数据组件编解码器喵~
	 */
	public static final Codec<DeadAnimalData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ItemStack.CODEC.listOf().fieldOf("loots").forGetter(DeadAnimalData::loots),
			Codec.INT.fieldOf("age").forGetter(DeadAnimalData::age)
	).apply(instance, DeadAnimalData::new));

	/**
	 * 动物尸体数据组件流编解码器喵~
	 */
	public static final StreamCodec<RegistryFriendlyByteBuf, DeadAnimalData> STREAM_CODEC = StreamCodec.composite(
			ItemStack.LIST_STREAM_CODEC, DeadAnimalData::loots,
			ByteBufCodecs.INT, DeadAnimalData::age,
			DeadAnimalData::new
	);
}
