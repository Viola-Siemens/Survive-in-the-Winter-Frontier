# 纯循声系统（Hearing）需求工程摘要

> 依据文档：`C:\Users\liudongyu\Downloads\hearing.md`（2347 行，标题《冬境边域——纯循声系统技术需求（开发审阅版）》，文档状态：开发评审稿 v2.0）
> 对照旧实现：`C:\Projects\Sources-1.18.2\com\scarasol\sona\`（Sona，1.18.2 Forge）
> 对照适配目标：`C:\Projects\Sources-1.21.1\com\atsuishio\superbwarfare\`（Superb Warfare，1.21.1 NeoForge）
> 目标平台：Minecraft 1.21.1 / NeoForge 21.1.x
> 本文仅为工程摘要与差异梳理，不含实现；行内 `§n` 指需求文档章节号。

---

## 1. 范围与非目标

### 1.1 一句话目标

只回答两个问题：**怪物能不能听见这个声音**、**听见以后去哪个坐标调查**；实现链路为「有意义的游戏行为 → 服务端产生 `NoisePulse` → `NoiseManager` 记录 → 正常 AI Tick 的怪物查询附近噪声 → 选择一个值得调查的声音位置 → 用当前 `Navigation` 前往该坐标 → 到达 / 过期 / 失败 / 被更高优先级行为接管 → 循声结束」（§1）。

核心原则（§5 参考文献抬头、§50）：

> **我只告诉怪物哪里响了，不告诉怪物谁在那里。**

### 1.2 明确不做（非目标清单）

需求 §2「明确不迁移的 Sona 功能」逐条列出，全部**不迁移**：

```text
EXPOSURE MobEffect
CAMOUFLAGE
SoundDecoy Entity
TargetGoal 修改
怪物视觉修改
FOLLOW_RANGE 修改
中立生物逻辑
感染系统
玩家隐蔽系统
尸潮系统
破门/破墙逻辑
群体警觉
SEARCH 状态
额外攻击 AI
额外目标选择系统
```

§1「不负责」清单同义补充：判断发声者并设为敌人、搜索房间、视觉发现、攻击、追击、爬行、破门、破墙、群体警觉、战斗协同。

§0 兼容原则再次固化：

```text
不新增 Target 系统
不修改视觉
不替换 Navigation
不修改 PathFinder
不强制唤醒 ServerCore inactive Mob
不扫描全维度 Mob
不生成 SoundDecoy 实体
不恢复 EXPOSURE/CAMOUFLAGE 体系
```

§4「绝对兼容边界」把上述约束升级为硬性禁令：

| 编号 | 禁令 | 具体内容 |
| --- | --- | --- |
| §4.1 | 禁止 Target 操作 | 严禁 `mob.setTarget(...)`；严禁 `mob.targetSelector.addGoal(...)`；严禁向 `targetSelector` 加入任何循声逻辑。听见声音后最多只能保存 `Vec3 heardPosition`；声音来源 UUID 仅用于噪声管理、调试或外部 API，**不允许被 `HearNoiseGoal` 自动转换为攻击目标** |
| §4.2 | 禁止修改 AI / 寻路底层 | `Mob#getTarget`、`TargetGoal`、`NearestAttackableTargetGoal`、`TargetingConditions`、`Sensing`、`Attributes.FOLLOW_RANGE`、`PathFinder`、`PathNavigation`、`NodeEvaluator`、`MoveControl`、`LookControl`、`GoalSelector tick`、`Activation Range` |
| §4.3 | 禁止删除 / 重排其他 Goal | 不得 `removeGoal(...)`、`removeAllGoals(...)`，不得为了循声重新排序其他模组 Goal |

§2 进一步给出裁决原则：若开发中发现「必须重新引入 EXPOSURE / TargetGoal / Sensing / PathFinder Mixin 才能实现」，应**优先重新评估设计**，而不是扩大项目边界。

§3.2 补充：Improved Mobs 可能动态增加 `ItemUseGoal`、`StealGoal`、`BlockBreakGoal`、`LadderClimbGoal`、`TargetGoal`，并介入 `TargetingConditions`、`Sensing`、`PathNavigation`、`NodeEvaluator`、`PathFindingContext`，因此循声系统**严禁为了兼容而 Mixin 这些核心类**。

§3.4 / §3.5 补充：允许 Hordes 生成的、符合 Tag 的感染者自然获得 `HearNoiseGoal`，但不得修改 Horde 生成 / AI / 感染机制；CrawlMod 的高优先级爬行行为必须自然抢占循声，循声不得强制站立、修改 Pose、修改 Crawl 状态、修改移动控制，或为了「继续循声」强行取消 `CrawlGoal`。

### 1.3 核心模块依赖禁令

§5 要求核心模块**不得 import**：`ZombieTactics2`、`ImprovedMobs`、`ServerCore`、`TheHordes`、`CrawlMod`、`SuperbWarfare`。第三方兼容只能位于 `compat/`。

§37 给出稳定边界：稳定包 `winter_frontier.hearing.api`，允许外部依赖 `HearingApi`、`NoiseSpec`、`NoiseSnapshot`、`NoiseEmitEvent`；内部包 `hearing.core`、`hearing.ai`、`hearing.internal`、`hearing.compat` 不承诺二进制稳定；`misc_twf` 只能 import `hearing.api.*`。

---

## 2. 核心数据模型

### 2.1 `NoisePulse`（§7）

文档建议的 record 形态：

```java
public record NoisePulse(
        long id,
        Vec3 position,
        ResourceLocation type,
        float radius,
        float strength,
        long createdTick,
        long expireTick,
        @Nullable UUID sourceUuid
) {}
```

字段语义与规则：

| 字段 | 类型 | 语义 / 规则 |
| --- | --- | --- |
| `id` | `long` | 新增字段，**每个 `ServerLevel` 内单调递增**即可，不需要跨重启稳定。用途：`HearingMemory` 判定当前跟踪的是不是同一个 Pulse；合并后保持同一 ID；调试日志定位；避免用 position/type/tick 判断导致误判（§7.1） |
| `position` | `Vec3` | 声音发生时的位置**快照**；来源移动后 `NoisePulse.position` **不跟随来源移动**（§7.2） |
| `type` | `ResourceLocation` | 声音类型；§34 第一版类型集合：`SPRINT`、`LAND`、`DOOR`、`CONTAINER`、`BLOCK_BREAK`、`GLASS_BREAK`、`GUNSHOT`、`SUPPRESSED_GUNSHOT`、`EXPLOSION`、`CUSTOM` |
| `radius` | `float` | 基础传播半径，**不等于 `FOLLOW_RANGE`**；实际可听距离 `effectiveRadius = noise.radius × hearingMultiplier`（§7.3） |
| `strength` | `float` | 仅用于同一怪物对多个可听声音做价值比较；**不是**攻击优先级 / 仇恨值 / Target 权重。建议范围 `0.0 ~ 1.0`；允许 API 输入超过 1，但**推荐 P0 直接 clamp 到 0.0F ~ 1.0F**，减少异常配置（§7.4） |
| `createdTick` | `long` | 首次创建 tick；合并时保留第一次创建时间（§12.3） |
| `expireTick` | `long` | 过期 tick，必须 `expireTick > createdTick`（§7.5） |
| `sourceUuid` | `@Nullable UUID` | 来源实体 UUID；可为空（方块 / 爆炸 / 机关） |

§12.3 额外**推荐新增**字段 `long lastEmitTick`，并明确「更推荐内部 Pulse 使用 `createdTick` / `lastEmitTick` / `expireTick` 三件套」，公开 Snapshot 同样暴露 `lastEmitTick`。理由：freshness 计算用 `lastEmitTick`，统计/调试仍可看到最初创建时间；若不想扩大 record，也可把 `createdTick` 定义为「最近一次有效发声时间」，但名称容易误导。

### 2.2 生命周期（lifetime，§7.5）

`expireTick > createdTick`，推荐按类别设定：

```text
脚步/落地       20~40 tick
开门/容器       30~60 tick
方块破坏        40~80 tick
枪声             60~120 tick
爆炸             80~160 tick
```

**最终数值应配置化，不硬编码在 Goal。**

### 2.3 来源类型

需求未给出封闭的「来源枚举」，而是由 `type`（`ResourceLocation`）+ `sourceUuid` 组合表达：

- **玩家**：`SPRINT`（§34、§43「玩家奔跑」）、`LAND`、武器相关类型（经 §33 SBW 适配层）；
- **生物**：与玩家共用脚步 / 落地类型（文档未单列「生物」专属类型）；
- **方块**：`DOOR`、`CONTAINER`、`BLOCK_BREAK`、`GLASS_BREAK`（§34）；
- **爆炸**：`EXPLOSION`（原版爆炸与 SBW 爆炸武器，§33.3、§44.7）；
- **SBW 枪声**：`GUNSHOT`、`SUPPRESSED_GUNSHOT`（§33.3 明确第一版至少区分这三类：`GUNSHOT` / `SUPPRESSED_GUNSHOT` / `EXPLOSION`）；
- **其它**：`CUSTOM`——`emit(level, position, type, radius, source)` 简化重载中，**若 type 未配置，推荐使用 `CUSTOM` 默认值，而不是异常退出**（§10.1）。

§34 同时给出禁止项：**禁止监听所有 `SoundEvent` 然后一律生成 Noise**。UI 声音、环境 BGM、客户端局部声音、其他模组装饰性音效都不应默认引发怪物调查。

### 2.4 维度与坐标

- 每个 `ServerLevel` 必须拥有**独立的** `NoiseManager`（§8），逻辑入口 `NoiseManager.get(serverLevel)`。
- 坐标即 `position` 快照，不做传播范围展开存储（见 §9.1）。
- §24 的 Goal 结束条件包含「当前维度发生异常变化」。

### 2.5 `NoiseManager` 职责与生命周期（§8）

| 维度 | 规定 |
| --- | --- |
| 存储方式 | 允许实现为 **NeoForge Level Attachment**，或**生命周期明确绑定 `ServerLevel` 的内部管理器**。**不推荐** `static Map<ResourceKey<Level>, NoiseManager>`；若使用静态 Map，必须实现维度卸载 / 服务器停止清理，否则存在开发环境重进世界或多次开服时残留风险（§8.1） |
| 持久化 | **不持久化**：不写 NBT、不进入 `SavedData`、不保存到区块；服务器重启后全部消失；维度卸载后全部释放。这是预期行为。§8.1 补充：若采用 Attachment，**不要提供持久化 serializer**（纯运行时数据） |
| Tick 清理 | 过期噪声不能只依赖「有人查询时顺便删除」。推荐 `ServerTickEvent.Post` → 每 N tick 对已存在的 NoiseManager 做一次轻量清理；**推荐默认 `cleanupInterval = 20 tick`**。也允许「查询时惰性删除 + 每秒兜底清理」（§8.3） |
| 禁止 | 每 tick 对每个 chunk 做全表扫描（§8.3） |
| 容量保护 | §39：`maxActivePulsesPerLevel = 4096`、`maxActivePulsesPerChunk = 128`（软性上限，防第三方 API 滥用）。超过时：① 先清理过期；② 再删除最旧、最弱的 Pulse；③ rate-limited warning 日志；**不要直接崩溃**。属防御性设计，不是正常玩法限制 |
| 线程约束 | §38：`HearingApi` 为**服务端主线程 API**；异步线程调用时推荐「开发环境日志警告」且**不允许异步直接修改 NoiseManager**；不要为了兼容异步调用把内部结构改成并发容器 |
| 实体死亡清理 | 文档未单列「实体死亡」清理时机；`§36 resolveSource` 明确允许因实体死亡 / 换维度 / 卸载而返回 empty，噪声本身按 `expireTick` 过期 |

### 2.6 `NoiseSpatialIndex`（§9）

```text
ChunkPos.asLong(chunkX, chunkZ)
        ↓
Long2ObjectMap<List<NoisePulse>>
```

- 内部可优先使用 **fastutil**，减少装箱开销（§9）。
- **索引原则（§9.1）**：Pulse **只放入「声音源位置所在 Chunk」的桶**中，**不需要按传播半径复制到所有周围 Chunk**。怪物查询时根据自身最大有效听觉范围计算需扫描的 Chunk 范围：

```text
listener = (x, z)
effectiveMaxRadius = 当前允许查询的最大声音范围
chunkRadius = ceil(effectiveMaxRadius / 16)
```

然后只遍历这些**已加载 / 已存在**的索引桶。

- **不主动加载区块（§9.2）**：查询噪声记录本身不应访问世界方块，也不应调用任何会同步加载新区块的 API；`NoiseSpatialIndex` 只能查询自己已持有的内存数据。若后续 P1 做墙体衰减，需**单独重新审查**「是否访问未加载区块」。

### 2.7 声音合并规则（§12）

动机：连续自动武器若每 2 tick 产生独立 Pulse，会快速增加索引数量。

原规则保留：

```text
同 source
同 type
距离 <= 4
时间 <= 5 tick
```

两个关键限制：

- **§12.1 有 `sourceUuid` 的声音**：只有「`sourceUuid` 相同 + `type` 相同 + 时间窗口内 + 距离窗口内」才自动合并。
- **§12.2 `sourceUuid == null`**：**不要因为两个声音都没有 `sourceUuid` 就自动认定为同一来源**，否则两个相邻方块、两个爆炸、两个机关可能被错误合并。**P0 推荐：匿名 Noise 默认不跨 emit 自动合并**；后续若确有「方块连续发声」需求，再扩展可选 `mergeKey`，不要现在为了泛化把 API 做复杂。

合并后更新规则（§12.3）：

```text
id             保持原 ID
position       更新为最新位置
expireTick     延长
radius         max(old, new)
strength       max(old, new)
createdTick    保留第一次创建时间（或改为 lastEmitTick 语义，见 §2.1）
```

§12 默认参数：`merge_window_ticks = 5`、`merge_distance = 4.0`（§41 配置草案 `[noise]`）。

---

## 3. 查询 API 语义（§10、§15、§16、§36、§47）

### 3.1 噪声产生 API（§10）

```java
public final class HearingApi {

    public static NoiseSnapshot emit(
            ServerLevel level,
            Vec3 position,
            NoiseSpec spec,
            @Nullable Entity source
    );
}
```

- **建议 `emit()` 返回最终写入的 `NoiseSnapshot`，而不是 `void`**（§10）。理由：外部模组可获得合并后的最终 Pulse；测试 / 调试更方便；不需要 emit 后立即再 query 一次。若希望 API 更保守也可保留 `void`，但从扩展性上推荐返回快照。
- 简化重载（§10.1）：

```java
public static NoiseSnapshot emit(
        ServerLevel level,
        Vec3 position,
        ResourceLocation type,
        float radius,
        @Nullable Entity source
);
```

默认 `strength` / `lifetime` 从注册配置中读取；`type` 未配置时推荐使用 `CUSTOM` 默认值。

### 3.2 `NoiseSpec`（§11）

```java
public record NoiseSpec(
        ResourceLocation type,
        float radius,
        float strength,
        int lifetimeTicks
) {}
```

构造 / emit 时必须校验：

```text
position 三轴有限值
radius > 0
strength >= 0
lifetimeTicks > 0
```

异常输入处置：开发环境记 warning；运行环境安全 clamp / reject；**不允许 NaN / Infinity 进入索引**。

### 3.3 `query(AABB)` 的精确语义（§15.1）

```java
public static List<NoiseSnapshot> query(
        ServerLevel level,
        AABB area
);
```

> **返回 `NoiseSnapshot.position` 位于 AABB 内的有效噪声。**

- 它**不**因为 `noise.radius` 与 AABB 相交就返回。
- 即「**声音发生在区域内**」语义，而非「**传播范围覆盖区域**」。
- 适用场景：`misc_twf` 的方块 / 机关「附近是否发生过声音」逻辑（§35.1）。

### 3.4 监听点查询 `findBestAudible(...)`（§15.2）

```java
public static Optional<NoiseSnapshot> findBestAudible(
        ServerLevel level,
        Vec3 listenerPosition,
        float hearingMultiplier,
        Predicate<NoiseSnapshot> predicate
);
```

内部条件：

```text
distance(listener, noise.position) <= noise.radius × hearingMultiplier
```

并按统一 score 选出最值得调查的声音。**`HearNoiseGoal` 应直接使用这一类 API，而不是先 `query(AABB)` 再自己重复一套筛选逻辑**（§15.2）。

### 3.5 `findStrongest(...)` 命名歧义（§15.3）

如果保留 `findStrongest(...)`，**必须定义 `Strongest = 单纯比较 noise.strength`**；不要一会儿代表 raw strength，一会儿代表带距离 / 新鲜度的 score。若是综合分数，应命名为 `findBestAudible` / `findBestNoise`。

§35.1 再次强调：`misc_twf` 场景下 `findStrongest` **必须严格定义为 strength 最大**。

§47 给出的草案签名同时保留两者：

```java
public static Optional<NoiseSnapshot> findStrongest(ServerLevel level, AABB area);
public static Optional<NoiseSnapshot> findStrongest(ServerLevel level, AABB area, Predicate<NoiseSnapshot> predicate);
public static Optional<NoiseSnapshot> findBestAudible(ServerLevel level, Vec3 listenerPosition, float hearingMultiplier, Predicate<NoiseSnapshot> predicate);
```

排序 / 筛选规则：`query` 为集合语义（返回全部命中）；`findStrongest` 按 raw `strength` 取最大；`findBestAudible` 按 §16 统一 score 取最大。过期（`expireTick` 已到）的噪声不参与任何查询——§15.1 用词为「**有效**噪声」，§23/§30 亦以「过期」为失效判据。

### 3.6 节流与随机错峰策略（§22）

`GoalSelector` 可能频繁调用 `canUse()`。禁止：

```text
200 个感染者 × 每 tick × 扫附近多个 Chunk
```

推荐默认：

```text
queryInterval = 10 tick
0~4 tick jitter
```

示例实现形式：

```java
if (gameTime < nextQueryTick) return false;
nextQueryTick = gameTime + queryInterval + mob.getRandom().nextInt(5);
```

- 目的：大量尸潮不会在同一 tick 同时查询。
- 可配置范围建议：`5~20 tick`；**不建议默认每 tick 查询**（§22）。
- 相关配置名：`query_interval_ticks = 10`、`query_jitter_ticks = 4`（§41）。

### 3.7 来源实体查询 `resolveSource(...)`（§36）

```java
public static Optional<Entity> resolveSource(
        ServerLevel level,
        NoiseSnapshot noise
);
```

语义必须是：**只尝试解析当前仍在该 `ServerLevel` 且仍可解析的实体**。允许返回 empty 的情形：`sourceUuid` 本来为空；实体已死亡；实体已换维度；实体已卸载；来源本来就是方块 / 爆炸 / 机关。**外部 API 不得假设 Noise 一定来自 `LivingEntity`。**

### 3.8 声音评分公式（§16）

统一放入 `NoiseScoring`，**不要让 `HearNoiseGoal`、API、`misc_twf` 各自复制不同公式**。P0 推荐简单、可预测：

```text
effectiveRadius = radius × hearingMultiplier

distanceFactor = clamp(1 - distance / effectiveRadius, 0, 1)

remaining = clamp(
    (expireTick - now) / (expireTick - lastEmitTick),
    0,
    1
)

freshnessFactor = 0.5 + 0.5 × remaining

score = strength × distanceFactor × freshnessFactor
```

说明：距离越近越重要；新声音优先；即将过期的声音不会在未过期前瞬间变成几乎 0 分；不涉及 `FOLLOW_RANGE`、不涉及 Target、不涉及来源实体类别。若程序认为 freshness 不需要，可 P0 进一步简化，但**必须全局只保留一种算法**。

### 3.9 事件（§13）

建议 `NoiseEmitEvent.Pre` / `NoiseEmitEvent.Post` 两阶段：

| 阶段 | 能力 | 持有数据 |
| --- | --- | --- |
| `Pre` | 可以 cancel；可以修改 `NoiseSpec`；可以修改 position（是否允许由程序评估，**建议允许但需校验**）。**不要直接暴露可修改的内部 `NoisePulse`** | `ServerLevel`、`Vec3 position`、`NoiseSpec spec`、`@Nullable Entity source`（由 setter 或 mutable fields 修改） |
| `Post` | **必须只读**。表示 Pulse 已正式进入 Manager（或完成合并）；外部模组不得通过 Post 修改内部状态 | `ServerLevel`、`NoiseSnapshot` |

`NoiseEmitEvent.Post` 亦被推荐为一次性机关 / 陷阱 / 怪物卵的驱动入口，以取代 BlockEntity 每 tick 轮询（§35.1、§35.2）。

### 3.10 `NoiseSnapshot`（§14）

```java
public record NoiseSnapshot(
        long id,
        Vec3 position,
        ResourceLocation type,
        float radius,
        float strength,
        long createdTick,
        long lastEmitTick,
        long expireTick,
        @Nullable UUID sourceUuid
) {}
```

**外部只能读取；不要返回内部 `NoisePulse` 对象引用。**

---

## 4. AI 侧：`HearNoiseGoal`

### 4.1 Goal 注入（§19）

- 统一使用 **`EntityJoinLevelEvent`**（NeoForge 1.21.1 确认存在），建议 `@SubscribeEvent(priority = EventPriority.LOWEST)`。
- 但必须正确理解：**LOWEST 只能让同一事件总线中更高优先级监听器先执行，并不能作为「所有其他模组一定完成 AI 初始化」的绝对保证**；因此代码本身仍必须具备**幂等性**。
- §19.1 服务器端检查：必须 `if (!(event.getLevel() instanceof ServerLevel)) return;`，客户端不注入。
- §19.2 防重复注入：推荐注册一个**非持久化布尔 Attachment** `HEARING_GOAL_ATTACHED`，逻辑为：

```text
不是 Mob → return
不在 hearing_mobs → return
在 blacklist → return
已 attached → return
addGoal(priority, new HearNoiseGoal(...))
attached = true
```

**不要为了防重复维护一个全局 `Set<UUID>`。**

### 4.2 Goal Priority（§20）

- 默认 `6`；**Priority 必须配置化**（配置项 `priority = 6`）。
- 只声明 `setFlags(EnumSet.of(Flag.MOVE))`；**不要声明 `LOOK`、`TARGET`、`JUMP`**。
- 预期顺序：

```text
战斗 / 避让 / 破坏 / Crawl / 高优先级特殊行为   → Priority 0~5
循声                                          → Priority 6
普通移动 / 村庄行为 / RandomStroll             → Priority 7~10
```

- 与现有 `GoalSelector` 的关系：**只 `addGoal`，不改 `targetSelector`**（§4.1、§4.3）；高优先级 MOVE Goal 抢占由 `GoalSelector` 自然停止本 Goal，核心代码**不写** `if (CrawlMod...)` / `if (ZombieTactics2...)` / `if (ImprovedMobs...)`（§24）。

### 4.3 内部状态字段（§21）

第一版**不需要 SEARCH 状态机**。最少字段：

```java
private @Nullable NoiseSnapshot currentNoise;
private @Nullable Vec3 destination;
private long nextQueryTick;
private long nextRepathTick;
private int failedPathAttempts;
```

可选：`private long lastStartedNoiseId;`。

**不要保存**：目标玩家、目标实体引用、攻击目标、`SoundDecoy`。

§48 伪代码额外含 `private long nextSwitchAllowedTick;` 与 `private final HearingProfile profile;`。

### 4.4 `canUse()` 行为（§22、§23）

建议顺序：

```text
1. Mob 是否活着 / 可正常导航
2. 当前是否存在有效真实 Target
3. 是否到达 nextQueryTick
4. 查询可听声音
5. 是否找到 score > 最低阈值的 Noise
6. 保存 currentNoise / destination
7. 尝试第一次 Navigation.moveTo
8. 成功则启动 Goal
```

- 若 `LivingEntity target = mob.getTarget();` 且 `target != null && target.isAlive()` → **直接 `return false`**。**循声不与 Target 竞争。**
- 节流实现见 §3.6。

### 4.5 `canContinueToUse()` 行为（§24）

满足任意条件应结束：

```text
出现有效真实 Target
currentNoise == null
Noise 已过期
到达目的地
路径连续失败达到阈值
Mob 已死亡/移除
当前维度发生异常变化
```

高优先级 MOVE Goal 抢占则由 `GoalSelector` 自然停止本 Goal。

### 4.6 `start` / `stop` / `tick`（§21、§48、§27、§29）

- `start`：§48 伪代码未单独实现 `start()`（首次 `moveTo` 在 `canUse()` 内完成）。
- `tick`：`hasLiveTarget()` 直接返回；`maybeSwitchToBetterNoise(now)`；当 `now >= nextRepathTick && mob.getNavigation().isDone()` 时 `tryMoveToDestination(now)`。
- `stop`：`currentNoise = null; destination = null; failedPathAttempts = 0;`。
- §48 明确：`stop()` 是否主动 `navigation.stop()` **请程序结合 GoalSelector 调度和现有 AI 冲突测试决定**；原则是**不残留本 Goal 的旧路径，也不能清掉新接管 Goal 刚建立的路径**；如需停止 Navigation，必须通过实际 1.21.1 调度顺序测试确认。

### 4.7 到达判定（§25）

- 配置化：`arrivalDistance = 3.0` blocks；推荐范围 `2.5 ~ 3.5`。
- 使用平方距离避免开方：`distanceToSqr(destination) <= arrivalDistance * arrivalDistance`。
- 到达后：`currentNoise = null; destination = null;` Goal 结束。
- 第一版**不做**：原地搜索、转头扫描、随机巡逻、房间搜索。

### 4.8 Navigation 使用原则（§26）

- 只调用公开 Vanilla / NeoForge 入口：`mob.getNavigation().moveTo(x, y, z, speedModifier);`（或程序确认 1.21.1 mapping 后的等价公开方法）。
- 严禁：直接 `PathFinder`、Mixin `PathNavigation`、替换 `PathNavigation`、修改 `NodeEvaluator`、修改 `MoveControl`。
- 循声只提出「去这个位置」，怎么走由当前 Navigation 实现决定，这样 ZT2 / Improved Mobs / ServerCore 对 Navigation 的修改仍然自然生效。

### 4.9 重寻路间隔与路径失败（§27、§28）

- **禁止每 tick 重建路径**（`moveTo(...)` every tick）。
- 推荐 `repathInterval = 20 tick`（配置项 `repath_interval_ticks = 20`）。仅以下情况允许重新调用：到达 `nextRepathTick`；当前 Navigation 已结束但仍未到达目的地；目的地因切换 Noise 发生明显改变。若 destination 完全没变且现有 path 正常，不重新建路径。
- 路径失败：默认 `maxPathFailures = 3`（配置项 `max_path_failures = 3`）。失败包括 `moveTo(...)` 返回 `false`；Navigation 很快结束且没有明显靠近目标；重试后仍无法建立有效路径。达到阈值 → **直接放弃当前 Noise**。
- 不得：自己破墙 / 开门 / 挖掘 / 跳跃寻路 / 传送 / 搜索替代道路 / 修改 `NodeEvaluator`。

### 4.10 切换新声音阈值（§29）

不能任何新声音都立即切换，否则尸潮会出现 `A → B → A → B`。推荐：

```text
newScore > oldScore × switchThreshold
switchThreshold = 1.15
switchCooldown = 5~10 tick     // 配置草案实取 switch_cooldown_ticks = 8
```

**同一个 `noise.id` 合并更新时，不视为「切换到新 Noise」。**

### 4.11 当前声音过期后的行为（§30）

文档在本节**内部自相矛盾**（详见 §10 疑点 D）：先写「方案 A（推荐）：Noise 过期即结束调查」，随后又写「**本项目更推荐方案 B**」，最终定义采用 B：

> `expireTick` 只决定「这个声音还能否被新听见 / 重新选择」；一旦 `HearNoiseGoal` 已经记录 `destination`，**允许继续调查**，除非被 Target / 更高优先级 Goal / 路径失败中断。

即 `NoisePulse` 可以很短，但怪物不会中途突然停步。

### 4.12 Target 出现时立即让路（§31）

无论在首次寻路 / 重寻路 / 调查途中 / 声音切换，只要 `mob.getTarget() != null && mob.getTarget().isAlive()`，循声**立即结束**。

禁止：先走到声音点再攻击；延迟清 Target；比较 Noise score 和 Target。**Target 永远高于纯循声。**

### 4.13 `HearNoiseGoal` 伪代码要点（§48）

文档给出完整行为示意（`public final class HearNoiseGoal extends Goal`），并声明「这是行为示意，不要求程序照抄」。构造器签名 `HearNoiseGoal(Mob mob, HearingProfile profile)`，构造时 `this.setFlags(EnumSet.of(Flag.MOVE));`。

---

## 5. 配置与数据驱动

### 5.1 「是否拥有听觉」的 Tag 入口（§17）

```text
#winter_frontier:hearing_mobs
#winter_frontier:hearing_blacklist
```

- **Blacklist 优先。**
- 默认：**不给所有 Monster 自动加入、不给所有动物自动加入、不给 Boss 自动加入、不给 NPC 自动加入**；必须显式进入 Tag。
- NeoForge 1.21.1 支持对 `EntityType` 等注册对象使用 Tag，因此该设计可保留。
- 注意：需求写的是 `winter_frontier` 命名空间，而本工程 modid 为 `misc_twf` / `misc_twf_*`（见 §9 疑点 D）。

### 5.2 听觉数值配置数据化（§18）

仅靠 Tag 只能表达「能听 / 不能听」，但还需要：

```text
hearingMultiplier
queryInterval
investigateSpeed
arrivalDistance
```

两种方案，程序可评估：

- **方案 A：Tag + 配置映射**（服务端配置）

```toml
[entity_hearing]
"minecraft:zombie" = 1.0
"minecraft:husk" = 0.9
"xxx:sensitive_infected" = 1.3
```

优点：实现简单。

- **方案 B：NeoForge Data Map**——将 `EntityType` 映射到 `HearingProfile`，允许数据包重载。优点：更数据驱动；整合包后续添加感染者无需改代码；数值配置比 Tag 更适合 Data Map。缺点：初期实现复杂度略高；**需要程序确认当前项目对 Data Map 的使用习惯**。

**评审建议：P0 至少完成 Tag + server config；若程序熟悉 NeoForge Data Map，可直接采用 Data Map。**

### 5.3 服务端配置草案（§41）

```text
[goal]
priority = 6
query_interval_ticks = 10
query_jitter_ticks = 4
repath_interval_ticks = 20
max_path_failures = 3
arrival_distance = 3.0
speed_modifier = 1.0
switch_threshold = 1.15
switch_cooldown_ticks = 8

[noise]
cleanup_interval_ticks = 20
max_active_per_level = 4096
max_active_per_chunk = 128
merge_window_ticks = 5
merge_distance = 4.0

[debug]
enabled = false
```

§41 补充：具体声音参数建议独立数据文件或配置，条目对应类型：

```text
SPRINT
LAND
DOOR
CONTAINER
BLOCK_BREAK
GLASS_BREAK
GUNSHOT
SUPPRESSED_GUNSHOT
EXPLOSION
```

### 5.4 名称对应关系提醒

文档中同一参数在不同小节使用了三套命名，实现时必须统一映射（详见 §10 疑点 E）：

| §18 名称 | §41 TOML 名称 | 语义 |
| --- | --- | --- |
| `hearingMultiplier` | （`[entity_hearing]` 映射值 / Data Map `HearingProfile`） | 听觉倍率，乘到 `noise.radius` |
| `queryInterval` | `query_interval_ticks` | `canUse()` 查询间隔 |
| `investigateSpeed` | `speed_modifier` | `moveTo` 的 speedModifier |
| `arrivalDistance` | `arrival_distance` | 到达判定距离 |

---

## 6. 服务端权威与网络

### 6.1 服务端权威（§6）

所有 `NoisePulse` **必须由服务器逻辑创建**。禁止：

```text
客户端播放了声音
        ↓
客户端判断“这里有噪声”
        ↓
直接驱动服务器怪物
```

原因：客户端声音可能被资源包、音效设置、渲染优化等改变；客户端事件不应成为 AI 权威来源；多人环境必须保证噪声判定一致；防止客户端伪造噪声影响 AI。正确入口：

```text
服务端确认某行为真的发生
        ↓
HearingApi.emit(...)
```

§34 再次强化：**禁止监听所有 `SoundEvent` 然后一律生成 Noise**；UI 声音、环境 BGM、客户端局部声音、其他模组装饰性音效都不应默认引发怪物调查。即**客户端声音播放不得等价为噪声事件**（§0、§6、§33.1）。

### 6.2 是否需要网络同步

需求文档**未定义任何噪声网络包**：

- 噪声只在服务端创建、查询、驱动 AI（§6、§38）；
- `NoiseManager` 为 `ServerLevel` 绑定、非持久化、维度卸载即释放（§8.1、§8.2）；
- 查询与查询结果不跨端；
- 因此 **P0 不需要新增网络包**。

若未来需要客户端可视化（调试 HUD / 粒子），方向应为 **Server → Client 单向同步**，且不得让客户端回写噪声（§6 精神）。文档仅给出「调试模式可仅对指定管理员显示」的要求（§40），未指定展示通道（聊天 / HUD / 粒子任一）。

### 6.3 服务端权威的副作用约束（§32）

ServerCore 硬性兼容要求（10 条）中与权威相关者：

1. 不强制 Mob Tick；
2. 不维护「所有听觉 Mob 列表」然后全局循环；
3. 不取消 inactive tick；
4. 不修改 `GoalSelector` inactive 行为；
5. 不修改 `Activation Range`；
6. 不主动加载 Chunk；
7. 不为循声访问未加载世界区域；
8. Navigation 失败就接受失败；
9. **`NoiseManager` 记录枪声本身不能唤醒实体**；
10. **inactive Mob 恢复正常 AI 后，只能听到届时仍有效的 Noise；已经过期的旧枪声不补发。**

最后一条极其重要：**不做「离线补听」。**

---

## 7. SBW（Superb Warfare）适配层

> 需求 §33 要求：SBW 兼容层**只做三件事**——服务端确认真实开火 → 读取当前武器实际噪声参数 → `HearingApi.emit(...)`；不得 `setTarget`、直接通知 Zombie、修改 `FOLLOW_RANGE`、添加 EXPOSURE、直接修改 `HearNoiseGoal` 内存。
> §33.1 接入点优先级：① SBW 已有服务端 `ShootEvent` / 开火 API；② SBW 服务端实际消耗弹药 / 生成弹丸的可靠逻辑入口；③ 最后才考虑低侵入 Mixin 到 SBW 自身具体开火方法。**禁止通过「听到枪声音效」推断开枪**（原因：客户端与服务端可能不同步；空枪、动画、载具武器、特殊武器可能播放不同声音；消音 / 配件可能动态改变最终噪声；音效播放不是服务端 AI 权威事件）。
> §33.2 明确：兼容代码必须以 **1.21.1 当前分支 / 当前 Jar 的真实 API** 为准，不要按旧 1.20.1 Forge 包路径硬写。

### 7.1 可用的服务端开火 / 命中事件（已核对 1.21.1 源码）

`com.atsuishio.superbwarfare.api.event` 包是 SBW 唯一显式 API 包，共 6 个事件类。与噪声适配相关者：

| 事件类 | 继承关系 | 触发点（源码位置） | 关键 getter / 字段 | 适配价值 |
| --- | --- | --- | --- | --- |
| `ShootEvent.Pre` | `ShootEvent extends Event`（**未实现 `ICancellableEvent`，不可取消**），标注 `@ApiStatus.AvailableSince("0.8.8")` | `GunItem.beforeShoot(ShootParameters)` 内 `NeoForge.EVENT_BUS.post(new ShootEvent.Pre(parameters))`（`item/gun/GunItem.java:516`） | `getShootParameters()`、`getShooter()`、`getLevel()`（`ServerLevel`）、`getData()`（`GunData`）、`getSpread()`、`isZoom()` | 与 `Post` 二选一；`Pre` 位于弹丸生成之前 |
| `ShootEvent.Post` | 同上 | `GunItem.afterShoot(ShootParameters)` 内 `NeoForge.EVENT_BUS.post(new ShootEvent.Post(parameters))`（`item/gun/GunItem.java:550`） | 同上 | **推荐主挂接点**（已确认弹丸生成 / 音效 / 弹药结算路径走完） |
| `ProjectileHitEvent.HitEntity` | `ProjectileHitEvent extends Event implements ICancellableEvent`，`@ApiStatus.AvailableSince("0.8.7")` | `entity/projectile/ProjectileEntity.java:592`、`entity/projectile/FastThrowableProjectile.java:149` | `getOwner()`（命中者）、`getProjectile()`、`getHitVec()`、`getTarget()`、`isHeadshot()`、`isLegShot()` | 可用于 `EXPLOSION` / 命中类噪声 |
| `ProjectileHitEvent.HitBlock` | 同上 | `entity/projectile/ProjectileEntity.java:535`、`FastThrowableProjectile.java:162` | `getOwner()`、`getProjectile()`、`getHitVec()`、`getPos()`、`getState()`、`getFace()` | 可用于弹着点噪声 |
| `ReloadEvent.Pre` / `ReloadEvent.Post` | `ReloadEvent extends Event`，标注 `@ApiStatus.Internal`、`@ApiStatus.AvailableSince("0.8.0")`；公开字段 `shooter`、`data`、`stack` | `event/GunEventHandler.java:235`、`:313`、`:318`、`:359`、`:461` | `getEntity()`、`getStack()` | 换弹噪声（`CUSTOM` 类型）可选 |
| `PreKillEvent.SendKillMessage` / `.Indicator` | `api/event/PreKillEvent.java` | 击杀播报 | — | 与本系统无关 |
| `RegisterContainersEvent` | `api/event/RegisterContainersEvent.java` | 容器注册 | `add(...)` | 与本系统无关 |
| `RenderPlayerArmEvent` | `api/event/RenderPlayerArmEvent.java` | **客户端**手臂渲染 | — | 明确不可用（客户端） |

关键源码事实（`item/gun/GunItem.java:629-677`，`/** 服务端处理单次开火 */ public void shoot(@NotNull ShootParameters parameters)`）：

```java
public void shoot(@NotNull ShootParameters parameters) {
    var data = parameters.data();
    var shooter = parameters.shooter();
    ...
    if (!data.canShoot(ammoSupplier)) return;   // 弹药/过热/换弹校验
    data.item.beforeShoot(parameters);          // → ShootEvent.Pre
    ... shootBullet(...) ...
    playFireSounds(data, shooter, zoom);        // 客户端/服务端音效
    data.item.afterShoot(parameters);           // → ShootEvent.Post，含弹药扣减
    data.save();
}
```

- 该方法是**服务端单次开火总入口**，`gunData.shoot(...)` / `GunItem.shoot(...)` 的各重载最终都委托到它（`data/gun/GunData.java` 同名方法 → `this.item.shoot(...)`），且 `GunItem.shoot(GunData, Entity, ...)` 只有 `shooter.level() instanceof ServerLevel` 时才构造参数（`GunItem.java:612-622`）。**因此 `ShootEvent` 天然是服务端事件，且已经通过 `canShoot` 校验——不会把空枪 / 动画误判为真实开火**，满足 §33.1 优先级 1 且比优先级 2 更稳定。

### 7.2 可用数据字段

| 字段 | 定义位置 | 类型 / 默认值 | 说明 |
| --- | --- | --- | --- |
| `soundRadius` | `data/gun/DefaultGunData.java:226-228`，`@ServerOnly @SerializedName("SoundRadius") public double soundRadius;` | `double`，**无显式初始化值，默认 `0.0`** | 枪械音效基准半径；`@ServerOnly` 标注 |
| `GunProp.SOUND_RADIUS` | `data/gun/GunProp.java:107`，`public static final GunProp<Double> SOUND_RADIUS = new GunProp<>("SoundRadius");` | `GunProp<Double>`，**该类已 `@Deprecated(forRemoval = true)`** | 旧式属性访问入口，新代码应直接读 `data.compute().soundRadius` |
| `SoundInfo` | `data/gun/SoundInfo.java` | 见下 | 音效事件载体，`@SerializedName` 名与字段名不完全一致 |
| `rpm` | `DefaultGunData.java:229-230`，`@SerializedName("RPM") public int rpm = 600;` | `int`（默认 600） | 射速，可推导连发合并压力 |
| `explosionDamage` / `explosionRadius` | `DefaultGunData.java:232-235`，`@SerializedName("ExplosionDamage")` / `("ExplosionRadius")` | `double` | 爆炸武器（RPG / 榴弹 / 炮）参数，可用于推导 `EXPLOSION` 噪声半径 |
| `SoundInfo.fire3P` / `fire3PFar` / `fire3PVeryFar` | `SoundInfo.java:15-23`，`@SerializedName("Fire3P")` / `("Fire3PFar")` / `("Fire3PVeryFar")` | `SoundEvent`，均 `@ServerOnly` | 正常开火三层远距离音效（×0.4 / ×0.7 / ×1.0 音量衰减） |
| `SoundInfo.fire3PSilent` / `fire3PFarSilent` / `fire3PVeryFarSilent` | `SoundInfo.java:29-37` | `SoundEvent`，`@ServerOnly` | 装消音器时的三层音效 |
| `SoundInfo.reload*` / `bolt` / `change` / `locking` / `locked` / `vehicleReload` / `vehicleReload3p` / `vehicleReloadSoundTime` | `SoundInfo.java:40-79` | `SoundEvent` / `int` | 换弹与操作音效；`vehicleReload*` 为载具换弹 |

**消音判定（关键）**：`GunItem.playFireSounds` 中

```java
boolean isSilent = data.attachment.get(AttachmentType.BARREL) == 2;   // item/gun/GunItem.java:708
```

且

```java
/** 获取额外音效半径加成 */
public double getCustomSoundRadius(GunData data) {
    return data.attachment.get(AttachmentType.BARREL) == 2 ? 0.6 : 1;  // item/gun/GunItem.java:456-458
}
```

`getCustomSoundRadius` 在 `computeProperties` 中以 `rawData.soundRadius += getCustomSoundRadius(gunData);` 叠加（`GunItem.java:122`）。

**注意此处存在语义陷阱（见 §10 疑点 C）**：这是**加法**而非乘法，所以装消音器时 `soundRadius` 实际被**加上 0.6**，同时 `fire3P*Silent` 音效的播放音量按 `soundRadius × 0.4 / 0.7 / 1.0` 计算。因此「消音器让半径变小」这一直觉在 SBW 数据层**并不成立**（`0.6` 是加成值，不是倍率）；适配层若要实现 `SUPPRESSED_GUNSHOT` 的更小半径，**必须自行按 `AttachmentType.BARREL == 2` 分支决定半径策略，不能直接照搬 `soundRadius`**。

**数据来源与读取方式**：

- 数据表注册：`data/CustomData.java:19`

```java
public static final DataLoader.DataMap<DefaultGunData> GUN_DATA =
        DataLoader.createData("sbw/guns", DefaultGunData.class, map -> GunData.DATA_CACHE.invalidateAll());
```

即 JSON 数据包目录 **`data/<namespace>/sbw/guns/**.json`**（由 SBW 的 `DataLoader` 加载，支持数据包覆盖 / 重载），字段名即 `@SerializedName` 中的名字（`SoundRadius`、`RPM`、`ExplosionRadius` 等）。

- 读取路径（推荐）：

```java
GunData data = event.getData();                       // ShootEvent 提供
double soundRadius = data.compute().soundRadius;      // 最终值（含 computeProperties 与配件修正）
String gunId = data.id;                               // 形如 "superbwarfare:ak_47"
```

`data.compute()` 内部流程（`data/gun/GunData.java:231-244`）：`getDefault().copy()` → 属性覆盖 tag（`jsonPropModifier`）→ `item.computeProperties(this, rawData)` → 射速模式属性。`GunData.getDefault(String id)` 在缺少 JSON 时回退到 `new DefaultGunData()`（`GunData.java:182-187`）。

### 7.3 载具武器与爆炸物

**载具武器是否走同一 `ShootEvent`：是（已核实）。** `entity/vehicle/base/VehicleEntity.java` 直接构造并提交 `ShootParameters`：

```java
// entity/vehicle/base/VehicleEntity.java:1261
data.shoot(new ShootParameters(getAmmoSupplier(), living, (ServerLevel) this.level(), getShootPos(weaponName, 1), getShootVec(weaponName, 1), data, data.compute().spread, true, null, null));
// :1273 同类调用（另一武器/乘员路径）
```

因 `GunData.shoot(ShootParameters)` → `GunItem.shoot(ShootParameters)` → `beforeShoot` / `afterShoot`，**载具武器同样会触发 `ShootEvent.Pre` / `ShootEvent.Post`**。这直接回答了 §49 的第 4 问。

载具音效本身另走服务端广播：`SoundTool.playDistantSound(ServerLevel, SoundEvent, Vec3, float radius, float pitch, Entity sender)`（`tools/SoundTool.java:52`），调用点如 `VehicleEntity.java:1345/1349/1353`（半径同样取自 `computedGunData.soundRadius` ×0.4 / ×0.7 / ×1.0）、`MortarEntity.java:115-116`、`Type63Entity.java:306`。**但这些是音效播放，不可作为噪声依据**（§33.1）。

**爆炸物没有专用事件**：SBW 的爆炸统一由 `tools/CustomExplosion.Builder`（`tools/CustomExplosion.java:40`，`extends Explosion`）产生，落地调用 `explode()`（`:323`）并伴随 `EventHooks.onExplosionStart` / `finalizeExplosion`。调用方遍布 `item/HandGrenade.java:78`、`item/RgoGrenade.java:81`、`entity/ClaymoreEntity.java:232/244`、`entity/Tm62Entity.java:241`、`entity/projectile/C4Entity.java:432`、`ProjectileEntity.java:665`、`DroneEntity.java:700`、`VehicleEntity.java:2928/2943`、`Ptkm1rEntity.java:303`、`AutoAimableEntity.java:372/382`、`Firefly.java:33` 等。

因此 `EXPLOSION` 噪声有两条可选挂接路径：

1. **原版 / NeoForge 通用路径**：监听 `net.neoforged.neoforge.event.level.ExplosionEvent.Detonate`（或 `Start`），位置取 `event.getExplosion().center()`，天然覆盖 SBW 的 `CustomExplosion` 与所有其它模组爆炸，且仍属服务端权威；
2. **SBW 专属路径**：在 `compat/superbwarfare` 内对 `CustomExplosion.Builder.explode()` 做低侵入 Mixin（§33.1 优先级 3），或对上述调用点逐一注入。

**推荐路径 1**：既满足「不从客户端音效反推」，又避免对 SBW 内部类做 Mixin，且能统一覆盖原版 TNT / 苦力怕以外的所有爆炸来源。

### 7.4 挂接点建议

```text
GUNSHOT / SUPPRESSED_GUNSHOT
    ShootEvent.Post（唯一权威入口，服务端、已过 canShoot 校验）
        ↓
    GunNoiseResolver.resolve(ShootParameters, GunData)
        · gunId          = data.id（"superbwarfare:ak_47"）
        · soundRadius    = data.compute().soundRadius（JSON 的 SoundRadius 字段）
        · isSilent       = data.attachment.get(AttachmentType.BARREL) == 2
        · type           = isSilent ? SUPPRESSED_GUNSHOT : GUNSHOT
        · radius         = 由 GunNoiseResolver 按 type 与 soundRadius 计算
        · position       = parameters.shootPosition()
        · source         = parameters.shooter()
        ↓
    HearingApi.emit(parameters.level(), position, spec, source)

EXPLOSION
    ExplosionEvent.Detonate（NeoForge 通用、服务端）
        ↓
    type = EXPLOSION，position = explosion.center()
        · radius 由爆炸半径或自定义配置推导
        ↓
    HearingApi.emit(...)

RELOAD（可选，CUSTOM）
    ReloadEvent.Post → emit(CUSTOM, 小半径)
```

§33.3 硬性要求：**不要按 `GunItem` 类名硬编码所有枪**；推荐抽象 `weapon / fire context → GunNoiseResolver → NoiseSpec`；第一版至少区分 `GUNSHOT` / `SUPPRESSED_GUNSHOT` / `EXPLOSION`；后续可数据化具体枪械半径。

§33.2 列出程序接入前必须确认的 5 件事：开火服务端入口；`ShootEvent` 是否仍存在、包名是否变化；消音参数从哪里取；载具武器是否走同一事件；爆炸物 / 火箭筒是否额外产生 `EXPLOSION` Noise。本摘要已依据当前 1.21.1 源码逐项给出答案（见上表与 §7.3），但**接入前仍应按实际打包 Jar 复核一次**。

---

## 8. 性能预算与验收矩阵

### 8.1 性能原则（§42）

| 编号 | 原则 | 具体要求 |
| --- | --- | --- |
| §42.1 | 禁止全 Mob 扫描 | **绝对禁止** `for (Mob mob : allMobs) { checkNoise(mob); }`。正确路径：Mob 正常 AI Tick → `HearNoiseGoal` 到查询时间 → 查询附近 `NoiseSpatialIndex` |
| §42.2 | 禁止全维度 Noise 扫描 | 禁止每个怪物 query 都遍历该维度全部 `NoisePulse`；必须按 Chunk 空间索引缩小候选集合 |
| §42.3 | 禁止同步 Chunk Load | Noise 查询本身只查询内存索引，不接触未加载区块 |
| §42.4 | 查询错峰 | 200 个感染者不应在同一 tick 同时做 query；必须保留 query interval + jitter |
| §42.5 | 路径请求限频 | 一个 Mob 默认最多 **20 tick / 次重寻路**；不能每 tick Path 创建 |

### 8.2 性能目标量化汇总

| 指标 | 目标值 | 出处 |
| --- | --- | --- |
| 每 Mob 查询间隔 | `query_interval_ticks = 10`（可配 5~20），附 `0~4` tick jitter | §22、§41 |
| 实体数量级 | 200 个感染者（性能验收场景基准） | §42.4、§43 |
| 重寻路间隔 | `repath_interval_ticks = 20` | §27、§42.5 |
| 过期清理间隔 | `cleanup_interval_ticks = 20` | §8.3、§41 |
| 单维度活跃 Pulse 上限 | `max_active_per_level = 4096` | §39、§41 |
| 单 Chunk 活跃 Pulse 上限 | `max_active_per_chunk = 128` | §39、§41 |
| 合并窗口 | `merge_window_ticks = 5`、`merge_distance = 4.0` | §12、§41 |
| 路径失败上限 | `max_path_failures = 3` | §28、§41 |
| 切换阈值 / 冷却 | `switch_threshold = 1.15`、`switch_cooldown_ticks = 8`（建议 5~10） | §29、§41 |
| 到达判定 | `arrival_distance = 3.0`（建议 2.5~3.5） | §25、§41 |
| Chunk 扫描半径 | `chunkRadius = ceil(effectiveMaxRadius / 16)` | §9.1 |

### 8.3 性能验收场景（§43）

测试配置：**200 个感染者 + ZombieTactics2 + Improved Mobs + CrawlMod + ServerCore + The Hordes**；持续施加「自动步枪射击 / 爆炸 / 玩家奔跑」。要求：

- 无 `SoundDecoy` Entity；
- 无大量临时实体；
- 无全 Mob 遍历；
- 无全维度 Noise 遍历；
- 无每 tick 重建 Path；
- 无强制 Chunk Load；
- 无 inactive Mob 被噪声唤醒；
- 无 Goal 死循环；
- 无明显怪物原地抽搐；
- 无持续覆盖其他模组 Navigation；
- 自动武器 Pulse 数量受合并机制控制；
- 查询负载不会出现明显「所有 Mob 同 tick 峰值」。

建议使用 **spark** 对比三组 Profile：

```text
关闭循声
开启循声但无噪声
开启循声 + 自动武器持续射击
```

### 8.4 兼容验收矩阵（§44）

| 编号 | 对象 | 测试项 | 验收点 |
| --- | --- | --- | --- |
| §44.1 | Vanilla Zombie | — | 无 Target 时听枪声并前往声音坐标；玩家离开声音点后**不自动跟踪玩家新位置**；到达后回归原本 AI；看见玩家获得 Target 后**立即停止循声** |
| §44.2 | ZombieTactics2 | 攻击 / 破坏 / 自定义寻路 / 目标选择 | 没 Target 时可循声；有 Target 后 ZT2 接管；不抢破坏 Goal；不重置 ZT2 Target；不持续覆盖路径 |
| §44.3 | Improved Mobs | 偷窃 / 使用物品 / 特殊索敌 / 破坏 / 爬梯 | 高优先级 MOVE 行为可以自然抢占；循声不修改其 `TargetingConditions`；不修改 `Sensing`；不修改 Navigation 类 |
| §44.4 | CrawlMod | — | 听见声音后正常前往；进入 `CrawlGoal` 条件后 **Priority 2 抢占**；Crawl 结束后若循声条件仍成立，可由正常 `GoalSelector` 决定是否继续；**不强制 Pose** |
| §44.5 | ServerCore | — | inactive Mob 不因 Noise 激活；`NoiseManager` 不遍历 inactive Mob；恢复 active 后只处理仍有效的新噪声；不访问未加载 Chunk |
| §44.6 | The Hordes | — | Horde 生成的、在 `hearing_mobs` Tag 中的 Zombie 获得 Goal；Horde AI 不被删除 / 改序；大量实体时 query 有错峰 |
| §44.7 | SBW | 半自动 / 全自动 / 消音武器 / 高射速武器 / 霰弹枪 / 爆炸武器 / 载具武器（若走支持入口） | 一次真实射击只产生预期 Noise；**客户端重复音效不产生重复 Noise**；自动武器按 source/type 合并；消音武器进入正确 type/半径；爆炸可单独产生 `EXPLOSION`；**不对玩家添加 MobEffect** |
| §44.8 | misc_twf | — | 完全删除 Sona import；MonsterEgg 能通过 Hearing API 工作；不再扫描 EXPOSURE；source 为空时也不会 NPE；`NoiseEmitEvent` 与 query 两种方式至少有一种完成实际迁移 |

### 8.5 单元 / 自动测试建议（§45）

若项目已有 GameTest / 单元测试基础，建议至少覆盖：

```text
Noise 过期清理
有 source 的连续 Noise 正确合并
匿名 Noise 不错误合并
query(AABB) 只返回位置在区域内的 Noise
findBestAudible 正确过滤 radius
blacklist 覆盖 hearing_mobs
已有 Target 时 canUse=false
新 Noise 低于 1.15 不切换
新 Noise 高于阈值会切换
路径失败达到阈值后放弃
```

### 8.6 调试能力（§40，P0/P0.5）

建议至少提供：

```text
/hearing debug <player> on|off
/hearing stats
/hearing emit <type> <radius> <strength>
```

`/hearing stats` 可输出：

```text
当前维度 active pulse 数
索引 chunk 数
过去 5 秒 emit 数
merge 数
query 数
平均 query 候选数
path fail 数
```

Debug 模式可仅对指定管理员显示：

```text
Mob UUID
Noise ID
Noise type
score
distance
effectiveRadius
当前结束原因
```

**不要默认全服打印每次枪声日志。**

### 8.7 优先级划分（§46）

- **P0（首版必须完成）**：`NoisePulse`、`NoiseSnapshot`、`NoiseSpec`、`NoiseManager`、`NoiseSpatialIndex`、过期清理、有 source 连续 Noise 合并、容量保护；`HearingApi.emit` / `query` / `findBestAudible` / `resolveSource`；`NoiseEmitEvent.Pre/Post`；`HearNoiseGoal`（MOVE-only）、Priority 配置、query interval + jitter、repath interval、路径失败上限、新声音切换阈值；`EntityJoinLevelEvent` 注入、非持久化重复注入标记、`EntityType` Tag / Blacklist；**SBW 真实服务端开火入口适配**；`misc_twf` API 迁移；ZT2 / Improved Mobs / ServerCore / CrawlMod / The Hordes 兼容测试。
- **P0.5（强烈推荐一起完成）**：`/hearing stats`、基础 debug 日志、spark 性能基线、声音参数配置化、`EntityType` 听觉倍率配置化。
- **P1（后续增强）**：墙体 / 遮挡衰减、不同地面材质脚步、更多第三方声音源、Data Map 完整 `HearingProfile`、更细的 SBW 武器数据驱动噪声。
- **P2（原则上不属于本项目）**：SEARCH 房间搜索、群体警觉、视觉感知、仇恨传播、Target 接管、攻击策略、破门破墙——若未来需要，应作为**独立 AI 项目**重新设计，不能偷偷塞回 hearing core。

### 8.8 建议架构与包结构（§5）

```text
hearing/
│
├─ api/
│  ├─ HearingApi.java
│  ├─ NoiseSpec.java
│  ├─ NoiseSnapshot.java
│  ├─ NoiseEmitEvent.java
│  └─ NoisePredicate.java            // 可选
│
├─ core/
│  ├─ NoiseManager.java
│  ├─ NoisePulse.java
│  ├─ NoiseSpatialIndex.java
│  └─ NoiseScoring.java
│
├─ ai/
│  ├─ HearNoiseGoal.java
│  └─ HearingMemory.java             // 若状态全部存在 Goal 内，可省略
│
├─ event/
│  ├─ HearingGoalInjector.java
│  ├─ NoiseLifecycleHandler.java
│  └─ VanillaNoiseEventHandler.java
│
├─ compat/
│  └─ superbwarfare/
│     └─ SuperbWarfareNoiseCompat.java
│
├─ config/
│  ├─ HearingConfig.java
│  └─ HearingProfile.java
│
└─ debug/
   ├─ HearingDebugCommand.java        // 推荐
   └─ HearingDebugLogger.java         // 推荐
```

### 8.9 `misc_twf` 迁移要求（§35）

旧版 `misc_twf 1.18.2` 的 `MonsterEggBlockEntity` 直接依赖 `SonaMobEffects.EXPOSURE`，迁移后必须完全删除 `import com.scarasol.sona.*;` 以及 `EXPOSURE`、`SoundDecoy`、`Sona MobEffect`。

推荐迁移方式：

```java
AABB hearingArea = new AABB(blockPos).inflate(8);

List<NoiseSnapshot> noises = HearingApi.query(
        serverLevel,
        hearingArea
);
```

如需「最强声音」：

```java
Optional<NoiseSnapshot> noise = HearingApi.findStrongest(
        serverLevel,
        hearingArea,
        predicate
);
```

此处 `findStrongest` **必须严格定义为 strength 最大**。若是一次性触发机关，更推荐监听 `NoiseEmitEvent.Post`，避免 BlockEntity 每 tick 轮询。

§35.2 原则：

```text
事件驱动适合：一次触发机关、陷阱、怪物卵
轮询适合：需要检查“最近一段时间是否发生过某类噪声”的持续逻辑
```

**不要强行所有功能都用一种模式。**

### 8.10 参考事实（附录 A）

1. NeoForge 1.21.1 存在 `EntityJoinLevelEvent`，可用于实体进入 Level 时低侵入 Goal 注入；
2. 存在 `ServerTickEvent.Post`，可用于 `NoiseManager` 周期清理 / 统计；
3. NeoForge 1.21.1 的 Entity / ServerLevel 支持 Attachment；非持久化运行时标记与管理对象可评估使用 Attachment；
4. NeoForge 1.21.1 支持 `EntityType` Tag，适合继续维护 `hearing_mobs` / `hearing_blacklist`；
5. NeoForge 的 Data Map 适合「注册对象 → 可重载数值配置」，因此 `EntityType → HearingProfile` 可作为后续数据驱动方案；
6. Superb Warfare 已存在 1.21.1 / NeoForge 实际版本与使用环境，兼容层必须以当前 1.21.1 实际代码 / API 为准。

文档引用链接：NeoForge Tags (1.21.1)、NeoForge Javadocs - `EntityJoinLevelEvent`、NeoForge Data Maps、Superb Warfare GitHub（`https://github.com/Mercurows/SuperbWarfare`）。

---

## 9. Sona 旧实现对照表

> 源码根：`C:\Projects\Sources-1.18.2\com\scarasol\sona\`（1.18.2 / Forge）

### 9.1 听觉 / 曝光相关类逐个对照

| 类 | 路径 | 实际职责 | 需求处置 |
| --- | --- | --- | --- |
| `SoundManager` | `manager/SoundManager.java` | 全局静态听觉中枢：`soundOpen` 开关、`soundWhiteList`（`List<String>`，客户端同步而来）、`isSoundAttractedMob(Mob)`（白名单 → 黑名单 → 默认 `MobType.UNDEAD`）、`insertAi(Mob)`（向 `targetSelector` 加 `NearestAttackableTargetGoal`，优先级 3，只索敌 `SoundDecoy`）、`getIndex(String)` / `getAmplifier(int)`（解析 `"id, amplifier"` 字符串）、`spawnSoundDecoy(Level, x, y, z, amplifier)`、`syncSoundWhiteList(ServerPlayer)` | **整体不复用**。其「听觉判定 + AI 注入 + 诱饵生成」三件事分别被拆到 `HearingApi` / `HearingGoalInjector` + `HearNoiseGoal` / 直接删除。静态 `List`/`boolean` 与「全局 Set/Map」正是需求 §8.1、§19.2 明令避免的形态 |
| `Exposure` (`MobEffect`) | `effect/Exposure.java` | 一个 `MobEffectCategory.HARMFUL` 的 MobEffect，`applyEffectTick` 每次都 `entity.removeEffect(CAMOUFLAGE)`，`isDurationEffectTick` 恒 `true` | **明确排除**（§2 `EXPOSURE MobEffect`） |
| `SoundDecoy` (`Mob`) | `entity/SoundDecoy.java` | 无重力、`setNoAi(true)`、免疫伤害、`hurt` 返回 `false`、不推动实体、`life = 100 * (amplifier + 1)` 后 `discard()`；`finalizeSpawn` 中给自身挂 `EXPOSURE`（`life` tick、`amplifier` 等级）；`FOLLOW_RANGE = 0`、`MOVEMENT_SPEED = 0.0` | **明确排除**（§2 `SoundDecoy Entity`；§43 要求「无 `SoundDecoy` Entity」） |
| `MobMixin` | `mixin/MobMixin.java` | `@Mixin(Mob.class)` 注入 `aiStep` HEAD：当 `ENHANCED_CAMOUFLAGE` 开启且目标带 `CAMOUFLAGE` 且无视线时 `setLastHurtByPlayer(null)` / `setLastHurtByMob(null)` / `setTarget(null)`，记录丢失坐标，待 `navigation.isDone()` 后 `moveTo(lostX, lostY, lostZ, 1)` | **明确排除**（§2 `TargetGoal 修改`、`怪物视觉修改`；§4.2 禁止改 `Mob#getTarget` / `GoalSelector tick`）。注意其「先记住坐标、稍后 moveTo」的形态与本需求「记住声音坐标再前往」**形似但语义不同**——后者不得清 Target |
| `TargetGoalMixin` | `mixin/TargetGoalMixin.java` | `@Mixin(TargetGoal.class)` 注入 `canContinueToUse` RETURN：目标带 `CAMOUFLAGE` 且无视线时强制返回 `false` | **明确排除**（§4.2 禁令含 `TargetGoal`） |
| `SyncSoundPacket` | `network/SyncSoundPacket.java` | 服务端 → 客户端单向包：把 `soundWhiteList` 条目（`byte[]`）与 `soundOpen` 同步给客户端；客户端 handler 内 `SoundManager.addSoundWhiteList(...)` / `setSoundOpen(...)` | **不复用**。方向正确（S→C）但内容无关：新系统 Token / Tag 由服务端权威判定，**不需要把声音白名单下发客户端**（§6） |
| `SoundDecoyPacket` | `network/SoundDecoyPacket.java` | **客户端 → 服务端**上行包：携带 `x/y/z/amplifier`，服务端 handler 调 `SoundManager.spawnSoundDecoy(player.level, ...)` | **明确排除**，且此方向正是 §6 明令禁止的「客户端触发服务端噪声 / 实体」形态 |
| `CommonConfig` | `configuration/CommonConfig.java` | `[Sound System]`：`SOUND_OPEN`（默认 `true`）、`SOUND_WHITELIST`（默认 `["sona:crate, 1", "minecraft:entity.generic.explode, 3", "$chest, 0"]`，格式 `"<soundId>, <amplifier>"`，`$` 前缀为子串匹配）、`SOUND_ATTRACTED_MOB_WHITELIST`（默认空）、`SOUND_ATTRACTED_MOB_BLACKLIST`（默认空，默认所有 undead 被吸引）、`SPRINT_SOUND`（默认 `true`）；`[Misc]` 中 `ENHANCED_CAMOUFLAGE`（默认 `false`） | **逐个替换**：听觉开关 / 名单 → `hearing_mobs` + `hearing_blacklist` Tag（§17）；声音白名单字符串 → `NoiseSpec` + 按类型的半径 / 强度 / 生命周期配置（§41）；`SPRINT_SOUND` → `SPRINT` 噪声类型；`ENHANCED_CAMOUFLAGE` → 删除 |
| `ServerLevelMixin` | `mixin/ServerLevelMixin.java` | `@Mixin(ServerLevel.class)` 注入两个 `playSound(...)` RETURN：命中白名单则在音效坐标 `spawnSoundDecoy(...)` | **不复用**。其思路是「音效即噪声」，恰是 §33.1 禁止的反推路径；新系统要求**服务端确认行为发生 → `HearingApi.emit`** |
| `ClientLevelMixin` | `mixin/ClientLevelMixin.java` | `@Mixin(ClientLevel.class)` 注入 `playLocalSound` RETURN → `spawnSoundDecoy(...)`（客户端会走 `SoundDecoyPacket` 上行） | **明确排除**（§6 禁止客户端噪声权威） |
| `LivingEntityMixin` | `mixin/LivingEntityMixin.java:150-152` | `baseTick` TAIL：`isSprinting()` 且 `SOUND_OPEN` / `SPRINT_SOUND` 开启且 `gameTime % 60 == 0` → `spawnSoundDecoy(level, x, y, z, 0)` | **需要以纯循声方式重新实现**：`SPRINT` 类型噪声，但触发点应从「Mixins `LivingEntity#baseTick`」改为**服务端事件（如 `LivingEntity#tick` 相关 NeoForge 事件）+ `HearingApi.emit`**，且不应为 1.18.2 的 `% 60` 硬编码 |
| `ManagerEventHandler#soundDecoyTarget` | `event/ManagerEventHandler.java:204-208` | 监听 `EntityJoinWorldEvent`，`Mob` 且听觉开启则 `insertAi(mob)` | **形态保留、实现替换**：新系统同样用实体入世界事件，但事件换为 `EntityJoinLevelEvent`，注入目标换为 `addGoal(6, new HearNoiseGoal(...))`，且必须有 `ServerLevel` 判定 + `HEARING_GOAL_ATTACHED` 幂等标记（§19） |
| `ManagerEventHandler#syncSoundList` / `loadSoundWhiteList` | `event/ManagerEventHandler.java:210-222` | 玩家登录时同步白名单；服务器启动时从配置装载白名单与开关 | **不复用**：新系统不需要客户端名单同步；配置改由 server config / Data Map + Tag 驱动 |
| `SonaMod` / `SonaEntities` / `SonaMobEffects` / `SonaSounds` | `SonaMod.java`、`init/*.java` | `SOUND_DECOY` 实体类型、`EXPOSURE` / `CAMOUFLAGE` 效果、音效注册 | **明确排除** |
| `misc_twf` 侧的消费点 | `src/main/java/.../common/block/entity/MonsterEggBlockEntity.java:146-149`（`level.getEntitiesOfClass(LivingEntity.class, AABB.ofSize(blockPos.getBottomCenter(), 8.0D, 4.0D, 8.0D), livingEntity -> livingEntity.hasEffect(SonaMobEffects.EXPOSURE.get()))`，并以 `mobEffectInstance.getAmplifier()` 分支）；`src/main/java/.../mixin/tacz/ModernKineticGunScriptAPIMixin.java:101-109`（注入 TaC `SoundManager.sendSoundToNearby` 调用点，给射手加 `EXPOSURE`，等级按 `useSilenceSound ? 0 : 3`，受 `MISCTWFCommonConfig.TACZ_WHITELIST` 反选） | 旧整合包中「怪物卵用 EXPOSURE 感知附近枪声 / 脚步声」与「TaC 枪声给玩家挂 EXPOSURE」两处直接耦合 | **必须迁移**（§35）：`MonsterEgg` 改为 `HearingApi.query(AABB) + findStrongest(...)` 或 `NoiseEmitEvent.Post`；TaC mixin 整体作废（1.21.1 已换 SBW），改由 `compat/superbwarfare` 适配层产出 `GUNSHOT` / `SUPPRESSED_GUNSHOT` 噪声 |

### 9.2 Sona 听觉链路的实际数值语义（用于避免误迁移）

- `insertAi` 使用 `mob.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(mob, Mob.class, 5, false, false, livingEntity -> livingEntity instanceof SoundDecoy))` —— 即 Sona 的「循声」是**把 `SoundDecoy` 实体变成 Target，让原版索敌 AI 去追**。
- `spawnSoundDecoy` 给 `SoundDecoy` 挂 `EXPOSURE`，效果等级 = 声音白名单条目里的 `amplifier`，持续时间 `life = 100 * (amplifier + 1)` tick。
- **实际吸引半径的语义只存在于配置注释里**（`CommonConfig.java:236-241`）：`"minecraft:entity.generic.explode, 3"` 表示「`(3 + 1) * (0.3 * FOLLOW_RANGE)` 范围内的怪物被吸引」。也就是说 Sona 的听觉半径**依赖 `FOLLOW_RANGE`**，而本需求 §7.3 明确「`radius` 不等于 `FOLLOW_RANGE`」、§4.2 明确禁止修改 `FOLLOW_RANGE`。**这是两套系统最根本的数值差异，迁移时不可沿用 `0.3 * FOLLOW_RANGE` 公式。**
- 白名单格式 `"<soundId>, <amplifier>"`，`$` 前缀表示「声音 id 包含该子串」（`CommonConfig.containSearch`），`#` 前缀是物品 Tag 匹配（`tagSearch`，听觉链路未使用）。`SoundManager.getIndex` 先精确匹配再子串匹配。

### 9.3 结论：Sona 的四个核心类我们是否完全不用

| 类 | 结论 |
| --- | --- |
| `SoundManager` | **完全不用**（不复用任何方法；其职责被 `HearingApi` / `NoiseManager` / `HearingGoalInjector` 拆分替代） |
| `SyncSoundPacket` | **完全不用**（新系统无客户端名单同步需求；噪声不跨端） |
| `SoundDecoy` | **完全不用**（实体方案被内存 `NoisePulse` 索引替代；§2 明确排除） |
| `Exposure` | **完全不用**（§2 明确排除；`EXPOSURE` / `CAMOUFLAGE` 体系整体废弃） |

**需要以纯循声方式重新实现**的能力（从 Sona 迁移过来但改变实现）：① 玩家的 `SPRINT` 脚步噪声；② 方块 / 容器 / 门 / 破坏 / 玻璃破碎的方块噪声（Sona 通过音效白名单隐式覆盖，新系统要求显式 `emit`）；③ 枪声（Sona 走 TaC 音效白名单 + `EXPOSURE`，新系统走 SBW `ShootEvent` + `GUNSHOT`）；④ 爆炸（Sona 走 `minecraft:entity.generic.explode` 音效，新系统走爆炸事件）；⑤ 怪物卵「附近是否有声音」判定（Sona 走 `EXPOSURE` 效果查询，新系统走 `HearingApi.query/findStrongest`）。

---

## 10. 文档中的疑点与需澄清问题

### A. 命名空间冲突（**必须拍板**）

需求 §17 与 §37 使用 `winter_frontier`（`#winter_frontier:hearing_mobs`、`#winter_frontier:hearing_blacklist`、稳定包 `winter_frontier.hearing.api`），但本工程实际 modid 为 `misc_twf`（根工程）与 `misc_twf_zombie_animals`、`misc_twf_wildlife`、`misc_twf_industry`、`misc_twf_adventure`（M1~M4），主包为 `com.hexagram2021.misc_twf*`。需确认：

1. Tag 命名空间究竟用 `winter_frontier` 还是 `misc_twf`？
2. 循声系统落在**哪个模块**？（§3.1 提到 `misc_twf` 只能 import `hearing.api.*`，暗示 hearing 是独立于 `misc_twf` 的单元；但 §5 建议的包结构又写成 `hearing/` 顶层目录。）
3. 包名是否统一为 `com.hexagram2021.misc_twf*.hearing...`（符合主人命名洁癖与工程既有约定）？

### B. 配置项命名不一致：`investigateSpeed` vs `speed_modifier`

§18 列出的数值配置名为 `hearingMultiplier` / `queryInterval` / `investigateSpeed` / `arrivalDistance`；§41 的 TOML 却写 `query_interval_ticks` / `speed_modifier` / `arrival_distance`。需确认 `speed_modifier` 是否就是 `investigateSpeed`，以及 `HearingProfile` 的最终字段名（§48 伪代码使用 `profile.hearingMultiplier()` / `profile.maxPathFailures()`，即 record 风格访问器）。

### C. SBW 消音器语义陷阱（**高优先级**）

`GunItem.getCustomSoundRadius` 返回 `data.attachment.get(AttachmentType.BARREL) == 2 ? 0.6 : 1`，而调用处是 `rawData.soundRadius += getCustomSoundRadius(gunData)`（**加法**）。因此：

- 装消音器后 `compute().soundRadius` **变大**（基础值 + 0.6），不是变小；
- 而 `playFireSounds` 中三层音量是 `soundRadius × 0.4 / 0.7 / 1.0`；
- 需求 §33.3 要求「消音武器进入正确 type/半径」，但**未定义 `SUPPRESSED_GUNSHOT` 的半径策略**。

需业务方拍板：`SUPPRESSED_GUNSHOT` 半径 = `soundRadius × 系数`（系数取值？）还是独立配置的固定半径？是否需要在适配层完全绕过 `soundRadius` 而改用配置表？

### D. §30 内部自相矛盾（方案 A 与方案 B）

§30 先写「**方案 A（推荐）**：Noise 过期即结束调查」，随后写「**本项目更推荐方案 B**」并给出定义（已记录 `destination` 则允许继续调查）。两处「推荐」冲突。按后文与 §49 第 6 问（「本文推荐『继续调查』，请确认」）判断，**实际意图为方案 B**，但需书面确认，因为它直接影响 `canContinueToUse()` 是否检查 `expireTick`——且与 §24「Noise 已过期」作为结束条件**直接冲突**。

### E. §24 与 §30-B 的判定冲突（同上根因）

§24 把「Noise 已过期」列为 `canContinueToUse()` 应结束的条件；§30-B 又说已开始调查的可以继续走到旧坐标。两者必须二选一定稿，否则实现会二义。

### F. 各声音类型的半径 / 强度默认值全部缺失

§7.5 只给了 **lifetime 区间**（脚步/落地 20~40、开门/容器 30~60、方块破坏 40~80、枪声 60~120、爆炸 80~160），§7.4 只给了 `strength` 的建议范围 `0.0~1.0`（并用词为「建议」，不是默认值）。文档**完全没有**给出：

- 每种 `type` 的默认 `radius`（尤其 `SPRINT` / `LAND` / `DOOR` / `CONTAINER` / `BLOCK_BREAK` / `GLASS_BREAK`）；
- 每种 `type` 的默认 `strength`（若统一取 `1.0`，请明示）；
- 各 `type` 的 `ResourceLocation` 具体命名（`misc_twf:gunshot`？`winter_frontier:gunshot`？）
- §23 第 5 步「`score > 最低阈值`」的**最低阈值数值与配置项名**（§41 配置草案中也缺失）；
- `findBestAudible` 的 `predicate` 由谁提供、默认 `canHearType` 是否按 `type` 白名单过滤。

### G. Data Map 选型仍未定（§18 / §49 第 7 问）

文档把「方案 A：Tag + server config」与「方案 B：NeoForge Data Map」并列，评审建议为「P0 至少完成 Tag + server config；若程序熟悉 Data Map 可直接采用 Data Map」。**当前工程实测未使用 NeoForge Data Map**（全仓 `DataMapType` / `RegisterDataMapTypesEvent` / `dataMap` 检索 0 命中；根工程 `common/config` 下仅有 `package-info.java`，模块配置为 `MISCTWFZombieAnimalsConfig` / `MISCTWFWildlifeConfig` / `MISCTWFIndustryConfig` 风格）。建议：**P0 用 Tag + server config，`HearingProfile` 以 record + 静态映射表承载，P1 再评估 Data Map**。需主人确认。

### H. `NoisePulse` 声明为 `record` 但合并需要可变更新

§7 建议 `public record NoisePulse(...)`，§12.3 又要求合并时「`position` 更新为最新位置 / `expireTick` 延长 / `radius` 取 max / `strength` 取 max」。record 不可变，需明确：

- 是否改为普通 `final class` + 私有字段 + 合并方法；或
- 合并时**替换**为新 record 实例（此时「保持原 ID」需显式传入旧 ID）。

同时 `lastEmitTick` 是「推荐增加」而非强制，需拍板是否纳入 P0。

### I. `NoiseEmitEvent.Pre` 的可取消性未定义

§13.1 说 Pre「可以 cancel」，但 §47 的 API 草案未给出 `NoiseEmitEvent` 的实现形态。NeoForge 中取消需 `implements ICancellableEvent`，需明确：

- `NoiseEmitEvent` 是否 `extends Event implements ICancellableEvent`？
- Pre 修改 `position` 是否允许（§13.1 说「由程序评估，建议允许但需校验」）？
- Pre 修改 `spec` 的字段是否要求重新过 §11 的校验（`radius > 0` 等）？

### J. `HearingProfile` 的载体与来源

§18 把 `hearingMultiplier` / `queryInterval` / `investigateSpeed` / `arrivalDistance` 列为「需要」的数据，§48 伪代码以 `HearingProfile profile` 注入 `HearNoiseGoal`。但：

- `HearingProfile` 是「每实体类型」还是「每 Mob 实例」？
- 若为每实体类型，`queryInterval` 等「行为参数」是否也应每类型可配（这会让配置矩阵膨胀）？
- `HearingProfile` 中是否包含 `maxPathFailures`（§48 伪代码用到 `profile.maxPathFailures()`，但 §41 把它列在 `[goal]` 全局配置里）？

### K. ZT2 / Improved Mobs / Hordes / CrawlMod 的实体类型清单缺失

`hearing_mobs` / `hearing_blacklist` 需要具体实体 id，但文档只给出 Tag 名与「必须显式进入 Tag」的原则，**没有列出任何实体 id**。需业务方提供：

- 哪些整合包实体（ZT2 感染者、Hordes 生成物、`zombie_extreme` 的 `night_hunter` / `infected_juggernaut` / `infected_hazmat` / `patient_zero` 等）应入 `hearing_mobs`；
- 默认 `hearing_blacklist` 是否为空。

### L. §3.1 与 §3.4 关于 `misc_twf` / Hordes 的边界

- §3.1 提到 `misc_twf` 的方块/机关「附近是否发生过声音」逻辑（即怪物卵），但 M4 冒险模块「未开始」（`docs/MODULARIZATION.md` §10）；
- §3.4 说「允许尸潮生成的符合 Tag 的感染者自然获得 `HearNoiseGoal`」，但 Hordes 生成的实体是否走 `EntityJoinLevelEvent`（部分刷怪路径可能不触发）需运行时验证，否则 Hordes 验收点「Horde 生成的 Zombie 获得 Goal」可能不成立。

### M. 载具武器是否纳入仍需业务确认

虽然源码已证实载具武器走 `ShootEvent`（§7.3），但需求 §44.7 的用词是「**载具武器（若走支持入口）**」，§49 第 4 问亦为待确认项。需业务方明确：坦克炮 / 迫击炮 / 直升机机炮 / 无人机自杀爆炸是否都算 `GUNSHOT` / `EXPLOSION` 噪声源，以及是否对载具噪声做额外限流（载具射速高、单次战斗噪声量大）。

### N. SBW 武器白名单与配置入口缺失

旧实现有 `MISCTWFCommonConfig.TACZ_WHITELIST`（按 `GunId` 排除某些枪不吸引怪物）。新需求§41 配置草案中**没有对应项**，也未说明是否保留「按枪械排除」能力。需确认是否新增 `[sbw] gun_whitelist` / `gun_blacklist` 或 `GunNoiseResolver` 中的 per-gun 覆盖表。

### O. §43 与 §42.5 的数值表述差异

- §42.5 写「一个 Mob 默认最多 **20 tick / 次重寻路**」；
- §41 配置项为 `repath_interval_ticks = 20`；
- §27 写「`repathInterval = 20 tick`」。

三者一致，但 §43 未重复该数值，且 §43 亦未给出「每 tick 查询数上限」的**绝对数值指标**（只有「不出现同 tick 峰值」的定性描述）。若验收需要可量化门槛（如 spark 中 `NoiseScoring` / `NoiseManager` 的 ms/tick 上限），需业务方补充。

### P. 生命周期与 Goal 状态的重启 / 重连语义

§8.2 明确 `NoiseManager` 不持久化、服务器重启后全部消失；但文档未说明**单机退出重进世界**时已注入的 `HEARING_GOAL_ATTACHED` 标记与已注入 Goal 是否需要重新注入（`EntityJoinLevelEvent` 在区块重载时会再次触发，故依靠 Attachment + 幂等即可；但若实体从 NBT 恢复且 Goal 未持久化，需确认重注入路径）。

### Q. 调试指令的权限级别与输出通道

§40 给出 `/hearing debug <player> on|off`、`/hearing stats`、`/hearing emit <type> <radius> <strength>`，但未定义：

- 权限等级（2 / 3 / 4）；
- `debug` 输出通道（聊天 / actionbar / HUD / 粒子）；
- `emit` 未给 `strength` / `lifetime` 时的默认值来源。

### R. `NoiseManager.get()` 的公开性

§8 写「逻辑上 `NoiseManager.get(serverLevel)`」，但 §5 把 `NoiseManager` 放在 `core/`（内部包，§37 明确「不承诺二进制稳定」）。外部模组（如 `misc_twf` 的怪物卵）只能通过 `HearingApi` 访问，需确认 `NoiseManager` 完全 internal，不对外暴露。

---

## 附录：建议的模块落地拆分草案（无需实现）

> 以下为**建议草案**，供主人拍板；不涉及任何代码改动。命名遵循工程既有约定（`com.hexagram2021.<modid>`、全小写下划线包名、modid 全小写）。

### 1. 归属决策建议

建议把循声系统落为**独立模块 `misc_twf_hearing`**（M5），理由：

- 需求 §5 明确核心模块不得 import ZT2 / ImprovedMobs / ServerCore / TheHordes / CrawlMod / SuperbWarfare，第三方兼容只能在 `compat/`——独立模块能天然保证这条边界；
- 需求 §3.1 提到「`misc_twf` 只能 import `hearing.api.*`」，说明 hearing 是 `misc_twf`（M4 冒险侧）的**上游依赖**，而非其内部包；
- M2 已有模块并列布局（`modules/misc_twf_*`），新增 M5 与现有 `settings.gradle` / 独立网络包 id / 独立创造页约定一致；
- 若主人坚持单一命名空间 `winter_frontier`，则 modid 可定为 `winter_frontier_hearing`，但需与 M1~M4 的 `misc_twf_*` 命名风格取舍。

依赖方向建议：`M4 misc_twf_adventure`（怪物卵）→ **optional** → `M5 misc_twf_hearing`；`M5` → **optional** → `superbwarfare`（仅 `compat/` 内，编译期可 optional，运行期判空降级）。**M5 不依赖 M1/M2/M3**。

### 2. 包结构建议

```text
com.hexagram2021.misc_twf_hearing
├─ MiscTwfHearing.java                      // @Mod 主类，MODID = "misc_twf_hearing"
├─ api/                                     // 稳定包：外部只允许 import 这一层
│  ├─ HearingApi.java                       // emit / query / findStrongest / findBestAudible / resolveSource
│  ├─ NoiseSpec.java                        // 输入规格 record：type / radius / strength / lifetimeTicks
│  ├─ NoiseSnapshot.java                    // 只读快照 record：id / position / type / radius / strength / createdTick / lastEmitTick / expireTick / sourceUuid
│  ├─ NoiseEmitEvent.java                   // Pre（可取消、可改 spec / position）+ Post（只读）
│  └─ NoisePredicate.java                   // 可选：类型 / 来源过滤谓词
├─ core/                                    // 内部实现，不承诺二进制稳定
│  ├─ NoiseManager.java                     // 每 ServerLevel 一个：持有索引、id 计数器、清理与容量保护、统计计数
│  ├─ NoisePulse.java                       // 内部可变 Pulse（合并时原地更新）
│  ├─ NoiseSpatialIndex.java                // ChunkPos.asLong → Long2ObjectMap<List<NoisePulse>>（fastutil）
│  └─ NoiseScoring.java                     // 唯一评分实现：effectiveRadius / distanceFactor / freshnessFactor
├─ ai/
│  ├─ HearNoiseGoal.java                     // MOVE-only、Priority 6、节流 + 错峰 + 重寻路 + 切换阈值
│  └─ HearingMemory.java                     // 可选：若状态全在 Goal 内则省略
├─ event/
│  ├─ HearingGoalInjector.java               // EntityJoinLevelEvent(LOWEST) + HEARING_GOAL_ATTACHED 幂等注入
│  ├─ NoiseLifecycleHandler.java             // ServerTickEvent.Post 周期清理 + 维度卸载 / 服务器停止释放
│  └─ VanillaNoiseEventHandler.java          // 原版声音源：SPRINT / LAND / DOOR / CONTAINER / BLOCK_BREAK / GLASS_BREAK / EXPLOSION
├─ compat/
│  └─ superbwarfare/
│     ├─ SuperbWarfareNoiseCompat.java       // ShootEvent.Post 挂接 → emit
│     └─ GunNoiseResolver.java               // gunId + soundRadius + isSilent → NoiseSpec
├─ config/
│  ├─ MISCTWFHearingConfig.java              // server config：[goal] / [noise] / [debug] / [sound] 各类型半径强度寿命
│  └─ HearingProfile.java                    // record：hearingMultiplier / queryInterval / investigateSpeed / arrivalDistance / maxPathFailures
├─ server/                                   // （与工程既有 server/ 约定一致时使用）
│  └─ MISCTWFHearingSavedData.java           // 建议不建：本系统明确不持久化
├─ debug/
│  ├─ HearingDebugCommand.java               // /hearing debug|stats|emit
│  └─ HearingDebugLogger.java                // rate-limited 日志 + 管理员专用展示
└─ datagen/                                  // 可选：hearing_mobs / hearing_blacklist Tag 数据生成
   └─ MISCTWFHearingTagProvider.java
```

资源侧（`modules/misc_twf_hearing/src/main/resources/`）：

```text
data/misc_twf_hearing/tags/entity_type/hearing_mobs.json        // 或按最终命名空间
data/misc_twf_hearing/tags/entity_type/hearing_blacklist.json
data/misc_twf_hearing/hearing/sound_types/*.json                // P1：按类型半径/强度/寿命数据化
```

### 3. 需要的新类清单（每个一句话职责）

| 类 | 一句话职责 |
| --- | --- |
| `MiscTwfHearing` | 模块主类：注册配置、事件总线监听、命令、可选 compat 初始化 |
| `HearingApi` | 唯一的公开门面：`emit` / `query` / `findStrongest` / `findBestAudible` / `resolveSource`，全部服务端主线程 |
| `NoiseSpec` | 调用方输入的噪声规格（type / radius / strength / lifetimeTicks），含 §11 校验 |
| `NoiseSnapshot` | 对外只读的噪声快照，绝不暴露内部 `NoisePulse` 引用 |
| `NoiseEmitEvent` | Pre（可取消、可改 spec/position）与 Post（只读）两阶段事件 |
| `NoisePredicate` | 可选的噪声过滤谓词（按类型 / 来源 / 强度） |
| `NoiseManager` | 每 `ServerLevel` 一个的噪声中枢：id 分配、合并、清理、容量保护、统计 |
| `NoisePulse` | 内部可变噪声记录：`id` / `position` / `type` / `radius` / `strength` / `createdTick` / `lastEmitTick` / `expireTick` / `sourceUuid` |
| `NoiseSpatialIndex` | 按 `ChunkPos.asLong` 分桶的 fastutil 空间索引，只存源 Chunk、不复制传播范围 |
| `NoiseScoring` | 全局唯一的评分实现：`score = strength × distanceFactor × freshnessFactor` |
| `HearNoiseGoal` | 只声明 `Flag.MOVE` 的循声 Goal：节流查询、选择最佳可听噪声、限频重寻路、切换阈值、目标让路 |
| `HearingMemory` | 可选：抽取 Goal 状态（当前噪声 id / destination / 失败计数）以便复用与调试 |
| `HearingGoalInjector` | `EntityJoinLevelEvent` 中按 Tag / 黑名单 / Attachment 幂等注入 `HearNoiseGoal` |
| `NoiseLifecycleHandler` | `ServerTickEvent.Post` 周期清理、维度卸载与服务器停止时释放 `NoiseManager` |
| `VanillaNoiseEventHandler` | 服务端权威地捕获原版声音源行为（Sprint / Land / Door / Container / BlockBreak / GlassBreak / Explosion）并 `emit` |
| `SuperbWarfareNoiseCompat` | SBW 适配层：监听 `ShootEvent.Post`，只做「确认开火 → 取参数 → emit」 |
| `GunNoiseResolver` | 由 `gunId` / `soundRadius` / 消音配件推导 `NoiseSpec`（区分 `GUNSHOT` / `SUPPRESSED_GUNSHOT`） |
| `MISCTWFHearingConfig` | server config：`[goal]` / `[noise]` / `[debug]` / 各噪声类型半径强度寿命 |
| `HearingProfile` | 每实体类型（或每实体）的听觉数值载体（Design 见 §10 疑点 J） |
| `HearingDebugCommand` | `/hearing debug|stats|emit` 三条子命令与权限控制 |
| `HearingDebugLogger` | rate-limited 日志与管理员专属 debug 展示（Mob UUID / Noise ID / type / score / distance / effectiveRadius / 结束原因） |
| `MISCTWFHearingTagProvider` | 可选数据生成：`hearing_mobs` / `hearing_blacklist` Tag |

### 4. 与现有 4 模块的交互点

| 模块 | 交互点 | 方向 | 说明 |
| --- | --- | --- | --- |
| **M1 `misc_twf_zombie_animals`** | 僵尸动物实体（感染者）进入 `hearing_mobs` Tag；Hordes 豁免 mixin 与 `MISCTWFImmunitySavedData` **不受影响** | M1 →（数据）→ M5 | 循声只在 `EntityJoinLevelEvent` 时 `addGoal`，不触碰 M1 的免疫存档 / Hordes mixin / 僵尸化逻辑。若 M1 内的自定义感染者需要特殊听觉倍率，通过 `HearingProfile` 配置，不改 M1 代码。**需确认**：M1 实体是否走 `EntityJoinLevelEvent` 注入；Hordes 生成路径是否触发该事件（§10 疑点 L） |
| **M2 `misc_twf_wildlife`** | 无直接交互 | — | 农牧生态不产生有玩法意义的噪声源；不建议为动物加入 `hearing_mobs`（§17 明确「不给所有动物自动加入」）。若将来有「被枪声惊跑的动物」，属独立需求，不进 hearing core |
| **M3 `misc_twf_industry`** | 工业机械噪声（回收炉 / 模具分离台 / 紫外线灯）**当前不建议**纳入 P0 | 暂不交互 | 需求 §34 明确「只处理有明确玩法意义的行为」，且禁止「监听所有 SoundEvent」。工业机械若纳入，应新增独立 `type` 并按配置给低频 / 低强度，避免持续噪声把感染者全部吸到工厂。**需业务方确认是否纳入** |
| **M4 `misc_twf_adventure`** | ①**怪物卵 `MonsterEggBlockEntity`**：删除 `SonaMobEffects.EXPOSURE` 依赖，改为 `HearingApi.query(AABB.inflate(8))` + `findStrongest(...)` 或监听 `NoiseEmitEvent.Post`（§35）；②**TaC 枪声 mixin**（`ModernKineticGunScriptAPIMixin`）整体作废，改由 M5 的 `compat/superbwarfare` 产出枪声噪声；③`MISCTWFCommonConfig.TACZ_WHITELIST` 迁移到 M5 的枪械白/黑名单配置（§10 疑点 N） | M4 →（optional 依赖）→ M5 | M4 目前「未开始」（`docs/MODULARIZATION.md` §10），因此该迁移可与 M4 拆分同期进行；M5 缺失时 M4 应判空降级，不崩溃 |
| **根工程 `misc_twf`** | 迁移期作为 M4 的暂存地，需移除 `com.scarasol.sona.*` 全部 import | 根 →（transitional）→ M5 | 与 `MODULARIZATION.md` 既有的「过渡期根工程引用 M1」模式一致 |

### 5. 落地顺序建议（对齐 §46 优先级）

1. **阶段 0（裁决）**：定稿 §10 中的 A~R 疑点，特别是命名空间与模块归属（A）、§30 方案选择（D/E）、各类型半径/强度默认值（F）、Data Map 选型（G）、SBW 消音半径策略（C）。
2. **阶段 1（core + api）**：`NoiseSpec` / `NoisePulse` / `NoiseSnapshot` / `NoiseSpatialIndex` / `NoiseScoring` / `NoiseManager` / `HearingApi` / `NoiseEmitEvent` + 过期清理 + 合并 + 容量保护 + 单测（§45 前十项）。
3. **阶段 2（ai + 注入）**：`HearingGoalInjector`（`EntityJoinLevelEvent` + Attachment 幂等）、`HearNoiseGoal`（MOVE-only / 节流 / 重寻路 / 切换阈值 / Target 让路）、`hearing_mobs` / `hearing_blacklist` Tag 与配置。
4. **阶段 3（声音源）**：`VanillaNoiseEventHandler`（`SPRINT` / `LAND` / `DOOR` / `CONTAINER` / `BLOCK_BREAK` / `GLASS_BREAK` / `EXPLOSION`）+ 爆炸通用事件挂接。
5. **阶段 4（SBW compat）**：`ShootEvent.Post` 挂接 + `GunNoiseResolver` + 消音分支 + 载具武器验证（§44.7 全部验收点）。
6. **阶段 5（迁移 + 调试）**：`misc_twf` 怪物卵迁移（§44.8）、`/hearing` 三条命令与 debug 日志、spark 三组基线（§43）。
7. **阶段 6（兼容回归）**：ZT2 / Improved Mobs / CrawlMod / ServerCore / The Hordes 逐项过 §44 矩阵，重点验证 §32 的 10 条 ServerCore 硬性要求。
