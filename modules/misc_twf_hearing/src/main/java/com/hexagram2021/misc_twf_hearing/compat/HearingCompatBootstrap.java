package com.hexagram2021.misc_twf_hearing.compat;

import com.hexagram2021.misc_twf_hearing.compat.superbwarfare.SuperBWarfareNoiseCompat;
import net.neoforged.fml.ModList;

/**
 * 第三方兼容层的引导类喵~：**本类不 import 任何第三方模组的类**。
 *
 * <p>只有确认对应模组已加载后，才会去解析并初始化其适配类；因此模组缺失时不会触发
 * {@code NoClassDefFoundError}，核心功能照常工作喵~</p>
 *
 * @author liudongyu
 */
public final class HearingCompatBootstrap {
	/**
	 * 按已加载的模组注册对应兼容层喵~
	 */
	public static void registerIfPresent() {
		if(ModList.get().isLoaded("superbwarfare")) {
			SuperBWarfareNoiseCompat.register();
		}
	}

	private HearingCompatBootstrap() {
	}
}
