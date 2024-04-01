package com.hexagram2021.misc_twf.common.world.structures;

import com.hexagram2021.misc_twf.common.world.structures.pieces.BossLairPieces;
import com.mojang.serialization.Codec;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

import java.util.List;

public class BossLairFeature extends StructureFeature<NoneFeatureConfiguration> {
	public BossLairFeature(Codec<NoneFeatureConfiguration> codec) {
		super(codec, PieceGeneratorSupplier.simple(PieceGeneratorSupplier.checkForBiomeOnTop(Heightmap.Types.OCEAN_FLOOR_WG), BossLairFeature::generatePieces));
	}

	private static void generatePieces(StructurePiecesBuilder builder, PieceGenerator.Context<NoneFeatureConfiguration> context) {
		BossLairPieces.StartPiece startPiece = new BossLairPieces.StartPiece(context.random(), context.chunkPos().getBlockX(2), context.chunkPos().getBlockZ(2));
		builder.addPiece(startPiece);
		startPiece.addChildren(startPiece, builder, context.random());
		List<StructurePiece> list = startPiece.pendingChildren;
		while(!list.isEmpty()) {
			int rank = context.random().nextInt(list.size());
			StructurePiece piece = list.remove(rank);
			piece.addChildren(startPiece, builder, context.random());
		}
	}

	@Override
	public GenerationStep.Decoration step() {
		return GenerationStep.Decoration.VEGETAL_DECORATION;
	}
}
