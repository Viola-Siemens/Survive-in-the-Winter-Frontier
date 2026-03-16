package com.hexagram2021.misc_twf.common.recipe.compat;

import com.hexagram2021.misc_twf.common.recipe.MoldWorkbenchRecipe;
import com.hexagram2021.misc_twf.common.register.MISCTWFBlocks;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import mezz.jei.library.util.RecipeUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;

/**
 * 模具工作台配方的 JEI 展示类别,负责在 JEI 中渲染和展示模具工作台配方喵~
 *
 * @author liudongyu
 */
public class MoldWorkbenchRecipeCategory extends AbstractRecipeCategory<MoldWorkbenchRecipe> {
	/** 配方类别的唯一标识符喵~ */
	public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(MODID, "mold_workbench");

	/**
	 * 构造模具工作台配方类别喵~
	 *
	 * @param guiHelper JEI GUI 辅助工具,用于创建可绘制对象喵~
	 */
	public MoldWorkbenchRecipeCategory(IGuiHelper guiHelper) {
		super(
				JEIHelper.MISCTWFJEIRecipeTypes.MOLD_WORKBENCH,
				Component.translatable("block.misc_twf.mold_workbench"),
				guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(MISCTWFBlocks.MOLD_WORKBENCH.get())),
				82, 34
		);
	}

	/**
	 * 设置配方布局,配置输入和输出槽位的位置及内容喵~
	 *
	 * @param builder 配方布局构建器喵~
	 * @param recipe 要展示的配方喵~
	 * @param focuses 焦点组,用于处理 JEI 的查询功能喵~
	 */
	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, MoldWorkbenchRecipe recipe, IFocusGroup focuses) {
		// 添加输入槽位喵~
		builder.addSlot(RecipeIngredientRole.INPUT, 1, 9).addIngredients(recipe.input());
		// 添加输出槽位喵~
		builder.addSlot(RecipeIngredientRole.OUTPUT, 61, 9).addItemStack(RecipeUtil.getResultItem(recipe));
	}

	/**
	 * 判断配方是否应该被 JEI 处理和展示喵~
	 *
	 * @param recipe 要判断的配方喵~
	 * @return 如果配方不是特殊配方则返回 true,否则返回 false 喵~
	 */
	@Override
	public boolean isHandled(MoldWorkbenchRecipe recipe) {
		return !recipe.isSpecial();
	}
}
