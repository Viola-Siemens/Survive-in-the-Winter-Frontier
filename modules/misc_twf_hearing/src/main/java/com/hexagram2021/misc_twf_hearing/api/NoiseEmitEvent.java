package com.hexagram2021.misc_twf_hearing.api;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import org.jetbrains.annotations.Nullable;

/**
 * 噪声产生事件喵~
 *
 * <p>{@link Pre} 可取消、可修改位置与规格（但**不暴露内部噪声对象**）；{@link Post} 只读，
 * 表示噪声已经进入索引（或完成合并）喵~</p>
 *
 * @author liudongyu
 */
public abstract class NoiseEmitEvent extends Event {
	private final ServerLevel level;
	@Nullable
	private final Entity source;

	protected NoiseEmitEvent(ServerLevel level, @Nullable Entity source) {
		this.level = level;
		this.source = source;
	}

	/**
	 * 获取噪声所在维度喵~
	 *
	 * @return 服务端维度喵~
	 */
	public ServerLevel getLevel() {
		return this.level;
	}

	/**
	 * 获取噪声来源实体喵~
	 *
	 * @return 来源实体，可能为空喵~
	 */
	@Nullable
	public Entity getSource() {
		return this.source;
	}

	/**
	 * 噪声写入前事件喵~：可取消，可修改位置与规格喵~
	 *
	 * @author liudongyu
	 */
	public static final class Pre extends NoiseEmitEvent implements ICancellableEvent {
		private Vec3 position;
		private NoiseSpec spec;

		/**
		 * 构造函数
		 * @param level 世界
		 * @param position 位置
		 * @param spec 噪声规格
		 * @param source 声源
		 */
		public Pre(ServerLevel level, Vec3 position, NoiseSpec spec, @Nullable Entity source) {
			super(level, source);
			this.position = position;
			this.spec = spec;
		}

		/**
		 * 获取待写入的噪声位置喵~
		 *
		 * @return 噪声位置喵~
		 */
		public Vec3 getPosition() {
			return this.position;
		}

		/**
		 * 修改待写入的噪声位置喵~
		 *
		 * @param position 新的噪声位置（必须为有限值）喵~
		 */
		public void setPosition(Vec3 position) {
			this.position = position;
		}

		/**
		 * 获取待写入的噪声规格喵~
		 *
		 * @return 噪声规格喵~
		 */
		public NoiseSpec getSpec() {
			return this.spec;
		}

		/**
		 * 修改待写入的噪声规格喵~
		 *
		 * @param spec 新的噪声规格喵~
		 */
		public void setSpec(NoiseSpec spec) {
			this.spec = spec;
		}
	}

	/**
	 * 噪声写入后事件喵~：只读，不可取消喵~
	 *
	 * @author liudongyu
	 */
	public static final class Post extends NoiseEmitEvent {
		private final NoiseSnapshot snapshot;

		/**
		 * 构造函数
		 * @param level 世界
		 * @param snapshot 噪声快照
		 * @param source 声源
		 */
		public Post(ServerLevel level, NoiseSnapshot snapshot, @Nullable Entity source) {
			super(level, source);
			this.snapshot = snapshot;
		}

		/**
		 * 获取写入后的噪声快照喵~
		 *
		 * @return 噪声快照喵~
		 */
		public NoiseSnapshot getSnapshot() {
			return this.snapshot;
		}
	}
}
