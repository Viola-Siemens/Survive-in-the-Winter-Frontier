package com.hexagram2021.misc_twf.common;

import com.hexagram2021.misc_twf.common.block.MutantPotionCauldronBlock;
import com.hexagram2021.misc_twf.common.block.compat.MutantPotionCauldronProvider;
import com.hexagram2021.misc_twf.common.block.entity.MutantPotionCauldronBlockEntity;
import com.hexagram2021.misc_twf.common.entity.compat.LivingPoopProvider;
import com.hexagram2021.misc_twf.common.entity.compat.MobProduceMilkProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.goat.Goat;
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
		registration.registerEntityDataProvider(MobProduceMilkProvider.INSTANCE, Cow.class);
		registration.registerEntityDataProvider(MobProduceMilkProvider.INSTANCE, Goat.class);
		registration.registerEntityDataProvider(LivingPoopProvider.INSTANCE, LivingEntity.class);
	}

	@Override
	public void registerClient(IWailaClientRegistration registration) {
		registration.registerBlockComponent(MutantPotionCauldronProvider.INSTANCE, MutantPotionCauldronBlock.class);
		registration.registerEntityComponent(MobProduceMilkProvider.INSTANCE, Cow.class);
		registration.registerEntityComponent(MobProduceMilkProvider.INSTANCE, Goat.class);
		registration.registerEntityComponent(LivingPoopProvider.INSTANCE, LivingEntity.class);
	}
}
