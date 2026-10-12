package com.hexagram2021.misc_twf_hearing.core;

import com.hexagram2021.misc_twf_hearing.api.NoiseSnapshot;
import com.hexagram2021.misc_twf_hearing.api.NoiseSpec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * 内部噪声对象喵~：可变（合并时就地更新），**不对外暴露**，外部只能拿到 {@link NoiseSnapshot}。
 *
 * <p>合并语义：{@code id}/{@code createdTick}/{@code sourceUuid}/{@code type} 保持不变；
 * {@code position} 更新为最新、{@code expireTick} 延长、{@code radius}/{@code strength} 取较大、
 * {@code lastEmitTick} 刷新喵~</p>
 *
 * @author liudongyu
 */
public final class NoisePulse {
	private final long id;
	private final ResourceLocation type;
	private final long createdTick;
	@Nullable
	private final UUID sourceUuid;
	private Vec3 position;
	private float radius;
	private float strength;
	private long lastEmitTick;
	private long expireTick;

	/**
	 * 构造一个新噪声喵~
	 *
	 * @param id           稳定 id喵~
	 * @param position     发生位置喵~
	 * @param spec         噪声规格喵~
	 * @param sourceUuid   来源实体 UUID，可能为空喵~
	 * @param now          当前游戏时间（tick）喵~
	 */
	public NoisePulse(long id, Vec3 position, NoiseSpec spec, @Nullable UUID sourceUuid, long now) {
		this.id = id;
		this.type = spec.type();
		this.createdTick = now;
		this.sourceUuid = sourceUuid;
		this.position = position;
		this.radius = spec.radius();
		this.strength = spec.strength();
		this.lastEmitTick = now;
		this.expireTick = now + spec.lifetimeTicks();
	}

	/**
	 * 以新的规格合并进本噪声喵~
	 *
	 * @param position 新的发生位置喵~
	 * @param spec     新的噪声规格喵~
	 * @param now      当前游戏时间（tick）喵~
	 */
	public void merge(Vec3 position, NoiseSpec spec, long now) {
		this.position = position;
		this.radius = Math.max(this.radius, spec.radius());
		this.strength = Math.max(this.strength, spec.strength());
		this.lastEmitTick = now;
		this.expireTick = Math.max(this.expireTick, now + spec.lifetimeTicks());
	}

	/**
	 * 判断本噪声是否已过期喵~
	 *
	 * @param now 当前游戏时间（tick）喵~
	 * @return 是否过期喵~
	 */
	public boolean isExpired(long now) {
		return now >= this.expireTick;
	}

	/**
	 * 转换为对外只读快照喵~
	 *
	 * @return 噪声快照喵~
	 */
	public NoiseSnapshot snapshot() {
		return new NoiseSnapshot(this.id, this.position, this.type, this.radius, this.strength,
				this.createdTick, this.lastEmitTick, this.expireTick, this.sourceUuid);
	}

	/**
	 * 噪声 ID
	 * @return 噪声 ID
	 */
	public long id() {
		return this.id;
	}

	/**
	 * 噪声产生位置
	 * @return 噪声产生位置
	 */
	public Vec3 position() {
		return this.position;
	}

	/**
	 * 噪声类型
	 * @return 噪声类型
	 */
	public ResourceLocation type() {
		return this.type;
	}

	/**
	 * 噪声传播半径
	 * @return 噪声传播半径
	 */
	public float radius() {
		return this.radius;
	}

	/**
	 * 噪声强度
	 * @return 噪声强度
	 */
	public float strength() {
		return this.strength;
	}

	/**
	 * 噪声产生时间（游戏刻）
	 * @return 噪声产生时间
	 */
	public long createdTick() {
		return this.createdTick;
	}

	/**
	 * 噪声最后散发时间（游戏刻）
	 * @return 噪声最后散发时间
	 */
	public long lastEmitTick() {
		return this.lastEmitTick;
	}

	/**
	 * 噪声过期时间（游戏刻）
	 * @return 噪声过期时间
	 */
	public long expireTick() {
		return this.expireTick;
	}

	/**
	 * 噪声源实体 UUID
	 * @return 噪声源实体 UUID
	 */
	@Nullable
	public UUID sourceUuid() {
		return this.sourceUuid;
	}
}
