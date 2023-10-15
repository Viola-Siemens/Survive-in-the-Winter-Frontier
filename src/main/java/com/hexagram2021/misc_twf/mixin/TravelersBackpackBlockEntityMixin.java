package com.hexagram2021.misc_twf.mixin;

import com.hexagram2021.misc_twf.common.util.IAmmoBackpack;
import com.tiviacz.travelersbackpack.blockentity.TravelersBackpackBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.items.ItemStackHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TravelersBackpackBlockEntity.class)
public class TravelersBackpackBlockEntityMixin implements IAmmoBackpack {
	private boolean updateToTac = false;

	@Inject(method = "saveAllData", at = @At(value = "HEAD"), remap = false)
	public void saveTac(CompoundTag compound, CallbackInfo ci) {
		compound.putBoolean("updateToTac", this.updateToTac);
	}

	@Inject(method = "loadAllData", at = @At(value = "HEAD"), remap = false)
	public void loadTac(CompoundTag compound, CallbackInfo ci) {
		this.updateToTac = compound.contains("updateToTac", Tag.TAG_BYTE) && compound.getBoolean("updateToTac");
	}

	@Override
	public boolean canStoreAmmo() {
		return this.updateToTac;
	}

	@Override
	public ItemStackHandler getAmmoHandler() {
		return null;
	}
}
