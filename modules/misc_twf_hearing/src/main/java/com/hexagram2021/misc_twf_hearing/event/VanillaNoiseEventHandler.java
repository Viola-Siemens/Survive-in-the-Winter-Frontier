package com.hexagram2021.misc_twf_hearing.event;

import com.hexagram2021.misc_twf_hearing.MiscTwfHearing;
import com.hexagram2021.misc_twf_hearing.api.HearingApi;
import com.hexagram2021.misc_twf_hearing.api.NoiseTypes;
import com.hexagram2021.misc_twf_hearing.common.config.MISCTWFHearingConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/**
 * 原版行为来源监听器喵~：只把**有明确玩法意义**的行为转换为噪声，绝不监听所有 {@code SoundEvent}。
 *
 * <p>P0 覆盖：疾跑脚步（{@code sprint}）、落地（{@code land}）、开关门/活板门/栅栏门（{@code door}）、
 * 容器交互（{@code container}）、方块破坏（{@code block_break}）与玻璃破碎（{@code glass_break}）喵~</p>
 *
 * <p>所有入口都要求服务端权威：客户端侧事件一律忽略，且不因音频播放反推噪声喵~</p>
 *
 * @author liudongyu
 */
@EventBusSubscriber(modid = MiscTwfHearing.MODID)
public final class VanillaNoiseEventHandler {
	/**
	 * 玩家疾跑时按间隔产生脚步噪声喵~
	 *
	 * @param event 玩家 tick 事件喵~
	 */
	@SubscribeEvent
	public static void onPlayerTick(PlayerTickEvent.Post event) {
		if(!MISCTWFHearingConfig.ENABLE_SPRINT_NOISE.get()) {
			return;
		}
		Player player = event.getEntity();
		if(!(player.level() instanceof ServerLevel serverLevel) || !player.isSprinting() || !player.onGround()) {
			return;
		}
		int interval = MISCTWFHearingConfig.SPRINT_EMIT_INTERVAL_TICKS.get();
		if(player.tickCount % interval != 0) {
			return;
		}
		HearingApi.emit(serverLevel, player.position(), NoiseTypes.SPRINT, player);
	}

	/**
	 * 生物落地时产生噪声（下落距离不足则静默）喵~
	 *
	 * @param event 落地事件喵~
	 */
	@SubscribeEvent
	public static void onLivingFall(LivingFallEvent event) {
		if(!MISCTWFHearingConfig.ENABLE_LAND_NOISE.get()) {
			return;
		}
		LivingEntity entity = event.getEntity();
		if(!(entity.level() instanceof ServerLevel serverLevel)) {
			return;
		}
		if(event.getDistance() < MISCTWFHearingConfig.LAND_MIN_DISTANCE.get()) {
			return;
		}
		HearingApi.emit(serverLevel, entity.position(), NoiseTypes.LAND, entity);
	}

	/**
	 * 玩家与门类方块或容器交互时产生噪声喵~
	 *
	 * @param event 右键方块事件喵~
	 */
	@SubscribeEvent
	public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
		if(!(event.getLevel() instanceof ServerLevel serverLevel)) {
			return;
		}
		BlockState state = serverLevel.getBlockState(event.getPos());
		if(MISCTWFHearingConfig.ENABLE_DOOR_NOISE.get() && isDoorLike(state)) {
			HearingApi.emit(serverLevel, Vec3.atCenterOf(event.getPos()), NoiseTypes.DOOR, event.getEntity());
			return;
		}
		if(MISCTWFHearingConfig.ENABLE_CONTAINER_NOISE.get() && state.hasBlockEntity()
				&& serverLevel.getBlockEntity(event.getPos()) instanceof Container) {
			HearingApi.emit(serverLevel, Vec3.atCenterOf(event.getPos()), NoiseTypes.CONTAINER, event.getEntity());
		}
	}

	/**
	 * 方块被破坏时产生噪声；玻璃类方块（按 {@link SoundType#GLASS} 判定）走玻璃破碎类型喵~
	 *
	 * @param event 方块破坏事件喵~
	 */
	@SubscribeEvent
	public static void onBlockBreak(BlockEvent.BreakEvent event) {
		if(!(event.getLevel() instanceof ServerLevel serverLevel)) {
			return;
		}
		BlockState state = event.getState();
		Vec3 center = Vec3.atCenterOf(event.getPos());
		if(state.getSoundType(serverLevel, event.getPos(), event.getPlayer()) == SoundType.GLASS) {
			if(MISCTWFHearingConfig.ENABLE_GLASS_BREAK_NOISE.get()) {
				HearingApi.emit(serverLevel, center, NoiseTypes.GLASS_BREAK, event.getPlayer());
			}
			return;
		}
		if(MISCTWFHearingConfig.ENABLE_BLOCK_BREAK_NOISE.get()) {
			HearingApi.emit(serverLevel, center, NoiseTypes.BLOCK_BREAK, event.getPlayer());
		}
	}

	private static boolean isDoorLike(BlockState state) {
		return state.getBlock() instanceof DoorBlock
				|| state.getBlock() instanceof TrapDoorBlock
				|| state.getBlock() instanceof FenceGateBlock;
	}

	private VanillaNoiseEventHandler() {
	}
}
