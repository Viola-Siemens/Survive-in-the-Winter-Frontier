package com.hexagram2021.misc_twf_industry.common.register;

import com.hexagram2021.misc_twf_industry.common.recipe.MoldDetacherRecipe;
import com.hexagram2021.misc_twf_industry.common.recipe.MoldWorkbenchRecipe;
import com.hexagram2021.misc_twf_industry.common.recipe.RecoveryFurnaceRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.hexagram2021.misc_twf_industry.MiscTwfIndustry.CONTENT_NAMESPACE;

/**
 * 工业生态模块配方类型注册类喵~
 *
 * @author liudongyu
 */
@SuppressWarnings("SameParameterValue")
public final class MISCTWFRecipeTypes {
	private static final DeferredRegister<RecipeType<?>> REGISTER = DeferredRegister.create(Registries.RECIPE_TYPE, CONTENT_NAMESPACE);

	/** 模具分离器配方类型喵~ */
	public static final DeferredHolder<RecipeType<?>, RecipeType<MoldDetacherRecipe>> MOLD_DETACHER = register("mold_detach");

	/** 模具加工台配方类型喵~ */
	public static final DeferredHolder<RecipeType<?>, RecipeType<MoldWorkbenchRecipe>> MOLD_WORKBENCH = register("mold_workbench");

	/** 回收炉配方类型喵~ */
	public static final DeferredHolder<RecipeType<?>, RecipeType<RecoveryFurnaceRecipe>> RECOVERY_FURNACE = register("recovery_furnace");

	/**
	 * 初始化并注册所有配方类型到事件总线喵~
	 *
	 * @param bus NeoForge 事件总线喵~
	 */
	public static void init(IEventBus bus) {
		REGISTER.register(bus);
	}

	private static <T extends Recipe<?>> DeferredHolder<RecipeType<?>, RecipeType<T>> register(String name) {
		return REGISTER.register(name, () -> new RecipeType<>() {
			@Override
			public String toString() {
				return ResourceLocation.fromNamespaceAndPath(CONTENT_NAMESPACE, name).toString();
			}
		});
	}

	private MISCTWFRecipeTypes() {
	}
}
