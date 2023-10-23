package com.hexagram2021.misc_twf.common;

import com.hexagram2021.misc_twf.common.block.MutantPotionCauldronBlock;
import com.hexagram2021.misc_twf.common.block.compat.MutantPotionCauldronProvider;
import com.hexagram2021.misc_twf.common.block.entity.MutantPotionCauldronBlockEntity;
import com.hexagram2021.misc_twf.common.entity.compat.MobProduceMilkProvider;
import mcp.mobius.waila.api.*;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.goat.Goat;

@SuppressWarnings("UnstableApiUsage")
@WailaPlugin
public class WailaHelper implements IWailaPlugin {
	@Override
	public void register(IWailaCommonRegistration registration) {
		registration.registerBlockDataProvider(MutantPotionCauldronProvider.INSTANCE, MutantPotionCauldronBlockEntity.class);
		registration.registerEntityDataProvider(MobProduceMilkProvider.INSTANCE, Cow.class);
		registration.registerEntityDataProvider(MobProduceMilkProvider.INSTANCE, Goat.class);
	}

	@Override
	public void registerClient(IWailaClientRegistration registration) {
		registration.registerComponentProvider(MutantPotionCauldronProvider.INSTANCE, TooltipPosition.BODY, MutantPotionCauldronBlock.class);
		registration.registerComponentProvider(MobProduceMilkProvider.INSTANCE, TooltipPosition.BODY, Cow.class);
		registration.registerComponentProvider(MobProduceMilkProvider.INSTANCE, TooltipPosition.BODY, Goat.class);
	}
}
