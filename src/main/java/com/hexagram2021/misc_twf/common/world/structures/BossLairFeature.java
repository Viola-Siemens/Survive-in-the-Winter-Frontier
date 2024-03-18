package com.hexagram2021.misc_twf.common.world.structures;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

public class BossLairFeature extends StructureFeature<NoneFeatureConfiguration> {
	public BossLairFeature(Codec<NoneFeatureConfiguration> codec) {
		super(codec, PieceGeneratorSupplier.simple(BossLairFeature::checkLocation, BossLairFeature::generatePieces));
	}

	private static boolean checkLocation(PieceGeneratorSupplier.Context<NoneFeatureConfiguration> context) {
		return context.validBiomeOnTop(Heightmap.Types.OCEAN_FLOOR_WG);
	}

	private static void generatePieces(StructurePiecesBuilder builder, PieceGenerator.Context<NoneFeatureConfiguration> context) {

	}

	@Override
	public GenerationStep.Decoration step() {
		return GenerationStep.Decoration.STRONGHOLDS;
	}
}
