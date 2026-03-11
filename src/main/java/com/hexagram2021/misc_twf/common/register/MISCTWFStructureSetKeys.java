package com.hexagram2021.misc_twf.common.register;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.StructureSet;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;

/**
 * 模组结构集资源键注册类，定义所有结构集的 {@link ResourceKey} 喵~
 *
 * @author liudongyu
 */
public final class MISCTWFStructureSetKeys {
	/** Boss 巢穴结构集资源键喵~ */
	public static final ResourceKey<StructureSet> BOSS_LAIR = createKey("boss_lair");

	private MISCTWFStructureSetKeys() {
	}

	/**
	 * 创建结构集的资源键喵~
	 *
	 * @param name 结构集名称喵~
	 * @return 结构集资源键喵~
	 */
	@SuppressWarnings("SameParameterValue")
	private static ResourceKey<StructureSet> createKey(String name) {
		return ResourceKey.create(Registries.STRUCTURE_SET, ResourceLocation.fromNamespaceAndPath(MODID, name));
	}
}
