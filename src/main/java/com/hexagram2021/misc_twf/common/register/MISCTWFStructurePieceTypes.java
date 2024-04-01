package com.hexagram2021.misc_twf.common.register;

import com.hexagram2021.misc_twf.common.world.structures.pieces.BossLairPieces;
import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;

public final class MISCTWFStructurePieceTypes {
	public static final StructurePieceType HALL_TYPE = Registry.register(Registry.STRUCTURE_PIECE, "boss_lair_hall", BossLairPieces.HallPiece::new);
	public static final StructurePieceType START_TYPE = Registry.register(Registry.STRUCTURE_PIECE, "boss_lair_start", BossLairPieces.StartPiece::new);
	public static final StructurePieceType BOILER_ROOM_TYPE = Registry.register(Registry.STRUCTURE_PIECE, "boss_lair_boiler", BossLairPieces.BoilerRoomPiece::new);
	public static final StructurePieceType BOSS_ROOM_TYPE = Registry.register(Registry.STRUCTURE_PIECE, "boss_lair_boss", BossLairPieces.BossRoomPiece::new);
	public static final StructurePieceType WALL_TYPE = Registry.register(Registry.STRUCTURE_PIECE, "boss_lair_wall", BossLairPieces.WallPiece::new);

	private MISCTWFStructurePieceTypes() {
	}

	public static void init() {
	}
}
