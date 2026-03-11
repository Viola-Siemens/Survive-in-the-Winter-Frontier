package com.hexagram2021.misc_twf.common.world.structures;

import com.hexagram2021.misc_twf.common.register.MISCTWFStructureTypes;
import com.hexagram2021.misc_twf.common.world.structures.pieces.BossLairPieces;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

import java.util.List;
import java.util.Optional;

/**
 * Boss 巢穴结构特征，负责生成地下 Boss 巢穴结构喵~
 *
 * @author liudongyu
 */
public class BossLairFeature extends Structure {
	/** Boss 巢穴结构的 MapCodec 编解码器喵~ */
	public static final MapCodec<BossLairFeature> CODEC = simpleCodec(BossLairFeature::new);

	/**
	 * 构造 Boss 巢穴结构实例喵~
	 *
	 * @param settings 结构设置喵~
	 */
	public BossLairFeature(Structure.StructureSettings settings) {
		super(settings);
	}

	@Override
	protected Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
		return Optional.of(new Structure.GenerationStub(context.chunkPos().getWorldPosition(), builder -> generatePieces(builder, context)));
	}

	/**
	 * 生成 Boss 巢穴的所有结构组件喵~
	 *
	 * @param builder 结构组件构建器喵~
	 * @param context 生成上下文喵~
	 */
	private static void generatePieces(StructurePiecesBuilder builder, GenerationContext context) {
		BlockPos blockpos = new BlockPos(context.chunkPos().getBlockX(2), 0, context.chunkPos().getBlockZ(2));
		BossLairPieces.StartPiece startPiece = new BossLairPieces.StartPiece(context.random(), blockpos.getX(), blockpos.getZ());
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
	public StructureType<BossLairFeature> type() {
		return MISCTWFStructureTypes.BOSS_LAIR.get();
	}
}
