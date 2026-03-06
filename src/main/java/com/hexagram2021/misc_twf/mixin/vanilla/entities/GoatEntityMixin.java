package com.hexagram2021.misc_twf.mixin.vanilla.entities;

import com.hexagram2021.misc_twf.common.config.MISCTWFCommonConfig;
import com.hexagram2021.misc_twf.common.entity.IProduceMilk;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 山羊实体 Mixin，为山羊添加挤奶冷却时间机制喵~
 * <p>
 * 该 Mixin 实现了以下功能：
 * <ul>
 *   <li>注入数据同步器，添加挤奶冷却时间数据喵~</li>
 *   <li>拦截玩家与山羊的交互，检查是否可以挤奶喵~</li>
 *   <li>在 AI 更新中自动减少冷却时间喵~</li>
 * </ul>
 * </p>
 *
 * @see IProduceMilk
 * @author liudongyu
 */
@Mixin(Goat.class)
public class GoatEntityMixin implements IProduceMilk {
	/**
	 * 注入数据同步器定义，添加山羊的挤奶冷却时间数据喵~
	 *
	 * @param builder 数据同步器构建器喵~
	 * @param ci Mixin 回调信息喵~
	 */
	@Inject(method = "defineSynchedData", at = @At(value = "TAIL"))
	protected void addMilkCoolDownData(SynchedEntityData.Builder builder, CallbackInfo ci) {
		builder.define(DataAccessors.DATA_GOAT_MILK_COOL_DOWN, 0);
	}

	/**
	 * 拦截玩家与山羊的交互，检查挤奶冷却时间喵~
	 * <p>
	 * 如果冷却时间未结束，会阻止挤奶操作并返回成功状态（但不产生牛奶）喵~
	 * 如果冷却时间已结束，会设置新的冷却时间喵~
	 * </p>
	 *
	 * @param player 与山羊交互的玩家喵~
	 * @param hand 玩家使用的手喵~
	 * @param cir Mixin 回调信息返回值喵~
	 */
	@Inject(method = "mobInteract", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;playSound(Lnet/minecraft/sounds/SoundEvent;FF)V", shift = At.Shift.BEFORE), cancellable = true)
	public void checkMilkCoolDown(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
		Goat current = (Goat)(Object)this;

		if(!this.misc_twf$isAvailableToProduceMilk()) {
			cir.setReturnValue(InteractionResult.sidedSuccess(current.level().isClientSide));
			cir.cancel();
			return;
		}

		this.misc_twf$setMilkCoolDown(MISCTWFCommonConfig.MILK_INTERVAL.get() * 20);
	}

	/**
	 * 在山羊的 AI 更新中倒计时挤奶冷却时间喵~
	 *
	 * @param ci Mixin 回调信息喵~
	 */
	@Inject(method = "aiStep", at = @At(value = "TAIL"))
	public void misc_twf$countDownMilkCoolDown(CallbackInfo ci) {
		int milkCD = this.misc_twf$getMilkCoolDown();
		if(milkCD > 0) {
			this.misc_twf$setMilkCoolDown(milkCD - 1);
		}
	}

	@Override
	public int misc_twf$getMilkCoolDown() {
		Goat current = (Goat)(Object)this;
		return current.getEntityData().get(DataAccessors.DATA_GOAT_MILK_COOL_DOWN);
	}

	@Override
	public void misc_twf$setMilkCoolDown(int delay) {
		Goat current = (Goat)(Object)this;
		current.getEntityData().set(DataAccessors.DATA_GOAT_MILK_COOL_DOWN, delay);
	}

	@Inject(method = "<clinit>", at = @At(value = "TAIL"))
	private static void misc_twf$addDataAccessors(CallbackInfo ci) {
		DataAccessors.DATA_GOAT_MILK_COOL_DOWN = SynchedEntityData.defineId(Goat.class, EntityDataSerializers.INT);
	}
}
