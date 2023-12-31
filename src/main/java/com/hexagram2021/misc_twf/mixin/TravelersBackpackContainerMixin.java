package com.hexagram2021.misc_twf.mixin;

import com.hexagram2021.misc_twf.common.util.IAmmoBackpack;
import com.tiviacz.travelersbackpack.inventory.TravelersBackpackContainer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.items.ItemStackHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TravelersBackpackContainer.class)
public abstract class TravelersBackpackContainerMixin implements IAmmoBackpack {
	@Shadow(remap = false)
	protected abstract ItemStackHandler createHandler(int size, boolean isInventory);

	@Unique
	private final ItemStackHandler ammoInventory = this.createHandler(9, false);

	@Unique
	private boolean upgradeToTac = false;

	@Inject(method = "saveAllData", at = @At(value = "HEAD"), remap = false)
	public void misc_twf$saveTac(CompoundTag compound, CallbackInfo ci) {
		this.saveAmmo(compound);
	}

	@Inject(method = "loadAllData", at = @At(value = "HEAD"), remap = false)
	public void misc_twf$loadTac(CompoundTag compound, CallbackInfo ci) {
		this.upgradeToTac = compound.contains("UpgradeToTac", Tag.TAG_BYTE) && compound.getBoolean("UpgradeToTac");
		if(compound.contains("AmmoInventory", Tag.TAG_COMPOUND)) {
			this.ammoInventory.deserializeNBT(compound.getCompound("AmmoInventory"));
		}
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
