# Superb Warfare 枪械 → 弹药 映射表（自动提取）

来源：`C:\Projects\Sources-1.21.1\data\superbwarfare\sbw\guns\*.json`（43 个枪械数据文件，只读提取）。
用途：M3/M4 的枪械白名单（原 `TACZ_WHITELIST`）、战利品表与枪械分类判定。

## 按弹药类别分组

| 弹药类别（AmmoType） | 对应物品 | 枪械（`superbwarfare:<id>`） |
| --- | --- | --- |
| `@HandgunAmmo` | `superbwarfare:handgun_ammo` | `glock_17`、`glock_18`、`m_1911`、`mp_443`、`mp_5`、`vector` |
| `@RifleAmmo` | `superbwarfare:rifle_ammo` | `ak_12`、`ak_47`、`devotion`、`hk_416`、`insidious`、`m_4`、`m_60`、`marlin`、`minigun`、`mk_14`、`qbz_191`、`qbz_95`、`rpk`、`sks`、`trachelium` |
| `@ShotgunAmmo` | `superbwarfare:shotgun_ammo` | `aa_12`、`homemade_shotgun`、`m_870` |
| `@SniperAmmo` | `superbwarfare:sniper_ammo` | `awm`、`hunting_rifle`、`k_98`、`m_98b`、`mosin_nagant`、`sentinel`、`svd` |
| `@HeavyAmmo` | `superbwarfare:heavy_ammo` | `m_2_hb`、`ntw_20` |
| `FE`（能量） | —（`EnergyStorageItem`） | `ql_1031`、`repair_tool` |
| 指定物品 | 见右列 | `bocek`（`minecraft:arrow`）、`m_79`（`superbwarfare:grenade_40mm`）、`javelin`（`superbwarfare:javelin_missile`）、`igla_9k38`（`superbwarfare:medium_anti_air_missile`）、`taser`（`superbwarfare:taser_electrode`） |
| 无 `AmmoType` 字段 | — | `aurelia_sceptre`、`rpg`、`secondary_cataclysm`（发射器/载具类，另有 `Projectile` 配置） |

## 对 M3 的直接用途

- **5 种模具**的产物（`mold_detacher` 输出）与上表五个 `*_ammo` 一一对应。
- **枪械白名单**（原 `tacz:ai_awp` 之类的配置项）改用 SW 枪械物品 id，例如 `superbwarfare:ak_47`。
- **战利品表**里原来的 `tacz:modern_kinetic_gun` + `GunId` 改为直接输出具体枪物品（上表任一 id）。
- 注意 `trachelium` 的 `GunType` 是 Handgun 但弹药是 `@RifleAmmo`、`ntw_20` 的 `GunType` 是 Sniper 但弹药是 `@HeavyAmmo`——**分类判定请以 `AmmoType` 为准**，不要用 `GunType` 推断。

## 相关事实（来自两份调研报告）

- 弹药数量：弹药物品上的 data component `superbwarfare:ammo_<name>`（int）；玩家弹药总量在 data attachment `PlayerVariable.ammo`。
- 弹药配方为原版 `minecraft:crafting_shaped`，例如 `superbwarfare:handgun_ammo` count 64（`recipe/handgun_ammo.json`）。
- 枪械数据字段：`"ID"`、`"AmmoType"`、`"GunType"`、`"SoundInfo"` 等；无 `GunId` NBT，枪械身份即物品注册名。
