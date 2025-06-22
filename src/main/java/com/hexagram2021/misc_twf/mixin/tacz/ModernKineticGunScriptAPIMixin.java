package com.hexagram2021.misc_twf.mixin.tacz;

import com.hexagram2021.misc_twf.common.util.IAmmoBackpack;
import com.scarasol.sona.init.SonaMobEffects;
import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.api.item.IAmmo;
import com.tacz.guns.item.ModernKineticGunScriptAPI;
import com.tacz.guns.resource.pojo.data.gun.BulletData;
import com.tacz.guns.resource.pojo.data.gun.GunData;
import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.capability.ITravelersBackpack;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ModernKineticGunScriptAPI.class, remap = false)
public class ModernKineticGunScriptAPIMixin {
	@Shadow
	private LivingEntity shooter;

	@Shadow
	private ItemStack itemStack;

	@SuppressWarnings("ConstantConditions")
	@Inject(method = "consumeAmmoFromPlayer", at = @At(value = "RETURN", ordinal = 1), cancellable = true)
	private void misc_twf$consumeTravelersBackpackTacSlot(int neededAmount, CallbackInfoReturnable<Integer> cir) {
//		if(this.abstractGunItem.useDummyAmmo(this.itemStack)) {
//			return;
//		}
		int stillNeededAmount = neededAmount - cir.getReturnValue();
		if(stillNeededAmount <= 0) {
			return;
		}
		int cnt = 0;
		if(this.shooter instanceof Player player) {
			ITravelersBackpack backpack = CapabilityUtils.getCapability(player).orElse(null);
			if (backpack == null) {
				return;
			}
			IAmmoBackpack ammoBackpack = (IAmmoBackpack)backpack.getContainer();
			if (!ammoBackpack.canStoreAmmo()) {
				return;
			}
			for (int i = 0; i < ammoBackpack.getAmmoHandler().getSlots(); ++i) {
				ItemStack ammoStack = ammoBackpack.getAmmoHandler().getStackInSlot(i);

				if (ammoStack.getItem() instanceof IAmmo iAmmo && iAmmo.isAmmoOfGun(this.itemStack, ammoStack)) {
					if(cnt + ammoStack.getCount() > stillNeededAmount) {
						ammoStack.shrink(stillNeededAmount - cnt);
						cnt = stillNeededAmount;
					} else {
						cnt += ammoStack.getCount();
						ammoBackpack.getAmmoHandler().setStackInSlot(i, ItemStack.EMPTY);
					}
					if(cnt >= stillNeededAmount) {
						break;
					}
				}
			}
			ammoBackpack.saveAmmo(backpack.getContainer().getItemStack().getOrCreateTag());
			CapabilityUtils.synchronise(player);
			CapabilityUtils.synchroniseToOthers(player);

			cir.setReturnValue(cir.getReturnValue() + cnt);
		}
	}

	@SuppressWarnings("ConstantConditions")
	@Inject(method = "hasAmmoToConsume", at = @At(value = "RETURN", ordinal = 2), cancellable = true)
	private void misc_twf$checkTravelersBackpackTacSlot(CallbackInfoReturnable<Boolean> cir) {
		if(cir.getReturnValue()) {
			return;
		}
		if(this.shooter instanceof Player player) {
			ITravelersBackpack backpack = CapabilityUtils.getCapability(player).orElse(null);
			if (backpack == null) {
				return;
			}
			IAmmoBackpack ammoBackpack = (IAmmoBackpack) backpack.getContainer();
			if (!ammoBackpack.canStoreAmmo()) {
				return;
			}
			for (int i = 0; i < ammoBackpack.getAmmoHandler().getSlots(); ++i) {
				ItemStack ammoStack = ammoBackpack.getAmmoHandler().getStackInSlot(i);

				if (ammoStack.getItem() instanceof IAmmo iAmmo && iAmmo.isAmmoOfGun(this.itemStack, ammoStack)) {
					cir.setReturnValue(true);
					return;
				}
			}
		}
	}

	@Inject(method = "lambda$shootOnce$0", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/sound/SoundManager;sendSoundToNearby(Lnet/minecraft/world/entity/LivingEntity;ILnet/minecraft/resources/ResourceLocation;Lnet/minecraft/resources/ResourceLocation;Ljava/lang/String;FF)V"))
	private void misc_twf$soundAttract(boolean consumeAmmo, GunData gunData, int bulletAmount, BulletData bulletData, IGunOperator gunOperator, float processedSpeed, float finalInaccuracy, int soundDistance, boolean useSilenceSound, CallbackInfoReturnable<Boolean> cir) {
		if (gunOperator instanceof LivingEntity livingEntity) {
			livingEntity.addEffect(new MobEffectInstance(SonaMobEffects.EXPOSURE.get(), 10, useSilenceSound ? 0 : 3, true, false));
		}
	}
}
