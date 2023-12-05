package com.hexagram2021.misc_twf.common.entity.capability;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

import java.util.function.Consumer;

public final class CapabilityAnimal {
	public static final Capability<IPoopingAnimal> POOPING = CapabilityManager.get(new CapabilityToken<>(){});;

	public static void register(Consumer<Class<?>> registry) {
		registry.accept(IPoopingAnimal.class);
	}

	private CapabilityAnimal() {
	}
}
