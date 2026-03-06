package com.hexagram2021.misc_twf.mixin.vanilla.entities;

import com.hexagram2021.misc_twf.common.register.MISCTWFItems;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * 马类实体 Mixin，使马类动物可以食用冬小麦喵~
 * <p>
 * 该 Mixin 通过包装操作（WrapOperation）扩展了马类动物的食物判定，
 * 使它们可以像食用普通小麦一样食用冬小麦喵~
 * </p>
 *
 * @see AbstractHorse
 * @author liudongyu
 */
@Mixin(AbstractHorse.class)
public class AbstractHorseEntityMixin {
	/**
	 * 包装马类实体的食物判定操作，添加对冬小麦的支持喵~
	 * <p>
	 * 当马类实体尝试食用物品时，除了原版的判定逻辑外，还会检查该物品是否为冬小麦喵~
	 * </p>
	 *
	 * @param itemStack 待检查的物品堆喵~
	 * @param item 待比较的物品类型喵~
	 * @param original 原始的判定操作喵~
	 * @return 如果物品是原版食物或冬小麦，返回 true，否则返回 false 喵~
	 */
	@WrapOperation(method = "handleEating", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z", ordinal = 0))
	private boolean misc_twf$tryIsWinterWheat(ItemStack itemStack, Item item, Operation<Boolean> original) {
		return original.call(itemStack, item) || itemStack.is(MISCTWFItems.Materials.WINTER_WHEAT.get());
	}
}
