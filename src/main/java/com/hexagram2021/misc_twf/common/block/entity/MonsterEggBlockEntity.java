package com.hexagram2021.misc_twf.common.block.entity;

import com.hexagram2021.misc_twf.common.register.MISCTWFBlockEntities;
import com.hexagram2021.misc_twf.common.util.MISCTWFLogger;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.util.random.Weight;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;

public class MonsterEggBlockEntity extends BlockEntity {
	protected WeightedRandomList<MonsterEggEntry> entries = WeightedRandomList.create();

	public MonsterEggBlockEntity(BlockPos blockPos, BlockState blockState) {
		this(MISCTWFBlockEntities.MONSTER_EGG.get(), blockPos, blockState);
	}

	public MonsterEggBlockEntity(BlockEntityType<MonsterEggBlockEntity> type, BlockPos blockPos, BlockState blockState) {
		super(type, blockPos, blockState);
	}

	@Nullable
	public Entity createMonster(Level level) {
		return this.entries.getRandom(level.random).map(entry -> entry.type().create(level)).orElse(null);
	}

	public void setEntries(WeightedRandomList<MonsterEggEntry> entries) {
		this.entries = entries;
	}

	public void fromItem(ItemStack itemStack) {
		CompoundTag nbt = itemStack.getTag();
		if(nbt == null) {
			return;
		}
		this.loadInner(nbt);
	}

	private void loadInner(CompoundTag nbt) {
		if(nbt.contains("Entries", Tag.TAG_LIST)) {
			this.setEntries(WeightedRandomList.create(
					MonsterEggEntry.CODEC.listOf().parse(NbtOps.INSTANCE, nbt.getList("Entries", Tag.TAG_COMPOUND)).getOrThrow(false, MISCTWFLogger::error)
			));
		}
	}

	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		this.loadInner(nbt);
	}

	@Override
	protected void saveAdditional(CompoundTag nbt) {
		super.saveAdditional(nbt);
		nbt.put("Entries", MonsterEggEntry.CODEC.listOf().encode(this.entries.unwrap(), NbtOps.INSTANCE, new ListTag()).getOrThrow(false, MISCTWFLogger::error));
	}

	public static class MonsterEggEntry extends WeightedEntry.IntrusiveBase {
		public static final Codec<MonsterEggEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				ForgeRegistries.ENTITIES.getCodec().fieldOf("type").forGetter(MonsterEggEntry::type),
				Weight.CODEC.fieldOf("weight").forGetter(WeightedEntry.IntrusiveBase::getWeight)
		).apply(instance, MonsterEggEntry::new));

		private final EntityType<?> type;

		public MonsterEggEntry(EntityType<?> type, Weight weight) {
			super(weight);
			this.type = type;
		}

		public EntityType<?> type() {
			return type;
		}
	}
}
