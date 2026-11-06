package com.hexagram2021.misc_twf_adventure.common.config;

import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

/**
 * 冒险与探索模块的通用配置喵~
 *
 * <p>本轮按模块内引用重建最小实现：怪物蛋被踩/被压碎的触发概率、怪物蛋听声半径与衰减、
 * 以及 Just Leveling Fork 技能等级需求；缺失字段的语义按调用处推断，均为推断值并标了 TODO喵~</p>
 *
 * @author liudongyu
 */
public final class MISCTWFCommonConfig {
	private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

	/** 实体从上方落到怪物蛋上时压碎蛋的概率（0~1）喵~ */
	public static final ModConfigSpec.DoubleValue POSSIBILITY_FALL_DESTROY_EGG = BUILDER
			.comment("Probability (0~1) that a monster egg is destroyed when something falls onto it.")
			.defineInRange("monster_egg.possibility_fall_destroy", 0.05D, 0.0D, 1.0D);
	/** 实体踩踏/进入怪物蛋时压碎蛋的概率（0~1）喵~ */
	public static final ModConfigSpec.DoubleValue POSSIBILITY_STEP_DESTROY_EGG = BUILDER
			.comment("Probability (0~1) that a monster egg is destroyed when an entity steps on or inside it.")
			.defineInRange("monster_egg.possibility_step_destroy", 0.02D, 0.0D, 1.0D);
	/** 怪物蛋"听声"半径（格）喵~ */
	public static final ModConfigSpec.DoubleValue MONSTER_EGG_HEARING_RADIUS = BUILDER
			.comment("Radius (in blocks) within which monster eggs can hear noises emitted by the hearing module.")
			.defineInRange("monster_egg.hearing_radius", 8.0D, 0.0D, 64.0D);
	/** 怪物蛋听声强度衰减间隔（tick）喵~ */
	public static final ModConfigSpec.IntValue MONSTER_EGG_HEARING_DECAY_TICKS = BUILDER
			.comment("Ticks between two decay steps of the heard strength stored on a monster egg.")
			.defineInRange("monster_egg.hearing_decay_ticks", 40, 1, 12000);

	/** 强胃被动的等级需求列表喵~（TODO：数值为推断默认值，待玩法确认） */
	public static final ModConfigSpec.ConfigValue<List<? extends Integer>> STRONG_STOMACH_SKILL_LEVELS = BUILDER
			.comment("Level requirements of the strong stomach passive (Just Leveling Fork).")
			.defineListAllowEmpty("skills.strong_stomach_levels", List.of(1, 2, 3), object -> object instanceof Integer value && value > 0);
	/** 枪械精通被动的等级需求列表喵~（TODO：数值为推断默认值，待玩法确认） */
	public static final ModConfigSpec.ConfigValue<List<? extends Integer>> GUN_MASTERY_SKILL_LEVELS = BUILDER
			.comment("Level requirements of the gun mastery passive (Just Leveling Fork).")
			.defineListAllowEmpty("skills.gun_mastery_levels", List.of(1, 2, 3), object -> object instanceof Integer value && value > 0);

	private static final ModConfigSpec CONFIG_SPEC = BUILDER.build();

	/**
	 * 获取配置规格喵~
	 *
	 * @return 配置规格喵~
	 */
	public static ModConfigSpec getConfig() {
		return CONFIG_SPEC;
	}

	private MISCTWFCommonConfig() {
	}
}