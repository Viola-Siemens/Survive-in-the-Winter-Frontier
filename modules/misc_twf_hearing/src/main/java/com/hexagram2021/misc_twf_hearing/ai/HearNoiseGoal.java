package com.hexagram2021.misc_twf_hearing.ai;

import com.hexagram2021.misc_twf_hearing.api.HearingApi;
import com.hexagram2021.misc_twf_hearing.api.NoiseSnapshot;
import com.hexagram2021.misc_twf_hearing.common.config.MISCTWFHearingConfig;
import com.hexagram2021.misc_twf_hearing.core.NoiseScoring;
import com.hexagram2021.misc_twf_hearing.debug.HearingDebugState;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.Optional;

/**
 * 循声 Goal 喵~：**只使用 MOVE 旗标**，听见噪声后前往噪声坐标调查。
 *
 * <p>硬性约束（见 docs/MODULARIZATION.md 11.9）：不调用 {@code mob.setTarget(...)}、
 * 不向 {@code targetSelector} 添加任何 Goal、不修改 {@code Sensing}/{@code TargetGoal}/寻路类、
 * 不为循声唤醒 inactive Mob、不每 tick 重建路径喵~</p>
 *
 * <p>结束条件只有三种：出现真实 Target 时让路、到达目的地、路径失败次数超限。
 * 噪声本身是否过期**不作为**结束条件（业务方决策 D8：已开始调查就走到旧坐标）喵~</p>
 *
 * @author liudongyu
 */
public final class HearNoiseGoal extends Goal {
	private final Mob mob;
	private final HearingProfile profile;
	@Nullable
	private NoiseSnapshot currentNoise;
	@Nullable
	private Vec3 destination;
	private float currentScore;
	private long nextQueryTick;
	private long nextRepathTick;
	private long nextSwitchAllowedTick;
	private int failedPathAttempts;

	/**
	 * 构造循声 Goal 喵~
	 *
	 * @param mob     宿主怪物喵~
	 * @param profile 听觉档案喵~
	 */
	public HearNoiseGoal(Mob mob, HearingProfile profile) {
		this.mob = mob;
		this.profile = profile;
		// 只声明 MOVE：不抢 LOOK/JUMP/TARGET，保证与 ZT2/CrawlMod 等行为自然共存喵~
		this.setFlags(EnumSet.of(Flag.MOVE));
	}

	@Override
	public boolean canUse() {
		if(this.hasLiveTarget()) {
			return false;
		}
		long now = this.mob.level().getGameTime();
		if(now < this.nextQueryTick) {
			return false;
		}
		this.scheduleNextQuery(now);
		if(!(this.mob.level() instanceof ServerLevel serverLevel)) {
			return false;
		}
		Optional<NoiseSnapshot> best = HearingApi.findBestAudible(
				serverLevel, this.mob.position(), this.profile.hearingMultiplier(), this::canHearType
		);
		if(best.isEmpty()) {
			return false;
		}
		this.currentNoise = best.get();
		this.destination = this.currentNoise.position();
		this.currentScore = this.scoreOf(this.currentNoise, now);
		this.failedPathAttempts = 0;
		this.reportDebug(serverLevel, "investigates");
		return this.tryMoveToDestination(now);
	}

	@Override
	public boolean canContinueToUse() {
		if(this.hasLiveTarget() || this.destination == null) {
			return false;
		}
		if(this.arrived()) {
			return false;
		}
		return this.failedPathAttempts < MISCTWFHearingConfig.MAX_PATH_FAILURES.get();
	}

	@Override
	public void tick() {
		if(this.hasLiveTarget()) {
			return;
		}
		long now = this.mob.level().getGameTime();
		this.maybeSwitchToBetterNoise(now);
		if(now >= this.nextRepathTick && this.mob.getNavigation().isDone()) {
			this.tryMoveToDestination(now);
		}
	}

	@Override
	public void stop() {
		// 按决策 D5：不主动 navigation.stop()，避免清掉刚接管的高优先级 Goal 建立的路径喵~
		if(this.mob.level() instanceof ServerLevel serverLevel) {
			this.reportDebug(serverLevel, "stopped investigating");
		}
		this.currentNoise = null;
		this.destination = null;
		this.currentScore = 0.0F;
		this.failedPathAttempts = 0;
	}

	/**
	 * 允许调查的噪声类型过滤（P0 接受全部类型，留给后续按类型细调）喵~
	 *
	 * @param noise 噪声快照喵~
	 * @return 是否可调查喵~
	 */
	private boolean canHearType(NoiseSnapshot noise) {
		// TODO P1：如需按类型区分（例如只有爆炸/枪声才吸引远处怪物），在此加入类型白名单喵~
		return true;
	}

	/**
	 * 向开启了循声调试的管理员输出诊断信息（无人开启时不构造消息）喵~
	 *
	 * @param level  当前维度喵~
	 * @param action 行为描述喵~
	 */
	private void reportDebug(ServerLevel level, String action) {
		NoiseSnapshot noise = this.currentNoise;
		Vec3 destination = this.destination;
		float score = this.currentScore;
		String uuid = this.mob.getUUID().toString();
		HearingDebugState.broadcast(
				level, () -> Component.literal("[hearing] ")
						.append(Component.literal(uuid).withStyle(
								style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, uuid))
										.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("commands.misc_twf_hearing.debug.copy")))
										.withColor(ChatFormatting.DARK_GREEN)
						)).append(" " + action +
								(noise == null ? "" : " noise=#" + noise.id() + " type=" + noise.type() + " score=" + score) +
								(destination == null ? "" : " dest=" + destination))
		);
	}

	private boolean hasLiveTarget() {
		return this.mob.getTarget() != null && this.mob.getTarget().isAlive();
	}

	private boolean arrived() {
		return this.destination != null && this.mob.position().distanceTo(this.destination) <= this.profile.arrivalDistance();
	}

	private void scheduleNextQuery(long now) {
		int jitter = Math.max(0, this.profile.queryJitterTicks());
		int extra = jitter == 0 ? 0 : this.mob.getRandom().nextInt(jitter + 1);
		this.nextQueryTick = now + this.profile.queryIntervalTicks() + extra;
	}

	private boolean tryMoveToDestination(long now) {
		if(this.destination == null) {
			return false;
		}
		boolean moved = this.mob.getNavigation().moveTo(
				this.destination.x, this.destination.y, this.destination.z, this.profile.investigateSpeed()
		);
		this.nextRepathTick = now + MISCTWFHearingConfig.REPATH_INTERVAL_TICKS.get();
		if(!moved) {
			this.failedPathAttempts++;
		}
		return moved;
	}

	private void maybeSwitchToBetterNoise(long now) {
		if(now < this.nextSwitchAllowedTick || !(this.mob.level() instanceof ServerLevel serverLevel)) {
			return;
		}
		Optional<NoiseSnapshot> best = HearingApi.findBestAudible(
				serverLevel, this.mob.position(), this.profile.hearingMultiplier(), this::canHearType
		);
		if(best.isEmpty()) {
			return;
		}
		NoiseSnapshot candidate = best.get();
		if(this.currentNoise != null && candidate.id() == this.currentNoise.id()) {
			return;
		}
		float candidateScore = this.scoreOf(candidate, now);
		if(candidateScore <= this.currentScore * (float) (double) MISCTWFHearingConfig.SWITCH_THRESHOLD.get()) {
			return;
		}
		this.currentNoise = candidate;
		this.destination = candidate.position();
		this.currentScore = candidateScore;
		this.failedPathAttempts = 0;
		this.reportDebug(serverLevel, "switches to");
		this.nextSwitchAllowedTick = now + MISCTWFHearingConfig.SWITCH_COOLDOWN_TICKS.get();
		this.tryMoveToDestination(now);
	}

	private float scoreOf(NoiseSnapshot noise, long now) {
		return NoiseScoring.score(noise, this.mob.position(), this.profile.hearingMultiplier(), now);
	}
}
