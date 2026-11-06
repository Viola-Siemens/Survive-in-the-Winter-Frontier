# M4 Phase A 迁移报告（misc_twf_adventure）

> 范围：根工程 `misc_twf` 单体剩余 Java/资源整体迁入 `modules/misc_twf_adventure`，只做机械迁移 + 明确删除项；**不处理 1.18→1.21 语义**（Phase B）。
> 本轮未运行 Gradle（子代理沙箱限制），编译验证由主人执行。

## 1. 文件计数

| 项 | 数量 |
| --- | --- |
| 根工程迁移前 Java | 140 |
| 迁入模块 Java（含文本变换） | 131 |
| 模块 Java 总数（含重写的主类） | 132 |
| 根工程迁移前资源 | 470 |
| 迁入模块资源 | 467 |
| 模块资源总数 | 470（467 + 原有 toml/AT/mixins） |
| 删除的根 Java | 140（全部；根 `src/main/java` 已为空） |
| 删除的根资源 | 467 |
| 保留在根的资源 | 3（`neoforge.mods.toml`、`accesstransformer.cfg`、`misc_twf.mixins.json`） |

## 2. 未复制 / 删除清单（9 个 Java）

| 文件 | 原因 |
| --- | --- |
| `SurviveInTheWinterFrontier.java` | 主类职责并入 `MiscTwfAdventure` |
| `mixin/tacz/{AbstractGunItemMixin,EntityKineticBulletMixin,GunAnimationStateContextMixin,GunHudOverlayMixin,ModernKineticGunScriptAPIMixin}.java` | TaC 已由 SW 替代，按决策删除 |
| `mixin/tacz/package-info.java` | 整包删除后的孤儿 |
| `mixin/embeddiumplus/package-info.java` | 空包孤儿（DarknessMixin 在 M3） |
| `mixin/vanilla/items/package-info.java` | 空包孤儿（补丁类已迁 M3） |

## 3. 文本变换规则

- `package com.hexagram2021.misc_twf` → `package com.hexagram2021.misc_twf_adventure`
- `import com.hexagram2021.misc_twf.` → `import com.hexagram2021.misc_twf_adventure.`（其余全限定名同理）
- 原 `import static ...SurviveInTheWinterFrontier.MODID;` → 同时静态导入 `MiscTwfAdventure.CONTENT_NAMESPACE` 与 `MiscTwfAdventure.MODID`（29 个文件）
- 裸 `MODID` → `CONTENT_NAMESPACE`（注册/资源命名空间），随后把 `@EventBusSubscriber(modid = CONTENT_NAMESPACE` 修正为 `modid = MODID`（3 个事件处理器）
- `MiscTwfAdventure` 新增 `public static final String CONTENT_NAMESPACE = "misc_twf";`，`MODID = "misc_twf_adventure"`

## 4. 主类接线（合并原单体初始化职责）

`MiscTwfAdventure` 构造方法按顺序调用：`MISCTWFItems/Fluids/BlockEntities/MenuTypes/RecipeTypes/RecipeSerializers/DataComponentTypes/CreativeModeTabs/Attributes/StructureTypes/TravelersBackpackTacOps/Skills` 的 `init(modBus)`；`modBus.register(MISCTWFContent.class)`（酿造/属性/网络处理器）；`FMLCommonSetupEvent#enqueueWork(ModVanillaCompat::init)`。

## 5. Mixin

- 迁移并登记：`travelersbackpack.TravelersBackpackBlockEntityMixin`、`travelersbackpack.TravelersBackpackContainerMixin`、`vanilla.entities.EntityMixin`；client：`travelersbackpack.TravelersBackpackScreenMixin`
- 模块 `misc_twf_adventure.mixins.json` 已更新；根 `misc_twf.mixins.json` 已清空（避免悬空条目）

## 6. 资源重复检测

与 M1/M2/M3 模块比较：仅 `META-INF/neoforge.mods.toml`、`META-INF/accesstransformer.cfg` 同名（属模块自身配置，未复制）；**无内容资源重复**。`assets/misc_twf/lang/*` 与 `sounds.json` 本就不在根工程（已由 shared-resources 统一），未复制。

## 7. tacz id 清单（8 个战利品表，20 个不同 id，80 次命中）

| 文件 | id（次数） |
| --- | --- |
| `data/misc_twf/loot_tables/blocks/ammunition_box.json` | `tacz:12g,30_06,308,338,357mag,45acp,46x30,50ae,50bmg,556x45,57x28,58x42,68x51fury,762x25,762x39,762x54,9mm` 各 ×1；`tacz:ammo` ×17 |
| `data/misc_twf/loot_tables/blocks/stacked_ammunition_box.json` | 同上（17 口径 ×1 + `tacz:ammo` ×17） |
| `data/misc_twf/loot_tables/blocks/body_headless_soldier.json` | `tacz:556x45` ×1、`tacz:ammo` ×1 |
| `data/misc_twf/loot_tables/blocks/body_nailed_soldier.json` | `tacz:556x45` ×1、`tacz:ammo` ×1 |
| `data/misc_twf/loot_tables/blocks/body_sit_soldier.json` | `tacz:556x45` ×1、`tacz:ammo` ×1 |
| `data/misc_twf/loot_tables/blocks/magazine_556.json` | `tacz:556x45` ×1、`tacz:ammo` ×1 |
| `data/misc_twf/loot_tables/blocks/m4a1_carbine_alternative.json` | `tacz:556x45` ×1、`tacz:ammo` ×1 |
| `data/misc_twf/loot_tables/blocks/m4a1_carbine.json` | `tacz:m4a1` ×1、`tacz:modern_kinetic_gun` ×1 |

### 建议弹药映射（供主人确认，本轮**未改数据**）

| TaC 口径 | 建议 SW 弹药 | 置信度 |
| --- | --- | --- |
| `12g` | `superbwarfare:shotgun_ammo` | 高 |
| `9mm`、`45acp`、`357mag`、`50ae`、`762x25` | `superbwarfare:handgun_ammo` | 中 |
| `556x45`、`762x39`、`762x54`、`58x42`、`68x51fury`、`46x30` | `superbwarfare:rifle_ammo` | 中 |
| `308`、`30_06`、`338` | `superbwarfare:sniper_ammo` | 中 |
| `50bmg` | `superbwarfare:sniper_ammo` 或 `heavy_ammo` | **待确认** |
| `tacz:ammo`（通用物品） | 需按所在战利品的 `functions/count` 上下文决定 | **待确认** |
| `tacz:m4a1` / `tacz:modern_kinetic_gun`（枪械物品与 NBT 类型） | 需具体 SW 枪 id（如 `superbwarfare:ak_47` 之类） | **待确认（等映射表）** |

## 8. 缺失符号（迁移前根工程即缺失，Phase B 需补）

| 符号 | 引用次数 | 说明 |
| --- | --- | --- |
| `MISCTWFBlocks` | 17 | 方块注册类整个文件缺失（`ModVanillaCompat`、多处方块引用） |
| `MISCTWFCommonConfig` | 8 | 配置类缺失（含 `POSSIBILITY_*_DESTROY_EGG`、`STRONG_STOMACH_SKILL_LEVELS`、`GUN_MASTERY_SKILL_LEVELS` 等） |
| `MISCTWFImmunitySavedData` | 6 | 实为 **M1** 的 `com.hexagram2021.misc_twf_zombie_animals.server.MISCTWFImmunitySavedData`，迁移后 import 被改成 adventure 包，需改回 M1 包并接其公开 API |

## 9. 依赖变更

- `build.gradle` 新增：`compileOnly project(':misc_twf_zombie_animals')`、`compileOnly project(':misc_twf_hearing')`、`compileOnly 'curse.maven:jade-324717:8591319'`（`MutantPotionCauldronProvider` 需要，M2 同款坐标）
- `neoforge.mods.toml` 新增 optional：`misc_twf_hearing`、`misc_twf_zombie_animals`；更新 SW 注释（不再写"代码命名空间仍为 tacz"）；Sona 说明改为"已移除，由 hearing 替代"
- **仍缺依赖坐标（未声明，Phase B 待主人提供）**：`com.seniors.justlevelingfork`（Skill/Passive 注册，10 处引用）、`net.mcbbs.uid1525632.hungerreworkedreforged`（`AttributeRegistration`，1 处引用）

## 10. 预期下一轮编译问题（按类别）

| 类别 | 计数 | 备注 |
| --- | --- | --- |
| `net.minecraftforge.*` 残留 | 5 | 改 `net.neoforged.*`（`ForgeRegistries` 22 处、`DistExecutor`/`ForgeConfigSpec` 等） |
| `level` 字段直接访问（近似） | 419 | 实体/方块实体 `level` 字段 → `level()`；BlockEntity 侧保留字段访问 |
| TaC API（`com.tacz`） | 1 | 剩余 `IAmmo` 体系 20 处（`BulletSlotItemHandler`/`IAmmoBackpack` 等），Phase B 换 SW 弹药判定 |
| Sona `EXPOSURE` | 3 | `MonsterEggBlockEntity`；Phase B 改接 `NoiseEmitEvent.Post` |
| `MISCTWFBlocks` / `MISCTWFCommonConfig` | 17 / 8 | 类缺失，需补 |
| `MISCTWFImmunitySavedData` | 6 | 改指 M1 包 |
| justlevelingfork / hunger reworked | 10 / 1 | 缺依赖 |
| GeckoLib 3→4 | 6 个文件 | `GeoItem/AnimationFactory/RenderProvider` 等 API 差异 |
| travelersbackpack API | 28 个文件 | 1.21 版包路径/方法可能变化 |
| `@OnlyIn(Dist)` | 3 | NeoForge 1.21 已无该注解 |
| Forge 网络（`SimpleChannel`） | 1 | 改 `RegisterPayloadHandlersEvent`（`MISCTWFContent` 已用新 API，检查是否残留） |
| 数据组件 / BlockEntity 存档 | — | 1.20.5+ `loadAdditional(CompoundTag, HolderLookup.Provider)`、`ItemStack#getTag` 已移除（当前未见 `getTag`） |
| 结构/特征注册 | — | `StructureType`/`StructurePieceType` 注册 API 变化 |
| 创造页 | — | `CreativeModeTab` 注册 API 需核对 |
| 数据路径 | — | `data/forge/loot_modifiers/` → `data/neoforge/loot_modifiers/`；`data/forge/tags/blocks/mineable/*` → 单数 `block`（待确认） |

## 11. 待确认（请主人/业务方拍板）

1. `tacz:ammo` 通用物品在各战利品表里的具体语义（数量/品质由什么决定）。
2. `tacz:m4a1` 与 `tacz:modern_kinetic_gun`（NBT 枪械）对应哪把 SW 枪。
3. `50bmg` 归 `sniper_ammo` 还是 `heavy_ammo`。
4. `MISCTWFBlocks`/`MISCTWFCommonConfig` 是按 1.18.2 源码补回，还是本轮按需求重写。
5. justlevelingfork 与 hunger reworked reforged 的 CurseForge 坐标/版本。
6. `data/forge/*` 旧路径是否在本轮一并现代化（涉及整合包数据包引用）。
7. travelersbackpack 依赖坐标 `7543685` 是否确为 1.21.1 版本（若否则需换版本，mixin 目标也会变）。

## 12. Phase B 待办（不在本轮）

- 背包弹药槽：删 `IAmmo` 体系，改 `AmmoSupplierItem`/`AmmoBoxItem` 判定；
- 怪物蛋：删 Sona EXPOSURE，改 `NoiseEmitEvent.Post`（M4 → hearing optional）；
- 疫苗：`MISCTWFImmunitySavedData` 改指 M1 API；
- 数据：按映射表重写 8 个战利品表的 tacz id；
- Forge→NeoForge API、1.18→1.21 数据组件/网络/创造页、GeckoLib/travelersbackpack 适配；
- 补 `MISCTWFBlocks`/`MISCTWFCommonConfig`；
- 根工程收尾（根 java 已空、根资源仅 3 个配置）。