package com.hexagram2021.misc_twf.common.entity;

import com.hexagram2021.misc_twf.common.register.MISCTWFSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ZombieChickenEntity extends ZombieAnimalEntity<Chicken> {
	public float flap;
	public float flapSpeed;
	public float oFlapSpeed;
	public float oFlap;
	public float flapping = 1.0F;
	private float nextFlap = 1.0F;

	public ZombieChickenEntity(EntityType<? extends Zombie> entityType, Level level) {
		super(entityType, EntityType.CHICKEN, level);
	}

	@Override
	public void aiStep() {
		super.aiStep();
		this.oFlap = this.flap;
		this.oFlapSpeed = this.flapSpeed;
		this.flapSpeed += (this.onGround ? -1.0F : 4.0F) * 0.3F;
		this.flapSpeed = Mth.clamp(this.flapSpeed, 0.0F, 1.0F);
		if (!this.onGround && this.flapping < 1.0F) {
			this.flapping = 1.0F;
		}

		this.flapping *= 0.9F;
		Vec3 vec3 = this.getDeltaMovement();
		if (!this.onGround && vec3.y < 0.0D) {
			this.setDeltaMovement(vec3.multiply(1.0D, 0.6D, 1.0D));
		}

		this.flap += this.flapping * 2.0F;
	}

	@Override
	protected boolean isFlapping() {
		return this.flyDist > this.nextFlap;
	}

	@Override
	protected void onFlap() {
		this.nextFlap = this.flyDist + this.flapSpeed / 2.0F;
	}

	@Override
	public boolean causeFallDamage(float damage, float multiplier, DamageSource damageSource) {
		return false;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return MISCTWFSounds.ZOMBIE_CHICKEN_AMBIENT;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return MISCTWFSounds.ZOMBIE_CHICKEN_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return MISCTWFSounds.ZOMBIE_CHICKEN_DEATH;
	}

	@Override
	protected SoundEvent getStepSound() {
		return MISCTWFSounds.ZOMBIE_CHICKEN_STEP;
	}
}
