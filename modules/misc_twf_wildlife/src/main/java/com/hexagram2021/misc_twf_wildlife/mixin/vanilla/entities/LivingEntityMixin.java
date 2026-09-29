package com.hexagram2021.misc_twf_wildlife.mixin.vanilla.entities;

import com.hexagram2021.misc_twf_wildlife.common.register.MISCTWFBlocks;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.appleseed.appleseed.common.capability.DietData;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.neoforged.fml.ModList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 生物实体注入：动物死亡掉落尸体、腌制食物联动 AppleSeed 盐分组喵~
 *
 * @author liudongyu
 */
@Mixin(LivingEntity.class)
public class LivingEntityMixin {
	@Unique
	private static final String MISC_TWF$GROUP_SALT = "salt";
	@Unique
	private static final String MISC_TWF$APPLESEED_MOD_ID = "appleseed";

	/**
	 * 进食后若食物带有 Salted 标记，则增加 AppleSeed 的 salt 分组数值（AppleSeed 缺失时跳过）喵~
	 *
	 * @param level          世界喵~
	 * @param itemStack      被进食的物品堆叠喵~
	 * @param foodProperties 食物属性喵~
	 * @param cir            返回值回调喵~
	 */
	@Inject(
			method = "eat(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/food/FoodProperties;)Lnet/minecraft/world/item/ItemStack;",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;addEatEffect(Lnet/minecraft/world/food/FoodProperties;)V", shift = At.Shift.AFTER)
	)
	public void misc_twf$applyToDietIfSalted(Level level, ItemStack itemStack, FoodProperties foodProperties, CallbackInfoReturnable<ItemStack> cir) {
		// 约定 API 占位：AppleSeed 未安装时直接跳过，避免类解析失败喵~
		if(!ModList.get().isLoaded(MISC_TWF$APPLESEED_MOD_ID)) {
			return;
		}
		CustomData customData = itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
		if(customData.copyTag().getBoolean("Salted")) {
			if((Object)this instanceof ServerPlayer serverPlayer &&
					DietData.getAllValues(serverPlayer).containsKey(MISC_TWF$GROUP_SALT)) {
				DietData.setValue(serverPlayer, MISC_TWF$GROUP_SALT, DietData.getValue(serverPlayer, MISC_TWF$GROUP_SALT) + 0.02F);
				DietData.syncToClient(serverPlayer);
			}
		}
	}

	/**
	 * 将原版动物死亡掉落替换为对应动物尸体方块喵~
	 *
	 * @param instance    死亡的生物喵~
	 * @param damageSource 伤害来源喵~
	 * @param hurtByPlayer 是否被玩家击杀喵~
	 * @param original    原掉落表调用喵~
	 */
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
