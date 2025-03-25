package com.hexagram2021.misc_twf.common.world.features;

import com.hexagram2021.misc_twf.common.block.entity.MonsterEggBlockEntity;
import com.hexagram2021.misc_twf.common.register.MISCTWFBlocks;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

import java.util.List;

public class MonsterEggFeature extends Feature<MonsterEggFeature.MonsterEggFeatureConfiguration> {
	public MonsterEggFeature(Codec<MonsterEggFeatureConfiguration> codec) {
		super(codec);
	}

	@Override
	public boolean place(FeaturePlaceContext<MonsterEggFeatureConfiguration> context) {
		MonsterEggFeatureConfiguration config = context.config();
		WorldGenLevel level = context.level();
		BlockPos blockPos = context.origin();
		BlockState blockState = MISCTWFBlocks.MONSTER_EGG.defaultBlockState();
		BlockState origin = level.getBlockState(blockPos);
		if((origin.isAir() || origin.is(Blocks.WATER)) && blockState.canSurvive(level, blockPos)) {
			level.setBlock(blockPos, blockState, Block.UPDATE_CLIENTS);
			if(level.getBlockEntity(blockPos) instanceof MonsterEggBlockEntity monsterEggBlockEntity) {
				monsterEggBlockEntity.setEntries(WeightedRandomList.create(config.entries));
			}
			return true;
		}
		return false;
	}

	public record MonsterEggFeatureConfiguration(List<MonsterEggBlockEntity.MonsterEggEntry> entries) implements FeatureConfiguration {
		public static final Codec<MonsterEggFeatureConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				MonsterEggBlockEntity.MonsterEggEntry.CODEC.listOf().fieldOf("entries").forGetter(configuration -> configuration.entries)
		).apply(instance, MonsterEggFeatureConfiguration::new));
	}
}
