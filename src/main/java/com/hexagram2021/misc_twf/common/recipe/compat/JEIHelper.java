package com.hexagram2021.misc_twf.common.recipe.compat;

import com.hexagram2021.misc_twf.client.screen.RecoveryFurnaceScreen;
import com.hexagram2021.misc_twf.common.block.entity.MoldWorkbenchBlockEntity;
import com.hexagram2021.misc_twf.common.block.entity.RecoveryFurnaceBlockEntity;
import com.hexagram2021.misc_twf.common.menu.MoldWorkbenchMenu;
import com.hexagram2021.misc_twf.common.menu.RecoveryFurnaceMenu;
import com.hexagram2021.misc_twf.common.recipe.MoldDetacherRecipe;
import com.hexagram2021.misc_twf.common.recipe.MoldWorkbenchRecipe;
import com.hexagram2021.misc_twf.common.recipe.RecoveryFurnaceRecipe;
import com.hexagram2021.misc_twf.common.recipe.cache.CachedRecipeList;
import com.hexagram2021.misc_twf.common.register.MISCTWFBlocks;
import com.hexagram2021.misc_twf.common.register.MISCTWFMenuTypes;
import com.hexagram2021.misc_twf.common.util.MISCTWFLogger;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.*;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;

/**
 * JEI(Just Enough Items) 插件入口类,负责注册配方类别、配方数据和交互处理器喵~
 *
 * @author liudongyu
 */
@JeiPlugin
public class JEIHelper implements IModPlugin {
	/**
	 * 模组配方类型定义,用于在 JEI 中注册和引用配方喵~
	 */
	public static final class MISCTWFJEIRecipeTypes {
		/** 模具分离配方类型喵~ */
		public static final RecipeType<MoldDetacherRecipe> MOLD_DETACHER = new RecipeType<>(MoldDetacherRecipeCategory.UID, MoldDetacherRecipe.class);
		/** 模具工作台配方类型喵~ */
		public static final RecipeType<MoldWorkbenchRecipe> MOLD_WORKBENCH = new RecipeType<>(MoldWorkbenchRecipeCategory.UID, MoldWorkbenchRecipe.class);
		/** 回收炉配方类型喵~ */
		public static final RecipeType<RecoveryFurnaceRecipe> RECOVERY_FURNACE = new RecipeType<>(RecoveryFurnaceRecipeCategory.UID, RecoveryFurnaceRecipe.class);

		private MISCTWFJEIRecipeTypes() {
		}
	}

	private static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(MODID, "main");

	/**
	 * 获取 JEI 插件的唯一标识符喵~
	 *
	 * @return 插件的资源位置标识符喵~
	 */
	@Override
	public ResourceLocation getPluginUid() {
		return UID;
	}

	/**
	 * 注册配方类别,将模组的配方展示分类添加到 JEI 中喵~
	 *
	 * @param registry 配方类别注册器喵~
	 */
	@Override
	public void registerCategories(IRecipeCategoryRegistration registry) {
		// 获取 GUI 辅助工具并注册所有配方类别喵~
		IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();
		registry.addRecipeCategories(
				new MoldDetacherRecipeCategory(guiHelper),
				new MoldWorkbenchRecipeCategory(guiHelper),
				new RecoveryFurnaceRecipeCategory(guiHelper)
		);
	}

	/**
	 * 注册配方数据,将所有配方添加到对应的 JEI 类别中喵~
	 *
	 * @param registration 配方注册器喵~
	 */
	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		MISCTWFLogger.info("Adding recipes to JEI!!");
		// 将各类配方从缓存列表中提取并注册到 JEI 喵~
		registration.addRecipes(MISCTWFJEIRecipeTypes.MOLD_DETACHER, getRecipes(MoldDetacherRecipe.recipeList));
		registration.addRecipes(MISCTWFJEIRecipeTypes.MOLD_WORKBENCH, getRecipes(MoldWorkbenchRecipe.recipeList));
		registration.addRecipes(MISCTWFJEIRecipeTypes.RECOVERY_FURNACE, getRecipes(RecoveryFurnaceRecipe.recipeList));
	}

	/**
	 * 从缓存的配方列表中获取当前世界的所有配方喵~
	 *
	 * @param cachedList 缓存的配方列表喵~
	 * @param <T> 配方类型喵~
	 * @return 配方列表的副本喵~
	 */
	@SuppressWarnings("SameParameterValue")
	private static <T extends Recipe<?>> List<T> getRecipes(CachedRecipeList<T> cachedList) {
		// 从当前客户端世界获取配方并返回新列表喵~
		return new ArrayList<>(cachedList.getRecipes(Objects.requireNonNull(Minecraft.getInstance().level)));
	}

	/**
	 * 注册配方转移处理器,允许玩家在 JEI 中点击配方快速转移物品喵~
	 *
	 * @param registration 配方转移注册器喵~
	 */
	@Override
	public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
		// 注册模具工作台的配方转移喵~
		registration.addRecipeTransferHandler(
				MoldWorkbenchMenu.class,
				MISCTWFMenuTypes.MOLD_WORKBENCH_MENU.get(),
				MISCTWFJEIRecipeTypes.MOLD_WORKBENCH,
				MoldWorkbenchBlockEntity.SLOT_INPUT, 1,
				MoldWorkbenchMenu.INV_SLOT_START, 36
		);
		// 注册回收熔炉的配方转移喵~
		registration.addRecipeTransferHandler(
				RecoveryFurnaceMenu.class,
				MISCTWFMenuTypes.RECOVERY_FURNACE_MENU.get(),
				MISCTWFJEIRecipeTypes.RECOVERY_FURNACE,
				RecoveryFurnaceBlockEntity.SLOT_INPUT, 1,
				RecoveryFurnaceMenu.INV_SLOT_START, 36
		);
		// 注册回收熔炉的燃料转移喵~
		registration.addRecipeTransferHandler(
				RecoveryFurnaceMenu.class,
				MISCTWFMenuTypes.RECOVERY_FURNACE_MENU.get(),
				RecipeTypes.FUELING,
				RecoveryFurnaceBlockEntity.SLOT_FUEL, 1,
				RecoveryFurnaceMenu.INV_SLOT_START, 36
		);
	}

	/**
	 * 注册配方催化剂,将方块物品与其对应的配方类型关联喵~
	 *
	 * @param registration 配方催化剂注册器喵~
	 */
	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
		// 注册各个工作站方块为其对应配方的催化剂喵~
		registration.addRecipeCatalyst(new ItemStack(MISCTWFBlocks.MOLD_WORKBENCH.get()), MISCTWFJEIRecipeTypes.MOLD_WORKBENCH);
		registration.addRecipeCatalyst(new ItemStack(MISCTWFBlocks.MOLD_DETACHER.get()), MISCTWFJEIRecipeTypes.MOLD_DETACHER);
		registration.addRecipeCatalyst(new ItemStack(MISCTWFBlocks.RECOVERY_FURNACE.get()), MISCTWFJEIRecipeTypes.RECOVERY_FURNACE);
		registration.addRecipeCatalysts(RecipeTypes.FUELING, MISCTWFBlocks.RECOVERY_FURNACE);
	}

	/**
	 * 注册 GUI 处理器,配置可点击区域以显示对应的配方喵~
	 *
	 * @param registration GUI 处理器注册器喵~
	 */
	@Override
	public void registerGuiHandlers(IGuiHandlerRegistration registration) {
		// 注册回收熔炉界面的点击区域,点击后显示相关配方喵~
		registration.addRecipeClickArea(RecoveryFurnaceScreen.class, 83, 32, 28, 23, MISCTWFJEIRecipeTypes.RECOVERY_FURNACE, RecipeTypes.FUELING);
	}
}
