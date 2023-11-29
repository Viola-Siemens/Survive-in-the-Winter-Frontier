package com.hexagram2021.misc_twf.common.entity;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.animal.PolarBear;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;

public class ZombiePolarBearEntity extends ZombieAnimalEntity<PolarBear> {
	private static final EntityDataAccessor<Boolean> DATA_STANDING_ID = SynchedEntityData.defineId(ZombiePolarBearEntity.class, EntityDataSerializers.BOOLEAN);
	private static final float STAND_ANIMATION_TICKS = 6.0F;
	private float clientSideStandAnimationO;
	private float clientSideStandAnimation;

	public ZombiePolarBearEntity(EntityType<? extends Zombie> entityType, Level level) {
		super(entityType, EntityType.POLAR_BEAR, level);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(DATA_STANDING_ID, false);
	}

	@Override
	public void tick() {
		super.tick();

		if (this.level.isClientSide) {
			if (this.clientSideStandAnimation != this.clientSideStandAnimationO) {
				this.refreshDimensions();
			}

			this.clientSideStandAnimationO = this.clientSideStandAnimation;
			if (this.isStanding()) {
				this.clientSideStandAnimation = Mth.clamp(this.clientSideStandAnimation + 1.0F, 0.0F, STAND_ANIMATION_TICKS);
			} else {
				this.clientSideStandAnimation = Mth.clamp(this.clientSideStandAnimation - 1.0F, 0.0F, STAND_ANIMATION_TICKS);
			}
		}
	}

	public boolean isStanding() {
		return this.entityData.get(DATA_STANDING_ID);
	}

	public void setStanding(boolean standing) {
		this.entityData.set(DATA_STANDING_ID, standing);
	}

	public float getStandingAnimationScale(float alpha) {
		return Mth.lerp(alpha, this.clientSideStandAnimationO, this.clientSideStandAnimation) / STAND_ANIMATION_TICKS;
	}

	@Override
	protected void addAnimalBehaviourGoals() {
		this.goalSelector.addGoal(2, new PolarBearMeleeAttackGoal());
	}

	@Override
	public boolean canAnimalBreakDoors() {
		return this.canBreakDoors();
	}

	class PolarBearMeleeAttackGoal extends MeleeAttackGoal {
		public PolarBearMeleeAttackGoal() {
			super(ZombiePolarBearEntity.this, 1.0D, false);
		}

		@Override
		protected void checkAndPerformAttack(LivingEntity target, double distanceSqr) {
			double attackReachSqr = this.getAttackReachSqr(target);
			if (distanceSqr <= attackReachSqr && this.isTimeToAttack()) {
				this.resetAttackCooldown();
				this.mob.doHurtTarget(target);
				ZombiePolarBearEntity.this.setStanding(false);
			} else if (distanceSqr <= attackReachSqr * 2.0D) {
				if (this.isTimeToAttack()) {
					ZombiePolarBearEntity.this.setStanding(false);
					this.resetAttackCooldown();
				}

				if (this.getTicksUntilNextAttack() <= 10) {
					ZombiePolarBearEntity.this.setStanding(true);
				}
			} else {
				this.resetAttackCooldown();
				ZombiePolarBearEntity.this.setStanding(false);
			}

		}

		@Override
		public void stop() {
			ZombiePolarBearEntity.this.setStanding(false);
			super.stop();
		}

		@Override
		protected double getAttackReachSqr(LivingEntity livingEntity) {
			return 5.0F + livingEntity.getBbWidth();
		}
	}
}
