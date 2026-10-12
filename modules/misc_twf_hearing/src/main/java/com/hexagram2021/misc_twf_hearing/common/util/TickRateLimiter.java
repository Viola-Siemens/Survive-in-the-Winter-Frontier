package com.hexagram2021.misc_twf_hearing.common.util;

/**
 * 按游戏时间（tick）限频的小工具喵~
 *
 * <p>刻意设计为**值对象**：游标由持有者（如每个维度的 {@code NoiseManager}）自己保存，
 * 因此不同业务、不同维度的告警互不干扰，也不会跨存档/跨世界残留喵~</p>
 *
 * <p>线程约束：与调用方一致（本模块只在服务端主线程使用）喵~</p>
 *
 * @author liudongyu
 */
public final class TickRateLimiter {
	private final long intervalTicks;
	private long lastTick = Long.MIN_VALUE;

	/**
	 * 构造限频器喵~
	 *
	 * @param intervalTicks 限频窗口（tick），必须为正数喵~
	 */
	public TickRateLimiter(long intervalTicks) {
		if(intervalTicks <= 0L) {
			throw new IllegalArgumentException("rate limit interval must be > 0, but was " + intervalTicks);
		}
		this.intervalTicks = intervalTicks;
	}

	/**
	 * 尝试获取一次输出许可（获取成功会刷新游标）喵~
	 *
	 * @param nowTick 当前游戏时间（tick）喵~
	 * @return 是否允许本次输出喵~
	 */
	public boolean tryAcquire(long nowTick) {
		if(this.lastTick != Long.MIN_VALUE && nowTick - this.lastTick < this.intervalTicks) {
			return false;
		}
		this.lastTick = nowTick;
		return true;
	}

	/**
	 * 重置游标喵~
	 */
	public void reset() {
		this.lastTick = Long.MIN_VALUE;
	}
}
