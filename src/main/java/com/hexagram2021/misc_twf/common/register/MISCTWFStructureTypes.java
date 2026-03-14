package com.hexagram2021.misc_twf.common.register;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;

/**
 * 模组结构类型注册类，负责注册所有自定义结构类型喵~
 *
 * @author liudongyu
 */
public final class MISCTWFStructureTypes {
	private static final DeferredRegister<StructureType<?>> REGISTER = DeferredRegister.create(Registries.STRUCTURE_TYPE, MODID);

	private MISCTWFStructureTypes() {
	}

	/**
	 * 初始化结构类型注册，将延迟注册器绑定到模组事件总线喵~
	 *
	 * @param bus 模组事件总线喵~
	 */
	public static void init(IEventBus bus) {
		REGISTER.register(bus);
	}
}
