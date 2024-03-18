package com.hexagram2021.misc_twf.mixin;

import com.hexagram2021.misc_twf.client.screen.TacButton;
import com.tiviacz.travelersbackpack.client.screens.TravelersBackpackScreen;
import com.tiviacz.travelersbackpack.client.screens.buttons.IButton;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackContainer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(TravelersBackpackScreen.class)
public class TravelersBackpackScreenMixin {
	@Shadow(remap = false) @Final
	public ITravelersBackpackContainer container;

	@Shadow(remap = false)
	public List<IButton> buttons;

	@Inject(method = "initButtons", at = @At(value = "TAIL"), remap = false)
	public void addTACButton(CallbackInfo ci) {
		this.buttons.add(new TacButton((TravelersBackpackScreen)(Object)this));
	}
}
