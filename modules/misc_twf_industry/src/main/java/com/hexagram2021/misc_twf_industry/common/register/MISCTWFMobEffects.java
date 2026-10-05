package com.hexagram2021.misc_twf_industry.common.register;

import com.hexagram2021.misc_twf_industry.common.effect.FragileEffect;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.hexagram2021.misc_twf_industry.MiscTwfIndustry.CONTENT_NAMESPACE;

/**
 * 工业生态模块药水效果注册类喵~
 *
 * <p>脆弱效果由紫外线灯施加（决策：随触发方归本模块）喵~</p>
 *
 * @author liudongyu
 */
public final class MISCTWFMobEffects {
	private static final DeferredRegister<MobEffect> REGISTER = DeferredRegister.create(Registries.MOB_EFFECT, CONTENT_NAMESPACE);

	/** 脆弱效果，使受影响的生物受到的伤害增加喵~ */
	public static final DeferredHolder<MobEffect, FragileEffect> FRAGILE = REGISTER.register("fragile", FragileEffect::new);

	/**
	 * 初始化并注册所有药水效果到事件总线喵~
	 *
	 * @param bus NeoForge 事件总线喵~
	 */
	public static void init(IEventBus bus) {
		REGISTER.register(bus);
	}

	private MISCTWFMobEffects() {
	}
}
