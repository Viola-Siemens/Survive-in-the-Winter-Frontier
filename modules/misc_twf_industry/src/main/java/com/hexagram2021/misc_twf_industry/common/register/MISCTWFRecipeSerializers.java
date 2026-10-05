package com.hexagram2021.misc_twf_industry.common.register;

import com.hexagram2021.misc_twf_industry.common.recipe.MoldDetacherRecipe;
import com.hexagram2021.misc_twf_industry.common.recipe.MoldWorkbenchRecipe;
import com.hexagram2021.misc_twf_industry.common.recipe.RecoveryFurnaceRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.hexagram2021.misc_twf_industry.MiscTwfIndustry.CONTENT_NAMESPACE;

/**
 * 工业生态模块配方序列化器注册类喵~
 *
 * @author liudongyu
 */
public final class MISCTWFRecipeSerializers {
	private static final DeferredRegister<RecipeSerializer<?>> REGISTER = DeferredRegister.create(Registries.RECIPE_SERIALIZER, CONTENT_NAMESPACE);

	/** 模具分离器配方序列化器喵~ */
	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<MoldDetacherRecipe>> MOLD_DETACHER = REGISTER.register("mold_detach", MoldDetacherRecipe.Serializer::new);

	/** 模具加工台配方序列化器喵~ */
	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<MoldWorkbenchRecipe>> MOLD_WORKBENCH = REGISTER.register("mold_workbench", MoldWorkbenchRecipe.Serializer::new);

	/** 回收炉配方序列化器喵~ */
	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<RecoveryFurnaceRecipe>> RECOVERY_FURNACE = REGISTER.register("recovery_furnace", RecoveryFurnaceRecipe.Serializer::new);

	/**
	 * 初始化并注册所有配方序列化器到事件总线喵~
	 *
	 * @param bus NeoForge 事件总线喵~
	 */
	public static void init(IEventBus bus) {
		REGISTER.register(bus);
	}

	private MISCTWFRecipeSerializers() {
	}
}
