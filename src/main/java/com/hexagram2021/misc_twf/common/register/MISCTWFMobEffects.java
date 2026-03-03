package com.hexagram2021.misc_twf.common.register;

import com.hexagram2021.misc_twf.common.effect.FragileEffect;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;

/**
 * 药水效果注册类，管理模组中所有自定义药水效果的注册喵~
 * 药水效果可以施加到生物身上，产生各种增益或减益效果喵~
 *
 * @author liudongyu
 */
public final class MISCTWFMobEffects {
	private static final DeferredRegister<MobEffect> REGISTER = DeferredRegister.create(Registries.MOB_EFFECT, MODID);

	/**
	 * 脆弱效果，使受影响的生物受到的伤害增加喵~
	 * 对应本地化键：effect.misc_twf.fragile喵~
	 */
	public static final DeferredHolder<MobEffect, FragileEffect> FRAGILE = REGISTER.register("fragile", FragileEffect::new);

	private MISCTWFMobEffects() {
	}

	/**
	 * 初始化并注册所有药水效果到事件总线喵~
	 *
	 * @param bus NeoForge 事件总线喵~
	 */
	public static void init(IEventBus bus) {
		REGISTER.register(bus);
	}
}
