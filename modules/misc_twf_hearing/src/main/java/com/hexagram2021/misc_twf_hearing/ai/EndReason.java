package com.hexagram2021.misc_twf_hearing.ai;

import net.minecraft.util.StringRepresentable;

/**
 * 循声 Goal 的结束原因喵~
 *
 * <p>用于诊断输出与后续行为分析；新增原因时在此扩展，避免使用裸字符串喵~</p>
 *
 * @author liudongyu
 */
public enum EndReason implements StringRepresentable {
	/**
	 * 已到达目的地（距离 ≤ arrival_distance）喵~
	 */
	ARRIVED("arrived"),
	/**
	 * 路径失败次数达到上限，放弃该目标喵~
	 */
	PATH_FAILED("path-failed"),
	/**
	 * 出现真实 Target，立即让路喵~
	 */
	TARGET("target"),
	/**
	 * 没有有效目的地（异常兜底）喵~
	 */
	NO_DESTINATION("no-destination"),
	/**
	 * 原因未知：通常是 Goal 被外部停止（例如被更高优先级的 MOVE Goal 抢占），未经过 canContinueToUse 判定喵~
	 */
	UNKNOWN("unknown");

	private final String key;

	EndReason(String key) {
		this.key = key;
	}

	@Override
	public String toString() {
		return this.key;
	}

	@Override
	public String getSerializedName() {
		return this.key;
	}
}
