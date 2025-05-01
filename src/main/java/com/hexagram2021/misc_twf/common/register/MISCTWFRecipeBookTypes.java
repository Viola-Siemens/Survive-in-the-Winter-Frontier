package com.hexagram2021.misc_twf.common.register;

import com.tacz.guns.init.ModItems;
import net.minecraft.Util;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.client.RecipeBookRegistry;

import java.util.List;

public final class MISCTWFRecipeBookTypes {
	public static final RecipeBookType RECOVER_FURNACE = RecipeBookType.create("MISC_TWF$RECOVER_FURNACE");

	public static final RecipeBookCategories RECOVER_FURNACE_SEARCH = RecipeBookCategories.create("MISC_TWF$RECOVER_FURNACE_SEARCH", new ItemStack(Items.COMPASS));
	public static final RecipeBookCategories RECOVER_FURNACE_GUNS = RecipeBookCategories.create(
			"MISC_TWF$RECOVER_FURNACE_GUNS",
			new ItemStack(ModItems.MODERN_KINETIC_GUN.get(), 1, Util.make(new CompoundTag(), nbt -> nbt.putString("GunId", "tacz:glock_17")))
	);
	public static final RecipeBookCategories RECOVER_FURNACE_BULLETS = RecipeBookCategories.create(
			"MISC_TWF$RECOVER_FURNACE_BULLETS",
			new ItemStack(ModItems.AMMO.get(), 1, Util.make(new CompoundTag(), nbt -> nbt.putString("AmmoId", "tacz:9mm")))
	);
	public static final RecipeBookCategories RECOVER_FURNACE_MISC = RecipeBookCategories.create("MISC_TWF$RECOVER_FURNACE_MISC", new ItemStack(ModItems.STATUE.get()));

	private MISCTWFRecipeBookTypes() {
	}

	public static void init() {
		RecipeBookRegistry.addCategoriesToType(RECOVER_FURNACE, List.of(RECOVER_FURNACE_SEARCH, RECOVER_FURNACE_GUNS, RECOVER_FURNACE_BULLETS));
		RecipeBookRegistry.addAggregateCategories(RECOVER_FURNACE_SEARCH, List.of(RECOVER_FURNACE_GUNS, RECOVER_FURNACE_BULLETS));
		RecipeBookRegistry.addCategoriesFinder(MISCTWFRecipeTypes.RECOVERY_FURNACE.get(), recipe -> {
			if(recipe.getGroup().equals("guns")) {
				return RECOVER_FURNACE_GUNS;
			}
			if(recipe.getGroup().equals("bullets")) {
				return RECOVER_FURNACE_BULLETS;
			}
			return RECOVER_FURNACE_MISC;
		});
	}
}
