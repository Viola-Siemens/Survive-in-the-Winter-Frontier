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

> 说明：modid 可在拆前全局替换为更贴切的命名（如 M2 可用 `misc_twf_husbandry` 等），但必须保留 `misc` 前缀并全小写下划线。

### 4.2 命名与工程布局

- **内容命名空间（决策 D6）**：所有注册 id 与资源继续使用 `misc_twf`，**不做 id/资源命名空间迁移**。理由：
  - 移植期 id 稳定性：现存配方、战利品、lang、客户端模型引用均以 `misc_twf:*` 书写，全量改名成本高且易错；
  - 巢穴结构等二进制 NBT 数据中写死的方块 id 无需改写；
  - 同族 4 模块通常在整合包内成套安装，跨模块仍以 `misc_twf:*` 互相引用与现状一致。
- **Java 包**：按模块拆分根包 `com.hexagram2021.misc_twf_zombie_animals`、`..._wildlife`、`..._industry`、`..._adventure`（每模块内部沿用 `common/client/mixin/server` 分层），避免跨 jar 同名类。
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
  ```
  建议 `buildSrc`（或 included convention）统一模块构建；每模块自带 `neoforge.mods.toml`、`*.mixins.json`、独立配置与独立存档键名、独立创造页。
- 原 `misc_twf` 单体入口随迁移完成退役；发布由 4 个 jar 取代（整合包安装 4 个模块）。

### 4.3 模块间关系与依赖矩阵

```
M1 僵尸动物 ◄──(可选)── M4 冒险（疫苗写入免疫 API）
M3 工业生态： 能量/灯（Create:Additions）、回收炉/弹药工业（Create + 枪械）
M4 冒险： 医疗 / 背包×枪械 / 巢穴与怪物蛋 / 装饰（内部强关联，D2）

兄弟模块依赖方向：仅 M4 → M1（可选，提供疫苗写入免疫的 API）；
其余兄弟间无代码依赖；跨模块内容引用只存在于数据文件（见第 6 节处置表）。
```

| 模块 | 对兄弟模块 | 对外部模组（草案） |
| --- | --- | --- |
| M1 | 无（不依赖 M2/M3/M4） | hordes（强制，因豁免 mixin 目标为其类）；zombie_extreme（可选，数据） |
| M2 | 无 | 无强制（第三方 id 引用一律判空降级） |
| M3 | 无 | create、createaddition、curios、Registrate、ponder、tetrachordlib、枪械模组（tacz，见 6.2-E2）；JEI 可选 |
| M4 | M1（optional，仅医疗免疫写入） | travelersbackpack、枪械模组（tacz）、GeckoLib、expandability（强制）；zombie_extreme / kubejs / Sona（可选或数据引用，见 6.2） |

---

## 5. 各模块拆分明细

> 每模块给出职责、内容清单（Java / 资源 / mixin / 配置 / 存档 / 网络）、对外接口、解耦改造点与验收要点。

### 5.1 M1 `misc_twf_zombie_animals`（僵尸动物）

- **职责**：末日动物敌人本体 + "僵尸化/免疫"机制的属主（D4）。
- **内容清单**：
  - 实体：`zombie_chicken/cow/goat/pig/polar_bear/rabbit/sheep/wolf`（原 `MISCTWFEntities`、`common/entity/*`、AI goal）；
  - 行为：僵尸山羊冲撞击退、兔子跳跃、绵羊转化保留毛色、金苹果治疗（原 `ForgeEventHandler` 中随域方法）；
  - 效果：`FragileEffect`（如伤害加深语义属本域则带走；否则留给触发方，需运行时确认）；
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
- **阶段 3（冒险层收敛）**：M4 医疗/背包×枪械/巢穴与怪物蛋迁入并自洽；处理 6.2 各引用项（条件/懒加载/占位）。
- **阶段 4（依赖就绪联编）**：随第三方模组 1.21.1 版本更新逐项替换 TODO 占位；产出 4 个可执行 jar 并跑完整回归与冒烟矩阵。
- **阶段 5（收尾）**：退役原 `misc_twf` 单体；整理整合包安装清单（4 模块 + 外部依赖树）；更新 README/CHANGELOG/存档迁移说明。

---

## 9. 验收清单

1. **缺模块不崩溃（D5）**：每个模块在"仅本模块"与"本模块 ± 缺任一兄弟模块"组合下启动进游戏不崩溃、无系统级故障。
2. 数据降级表现记录在案：哪些表/配方在缺兄弟模块时会刷日志（期望清单），逐项确认后决定接受或加条件。
3. 跨模块免疫链路（M4 疫苗 → M1 免疫存档 → Hordes 豁免）在"M1+M4"与"仅 M4"下分别验证。
4. 聚合资源（lang/sounds.json）冲突测试：多 jar 并存时语言与音效完整。
5. mixin 共存冒烟：dev 环境合模（LivingEntity/Animal/Monster 等多模块注入目标类）。
6. 存档兼容：旧档加载测试 + 迁移/不兼容声明落地。

## 附录：待运行时确认清单

1. 僵尸动物的实际生成路径（Hordes 转化 / 怪物蛋 / 其它）与免疫覆盖范围；`TACZ_WHITELIST` 的消费机制归属。
2. 回收炉可回收输入集合与"点火微调面向原版熔炉"的范围。
3. 紫外线灯充能来源与 GUI 语义；`DarknessMixin`（embeddiumplus）归属。
4. 怪物蛋"曝光"机制与 Sona 依赖是否必须；怪物蛋 feature 对 zombie_extreme 缺失时的行为。
5. 各装饰方块是否有隐藏交互（逐个右键回归）。
6. 弹药槽对弹药品类的过滤口径与枪械模组版本绑定。
7. `DeferredBlock`（createdeco/verdure 等）懒解析在方块缺失时的实际降级行为。
8. 数据条件（`mod_loaded`）在配方/战利品/worldgen 中各自的支持情况（决定 6.2 各处置的最终形态）。
