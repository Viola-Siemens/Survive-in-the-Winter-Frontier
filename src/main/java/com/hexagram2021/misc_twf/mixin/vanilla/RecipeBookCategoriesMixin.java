package com.hexagram2021.misc_twf.mixin.vanilla;

import com.hexagram2021.misc_twf.client.IHasItemIcons;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

@Mixin(RecipeBookCategories.class)
public class RecipeBookCategoriesMixin implements IHasItemIcons {
	@Shadow @Final @Mutable
	private List<ItemStack> itemIcons;

	@Unique
	@Nullable
	private Supplier<List<ItemStack>> misc_twf$lazyItemIconsGetter;

	@Override
	public void misc_twf$setLazyItemIconsGetter(Supplier<List<ItemStack>> lazyItemIconsGetter) {
		this.misc_twf$lazyItemIconsGetter = lazyItemIconsGetter;
	}

	@Inject(method = "getIconItems", at = @At(value = "HEAD"))
	private void misc_twf$lazyInitializeItemIcons(CallbackInfoReturnable<List<ItemStack>> cir) {
		if(this.misc_twf$lazyItemIconsGetter != null) {
			List<ItemStack> itemIcons = this.misc_twf$lazyItemIconsGetter.get();
			this.misc_twf$lazyItemIconsGetter = null;
			if(itemIcons != null) {
				this.itemIcons = itemIcons;
			}
		}
	}
}
