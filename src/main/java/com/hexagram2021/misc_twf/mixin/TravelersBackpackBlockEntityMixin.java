package com.hexagram2021.misc_twf.mixin;

import com.hexagram2021.misc_twf.common.util.IAmmoBackpack;
import com.tiviacz.travelersbackpack.blockentity.TravelersBackpackBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(TravelersBackpackBlockEntity.class)
public abstract class TravelersBackpackBlockEntityMixin implements IAmmoBackpack {
	@Shadow(remap = false)
	protected abstract ItemStackHandler createHandler(int size, boolean isInventory);

	private final ItemStackHandler ammoInventory = this.createHandler(9, false);

	private boolean upgradeToTac = false;

	@Inject(method = "saveAllData", at = @At(value = "HEAD"), remap = false)
	public void saveTac(CompoundTag compound, CallbackInfo ci) {
		this.saveAmmo(compound);
	}

	@Inject(method = "loadAllData", at = @At(value = "HEAD"), remap = false)
	public void loadTac(CompoundTag compound, CallbackInfo ci) {
		this.upgradeToTac = compound.contains("UpgradeToTac", Tag.TAG_BYTE) && compound.getBoolean("UpgradeToTac");
		if(compound.contains("AmmoInventory", Tag.TAG_COMPOUND)) {
			this.ammoInventory.deserializeNBT(compound.getCompound("AmmoInventory"));
		}
	}

	@Inject(method = "transferToItemStack", at = @At(value = "INVOKE", target = "Lcom/tiviacz/travelersbackpack/blockentity/TravelersBackpackBlockEntity;saveTier(Lnet/minecraft/nbt/CompoundTag;)V", shift = At.Shift.BEFORE), remap = false, locals = LocalCapture.CAPTURE_FAILSOFT)
	public void saveTacToItemStack(ItemStack stack, CallbackInfoReturnable<ItemStack> cir, CompoundTag compound) {
		this.saveAmmo(compound);
	}

	@Override
	public boolean canStoreAmmo() {
		return this.upgradeToTac;
	}

	@Override
	public ItemStackHandler getAmmoHandler() {
		return this.ammoInventory;
	}

	@Override
	public void saveAmmo(CompoundTag compound) {
		compound.putBoolean("UpgradeToTac", this.upgradeToTac);
		compound.put("AmmoInventory", this.ammoInventory.serializeNBT());
	}
}
