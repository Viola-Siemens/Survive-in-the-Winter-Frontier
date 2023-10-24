package com.hexagram2021.misc_twf.mixin;

import com.hexagram2021.misc_twf.common.register.MISCTWFItemTags;
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
		if(itemStack1.sameItem(itemStack2) && itemStack1.is(MISCTWFItemTags.WAYFARER_ARMORS)) {
			return Objects.equals(itemStack1.getTag(), itemStack2.getTag());
		}
		return ItemStack.matches(itemStack1, itemStack2);
	}
}
