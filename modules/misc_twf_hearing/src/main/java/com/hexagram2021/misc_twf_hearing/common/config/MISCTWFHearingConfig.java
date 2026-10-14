package com.hexagram2021.misc_twf_hearing.common.config;

import com.hexagram2021.misc_twf_hearing.api.NoiseSpec;
import com.hexagram2021.misc_twf_hearing.api.NoiseTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;
import java.util.Map;

/**
 * 循声模块的服务端配置喵~
 *
 * <p>配置分三段：{@code [goal]} 循声 Goal 的行为参数、{@code [noise]} 噪声索引与合并参数、
 * {@code [noise_type.<类型>]} 各噪声类型的半径/强度/存活区间（默认值按"接近现实可闻距离"填写）喵~</p>
 *
 * <p>存活时长为区间：emit 时以 {@code min + random.nextInt(max - min)} 掷出确定值喵~</p>
 *
 * <p>本模块**不提供日志开关配置**：是否需要 DEBUG 级别的日志由整合包的 {@code log4j2.xml} 按级别控制，
 * 避免出现"配置说开、日志级别说关"的两套开关；{@code /hearing debug <player> on|off} 只影响
 * 该管理员在游戏内的聊天/动作栏诊断输出，属于会话级状态，不写入配置文件喵~</p>
 *
 * @author liudongyu
 */
public final class MISCTWFHearingConfig {
	private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

	// ==================== [goal] ====================
	/** 循声 Goal 的优先级（addGoal 时使用）喵~ */
	public static final ModConfigSpec.IntValue GOAL_PRIORITY = BUILDER
			.comment("Priority used when injecting HearNoiseGoal via goalSelector.addGoal.")
			.defineInRange("goal.priority", 6, 0, 10);
	/** 查询间隔（tick）喵~ */
	public static final ModConfigSpec.IntValue QUERY_INTERVAL_TICKS = BUILDER
			.comment("Base interval between two noise queries of the same mob (in ticks).")
			.defineInRange("goal.query_interval_ticks", 10, 1, 12000);
	/** 查询错峰抖动（tick）喵~ */
	public static final ModConfigSpec.IntValue QUERY_JITTER_TICKS = BUILDER
			.comment("Random extra delay added to each query interval to spread queries over ticks.")
			.defineInRange("goal.query_jitter_ticks", 4, 0, 12000);
	/** 重寻路间隔（tick）喵~ */
	public static final ModConfigSpec.IntValue REPATH_INTERVAL_TICKS = BUILDER
			.comment("Minimum interval between two path requests while investigating (in ticks).")
			.defineInRange("goal.repath_interval_ticks", 20, 1, 12000);
	/** 路径失败次数上限喵~ */
	public static final ModConfigSpec.IntValue MAX_PATH_FAILURES = BUILDER
			.comment("HearNoiseGoal gives up after this many failed path attempts.")
			.defineInRange("goal.max_path_failures", 3, 0, 100);
	/** 到达判定距离（格）喵~ */
	public static final ModConfigSpec.DoubleValue ARRIVAL_DISTANCE = BUILDER
			.comment("Distance (in blocks) at which the mob is considered to have reached the noise position.")
			.defineInRange("goal.arrival_distance", 3.0D, 0.5D, 64.0D);
	/** 调查移动速度修正喵~ */
	public static final ModConfigSpec.DoubleValue SPEED_MODIFIER = BUILDER
			.comment("Navigation speed modifier used while investigating.")
			.defineInRange("goal.speed_modifier", 1.0D, 0.1D, 4.0D);
	/** 切换新声音的评分阈值倍率喵~ */
	public static final ModConfigSpec.DoubleValue SWITCH_THRESHOLD = BUILDER
			.comment("A new noise replaces the current one only when newScore > currentScore * this value.")
			.defineInRange("goal.switch_threshold", 1.15D, 1.0D, 10.0D);
	/** 切换冷却（tick）喵~ */
	public static final ModConfigSpec.IntValue SWITCH_COOLDOWN_TICKS = BUILDER
			.comment("Minimum ticks between two noise switches.")
			.defineInRange("goal.switch_cooldown_ticks", 8, 0, 12000);
	/** 最低可调查评分喵~ */
	public static final ModConfigSpec.DoubleValue MIN_SCORE_THRESHOLD = BUILDER
			.comment("Noises whose score is below this value are ignored.")
			.defineInRange("goal.min_score_threshold", 0.10D, 0.0D, 1.0D);

	/** 各实体类型的听觉倍率覆盖（格式 `namespace:path=倍率`，未列出的实体为 1.0）喵~ */
	public static final ModConfigSpec.ConfigValue<List<? extends String>> ENTITY_HEARING_MULTIPLIERS = BUILDER
			.comment(
					"Per entity type hearing multiplier overrides, formatted as 'namespace:path=multiplier'.",
					"Entity types not listed here use 1.0. The whitelist itself is the entity_type tag misc_twf:hearing_mobs."
			).defineListAllowEmpty(
					"goal.entity_hearing_multipliers",
					List.of("minecraft:zombie=1.0", "minecraft:husk=0.9", "minecraft:drowned=0.8"),
					() -> "minecraft:zombie=1.0",
					object -> object instanceof String value && value.contains("=")
			);

	// ==================== [vanilla] ====================
	/** 是否启用疾跑脚步噪声喵~ */
	public static final ModConfigSpec.BooleanValue ENABLE_SPRINT_NOISE = BUILDER
			.comment("Set to false to disable sprint footstep noises.")
			.define("vanilla.enable_sprint", true);
	/** 疾跑噪声的产生间隔（tick）喵~ */
	public static final ModConfigSpec.IntValue SPRINT_EMIT_INTERVAL_TICKS = BUILDER
			.comment("Interval between two sprint footstep noises (in ticks).")
			.defineInRange("vanilla.sprint_emit_interval_ticks", 10, 1, 200);
	/** 是否启用落地噪声喵~ */
	public static final ModConfigSpec.BooleanValue ENABLE_LAND_NOISE = BUILDER
			.comment("Set to false to disable landing noises.")
			.define("vanilla.enable_land", true);
	/** 落地发声的最小下落距离（格）喵~ */
	public static final ModConfigSpec.DoubleValue LAND_MIN_DISTANCE = BUILDER
			.comment("Minimum fall distance (in blocks) required to emit a landing noise.")
			.defineInRange("vanilla.land_min_distance", 1.0D, 0.0D, 64.0D);
	/** 是否启用开关门噪声喵~ */
	public static final ModConfigSpec.BooleanValue ENABLE_DOOR_NOISE = BUILDER
			.comment("Set to false to disable door/trapdoor/fence gate noises.")
			.define("vanilla.enable_door", true);
	/** 是否启用容器交互噪声喵~ */
	public static final ModConfigSpec.BooleanValue ENABLE_CONTAINER_NOISE = BUILDER
			.comment("Set to false to disable container interaction noises.")
			.define("vanilla.enable_container", true);
	/** 是否启用方块破坏噪声喵~ */
	public static final ModConfigSpec.BooleanValue ENABLE_BLOCK_BREAK_NOISE = BUILDER
			.comment("Set to false to disable block breaking noises.")
			.define("vanilla.enable_block_break", true);
	/** 是否启用玻璃破碎噪声喵~ */
	public static final ModConfigSpec.BooleanValue ENABLE_GLASS_BREAK_NOISE = BUILDER
			.comment("Set to false to disable glass breaking noises.")
			.define("vanilla.enable_glass_break", true);
	/** 是否启用爆炸噪声喵~ */
	public static final ModConfigSpec.BooleanValue ENABLE_EXPLOSION_NOISE = BUILDER
			.comment("Set to false to disable explosion noises.")
			.define("vanilla.enable_explosion", true);

	// ==================== [noise] ====================
	/** 过期清理间隔（tick）喵~ */
	public static final ModConfigSpec.IntValue CLEANUP_INTERVAL_TICKS = BUILDER
			.comment("Interval of the periodic expired-noise cleanup (in ticks).")
			.defineInRange("noise.cleanup_interval_ticks", 20, 1, 12000);
	/** 单维度噪声上限喵~ */
	public static final ModConfigSpec.IntValue MAX_ACTIVE_PER_LEVEL = BUILDER
			.comment("Soft cap of active noises per level; oldest/weakest are dropped above it.")
			.defineInRange("noise.max_active_per_level", 4096, 1, 1048576);
	/** 单区块噪声上限喵~ */
	public static final ModConfigSpec.IntValue MAX_ACTIVE_PER_CHUNK = BUILDER
			.comment("Soft cap of active noises per chunk.")
			.defineInRange("noise.max_active_per_chunk", 128, 1, 65536);
	/** 合并时间窗口（tick）喵~ */
	public static final ModConfigSpec.IntValue MERGE_WINDOW_TICKS = BUILDER
			.comment("Two noises from the same source and type merge when emitted within this window.")
			.defineInRange("noise.merge_window_ticks", 5, 0, 200);
	/** 合并距离窗口（格）喵~ */
	public static final ModConfigSpec.DoubleValue MERGE_DISTANCE = BUILDER
			.comment("Two noises from the same source and type merge when emitted within this distance.")
			.defineInRange("noise.merge_distance", 4.0D, 0.0D, 64.0D);
	/** 监听点查询的最大扫描半径（格）喵~ */
	public static final ModConfigSpec.DoubleValue QUERY_MAX_RADIUS = BUILDER
			.comment("Maximum radius scanned around a listener when looking for audible noises (in blocks).",
					"Keep it >= the largest noise radius * hearingMultiplier, otherwise far noises are never found.")
			.defineInRange("noise.query_max_radius", 128.0D, 16.0D, 512.0D);

	// ==================== [noise_type.*] ====================
	/** 疾跑脚步喵~ */
	public static final NoiseTypeSettings SPRINT = defineType("sprint", 16.0D, 0.45D, 20, 40);
	/** 落地闷响喵~ */
	public static final NoiseTypeSettings LAND = defineType("land", 10.0D, 0.40D, 20, 40);
	/** 开关门喵~ */
	public static final NoiseTypeSettings DOOR = defineType("door", 12.0D, 0.50D, 30, 60);
	/** 容器交互喵~ */
	public static final NoiseTypeSettings CONTAINER = defineType("container", 8.0D, 0.35D, 30, 60);
	/** 方块破坏喵~ */
	public static final NoiseTypeSettings BLOCK_BREAK = defineType("block_break", 14.0D, 0.50D, 40, 80);
	/** 玻璃破碎喵~ */
	public static final NoiseTypeSettings GLASS_BREAK = defineType("glass_break", 20.0D, 0.65D, 40, 80);
	/** 未消音枪声（SW 存在时半径以枪械数据为准）喵~ */
	public static final NoiseTypeSettings GUNSHOT = defineType("gunshot", 48.0D, 1.00D, 60, 120);
	/** 消音枪声（SW 存在时半径取 max(4, soundRadius × 0.5)）喵~ */
	public static final NoiseTypeSettings SUPPRESSED_GUNSHOT = defineType("suppressed_gunshot", 16.0D, 0.70D, 60, 120);
	/** 爆炸喵~ */
	public static final NoiseTypeSettings EXPLOSION = defineType("explosion", 64.0D, 1.00D, 80, 160);
	/** 第三方自定义噪声喵~ */
	public static final NoiseTypeSettings CUSTOM = defineType("custom", 16.0D, 0.50D, 20, 40);

	private static final Map<ResourceLocation, NoiseTypeSettings> BY_TYPE = Map.of(
			NoiseTypes.SPRINT, SPRINT,
			NoiseTypes.LAND, LAND,
			NoiseTypes.DOOR, DOOR,
			NoiseTypes.CONTAINER, CONTAINER,
			NoiseTypes.BLOCK_BREAK, BLOCK_BREAK,
			NoiseTypes.GLASS_BREAK, GLASS_BREAK,
			NoiseTypes.GUNSHOT, GUNSHOT,
			NoiseTypes.SUPPRESSED_GUNSHOT, SUPPRESSED_GUNSHOT,
			NoiseTypes.EXPLOSION, EXPLOSION,
			NoiseTypes.CUSTOM, CUSTOM
	);

	private static final ModConfigSpec CONFIG_SPEC = BUILDER.build();

	/**
	 * 定义单个噪声类型的配置项喵~
	 *
	 * @param name        类型路径名（用于配置 section）喵~
	 * @param radius      半径默认值（格）喵~
	 * @param strength    强度默认值（0.0 ~ 1.0）喵~
	 * @param lifetimeMin 存活下界（tick，含）喵~
	 * @param lifetimeMax 存活上界（tick，不含）喵~
	 * @return 该类型的配置视图喵~
	 */
	private static NoiseTypeSettings defineType(String name, double radius, double strength, int lifetimeMin, int lifetimeMax) {
		String prefix = "noise_type." + name + ".";
		ModConfigSpec.DoubleValue radiusValue = BUILDER
				.comment("Audible base radius in blocks (effective radius = radius * hearingMultiplier).")
				.defineInRange(prefix + "radius", radius, 0.5D, 512.0D);
		ModConfigSpec.DoubleValue strengthValue = BUILDER
				.comment("Investigation value used to compare noises for the same mob (0.0 ~ 1.0).")
				.defineInRange(prefix + "strength", strength, 0.0D, 1.0D);
		ModConfigSpec.IntValue minValue = BUILDER
				.comment("Minimum lifetime in ticks; the actual lifetime is rolled in [min, max).")
				.defineInRange(prefix + "lifetime_min", lifetimeMin, 1, 24000);
		ModConfigSpec.IntValue maxValue = BUILDER
				.comment("Maximum lifetime in ticks (exclusive).")
				.defineInRange(prefix + "lifetime_max", lifetimeMax, 2, 24000);
		return new NoiseTypeSettings(radiusValue, strengthValue, minValue, maxValue);
	}

	/**
	 * 获取指定噪声类型的配置视图（未配置类型回退到 {@code custom}）喵~
	 *
	 * @param type 噪声类型 id喵~
	 * @return 该类型的配置视图喵~
	 */
	public static NoiseTypeSettings getSettings(ResourceLocation type) {
		return BY_TYPE.getOrDefault(type, CUSTOM);
	}

	/**
	 * 按类型配置掷出一个噪声规格喵~
	 *
	 * @param type   噪声类型 id喵~
	 * @param random 随机源喵~
	 * @return 噪声规格喵~
	 */
	public static NoiseSpec rollSpec(ResourceLocation type, RandomSource random) {
		return getSettings(type).rollSpec(type, random);
	}

	/**
	 * 按 {@link NoiseTypes#ALL} 的稳定顺序返回全部类型配置视图喵~
	 *
	 * @return 全部类型配置视图喵~
	 */
	public static List<NoiseTypeSettings> allSettings() {
		return NoiseTypes.ALL.stream().map(MISCTWFHearingConfig::getSettings).toList();
	}

	/**
	 * 获取配置规格喵~
	 *
	 * @return 配置规格喵~
	 */
	public static ModConfigSpec getConfig() {
		return CONFIG_SPEC;
	}

	private MISCTWFHearingConfig() {
	}
}
