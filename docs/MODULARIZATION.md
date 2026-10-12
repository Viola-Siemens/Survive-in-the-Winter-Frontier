# Survive in the Winter Frontier 功能梳理与模块化拆分方案（最终版）

> 文档版本：v2.0（终版，四模块方案）
>
> 适用范围：NeoForge 1.21.1（Minecraft 1.21.x），Java 21。
>
> 项目背景：本模组是整合包 The Winter Frontier（冬境边域）的定制模组，向整合包补充内容并把大量既有模组的功能"打通"。当前正处于 1.18.2 → 1.21.1 移植过程中，尚未完成；部分所联动的第三方模组尚未更新到 1.21.1，因此在代码中只能按与对方约定的 API 先行开发（含 TODO 占位），待对方发布后才能产出可执行制品。
>
> 说明：本文基于对仓库源码的静态梳理（`src/main/java` 237 个文件、约 1.7 万行；`src/main/resources` 869 个文件）与主人的决策整理。标注"需运行时确认"的条目请在依赖就绪后的联编回归中逐一验证。

---

## 1. 目标

将单体模组拆分为 **4 个可单独安装的 NeoForge Mod（模块）**，要求：

- 模块内高内聚、模块间低耦合；
- 每个模块可单独打包安装；
- **验收底线（决策 D5）**：缺失某个自家兄弟模块时，其余模块**不得崩溃**（允许出现数据加载失败等降级表现，但不应崩溃或产生系统性故障）。

---

## 2. 本次定稿的决策记录

| 编号 | 决策 | 结论 |
| --- | --- | --- |
| D1 | 能源与防护设备是否单列 | **不单列**。能源本身是工业的一部分，能源装备与紫外线灯并入"工业生态"模块。 |
| D2 | 末世装饰是否独立成模块 | **不独立**。巢穴结构（NBT/二进制结构数据）依赖大量装饰方块，二者必须同模块；装饰并入"冒险与探索"模块。 |
| D3 | "原版微调"归属 | 按**机制属主**分发到各模块，不设独立"微调"模块，也不整包归入某一个模块。 |
| D4 | 免疫存档与 Hordes 联动归属 | 归属**僵尸动物模块（M1）**：免疫是"僵尸化"概念的属主数据；冒险模块中的疫苗通过 M1 公开 API 写入免疫记录（方向 M4 → M1，可选依赖）。 |
| D5 | "单独安装"验收语义 | 缺失自家兄弟模块**不崩溃**即可（见第 1 节）。 |
| D6 | 内容命名空间 | **统一保留 `misc_twf` 内容命名空间**，4 个模块的 jar 使用各自 modid（`misc_twf_*`）；注册 id、资源路径、lang key、结构数据中的方块 id 全部维持现状，不做 id 迁移（详见 4.2）。 |

---

## 3. 现状盘点与移植期注意事项

### 3.1 现状形态

- 单 Mod（modid = `misc_twf`）、单 Gradle 工程、单 `neoforge.mods.toml`、单 `misc_twf.mixins.json`。
- 包根 `com.hexagram2021.misc_twf`，顶层分 `common` / `client` / `mixin` / `server`。
- 资源统一挂在命名空间 `misc_twf`（`assets/misc_twf`、`data/misc_twf`），另有向外部命名空间写入的数据（`data/minecraft`、`data/forge`、`data/neoforge`、`data/hordes`、`data/curios`）。
- 对外 mandatory 前置：`createaddition`、`curios`、`expandability`、`hordes`、`jade`、`travelersbackpack`、`tetrachordlib`；构建与代码中还实际引用 create/Registrate/JEI/Ponder/GeckoLib、枪械模组（TaC / Superb Warfare，代码命名空间仍为 `tacz`）、KubeJS（compileOnly）、EasyDiet、zombie_extreme、Sona 等（详见第 6 节）。

### 3.2 移植期遗留/未就绪清单（整理现状，供迁移时处理）

1. `MISCTWFSkills`（Just Leveling Fork 技能）整段被注释，配置中残留 `STRONG_STOMACH_SKILL_LEVELS`、`GUN_MASTERY_SKILL_LEVELS` 参数——建议**不进入任何模块**，归档/删除，待对应模组更新后另议。
2. `MonsterEggBlockEntity` 直接 import `com.scarasol.sona.*`（Sona 尚未更新 1.21.1）——按"约定 API + TODO 占位"处理，保留在冒险模块内并标注（见 5.4）。
3. 构建注释中 `astages`、`biomancy`、`hunger_reworked`、`tacz`（被 superb-warfare 取代）等依赖未就绪——模块依赖表（6.2）需在依赖模组发布后定稿。
4. `data/misc_twf/tags/worldgen/biome/has_structure/boss_lair.json` 当前 `values` 为空数组：结构生成依赖整合包 datapack 追加群系，属"整合包协作契约"，拆分后保持不变（归冒险模块）。
5. 熔炉点火、炼药锅、发射器、配方书等"原版微调"逻辑与各自机制交织（见决策 D3 与 5.x 的归属速查）。
6. 巢穴结构（代码 pieces）的方块调色板软引用外部模组方块（如 `createdeco:dean_bricks`、`verdure:pebbles`，`DeferredBlock` 懒解析）——迁入冒险模块时保持"懒解析/存在即用"模式（需运行时确认缺失时的降级行为）。

---

## 4. 目标架构

### 4.1 模块划分总览

| 模块 | 建议 modid（沿用/新增） | 职责（内容域） | 主要外部依赖（待依赖就绪后定稿） |
| --- | --- | --- | --- |
| M1 | `misc_twf_zombie_animals` | 僵尸动物：感染动物实体 + 行为/渲染/音效 + **僵尸化/免疫存档与 Hordes 联动（D4）** | hordes（豁免 mixin）；可选：zombie_extreme（战利品数据） |
| M2 | `misc_twf_wildlife` | 农牧生态：动物尸体、粪便肥料、产奶冷却、冬小麦与食物 | 无强制；可选：kubejs / cold_sweat / delightful / EasyDiet |
| M3 | `misc_twf_industry` | 工业生态：能源装备与紫外线灯（D1）+ 回收炉 + 弹药模具工业 | create、createaddition、curios、Registrate、ponder、tetrachordlib、枪械模组（tacz）；可选：JEI |
| M4 | `misc_twf_adventure` | 冒险与探索：生化医疗玩法 + 旅行背包×枪械 + 深渊巢穴/怪物蛋世界生成 + 末世装饰（D2） | travelersbackpack、枪械模组（tacz）、GeckoLib、expandability（血液游泳微调）；可选：zombie_extreme、kubejs、Sona |
| H | `misc_twf_hearing` | **纯循声系统（M4 前置，替代 Sona）**：服务端噪声索引 + MOVE-only 循声 Goal，只用"哪里响了"驱动怪物前往调查（详见第 11 章） | 无强制；Superb Warfare（compileOnly，可选枪声适配） |

> 说明：modid 可在拆前全局替换为更贴切的命名（如 M2 可用 `misc_twf_husbandry` 等），但必须保留 `misc` 前缀并全小写下划线。

### 4.2 命名与工程布局

- **内容命名空间（决策 D6）**：所有注册 id 与资源继续使用 `misc_twf`，**不做 id/资源命名空间迁移**。理由：
  - 移植期 id 稳定性：现存配方、战利品、lang、客户端模型引用均以 `misc_twf:*` 书写，全量改名成本高且易错；
  - 巢穴结构等二进制 NBT 数据中写死的方块 id 无需改写；
  - 同族 4 模块通常在整合包内成套安装，跨模块仍以 `misc_twf:*` 互相引用与现状一致。
- **Java 包**：按模块拆分根包 `com.hexagram2021.misc_twf_zombie_animals`、`..._wildlife`、`..._industry`、`..._adventure`、`..._hearing`（每模块内部沿用 `common/client/mixin/server` 分层，循声模块内部为 `api/core/ai/event/compat/config/debug/command`），避免跨 jar 同名类。
- **聚合资源文件策略**：`assets/misc_twf/lang/*.json`、`assets/misc_twf/sounds.json` 等"命名空间级单文件"无法跨 jar 拆分——放在**共享单一资源源目录**，由每个模块的构建同时打进自己的 jar（各 jar 内容完全一致，任一 jar 生效结果相同）；模型/贴图/方块状态/ogg 等按路径拆分的资源**只允许存在于唯一一个模块**，严禁同路径重复。
- **Gradle**（多工程）：
  ```
  settings.gradle                     # include 4 个模块；共享版本目录
  shared-resources/                   # lang、sounds.json 等聚合文件单一来源
  modules/
    misc_twf_zombie_animals/          # 独立 neoForge userdev 工程
    misc_twf_wildlife/
    misc_twf_industry/
    misc_twf_adventure/
    misc_twf_hearing/                 # 循声模块（M4 前置；无内容注册，仅 API/Goal/命令）
  ```
  建议 `buildSrc`（或 included convention）统一模块构建；每模块自带 `neoforge.mods.toml`、`*.mixins.json`、独立配置与独立存档键名、独立创造页。
- 原 `misc_twf` 单体入口随迁移完成退役；发布由 4 个内容模块 + 1 个循声模块（共 5 个 jar）取代（整合包按需安装，循声模块为 M4 的前置）。

### 4.3 模块间关系与依赖矩阵

```
M1 僵尸动物 ◄──(可选)── M4 冒险（疫苗写入免疫 API）
M3 工业生态： 能量/灯（Create:Additions）、回收炉/弹药工业（Create + 枪械）
M4 冒险： 医疗 / 背包×枪械 / 巢穴与怪物蛋 / 装饰（内部强关联，D2）
M4 冒险 ──(可选 API)──► H 循声（怪物卵由"曝光"改为"听过声音"，见 11.11/11.13）
H 循声： 独立闭环，不依赖任何兄弟模块（枪声适配走 compat 层，可选）

兄弟模块依赖方向：M4 → M1（可选，提供疫苗写入免疫的 API）与 M4 → H（可选，消费循声查询/事件 API）；
其余兄弟间无代码依赖；跨模块内容引用只存在于数据文件（见第 6 节处置表）。
```

| 模块 | 对兄弟模块 | 对外部模组（草案） |
| --- | --- | --- |
| M1 | 无（不依赖 M2/M3/M4/H） | hordes（强制，因豁免 mixin 目标为其类）；zombie_extreme（可选，数据） |
| M2 | 无 | 无强制（第三方 id 引用一律判空降级） |
| M3 | 无 | create、createaddition、curios、Registrate、ponder、tetrachordlib、枪械模组（tacz，见 6.2-E2）；JEI 可选 |
| M4 | M1（optional，仅医疗免疫写入）、H（optional，循声 API） | travelersbackpack、枪械模组（tacz）、GeckoLib、expandability（强制）；zombie_extreme / kubejs（可选或数据引用，见 6.2；Sona 由 H 替代） |
| H | 无 | 无强制；superbwarfare（compileOnly，可选枪声适配） |

---

## 5. 各模块拆分明细

> 每模块给出职责、内容清单（Java / 资源 / mixin / 配置 / 存档 / 网络）、对外接口、解耦改造点与验收要点。

### 5.1 M1 `misc_twf_zombie_animals`（僵尸动物）

- **职责**：末日动物敌人本体 + "僵尸化/免疫"机制的属主（D4）。
- **内容清单**：
  - 实体：`zombie_chicken/cow/goat/pig/polar_bear/rabbit/sheep/wolf`（原 `MISCTWFEntities`、`common/entity/*`、AI goal）；
  - 行为：僵尸山羊冲撞击退、兔子跳跃、绵羊转化保留毛色、金苹果治疗（原 `ForgeEventHandler` 中随域方法）；
  - 效果：~~`FragileEffect`~~（**已修订归属，见 §6.1/§10**：脆弱效果实际仅由工业模块紫外线灯施加，按“留给触发方”归 M3，不随 M1 迁出）；
  - 渲染/音效：8 套模型与渲染器、全套实体音效与 ogg；
  - **僵尸化免疫（D4）**：`MISCTWFImmunitySavedData`（原 `MISCTWFSavedData` 免疫部分，含读写 API：`isImmune(uuid)` / `markImmune(...)`）；
  - Hordes 联动：`hordes/InfectionEventHandlerMixin`（感染豁免/转化豁免）、`data/hordes/tags/entity_types/infection_entities.json`；
  - 数据：`data/misc_twf/loot_tables/entities/zombie_*.json`。
- **对外提供**：免疫查询/写入 API（供 M4 疫苗调用）。
- **解耦改造点**：
  - 免疫存档从单体 SavedData 拆出，存档文件名与 NBT key 带模块标识（如 `MiscTWF-ZombieAnimals`）；与灯坐标存档（M3）彻底分离；
  - 不依赖 M2/M3/M4；不做任何"躲避灯"逻辑（驱怪/避灯由 M3 注入原版怪物，见 5.3）；
  - 原 `MISCTWFSavedData` 中与 M1 无关的方法全部移交对应模块。
- **验收要点**：单装时实体/模型/音效正常；僵尸动物战利品表对 zombie_extreme 缺失时不崩溃（数据级降级可接受）；疫苗模块缺席时免疫查询默认返回 false。

### 5.2 M2 `misc_twf_wildlife`（农牧生态）

- **职责**：动物生存农牧循环与冬小麦食物线。
- **内容清单**：
  - 动物尸体：9 种 `DeadAnimals` 方块族 + BE + `DeadAnimalData` + 原版动物死亡掉落尸体注入（`LivingEntityMixin` 中尸体替换段）；
  - 粪便肥料：`PoopingAnimal` attachment、`pooping_animals` 标签（仅含原版动物，已核实）、`animal_poop` 物品与发射器行为；
  - 产奶：`IProduceMilk`、`CowEntityMixin`、`GoatEntityMixin`、`MILK_INTERVAL`；
  - 农牧：冬小麦作物/种子、动物食性修改（`AnimalEntityMixin`）、面包/蛋糕/曲奇等食物数据、yarn 织物原料（与医疗的纱布归属联动见 6.1）；
  - 饮食微调：`LivingEntityMixin` 中 EasyDiet 盐分组联动（D3：随本域）；
  - 配置：原 `MILK_INTERVAL / ANIMAL_POOPING_INTERVAL(_NOISE)` 等。
- **解耦改造点**：
  - 代码内对 kubejs/cold_sweat/delightful 注册名硬查（无判空）改为"可选模组判定 + 判空 + 原版替代"；
  - `LivingEntityMixin` 是全实体注入，保持对非本域实体零副作用；与其它模块可能共存于同一目标类时用 `misc_twf_*$` 前缀 + priority 管理。
- **验收要点**：杀动物得尸体、采尸得肉；不装 kubejs 等联动模组不报错；粪便/产奶节奏受配置控制。

### 5.3 M3 `misc_twf_industry`（工业生态）

- **职责**：Create 系工业与能量的整合模块（D1 采纳，能源并入工业）。
- **内容清单**（三个子簇，均自含）：
  1. **能源装备与紫外线灯**：`IEnergyItem` 与能量能力注册、蓄电池（普通/军用）、夜视仪（Curios head 槽 + 客户端模型/渲染 + `LightTextureMixin` 视觉）、wayfarer MK-I 护甲套（单件/套装效果 + `wayfarer_armors` 标签）、`ultraviolet_lamp`（方块/BE/GUI/能量/充电）；灯坐标 `MISCTWFLampSavedData`（原 SavedData 的 lamps 部分，KD 树 + tetrachordlib）、`MobSpawnEvent` 刷怪拦截、怪物避灯 AI（`AvoidBlockGoal`/`IAvoidBlockMonster` 注入原版 Monster，本模块内部闭环）；能量类材料与零件、`battery` 标签、能量 tooltip（`ForgeClientEventHandler` 中随域部分）；`curios` head 标签数据；夜视/灯的客户端视觉 mixin（`DarknessMixin`/embeddiumplus 归属需运行时确认）。
  2. **回收炉**：L 形三方块机器 + BE + Menu/Screen + `RecoveryFurnaceRecipe`（多输出/经验/回收时间/公共标签映射）+ 配方类型/序列化器/缓存 + 配方书类型与分类（`MISCTWFRecipeBookTypes`）+ JEI 类别；**点火微调**（`FireChargeItemMixin`、`FlintAndSteelItemMixin`、`BlockToolModificationEvent` 点燃熔炉、`AbstractFurnaceBlockEntityMixin`、配方书相关客户端 mixin）按机制属主随回收炉（D3）。
  3. **弹药模具工业**：`mold_workbench`/`mold_detacher`（Registrate 注册 + Ponder 场景）、clay mold 与 14 口径 ×（未完成/完成）模具、模具工作台/分离台自定义配方与缓存、Create 数据配方（mixing/pressing/sandpaper_polishing）、JEI 类别。
- **对外提供/消费**：无兄弟依赖；对外（枪械模组）见 6.2-E2。
- **解耦改造点**：
  - 灯存档独立为 `MISCTWFLampSavedData`，与 M1 免疫存档分离；
  - 能量 tooltip 中涉及"旅行背包 TAC 槽"的部分移交 M4；
  - 配置项按子簇归属本模块统一成模块配置；
  - 因回收炉配方书分类在**代码静态初始化**中 import `com.tacz.guns.*`（已核实），本模块只要包含回收炉就必须把枪械模组声明为编译与运行依赖；若希望枪械可选，需重构该分类为懒加载/可选注册（推荐立项，非阻塞）。
- **验收要点**：能量装备与灯在无 Create 机械场景下可用；给旋转力 + 漏斗 + 机械臂的全流程在 Create 与枪械模组存在时可跑通；枪械模组缺失时模具/回收炉数据降级不崩溃。

### 5.4 M4 `misc_twf_adventure`（冒险与探索）

- **职责**：整合包的冒险层——医疗/免疫手段、枪械携行联动、深渊巢穴与怪物蛋、末世场景装饰、以及按机制属主归此的微调（D2/D3）。
- **内容清单**：
  1. **生化医疗**：变异药水锅玩法链（`mutant_potion_cauldron`/BE/flag 数据组件/桶类）、疫苗锅链（`abyss_virus_vaccine_cauldron`）、`mutant_potion`/疫苗/桶、注射器/玻璃棒/纱布/神秘血肉/二阶脑核等医疗材料、酿造配方注册、炼药锅与发射器交互（`ModVanillaCompat` 中随医疗部分）；疫苗右键实体逻辑：**通过 M1 提供的免疫 API 写入免疫**（M4 → M1 optional）；`server` 中免疫相关逻辑移交 M1（D4）；
  2. **旅行背包 × 枪械**：TAC 弹药槽升级（锻造配方数据、`TravelersBackpackTacData`、tooltip）、方块/物品两种弹药槽菜单与槽位容器、客户端标签页按钮/界面、`travelersbackpack/*` 与 `tacz/AbstractGunItemMixin`、开背包网络包、背包战利品修改器、`IAmmoBackpack`（随域迁入）；
  3. **深渊巢穴与怪物蛋**：BossLair 结构全套（type/configured/set/pieces/biome 标签 + 结构数据/代码调色板）、怪物蛋方块/BE/数据组件/特性与放置、孵化判定与客户端动画包（GeckoLib）、巢穴战利品表（含对 M3 材料与大量外部模组物品的引用，见 6.2-E1/E3/E4）；
  4. **末世装饰**：人体尸体族、血腥内脏（含血液流体与 expandability 游泳微调）、医疗家具、垃圾废品、军警/工地道具、感染地块等（与结构同模块，D2）；
  5. **随域微调（D3）**：炼药锅/发射器/酿造、怪物蛋音效的 `sound_barrier` 方块标签、其它明确属冒险机制的微调。
- **对外提供/消费**：消费 M1 免疫 API（optional）；其余兄弟模块零依赖。
- **解耦改造点**：
  - 疫苗写入免疫的调用点以 M1 API + 可选模组判定实现（M1 缺失时跳过，不崩溃）；
  - 结构代码对 `createdeco`/`verdure` 等外部方块的懒解析保持"存在即用"；
  - 战利品/怪物蛋数据对 M3 材料、tacz、zombie_extreme、kubejs 的引用按 6.2 处置；
  - Sona 引用按"约定 API + TODO 占位"处理（等待其 1.21.1 版本）。
- **验收要点**：疫苗链可独立游玩（无 M1 时免疫注册降级）；背包升级/弹药存取/破坏保留数据；巢穴与怪物蛋按数据配置生成；装饰道具全部正常。

---

## 6. 跨模块/外部引用处置表（已核实证据）

### 6.1 内容归属速查（材料与易摇摆项）

| 内容 | 归属 | 备注 |
| --- | --- | --- |
| 能量类材料（energy core、white_crystal_core、sculk_shard、void_crystal、wayfarer_ingot、glowing_netherite_ingot、aluminum/rubber plate、mechanical_enclosure、uv_led、灯零件） | M3 | white_crystal_core 出现在 M4 巢穴宝箱，见 E1 |
| clay mold、口径模具族 | M3 | 产出 tacz 弹药，见 E2 |
| syringe / glass_rod / gauze / mysterious_flesh / second_brain_core | M4 | gauze 若与 M2 yarn 有合成引用，属跨模块数据引用（按 6.2 通用策略） |
| yarn | M2 | 若最终归属有变，只改归属表与相关配方所在模块 |
| 血液流体/血块/血污/内脏等 | M4 | 装饰语境（expandability 游泳微调随域） |
| 冬小麦与面包/蛋糕/曲奇数据 | M2 | — |
| `ZOMBIE_ANIMALS_CAN_BE_HEALED` / 蛋的踩踏/坠落概率 | M1 / M4 | 各随机制属主 |
| FragileEffect（脆弱效果） | **M3（紫外线灯施加）** | 修订（见 §10）：证据为 `UltravioletLampBlockEntity` 施加脆弱 II/IV；§5.1 原“M1 待确认”作废 |
| `TACZ_WHITELIST`（枪声吸引怪物白名单） | 待运行时确认 | 归其消费机制所在模块（疑似 M1 或 M4） |

### 6.2 已核实的引用与处置

| 编号 | 引用（源 → 目标） | 影响 | 处置 |
| --- | --- | --- | --- |
| E1 | M4 巢穴宝箱 `abyss_lair_rare` → `misc_twf:white_crystal_core`（M3 材料） | 缺 M3 时该战利品表解析失败（日志，不崩溃） | 内容命名空间统一（D6），id 不变；可选优化：由 M3 的全局战利品修改器在 M3 存在时注入该条目，实现"缺 M3 即自动消失" |
| E2 | M3 模具分离台配方 → 产出 `tacz:ammo`（NBT `AmmoId`）；且 M3 回收炉配方书分类**代码 import `com.tacz.guns.*`（已核实）** | 枪械模组缺失：配方个体解析失败（日志）；代码静态引用则直接崩溃 | 代码层将 tacz 声明为 M3 的编译+运行依赖（推荐）；数据层配方可按需加 `mod_loaded` 条件（可用性需验证） |
| E3 | M4 怪物蛋 feature → `zombie_extreme:night_hunter` 等实体；M1 僵尸动物战利品 → `zombie_extreme:rotten_apple`；M4 巢穴宝箱 → `zombie_extreme:*` 武器 | zombie_extreme 缺失时相关数据解析失败 | zombie_extreme 属"整合包既有联动模组"：M1 仅数据引用（缺失=日志）；M4 世界生成引用需运行时验证"feature 解析失败是否引发 chunk 异常"——若会，则改程序化注册+跳过，或声明为 M4 的依赖/强联动 |
| E4 | M4 巢穴宝箱 → `kubejs:*`（罐头/口香糖等）、`tac:*`、`create:*` | kubejs 脚本未提供物品时该表解析失败（日志） | 引用按整合包既有模组声明；可选：将整合包专属条目拆为独立 pool 并加条件 |
| E5 | M2 动物尸体掉落代码 → `kubejs`/`cold_sweat`/`delightful` 注册名硬查（无判空） | 缺联动模组时可能取到空物品 | 改造为"可选模组判定 + 判空 + 原版替代" |
| E6 | M4 巢穴结构调色板 → `createdeco:dean_bricks`、`verdure:pebbles`（DeferredBlock 懒解析） | 外部方块缺失时按懒解析降级（需确认语义） | 保持懒解析模式，对外部模组不强依赖 |
| E7 | M4 疫苗写入免疫 → M1 API | M1 缺失时疫苗免疫无法记录 | M1 提供 API；M4 以 optional 依赖 + 运行时判定调用（D4/D5） |
| E8 | 跨模块配方引用（如 M2 纱线 → M4 纱布的合成链） | 缺对方模块时个体配方解析失败（日志） | 接受降级（D5）；可选加数据条件，尽力避免脏日志 |

---

## 7. 解耦改造通用要点（迁移时逐项落实）

1. **注册器本地化**：各模块自建 DeferredRegister 与注册类；原 `BlockEntry`/`ItemEntry`/`FluidEntry` 便利封装按模块复制（无公共库模块），删除跨模块静态字段引用。
2. **存档拆分**：`MISCTWFSavedData` → `MISCTWFImmunitySavedData`（M1）+ `MISCTWFLampSavedData`（M3）+ 其余各自归属；存档名与 NBT key 带模块标识；旧档一次性迁移或在更新说明声明不兼容（整合包测试期推荐后者）。
3. **mixin 独立化**：每模块独立 `*.mixins.json`；凡目标类来自第三方模组的 mixin（tacz/hordes/travelersbackpack/embeddiumplus），所在模块必须将对应第三方模组声明为依赖（否则目标类缺失会在 mixin 阶段直接崩溃）——这是"单独安装"最大的硬约束；vanilla 目标类混入共存用 `misc_twf_*$` 前缀 + priority 管理。
4. **配置拆分**：原 `MISCTWFCommonConfig` 按模块拆成独立 ModConfigSpec，键名随模块（旧配置迁移说明）。
5. **网络/创造页**：每模块独立网络包 id 与创造页；原 2 个网络包（开 TAC 背包包、怪物蛋动画包）均属冒险模块 M4，随域迁移。
6. **数据文件条件化**：跨模块/跨模组引用的配方、标签、战利品、worldgen 尽量用数据条件（`mod_loaded`）包裹，可用性需在目标版本逐一验证；不可用处按 6.2 处置（程序化注册 + 运行时跳过 / 依赖声明 / 接受降级日志）。

---

## 8. 建议实施路线（对齐移植节奏）

> 移植未完成、部分依赖未就绪：拆分的**工程结构先行**，代码迁移按模块增量推进；每个模块先做到"能独立编译自身 + 对外约定 API 面"，可执行制品待依赖模组发布后联编验证。

- **阶段 0（地基）**：Gradle 多工程骨架（4 模块 + 共享聚合资源源 + convention）；每模块 `mods.toml`/mixin 配置/独立创造页/配置骨架；注册与存档拆分的公共改造点落位。
- **阶段 1（低风险域）**：M2（农牧生态）与 M4 中的装饰部分先行搬迁，验证资源分卷、聚合文件（lang/sounds）冗余策略与回归清单。
- **阶段 2（实体与工业）**：M1（僵尸动物 + 免疫存档/API + Hordes 豁免）与 M3（能源/灯、回收炉、模具工业）搬迁；完成 SavedData 拆分与 M4→M1 免疫 API 打通。
- **阶段 2.5（循声前置，先于 M4 主体）**：新建 `misc_twf_hearing` 并按 H0→H1→H2→H3→H4 **全量完成并验收**（API/索引/Goal/注入/Tag 与命令、SW 枪声适配、性能与兼容矩阵，见 11.14）；随后 M4 的怪物卵逻辑直接改接已验收的循声 API（删除 `com.scarasol.sona.*` 与 EXPOSURE 依赖），避免二次改造。
- **阶段 3（冒险层收敛）**：M4 医疗/背包×枪械/巢穴与怪物蛋迁入并自洽；处理 6.2 各引用项（条件/懒加载/占位）。
- **阶段 4（依赖就绪联编）**：随第三方模组 1.21.1 版本更新逐项替换 TODO 占位；产出 5 个可执行 jar 并跑完整回归与冒烟矩阵（含循声的 §11.14 H4 矩阵）。
- **阶段 5（收尾）**：退役原 `misc_twf` 单体；整理整合包安装清单（4 个内容模块 + 循声模块 + 外部依赖树）；更新 README/CHANGELOG/存档迁移说明与"移除 Sona"的玩家告知。

---

## 9. 验收清单

1. **缺模块不崩溃（D5）**：每个模块在"仅本模块"与"本模块 ± 缺任一兄弟模块"组合下启动进游戏不崩溃、无系统级故障。
2. 数据降级表现记录在案：哪些表/配方在缺兄弟模块时会刷日志（期望清单），逐项确认后决定接受或加条件。
3. 跨模块免疫链路（M4 疫苗 → M1 免疫存档 → Hordes 豁免）在"M1+M4"与"仅 M4"下分别验证。
4. 聚合资源（lang/sounds.json）冲突测试：多 jar 并存时语言与音效完整。
5. mixin 共存冒烟：dev 环境合模（LivingEntity/Animal/Monster 等多模块注入目标类）。
6. 存档兼容：旧档加载测试 + 迁移/不兼容声明落地。
7. **循声模块（H）专项验收**（详见 11.14）：只 addGoal 不改 targetSelector；原版僵尸"闻声前往坐标"；ZT2/Improved Mobs/CrawlMod/ServerCore/The Hordes/SBW 六项兼容矩阵逐条通过；性能基线与容量保护指标达标；`/hearing stats` 指标可解释。

## 附录：待运行时确认清单

1. 僵尸动物的实际生成路径（Hordes 转化 / 怪物蛋 / 其它）与免疫覆盖范围；`TACZ_WHITELIST` 的消费机制归属。
2. 回收炉可回收输入集合与"点火微调面向原版熔炉"的范围。
3. 紫外线灯充能来源与 GUI 语义；`DarknessMixin`（embeddiumplus）归属。
4. 怪物蛋"曝光"机制的替代方案（**已定**：Sona 不迁移，改用循声模块 H 的 `HearingApi.query/findStrongest` 或 `NoiseEmitEvent.Post`，见 11.2/11.11/11.13）；怪物蛋 feature 对 zombie_extreme 缺失时的行为。
5. 各装饰方块是否有隐藏交互（逐个右键回归）。
6. 弹药槽对弹药品类的过滤口径与枪械模组版本绑定。
7. `DeferredBlock`（createdeco/verdure 等）懒解析在方块缺失时的实际降级行为。
8. 数据条件（`mod_loaded`）在配方/战利品/worldgen 中各自的支持情况（决定 6.2 各处置的最终形态）。

---

## 10. 实施状态（随拆分滚动维护）

> 本节随各模块实际拆分进度更新，作为“计划方案”与“落地现状”的对账记录。

| 模块 | 状态 | 说明 |
| --- | --- | --- |
| M1 `misc_twf_zombie_animals` | **已完成迁入与移植（v1）** | 实体/行为/渲染/音效、免疫存档与 API、Hordes 豁免 mixin 与数据、模块配置均已迁入；模块在 NeoForge 1.21.1 下**可独立编译打包**（`misc_twf_zombie_animals-4.0.0.jar`）。运行期联编冒烟待依赖就绪。 |
| M2 `misc_twf_wildlife` | **已完成迁入与移植（v1）** | 动物尸体方块族/方块实体/数据组件、粪便 attachment 与排泄计时、产奶冷却、冬小麦与食物数据、Jade 实体信息 provider、AppleSeed 盐分组联动（替代原 EasyDiet）均已迁入；模块在 NeoForge 1.21.1 下**可独立编译打包**（`misc_twf_wildlife-4.0.0.jar`，含 77 个资源与模块创造页）。运行期联编冒烟待依赖就绪。 |
| M3 `misc_twf_industry` | **已完成迁入与移植（v1）** | 能源装备与紫外线灯（含灯坐标存档 `MISCTWFLampSavedData`、chloride 真黑暗抑制）、回收炉（配方书分类 SW 化）、弹药模具工业（模具 14 → 5 并产出 `superbwarfare:<cat>_ammo`）均已迁入；模块在 NeoForge 1.21.1 下**可独立编译打包**（`misc_twf_industry-4.0.0.jar`，316 条目），根工程 M3 残留 0 命中；`MISCTWFRecipeBookTypes` 去 TaC 化、两个死代码 accessor 已删。 |
| M4 `misc_twf_adventure` | 未开始 | — |
| H `misc_twf_hearing` | **H0/H1 已落地，进行中** | 纯循声系统（替代 Sona，M4 前置）：设计与实施计划见第 11 章；D1/D2/D4/D5/D6 已确认，D3/D7/D8/D9 已按拍板定稿（lifetime 区间随机、每类型 radius/strength 配置化、§30-B）。现状：模块工程与配置骨架完成，H1 核心（噪声数据模型/区块桶索引/评分/管理器/公开 API/事件/生命周期清理）已实现并**可独立编译打包**（`misc_twf_hearing-4.0.0.jar`，37 条目）；下一步 H1 收尾（原版来源监听 + 实机链路验证）→ H2 Goal 与注入。 |

- **FragileEffect 归属修订（运行时证据，§5.1/§6.1 已同步）**：脆弱效果在单体中只被紫外线灯（工业域）施加（`UltravioletLampBlockEntity` 施加脆弱 II/IV），按“留给触发方”原则归 M3，不随 M1 迁出；其伤害加成消费方（原 `ForgeEventHandler.onLivingHurt`）同样留在根工程（M3 域）。
- **M1 免疫机制落地位置（决策 D4）**：`server/MISCTWFImmunitySavedData`（模块内），存档名 `MiscTWF-ZombieAnimals`，提供 `isImmuneToZombification/setImmuneToZombification` 公开 API；NBT 反序列化顺带修正原单体“误读外层 nbt”的疑似笔误。
- **过渡期根工程引用 M1**：根工程（仍含 M4 疫苗等代码）以 `implementation project(':misc_twf_zombie_animals')` 消费 M1 免疫 API（对应未来 M4 → M1，E7），M4 拆分完成后移除。
- **M1 移植适配要点（1.18 → NeoForge 1.21.1）**：`Entity.level()`；synced data 经 `SynchedEntityData.Builder`；`Mob#finalizeSpawn` 4 参；`MeleeAttackGoal.checkAndPerformAttack(LivingEntity)`（北极熊攻击目标重写）；`GameEvent.ENTITY_INTERACT`；转化/破坏判定改 `net.neoforged.neoforge.event.EventHooks`（`canLivingConvert/onLivingConvert/canEntityGrief`）；hordes 豁免 mixin 按 hordes 1.21.1 源码注入 `onDamage(LivingDamageEvent.Post)` 与 `onInfectDeath`。
- **根工程编译现状（2024 基准）**：整体编译失败均为**既有移植遗留**，与 M1 拆分无关（按 M1 标识检索 0 命中）。遗留类别：`com.tacz.guns.*` 缺失（枪械模组 1.21.1 包结构与代码不一致，E2）、`net.minecraftforge.*`/`ForgeRegistries` 1.18 残留、gamestages/sona/diet(EasyDiet)/embeddiumplus 等第三方 API 未就绪。上述将在对应模块轮次（M2–M4）与依赖就绪后逐项消化。
- **M1 网络/创造页**：M1 无网络包与创造页需求（僵尸动物无自产物品/刷怪蛋；原 2 个网络包均属 M4），故无新增。
- **mixin 配置**：模块 `misc_twf_zombie_animals.mixins.json` 目前 `required=false`，联编回归通过后再翻 `true`。

### 10.1 M3 前置调研：TaC 1.18.2 → Superb Warfare 1.21.1 对照结论（已完成）

> 详细报告见 `docs/SuperbWarfare-API-调研报告.md`（含 25 行对照总表与源码附录）。

- **命名空间**：modid = `superbwarfare`，未发现 `tacz` 兼容命名空间 → 现有数据文件中的 `tacz:*` 引用（14 个 mold_detacher 配方、弹药箱/枪械类战利品表）**必须整体改写**。
- **弹药最大断裂点**：SW 无 `AmmoItemBuilder`、无 `AmmoId`、无口径概念；只有 5 类 `Ammo` 枚举
  （`HANDGUN/RIFLE/SHOTGUN/SNIPER/HEAVY`，`data/gun/Ammo.java`）对应 5 个固定物品
  `superbwarfare:{handgun,rifle,shotgun,sniper,heavy}_ammo`；数量存 data component `superbwarfare:ammo_<name>`（int），
  玩家弹药总量存 data attachment `PlayerVariable.ammo`。
- **枪械弹药声明（口径的真实替代品）**：枪械数据 JSON 的 `AmmoType` 数组元素为 `AmmoConsumer`，`"Ammo"` 按语法解析：
  `@RifleAmmo`（消耗玩家弹药池）/ `infinity` / `fe` / `superbwarfare:some_item{SNBT}`（**直接消耗指定物品**）/ `1 @RifleAmmo`（loadAmount）。
  即：若坚持 14 口径弹药物品，需要以数据包方式覆写 SW 枪械数据把它们指向我方物品（耦合与版本风险高）。
- **枪械身份**：无 `GunId` NBT；`GunData.from(stack).id` 由物品注册名推导（`superbwarfare:ak_47` 形式）→ 白名单、战利品表、存档字段全部改用物品 id 字符串。
- **开火/换弹/命中钩子**：`api.event` 仅 6 个事件类，替代 TaC 的 mixin 面：
  `ShootEvent.Pre/Post`（不可取消）、`ReloadEvent.Pre/Post`、`ProjectileHitEvent.HitEntity/HitBlock`（可取消）、`PreKillEvent`。
- **音效与投射物**：`SoundTool.playDistantSound(...)` 近似 `SoundManager.sendSoundToNearby`；子弹实体为
  `entity/projectile/ProjectileEntity`（Builder 风格 API，含 `setGunItemId`）。
- **客户端**：无 `GunAnimationStateContext`/`GunHudOverlay`；动画走 GeckoLib（`GunGeoItem.animationPredicate` + `ClientEventHandler` 全局状态），
  HUD 拆为 22 个 `LayeredDraw.Layer`（`AmmoCountOverlay` ID 为 `superbwarfare:ammo_count`），注入用 `RegisterGuiLayersEvent`。
- **合成**：无枪械工作台/`*TableResult`；枪械用原版锻造台 `minecraft:smithing_transform`（蓝图+材料包+附加材料），
  根工程两个 TaC accessor mixin（`GunSmithTableResultAccess`/`RawGunTableResultAccess`）**无任何引用，判定为死代码**。
- **配件**：SW 配件是 `AttachmentType`（Scope/Magazine/Barrel/Stock/Grip）数据 + `Perk` 体系，未找到独立"配件物品"注册；
  回收炉配方书的"枪械/弹药/配件/杂项"分类需重新设计。
- **待主人决策**：① 口径路线（收敛 5 类 / 自建 14 口径并覆写 SW 枪械数据 / 14 模具映射 5 类产物）；
  ② 背包弹药槽定位（mixin `AmmoConsumer`/`InventoryTool` 供弹 / 改用 SW 原生弹药池+弹药盒 / 暂缓）；
  ③ 枪械精通与枪声吸引的实现路径（事件 or mixin or 暂缓）；④ SW 枪械数据 JSON 原文（需从 `superbwarfare-<ver>.jar` 提取）。

**决策（主人已确认，2025 轮次）**：
① 口径路线：**模具直接收敛为 5 种**（handgun/rifle/shotgun/sniper/heavy），14 口径注册项、配方与 lang 键移除；
② 背包弹药槽：**改用 SW 原生弹药体系**（`AmmoSupplierItem` + `AmmoBoxItem` + `PlayerVariable.ammo` 弹药池），TaC 供弹 mixin 作废；
③ 枪械精通与枪声吸引：**改官方事件**（`ShootEvent.Post` / 伤害事件或 `ProjectileHitEvent`），删除 `mixin/tacz/*` 全部 7 个；
④ SW 真实数据已由主人提供（`Sources-1.21.1/data/superbwarfare`，含 `sbw/guns/*.json` 与 `recipe/*.json`）。
TaC 侧补充调研：`docs/TACZ_1.18.2_API_REPORT.md`（含 P0 硬依赖清单与两处"对称性陷阱"）。
待定项：枪械精通/枪声吸引实现落在 M3 还是 M4；5 种模具的 id 命名与每次分离产出数量；`m4a1_carbine*`/`body_*` 等 M4 装饰道具的 SW 枪械映射。

---

## 11. 循声模块 `misc_twf_hearing` 设计与实施计划（M4 前置，替代 Sona）

> 需求来源：业务方《冬境边域——纯循声系统技术需求（开发审阅版 v2.0）》（2347 行，目标平台 NeoForge 1.21.1）。
> 全文工程摘要见 `docs/HEARING_REQUIREMENT_DIGEST.md`；旧实现对照为 Sona 1.18.2（`C:\Projects\Sources-1.18.2\com\scarasol\sona`）。
> 本章定位：把业务需求翻译为**可实施、可验收、边界封闭**的工程计划，并给出需主人拍板的决策项。

### 11.1 定位与职责边界

核心原则（业务方原文）：**我只告诉怪物哪里响了，不告诉怪物谁在那里。**

模块只实现四步链路：

```text
有意义的游戏行为发生（服务端确认）
        ↓
产生 NoisePulse（服务端权威）
        ↓
怪物查询附近噪声并评分，选出一个"值得调查"的坐标
        ↓
用怪物当前 Navigation 前往该坐标；到达/过期/失败/被更高优先级行为抢占即结束
```

明确**不做**（P2，永久排除在 hearing core 之外）：Target 接管、视觉感知、房间搜索、群体警觉、仇恨传播、攻击策略、破门破墙、Navigation/PathFinder 替换、SoundDecoy 实体、EXPOSURE/CAMOUFLAGE 体系、全维度 Mob 扫描、强制唤醒 ServerCore inactive Mob。

### 11.2 与 Sona 1.18.2 的关系（旧实现证据）

Sona 的"循声"实际做法与本需求**方向相反**，因此不是"迁移"而是"以纯循声重写"：

| Sona 组成 | 实际做法（源码证据） | 本模块处置 |
| --- | --- | --- |
| `manager/SoundManager.insertAi` | `mob.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(mob, Mob.class, 5, false, false, le -> le instanceof SoundDecoy))` | **完全排除**：禁止任何 `targetSelector` 操作 |
| `entity/SoundDecoy`（+`SoundDecoyPacket`） | 生成一个"声音诱饵"实体让怪物去攻击它 | **完全排除**：不生成实体，只存坐标 |
| `effect/Exposure` + `SonaMobEffects.CAMOUFLAGE` | 玩家隐蔽/曝光的 MobEffect 体系 | **完全排除**：不恢复任何 EXPOSURE/CAMOUFLAGE |
| `mixin/TargetGoalMixin` | 注入 `TargetGoal#canContinueToUse`，按 CAMOUFLAGE + 视线改目标持续性 | **完全排除**：不 Mixin TargetGoal |
| `mixin/MobMixin` | 注入 `Mob#aiStep`，直接 `setTarget(null)` 并 `navigation.moveTo(丢失坐标)` | 仅保留"前往坐标"的玩法意图，改为**纯 Goal**（不改目标、不碰 target 生命周期） |
| `network/SyncSoundPacket`（声音白名单同步） | 客户端白名单同步 + `soundOpen` 开关 | **不需要**：P0 无客户端状态，噪声全在服务端 |

保留的**玩法意图**只有一条：怪物（感染者）听到有意义的声响后，会前往声响坐标查看；`misc_twf 1.18.2` 中依赖 `SonaMobEffects.EXPOSURE` 的怪物蛋逻辑（见 §11.13）改接本模块 API。

补充（摘要核实）：Sona 的真实吸引半径语义藏在配置注释中——`(amplifier + 1) * (0.3 * FOLLOW_RANGE)`，**依赖 FOLLOW_RANGE**；本需求 §7.3 明确 `radius ≠ FOLLOW_RANGE`、§4.2 禁止修改 FOLLOW_RANGE，故该公式**不可沿用**，必须改用本模块的 `radius × hearingMultiplier` 体系。

### 11.3 工程约定

- modid：`misc_twf_hearing`；Java 根包：`com.hexagram2021.misc_twf_hearing`（内部 `api/core/ai/event/compat/config/debug/command` 分层）。
- 内容命名空间：沿用 `misc_twf`（决策 D6）——本模块 P0 **不注册任何物品/方块/实体/音效**，故命名空间仅用于 Tag 与命令反馈文本；无创造页、无模型资源。
- 数据 Tag：`misc_twf:hearing_mobs`、`misc_twf:hearing_blacklist`（需求原文写作 `winter_frontier:*`，按工程一致性改为 `misc_twf:*`，见 §11.13-D2）。
- 外部依赖：**核心零第三方依赖**（不 import ZT2/Improved Mobs/ServerCore/The Hordes/CrawlMod/Superb Warfare，全部隔离在 `compat/`）。
  Superb Warfare 若做枪声适配，用 `compileOnly` + `ModList.get().isLoaded("superbwarfare")` 判定，避免硬依赖。
- 与兄弟模块：H 不依赖任何兄弟模块；M4 需要 H 的 API（建议 optional + 运行时判定，见 §11.13-D6）。

### 11.4 包结构与类清单（P0）

```text
com.hexagram2021.misc_twf_hearing/
├─ api/       HearingApi, NoiseSpec, NoiseSnapshot, NoiseEmitEvent(Pre/Post), NoiseTypes
├─ core/      NoisePulse, NoiseManager, NoiseSpatialIndex, NoiseScoring, NoiseProfile(听觉数值)
├─ ai/        HearNoiseGoal
├─ event/     HearingGoalInjector(EntityJoinLevelEvent), NoiseLifecycleHandler(ServerTickEvent.Post),
│             VanillaNoiseEventHandler(原版行为→emit)
├─ compat/    superbwarfare/SuperbWarfareNoiseCompat
├─ config/    HearingConfig(server config)
├─ debug/     HearingDebugLogger
└─ command/   HearingCommand(/hearing stats|debug|emit)
```

### 11.5 数据模型与 API 定稿

```java
public record NoiseSpec(ResourceLocation type, float radius, float strength, int lifetimeTicks) {}

public record NoisePulse(long id, Vec3 position, ResourceLocation type, float radius, float strength,
                         long createdTick, long lastEmitTick, long expireTick, @Nullable UUID sourceUuid) {}

public record NoiseSnapshot(long id, Vec3 position, ResourceLocation type, float radius, float strength,
                            long createdTick, long lastEmitTick, long expireTick, @Nullable UUID sourceUuid) {}
```

- **id**：每个 `ServerLevel` 内单调递增（`long`），合并后保持不变。
- **可变性约定**：`NoisePulse` 为**内部可变类**（`final class`，字段可写）以便合并时就地更新 `position/expireTick/radius/strength/lastEmitTick`；对外**只暴露不可变 `NoiseSnapshot`**（record），`HearingApi` 永不返回 Pulse 引用。
- **事件可取消性**：`NoiseEmitEvent.Pre implements net.neoforged.bus.api.ICancellableEvent`（可取消 + 可改 `position`/`NoiseSpec`）；`NoiseEmitEvent.Post` 只读、不可取消。
- **类型命名**：噪声类型为 `ResourceLocation`，P0 常量集中在 `NoiseTypes`（`misc_twf:sprint`、`misc_twf:land`、`misc_twf:door`、`misc_twf:container`、`misc_twf:block_break`、`misc_twf:glass_break`、`misc_twf:gunshot`、`misc_twf:suppressed_gunshot`、`misc_twf:explosion`、`misc_twf:custom`），未配置类型回退到 `misc_twf:custom` 而非抛异常。
- **position**：发声瞬间快照，**不跟随来源移动**。
- **radius / strength**：`effectiveRadius = radius × hearingMultiplier`；`strength` 仅用于同一怪物比较多个声音，**clamp 0.0~1.0**（含 NaN/Infinity 拒绝）。
- **lifetime（区间随机）**：每种类型配置为**区间** `lifetime_min ~ lifetime_max`，emit 时按 `lifetimeTicks = lifetime_min + random.nextInt(max(1, lifetime_max - lifetime_min))` 掷一次（即 `20~40` → `random.nextInt(20) + 20`），保证同一行为不会每次都被精确定时遗忘。`NoiseSpec.lifetimeTicks` 保存**掷出的确定值**，`expireTick > createdTick` 恒成立。
- **lifetime 参考区间**：脚步/落地 20~40、开关门/容器 30~60、方块破坏/玻璃破碎 40~80、枪声/消音枪声 60~120、爆炸 80~160 tick。
- **合并规则**：仅当 `sourceUuid` 相同 + `type` 相同 + 时间窗口（默认 5 tick）内 + 距离窗口（默认 4.0）内才合并；更新 `position`（最新）、`expireTick`（延长）、`radius/strength`（取较大）、`lastEmitTick`（刷新），`id/createdTick` 保留。**匿名噪声（sourceUuid == null）P0 不跨 emit 合并**，避免相邻两个机关/爆炸被误并。

API（仅服务端、仅主线程，非主线程调用一律记录警告并拒绝）：

```java
NoiseSnapshot emit(ServerLevel, Vec3, NoiseSpec, @Nullable Entity);
NoiseSnapshot emit(ServerLevel, Vec3, ResourceLocation type, float radius, @Nullable Entity);   // strength/lifetime 取类型默认
List<NoiseSnapshot> query(ServerLevel, AABB area);                       // 语义：噪声"发生位置"落在 area 内
Optional<NoiseSnapshot> findStrongest(ServerLevel, AABB, Predicate);     // 语义：raw strength 最大
Optional<NoiseSnapshot> findBestAudible(ServerLevel, Vec3 listener, float hearingMultiplier, Predicate); // 综合评分
Optional<Entity> resolveSource(ServerLevel, NoiseSnapshot);
```

- `findStrongest` 严格定义为"比较 `strength`"；综合评分必须用 `findBestAudible`（避免命名歧义，需求 §15.3）。
- **评分公式（全局唯一实现于 `NoiseScoring`）**：

```text
distanceFactor  = clamp(1 - distance / effectiveRadius, 0, 1)
remaining       = clamp((expireTick - now) / (expireTick - lastEmitTick), 0, 1)
freshnessFactor = 0.5 + 0.5 × remaining
score           = strength × distanceFactor × freshnessFactor
```

- **事件**：`NoiseEmitEvent.Pre`（可取消、可改 `position`/`NoiseSpec`，**不外泄内部 Pulse**）；`NoiseEmitEvent.Post`（只读，持 `ServerLevel` + `NoiseSnapshot`，表示已入索引/已完成合并）。

### 11.6 生命周期与清理

- 每 `ServerLevel` 一个 `NoiseManager`，通过 **Level Attachment（非持久化，不提供 serializer）** 绑定，禁止 `static Map<ResourceKey<Level>, NoiseManager>`（避免重进世界/多次开服残留）。
- `ServerTickEvent.Post` 每 `cleanup_interval_ticks`（默认 20）对已存在的 Manager 做一次惰性清理；禁止每 tick 全 chunk 扫描。
- **不写 NBT / 不进 SavedData / 不存区块**；服务器重启与维度卸载即全部释放（预期行为）。

### 11.7 空间索引与查询代价

- 索引：`ChunkPos.asLong(x, z) → Long2ObjectMap<List<NoisePulse>>`（fastutil），Pulse 只放入**声源所在 chunk**的桶，不按半径复制。
- 监听点查询：`chunkRadius = ceil(effectiveMaxRadius / 16)`，只遍历已存在的桶；**查询不访问方块、不触发同步加载区块**。

### 11.8 听觉归属与配置

- 归属入口：EntityType Tag `misc_twf:hearing_mobs`（白名单）、`misc_twf:hearing_blacklist`（黑名单优先）；**不默认给所有 Monster/动物/Boss/NPC**，必须显式进 Tag。
- 数值入口：**P0 采用 Tag + server config 映射**（`hearingMultiplier` 等），理由是"稳定、可维护、无数据包重载需求风险"；P1 再评估 NeoForge Data Map `HearingProfile`（EntityType → 可重载数值），并预留 `HearingConfig#getProfile(EntityType)` 单一取值入口，便于后续无缝切换。
- server config 默认值（沿用需求 §41）：

```text
[goal]   priority=6, query_interval_ticks=10, query_jitter_ticks=4, repath_interval_ticks=20,
         max_path_failures=3, arrival_distance=3.0, speed_modifier=1.0,
         switch_threshold=1.15, switch_cooldown_ticks=8, min_score_threshold=0.10
[noise]  cleanup_interval_ticks=20, max_active_per_level=4096, max_active_per_chunk=128,
         merge_window_ticks=5, merge_distance=4.0, query_max_radius=128.0
```

- **不设日志开关配置**：是否需要 DEBUG 级别日志由整合包 `log4j2.xml` 的级别决定，避免出现"配置说开、日志级别说关"的两套开关；`/hearing debug <player> on|off` 只影响该管理员在游戏内的聊天/动作栏诊断输出，属会话级状态，不写入配置文件（需求 §41 中的 `[debug] enabled` 因此**不采纳**）。需要拼接昂贵调试字符串时使用 `MISCTWFHearingLogger.debug(Supplier)`，它在级别未开启时不会求值喵~

- 命名统一：需求 §18 的 `investigateSpeed` 与 §41 的 `speed_modifier` 是同一项，**配置键统一为 `speed_modifier`**（HearingProfile 内部字段沿用 `investigateSpeed` 亦可，但必须只有一个写入源）。
- `min_score_threshold` 对应需求 §23 第 5 步的"score > 最低阈值"——原文未给数值与键名，本方案暂定 `0.10`（**D9，待确认**）。
- **HearingProfile 载体与参数归属**：与实体类型相关的数值（`hearingMultiplier`、`queryInterval(+jitter)`、`investigateSpeed`、`arrivalDistance`）放 profile；与 Goal 行为相关的全局参数（`repath_interval_ticks`、`max_path_failures`、`switch_threshold`、`switch_cooldown_ticks`、`min_score_threshold`）放全局 `[goal]` 配置。需求 §48 伪代码中的 `profile.maxPathFailures()` 按此修正为读全局配置。
- **Data Map 选型证据**：全仓检索 `DataMapType`/`RegisterDataMapTypesEvent` 命中 **0**，即项目当前从未使用 NeoForge Data Map → P0 采用 Tag + server config，P1 再评估（回应需求 §49 第 7 问）。
- **`misc_twf:hearing_mobs` P0 初始清单**：需求未给实体清单。建议 P0 数据文件留空（由整合包数据包决定），并在文档给出建议初值：`misc_twf` 的僵尸动物家族实体（M1）+ 原版 `minecraft:zombie`/`husk`（可选）；`hearing_blacklist` 优先于白名单。

- 声音类型默认参数（**每种类型均为独立配置项**，默认值由开发按"接近现实可闻距离"填写，单位：格 / 0~1 / tick）：

| 类型（id = `misc_twf:<小写下划线>`） | radius 默认 | strength 默认 | lifetime 区间 | 现实取向与说明 |
| --- | --- | --- | --- | --- |
| `sprint` | 16.0 | 0.45 | 20~40 | 雪原安静环境下奔跑脚步的可闻距离；潜行不发声（无 SNEAK 类型） |
| `land` | 10.0 | 0.40 | 20~40 | 落地闷响，比脚步略弱 |
| `door` | 12.0 | 0.50 | 30~60 | 木门开合的短促声响 |
| `container` | 8.0 | 0.35 | 30~60 | 翻箱倒柜的细碎声，传播最短 |
| `block_break` | 14.0 | 0.50 | 40~80 | 挖掘/破坏方块的连续声 |
| `glass_break` | 20.0 | 0.65 | 40~80 | 玻璃破碎清脆且传得远 |
| `gunshot` | 48.0 | 1.00 | 60~120 | 未消音枪械（开阔地）；SW 存在时以 `compute().soundRadius` 覆盖半径 |
| `suppressed_gunshot` | 16.0 | 0.70 | 60~120 | 消音枪械；SW 存在时半径取 `max(4, soundRadius × 0.5)`（D7） |
| `explosion` | 64.0 | 1.00 | 80~160 | 爆炸巨响，传播最远、持续最久 |
| `custom` | 16.0 | 0.50 | 20~40 | 第三方 API 默认；未配置类型回退到它 |

- 配置键形如：

```toml
[noise_type.sprint]
    radius = 16.0
    strength = 0.45
    lifetime_min = 20
    lifetime_max = 40
```

- 类型 id 常量集中在 `api/NoiseTypes`；配置文件以 `misc_twf:<type>` 的 path 部分作为 section 名，缺失 section 时回退 `custom` 默认值并限频 warning。

### 11.9 Goal 与注入

- 注入：`EntityJoinLevelEvent`（服务端、`Mob`、Tag 命中且未注入）→ `mob.goalSelector.addGoal(priority, new HearNoiseGoal(mob, profile))`；
  **必须幂等**：用**非持久化 Entity Attachment**（`HEARING_GOAL_ATTACHED`）做重复注入标记。
- `HearNoiseGoal` 仅 `setFlags(EnumSet.of(Goal.Flag.MOVE))`：
  - `canUse()`：有真实 Target → false；`now < nextQueryTick` → false（否则按 `query_interval + rand(jitter)` 排下一次查询）；调用 `HearingApi.findBestAudible(...)`；命中则记录 `currentNoise/destination` 并首次寻路。
  - `canContinueToUse()`：有真实 Target → false；到达（`arrival_distance`）→ false；`failedPathAttempts >= max_path_failures` → false。
  - `tick()`：有 Target 立即让路；`nextSwitchAllowedTick` 之后用评分切换更值得调查的新声音（`newScore > currentScore × switch_threshold`）；`now >= nextRepathTick && navigation.isDone()` 才重新寻路。
  - `stop()`：清空本 Goal 状态；**P0 不主动 `navigation.stop()`**（避免清掉刚接管 Goal 的路径），由 ZT2/CrawlMod 实测后再定（§11.13-D5）。
- 明确禁止：`mob.setTarget(...)`、`targetSelector.addGoal(...)`、修改 `Sensing`/`TargetGoal`/`PathNavigation`/`NodeEvaluator`、为循声唤醒 inactive Mob、每 tick 重建路径。

### 11.10 噪声来源（P0 白名单）

- 原版：`SPRINT`、`LAND`、`DOOR`、`CONTAINER`、`BLOCK_BREAK`、`GLASS_BREAK`（只处理有明确玩法意义的行为，**禁止监听所有 `SoundEvent` 并一律 emit**）。
- 枪械：`GUNSHOT` / `SUPPRESSED_GUNSHOT`（`ShootEvent.Post`）；`EXPLOSION` 走 NeoForge 通用 `ExplosionEvent.Detonate`（见 §11.11）。
- 第三方：`CUSTOM`（公开 API）。
- 权威性：**只有服务端事件能 emit**；客户端音效播放不得反推噪声。

### 11.11 Superb Warfare 适配层（已对 1.21.1 源码核实）

| 事实 | 证据（1.21.1 源码已核实） |
| --- | --- |
| 存在服务端开火事件，且不可取消 | `com.atsuishio.superbwarfare.api.event.ShootEvent`（`Pre`/`Post`，`extends Event`，**未实现 ICancellableEvent**，`@ApiStatus.AvailableSince("0.8.8")`），取 `getShooter/getLevel/getData/getShootParameters` |
| 首选挂接点 | `ShootEvent.Post`：广播于 `GunItem.afterShoot`（`GunItem:550`，`Pre` 在 `beforeShoot:516`）。`Post` 复用服务端开火总入口 `GunItem.shoot(ShootParameters)`（`:629`），此处**已通过 `data.canShoot(ammoSupplier)` 弹药/过热校验**，且仅在 `shooter.level() instanceof ServerLevel` 时构造参数（`:612-622`）→ 天然满足需求 §33.1 优先级 1，且空枪/动画不会误触发 |
| 枪声半径数据来源 | JSON `"SoundRadius"`（示例 `sbw/guns/aa_12.json` = 18）→ `data/gun/DefaultGunData:226-228`（`@ServerOnly`，**无默认初始化值 0.0**）；数据表 `data/CustomData.GUN_DATA = "sbw/guns"`；读取用 `event.getData().compute().soundRadius`，枪械 id 用 `data.id`。`GunProp.SOUND_RADIUS` 已 `@Deprecated(forRemoval=true)` → **不使用** |
| 消音是**加法**且语义反直觉 | `GunItem:122 rawData.soundRadius += getCustomSoundRadius(gunData)`；`getCustomSoundRadius`（`:456-458`）在 `AttachmentType.BARREL == 2`（消音器）时返回 `0.6`，否则 `1`。即普通 = JSON+1.0、消音 = JSON+0.6：**两者都大于 JSON 原值**，消音只是"加得少"。因此 `SUPPRESSED_GUNSHOT` 的半径策略**必须由适配层自行决定**（见 §11.15-D7），不可照搬 `soundRadius` |
| 载具武器**确实走同一事件** | `entity/vehicle/base/VehicleEntity:1261/1273` 直接 `data.shoot(new ShootParameters(...))` → 触发 `ShootEvent`。故需求 §49 第 4 问的答案为"是，纳入 P0"，无需单独适配 |
| 爆炸无专用事件 | SBW 统一走 `tools/CustomExplosion.Builder.explode()`（`tools/CustomExplosion:40/323`，调用点遍布手雷/RPG/阔剑/C4/弹丸/无人机/载具等 20+ 处）→ 建议改挂 **NeoForge 通用 `ExplosionEvent.Detonate`**（服务端权威、覆盖所有模组、零 Mixin），而非在 compat 内 Mixin SBW |

适配层设计：

```text
ShootEvent.Post（服务端，已过弹药校验）
        ↓
GunNoiseResolver：GunData.compute().soundRadius + AttachmentType.BARREL
        ↓
NoiseSpec(type = GUNSHOT | SUPPRESSED_GUNSHOT, radius, strength, lifetime)
        ↓
HearingApi.emit(level, shooter.position(), spec, shooter)

ExplosionEvent.Detonate（NeoForge 通用，服务端）
        ↓
NoiseSpec(type = EXPLOSION, radius = 24, strength = 1.0, lifetime = 120)
        ↓
HearingApi.emit(level, explosion.position(), spec, null)
```

- 消音判定：`data.attachment.get(AttachmentType.BARREL) == 2` 即视为 `SUPPRESSED_GUNSHOT`；半径策略见 §11.15-D7（**默认 `max(4, soundRadius × 0.5)`**，因为 SBW 的 `soundRadius` 不能直接代表逼真噪声半径）。
- 装载具：走同一 `ShootEvent`，无需特判（已核实）。
- 爆炸：用 NeoForge 通用事件，覆盖 SBW 手雷/RPG/C4/无人机等全部 20+ 调用点，**零 Mixin**。
- 不做：从客户端音效反推、`setTarget`、`FOLLOW_RANGE` 修改、直接改 Goal 状态、Mixin SW 内部开火方法。
- 若 SW 缺失（`ModList` 未加载）：compat 层整体不注册，核心与爆炸/原版来源照常工作。

### 11.12 性能、容量与调试

- 容量保护：`max_active_per_level = 4096`、`max_active_per_chunk = 128`；超限时先清过期 → 再删最旧最弱 → 限频 warning，**不崩溃**。
- 性能硬约束：禁止全 Mob 扫描（只由 Goal 主动查询）、禁止全维度噪声扫描（走 chunk 索引）、禁止同步加载区块、查询错峰（interval + jitter）、路径请求限频（repath interval + `navigation.isDone()`）、`ServerCore` inactive Mob 自然不查询。
- 调试（P0.5）：`/hearing stats`（当前维度 active pulse 数、索引 chunk 数、5 秒内 emit/merge/query 数、平均候选数、path fail 数）、`/hearing debug <player> on|off`（仅对指定管理员显示 Mob UUID / Noise ID / type / score / distance / effectiveRadius / 结束原因）、`/hearing emit <type> <radius> <strength>`；默认不打印全服枪声日志。日志侧不做开关配置，统一由 `log4j2.xml` 级别控制（`MISCTWFHearingLogger.debug(Supplier)` 惰性求值，级别关闭时零开销）喵~
- **告警限频策略（按类别下沉，禁止全局单游标）**：日志工具类**不持有任何限频状态**，游标由业务对象各自持有——单维度容量告警与单区块容量告警各用 `NoiseManager` 的**实例** `TickRateLimiter`（200 tick 窗口，随管理器释放，天然按维度隔离且不跨存档残留）；`HearingApi` 检出的两类**调用方编程错误**（非法坐标、非服务端线程）采用"每个服务器会话、每个类别首次 WARN、之后同类降为 DEBUG"的 `AtomicBoolean` 策略。这样任意一类告警都不会抢走其它类别的输出机会，也杜绝"用限频掩盖真实异常"喵~

### 11.13 与现有模块/M4 的交互点

1. **M4 冒险（主要消费方）**：真实耦合点为 `MonsterEggBlockEntity`（1.18.2 根工程）第 146~149 行：`AABB.ofSize(blockPos.getBottomCenter(), 8.0D, 4.0D, 8.0D)` + `hasEffect(SonaMobEffects.EXPOSURE)` + `getAmplifier()` 分支。M4 拆分时删除全部 `com.scarasol.sona.*` import，改为：
   - 持续逻辑（"附近近期是否发生过声音"）→ `HearingApi.query(level, AABB.ofSize(pos.getBottomCenter(), 8.0D, 4.0D, 8.0D))`；
   - 一次性触发（"怪物卵被声音激活"）→ 监听 `NoiseEmitEvent.Post`（避免 BlockEntity 每 tick 轮询）。
2. **M1 僵尸动物**：感染者实体是否进 `misc_twf:hearing_mobs` 由**数据 Tag**决定（模块不硬编码实体类型）；Hordes 生成的感染者凡命中 Tag 即自然获得 Goal，不改 Horde 生成/AI/感染。**待实测**：Hordes 的生成路径是否触发 `EntityJoinLevelEvent`（若不触发，需为 Hordes 生成单独补一次性注入钩子，否则 §44.6 验收点不成立）。
3. **M3 工业 / 枪声吸引**：枪声由 hearing 的 SW 适配层统一 emit（M3 不再承担"枪声吸引"职责；`ShootEvent` 消费方唯一，避免重复 emit）。1.18.2 的 `mixin/tacz/ModernKineticGunScriptAPIMixin:101-109`（注入 TaC `SoundManager.sendSoundToNearby` 给射手加 EXPOSURE 等级 `useSilenceSound ? 0 : 3`，受 `MISCTWFCommonConfig.TACZ_WHITELIST` 反选）**随 1.21.1 换 SW 后整体作废**，直接删除；旧 `TACZ_WHITELIST` 配置项在本模块无对应物（若仍需白名单，改用 SW 枪械物品 id）。
4. **M2 农牧**：无交互（不改其数据与行为）。
5. **Sona 模组本体**：整合包中移除 Sona（其听觉/曝光体系被本模块替代）；如仍有其它 Sona 功能被其它模组引用，需在整合包清单中单独评估（见 §11.16 风险）。

### 11.14 实施阶段（H0~H4）与验收

| 阶段 | 交付 | 验收 |
| --- | --- | --- |
| H0 骨架 | ✅ **已完成**：`modules/misc_twf_hearing/` 工程、toml（含 optional superbwarfare）/mixin 配置（空）/server config、根包与主类、`settings.gradle` 接入 | `:misc_twf_hearing:jar` 独立 BUILD SUCCESSFUL（离线，22 条目） |
| H1 核心 | ✅ **核心已实现**：`NoisePulse`（内部可变）/`NoiseSpec`/`NoiseSnapshot`/`NoiseTypes`/`NoiseScoring`/`NoiseSpatialIndex`（区块桶）/`NoiseManager`（emit·合并·容量保护·过期清理·三种查询·统计）/`HearingApi`（线程与坐标校验）/`NoiseEmitEvent.Pre/Post`/`NoiseLifecycleHandler`（tick 清理 + 维度卸载/停服释放）；每类型 radius/strength/lifetime 区间均已配置化 | 编译通过（jar 37 条目）；**剩余**：原版行为来源监听（`VanillaNoiseEventHandler`）与 emit→query→合并→过期 的实机链路验证 |
| H2 AI | `HearNoiseGoal`、`EntityJoinLevelEvent` 注入 + Attachment 幂等、Tag 黑白名单、节流/错峰、到达/失败/切换/让路 | 原版僵尸：枪声后前往坐标；有 Target 时立即让路；`targetSelector` 未被触碰；**单机重进世界重复注入路径**与 **Hordes 生成是否触发 `EntityJoinLevelEvent`** 两项实测通过 |
| H3 兼容与工具 | SW 适配层（`ShootEvent.Post` + 消音分支 + `ExplosionEvent.Detonate`）、`/hearing` 命令、调试日志 | SW 各射速/消音/霰弹/爆炸/载具武器均产生正确类型与半径的 pulse；无客户端反推；命令权限等级 2 且不广播全服 |
| H4 联编验收 | 按需求 §43 性能场景与 §44 兼容矩阵逐项测试（ZT2 / Improved Mobs / ServerCore / CrawlMod / The Hordes / SW / misc_twf） | 逐条通过并记录；不达标项回退到设计（**不允许**通过扩大项目边界解决） |

- **H 全量完成后才启动 M4**（主人决策）：H0→H1→H2→H3→H4 顺序推进，M4 的怪物卵在 M4 阶段直接改接已验收的循声 API，避免二次改造。
- **量化验收指标（自行定义，需求 §43 未给数值，D9 待确认）**：200 感染者 + 每秒 10 次枪声场景下，hearing 引入的 MSPT 增量 ≤ 1.0 ms；单实体 query 频率 ≤ 1 / `query_interval_ticks`（含 jitter）；`/hearing stats` 的 `平均候选数` ≤ 8、`path fail` 占比 ≤ 10%。
- **构建环境要点（H0 实测）**：公司内网会拦截 `piston-meta.mojang.com`，必须加 `--offline`；且每个模块工程有**独立的** `.gradle/caches/minecraft`（含 `versions/<ver>/metadata.json`），新建模块首次构建前需从既有模块（如 `modules/misc_twf_wildlife/.gradle`）种子化复制该目录，否则离线报 `Could not find the file: .../metadata.json and we are offline`。
- `/hearing` 权限：`requires(source -> source.hasPermission(2))`，输出走 `sendSuccess/sendFailure`（不广播全服）。

性能基线（H4）：`spark` 采样对比"有/无 hearing"的 MSPT 与 Goal 数量；200+ 感染者场景下 query 有错峰、无 TPS 抖动；`/hearing stats` 指标在预期区间。

### 11.15 需求文档遗留问题与本方案处理（待主人拍板）

需求 §49 的 8 问，本方案建议答案：

| # | 问题 | 本方案建议 |
| --- | --- | --- |
| 1 | NoiseManager 存储方式 | **Level Attachment（非持久化，无 serializer）**；禁止静态 Map |
| 2 | 注入标记是否用 Attachment | **是**，非持久化 Entity Attachment `HEARING_GOAL_ATTACHED` |
| 3 | SBW 最可靠服务端开火入口 | **`api.event.ShootEvent.Post`**（已核实存在且带 `GunData`）；不 Mixin |
| 4 | 载具武器是否同一事件 | **是，已核实**（`VehicleEntity:1261/1273` 直接 `data.shoot(new ShootParameters(...))`）→ 纳入 P0，无需特判 |
| 5 | 被高优先级 MOVE Goal 抢占时 `stop()` 是否 `navigation.stop()` | **P0 不主动停**；由 ZT2/CrawlMod 实测决定（H2/H4） |
| 6 | Pulse 过期后是否继续前往旧坐标 | **继续调查**（目的地已是快照，不依赖 pulse 存活）。注意：需求 §24 把"已过期"列为 `canContinueToUse` 结束条件，与 §30-B 冲突——本方案采用 §30-B，并把 §24 的该条件删除（**D8，需业务方确认**） |
| 7 | 数值配置用 server config 还是 Data Map | **P0 用 Tag + server config**（已核实项目从未使用 Data Map）；P1 上 Data Map，保留单一取值入口 |
| 8 | `NoiseEmitEvent.Pre` 修改范围 | 允许 cancel（`implements ICancellableEvent`）+ 改 `position`/`NoiseSpec`，**不暴露内部 Pulse** |

**主人已确认的决策（2025 轮次）**：

- **D1 命名**：全部统一为 `misc_twf` 体系 —— modid `misc_twf_hearing`、根包 `com.hexagram2021.misc_twf_hearing`（内部 `api/core/ai/event/compat/config/debug/command`）。
- **D2 Tag 命名空间**：`misc_twf:hearing_mobs` / `misc_twf:hearing_blacklist`（黑名单优先）。
- **D4 依赖形态**：M4 → H 为 **optional**（`compileOnly` + `ModList` 判定）；H 缺失时 M4 怪物卵降级为"无听觉触发"，满足 §9.1"缺模块不崩溃"。
- **D5 Sona**：**移除 Sona，由 H 替代**；更新说明中告知玩家 SoundDecoy/曝光玩法消失。
- **D6 顺序**：**H 全量 H0~H4 完成并验收后再启动 M4 主体迁移**（不再采用"M4 与 H3/H4 并行"）。

**仍需主人/业务方拍板的事项（本方案已给默认取向）**：

- **D3 声音类型默认参数**：**已定**（主人确认 radius/strength 做成配置项、默认值由开发填写）→ 采用 §11.8 表格；lifetime 采用"区间配置 + `min + nextInt(max-min)` 随机"。
- **D7 SBW 消音半径策略**：**已定**（随其余内容一并认可默认取向）：`SUPPRESSED_GUNSHOT` 半径取 `max(4, compute().soundRadius × 0.5)`，strength `0.7`，lifetime `60~120`。
- **D8 §24 与 §30 的冲突**：**已定**——采用 §30 方案 B（已开始调查则继续走到旧坐标），**删除"Noise 已过期"作为 `canContinueToUse` 的结束条件**；`canContinueToUse` 只保留：有真实 Target（让路）、到达（`arrival_distance`）、路径失败超限三种结束原因。
- **D9 数值缺口 + 量化指标**：**已定**：`min_score_threshold` 默认 `0.10`；`type` 的 `ResourceLocation` 命名按 `misc_twf:<type>`；H4 量化指标见 §11.14。
- **其余摘要疑点（B/H/I/J/K/L/N/O/P/Q/R）本方案的处理**：B 命名统一为 `speed_modifier`（§11.8）；H `NoisePulse` 改内部可变类、`lastEmitTick` **纳入 P0**；I `Pre` 实现 `ICancellableEvent`；J profile 只管实体类型数值、Goal 行为参数放全局配置；K `hearing_mobs` P0 数据文件留空并给出建议初值（§11.8）；L Hordes 生成是否触发 `EntityJoinLevelEvent` 列为 H2 实测项；N 旧 `TACZ_WHITELIST` 作废（§11.13）；O 量化指标见 §11.14；P 单机重进世界的重注入列为 H2 实测项；Q 命令权限等级 2、不广播；R `NoiseManager` 标记 `@ApiStatus.Internal`，仅 `HearingApi` 对外。

### 11.16 风险清单

1. **Goal 竞争**：与 ZT2/Improved Mobs 的动态 Goal 共存 → 只用 `addGoal` + MOVE flag + 让路策略；若出现抢占异常，优先调 priority/节流，**不得**改为 Target 接管。
2. **ServerCore inactive**：远处枪声不会唤醒远处怪物（业务方已明确接受）→ 需在文档与验收中写明"不是 bug"。
3. **CrawlMod**：爬行 Goal 自然抢占即可，禁止强改 Pose。
4. **数据膨胀**：高射速武器靠合并 + 容量上限；若第三方 API 滥用，按限频日志定位。
5. **性能回归**：所有第三方兼容不得进入 core 包；H4 必须有 spark 基线与对比数据。
6. **玩法落差**：Sona 移除后，原本"误导怪物"的 SoundDecoy 玩法消失（业务方明确不迁移）；需在更新说明中告知整合包玩家。