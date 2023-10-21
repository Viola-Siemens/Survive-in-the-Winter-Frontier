package com.hexagram2021.misc_twf.common.block;

import com.hexagram2021.misc_twf.common.ModVanillaCompat;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;

public class AbyssVirusVaccineCauldronBlock extends AbstractCauldronBlock {
	public AbyssVirusVaccineCauldronBlock(Properties properties) {
		super(properties, ModVanillaCompat.ABYSS_VIRUS_VACCINE);
	}

	@Override
	protected double getContentHeight(BlockState p_153500_) {
		return 0.9375D;
	}

	@Override
	public boolean isFull(BlockState blockState) {
		return true;
	}
}
