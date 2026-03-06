package com.hexagram2021.misc_twf.mixin.vanilla.entities;

import com.hexagram2021.misc_twf.common.config.MISCTWFCommonConfig;
import com.hexagram2021.misc_twf.common.entity.IProduceMilk;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 牛实体 Mixin，为牛添加挤奶冷却时间机制喵~
 * <p>
 * 该 Mixin 实现了以下功能：
 * <ul>
 *   <li>覆盖数据同步器定义，添加挤奶冷却时间数据喵~</li>
 *   <li>覆盖 AI 更新方法，自动减少冷却时间喵~</li>
 *   <li>拦截玩家与牛的交互，检查是否可以挤奶喵~</li>
 * </ul>
 * </p>
 *
 * @see IProduceMilk
 * @author liudongyu
 */
@Mixin(Cow.class)
public abstract class CowEntityMixin extends Animal implements IProduceMilk {
	/**
	 * Mixin 构造函数（Shadow 构造函数）喵~
	 *
	 * @param type 实体类型喵~
	 * @param level 世界对象喵~
	 */
	protected CowEntityMixin(EntityType<? extends Animal> type, Level level) {
		super(type, level);
	}

	/**
	 * 覆盖数据同步器定义，添加牛的挤奶冷却时间数据喵~
	 *
	 * @param builder 数据同步器构建器喵~
	 */
	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(DataAccessors.DATA_COW_MILK_COOL_DOWN, 0);
	}

	/**
	 * 覆盖 AI 更新方法，倒计时挤奶冷却时间喵~
	 */
	@Override
	public void aiStep() {
		super.aiStep();
		int milkCD = this.misc_twf$getMilkCoolDown();
		if(milkCD > 0) {
			this.misc_twf$setMilkCoolDown(milkCD - 1);
		}
	}

	/**
	 * 拦截玩家与牛的交互，检查挤奶冷却时间喵~
	 * <p>
	 * 如果冷却时间未结束，会阻止挤奶操作并返回成功状态（但不产生牛奶）喵~
	 * 如果冷却时间已结束，会设置新的冷却时间喵~
	 * </p>
	 *
	 * @param player 与牛交互的玩家喵~
	 * @param hand 玩家使用的手喵~
	 * @param cir Mixin 回调信息返回值喵~
	 */
	@Inject(method = "mobInteract", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;playSound(Lnet/minecraft/sounds/SoundEvent;FF)V", shift = At.Shift.BEFORE), cancellable = true)
	public void misc_twf$checkMilkCoolDown(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
		if(!this.misc_twf$isAvailableToProduceMilk()) {
			cir.setReturnValue(InteractionResult.sidedSuccess(this.level().isClientSide));
			cir.cancel();
			return;
		}

		this.misc_twf$setMilkCoolDown(MISCTWFCommonConfig.MILK_INTERVAL.get() * 20);
	}

	@Override
	public int misc_twf$getMilkCoolDown() {
		return this.getEntityData().get(DataAccessors.DATA_COW_MILK_COOL_DOWN);
	}

	@Override
	public void misc_twf$setMilkCoolDown(int delay) {
		this.getEntityData().set(DataAccessors.DATA_COW_MILK_COOL_DOWN, delay);
	}

	@Inject(method = "<clinit>", at = @At(value = "TAIL"))
	private static void misc_twf$addDataAccessors(CallbackInfo ci) {
		DataAccessors.DATA_COW_MILK_COOL_DOWN = SynchedEntityData.defineId(Cow.class, EntityDataSerializers.INT);
	}
}
