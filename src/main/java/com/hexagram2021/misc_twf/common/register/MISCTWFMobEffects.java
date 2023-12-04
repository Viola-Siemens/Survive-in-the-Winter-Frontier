package com.hexagram2021.misc_twf.common.register;

import com.hexagram2021.misc_twf.common.effect.FragileEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;

public final class MISCTWFMobEffects {
	private static final DeferredRegister<MobEffect> REGISTER = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, MODID);
	
	public static final RegistryObject<FragileEffect> FRAGILE = REGISTER.register("fragile", FragileEffect::new);

	private MISCTWFMobEffects() {
	}

	public static void init(IEventBus bus) {
		REGISTER.register(bus);
	}
}
