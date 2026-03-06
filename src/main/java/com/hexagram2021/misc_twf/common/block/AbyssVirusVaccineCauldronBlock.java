package com.hexagram2021.misc_twf.common.block;

import com.hexagram2021.misc_twf.common.ModVanillaCompat;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 深渊病毒疫苗炼药锅方块喵~
 * 这是一个特殊的炼药锅，用于容纳深渊病毒疫苗流体喵~
 *
 * @author liudongyu
 */
public class AbyssVirusVaccineCauldronBlock extends AbstractCauldronBlock {
	public static final MapCodec<AbyssVirusVaccineCauldronBlock> CODEC = simpleCodec(AbyssVirusVaccineCauldronBlock::new);

	/**
	 * 构造函数，创建一个深渊病毒疫苗炼药锅方块喵~
	 *
	 * @param properties 方块属性喵~
	 */
	public AbyssVirusVaccineCauldronBlock(Properties properties) {
		super(properties, ModVanillaCompat.ABYSS_VIRUS_VACCINE);
	}

	@Override
	protected MapCodec<AbyssVirusVaccineCauldronBlock> codec() {
		return CODEC;
	}

	/**
	 * 获取炼药锅内流体的高度喵~
	 *
	 * @param blockState 方块状态喵~
	 * @return 流体高度（0.9375D 表示满炼药锅）喵~
	 */
	@Override
	protected double getContentHeight(BlockState blockState) {
		return 0.9375D;
	}

	/**
	 * 检查炼药锅是否已满喵~
	 *
	 * @param blockState 方块状态喵~
	 * @return 总是返回 true，表示该炼药锅始终处于满状态喵~
	 */
	@Override
	public boolean isFull(BlockState blockState) {
		return true;
	}
}
