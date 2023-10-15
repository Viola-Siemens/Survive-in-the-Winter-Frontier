package com.hexagram2021.misc_twf.common.item;

import com.hexagram2021.misc_twf.common.config.MISCTWFCommonConfig;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.energy.CapabilityEnergy;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import javax.annotation.Nullable;

public class NightVisionDeviceItem extends Item implements ICurioItem, IEnergyItem {
	public NightVisionDeviceItem(Properties props) {
		super(props);
	}

	@Override
	public void curioTick(SlotContext slotContext, ItemStack stack) {
		LivingEntity entity = slotContext.entity();
		if(entity.tickCount % 20 == 0) {
			stack.getCapability(CapabilityEnergy.ENERGY).ifPresent(ies -> ies.extractEnergy(1, false));
		}
	}

	@Override
	public CompoundTag getShareTag(ItemStack stack) {
		CompoundTag nbt = stack.getTag();
		nbt = this.getEnergyShareTag(nbt == null ? new CompoundTag() : nbt.copy(), stack);
		return nbt;
	}

	@Override
	public void readShareTag(ItemStack stack, @Nullable CompoundTag nbt) {
		super.readShareTag(stack, nbt);

		if (nbt != null) {
			this.readEnergyShareTag(nbt, stack);
		}
	}

	@Override
	public int getEnergyCapability() {
		return MISCTWFCommonConfig.NIGHT_VISION_DEVICE_ENERGY_CAPABILITY.get();
	}

	@Override
	public int getMaxEnergyReceiveSpeed() {
		return 2;
	}

	@Override
	public int getMaxEnergyExtractSpeed() {
		return 1;
	}

	@Override
	public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> list) {
		if (this.allowdedIn(tab)) {
			ItemStack itemStack = new ItemStack(this);
			this.readShareTag(itemStack, this.getMaxEnergyTag());
			list.add(itemStack);
		}
	}
}
