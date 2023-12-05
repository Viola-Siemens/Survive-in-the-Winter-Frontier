package com.hexagram2021.misc_twf.common.entity.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.IntTag;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AnimalPoopingHandler implements ICapabilityProvider, INBTSerializable<IntTag> {
	private final PoopingAnimal poopingAnimal;
	private final LazyOptional<IPoopingAnimal> holder;

	public AnimalPoopingHandler(Entity entity) {
		this.poopingAnimal = new PoopingAnimal(entity);
		this.holder = LazyOptional.of(() -> this.poopingAnimal);
	}

	@Override @NotNull
	public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
		return CapabilityAnimal.POOPING.orEmpty(cap, this.holder);
	}

	@Override
	public IntTag serializeNBT() {
		return IntTag.valueOf(this.poopingAnimal.getPoopingRemainingTicks());
	}

	@Override
	public void deserializeNBT(IntTag nbt) {
		this.poopingAnimal.setPoopingRemainingTicks(nbt.getAsInt());
	}
}
