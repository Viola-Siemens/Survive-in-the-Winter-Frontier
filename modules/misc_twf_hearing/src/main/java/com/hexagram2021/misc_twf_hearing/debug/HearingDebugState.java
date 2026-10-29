package com.hexagram2021.misc_twf_hearing.debug;

import com.google.common.collect.Sets;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * 循声调试状态的会话级容器喵~
 *
 * <p>只记录"哪些管理员打开了循声调试"：这是**运行时状态**，不写入配置文件、不落盘，
 * 服务器停止时清空；日志侧是否输出 DEBUG 仍由整合包 {@code log4j2.xml} 的级别决定喵~</p>
 *
 * @author liudongyu
 */
public final class HearingDebugState {
	private static final Set<UUID> ENABLED_PLAYERS = Sets.newHashSet();

	/**
	 * 切换指定玩家的调试状态喵~
	 *
	 * @param player 管理员玩家喵~
	 * @return 切换后是否处于开启状态喵~
	 */
	public static boolean toggle(ServerPlayer player) {
		UUID uuid = player.getUUID();
		if(ENABLED_PLAYERS.contains(uuid)) {
			ENABLED_PLAYERS.remove(uuid);
			return false;
		}
		ENABLED_PLAYERS.add(uuid);
		return true;
	}

	/**
	 * 判断指定玩家是否开启了循声调试喵~
	 *
	 * @param player 玩家喵~
	 * @return 是否开启喵~
	 */
	public static boolean isEnabled(ServerPlayer player) {
		return ENABLED_PLAYERS.contains(player.getUUID());
	}

	/**
	 * 判断当前是否有任何管理员开启了调试（用于避免无谓的消息构造）喵~
	 *
	 * @return 是否存在开启调试的管理员喵~
	 */
	public static boolean hasAny() {
		return !ENABLED_PLAYERS.isEmpty();
	}

	/**
	 * 向指定维度中开启了调试的管理员发送诊断信息（内容惰性构造）喵~
	 *
	 * @param level   服务端维度喵~
	 * @param message 消息内容提供者喵~
	 */
	public static void broadcast(ServerLevel level, Supplier<Component> message) {
		if(ENABLED_PLAYERS.isEmpty()) {
			return;
		}
		Component component = message.get();
		for(ServerPlayer player : level.players()) {
			if(ENABLED_PLAYERS.contains(player.getUUID())) {
				player.sendSystemMessage(component);
			}
		}
	}

	/**
	 * 清空全部调试状态（服务器停止时调用）喵~
	 */
	public static void clear() {
		ENABLED_PLAYERS.clear();
	}

	private HearingDebugState() {
	}
}
