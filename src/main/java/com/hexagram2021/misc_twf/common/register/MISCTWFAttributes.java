package com.hexagram2021.misc_twf.common.register;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;

/**
 * 模组实体属性注册类，负责注册所有自定义的实体属性喵~
 *
 * @author liudongyu
 */
public final class MISCTWFAttributes {
	private static final DeferredRegister<Attribute> REGISTER = DeferredRegister.create(Registries.ATTRIBUTE, MODID);

	/**
	 * 枪械精通属性，影响玩家使用枪械的熟练度和伤害加成喵~
	 * 默认值为 0.0，取值范围为 -100.0 到 100.0 喵~
	 */
	public static final DeferredHolder<Attribute, RangedAttribute> GUN_MASTERY = REGISTER.register("gun_mastery", () -> new RangedAttribute(
			"attribute.name.misc_twf.gun_mastery", 0.0D, -100.0D, 100.0D
	));

	private MISCTWFAttributes() {
	}

	/**
	 * 初始化并注册所有实体属性到事件总线喵~
	 *
	 * @param bus NeoForge 事件总线喵~
	 */
	public static void init(IEventBus bus) {
		REGISTER.register(bus);
	}
}
