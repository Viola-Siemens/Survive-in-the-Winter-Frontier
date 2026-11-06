package com.hexagram2021.misc_twf_adventure.common.register;

import com.hexagram2021.misc_twf_adventure.common.world.features.MonsterEggFeature;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.hexagram2021.misc_twf_adventure.MiscTwfAdventure.CONTENT_NAMESPACE;

/**
 * 冒险与探索模块的世界生成特征类型注册喵~
 *
 * <p>1.21 中 ConfiguredFeature / PlacedFeature 已改为数据驱动，因此这里只注册 `Feature` 类型本身，
 * 具体配置与放置由 `data/misc_twf/worldgen/**` 的 JSON 提供喵~</p>
 *
 * @author liudongyu
 */
public final class MISCTWFFeatures {
	private static final DeferredRegister<Feature<?>> REGISTER = DeferredRegister.create(Registries.FEATURE, CONTENT_NAMESPACE);

	/** 怪物蛋特征喵~ */
	public static final DeferredHolder<Feature<?>, MonsterEggFeature> MONSTER_EGG = REGISTER.register(
			"monster_egg", () -> new MonsterEggFeature(MonsterEggFeature.MonsterEggFeatureConfiguration.CODEC)
	);

	/**
	 * 注册特征类型喵~
	 *
	 * @param bus 模块事件总线喵~
	 */
	public static void init(IEventBus bus) {
		REGISTER.register(bus);
	}

	private MISCTWFFeatures() {
	}
}