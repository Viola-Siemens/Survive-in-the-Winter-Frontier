package com.hexagram2021.misc_twf.common.entity.capability;

import com.hexagram2021.misc_twf.common.register.MISCTWFItems;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

import java.util.Random;

public class PoopingAnimal implements IPoopingAnimal {
	private final LivingEntity self;
	protected int remainingTicks = 0;

	public PoopingAnimal(LivingEntity self) {
		this.self = self;
		this.resetPoopingTicks();
	}

	@Override
	public int getPoopingRemainingTicks() {
		return this.remainingTicks;
	}

	@Override
	public void setPoopingRemainingTicks(int ticks) {
		this.remainingTicks = ticks;
	}

	@Override
	public void poop() {
		this.self.level.addFreshEntity(new ItemEntity(
				this.self.level,
				this.self.getRandomX(0.5D), this.self.getRandomY(), this.self.getRandomZ(0.5D),
				new ItemStack(MISCTWFItems.Materials.ANIMAL_POOP)
		));
	}

	@Override
	public Random getRandom() {
		return this.self.getRandom();
	}
}
