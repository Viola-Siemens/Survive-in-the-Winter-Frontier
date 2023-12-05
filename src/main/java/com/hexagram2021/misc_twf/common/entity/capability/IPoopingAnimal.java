package com.hexagram2021.misc_twf.common.entity.capability;

import com.hexagram2021.misc_twf.common.config.MISCTWFCommonConfig;

public interface IPoopingAnimal {
	int getPoopingRemainingTicks();
	void setPoopingRemainingTicks(int ticks);

	void poop();

	default void tick() {
		int ticks = this.getPoopingRemainingTicks() - 1;
		if(ticks > 0) {
			this.setPoopingRemainingTicks(ticks);
			return;
		}
		this.poop();
		this.resetPoopingTicks();
	}

	default void resetPoopingTicks() {
		this.setPoopingRemainingTicks((MISCTWFCommonConfig.ANIMAL_POOPING_INTERVAL.get() + MISCTWFCommonConfig.ANIMAL_POOPING_INTERVAL_NOISE.get()) * 20);
	}
}
