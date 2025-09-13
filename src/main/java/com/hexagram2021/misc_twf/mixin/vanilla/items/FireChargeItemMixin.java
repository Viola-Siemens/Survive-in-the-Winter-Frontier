package com.hexagram2021.misc_twf.mixin.vanilla.items;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.item.FireChargeItem;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FireChargeItem.class)
public class FireChargeItemMixin {
	@WrapOperation(method = "useOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/CampfireBlock;canLight(Lnet/minecraft/world/level/block/state/BlockState;)Z"))
	private boolean misc_twf$canFurnaceLight(BlockState blockState, Operation<Boolean> original) {
		return original.call(blockState) || (blockState.getBlock() instanceof AbstractFurnaceBlock && !blockState.getValue(BlockStateProperties.LIT));
	}
}
