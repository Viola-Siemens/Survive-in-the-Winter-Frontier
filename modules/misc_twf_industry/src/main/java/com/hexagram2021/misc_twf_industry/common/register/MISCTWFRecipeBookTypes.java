package com.hexagram2021.misc_twf_industry.common.register;

import com.atsuishio.superbwarfare.data.gun.Ammo;
import com.atsuishio.superbwarfare.init.ModItems;
import com.atsuishio.superbwarfare.item.common.ammo.AmmoBoxItem;
import com.atsuishio.superbwarfare.item.common.ammo.AmmoSupplierItem;
import com.atsuishio.superbwarfare.item.gun.GunItem;
import com.hexagram2021.misc_twf_industry.common.recipe.RecoveryFurnaceRecipe;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;
import net.neoforged.neoforge.client.event.RegisterRecipeBookCategoriesEvent;

import java.util.List;
import java.util.function.Supplier;

/**
 * 工业生态模块回收炉配方书分类喵~
 *
 * <p>已由 TaC 迁移到 Superb Warfare：枪械判定 {@link GunItem}，弹药判定 {@link AmmoSupplierItem} 与
 * {@link AmmoBoxItem}，并改为「枪械 / 弹药 / 杂项」三类（SW 无独立配件物品）喵~</p>
 *
 * <p>枚举扩展要点（NeoForge 规范）：`enumextensions.json` 中 `enum` 必须为斜杠二进制名、
 * 扩展常量名必须以 modid 小写前缀开头（故为 `MISC_TWF_INDUSTRY_*`）；`RecipeBookType` 为 0 参构造，
 * 无法通过构造参数回填 {@link EnumProxy}，因此改用 {@link RecipeBookType#valueOf(String)} 获取喵~</p>
 *
 * @author liudongyu
 */
public final class MISCTWFRecipeBookTypes {
	/** 回收炉配方书类型对应的枚举扩展常量名喵~ */
	private static final String RECOVER_FURNACE_TYPE_NAME = "MISC_TWF_INDUSTRY_RECOVER_FURNACE";

	/** 搜索（聚合）分类喵~ */
	public static final EnumProxy<RecipeBookCategories> MISC_TWF_RECOVER_FURNACE_SEARCH = new EnumProxy<>(
			RecipeBookCategories.class, (Supplier<List<ItemStack>>) () -> List.of(new ItemStack(Items.COMPASS))
	);
	/** 枪械分类喵~ */
	public static final EnumProxy<RecipeBookCategories> MISC_TWF_RECOVER_FURNACE_GUNS = new EnumProxy<>(
			RecipeBookCategories.class, (Supplier<List<ItemStack>>) () -> List.of(new ItemStack(ModItems.AK_47.get()))
	);
	/** 弹药分类喵~ */
	public static final EnumProxy<RecipeBookCategories> MISC_TWF_RECOVER_FURNACE_BULLETS = new EnumProxy<>(
			RecipeBookCategories.class, (Supplier<List<ItemStack>>) () -> List.of(Ammo.HANDGUN.getItemStack())
	);
	/** 杂项分类喵~ */
	public static final EnumProxy<RecipeBookCategories> MISC_TWF_RECOVER_FURNACE_MISC = new EnumProxy<>(
			RecipeBookCategories.class, (Supplier<List<ItemStack>>) () -> List.of(new ItemStack(Items.IRON_INGOT))
	);

	/**
	 * 获取回收炉的配方书类型（枚举扩展常量）喵~
	 *
	 * @return 回收炉配方书类型喵~
	 */
	public static RecipeBookType getRecoverFurnaceType() {
		return RecipeBookType.valueOf(RECOVER_FURNACE_TYPE_NAME);
	}

	/**
	 * 注册回收炉的配方书分类与分类查找器喵~
	 *
	 * @param event 配方书分类注册事件喵~
	 */
	public static void init(RegisterRecipeBookCategoriesEvent event) {
		event.registerBookCategories(getRecoverFurnaceType(), List.of(
				MISC_TWF_RECOVER_FURNACE_SEARCH.getValue(),
				MISC_TWF_RECOVER_FURNACE_GUNS.getValue(),
				MISC_TWF_RECOVER_FURNACE_BULLETS.getValue(),
				MISC_TWF_RECOVER_FURNACE_MISC.getValue()
		));
		event.registerAggregateCategory(MISC_TWF_RECOVER_FURNACE_SEARCH.getValue(), List.of(
				MISC_TWF_RECOVER_FURNACE_GUNS.getValue(),
				MISC_TWF_RECOVER_FURNACE_BULLETS.getValue(),
				MISC_TWF_RECOVER_FURNACE_MISC.getValue()
		));
		event.registerRecipeCategoryFinder(MISCTWFRecipeTypes.RECOVERY_FURNACE.get(), recipeHolder -> {
			ItemStack itemStack = recipeHolder.value() instanceof RecoveryFurnaceRecipe recoveryFurnaceRecipe
					? recoveryFurnaceRecipe.ingredient()
					: ItemStack.EMPTY;
			if(itemStack.getItem() instanceof GunItem) {
				return MISC_TWF_RECOVER_FURNACE_GUNS.getValue();
			}
			if(itemStack.getItem() instanceof AmmoSupplierItem || itemStack.getItem() instanceof AmmoBoxItem) {
				return MISC_TWF_RECOVER_FURNACE_BULLETS.getValue();
			}
			return MISC_TWF_RECOVER_FURNACE_MISC.getValue();
		});
	}

	private MISCTWFRecipeBookTypes() {
	}
}
