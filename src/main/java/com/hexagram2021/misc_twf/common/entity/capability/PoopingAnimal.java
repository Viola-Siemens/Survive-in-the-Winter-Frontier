package com.hexagram2021.misc_twf.common.entity.capability;

import com.hexagram2021.misc_twf.common.register.MISCTWFItems;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

public class PoopingAnimal implements IPoopingAnimal {
	private final Entity self;
	protected int remainingTicks = 0;

	public PoopingAnimal(Entity self) {
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
}
