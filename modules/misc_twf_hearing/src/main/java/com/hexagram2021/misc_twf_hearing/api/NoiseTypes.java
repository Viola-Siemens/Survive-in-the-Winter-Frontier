package com.hexagram2021.misc_twf_hearing.api;

import com.hexagram2021.misc_twf_hearing.MiscTwfHearing;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

/**
 * 噪声类型常量表喵~
 *
 * <p>P0 只处理有明确玩法意义的行为，禁止监听所有 {@code SoundEvent} 后一律生成噪声；
 * 每种类型的半径、强度与存活区间均由服务端配置提供（见 {@code MISCTWFHearingConfig}）喵~</p>
 *
 * @author liudongyu
 */
public final class NoiseTypes {
	/** 疾跑脚步喵~ */
	public static final ResourceLocation SPRINT = id("sprint");
	/** 落地闷响喵~ */
	public static final ResourceLocation LAND = id("land");
	/** 开关门喵~ */
	public static final ResourceLocation DOOR = id("door");
	/** 容器交互喵~ */
	public static final ResourceLocation CONTAINER = id("container");
	/** 方块破坏喵~ */
	public static final ResourceLocation BLOCK_BREAK = id("block_break");
	/** 玻璃破碎喵~ */
	public static final ResourceLocation GLASS_BREAK = id("glass_break");
	/** 未消音枪声喵~ */
	public static final ResourceLocation GUNSHOT = id("gunshot");
	/** 消音枪声喵~ */
	public static final ResourceLocation SUPPRESSED_GUNSHOT = id("suppressed_gunshot");
	/** 爆炸喵~ */
	public static final ResourceLocation EXPLOSION = id("explosion");
	/** 第三方自定义噪声（未配置类型的回退项）喵~ */
	public static final ResourceLocation CUSTOM = id("custom");

	/** 全部噪声类型（顺序稳定，供配置与调试输出使用）喵~ */
	public static final List<ResourceLocation> ALL = List.of(
			SPRINT, LAND, DOOR, CONTAINER, BLOCK_BREAK, GLASS_BREAK, GUNSHOT, SUPPRESSED_GUNSHOT, EXPLOSION, CUSTOM
	);

	/**
	 * 构造本模块命名空间下的噪声类型 id 喵~
	 *
	 * @param path 类型路径名喵~
	 * @return 噪声类型 id 喵~
	 */
	private static ResourceLocation id(String path) {
		return ResourceLocation.fromNamespaceAndPath(MiscTwfHearing.CONTENT_NAMESPACE, path);
	}

	private NoiseTypes() {
	}
}
