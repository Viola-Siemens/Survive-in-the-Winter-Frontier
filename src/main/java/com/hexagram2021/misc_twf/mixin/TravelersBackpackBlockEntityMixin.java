package com.hexagram2021.misc_twf.mixin;

import com.hexagram2021.misc_twf.common.util.IAmmoBackpack;
import com.tiviacz.travelersbackpack.blockentity.TravelersBackpackBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TravelersBackpackBlockEntity.class)
public abstract class TravelersBackpackBlockEntityMixin implements IAmmoBackpack {
	@Shadow
	protected abstract ItemStackHandler createHandler(int size, boolean isInventory);

	@SuppressWarnings("NotNullFieldNotInitialized")
	private ItemStackHandler ammoInventory;

	private boolean updateToTac = false;

	@Inject(method = "<init>", at = @At(value = "TAIL"), remap = false)
	public void addAmmoHandler(BlockPos pos, BlockState state, CallbackInfo ci) {
		this.ammoInventory = this.createHandler(9, false);
	}

	@Inject(method = "saveAllData", at = @At(value = "HEAD"), remap = false)
	public void saveTac(CompoundTag compound, CallbackInfo ci) {
		compound.putBoolean("UpgradeToTac", this.updateToTac);
		compound.put("AmmoInventory", this.ammoInventory.serializeNBT());
	}

	@Inject(method = "loadAllData", at = @At(value = "HEAD"), remap = false)
	public void loadTac(CompoundTag compound, CallbackInfo ci) {
		this.updateToTac = compound.contains("updateToTac", Tag.TAG_BYTE) && compound.getBoolean("updateToTac");
		if(compound.contains("AmmoInventory", Tag.TAG_COMPOUND)) {
			this.ammoInventory.deserializeNBT(compound.getCompound("AmmoInventory"));
		}
	}

	@Override
	public boolean canStoreAmmo() {
		return this.updateToTac;
	}

	@Override
	public ItemStackHandler getAmmoHandler() {
		return this.ammoInventory;
	}
}
