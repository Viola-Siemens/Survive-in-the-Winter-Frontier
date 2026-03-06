package com.hexagram2021.misc_twf.common.data_component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.random.Weight;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.world.entity.EntityType;

import java.util.List;

/**
 * 怪物蛋条目列表喵~
 * <br/>
 * 作为数据组件供 ItemStack 使用喵~
 * @param entries
 * @author liudongyu
 */
public record MonsterEggEntries(List<MonsterEggEntry> entries) {
	public static final Codec<MonsterEggEntries> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			MonsterEggEntry.CODEC.listOf().fieldOf("entries").forGetter(MonsterEggEntries::entries)
	).apply(instance, MonsterEggEntries::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, MonsterEggEntries> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.<RegistryFriendlyByteBuf, MonsterEggEntry>list().apply(MonsterEggEntry.STREAM_CODEC), MonsterEggEntries::entries,
			MonsterEggEntries::new
	);

	/**
	 * 怪物生成条目，包含实体类型和生成权重喵~
	 * 用于配置怪物蛋可以生成的怪物种类及其出现概率喵~
	 *
	 * @author liudongyu
	 */
	public static class MonsterEggEntry extends WeightedEntry.IntrusiveBase {
		public static final Codec<MonsterEggEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("type").forGetter(MonsterEggEntry::type),
				Weight.CODEC.fieldOf("weight").forGetter(IntrusiveBase::getWeight)
		).apply(instance, MonsterEggEntry::new));
		public static final StreamCodec<RegistryFriendlyByteBuf, MonsterEggEntry> STREAM_CODEC = StreamCodec.composite(
				ByteBufCodecs.registry(Registries.ENTITY_TYPE), MonsterEggEntry::type,
				ByteBufCodecs.fromCodec(Weight.CODEC), IntrusiveBase::getWeight,
				MonsterEggEntry::new
		);

		private final EntityType<?> type;

		/**
		 * 构造怪物生成条目喵~
		 *
		 * @param type 实体类型喵~
		 * @param weight 生成权重喵~
		 */
		public MonsterEggEntry(EntityType<?> type, Weight weight) {
			super(weight);
			this.type = type;
		}

		/**
		 * 获取此条目对应的实体类型喵~
		 *
		 * @return 实体类型喵~
		 */
		public EntityType<?> type() {
			return type;
		}
	}
}
