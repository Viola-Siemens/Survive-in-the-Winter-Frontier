# 循声模块 `misc_twf_hearing` H4 验收测试文档

> 用途：H4（联编验收）人工测试记录表。主人按"步骤/命令"逐条执行，把结果填进 **实测结果 / 结论 / 证据** 三列，完成后把本文件交回即可。
> 依据：`docs/MODULARIZATION.md` 第 11 章（尤其 11.12 性能与调试、11.14 阶段与验收、11.15 决策）与业务方需求 §43（性能验收场景）、§44（兼容验收矩阵）。
> 判定记号：`√` 通过、`×` 失败、`?` 无法判定（需补测说明）；结论列请勿留空。

---

## 0. 填表说明与证据采集

1. **每条尽量附证据**：截图、`logs/latest.log` 相关片段、spark 链接或导出的 JSON 路径、`/hearing stats` 输出。
2. **配置改动需记录**：任何非默认配置请写进备注列（默认值见 §11.8）。
3. **证据采集命令**：
   - 噪声与 AI 诊断：`/hearing debug <你的ID> on` → 观察聊天栏 `[hearing] ...` 行（investigates / switches to / stopped investigating）
   - 运行统计：`/hearing stats`
   - 手动制造噪声：`/hearing emit <type> <radius> <strength>`（例如 `/hearing emit misc_twf:gunshot 48 1.0`）
   - 性能采样：`/spark profiler start` → 测试场景跑 60 秒 → `/spark profiler stop`
   - 实体数量：`/execute as @e[type=minecraft:zombie] run ...` 或用整合包自带统计；建议用 `/spark health` 记录 TPS/MSPT
4. **测试环境建议**：创造/生存各测一轮；单人存档与专服各测一轮（至少单人一次）。
5. **关于 `/hearing emit` 的来源归属（重要）**：本命令**由实体执行时噪声归属该实体**（可用玩家身份执行来验证"合并"）；**由命令方块/控制台执行时没有来源实体**，属于**匿名噪声**，按需求 §12.2 默认**不合并**——因此"命令方块连发 5 次得到 5 个 pulse"是**正确行为**，不是缺陷。
6. **`queries=0` 的常见原因**：附近没有命中 `misc_twf:hearing_mobs` 白名单的实体时，噪声只会入索引、不会被任何 Goal 查询；验证查询/统计前请先确认附近确有白名单实体。

---

## 1. 测试环境（T-ENV）

| ID | 测试项 | 步骤 | 预期 | 实测结果 | 结论 | 证据/备注 |
| --- | --- | --- | --- | --- | --- | --- |
| T-ENV-1 | 模块可加载 | 把 `modules/misc_twf_hearing/build/libs/misc_twf_hearing-4.0.0.jar` 放入 `mods/`，启动游戏 | 模组列表可见 "The Winter Frontier: Hearing"；无 `Missing or unsupported mandatory dependencies` | √ | √ | |
| T-ENV-2 | 配置文件生成 | 首次启动后检查 `config/` | 生成 `misc_twf_hearing-server.toml`，含 `[goal]/[noise]/[vanilla]/[noise_type.*]` 各段 | √ | √ | |
| T-ENV-3 | 无强制依赖 | 只装本模块 + NeoForge 启动 | 正常进游戏（SW 缺失也不崩） | √ | √ | |
| T-ENV-4 | SW 可选适配 | 装 SBW 后启动，再卸载 SBW 启动 | 两次均正常；卸载后枪声不再产生噪声但其余来源正常 | √ | √ | |
| T-ENV-5 | 命令可用性与权限 | 以 OP 执行 `/hearing stats`；以非 OP 执行 | OP 正常返回；非 OP 提示权限不足 | √ | √ | |

---

## 2. 代码级静态红线（无需运行时，已由开发预核验）

| ID | 检查项 | 检查方式 | 预期 | 实测结果 | 结论 | 证据/备注 |
| --- | --- | --- | --- | --- | --- | --- |
| S-1 | 不操作 Target | 模块源码检索 `setTarget` / `targetSelector` | 0 命中 | **0 命中（已核验）** | √ | 预填 |
| S-2 | 不修改 AI/寻路底层 | 模块 `misc_twf_hearing.mixins.json` | `mixins`/`client` 均为空数组（零 Mixin） | **空数组（已核验）** | √ | 预填 |
| S-3 | 不修改 FOLLOW_RANGE / Sensing | 源码检索 `FOLLOW_RANGE` / `Sensing` / `NodeEvaluator` | 0 命中 | **0 命中（已核验）** | √ | 预填 |
| S-4 | 不监听全部 SoundEvent | 源码检索 `SoundEvent` 监听 | 0 命中（只监听行为事件） | **0 命中（已核验）** | √ | 预填 |
| S-5 | 噪声不持久化 | 源码检索 `SavedData` / `serializer` | 0 命中（仅非持久化 Attachment） | **0 命中（已核验）** | √ | 预填 |
| S-6 | 服务端权威 | 源码检索噪声产生入口 | 全部位于服务端事件（无客户端 emit） | **已核验** | √ | 预填 |

---

## 3. 核心链路（T-CORE）

| ID | 测试项 | 步骤 | 预期 | 实测结果 | 结论 | 证据/备注 |
| --- | --- | --- | --- | --- | --- | --- |
| T-CORE-1 | 手动 emit 入索引 | `/hearing emit misc_twf:gunshot 48 1.0` 后立即 `/hearing stats` | 该维度 `pulses` +1；`emitted` +1 | √ | √ | |
| T-CORE-2 | 过期清理 | emit 后等待 12 秒（≥120 tick）再 `/hearing stats` | `pulses` 回落；`chunks` 相应减少 | √ | √ | |
| T-CORE-3a | 合并（有来源：玩家执行） | 以**玩家身份**（非命令方块）在聊天栏连续快速执行 `/hearing emit misc_twf:gunshot 20 0.5` 5 次（间隔 < `merge_window_ticks`=5 tick、原地不动）；随后 `/hearing stats` | 只保留 **1 个** pulse（`merged` +4），`pulses` 不按次数线性增长；或直接用真实枪械连射验证 | √ | √ | |
| T-CORE-3b | 匿名噪声**不**合并（命令方块/控制台） | 用命令方块/控制台在同一坐标触发 5 次 `/hearing emit misc_twf:gunshot 20 0.5` | 产生 **5 个独立** pulse，`merged` 不变 —— 这是需求 §12.2 的**预期行为**（匿名噪声默认不合并，避免相邻两个机关/爆炸被误并） | √ | √ | |
| T-CORE-4 | 合并后字段语义 | 在 T-CORE-3a 之后用 `/hearing debug`/stats 观察 | 合并保持同一 `id`、`createdTick` 不变、`lastEmitTick` 刷新、`expireTick` 延长、`radius/strength` 取较大 | √ | √ | |
| T-CORE-5 | 半径语义 | 站在距离噪声源 20 格处，emit 半径 10 的噪声，观察附近僵尸反应 | 距离 > 有效半径 → 怪物不前往（“发生位置在区域内”与“传播覆盖”两种语义未被混用） | √ | √ | |
| T-CORE-6 | 类型默认参数生效 | 改配置 `[noise_type.gunshot] radius = 5.0`，重启后 emit 半径由配置决定 | 怪物只有贴近时才有反应 | √ | √ | |
| T-CORE-7 | lifetime 区间随机 | 连续 emit 同类型 5 次，记录每次 `/hearing stats` 中过期时间（或 debug 输出的 `expire`） | 过期时间落在 `lifetime_min ~ lifetime_max-1` 区间且**不固定** | √ | √ | |

---

## 4. AI 行为（T-AI）

前置：`data/misc_twf/tags/entity_type/hearing_mobs.json` 加入 `minecraft:zombie`（或整合包内感染者 id），并确保其不在 `hearing_blacklist` 中。

| ID | 测试项 | 步骤 | 预期 | 实测结果 | 结论 | 证据/备注 |
| --- | --- | --- | --- | --- | --- | --- |
| T-AI-1 | 闻声前往 | 远处（30~40 格）放一只僵尸，玩家在另一侧开枪/`/hearing emit` 制造噪声 | 僵尸朝**噪声坐标**前进；到达附近后停下（不追击玩家） | √ | √ | |
| T-AI-2 | 不接管 Target | T-AI-1 过程中玩家现身在其视线内 | 僵尸立即放弃循声、按原版/其它 AI 扑向玩家（循声让路） | √ | √ | |
| T-AI-3 | 到达判定 | 观察到达后行为 | 到达 `arrival_distance`（默认 3 格）内即结束，不反复抖动 | √ | √ | |
| T-AI-4 | 路径失败上限 | 把噪声坐标放在怪物无法到达处（隔墙/岩浆对面），emit 半径足够大 | 反复失败至 `max_path_failures`（默认 3）后放弃；无死循环重寻路 | | | |
| T-AI-5 | 新声音切换 | 先 emit 小半径弱声，怪物前往中再 emit 同方向更近/更强声音 | 仅当新评分 > 当前 `switch_threshold`（1.15×）才切换；切换受 `switch_cooldown_ticks` 限制 | | | |
| T-AI-6 | 过期后继续前往（D8） | emit 一个 lifetime 很短的噪声，让怪物刚出发后噪声过期 | 怪物**继续**走到旧坐标（不因过期中断） | | | |
| T-AI-7 | 查询节流 | `/hearing debug` 观察 20 只僵尸同场景 | 输出有错峰（非同一 tick 全部查询）；`/hearing stats` 的 `queries` 增长速度约为 实体数/查询间隔 | | | |
| T-AI-8 | 只声明 MOVE | 观察怪物边循声边正常转头/跳跃/被其它行为调用 | 无异常僵硬、无其它 Goal 被压制 | | | |
| T-AI-9 | 黑白名单与默认关闭 | 把某实体只放进 `hearing_blacklist`（且在白名单里） | 该实体完全不循声（黑名单优先） | | | |
| T-AI-10 | 幂等注入（单人重进世界） | 记下僵尸 UUID，退出存档重进，再次观察循声行为 | 行为正常且**没有重复添加 Goal**（不会出现速度叠加/重复寻路） | | | |
| T-AI-11 | hearing_mobs 为空 | 清空白名单数据包后重载 | 无任何实体获得循声（不默认给任何实体） | | | |

---

## 5. 兼容与安全边界（T-COMPAT / T-SAFE）

| ID | 测试项 | 步骤 | 预期 | 实测结果 | 结论 | 证据/备注 |
| --- | --- | --- | --- | --- | --- | --- |
| T-COMPAT-1 | 原版僵尸 | 无 AI 模组环境跑 T-AI-1~T-AI-4 | 全部符合预期 | | | |
| T-COMPAT-2 | ZombieTactics2 | 装 ZT2 后重复 T-AI-1/2/5 | ZT2 行为不被破坏；循声能正常让路；无 Goal 冲突性抖动 | | | |
| T-COMPAT-3 | Improved Mobs | 装 IM 后重复 T-AI-1/2；并触发其偷窃/破坏/爬梯行为 | 高优先级 MOVE 行为可自然抢占循声；循声不改其索敌/寻路 | | | |
| T-COMPAT-4 | CrawlMod | 触发爬行 | CrawlGoal 自然抢占；循声**不**强制站立、不改 Pose；爬行结束后可继续循声 | | | |
| T-COMPAT-5 | ServerCore | 远处（未激活区块）制造枪声 | inactive 怪物**不会**被唤醒；恢复激活后只处理仍有效的新噪声 | | | |
| T-COMPAT-6 | The Hordes | 触发尸潮，观察尸潮感染者（命中白名单者） | 尸潮 AI 不受影响；命中白名单的感染者能循声；**记录 Hordes 生成路径是否触发 `EntityJoinLevelEvent`**（关键未知项） | | | |
| T-COMPAT-7 | misc_twf M1 僵尸动物 | 与 `misc_twf_zombie_animals` 同装，把僵尸动物加入白名单 | 僵尸动物可循声；M1 感染/免疫逻辑不受影响 | | | |
| T-SAFE-1 | 不加载区块 | 在未加载区块 emit 半径极大的噪声 | 服务器无卡顿、无区块被强制加载（观察区块加载计数/日志） | | | |
| T-SAFE-2 | 容量保护（单维度） | 临时把 `noise.max_active_per_level` 调小（如 32），持续 emit 大量噪声 | 达到上限后开始丢弃最旧最弱；日志出现**按类别限频**的 cap 告警；**不崩溃** | | | |
| T-SAFE-3 | 容量保护（单区块） | 把 `noise.max_active_per_chunk` 调小（如 4），在同一区块持续 emit | 同上；单区块告警与单维度告警互不压制 | | | |
| T-SAFE-4 | 非法调用（坐标） | 由测试插件/命令传入 NaN 坐标（若可行，或代码走查代替） | 被拒绝并输出一次 WARN，之后同类降为 DEBUG（不刷屏） | | | |
| T-SAFE-5 | 线程违规 | 走查/构造异步调用（若无法构造则记"未覆盖"） | 被拒绝并输出一次 WARN；服务端不受影响 | | | |

---

## 6. 原版噪声来源（T-VAN）

逐项制造行为，用 `/hearing debug` 或 `/hearing stats` 确认产生了对应类型（可临时把该类型 `radius` 调大以便观察怪物反应）。

| ID | 来源 | 步骤 | 预期类型 | 实测结果 | 结论 | 证据/备注 |
| --- | --- | --- | --- | --- | --- | --- |
| T-VAN-1 | 疾跑 | 疾跑 ≥ 2 秒 | 周期性产生 `misc_twf:sprint`（间隔默认 10 tick） | | | |
| T-VAN-2 | 潜行/静止 | 潜行移动、站立不动 | **不**产生 sprint 噪声 | | | |
| T-VAN-3 | 落地 | 从 3 格高度落下 | 产生 `misc_twf:land` | | | |
| T-VAN-4 | 轻微落地 | 从 0.5 格落下（配置 `land_min_distance` 默认 1.0） | 不产生噪声 | | | |
| T-VAN-5 | 门类 | 右键开关木门/活板门/栅栏门 | 各产生 `misc_twf:door` | | | |
| T-VAN-6 | 容器 | 右键打开箱子/桶/熔炉 | 产生 `misc_twf:container` | | | |
| T-VAN-7 | 方块破坏 | 正常挖泥土/石头 | 产生 `misc_twf:block_break` | | | |
| T-VAN-8 | 玻璃破碎 | 打碎玻璃/玻璃板/染色玻璃 | 产生 `misc_twf:glass_break`（走 SoundType.GLASS） | | | |
| T-VAN-9 | 爆炸 | 引爆 TNT/苦力怕 | 产生 `misc_twf:explosion` | | | |
| T-VAN-10 | 开关关掉 | 关闭 `[vanilla]` 中某项后重复对应行为 | 不再产生该类型噪声 | | | |

---

## 7. Superb Warfare 适配（T-SW）

| ID | 测试项 | 步骤 | 预期 | 实测结果 | 结论 | 证据/备注 |
| --- | --- | --- | --- | --- | --- | --- |
| T-SW-1 | 半自动开火 | 用普通手枪（不含消音）开火 | 产生 `misc_twf:gunshot`，半径 = 枪械 JSON `SoundRadius` + 枪管修正 | | | |
| T-SW-2 | 全自动开火 | 用步枪连射 3 秒 | pulse 数远小于子弹数（**合并生效**）；`merged` 增长 | | | |
| T-SW-3 | 消音武器 | 装消音器（Barrel 等级 2）开火 | 类型为 `misc_twf:suppressed_gunshot`，半径 = `max(4, soundRadius × 0.5)` | | | |
| T-SW-4 | 霰弹枪 | 用霰弹枪开火 | 产生一次 gunshot；邻居僵尸反应正常 | | | |
| T-SW-5 | 爆炸类武器 | 用手雷/RPG/C4 | 产生 `misc_twf:explosion`（走通用爆炸事件） | | | |
| T-SW-6 | 载具武器 | 操作载具武器开火 | 同样产生 gunshot（SBW 载具走同一 `ShootEvent`） | | | |
| T-SW-7 | 空枪/换弹 | 空弹匣扣扳机、换弹 | **不**产生 gunshot 噪声（只有真实开火才发声） | | | |
| T-SW-8 | 无客户端反推 | 关闭客户端声音（音量 0）后开火 | 噪声仍正常产生（证明不依赖音效播放） | | | |
| T-SW-9 | 频段合理性 | 记录几种武器的实际半径（debug 输出） | 手枪 < 步枪 < 爆炸；消音明显短于未消音（数值合理，可提出调参建议） | | | |

---

## 8. 性能验收（T-PERF）

统一场景建议：200+ 感染者（或用 `summon` 生成 200 只僵尸并加入白名单）+ 每秒 10 次枪声 + 玩家在中心活动，持续 60 秒。

| ID | 指标 | 采集方式 | 通过标准（自定，可调整） | 实测结果 | 结论 | 证据/备注 |
| --- | --- | --- | --- | --- | --- | --- |
| T-PERF-1 | MSPT 增量 | spark 对比"装/不装 hearing"两次 60 秒采样 | 增量 ≤ 1.0 ms | | | |
| T-PERF-2 | TPS | `/spark health` | 保持 20（允许瞬时抖动） | | | |
| T-PERF-3 | 查询频率 | `/hearing stats` 的 `queries` 增长 | ≤ 实体数 / `query_interval_ticks`（含 jitter 波动） | | | |
| T-PERF-4 | 平均候选数 | `/hearing stats`：`candidates / queries` | ≤ 8 | | | |
| T-PERF-5 | 路径失败占比 | `/hearing stats`：`path fail` 与查询数之比（若未单独统计，用 debug 统计） | ≤ 10% | | | |
| T-PERF-6 | 索引规模 | `/hearing stats` 的 `chunks` | 与活跃发声区域数量级一致，无随时间的单调膨胀 | | | |
| T-PERF-7 | 长跑稳定性 | 连续运行 10 分钟后再看 stats | `pulses` 稳定在合理范围，无内存/索引泄漏迹象 | | | |
| T-PERF-8 | 极限压测 | 临时调低 cap 并狂刷噪声 | 有告警、有丢弃、无崩溃、TPS 可恢复 | | | |

---

## 9. 配置项验证（T-CFG）

| ID | 测试项 | 步骤 | 预期 | 实测结果 | 结论 | 证据/备注 |
| --- | --- | --- | --- | --- | --- | --- |
| T-CFG-1 | 听觉倍率覆盖 | 配置 `goal.entity_hearing_multipliers` 中 `minecraft:zombie=0.5` | 同一噪声下，僵尸可听距离约为默认一半 | | | |
| T-CFG-2 | 倍率非法值 | 写成 `minecraft:zombie=abc` | 输出一次 WARN 并回退 1.0，不影响启动 | | | |
| T-CFG-3 | Goal 优先级 | 修改 `goal.priority`（如 2 与 9） | 循声相对其它 MOVE 行为的抢占关系随之变化，且不与其它 Goal 冲突 | | | |
| T-CFG-4 | 到达/失败/切换参数 | 分别调整 `arrival_distance`、`max_path_failures`、`switch_threshold`、`switch_cooldown_ticks` | 行为按配置变化（到达更早/更晚、失败次数变化、切换更难/更容易） | | | |
| T-CFG-5 | 合并参数 | 调整 `merge_window_ticks`/`merge_distance` | 合并发生范围随配置变化 | | | |
| T-CFG-6 | 热改配置 | 运行中修改 server 配置并重载（`/reload` 或模组重载机制） | 新参数生效（配置项持有的是 live value） | | | |
| T-CFG-7 | 无 debug 开关 | 检查 `misc_twf_hearing-server.toml` | **不存在** `[debug]` 段（日志由 log4j2 控制） | | | |

---

## 10. 生命周期与存档（T-LIFE）

| ID | 测试项 | 步骤 | 预期 | 实测结果 | 结论 | 证据/备注 |
| --- | --- | --- | --- | --- | --- | --- |
| T-LIFE-1 | 维度卸载释放 | 去下界 emit 噪声后返回主世界并卸载下界（或传送远处触发卸载） | 下界噪声随之释放，`/hearing stats` 不再列出该维度或计数归零 | | | |
| T-LIFE-2 | 服务器重启清空 | 重启后立即 `/hearing stats` | 无历史噪声残留（`emitted` 从 0 开始） | | | |
| T-LIFE-3 | 存档无写入 | 退出后检查存档目录 | 无新增与本模块相关的数据文件 | | | |
| T-LIFE-4 | 停服清理调试状态 | 重启后 `/hearing debug` 状态 | 调试开关回到关闭（会话级） | | | |

---

## 11. 日志与调试（T-LOG）

| ID | 测试项 | 步骤 | 预期 | 实测结果 | 结论 | 证据/备注 |
| --- | --- | --- | --- | --- | --- | --- |
| T-LOG-1 | 级别控制 | 默认级别（INFO）下触发循声 | 无 DEBUG 刷屏 | | | |
| T-LOG-2 | 打开 DEBUG | 在 `log4j2.xml` 中把 `misc_twf_hearing` 调为 DEBUG 后触发循声 | 出现诊断日志；性能无显著恶化（惰性求值） | | | |
| T-LOG-3 | 容量告警按类别独立 | 分别制造单维度与单区块溢出 | 两类告警各自出现，互不吞掉 | | | |
| T-LOG-4 | 编程错误一次告警 | 触发非法坐标/线程违规（或走查替代） | 首次 WARN，之后同类 DEBUG；不刷屏 | | | |
| T-LOG-5 | 调试命令输出对象 | `/hearing debug <player> on` 后触发循声 | 仅该玩家看到诊断，其他玩家无输出 | | | |

---

## 12. 问题记录

| 编号 | 关联用例 | 现象描述 | 复现步骤 | 严重度（阻塞/高/中/低） | 初步判断 | 证据 |
| --- | --- | --- | --- | --- | --- | --- |
| P-1 | | | | | | |
| P-2 | | | | | | |
| P-3 | | | | | | |

---

## 13. 结论汇总

| 项 | 结论 |
| --- | --- |
| 用例总数 / 通过 / 失败 / 无法判定 | |
| 阻塞性问题 | |
| 需要调参的项（半径/间隔/阈值等） | |
| 对业务方的说明（例如 Sona 移除后的玩法变化） | |
| 是否可进入 M4（M4 消费循声 API） | |

---

## 附：需要重点确认的已知未知项（请主人特别留意）

1. **Hordes 生成路径是否触发 `EntityJoinLevelEvent`**（T-COMPAT-6）：若不触发，Hordes 生成的感染者不会自动获得循声 Goal，需要为 Hordes 单独补一次注入钩子。
2. **`stop()` 是否需要 `navigation.stop()`**（决策 D5 暂定"不主动停"）：请在 ZT2 + CrawlMod 环境观察是否出现"旧路径残留导致怪物原地抖动/折返"，如有则改为条件停止。
3. **消音半径策略**（D7：`max(4, soundRadius × 0.5)`）：装/不装消音器对比手感，如过强/过弱请给出建议倍率。
4. **各类型 radius/strength 默认值**（§11.8 表格）：请以实际手感反馈（尤其 `sprint`/`door`/`container` 是否过吵或过静）。
5. **ServerCore inactive 不唤醒**：这是业务方明确接受的行为，若联调中觉得"怪物太迟钝"需回到需求层再讨论，不要在实现里偷偷唤醒。
6. **容量上限默认值**（4096/维度、128/区块）：压力测试后可给出更贴合整合包的数值建议。
