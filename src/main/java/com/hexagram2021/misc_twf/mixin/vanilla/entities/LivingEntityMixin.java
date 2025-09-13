package com.hexagram2021.misc_twf.mixin.vanilla.entities;

import com.hexagram2021.misc_twf.common.register.MISCTWFBlocks;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.theillusivec4.diet.api.DietCapability;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
	@Unique
	private static final String MISC_TWF$GROUP_SALT = "salt";

	@Inject(method = "eat", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;addEatEffect(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;)V", shift = At.Shift.AFTER))
	public void misc_twf$applyToDietIfSalted(Level level, ItemStack itemStack, CallbackInfoReturnable<ItemStack> cir) {
		CompoundTag tag = itemStack.getTag();
		if(tag != null && tag.getBoolean("Salted")) {
			if((Object)this instanceof ServerPlayer serverPlayer) {
				DietCapability.get(serverPlayer).ifPresent(c -> {
					if(c.getValues().containsKey(MISC_TWF$GROUP_SALT)) {
						c.setValue(MISC_TWF$GROUP_SALT, c.getValue(MISC_TWF$GROUP_SALT) + 0.02F);
						c.sync();
					}
				});
			}
		}
	}

	@WrapOperation(method = "dropAllDeathLoot", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;dropFromLootTable(Lnet/minecraft/world/damagesource/DamageSource;Z)V"))
	protected void misc_twf$replaceLootTable(LivingEntity instance, DamageSource damageSource, boolean hurtByPlayer, Operation<Void> original) {
		EntityType<?> entityType = instance.getType();
		Item loot = null;
		if(entityType == EntityType.CHICKEN) {
			loot = MISCTWFBlocks.DeadAnimals.DEAD_CHICKEN.asItem();
		} else if(entityType == EntityType.COW) {
			loot = MISCTWFBlocks.DeadAnimals.DEAD_COW.asItem();
		} else if(entityType == EntityType.GOAT) {
			loot = MISCTWFBlocks.DeadAnimals.DEAD_GOAT.asItem();
		} else if(entityType == EntityType.HORSE) {
			loot = MISCTWFBlocks.DeadAnimals.DEAD_HORSE.asItem();
		} else if(entityType == EntityType.PIG) {
			loot = MISCTWFBlocks.DeadAnimals.DEAD_PIG.asItem();
		} else if(entityType == EntityType.POLAR_BEAR) {
			loot = MISCTWFBlocks.DeadAnimals.DEAD_POLARBEAR.asItem();
		} else if(entityType == EntityType.RABBIT) {
			loot = MISCTWFBlocks.DeadAnimals.DEAD_RABBIT.asItem();
		} else if(entityType == EntityType.SHEEP) {
			loot = MISCTWFBlocks.DeadAnimals.DEAD_SHEEP.asItem();
		} else if(entityType == EntityType.WOLF) {
			loot = MISCTWFBlocks.DeadAnimals.DEAD_WOLF.asItem();
		}
		if(loot == null) {
			original.call(instance, damageSource, hurtByPlayer);
		} else {
			instance.spawnAtLocation(loot);
		}
	}
}
