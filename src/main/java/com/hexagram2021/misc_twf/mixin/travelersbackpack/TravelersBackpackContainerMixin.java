package com.hexagram2021.misc_twf.mixin.travelersbackpack;

import com.hexagram2021.misc_twf.common.util.IAmmoBackpack;
import com.tiviacz.travelersbackpack.inventory.BackpackContainer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 旅行背包容器的 Mixin 类，为旅行背包注入 TAC 弹药存储功能喵~
 * 通过 Mixin 向 BackpackContainer 中添加弹药物品栏和升级状态字段喵~
 * 在背包数据的保存和加载时同步弹药槽数据喵~
 *
 * @author liudongyu
 */
@Mixin(BackpackContainer.class)
public abstract class TravelersBackpackContainerMixin implements IAmmoBackpack {
	@Shadow(remap = false)
	protected abstract ItemStackHandler createHandler(int size, boolean isInventory);

	@Unique
	private final ItemStackHandler misc_twf$ammoInventory = this.createHandler(9, false);

	@Unique
	private boolean misc_twf$upgradeToTac = false;

	@Inject(method = "saveAllData", at = @At(value = "HEAD"), remap = false)
	public void misc_twf$saveTac(CompoundTag compound, CallbackInfo ci) {
		this.saveAmmo(compound);
	}

	@Inject(method = "loadAllData", at = @At(value = "HEAD"), remap = false)
	public void misc_twf$loadTac(CompoundTag compound, CallbackInfo ci) {
		this.misc_twf$upgradeToTac = compound.contains("UpgradeToTac", Tag.TAG_BYTE) && compound.getBoolean("UpgradeToTac");
		if(compound.contains("AmmoInventory", Tag.TAG_COMPOUND)) {
			this.misc_twf$ammoInventory.deserializeNBT(compound.getCompound("AmmoInventory"));
		}
	}

	@Override
	public boolean canStoreAmmo() {
		return this.misc_twf$upgradeToTac;
	}

	@Override
	public ItemStackHandler getAmmoHandler() {
		return this.misc_twf$ammoInventory;
	}

	@Override
	public void saveAmmo(CompoundTag compound) {
		compound.putBoolean("UpgradeToTac", this.misc_twf$upgradeToTac);
		compound.put("AmmoInventory", this.misc_twf$ammoInventory.serializeNBT());
	}
}
