# misc_twf_wildlife（农牧生态模块）

模块化拆分 M2（见 `docs/MODULARIZATION.md` 5.2）。当前状态：**M2 盘点完成，迁入工程进行中**（尚未编译闭环，未裁根工程）。

- modid：`misc_twf_wildlife`；内容命名空间：`misc_twf`（决策 D6）
- Java 根包：`com.hexagram2021.misc_twf_wildlife`（内部沿用 common/client/mixin/server 分层）
- 模块入口：`MiscTwfWildlife`（骨架已就位，待接线内容注册）
- 兄弟模块依赖：无

## 内容域与迁移清单（已核实，来源为根工程现状）

**方块/物品/数据（随迁移放入模块 register 子集）**
- 方块：`winter_wheat`（冬小麦作物，种子由 `MISCTWFItems.Materials.WINTER_WHEAT` 提供）与 `dead_*` 动物尸体九族（chicken/cow/goat/horse/pig/polarbear/rabbit/sheep/wolf，`MISCTWFBlocks.DeadAnimals`）
- 物品：`yarn`、`animal_poop`（BoneMealItem）、`winter_wheat`、`winter_wheat_seeds`（现位于 `MISCTWFItems.Materials`）
- 方块实体：`dead_animal`（`DeadAnimalBlockEntity`，9 方块共用）；数据组件 `dead_animal_data`（`DeadAnimalData`）
- 附件：`pooping`（`AttachmentType<PoopingAnimal>`，key 取自 `pooping` 常量）；标签：`entity_types/pooping_animals`、尸体刀类工具标签（待定位 `KNIVES` 常量位置）
- 尸体掉落实体注入：`LivingEntityMixin#misc_twf$replaceLootTable`（dropAllDeathLoot wrap）

**机制类（整类迁移）**
- `IProduceMilk` + `CowEntityMixin`/`GoatEntityMixin`（产奶冷却，`MILK_INTERVAL`）
- `PoopingAnimal`/`IPoopingAnimal` + tick 逻辑（原 `ForgeEventHandler#onLivingTick`，按 `pooping_animals` 标签）
- 食性微调：`AnimalEntityMixin`、`AbstractHorseEntityMixin`（冬小麦可食）、`ParrotEntityMixin`（冬小麦驯食）；面包/蛋糕/曲奇等为数据层修改
- Jade 兼容：`LivingPoopProvider`/`MobProduceMilkProvider` 迁移入模块，模块新建 Jade 插件类（仅 M2 的两个实体 provider；根 `WailaHelper` 中的 M4 药水锅部分保留）
- 配置：`MILK_INTERVAL`、`ANIMAL_POOPING_INTERVAL`、`ANIMAL_POOPING_INTERVAL_NOISE`（模块 `MISCTWFWildlifeConfig`）

**饮食盐分组微调（EasyDiet，D3 归 M2，但暂缓）**
- 原 `LivingEntityMixin#misc_twf$applyToDietIfSalted` 引用 `top.theillusivec4.diet.api`，当前 easydiet 1.21.1 构件下该包不存在（根编译已证实）——按"可选依赖 + 约定 API"暂不迁入，代码留档本模块注释；待确认 EasyDiet 1.21.1 API 后补齐（见"待主人确认"）。

## 待主人确认/待办

1. **EasyDiet 1.21.1 API**：盐分组联动需要的 `diet.api` 包在当前 easydiet 构件中缺失（或需新坐标/新包名）。提供 EasyDiet 1.21.1 源码或正确构件坐标后再迁该段（可选）。
2. **Jade 处理方式**：M2 的实体信息 provider（PoopCD/产奶冷却）依赖 Jade API。建议模块以"可选兼容"承载（compileOnly jade + 模块自带 @WailaPlugin 子集，Jade 缺失时不被加载），但模块 toml 不声明 mandatory；请确认此处理（或将 Jade provider 与 root 的 M4 锅 provider 一起留到后续轮统一迁移）。
3. **第三方掉落物（E5 硬查加固）**：尸体掉落含 delightful/cold_sweat/kubejs 注册名直查（现未判空，缺失时可能产出 AIR）。模块迁入时按 E5 改为"判空 + AIR 过滤 + 日志"，与根旧实现形成小差异（会在 §10 记录）。
4. 面包/蛋糕/曲奇、冬季作物种植、发射器交互等**数据层**内容清单将在迁入阶段逐项核对（lang 聚合已就位）。

## 构建与运行（沿用骨架约定）

```powershell
gradlew :misc_twf_wildlife:jar        # 模块打包
gradlew :misc_twf_wildlife:runClient  # dev 客户端
```
