package com.hexagram2021.misc_twf.mixin;

import com.hexagram2021.misc_twf.common.ForgeEventHandler;
import com.hexagram2021.misc_twf.common.item.IEnergyItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Objects;

@Mixin(value = LivingEntity.class, priority = 4096)
public class LivingEntityMixin {
	@Redirect(method = "collectEquipmentChanges", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;matches(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z"))
	private boolean matchesAndIgnoreElectricity(ItemStack itemStack1, ItemStack itemStack2) {
		if(itemStack1.sameItem(itemStack2) && itemStack1.getItem() instanceof IEnergyItem) {
			CompoundTag tag1 = itemStack1.getTag();
			CompoundTag tag2 = itemStack2.getTag();
			if(tag1 != null) {
				tag1 = tag1.copy();
				tag1.remove(ForgeEventHandler.ENERGY.toString());
			}
			if(tag2 != null) {
				tag2 = tag2.copy();
				tag2.remove(ForgeEventHandler.ENERGY.toString());
			}
			return Objects.equals(tag1, tag2);
		}
		return ItemStack.matches(itemStack1, itemStack2);
	}
}
