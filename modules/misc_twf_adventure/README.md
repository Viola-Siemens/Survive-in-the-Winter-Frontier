# misc_twf_adventure（冒险与探索模块）

模块化拆分 M4（见 `docs/MODULARIZATION.md` 5.4）。当前状态：**业务代码已全部迁入并按 NeoForge 1.21.1 + Superb Warfare 完成移植（v1）**：`:misc_twf_adventure:jar` BUILD SUCCESSFUL（约 750 条目 / 170+ 个 class / 526 个资源，`misc_twf_adventure-4.0.0.jar`）；根工程已改为**聚合 jar**（`misc_twf-4.0.0.jar` 通过 `META-INF/jarjar/` 内嵌五个模块）。根 `src/main/java` 仅保留聚合主类。运行期联编与视觉细节待实机验证。

- modid：`misc_twf_adventure`；内容命名空间：`misc_twf`（决策 D6，注册 id 不做迁移）
- Java 根包：`com.hexagram2021.misc_twf_adventure`（内部沿用 common/client/event/menu/mixin/server 分层）
- 内容域：生化医疗玩法、旅行背包×枪械联动、深渊巢穴与怪物蛋世界生成、末世装饰（D2）
- 兄弟模块依赖：M1 免疫 API（optional，M4 → M1）、H 循声 API（optional，M4 → H）
- 外部依赖：travelersbackpack（10.1.33）/ superb-warfare（0.8.9）/ geckolib / expandibility（强制）；zombie_extreme / kubejs（可选数据引用）

## 构建与运行

```powershell
gradlew :misc_twf_adventure:jar --offline       # 编译并打包（本机内网需 --offline）
gradlew :misc_twf_adventure:runClient           # dev 客户端（需装齐依赖）
gradlew :misc_twf_adventure:data                # 数据生成到 src/generated/resources
```

## 迁移落地要点（与 1.18.2 单体的差异）

- **Sona 已由循声模块 H 整体替代**：`MonsterEggBlockEntity` 删除 `EXPOSURE`/`CAMOUFLAGE` 轮询，改为由 `event/MonsterEggHearingHandler` 监听 `NoiseEmitEvent.Post`，按噪声 `strength` 映射为听声等级（`>=0.9→3`、`>=0.6→2`、否则 1）并衰减；怪物蛋破壳判定与动画广播（`PacketDistributor`）保留。
- **GameStages 门槛移除**：`MutantPotionCauldronBlock#hasStageToCovert` 恒 `true`（`net.darkhax.gamestages` 依赖删除）。
- **TaC → Superb Warfare**：全模块 `com.tacz` Java 引用为 0；战利品表按 `docs/M4_TACZ_TO_SW_MAP.md` 改写（`tacz:m4a1 → superbwarfare:m_4`，16 口径 → 5 类 SW 弹药，弹药箱合并为 5 条，移除 TaC 枪械 NBT）。
- **背包弹药槽（决策 D-A）**：`BackpackAmmoHandler` 直接读写背包物品的 `TRAVELERS_BACKPACK_TAC_DATA` 数据组件（9 格，只接受 `AmmoSupplierItem`/`AmmoBoxItem`），原先两个 NBT mixin 与 `IAmmoBackpack` 已删除；`common/compat/superbwarfare/BackpackAmmoFeeder` 在 `ShootEvent.Pre`/`ReloadEvent.Pre` 把槽内弹药结算进 `PlayerVariable.ammo`（受 `Ammo#getLimit()` 上限约束）。
- **Travelers Backpack 1.21 API**：`BackpackWrapper` 统一承载状态；菜单改为继承 `BackpackBaseMenu`（`AbstractTravelersBackpackTacMenu`），物品/方块实体菜单分别用 `screenId+index`、`screenId+BlockPos` 解析；`TacButton` 注入 `BackpackScreen#initButtons` 尾部（`TravelersBackpackScreenMixin`）。
- **世界生成**：删除 4 个 1.18 动态注册表类（`MISCTWFConfiguredFeatures/PlacedFeatures/ConfiguredStructures/StructureSets`），改为数据驱动；`worldgen/configured_feature|placed_feature/monster_egg.json` 保留，并新增 `worldgen/structure|structure_set/boss_lair.json`（`type: misc_twf:boss_lair`、`biomes: #misc_twf:has_structure/boss_lair`、`spacing 40 / separation 12`）。
- **数据路径现代化**：`forge/loot_modifiers` → `neoforge/loot_modifiers`；`forge/tags/{blocks,items}` → `{block,item}`；`minecraft/tags/{blocks,items}` 单数化；`crops/seeds` 迁 `c:`；`forge/biome_modifier` → `neoforge/biome_modifier`。
- **缺依赖清理**：`hunger_reworked`（本地无缓存）不与 `justlevelingfork` 引入；`MISCTWFSkills` 保持注释状态，`MISCTWFAttributes` 仅保留原生 `misc_twf:gun_mastery`。
- **装饰方块注册**：按 blockstate/loot/物品模型证据重建 41 个装饰与尸体方块（`MISCTWFBlocks` 共 44 个），`BodyBlock(xSize,zSize)` 尺寸由模型像素范围推断；`bloodstain`/`bullet_hole`/`wastepaper` 仅注册方块不注册物品。
- **枪械精通（gun_mastery）重做**：不再用 TaC mixin，改为 `common/compat/superbwarfare/GunMasteryHandler` 监听 `LivingDamageEvent.Pre`：当伤害直接来源是 SW `ProjectileEntity` 且持有者拥有 `misc_twf:gun_mastery` 属性时，伤害乘以 `1 + 属性/100`（与 1.18.2 旧 `EntityKineticBulletMixin` 等价，属性范围 0~100，最大 2 倍）。
- **1.21 数据目录单数化**：`loot_tables/` → `loot_table/`、`recipes/` → `recipe/`、`tags/fluids/` → `tags/fluid/`、`loot_table/blocks/` → `loot_table/block/`（1.21 起注册表目录为单数，复数目录不会被加载）。

## 待办与风险（需实机验证）

1. **弹药持久化**：物品形态依赖 TB 把 `BackpackWrapper` 的 stack 写回物品、方块实体形态依赖 `BackpackBlockEntity` 写回；两条路径都需进游戏核对（`BackpackAmmoHandler#onContentsChanged` 已留 TODO）。
2. **TAC 界面视觉**：`TravelersBackpackTacScreen` 仍是 176×144 自定义背景，与 TB 原生 `BackpackScreen` 的滚动/行高未合并，弹药槽位置需微调。
3. **装饰方块注册**：已按 blockstate/loot/物品模型证据补齐 41 个（`MISCTWFBlocks` 共 44 个）；仍无 Java 类的 16 个旧 id（`blood`、`blood_block`、`flesh_and_blood_*`、`infected_*`、`intestine`）与 `magazine_556`、`m4a1_carbine_alternative` 的资源目前悬空，待确认是删除资源还是补实现。
4. **Boss 巢穴生成**：数据入口已补，但 `BossLairFeature/BossLairPieces` 的生成逻辑需要实机确认（1.18→1.21 结构 API 变化较大）。
5. **技能系统**：`strong_stomach`/`gun_mastery` 的技能解锁依赖已移除（原依赖 hunger reworked 属性），当前仅保留 `gun_mastery` 属性；是否启用技能系统待定。
6. **语言与 sounds.json** 统一放 `shared-resources/`，本模块不重复。
