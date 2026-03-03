package com.hexagram2021.misc_twf.common.register;

import com.hexagram2021.misc_twf.common.recipe.BackpackTacUpgradeRecipe;
import com.hexagram2021.misc_twf.common.recipe.MoldDetacherRecipe;
import com.hexagram2021.misc_twf.common.recipe.MoldWorkbenchRecipe;
import com.hexagram2021.misc_twf.common.recipe.RecoveryFurnaceRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
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

	/**
	 * 旅行背包TAC升级配方序列化器，用于将普通旅行背包升级为带弹药槽的版本喵~
	 */
	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<BackpackTacUpgradeRecipe>> BACKPACK_TAC_UPGRADE = REGISTER.register("backpack_tac_upgrade", BackpackTacUpgradeRecipe.Serializer::new);

	/**
	 * 模具分离器配方序列化器，用于处理从模具中分离子弹的配方数据喵~
	 */
	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<MoldDetacherRecipe>> MOLD_DETACHER = REGISTER.register("mold_detach", MoldDetacherRecipe.Serializer::new);

	/**
	 * 模具加工台配方序列化器，用于处理制作子弹模具的配方数据喵~
	 */
	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<MoldWorkbenchRecipe>> MOLD_WORKBENCH = REGISTER.register("mold_workbench", MoldWorkbenchRecipe.Serializer::new);

	/**
	 * 回收炉配方序列化器，用于处理回收和熔炼物品的配方数据喵~
	 */
	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<RecoveryFurnaceRecipe>> RECOVERY_FURNACE = REGISTER.register("recovery_furnace", RecoveryFurnaceRecipe.Serializer::new);

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
