# misc_twf_industry（工业生态模块）

模块化拆分 M3（见 `docs/MODULARIZATION.md` 5.3）。当前状态：**M3 业务代码已全部迁入并按 NeoForge 1.21.1 + Superb Warfare 完成移植**：`:misc_twf_industry:jar` BUILD SUCCESSFUL（343 条目，`misc_twf_industry-4.0.0.jar`），根工程 M3 残留检索 0 命中。运行期联编冒烟待依赖就绪。

- modid：`misc_twf_industry`；内容命名空间：`misc_twf`（决策 D6，注册 id 不做迁移）
- Java 根包：`com.hexagram2021.misc_twf_industry`（内部沿用 common/client/mixin/server 分层）
- 内容域：能源装备与紫外线灯（D1）、回收炉、弹药模具工业
- 兄弟模块依赖：无
- 外部依赖：create / createaddition / curios / tetrachordlib / geckolib / **superbwarfare（强制，运行时 modid = superbwarfare）**；
  Registrate / JEI / **chloride**（compileOnly 可选兼容）

## tacz → Superb Warfare 迁移决策（主人已确认）

调研报告：`docs/SuperbWarfare-API-调研报告.md`（SW 侧）、`docs/TACZ_1.18.2_API_REPORT.md`（TaC 侧）、`docs/M3_SW_GUN_AMMO_MAP.md`（枪械→弹药映射）。

1. **模具收敛为 5 种**：`handgun / rifle / shotgun / sniper / heavy` 各一套
   `<cat>_bullet_mold` 与 `<cat>_bullet_mold_completed`；`mold_detacher` 配方产出 `superbwarfare:<cat>_ammo`
   并返还同一模具；原 14 口径的注册项、配方、lang 键全部移除（存档兼容性在更新说明中声明）。
2. **背包弹药槽改用 SW 原生体系**（M4 执行）：不再走 TaC 的 `IAmmo`/口径弹药供给；
   弹药以 SW 弹药（`AmmoSupplierItem`）与弹药盒（`AmmoBoxItem`）为单位，供弹走 `PlayerVariable.ammo` 玩家弹药池。
3. **枪械精通与枪声吸引改官方事件**：删除 `mixin/tacz` 全部 7 个（含两个无引用的 `*TableResultAccess`）；
   枪声吸引用 `api.event.ShootEvent.Post`（白名单由 `tacz:GunId` 改为 SW 枪械物品 id），
   枪械精通伤害加用伤害事件 / `ProjectileHitEvent.HitEntity`（落在 M3 还是 M4 待定，倾向 M4）。
4. **SW 真实数据位置**：`C:\Projects\Sources-1.21.1\data\superbwarfare`；已核实枪械数据 `"ID"` + `"AmmoType": "@RifleAmmo"`、
   弹药配方为原版 `crafting_shaped` 产出 `superbwarfare:handgun_ammo`（count 64）。

## 已完成的迁移改造

- 注册/数据：模具 14 → 5（注册、`recipe/mold_detacher/*`、`recipe/mold_workbench/*`、`bullet_molds(_completed)` 标签、lang 14×2 → 5×2 键）。
- 回收炉配方书 `MISCTWFRecipeBookTypes`：去全部 TaC import，改 `GunItem` / `AmmoSupplierItem` / `AmmoBoxItem` 判定，
  分类=搜索/枪械/弹药/杂项，走 `RegisterRecipeBookCategoriesEvent`，图标 `ModItems.AK_47` 与 `Ammo.HANDGUN.getItemStack()`。
- 存档：灯坐标独立为 `MISCTWFLampSavedData`（存档名 `MiscTWF-Industry`，含 `dimensions/placeLamp/destroyLamp/denyMonsterSpawn`）。
- 点火微调：由 `common/IndustryEventHandler#onBlockToolModification`（`BlockToolModificationEvent`）实现；
  原 `FireChargeItemMixin` / `FlintAndSteelItemMixin` 已删除（与 mixins 配置同步）。
- 1.21 移植修复：`EventHooks.firePlayerSmeltedEvent`、`Capabilities.EnergyStorage.ITEM`、`EnumProxy#getValue()`、
  `Registries.ITEM`、`BlockEntityType.Builder.of(...).build(null)`、配方 API（`assembleAll()` 重载、容器物品转 `ItemStack`）等。
- `wayfarer_armors` 标签 id 修正为实际的 `wayfarer_helmet/chestplate/leggings/boots`。

## 脆弱效果（Fragile）归属

`MISCTWFMobEffects.FRAGILE` 与 `common/effect/FragileEffect` 同属本模块，形成完整闭环：

- **施加方**：`UltravioletLampBlockEntity`（紫外线灯照射怪物时施加脆弱 II/IV）；
- **伤害加成消费方**：`common/IndustryEventHandler#onLivingHurt`，订阅 `LivingDamageEvent.Pre`，
  按 `FragileEffect.getDamageMultiplier(amplifier)` 放大伤害；
- M1 的 `ZombieAnimalsEventHandler#onLivingHurt` 只保留僵尸山羊冲撞击退，不再处理脆弱。

## chloride（原 embeddiumplus）真暗兼容（已恢复）

- 依赖坐标不变：`curse.maven:embeddiumplus-931925:7378108`（该构件实为 **chloride 1.7.5**，运行时 modId = `chloride`，
  包 `me.srrapero720.chloride`，依赖 sodium）；模块 `compileOnly` 引入，toml 声明 `chloride` 可选依赖（非强制）。
- `mixin/chloride/DarknessMixin`：`@Inject(method = "updateLuminance", at = @At("HEAD"), cancellable = true)`，
  当玩家 Curios head 槽的 `misc_twf:night_vision_device` 或远行者头盔有电时，置 `Darkness.enabled = false` 并 `ci.cancel()`；
  已登记在 `misc_twf_industry.mixins.json` 的 `client` 列表。

## 资源状态

- 已补齐：`models/item/{mold_workbench,recovery_furnace}.json`（父级 `misc_twf:block/*`）、
  5 种模具的 10 个 item 模型；缺失的 `textures/block/night_vision_device.png`、`textures/block/mold_detacher.png` 亦已补。
- **待美术替换**：5 种模具的 10 张贴图目前是占位（复制自 `clay_mold.png`）。
