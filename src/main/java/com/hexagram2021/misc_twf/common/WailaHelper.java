package com.hexagram2021.misc_twf.common;

import com.hexagram2021.misc_twf.common.block.MutantPotionCauldronBlock;
import com.hexagram2021.misc_twf.common.block.compat.MutantPotionCauldronProvider;
import com.hexagram2021.misc_twf.common.block.entity.MutantPotionCauldronBlockEntity;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

/**
 * Jade 兼容插件
 * @author liudongyu
 */
@WailaPlugin
public class WailaHelper implements IWailaPlugin {
	@Override
	public void register(IWailaCommonRegistration registration) {
		registration.registerBlockDataProvider(MutantPotionCauldronProvider.INSTANCE, MutantPotionCauldronBlockEntity.class);
	}

	@Override
	public void registerClient(IWailaClientRegistration registration) {
		registration.registerBlockComponent(MutantPotionCauldronProvider.INSTANCE, MutantPotionCauldronBlock.class);
	}
}
