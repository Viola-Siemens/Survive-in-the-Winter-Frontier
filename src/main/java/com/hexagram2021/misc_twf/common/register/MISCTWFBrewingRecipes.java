package com.hexagram2021.misc_twf.common.register;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.brewing.BrewingRecipe;

/**
 * 模组自定义酿造配方注册类喵~
 * 定义了将强效治疗药水与神秘血肉酿造成突变药水的配方喵~
 *
 * @author liudongyu
 */
public class MISCTWFBrewingRecipes {
	/**
	 * 初始化酿造配方喵~
	 *
	 * @param builder 酿造配方构建器喵~
	 */
	public static void init(PotionBrewing.Builder builder) {
		ItemStack strongHealing = new ItemStack(Items.POTION);
		strongHealing.set(DataComponents.POTION_CONTENTS, new PotionContents(Potions.STRONG_HEALING));
		builder.addRecipe(new MISCTWFBrewingRecipe(
				strongHealing,
				MISCTWFItems.Materials.MYSTERIOUS_FLESH,
				new ItemStack(MISCTWFItems.Materials.MUTANT_POTION)
		));
	}

	/**
	 * 自定义酿造配方实现类喵~
	 * 重写了输入物品的匹配逻辑，使用物品及其组件数据进行完全匹配喵~
	 */
	private static final class MISCTWFBrewingRecipe extends BrewingRecipe {
		/**
		 * 构造一个酿造配方喵~
		 *
		 * @param input 输入物品（基础药水）喵~
		 * @param add 添加物品（酿造材料）喵~
		 * @param output 输出物品（目标药水）喵~
		 */
		public MISCTWFBrewingRecipe(ItemStack input, ItemLike add, ItemStack output) {
			super(Ingredient.of(input), Ingredient.of(add), output);
		}

		/**
		 * 判断给定物品是否与配方的输入物品匹配喵~
		 * 使用严格匹配策略，要求物品类型和数据组件完全相同喵~
		 *
		 * @param input 待检查的物品栈喵~
		 * @return 如果匹配则返回 true 喵~
		 */
		@Override
		public boolean isInput(ItemStack input) {
			ItemStack[] itemStacks = this.getInput().getItems();
			for(ItemStack itemstack : itemStacks) {
				if (ItemStack.isSameItemSameComponents(itemstack, input)) {
					return true;
				}
			}
			return false;
		}
	}
}
