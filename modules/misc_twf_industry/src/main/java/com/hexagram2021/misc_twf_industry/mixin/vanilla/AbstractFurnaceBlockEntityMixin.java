package com.hexagram2021.misc_twf_industry.mixin.vanilla;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 熔炉方块实体 Mixin，需要用打火石等物品手动点燃后才能消耗燃料并工作。<br/>
 * 即使没有输入原料，依然可以完整燃烧一个燃料。
 *
 * @author liudongyu
 */
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

	@WrapOperation(method = "serverTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/AbstractFurnaceBlockEntity;isLit()Z", ordinal = 0))
	private static boolean ignoreNotLit(AbstractFurnaceBlockEntity instance, Operation<Boolean> original) {
		return true;
	}

	@WrapOperation(method = "serverTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z", ordinal = 1))
	private static boolean ignoreEmpty(ItemStack instance, Operation<Boolean> original) {
		return false;
	}

	@WrapOperation(method = "serverTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/AbstractFurnaceBlockEntity;canBurn(Lnet/minecraft/core/RegistryAccess;Lnet/minecraft/world/item/crafting/RecipeHolder;Lnet/minecraft/core/NonNullList;ILnet/minecraft/world/level/block/entity/AbstractFurnaceBlockEntity;)Z", ordinal = 0))
	private static boolean canBurnEmpty(RegistryAccess registryAccess, RecipeHolder<?> recipe,
										NonNullList<ItemStack> itemStacks,
										int count, AbstractFurnaceBlockEntity instance,
										Operation<Boolean> original) {
		if(itemStacks.getFirst().isEmpty()) {
			return true;
		}
		return canBurn(registryAccess, recipe, itemStacks, count, instance);
	}
}
