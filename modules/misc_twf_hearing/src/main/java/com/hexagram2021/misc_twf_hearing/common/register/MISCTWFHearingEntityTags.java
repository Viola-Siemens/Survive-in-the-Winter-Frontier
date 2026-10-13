package com.hexagram2021.misc_twf_hearing.common.register;

import com.hexagram2021.misc_twf_hearing.MiscTwfHearing;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

/**
 * 循声模块的实体类型标签常量喵~
 *
 * <p>是否拥有听觉由数据 Tag 决定：`misc_twf:hearing_mobs` 为白名单、`misc_twf:hearing_blacklist` 为黑名单，
 * **黑名单优先**；不默认给任何实体内置听觉，必须显式进入白名单喵~</p>
 *
 * @author liudongyu
 */
public final class MISCTWFHearingEntityTags {
	/** 拥有听觉的实体类型喵~ */
	public static final TagKey<EntityType<?>> HEARING_MOBS = create("hearing_mobs");
	/** 明确排除听觉的实体类型（优先级高于白名单）喵~ */
	public static final TagKey<EntityType<?>> HEARING_BLACKLIST = create("hearing_blacklist");

	/**
	 * 构造本模块命名空间下的实体类型标签喵~
	 *
	 * @param path 标签路径喵~
	 * @return 实体类型标签喵~
	 */
	private static TagKey<EntityType<?>> create(String path) {
		return TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(MiscTwfHearing.CONTENT_NAMESPACE, path));
	}

	private MISCTWFHearingEntityTags() {
	}
}
