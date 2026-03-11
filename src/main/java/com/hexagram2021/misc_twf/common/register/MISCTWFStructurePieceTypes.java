package com.hexagram2021.misc_twf.common.register;

import com.hexagram2021.misc_twf.common.world.structures.pieces.BossLairPieces;
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
	/** Boss 巢穴大厅组件类型喵~ */
	public static final StructurePieceType HALL_TYPE = register("boss_lair_hall", BossLairPieces.HallPiece::new);
	/** Boss 巢穴入口组件类型喵~ */
	public static final StructurePieceType START_TYPE = register("boss_lair_start", BossLairPieces.StartPiece::new);
	/** Boss 巢穴锅炉房组件类型喵~ */
	public static final StructurePieceType BOILER_ROOM_TYPE = register("boss_lair_boiler", BossLairPieces.BoilerRoomPiece::new);
	/** Boss 巢穴 Boss 房间组件类型喵~ */
	public static final StructurePieceType BOSS_ROOM_TYPE = register("boss_lair_boss", BossLairPieces.BossRoomPiece::new);
	/** Boss 巢穴楼梯组件类型喵~ */
	public static final StructurePieceType STAIRCASE_TYPE = register("boss_lair_staircase", BossLairPieces.StaircasePiece::new);
	/** Boss 巢穴墙壁组件类型喵~ */
	public static final StructurePieceType WALL_TYPE = register("boss_lair_wall", BossLairPieces.WallPiece::new);

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
