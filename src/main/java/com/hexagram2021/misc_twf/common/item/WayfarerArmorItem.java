package com.hexagram2021.misc_twf.common.item;

import com.hexagram2021.misc_twf.common.config.MISCTWFCommonConfig;
import com.hexagram2021.misc_twf.common.register.MISCTWFArmorMaterials;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ArmorItem;

import javax.annotation.Nullable;

/**
 * 远行者护甲物品，不同部位在通电时可提供不同的药水效果喵~
 * <ul>
 *     <li>头盔 - 水下呼吸喵~</li>
 *     <li>胸甲 - 抗性提升喵~</li>
 *     <li>护腿 - 急迫喵~</li>
 *     <li>靴子 - 速度提升喵~</li>
 * </ul>
 *
 * @author liudongyu
 */
public class WayfarerArmorItem extends ArmorItem implements IEnergyItem {
	public static final String NAME = "wayfarer";

	/**
	 * 构造一个远行者护甲物品喵~
	 *
	 * @param type 护甲部位类型喵~
	 */
	public WayfarerArmorItem(ArmorItem.Type type) {
		super(MISCTWFArmorMaterials.WAYFARER, type, new Properties().stacksTo(1));
	}


	@Override
	public int getEnergyCapability() {
		return MISCTWFCommonConfig.WAYFARER_ARMOR_CAPABILITY.get();
	}

	@Override
	public int getMaxEnergyReceiveSpeed() {
		return 5;
	}

	@Override
	public int getMaxEnergyExtractSpeed() {
		return 1;
	}

	/**
	 * 根据护甲部位获取每 tick 应施加的药水效果喵~
	 *
	 * @return 对应部位的药水效果实例，若部位不匹配则返回 null 喵~
	 */
	@Nullable
	public MobEffectInstance getTickedEffect() {
		return switch (this.type.getSlot()) {
			case HEAD -> new MobEffectInstance(MobEffects.WATER_BREATHING, 40);
			case CHEST -> new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 40);
			case LEGS -> new MobEffectInstance(MobEffects.DIG_SPEED, 40);
			case FEET -> new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 40);
			default -> null;
		};
	}
}
