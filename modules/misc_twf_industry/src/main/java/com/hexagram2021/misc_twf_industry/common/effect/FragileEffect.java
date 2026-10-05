package com.hexagram2021.misc_twf_industry.common.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

/**
 * 脆弱效果
 *
 * @author liudongyu
 */
public class FragileEffect extends MobEffect {
	/**
	 * 构造函数
	 */
	public FragileEffect() {
		super(MobEffectCategory.HARMFUL, 0xfad6f4);
	}

	/**
	 * 根据效果等级，获得伤害提升倍率<br/>
	 * <table style="border-collapse: collapse; width: 100%; border: 1px solid">
	 *     <tr>
	 *         <th style="border: 1px solid">等级</th>
	 *         <td style="border: 1px solid">1</td>
	 *         <td style="border: 1px solid">2</td>
	 *         <td style="border: 1px solid">3</td>
	 *         <td style="border: 1px solid">4</td>
	 *     </tr>
	 *     <tr>
	 *         <th style="border: 1px solid">伤害倍率</th>
	 *         <td style="border: 1px solid">125%</td>
	 *         <td style="border: 1px solid">150%</td>
	 *         <td style="border: 1px solid">175%</td>
	 *         <td style="border: 1px solid">200%</td>
	 *     </tr>
	 * </table>
	 *
	 * @param level 等级
	 * @return 伤害倍率
	 */
	public static float getDamageMultiplier(int level) {
		return 1.25F + (level * 0.25F);
	}
}
