# M4 迁移：tacz → Superb Warfare 映射表（待主人审阅）

> 背景：M4 原有的枪械/弹药引用来自 TaC（1.18.2），1.21.1 整合包已改用 **Superb Warfare（SBW）**。
> SBW 只有 **5 类弹药**（`handgun/rifle/shotgun/sniper/heavy`）与固定物品 `superbwarfare:<cat>_ammo`，没有"口径"与 `AmmoId` NBT。
> 本表由开发按"近似语义"给出（决策 D-C），请主人校对；确认后按此改写数据文件。

## 1. 枪械映射

| 原 TaC id | SBW 目标 id | 说明 |
| --- | --- | --- |
| `tacz:m4a1` | `superbwarfare:m_4` | SBW 的 M4 同族卡宾枪，语义最接近 |

> 说明：SBW 枪械状态不是 TaC 的 `GunId`/`HasBulletInBarrel`/`GunFireMode`/`GunCurrentAmmoCount` NBT，**无法等价迁移**。
> 迁移策略：战利品直接给"全新未装填的 SBW 枪械物品"，不携带任何 NBT；玩家拾取后按 SBW 逻辑装弹。

## 2. 弹药映射（16 种 TaC 口径 → 5 类 SBW 弹药）

| 原 TaC 口径 | SBW 目标 | 依据 |
| --- | --- | --- |
| `tacz:9mm` | `superbwarfare:handgun_ammo` | 手枪口径 |
| `tacz:45acp` | `superbwarfare:handgun_ammo` | 手枪口径 |
| `tacz:357mag` | `superbwarfare:handgun_ammo` | 转轮手枪口径 |
| `tacz:50ae` | `superbwarfare:handgun_ammo` | 大口径手枪 |
| `tacz:46x30` | `superbwarfare:handgun_ammo` | PDW 弹 |
| `tacz:57x28` | `superbwarfare:handgun_ammo` | PDW 弹 |
| `tacz:762x25` | `superbwarfare:handgun_ammo` | 手枪弹 |
| `tacz:556x45` | `superbwarfare:rifle_ammo` | 步枪弹 |
| `tacz:762x39` | `superbwarfare:rifle_ammo` | 步枪弹 |
| `tacz:762x54` | `superbwarfare:rifle_ammo` | 步枪弹 |
| `tacz:308` | `superbwarfare:rifle_ammo` | 全威力步枪弹 |
| `tacz:30_06` | `superbwarfare:rifle_ammo` | 全威力步枪弹 |
| `tacz:58x42` | `superbwarfare:rifle_ammo` | 步枪弹 |
| `tacz:338` | `superbwarfare:sniper_ammo` | 狙击弹 |
| `tacz:68x51fury` | `superbwarfare:sniper_ammo` | 大口径高初速弹 |
| `tacz:50bmg` | `superbwarfare:heavy_ammo` | 重机枪/反器材弹 |
| `tacz:12g` | `superbwarfare:shotgun_ammo` | 霰弹 |

## 3. 需要改写的战利品文件（全部位于 `data/misc_twf/loot_tables/blocks/`）

| 文件 | 原内容 | 改写方案 |
| --- | --- | --- |
| `ammunition_box.json` | 16 条 `tacz:ammo` + `AmmoId` NBT，`set_count` 15~40 | 改为 5 条 SBW 弹药（`handgun/rifle/shotgun/sniper/heavy`），保留 15~40 随机数量；去重（同类的多个口径合并） |
| `stacked_ammunition_box.json` | 同上 | 同上（数量沿用原表） |
| `magazine_556.json` | `tacz:ammo` + `556x45` | 改为 `superbwarfare:rifle_ammo`，保留原 `set_count` |
| `m4a1_carbine.json` | `tacz:modern_kinetic_gun` + `GunId:"tacz:m4a1"` + 枪械 NBT | 改为 `superbwarfare:m_4`，**移除 NBT** |
| `m4a1_carbine_alternative.json` | `tacz:ammo` + `556x45` | 改为 `superbwarfare:rifle_ammo` |
| `body_headless_soldier.json` | `tacz:ammo` + `556x45` | 改为 `superbwarfare:rifle_ammo` |
| `body_nailed_soldier.json` | 同上 | 同上 |
| `body_sit_soldier.json` | 同上 | 同上 |

## 4. 待主人确认

1. 上表枪械/弹药映射是否符合整合包意图（尤其 `50bmg → heavy`、`338/68x51fury → sniper`、`12g → shotgun`）。
2. 弹药箱是否接受"合并为 5 条 SBW 弹药"的做法（而非保留 16 条导致同物品重复条目）。
3. 装饰方块 `m4a1_carbine` 掉落实枪是否符合预期（原设计确实是掉落一把枪）；若希望只掉零件/弹药，请指出。
