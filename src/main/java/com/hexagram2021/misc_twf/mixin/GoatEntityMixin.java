package com.hexagram2021.misc_twf.mixin;

import com.hexagram2021.misc_twf.common.config.MISCTWFCommonConfig;
import com.hexagram2021.misc_twf.common.entity.IProduceMilk;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Goat.class)
public class GoatEntityMixin implements IProduceMilk {
	@Inject(method = "defineSynchedData", at = @At(value = "TAIL"))
	protected void addMilkCoolDownData(CallbackInfo ci) {
		Goat current = (Goat)(Object)this;
		current.getEntityData().define(DATA_GOAT_MILK_COOL_DOWN, 0);
	}

	@Inject(method = "mobInteract", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;playSound(Lnet/minecraft/sounds/SoundEvent;FF)V", shift = At.Shift.BEFORE), cancellable = true)
	public void checkMilkCoolDown(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
		Goat current = (Goat)(Object)this;

		if(!this.isAvailableToProduceMilk()) {
			cir.setReturnValue(InteractionResult.sidedSuccess(current.level.isClientSide));
			cir.cancel();
			return;
		}

		this.setMilkCoolDown(MISCTWFCommonConfig.MILK_INTERVAL.get());
	}

	@Inject(method = "aiStep", at = @At(value = "TAIL"))
	public void countDownMilkCoolDown(CallbackInfo ci) {
		int milkCD = this.getMilkCoolDown();
		if(milkCD > 0) {
			this.setMilkCoolDown(milkCD - 1);
		}
	}

	@Override
	public int getMilkCoolDown() {
		Goat current = (Goat)(Object)this;
		return current.getEntityData().get(DATA_GOAT_MILK_COOL_DOWN);
	}

	@Override
	public void setMilkCoolDown(int delay) {
		Goat current = (Goat)(Object)this;
		current.getEntityData().set(DATA_GOAT_MILK_COOL_DOWN, delay);
	}
}
