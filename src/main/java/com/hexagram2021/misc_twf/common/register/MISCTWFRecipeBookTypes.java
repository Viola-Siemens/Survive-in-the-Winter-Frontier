package com.hexagram2021.misc_twf.common.register;

import com.hexagram2021.misc_twf.common.recipe.RecoveryFurnaceRecipe;
import com.tacz.guns.api.item.builder.AmmoItemBuilder;
import com.tacz.guns.init.ModItems;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;

import java.util.List;

/**
 * 模组配方书类型注册类喵~
 * 注册回收炉的配方书类型和分类，与 TaC（永恒枪械工坊）模组联动喵~
 * 将回收配方按枪械、弹药、配件和杂项进行分类显示喵~
 *
 * @author liudongyu
 */
public final class MISCTWFRecipeBookTypes {
	public static final EnumProxy<RecipeBookType> MISC_TWF_RECOVER_FURNACE = new EnumProxy<>(RecipeBookType.class);

	private static final ResourceLocation LOGO_BULLETS = ResourceLocation.fromNamespaceAndPath("tacz", "9mm");

	public static final EnumProxy<RecipeBookCategories> MISC_TWF_RECOVER_FURNACE_SEARCH = new EnumProxy<>(
			RecipeBookCategories.class, new ItemStack(Items.COMPASS)
	);
	public static final EnumProxy<RecipeBookCategories> MISC_TWF_RECOVER_FURNACE_GUNS = new EnumProxy<>(
			RecipeBookCategories.class, new ItemStack(Items.BOW)
	);
	public static final EnumProxy<RecipeBookCategories> MISC_TWF_RECOVER_FURNACE_BULLETS = new EnumProxy<>(
			RecipeBookCategories.class, AmmoItemBuilder.create().setCount(1).setId(LOGO_BULLETS).build()
	);
	public static final EnumProxy<RecipeBookCategories> MISC_TWF_RECOVER_FURNACE_ATTACHMENTS = new EnumProxy<>(
			RecipeBookCategories.class, new ItemStack(Items.IRON_INGOT)
	);
	public static final EnumProxy<RecipeBookCategories> MISC_TWF_RECOVER_FURNACE_MISC = new EnumProxy<>(new ItemStack(ModItems.STATUE.get()));

	private MISCTWFRecipeBookTypes() {
	}

	public static void init() {
		RecipeBookRegistry.addCategoriesToType(RECOVER_FURNACE, List.of(MISC_TWF_RECOVER_FURNACE_SEARCH, MISC_TWF_RECOVER_FURNACE_GUNS, MISC_TWF_RECOVER_FURNACE_BULLETS, MISC_TWF_RECOVER_FURNACE_ATTACHMENTS, MISC_TWF_RECOVER_FURNACE_MISC));
		RecipeBookRegistry.addAggregateCategories(MISC_TWF_RECOVER_FURNACE_SEARCH, List.of(MISC_TWF_RECOVER_FURNACE_GUNS, MISC_TWF_RECOVER_FURNACE_BULLETS, MISC_TWF_RECOVER_FURNACE_ATTACHMENTS, MISC_TWF_RECOVER_FURNACE_MISC));
		RecipeBookRegistry.addCategoriesFinder(MISCTWFRecipeTypes.RECOVERY_FURNACE.get(), recipe -> {
			ItemStack itemStack = recipe instanceof RecoveryFurnaceRecipe recoveryFurnaceRecipe ? recoveryFurnaceRecipe.ingredient().getResult() : ItemStack.EMPTY;
			if(itemStack.is(ModItems.MODERN_KINETIC_GUN.get())) {
				return MISC_TWF_RECOVER_FURNACE_GUNS;
			}
			if(itemStack.is(ModItems.AMMO.get())) {
				return MISC_TWF_RECOVER_FURNACE_BULLETS;
			}
			if(itemStack.is(ModItems.ATTACHMENT.get())) {
				return MISC_TWF_RECOVER_FURNACE_ATTACHMENTS;
			}
			return MISC_TWF_RECOVER_FURNACE_MISC;
		});
	}
}
