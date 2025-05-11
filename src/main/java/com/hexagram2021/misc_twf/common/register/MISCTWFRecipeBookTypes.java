package com.hexagram2021.misc_twf.common.register;

import com.google.common.collect.ImmutableList;
import com.hexagram2021.misc_twf.client.IHasItemIcons;
import com.hexagram2021.misc_twf.common.recipe.RecoveryFurnaceRecipe;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.attachment.AttachmentType;
import com.tacz.guns.api.item.builder.AmmoItemBuilder;
import com.tacz.guns.api.item.builder.AttachmentItemBuilder;
import com.tacz.guns.api.item.builder.GunItemBuilder;
import com.tacz.guns.init.ModItems;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.client.RecipeBookRegistry;

import java.util.EnumMap;
import java.util.List;
import java.util.function.Supplier;

public final class MISCTWFRecipeBookTypes {
	public static final RecipeBookType RECOVER_FURNACE = RecipeBookType.create("MISC_TWF$RECOVER_FURNACE");

	private static final ResourceLocation LOGO_GUNS = new ResourceLocation("tacz", "glock_17");
	private static final ResourceLocation LOGO_BULLETS = new ResourceLocation("tacz", "9mm");
	private static final ResourceLocation LOGO_ATTACHMENTS = new ResourceLocation("tacz", "sight_sro_dot");

	private static final ItemStack DEFAULT_ITEM_ICON_GUNS = new ItemStack(Items.BOW);
	private static final ItemStack DEFAULT_ITEM_ICON_ATTACHMENTS = new ItemStack(Items.IRON_INGOT);

	public static final RecipeBookCategories RECOVER_FURNACE_SEARCH = RecipeBookCategories.create("MISC_TWF$RECOVER_FURNACE_SEARCH", new ItemStack(Items.COMPASS));
	public static final RecipeBookCategories RECOVER_FURNACE_GUNS = create(
			"MISC_TWF$RECOVER_FURNACE_GUNS",
			DEFAULT_ITEM_ICON_GUNS,
			() -> ImmutableList.of(TimelessAPI.getCommonGunIndex(LOGO_GUNS).map(
					gunIndex -> GunItemBuilder.create()
							.setCount(1)
							.setId(LOGO_GUNS)
							.setAmmoCount(0)
							.setAmmoInBarrel(false)
							.putAllAttachment(new EnumMap<>(AttachmentType.class))
							.setFireMode(gunIndex.getGunData().getFireModeSet().get(0)).build()
			).orElse(DEFAULT_ITEM_ICON_GUNS))
	);
	public static final RecipeBookCategories RECOVER_FURNACE_BULLETS = RecipeBookCategories.create(
			"MISC_TWF$RECOVER_FURNACE_BULLETS",
			AmmoItemBuilder.create().setCount(1).setId(LOGO_BULLETS).build()
	);
	public static final RecipeBookCategories RECOVER_FURNACE_ATTACHMENTS = create(
			"MISC_TWF$RECOVER_FURNACE_ATTACHMENTS",
			DEFAULT_ITEM_ICON_ATTACHMENTS,
			() -> ImmutableList.of(TimelessAPI.getCommonAttachmentIndex(LOGO_ATTACHMENTS).map(
					attachmentIndex -> AttachmentItemBuilder.create().setCount(0).setId(LOGO_ATTACHMENTS).build()
			).orElse(DEFAULT_ITEM_ICON_ATTACHMENTS))
	);
	public static final RecipeBookCategories RECOVER_FURNACE_MISC = RecipeBookCategories.create("MISC_TWF$RECOVER_FURNACE_MISC", new ItemStack(ModItems.STATUE.get()));

	private MISCTWFRecipeBookTypes() {
	}

	public static void init() {
		RecipeBookRegistry.addCategoriesToType(RECOVER_FURNACE, List.of(RECOVER_FURNACE_SEARCH, RECOVER_FURNACE_GUNS, RECOVER_FURNACE_BULLETS, RECOVER_FURNACE_ATTACHMENTS, RECOVER_FURNACE_MISC));
		RecipeBookRegistry.addAggregateCategories(RECOVER_FURNACE_SEARCH, List.of(RECOVER_FURNACE_GUNS, RECOVER_FURNACE_BULLETS, RECOVER_FURNACE_ATTACHMENTS, RECOVER_FURNACE_MISC));
		RecipeBookRegistry.addCategoriesFinder(MISCTWFRecipeTypes.RECOVERY_FURNACE.get(), recipe -> {
			ItemStack itemStack = recipe instanceof RecoveryFurnaceRecipe recoveryFurnaceRecipe ? recoveryFurnaceRecipe.ingredient().getResult() : ItemStack.EMPTY;
			if(itemStack.is(ModItems.MODERN_KINETIC_GUN.get())) {
				return RECOVER_FURNACE_GUNS;
			}
			if(itemStack.is(ModItems.AMMO.get())) {
				return RECOVER_FURNACE_BULLETS;
			}
			if(itemStack.is(ModItems.ATTACHMENT.get())) {
				return RECOVER_FURNACE_ATTACHMENTS;
			}
			return RECOVER_FURNACE_MISC;
		});
	}

	private static RecipeBookCategories create(String name, ItemStack defaultItemStack, Supplier<List<ItemStack>> lazyItemIconsGetter) {
		RecipeBookCategories ret = RecipeBookCategories.create(name, defaultItemStack);
		if((Object)ret instanceof IHasItemIcons hasItemIcons) {
			hasItemIcons.misc_twf$setLazyItemIconsGetter(lazyItemIconsGetter);
		}
		return ret;
	}
}
