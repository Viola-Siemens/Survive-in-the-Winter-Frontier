package com.hexagram2021.misc_twf.common.register;

import com.hexagram2021.misc_twf.common.entity.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;

/**
 * 模组实体类型注册类，负责注册所有自定义的实体类型喵~
 * 包含各种僵尸化动物实体喵~
 *
 * @author liudongyu
 */
public final class MISCTWFEntities {
	private static final DeferredRegister<EntityType<?>> REGISTER = DeferredRegister.create(Registries.ENTITY_TYPE, MODID);

	private MISCTWFEntities() {
	}

	/**
	 * 初始化并注册所有实体类型到事件总线喵~
	 *
	 * @param bus NeoForge 事件总线喵~
	 */
	public static void init(IEventBus bus) {
		REGISTER.register(bus);
	}
}
