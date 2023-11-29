package com.hexagram2021.misc_twf.common.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

import javax.annotation.Nullable;

public class ZombieSheepEntity extends ZombieAnimalEntity<Sheep> {
	private static final EntityDataAccessor<Byte> DATA_WOOL_ID = SynchedEntityData.defineId(ZombieSheepEntity.class, EntityDataSerializers.BYTE);

	public ZombieSheepEntity(EntityType<? extends Zombie> entityType, Level level) {
		super(entityType, EntityType.SHEEP, level);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(DATA_WOOL_ID, (byte)0);
	}

	public DyeColor getColor() {
		return DyeColor.byId(this.entityData.get(DATA_WOOL_ID) & 0xf);
	}

	public void setColor(DyeColor dyeColor) {
		byte b0 = this.entityData.get(DATA_WOOL_ID);
		this.entityData.set(DATA_WOOL_ID, (byte)(b0 & 0xf0 | dyeColor.getId() & 0xf));
	}

	@Override
	protected void extraConversion(Sheep animal) {
		animal.setColor(this.getColor());
		animal.setSheared(true);
	}

	@Nullable
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficultyInstance, MobSpawnType mobSpawnType,
										@Nullable SpawnGroupData spawnGroupData, @Nullable CompoundTag tag) {
		this.setColor(Sheep.getRandomSheepColor(level.getRandom()));
		return super.finalizeSpawn(level, difficultyInstance, mobSpawnType, spawnGroupData, tag);
	}
}
