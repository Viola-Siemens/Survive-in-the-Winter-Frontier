package com.hexagram2021.misc_twf_adventure.mixin.travelersbackpack;

import com.hexagram2021.misc_twf_adventure.client.screen.TacButton;
import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.client.screens.buttons.IButton;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

/**
 * 在旅行背包界面初始化按钮时追加 TAC 弹药槽按钮喵~
 *
 * @author liudongyu
 */
@Mixin(BackpackScreen.class)
public class TravelersBackpackScreenMixin {
	@Shadow(remap = false)
	public List<IButton> buttons;

	@Inject(method = "initButtons", at = @At(value = "TAIL"), remap = false)
	public void addTACButton(CallbackInfo ci) {
		this.buttons.add(new TacButton((BackpackScreen) (Object) this));
	}
}