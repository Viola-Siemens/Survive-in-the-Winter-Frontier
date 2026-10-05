package com.hexagram2021.misc_twf_industry.common.entity.goal;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

/**
 * 避开方块目标，用于让生物寻路时远离特定方块（如怪物远离点亮的紫外线灯）
 *
 * @param <T> 方块类型
 */
public class AvoidBlockGoal<T extends Block> extends Goal {
	protected final PathfinderMob mob;
	protected final T toAvoid;
	protected final float maxDist;
	private final double tooCloseDistSqr;
	private final double walkSpeedModifier;
	private final double sprintSpeedModifier;
	@Nullable
	protected Path path;

	@Nullable
	private BlockPos blockPos;

	/**
	 * 构造函数
	 * @param mob 受目标影响的生物实体
	 * @param toAvoid 需要远离的方块
	 * @param maxDist 最远距离
	 * @param tooCloseDistSqr 需要跑起来的最远距离
	 * @param walkSpeedModifier 走开的速度
	 * @param sprintSpeedModifier 跑开的速度
	 */
	public AvoidBlockGoal(PathfinderMob mob, T toAvoid, float maxDist, double tooCloseDistSqr,
						  double walkSpeedModifier, double sprintSpeedModifier) {
		this.mob = mob;
		this.toAvoid = toAvoid;
		this.maxDist = maxDist;
		this.tooCloseDistSqr = tooCloseDistSqr;
		this.walkSpeedModifier = walkSpeedModifier;
		this.sprintSpeedModifier = sprintSpeedModifier;
	}

	@Override
	public boolean canUse() {
		if(this.blockPos == null || this.mob.getTarget() != null) {
			return false;
		}
		if(!this.blockPos.closerThan(this.mob.blockPosition(), this.maxDist)) {
			return false;
		}
		Vec3 avoidTarget = Vec3.atCenterOf(this.blockPos);
		Vec3 vec3 = DefaultRandomPos.getPosAway(this.mob, 16, 7, avoidTarget);
		if (vec3 == null) {
			return false;
		}
		if (vec3.distanceToSqr(avoidTarget) < this.mob.distanceToSqr(avoidTarget)) {
			return false;
		}
		this.path = this.mob.getNavigation().createPath(vec3.x, vec3.y, vec3.z, 0);
		return this.path != null;
	}

	@Override
	public boolean canContinueToUse() {
		return !this.mob.getNavigation().isDone();
	}

	@Override
	public void start() {
		this.mob.getNavigation().moveTo(this.path, this.walkSpeedModifier);
	}

	@Override
	public void stop() {
		this.blockPos = null;
	}

	@Override
	public void tick() {
		if (this.blockPos != null && this.mob.distanceToSqr(Vec3.atCenterOf(this.blockPos)) < this.tooCloseDistSqr) {
			this.mob.getNavigation().setSpeedModifier(this.sprintSpeedModifier);
		} else {
			this.mob.getNavigation().setSpeedModifier(this.walkSpeedModifier);
		}
	}

	/**
	 * 设置需要远离的方块位置
	 * @param blockPos 方块位置
	 */
	public void setBlockPos(BlockPos blockPos) {
		this.blockPos = blockPos;
	}
}
