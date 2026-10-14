package com.hexagram2021.misc_twf_hearing.compat.superbwarfare;

import com.atsuishio.superbwarfare.api.event.ShootEvent;
import com.atsuishio.superbwarfare.data.gun.GunData;
import com.atsuishio.superbwarfare.data.gun.value.AttachmentType;
import com.hexagram2021.misc_twf_hearing.api.HearingApi;
import com.hexagram2021.misc_twf_hearing.api.NoiseTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.Nullable;

/**
 * Superb Warfare 枪声适配层喵~：只负责"服务端确认真实开火 → 读取实际噪声参数 → emit"。
 *
 * <p>挂接点选择 {@code ShootEvent.Post}：它广播于服务端开火总入口之后，此时弹药/过热校验已通过，
 * 空枪与客户端动画都不会误触发；**禁止**从客户端音效或 {@code SoundEvent} 反推开枪喵~</p>
 *
 * <p>半径取值：未消音用 {@code GunData.compute().soundRadius}（= JSON {@code SoundRadius} + 枪管修正）；
 * 消音（{@code AttachmentType.BARREL == 2}）时 SBW 的该值只是"加得少"，并不代表真实噪声半径，
 * 因此按业务决策取 {@code max(4, soundRadius × 0.5)}喵~</p>
 *
 * @author liudongyu
 */
public final class SuperBWarfareNoiseCompat {
	/** 消音枪声的最小半径（格）喵~ */
	private static final double MIN_SUPPRESSED_RADIUS = 4.0D;

	/**
	 * 注册适配层到游戏事件总线喵~（仅在 superbwarfare 已加载时由引导类调用）
	 */
	public static void register() {
		NeoForge.EVENT_BUS.register(new Listener());
	}

	private SuperBWarfareNoiseCompat() {
	}

	/**
	 * SBW 开火事件监听器喵~
	 *
	 * @author liudongyu
	 */
	private static final class Listener {
		/**
		 * 服务端确认开火后产生对应类型的枪声噪声喵~
		 *
		 * @param event SBW 开火后事件喵~
		 */
		@SubscribeEvent
		public void onShoot(ShootEvent.Post event) {
			ServerLevel level = event.getLevel();
			Entity shooter = event.getShooter();
			GunData data = event.getData();
			if(level == null || level.isClientSide() || shooter == null || data == null) {
				return;
			}
			boolean suppressed = data.attachment.get(AttachmentType.BARREL) == 2;
			double rawRadius = data.compute().soundRadius;
			ResourceLocation type = suppressed ? NoiseTypes.SUPPRESSED_GUNSHOT : NoiseTypes.GUNSHOT;
			float radius = (float) (suppressed ? Math.max(MIN_SUPPRESSED_RADIUS, rawRadius * 0.5D) : rawRadius);
			if(radius <= 0.0F) {
				return;
			}
			HearingApi.emit(level, shooter.position(), type, radius, shooter);
		}
	}

	/**
	 * 解析枪声来源实体（供调试/外部 API 使用，不参与目标选择）喵~
	 *
	 * @param shooter 射手喵~
	 * @return 射手本身喵~
	 */
	@Nullable
	public static Entity resolveShooter(@Nullable Entity shooter) {
		return shooter;
	}
}
