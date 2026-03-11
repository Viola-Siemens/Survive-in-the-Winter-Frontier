package com.hexagram2021.misc_twf.common.recipe.compat;

import com.hexagram2021.misc_twf.common.recipe.MoldDetacherRecipe;
import com.hexagram2021.misc_twf.common.register.MISCTWFBlocks;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;

/**
 * 模具拆卸配方的 JEI 展示类别,负责在 JEI 中渲染和展示模具拆卸配方喵~
 *
 * @author liudongyu
 */
public class MoldDetacherRecipeCategory extends AbstractRecipeCategory<MoldDetacherRecipe> {
	/** 配方类别的唯一标识符喵~ */
	public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(MODID, "mold_detach");
	/** 配方背景纹理的资源位置喵~ */
	public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/jei/mold_detacher.png");

	/** 配方显示区域的宽度喵~ */
	public static final int WIDTH = 128;
	/** 配方显示区域的高度喵~ */
	public static final int HEIGHT = 48;

	private final IDrawable background;

	/**
	 * 构造模具拆卸配方类别喵~
	 *
	 * @param guiHelper JEI GUI 辅助工具,用于创建可绘制对象喵~
	 */
	public MoldDetacherRecipeCategory(IGuiHelper guiHelper) {
		super(
				JEIHelper.MISCTWFJEIRecipeTypes.MOLD_DETACHER,
				Component.translatable("block.misc_twf.mold_detacher"),
				guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(MISCTWFBlocks.MOLD_DETACHER.get())),
				WIDTH, HEIGHT
		);
		this.background = guiHelper.createDrawable(TEXTURE, 0, 0, WIDTH, HEIGHT);
	}

	/**
	 * 绘制配方界面,包括背景和其他渲染元素喵~
	 *
	 * @param recipe 要绘制的配方喵~
	 * @param recipeSlotsView 配方槽位视图喵~
	 * @param guiGraphics 图形渲染上下文喵~
	 * @param mouseX 鼠标 X 坐标喵~
	 * @param mouseY 鼠标 Y 坐标喵~
	 */
	@Override
	public void draw(MoldDetacherRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
		super.draw(recipe, recipeSlotsView, guiGraphics, mouseX, mouseY);
		this.background.draw(guiGraphics);
	}

	/**
	 * 设置配方布局,配置输入和输出槽位的位置及内容喵~
	 *
	 * @param builder 配方布局构建器喵~
	 * @param recipe 要展示的配方喵~
	 * @param focuses 焦点组,用于处理 JEI 的查询功能喵~
	 */
	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, MoldDetacherRecipe recipe, IFocusGroup focuses) {
		// 添加输入槽位喵~
		builder.addSlot(RecipeIngredientRole.INPUT, 12, 16).addIngredients(recipe.input());
		// 添加输出槽位,支持多个输出物品喵~
		for(int i = 0; i < recipe.results().size(); ++i) {
			builder.addSlot(RecipeIngredientRole.OUTPUT, 67 + 18 * i, 16).addItemStack(recipe.results().get(i));
		}
	}

	/**
	 * 判断配方是否应该被 JEI 处理和展示喵~
	 *
	 * @param recipe 要判断的配方喵~
	 * @return 如果配方不是特殊配方则返回 true,否则返回 false 喵~
	 */
	@Override
	public boolean isHandled(MoldDetacherRecipe recipe) {
		return !recipe.isSpecial();
	}
}
