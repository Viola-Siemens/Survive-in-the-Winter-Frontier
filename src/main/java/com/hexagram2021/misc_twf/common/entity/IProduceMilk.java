package com.hexagram2021.misc_twf.common.entity;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.goat.Goat;

public interface IProduceMilk {
	EntityDataAccessor<Integer> DATA_COW_MILK_COOL_DOWN = SynchedEntityData.defineId(Cow.class, EntityDataSerializers.INT);
	EntityDataAccessor<Integer> DATA_GOAT_MILK_COOL_DOWN = SynchedEntityData.defineId(Goat.class, EntityDataSerializers.INT);

	int getMilkCoolDown();
	void setMilkCoolDown(int delay);

	default boolean isAvailableToProduceMilk() {
		return this.getMilkCoolDown() <= 0;
	}
}
