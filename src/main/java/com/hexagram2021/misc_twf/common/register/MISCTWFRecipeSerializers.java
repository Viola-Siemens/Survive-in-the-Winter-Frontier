package com.hexagram2021.misc_twf.common.register;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;

/**
 * 配方序列化器注册类，管理模组中所有自定义配方序列化器的注册喵~
 * 配方序列化器负责配方数据的序列化和反序列化，用于在数据包中读取和保存配方信息喵~
 *
 * @author liudongyu
 */
public final class MISCTWFRecipeSerializers {
	private static final DeferredRegister<RecipeSerializer<?>> REGISTER = DeferredRegister.create(Registries.RECIPE_SERIALIZER, MODID);

	private MISCTWFRecipeSerializers() {
	}

	/**
	 * 初始化并注册所有配方序列化器到事件总线喵~
	 *
	 * @param bus NeoForge 事件总线喵~
	 */
	public static void init(IEventBus bus) {
		REGISTER.register(bus);
	}
}
