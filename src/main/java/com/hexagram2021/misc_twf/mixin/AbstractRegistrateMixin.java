package com.hexagram2021.misc_twf.mixin;

import com.tterrag.registrate.AbstractRegistrate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AbstractRegistrate.class)
public class AbstractRegistrateMixin {
	@Redirect(method = "onRegister", at = @At(value = "INVOKE", target = "Lcom/tterrag/registrate/AbstractRegistrate;isDevEnvironment()Z"))
	private boolean isDevEnvironment() {
		return false;
	}
}
