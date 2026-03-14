package com.hexagram2021.misc_twf.mixin.vanilla;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractFurnaceBlockEntity.class)
public class AbstractFurnaceBlockEntityMixin {
	@Shadow
	private static boolean canBurn(RegistryAccess pRegistryAccess, @Nullable RecipeHolder<?> pRecipe, NonNullList<ItemStack> pInventory, int pMaxStackSize, AbstractFurnaceBlockEntity furnace) {
		throw new UnsupportedOperationException("Unexpected call");
	}

	@Inject(method = "serverTick", at = @At(value = "HEAD"), cancellable = true)
	private static void checkIfLit(Level level, BlockPos blockPos, BlockState blockState, AbstractFurnaceBlockEntity blockEntity, CallbackInfo ci) {
		if(!blockState.getValue(AbstractFurnaceBlock.LIT)) {
			ci.cancel();
		}
	}

	@Redirect(method = "serverTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/AbstractFurnaceBlockEntity;isLit()Z", ordinal = 0))
	private static boolean ignoreNotLit(AbstractFurnaceBlockEntity instance) {
		return true;
	}

	@Redirect(method = "serverTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z", ordinal = 1))
	private static boolean ignoreEmpty(ItemStack instance) {
		return false;
	}

	@Redirect(method = "serverTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/AbstractFurnaceBlockEntity;canBurn(Lnet/minecraft/core/RegistryAccess;Lnet/minecraft/world/item/crafting/RecipeHolder;Lnet/minecraft/core/NonNullList;ILnet/minecraft/world/level/block/entity/AbstractFurnaceBlockEntity;)Z", ordinal = 0))
	private static boolean canBurnEmpty(RegistryAccess registryAccess, RecipeHolder<?> recipe, NonNullList<ItemStack> itemStacks, int count, AbstractFurnaceBlockEntity instance) {
		if(itemStacks.getFirst().isEmpty()) {
			return true;
		}
		return canBurn(registryAccess, recipe, itemStacks, count, instance);
	}
}
