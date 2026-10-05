package com.hexagram2021.misc_twf_wildlife.common;

import com.hexagram2021.misc_twf_wildlife.common.config.MISCTWFWildlifeConfig;
import com.hexagram2021.misc_twf_wildlife.common.entity.compat.LivingPoopProvider;
import com.hexagram2021.misc_twf_wildlife.common.entity.compat.MobProduceMilkProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.goat.Goat;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

/**
 * 农牧生态模块的 Jade 兼容插件（可选依赖：Jade 缺失时本类不会被加载）喵~
 *
 * @author liudongyu
 */
@WailaPlugin
public class WailaHelper implements IWailaPlugin {
	@Override
	public void register(IWailaCommonRegistration registration) {
		registration.registerEntityDataProvider(MobProduceMilkProvider.INSTANCE, Cow.class);
		registration.registerEntityDataProvider(MobProduceMilkProvider.INSTANCE, Goat.class);
		if(MISCTWFWildlifeConfig.ENABLE_ANIMAL_POOP.get()) {
			registration.registerEntityDataProvider(LivingPoopProvider.INSTANCE, LivingEntity.class);
		}
	}

	@Override
	public void registerClient(IWailaClientRegistration registration) {
		registration.registerEntityComponent(MobProduceMilkProvider.INSTANCE, Cow.class);
		registration.registerEntityComponent(MobProduceMilkProvider.INSTANCE, Goat.class);
		if(MISCTWFWildlifeConfig.ENABLE_ANIMAL_POOP.get()) {
			registration.registerEntityComponent(LivingPoopProvider.INSTANCE, LivingEntity.class);
		}
	}
}
