package com.hexagram2021.misc_twf.common.recipe.compat;

import com.hexagram2021.misc_twf.common.recipe.RecoveryFurnaceRecipe;
import com.hexagram2021.misc_twf.common.register.MISCTWFBlocks;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.placement.HorizontalAlignment;
import mezz.jei.api.gui.placement.VerticalAlignment;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;

/**
 * 回收熔炉配方的 JEI 展示类别,负责在 JEI 中渲染和展示回收熔炉配方喵~
 *
 * @author liudongyu
 */
public class RecoveryFurnaceRecipeCategory extends AbstractRecipeCategory<RecoveryFurnaceRecipe> {
	/** 配方类别的唯一标识符喵~ */
	public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(MODID, "recovery_furnace");
	/** 配方背景纹理的资源位置喵~ */
	public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/jei/recovery_furnace.png");

	/** 配方显示区域的宽度喵~ */
	public static final int WIDTH = 118;

	/** 默认的回收时间(用于未指定时间的配方)喵~ */
	private final int regularCookTime;

	/**
	 * 构造回收熔炉配方类别喵~
	 *
	 * @param guiHelper JEI GUI 辅助工具,用于创建可绘制对象喵~
	 */
	public RecoveryFurnaceRecipeCategory(IGuiHelper guiHelper) {
		super(
				JEIHelper.MISCTWFJEIRecipeTypes.RECOVERY_FURNACE,
				Component.translatable("block.misc_twf.recovery_furnace"),
				guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(MISCTWFBlocks.RECOVERY_FURNACE.get())),
				WIDTH, 54
		);
		this.regularCookTime = RecoveryFurnaceRecipe.Serializer.DEFAULT_RECOVERING_TIME;
	}

	/**
	 * 创建配方的额外渲染元素,包括动画箭头、燃烧火焰、经验值和烹饪时间显示喵~
	 *
	 * @param builder 配方额外元素构建器喵~
	 * @param recipe 要渲染的配方喵~
	 * @param focuses 焦点组,用于处理 JEI 的查询功能喵~
	 */
	@Override
	public void createRecipeExtras(IRecipeExtrasBuilder builder, RecoveryFurnaceRecipe recipe, IFocusGroup focuses) {
		// 获取配方的烹饪时间,未指定则使用默认值喵~
		int cookTime = recipe.recoveringTime();
		if (cookTime <= 0) {
			cookTime = regularCookTime;
		}
		// 添加配方进度箭头动画喵~
		builder.addAnimatedRecipeArrow(cookTime)
				.setPosition(26, 17);
		// 添加燃烧火焰动画喵~
		builder.addAnimatedRecipeFlame(300)
				.setPosition(1, 20);

		this.addExperience(builder, recipe);
		this.addCookTime(builder, recipe);
	}

	/**
	 * 添加经验值显示文本到配方界面喵~
	 *
	 * @param builder 配方额外元素构建器喵~
	 * @param recipe 要渲染的配方喵~
	 */
	protected void addExperience(IRecipeExtrasBuilder builder, RecoveryFurnaceRecipe recipe) {
		float experience = recipe.experience();
		if (experience > 0) {
			// 创建经验值文本并添加到界面右上角喵~
			Component experienceString = Component.translatable("gui.jei.category.smelting.experience", experience);
			builder.addText(experienceString, getWidth() - 20, 10)
					.setPosition(0, 0, getWidth(), getHeight(), HorizontalAlignment.RIGHT, VerticalAlignment.TOP)
					.setTextAlignment(HorizontalAlignment.RIGHT)
					.setColor(0xFF808080);
		}
	}

	/**
	 * 添加烹饪时间显示文本到配方界面喵~
	 *
	 * @param builder 配方额外元素构建器喵~
	 * @param recipe 要渲染的配方喵~
	 */
	protected void addCookTime(IRecipeExtrasBuilder builder, RecoveryFurnaceRecipe recipe) {
		// 获取配方的烹饪时间,未指定则使用默认值喵~
		int cookTime = recipe.recoveringTime();
		if (cookTime <= 0) {
			cookTime = regularCookTime;
		}
		if (cookTime > 0) {
			// 将游戏刻转换为秒并显示在界面右下角喵~
			int cookTimeSeconds = cookTime / 20;
			Component timeString = Component.translatable("gui.jei.category.smelting.time.seconds", cookTimeSeconds);
			builder.addText(timeString, getWidth() - 20, 10)
					.setPosition(0, 0, getWidth(), getHeight(), HorizontalAlignment.RIGHT, VerticalAlignment.BOTTOM)
					.setTextAlignment(HorizontalAlignment.RIGHT)
					.setTextAlignment(VerticalAlignment.BOTTOM)
					.setColor(0xFF808080);
		}
	}

	/**
	 * 设置配方布局,配置输入和输出槽位的位置及内容喵~
	 *
	 * @param builder 配方布局构建器喵~
	 * @param recipe 要展示的配方喵~
	 * @param focuses 焦点组,用于处理 JEI 的查询功能喵~
	 */
	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, RecoveryFurnaceRecipe recipe, IFocusGroup focuses) {
		// 添加输入槽位喵~
		builder.addSlot(RecipeIngredientRole.INPUT, 1, 1).addItemStack(recipe.ingredient().getResult());
		// 添加输出槽位,支持最多 4 个输出物品(2x2 网格)喵~
		List<ItemStack> outputs = recipe.results();
		for(int i = 0; i < outputs.size(); ++i) {
			builder.addSlot(RecipeIngredientRole.OUTPUT, 83 + 18 * (i % 2), 10 + 18 * (i / 2)).addItemStack(outputs.get(i));
		}
	}

	/**
	 * 判断配方是否应该被 JEI 处理和展示喵~
	 *
	 * @param recipe 要判断的配方喵~
	 * @return 如果配方不是特殊配方则返回 true,否则返回 false 喵~
	 */
	@Override
	public boolean isHandled(RecoveryFurnaceRecipe recipe) {
		return !recipe.isSpecial();
	}
}
