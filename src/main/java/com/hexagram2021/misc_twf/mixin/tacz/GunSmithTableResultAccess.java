package com.hexagram2021.misc_twf.mixin.tacz;

import com.tacz.guns.crafting.result.GunSmithTableResult;
import com.tacz.guns.crafting.result.RawGunTableResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import javax.annotation.Nullable;

@Mixin(value = GunSmithTableResult.class, remap = false)
public interface GunSmithTableResultAccess {
	@Accessor("raw") @Nullable
	RawGunTableResult misc_twf$getResult();
}
