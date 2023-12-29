package com.hexagram2021.misc_twf.mixin;

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

@Mixin(Monster.class)
public abstract class MonsterMixin extends PathfinderMob implements IAvoidBlockMonster {
	@Unique
	@SuppressWarnings("NotNullFieldNotInitialized")
	private AvoidBlockGoal<UltravioletLampBlock> avoidBlockGoal;

	protected MonsterMixin(EntityType<? extends PathfinderMob> type, Level level) {
		super(type, level);
	}

	@Inject(method = "<init>", at = @At(value = "TAIL"))
	protected void misc_twf$addAvoidBlockGoal(EntityType<? extends Monster> type, Level level, CallbackInfo ci) {
		this.avoidBlockGoal = new AvoidBlockGoal<>(this, MISCTWFBlocks.ULTRAVIOLET_LAMP.get(), 24.0F, 1.0D, 1.2D);
		this.goalSelector.addGoal(0, this.avoidBlockGoal);
	}

	@Override
	public AvoidBlockGoal<UltravioletLampBlock> getAvoidBlockGoal() {
		return this.avoidBlockGoal;
	}
}
