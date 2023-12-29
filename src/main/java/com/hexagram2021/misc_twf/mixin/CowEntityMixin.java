package com.hexagram2021.misc_twf.mixin;

import com.hexagram2021.misc_twf.common.config.MISCTWFCommonConfig;
import com.hexagram2021.misc_twf.common.entity.IProduceMilk;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Cow.class)
public abstract class CowEntityMixin extends Animal implements IProduceMilk {
	protected CowEntityMixin(EntityType<? extends Animal> type, Level level) {
		super(type, level);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.getEntityData().define(DATA_COW_MILK_COOL_DOWN, 0);
	}

	@Override
	public void aiStep() {
		super.aiStep();
		int milkCD = this.getMilkCoolDown();
		if(milkCD > 0) {
			this.setMilkCoolDown(milkCD - 1);
		}
	}

	@Inject(method = "mobInteract", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;playSound(Lnet/minecraft/sounds/SoundEvent;FF)V", shift = At.Shift.BEFORE), cancellable = true)
	public void misc_twf$checkMilkCoolDown(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
		if(!this.isAvailableToProduceMilk()) {
			cir.setReturnValue(InteractionResult.sidedSuccess(this.level.isClientSide));
			cir.cancel();
			return;
		}

		this.setMilkCoolDown(MISCTWFCommonConfig.MILK_INTERVAL.get() * 20);
	}

	@Override
	public int getMilkCoolDown() {
		return this.getEntityData().get(DATA_COW_MILK_COOL_DOWN);
	}

	@Override
	public void setMilkCoolDown(int delay) {
		this.getEntityData().set(DATA_COW_MILK_COOL_DOWN, delay);
	}
}
