package com.hexagram2021.misc_twf.client;

import net.minecraft.world.item.ItemStack;

/**
 * 自定义配方图标接口，用于在配方书 UI 中为配方显示自定义图标喵~
 * <p>
 * 默认情况下，配方书会显示配方的第一个输出物品作为图标喵~
 * 实现此接口可以覆盖该行为，指定任意物品作为配方的显示图标喵~
 * <p>
 * 该接口由客户端的 {@link com.hexagram2021.misc_twf.mixin.vanilla.RecipeButtonMixin RecipeButtonMixin}
 * 使用，拦截 {@code Recipe#getResultItem} 调用并替换为自定义图标喵~
 * <p>
 * 典型使用场景：
 * <ul>
 *     <li>多输出配方：需要突出显示特定产物而非默认的第一个产物喵~</li>
 *     <li>分解配方：显示输入物品而非输出物品，更符合玩家直觉喵~</li>
 *     <li>特殊配方：需要特殊标识的配方类型喵~</li>
 * </ul>
 *
 * @see com.hexagram2021.misc_twf.common.recipe.RecoveryFurnaceRecipe RecoveryFurnaceRecipe
 * @see com.hexagram2021.misc_twf.mixin.vanilla.RecipeButtonMixin RecipeButtonMixin
 *
 * @author liudongyu
 */
public interface IHasCustomIconRecipe {
	/**
	 * 获取配方在配方书中的自定义显示图标喵~
	 * <p>
	 * 该方法在渲染配方按钮、生成提示文本和更新屏幕阅读器信息时被调用喵~
	 * 返回的物品堆栈将替代配方的默认结果物品在 UI 中显示喵~
	 *
	 * @param ingredient 配方的默认结果物品（通过 {@code Recipe#getResultItem} 获取）喵~
	 * @return 用作配方图标的物品堆栈喵~
	 */
	ItemStack misc_twf$recipeIcon(ItemStack ingredient);
}
