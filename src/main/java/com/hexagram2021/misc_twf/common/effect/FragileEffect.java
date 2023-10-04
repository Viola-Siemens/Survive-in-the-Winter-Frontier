package com.hexagram2021.misc_twf.common.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class FragileEffect extends MobEffect {
	public FragileEffect() {
		super(MobEffectCategory.HARMFUL, 0xbe209a);
	}

	/*  |   1  |   2  |   3  |   4  |
		+------+------+------+------+
		| 125% | 150% | 175% | 200% |
	 */
	public static float getDamageMultiplier(int level) {
		return 1.25F + (level * 0.25F);
	}
}
