package com.hexagram2021.misc_twf.mixin.tacz;

import com.tacz.guns.crafting.result.RawGunTableResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = RawGunTableResult.class, remap = false)
public interface RawGunTableResultAccess {
	@Accessor("type")
	String misc_twf$getType();
}
