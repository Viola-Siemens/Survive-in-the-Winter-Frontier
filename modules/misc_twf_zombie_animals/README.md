# misc_twf_zombie_animals（僵尸动物模块）

模块化拆分 M1（见 `docs/MODULARIZATION.md` 5.1）。当前状态：**M1 业务代码已迁入并按 NeoForge 1.21.1 移植，模块可独立编译打包**；运行期联编冒烟待第三方依赖就绪后执行。

- modid：`misc_twf_zombie_animals`；内容命名空间：`misc_twf`（决策 D6，注册 id 与资源路径未迁移）
- Java 根包：`com.hexagram2021.misc_twf_zombie_animals`（内部沿用 common/client/mixin/server 分层）
- 模块入口：`MiscTwfZombieAnimals`（MODID + CONTENT_NAMESPACE 常量）
- 内容域：
  - 8 种僵尸动物实体（`ZombieAnimalEntity` 基类 + 鸡/山羊/北极熊/兔/绵羊子类，牛/猪/狼走泛型工厂）与 AI/行为；
  - 客户端模型（8）、渲染器（8）、模型层注册（`client/MISCTWFModelLayers`、`client/ModClientEventHandler`）；
  - 音效注册 `common/register/MISCTWFSounds` + `assets/misc_twf/sounds/zombie_*` ogg；
  - 实体贴图（9 张，含 `zombie_sheep_fur`）；实体战利品表（`data/misc_twf/loot_tables/entities/zombie_*.json`）；
  - 僵尸化/免疫属主（决策 D4）：`server/MISCTWFImmunitySavedData`（存档名 `MiscTWF-ZombieAnimals`）+ 免疫 API；
  - Hordes 联动：`mixin/hordes/InfectionEventHandlerMixin`（豁免感染施加与感染死亡转化）+ `data/hordes/tags/entity_types/infection_entities.json`；
  - 事件处理 `common/ZombieAnimalsEventHandler`（山羊冲撞击退、绵羊转化保色）；配置 `common/config/MISCTWFZombieAnimalsConfig`（金苹果治愈开关）。
- 兄弟模块依赖：无（可选被 M4 消费免疫 API，方向 M4 → M1）；根工程过渡期以 `implementation project(...)` 引用本模块提供疫苗免疫 API。
- 外部依赖：hordes（强制，mixin 目标）+ atlaslib（hordes 运行依赖）；zombie_extreme 仅数据引用（可选）。

## 构建与运行

```powershell
gradlew :misc_twf_zombie_animals:build          # 编译并打包（含 mixin 注解处理与资源）
gradlew :misc_twf_zombie_animals:runClient      # dev 客户端（需装齐草案依赖）
gradlew :misc_twf_zombie_animals:data           # 数据生成到 src/generated/resources
```

## 迁移说明

- 原单体中的脆弱效果（FragileEffect）实际仅由工业模块紫外线灯施加（证据：`UltravioletLampBlockEntity`），
  按“留给触发方”原则留在工业域，未随 M1 迁出（文档 §5.1 待办清单据此更新，见 §6 归属修订）；
- 移植要点：`Entity.level()`、`SynchedEntityData.Builder`、`Mob#finalizeSpawn` 4 参、`MeleeAttackGoal.checkAndPerformAttack(LivingEntity)`、
  `GameEvent.ENTITY_INTERACT`、mob 转化/破坏判定走 `net.neoforged.neoforge.event.EventHooks`；
- 免疫存档 NBT 反序列化顺带修正了原单体“误读外层 nbt”的疑似笔误（见代码 TODO 注释）；
- 语言与 sounds.json 等聚合文件统一在根目录 `shared-resources/`，本模块 jar 与其余模块冗余一致。
