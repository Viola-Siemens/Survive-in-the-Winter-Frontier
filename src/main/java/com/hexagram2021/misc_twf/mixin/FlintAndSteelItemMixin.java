package com.hexagram2021.misc_twf.mixin;

import net.minecraft.world.item.FlintAndSteelItem;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FlintAndSteelItem.class)
public class FlintAndSteelItemMixin {
	@Redirect(method = "useOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/CampfireBlock;canLight(Lnet/minecraft/world/level/block/state/BlockState;)Z"))
	private boolean canFurnaceLight(BlockState blockState) {
		return CampfireBlock.canLight(blockState) || (blockState.getBlock() instanceof AbstractFurnaceBlock && !blockState.getValue(BlockStateProperties.LIT));
	}
}
