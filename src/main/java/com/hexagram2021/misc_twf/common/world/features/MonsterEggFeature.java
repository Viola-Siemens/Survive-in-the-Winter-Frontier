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

/**
 * 怪物蛋世界生成特征类喵~
 * 负责在世界生成阶段放置怪物蛋方块，并为其配置可生成的怪物类型及其权重喵~
 * 怪物蛋只能在空气或水中生成，且必须满足方块的生存条件喵~
 *
 * @author liudongyu
 */
public class MonsterEggFeature extends Feature<MonsterEggFeature.MonsterEggFeatureConfiguration> {
	/**
	 * 构造怪物蛋特征喵~
	 *
	 * @param codec 特征配置的编解码器喵~
	 */
	public MonsterEggFeature(Codec<MonsterEggFeatureConfiguration> codec) {
		super(codec);
	}

	/**
	 * 尝试在指定位置放置怪物蛋方块喵~
	 * 如果目标位置是空气或水，且怪物蛋方块可以在该位置生存，则放置成功并配置怪物生成表喵~
	 *
	 * @param context 特征放置上下文，包含世界、位置和配置信息喵~
	 * @return 如果成功放置怪物蛋返回 true，否则返回 false 喵~
	 */
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

	/**
	 * 怪物蛋特征的配置类喵~
	 * 包含怪物蛋可以生成的怪物类型列表及其对应的权重喵~
	 *
	 * @param entries 怪物生成条目列表，每个条目包含一种实体类型和其生成权重喵~
	 *
	 * @author liudongyu
	 */
	public record MonsterEggFeatureConfiguration(List<MonsterEggBlockEntity.MonsterEggEntry> entries) implements FeatureConfiguration {
		public static final Codec<MonsterEggFeatureConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				MonsterEggBlockEntity.MonsterEggEntry.CODEC.listOf().fieldOf("entries").forGetter(configuration -> configuration.entries)
		).apply(instance, MonsterEggFeatureConfiguration::new));
	}
}
