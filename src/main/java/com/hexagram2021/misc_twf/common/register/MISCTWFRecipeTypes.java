package com.hexagram2021.misc_twf.common.register;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;

/**
 * 配方类型注册类，管理模组中所有自定义配方类型的注册喵~
 * 配方类型定义了不同合成系统的分类和识别方式喵~
 *
 * @author liudongyu
 */
@SuppressWarnings("SameParameterValue")
public final class MISCTWFRecipeTypes {
	private static final DeferredRegister<RecipeType<?>> REGISTER = DeferredRegister.create(Registries.RECIPE_TYPE, MODID);

	private MISCTWFRecipeTypes() {
	}

	/**
	 * 初始化并注册所有配方类型到事件总线喵~
	 *
	 * @param bus NeoForge 事件总线喵~
	 */
	public static void init(IEventBus bus) {
		REGISTER.register(bus);
	}

	/**
	 * 注册一个配方类型喵~
	 *
	 * @param name 配方类型名称喵~
	 * @param <T> 配方类型喵~
	 * @return 配方类型注册持有器喵~
	 */
	private static <T extends Recipe<?>> DeferredHolder<RecipeType<?>, RecipeType<T>> register(String name) {
		return REGISTER.register(name, () -> new RecipeType<>() {
			@Override
			public String toString() {
				return ResourceLocation.fromNamespaceAndPath(MODID, name).toString();
			}
		});
	}
}
