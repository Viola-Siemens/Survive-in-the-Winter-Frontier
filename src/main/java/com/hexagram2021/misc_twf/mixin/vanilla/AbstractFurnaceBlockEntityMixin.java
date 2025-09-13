package com.hexagram2021.misc_twf.mixin.vanilla;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class AbstractFurnaceBlockEntityMixin {
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

	@Redirect(method = "serverTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/AbstractFurnaceBlockEntity;canBurn(Lnet/minecraft/world/item/crafting/Recipe;Lnet/minecraft/core/NonNullList;I)Z", ordinal = 0))
	private static boolean canBurnEmpty(AbstractFurnaceBlockEntity instance, Recipe<?> recipe, NonNullList<ItemStack> itemStacks, int count) {
		if(itemStacks.get(0).isEmpty()) {
			return true;
		}
		return instance.canBurn(recipe, itemStacks, count);
	}
}
