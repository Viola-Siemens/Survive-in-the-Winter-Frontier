package com.hexagram2021.misc_twf.mixin.vanilla.entities;

import com.hexagram2021.misc_twf.common.block.UltravioletLampBlock;
import com.hexagram2021.misc_twf.common.entity.IAvoidBlockMonster;
import com.hexagram2021.misc_twf.common.entity.goal.AvoidBlockGoal;
import com.hexagram2021.misc_twf.common.register.MISCTWFBlocks;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 怪物实体 Mixin，为所有怪物添加避开紫外线灯的行为喵~
 * <p>
 * 该 Mixin 实现了以下功能：
 * <ul>
 *   <li>在怪物实体构造时，注入避开紫外线灯的 AI 目标喵~</li>
 *   <li>实现 {@link IAvoidBlockMonster} 接口，提供获取避开方块目标的方法喵~</li>
 *   <li>使怪物在 24 格范围内主动远离紫外线灯方块喵~</li>
 * </ul>
 * </p>
 *
 * @see IAvoidBlockMonster
 * @see AvoidBlockGoal
 * @see UltravioletLampBlock
 * @author liudongyu
 */
@Mixin(Monster.class)
public abstract class MonsterMixin extends PathfinderMob implements IAvoidBlockMonster {
	/**
	 * 避开紫外线灯的 AI 目标对象喵~
	 */
	@Unique
	@SuppressWarnings("NotNullFieldNotInitialized")
	private AvoidBlockGoal<UltravioletLampBlock> misc_twf$avoidBlockGoal;

	/**
	 * Mixin 构造函数（Shadow 构造函数）喵~
	 *
	 * @param type 实体类型喵~
	 * @param level 世界对象喵~
	 */
	protected MonsterMixin(EntityType<? extends PathfinderMob> type, Level level) {
		super(type, level);
	}

	/**
	 * 注入怪物构造函数，在初始化时添加避开紫外线灯的 AI 目标喵~
	 * <p>
	 * 该 AI 目标设置为最高优先级（优先级 0），使怪物在 24 格范围内以 1.0 倍速度行走、
	 * 1.2 倍速度奔跑来远离紫外线灯喵~
	 * </p>
	 *
	 * @param type 怪物实体类型喵~
	 * @param level 世界对象喵~
	 * @param ci Mixin 回调信息喵~
	 */
	@Inject(method = "<init>", at = @At(value = "TAIL"))
	protected void misc_twf$addAvoidBlockGoal(EntityType<? extends Monster> type, Level level, CallbackInfo ci) {
		this.misc_twf$avoidBlockGoal = new AvoidBlockGoal<>(this, MISCTWFBlocks.ULTRAVIOLET_LAMP.get(), 24.0F, 1.0D, 1.2D);
		this.goalSelector.addGoal(0, this.misc_twf$avoidBlockGoal);
	}

	@Override
	public AvoidBlockGoal<UltravioletLampBlock> misc_twf$getAvoidBlockGoal() {
		return this.misc_twf$avoidBlockGoal;
	}
}
