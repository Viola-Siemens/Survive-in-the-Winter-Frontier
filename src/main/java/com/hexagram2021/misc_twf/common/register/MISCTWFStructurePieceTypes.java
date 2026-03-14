package com.hexagram2021.misc_twf.common.register;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;

/**
 * 模组结构组件类型注册类，负责注册 Boss 巢穴各部分的结构组件类型喵~
 *
 * @author liudongyu
 */
public final class MISCTWFStructurePieceTypes {

	private MISCTWFStructurePieceTypes() {
	}

	/**
	 * 将结构组件类型注册到内置注册表喵~
	 *
	 * @param name 结构组件名称喵~
	 * @param type 结构组件类型喵~
	 * @return 注册后的结构组件类型喵~
	 */
	private static StructurePieceType register(String name, StructurePieceType type) {
		return Registry.register(BuiltInRegistries.STRUCTURE_PIECE, ResourceLocation.fromNamespaceAndPath(MODID, name), type);
	}

	/**
	 * 触发类加载以完成静态初始化喵~
	 */
	public static void init() {
	}
}
