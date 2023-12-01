package com.hexagram2021.misc_twf.common.entity;

import com.hexagram2021.misc_twf.common.register.MISCTWFSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.ZombieAttackGoal;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;

public class ZombieGoatEntity extends ZombieAnimalEntity<Goat> {
	public ZombieGoatEntity(EntityType<? extends Zombie> entityType, Level level) {
		super(entityType, EntityType.GOAT, level);
	}

	@Override
	protected void addAnimalBehaviourGoals() {
		this.goalSelector.addGoal(2, new ZombieAttackGoal(this, 1.0D, false));
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return MISCTWFSounds.ZOMBIE_GOAT_AMBIENT;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return MISCTWFSounds.ZOMBIE_GOAT_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return MISCTWFSounds.ZOMBIE_GOAT_DEATH;
	}

	@Override
	protected SoundEvent getStepSound() {
		return MISCTWFSounds.ZOMBIE_GOAT_STEP;
	}
}
