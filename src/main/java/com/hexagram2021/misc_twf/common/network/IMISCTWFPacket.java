package com.hexagram2021.misc_twf.common.network;

import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * MISC TWF 模组网络数据包的基础接口喵~
 * 所有自定义数据包都应该实现此接口，以提供统一的处理机制喵~
 *
 * @author liudongyu
 */
public interface IMISCTWFPacket {
	/**
	 * 处理数据包的核心方法喵~
	 * 在接收到数据包后，会在相应的线程上调用此方法来执行数据包的逻辑喵~
	 *
	 * @param context 数据包的上下文，包含玩家、网络连接等信息喵~
	 */
	void handle(IPayloadContext context);
}
