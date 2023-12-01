package com.hexagram2021.misc_twf.common.entity;

import com.hexagram2021.misc_twf.common.register.MISCTWFSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.control.JumpControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CarrotBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

public class ZombieRabbitEntity extends ZombieAnimalEntity<Rabbit> {
	private int jumpTicks;
	private int jumpDuration;
	private boolean wasOnGround;
	private int jumpDelayTicks;

	public ZombieRabbitEntity(EntityType<? extends Zombie> entityType, Level level) {
		super(entityType, EntityType.RABBIT, level);
		this.jumpControl = new ZombieRabbitEntity.RabbitJumpControl(this);
		this.moveControl = new ZombieRabbitEntity.RabbitMoveControl(this);
		this.setSpeedModifier(0.0D);
	}

	@Override
	protected void addAnimalBehaviourGoals() {
		super.addAnimalBehaviourGoals();
		this.goalSelector.addGoal(5, new ZombieRabbitEntity.RaidGardenGoal());
	}

	@Override
	protected float getJumpPower() {
		if (!this.horizontalCollision && (!this.moveControl.hasWanted() || !(this.moveControl.getWantedY() > this.getY() + 0.5D))) {
			Path path = this.navigation.getPath();
			if (path != null && !path.isDone()) {
				Vec3 vec3 = path.getNextEntityPos(this);
				if (vec3.y > this.getY() + 0.5D) {
					return 0.5F;
				}
			}

			return this.moveControl.getSpeedModifier() <= 0.6D ? 0.2F : 0.3F;
		} else {
			return 0.5F;
		}
	}

	@Override
	protected void jumpFromGround() {
		super.jumpFromGround();
		double d0 = this.moveControl.getSpeedModifier();
		if (d0 > 0.0D) {
			double d1 = this.getDeltaMovement().horizontalDistanceSqr();
			if (d1 < 0.01D) {
				this.moveRelative(0.1F, new Vec3(0.0D, 0.0D, 1.0D));
			}
		}

		if (!this.level.isClientSide) {
			this.level.broadcastEntityEvent(this, (byte)1);
		}
	}

	public float getJumpCompletion(float tick) {
		return this.jumpDuration == 0 ? 0.0F : ((float)this.jumpTicks + tick) / (float)this.jumpDuration;
	}

	public void setSpeedModifier(double speedModifier) {
		this.getNavigation().setSpeedModifier(speedModifier);
		this.moveControl.setWantedPosition(this.moveControl.getWantedX(), this.moveControl.getWantedY(), this.moveControl.getWantedZ(), speedModifier);
	}

	@Override
	public void customServerAiStep() {
		if (this.jumpDelayTicks > 0) {
			--this.jumpDelayTicks;
		}

		if (this.onGround) {
			if (!this.wasOnGround) {
				this.setJumping(false);
				this.checkLandingDelay();
			}

			RabbitJumpControl rabbitJumpControl = (RabbitJumpControl)this.jumpControl;
			if (!rabbitJumpControl.wantJump()) {
				if (this.moveControl.hasWanted() && this.jumpDelayTicks == 0) {
					Path path = this.navigation.getPath();
					Vec3 vec3 = new Vec3(this.moveControl.getWantedX(), this.moveControl.getWantedY(), this.moveControl.getWantedZ());
					if (path != null && !path.isDone()) {
						vec3 = path.getNextEntityPos(this);
					}

					this.facePoint(vec3.x, vec3.z);
					this.startJumping();
				}
			} else if (!rabbitJumpControl.canJump()) {
				this.enableJumpControl();
			}
		}

		this.wasOnGround = this.onGround;
	}

	@Override
	public void aiStep() {
		super.aiStep();
		if (this.jumpTicks != this.jumpDuration) {
			++this.jumpTicks;
		} else if (this.jumpDuration != 0) {
			this.jumpTicks = 0;
			this.jumpDuration = 0;
			this.setJumping(false);
		}
	}

	@Override
	public boolean canSpawnSprintParticle() {
		return false;
	}

	private void facePoint(double x, double z) {
		this.setYRot((float)(Mth.atan2(z - this.getZ(), x - this.getX()) * (180.0D / Math.PI)) - 90.0F);
	}

	private void enableJumpControl() {
		((RabbitJumpControl)this.jumpControl).setCanJump(true);
	}

	private void disableJumpControl() {
		((RabbitJumpControl)this.jumpControl).setCanJump(false);
	}

	private void setLandingDelay() {
		if (this.moveControl.getSpeedModifier() < 2.2D) {
			this.jumpDelayTicks = 10;
		} else {
			this.jumpDelayTicks = 1;
		}

	}

	private void checkLandingDelay() {
		this.setLandingDelay();
		this.disableJumpControl();
	}

	@Override
	public void setJumping(boolean jumping) {
		super.setJumping(jumping);
		if (jumping) {
			this.playSound(this.getStepSound(), this.getSoundVolume(), ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) * 0.8F);
		}

	}

	public void startJumping() {
		this.setJumping(true);
		this.jumpDuration = 10;
		this.jumpTicks = 0;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return MISCTWFSounds.ZOMBIE_RABBIT_AMBIENT;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return MISCTWFSounds.ZOMBIE_RABBIT_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return MISCTWFSounds.ZOMBIE_RABBIT_DEATH;
	}

	@Override
	protected SoundEvent getStepSound() {
		return MISCTWFSounds.ZOMBIE_RABBIT_STEP;
	}

	@Override
	protected void playStepSound(BlockPos blockPos, BlockState blockState) {
	}

	public static class RabbitJumpControl extends JumpControl {
		private final ZombieRabbitEntity rabbit;
		private boolean canJump;

		public RabbitJumpControl(ZombieRabbitEntity entity) {
			super(entity);
			this.rabbit = entity;
		}

		@SuppressWarnings("BooleanMethodIsAlwaysInverted")
		public boolean wantJump() {
			return this.jump;
		}

		public boolean canJump() {
			return this.canJump;
		}

		public void setCanJump(boolean canJump) {
			this.canJump = canJump;
		}

		@Override
		public void tick() {
			if (this.jump) {
				this.rabbit.startJumping();
				this.jump = false;
			}
		}
	}

	static class RabbitMoveControl extends MoveControl {
		private final ZombieRabbitEntity rabbit;
		private double nextJumpSpeed;

		public RabbitMoveControl(ZombieRabbitEntity entity) {
			super(entity);
			this.rabbit = entity;
		}

		@Override
		public void tick() {
			if (this.rabbit.onGround && !this.rabbit.jumping && !((RabbitJumpControl)this.rabbit.jumpControl).wantJump()) {
				this.rabbit.setSpeedModifier(0.0D);
			} else if (this.hasWanted()) {
				this.rabbit.setSpeedModifier(this.nextJumpSpeed);
			}

			super.tick();
		}

		@Override
		public void setWantedPosition(double x, double y, double z, double modifier) {
			if (this.rabbit.isInWater()) {
				modifier = 1.5D;
			}

			super.setWantedPosition(x, y, z, modifier);
			if (modifier > 0.0D) {
				this.nextJumpSpeed = modifier;
			}

		}
	}

	class RaidGardenGoal extends MoveToBlockGoal {
		private boolean wantsToRaid;
		private boolean canRaid;

		public RaidGardenGoal() {
			super(ZombieRabbitEntity.this, 0.7F, 16);
		}

		@Override
		public boolean canUse() {
			if (this.nextStartTick <= 0) {
				if (!net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(ZombieRabbitEntity.this.level, ZombieRabbitEntity.this)) {
					return false;
				}

				this.canRaid = false;
				this.wantsToRaid = true;
			}

			return super.canUse();
		}

		@Override
		public boolean canContinueToUse() {
			return this.canRaid && super.canContinueToUse();
		}

		@Override
		public void tick() {
			super.tick();
			ZombieRabbitEntity.this.getLookControl().setLookAt((double)this.blockPos.getX() + 0.5D, this.blockPos.getY() + 1, (double)this.blockPos.getZ() + 0.5D, 10.0F, (float)ZombieRabbitEntity.this.getMaxHeadXRot());
			if (this.isReachedTarget()) {
				Level level = ZombieRabbitEntity.this.level;
				BlockPos blockpos = this.blockPos.above();
				BlockState blockstate = level.getBlockState(blockpos);
				Block block = blockstate.getBlock();
				if (this.canRaid && block instanceof CarrotBlock) {
					int i = blockstate.getValue(CarrotBlock.AGE);
					if (i == 0) {
						level.setBlock(blockpos, Blocks.AIR.defaultBlockState(), 2);
						level.destroyBlock(blockpos, true, ZombieRabbitEntity.this);
					} else {
						level.setBlock(blockpos, blockstate.setValue(CarrotBlock.AGE, i - 1), 2);
						level.levelEvent(2001, blockpos, Block.getId(blockstate));
					}
				}

				this.canRaid = false;
				this.nextStartTick = 10;
			}
		}

		@Override
		protected boolean isValidTarget(LevelReader level, BlockPos blockPos) {
			BlockState blockstate = level.getBlockState(blockPos);
			if (blockstate.is(Blocks.FARMLAND) && this.wantsToRaid && !this.canRaid) {
				blockstate = level.getBlockState(blockPos.above());
				if (blockstate.getBlock() instanceof CarrotBlock && ((CarrotBlock)blockstate.getBlock()).isMaxAge(blockstate)) {
					this.canRaid = true;
					return true;
				}
			}

			return false;
		}
	}
}
