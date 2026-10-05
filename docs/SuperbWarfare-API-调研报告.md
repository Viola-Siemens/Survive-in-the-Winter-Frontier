# Superb Warfare (NeoForge 1.21.1) API 调研报告

> 调研对象：`C:\Projects\Sources-1.21.1\com\atsuishio\superbwarfare`（Superb Warfare 反编译源码树）
> 迁移参照：TaC（Timeless and Classics Guns）1.18.2 API
> 本文所有结论均来自本地源码逐文件阅读，**未使用任何外部猜测**。凡源码中找不到的能力，均明确标注「未找到」。

---

## 0. 调研范围与前置说明

### 0.1 源码树的实际内容边界（重要）

该目录是**类文件反编译（decompile）产物**，不是 Gradle 工程：

| 内容 | 是否存在 | 说明 |
|---|---|---|
| `com/atsuishio/superbwarfare/**/*.java` | 存在（981 个 java 文件） | 反编译后的 Java 源码 |
| `net/**`、`snownee/**` | 存在 | 第三方库反编译产物 |
| `data/superbwarfare/**`、`assets/superbwarfare/**` | **未找到** | 目录只有 `data/{c,minecraft,neoforge}` 与 `assets/{c,minecraft,neoforge}`，即**原版与 NeoForge 资源** |
| `META-INF/neoforge.mods.toml` | **未找到** | 无法取得 version / 依赖声明 |
| `sbw/guns/*.json`（枪械数据 JSON） | **未找到** | 需从 `superbwarfare-x.y.z.jar` 内提取 |

**因此本报告无法给出真实的 recipe JSON / gun data JSON 原文**。作为替代，报告通过**数据生成器（datagen）源码**反推出 JSON 的准确结构与字段名 —— 这是可靠且可执行的等价物，且 datagen 本身就是生成这些 JSON 的代码。相关位置已逐条标注。

### 0.2 关键定位：所有 gun/vehicle 数据 JSON 的真实路径

`com/atsuishio/superbwarfare/data/CustomData.java:17-24`：

```java
public static final DataLoader.DataMap<ProjectileInfo> LAUNCHABLE_ENTITY = DataLoader.createData("sbw/launchable", ProjectileInfo.class);
public static final DataLoader.DataMap<DefaultVehicleData> VEHICLE_DATA = DataLoader.createData("sbw/vehicles", DefaultVehicleData.class, map -> VehicleData.dataCache.invalidateAll());
public static final DataLoader.DataMap<DefaultGunData> GUN_DATA = DataLoader.createData("sbw/guns", DefaultGunData.class, map -> GunData.DATA_CACHE.invalidateAll());
public static final DataLoader.DataMap<DroneAttachmentData> DRONE_ATTACHMENT = DataLoader.createData("sbw/drone_attachments", DroneAttachmentData.class);
public static final DataLoader.DataMap<DefaultMobGunData> MOB_GUNS = DataLoader.createData("sbw/mob_guns", DefaultMobGunData.class, map -> MobGunData.dataCache.invalidateAll());
public static final DataLoader.DataMap<DefaultGunResource> GUN_RESOURCE = DataLoader.createResource("sbw/guns", DefaultGunResource.class, map -> GunResource.RESOURCE_CACHE.invalidateAll());
```

加载机制见 `data/ComplexJsonResourceReloadListener.java:31-47`：使用 `FileToIdConverter.json(name)`。

**结论**：
- 枪械**数值定义** JSON：`data/superbwarfare/sbw/guns/<gun_id>.json`（服务端数据包）
- 枪械**客户端资源** JSON：`assets/superbwarfare/sbw/guns/<gun_id>.json`（模型/动画/贴图）
- 载具：`data|assets/superbwarfare/sbw/vehicles/`
- 数据 ID 由 JSON 内的 `"ID"` 字段决定，为空时回退为「命名空间:文件路径」：

```java
// data/ComplexJsonResourceReloadListener.java:40-45
if (data instanceof IDBasedData<?> IDData && !IDData.getId().isEmpty()) {
    id = IDData.getId();
} else {
    id = pathLocation.toString();
    Mod.LOGGER.warn("{} ID for {} is empty, try using {} as id", name, id, pathLocation);
}
```

---

## 1. 模组标识与命名空间

| 项目 | 值 | 源码位置 |
|---|---|---|
| modid | `superbwarfare` | `Mod.java:44` `public static final String MODID = "superbwarfare";` |
| 主类注解 | `@net.neoforged.fml.common.Mod(Mod.MODID)` | `Mod.java:41` |
| 资源命名空间 | `superbwarfare` | 全部 `DeferredRegister.create(..., Mod.MODID)` |
| 便捷方法 | `Mod.loc(String path)` → `ResourceLocation.fromNamespaceAndPath(MODID, path)` | `Mod.java:96` |

**注册命名空间**（`init/ModItems.java:55,107,155,305,335,367`）：所有物品、方块、载具、Perk 均注册在 `superbwarfare` 命名空间下：

```java
public static final DeferredRegister<Item> GUNS = DeferredRegister.create(BuiltInRegistries.ITEM, Mod.MODID);   // :55
public static final DeferredRegister<Item> AMMO = DeferredRegister.create(BuiltInRegistries.ITEM, Mod.MODID);   // :107
public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, Mod.MODID);         // :155
public static final DeferredRegister<Item> BLOCKS = DeferredRegister.create(BuiltInRegistries.ITEM, Mod.MODID); // :305
public static final DeferredRegister<Item> VEHICLES = DeferredRegister.create(BuiltInRegistries.ITEM, Mod.MODID); // :335
public static final DeferredRegister<Item> PERKS = DeferredRegister.create(BuiltInRegistries.ITEM, Mod.MODID);  // :367
```

数据组件注册：`component/ModDataComponents.java:21-22`

```java
public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES =
        DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, Mod.MODID);
```

> **结论**：命名空间与 modid 完全一致，均为 `superbwarfare`。TaC 的 `tacz` 命名空间需要整体替换。

---

## 2. 弹药（Ammo）系统

### 2.1 与 TaC 的根本性差异（迁移第一风险点）

TaC 的弹药模型是 **「一个口径 = 一个 AmmoId = 一个独立注册的子弹物品」**（`tacz:9mm`、`tacz:556`、`tacz:762`…，由 `AmmoItemBuilder` 按 `AmmoId` 构造）。

Superb Warfare 的弹药模型是 **「一个弹药类别 = 一个枚举常量 = 一个固定物品」**，共 **5 种**，**不存在口径 id 概念**：

```java
// data/gun/Ammo.java:17-22
public enum Ammo {
    HANDGUN(ChatFormatting.GREEN, ModItems.HANDGUN_AMMO::get),
    RIFLE(ChatFormatting.AQUA, ModItems.RIFLE_AMMO::get),
    SHOTGUN(ChatFormatting.RED, ModItems.SHOTGUN_AMMO::get),
    SNIPER(ChatFormatting.GOLD, ModItems.SNIPER_AMMO::get),
    HEAVY(ChatFormatting.LIGHT_PURPLE, ModItems.HEAVY_AMMO::get);
    ...
}
```

- **`AmmoId` 类**：**未找到**
- **`AmmoItemBuilder` 等价物**：**未找到**（不需要，因为弹药物品是固定 5 个）
- **口径字符串（`9mm`/`556`/`762`）**：**未找到**。全源码不存在任何形如 `superbwarfare:9mm` 的口径 id。

### 2.2 弹药物品类与注册处

**注册处**：`init/ModItems.java:107-150`，`DeferredRegister<Item> AMMO`。

**类别化弹药物品**（真正的「子弹」，`item/common/ammo/AmmoSupplierItem.java`）：

| 枚举 | 物品注册名 | 完整 id | 源码行 |
|---|---|---|---|
| `Ammo.HANDGUN` | `handgun_ammo` | `superbwarfare:handgun_ammo` | `ModItems.java:109` |
| `Ammo.RIFLE` | `rifle_ammo` | `superbwarfare:rifle_ammo` | `ModItems.java:110` |
| `Ammo.SNIPER` | `sniper_ammo` | `superbwarfare:sniper_ammo` | `ModItems.java:111` |
| `Ammo.SHOTGUN` | `shotgun_ammo` | `superbwarfare:shotgun_ammo` | `ModItems.java:112` |
| `Ammo.HEAVY` | `heavy_ammo` | `superbwarfare:heavy_ammo` | `ModItems.java:113` |

```java
// init/ModItems.java:109-113
public static final DeferredHolder<Item, AmmoSupplierItem> HANDGUN_AMMO = AMMO.register("handgun_ammo", () -> new AmmoSupplierItem(Ammo.HANDGUN, 1, new Item.Properties()));
public static final DeferredHolder<Item, AmmoSupplierItem> RIFLE_AMMO   = AMMO.register("rifle_ammo",   () -> new AmmoSupplierItem(Ammo.RIFLE, 1, new Item.Properties()));
public static final DeferredHolder<Item, AmmoSupplierItem> SNIPER_AMMO  = AMMO.register("sniper_ammo",  () -> new AmmoSupplierItem(Ammo.SNIPER, 1, new Item.Properties()));
public static final DeferredHolder<Item, AmmoSupplierItem> SHOTGUN_AMMO = AMMO.register("shotgun_ammo", () -> new AmmoSupplierItem(Ammo.SHOTGUN, 1, new Item.Properties()));
public static final DeferredHolder<Item, AmmoSupplierItem> HEAVY_AMMO   = AMMO.register("heavy_ammo",   () -> new AmmoSupplierItem(Ammo.HEAVY, 1, new Item.Properties()));
```

**`AmmoSupplierItem` 全文（关键）**：`item/common/ammo/AmmoSupplierItem.java`

```java
public class AmmoSupplierItem extends Item {
    public final Ammo type;      // 该物品对应的弹药类别
    public final int ammoToAdd;  // 单次补给量

    public AmmoSupplierItem(Ammo type, int ammoToAdd, Properties properties) { ... }
}
```

其它同类注册在 `AMMO` 下的「弹药类物品」（非同口径子弹，而是发射物实体物品 / 特殊弹药盒），同样位于 `superbwarfare` 命名空间：

`handgun_ammo_box`, `rifle_ammo_box`, `sniper_ammo_box`, `shotgun_ammo_box`, `creative_ammo_box`, `ammo_box`, `taser_electrode`, `grenade_40mm`, `mortar_shell`, `potion_mortar_shell`, `rpg_rocket_standard`, `rpg_rocket_tbg`, `lunge_mine`, `he_5_inches`, `ap_5_inches`, `cm_5_inches`, `gs_5_inches`, `hand_grenade`, `rgo_grenade`, `m18_smoke_grenade`, `claymore_mine`, `tm_62`, `ptkm_1r`, `c4_bomb`, `blu_43_mine`, `small_shell`, `small_rocket`, `medium_rocket_ap`, `medium_rocket_he`, `medium_rocket_cm`, `javelin_missile`, `medium_anti_air_missile`, `medium_anti_ground_missile`, `large_anti_ground_missile`, `swarm_drone`, `medium_aerial_bomb`（`ModItems.java:114-150`）。

### 2.3 如何构造一个指定「口径」的弹药 ItemStack

由于不存在口径 id，等价操作是**按 `Ammo` 枚举构造**。`data/gun/Ammo.java:76-82` 提供了现成工厂方法：

```java
// data/gun/Ammo.java:76-82
public ItemStack getItemStack() {
    return getItemStack(1);
}

public ItemStack getItemStack(int count) {
    return new ItemStack(defaultItemSupplier.get(), count);
}
```

**可直接调用的写法（推荐）：**

```java
import com.atsuishio.superbwarfare.data.gun.Ammo;

// 1) 指定类别 + 数量，一步构造
ItemStack stack = Ammo.RIFLE.getItemStack(64);
ItemStack stack2 = Ammo.HANDGUN.getItemStack(32);

// 2) 只拿物品，自行构造 ItemStack
Item item = Ammo.SNIPER.defaultItemSupplier.get();   // public final Supplier<Item> defaultItemSupplier
ItemStack s3 = new ItemStack(item, 16);

// 3) 由字符串名反查（serializationName 为大驼峰，如 "RifleAmmo"）
Ammo type = Ammo.getType("RifleAmmo");   // data/gun/Ammo.java:84-91；未匹配返回 null
if (type != null) type.getItemStack(1);
```

> **注意**：`Ammo.getType(String)` 只接受 `serializationName`（`HandgunAmmo` / `RifleAmmo` / `ShotgunAmmo` / `SniperAmmo` / `HeavyAmmo`），**不接受** `rifle`、`9mm` 这类小写或口径字符串。

### 2.4 弹药「口径 / 数量」的读写方式

Superb Warfare 中「弹药」的**数量**才是被持久化的数据，且分两个载体：

#### （a）ItemStack 上的弹药数量 —— **data component（int）**

`component/ModDataComponents.java:62-68` 为**每个 `Ammo` 枚举动态注册一个 int 类型 data component**：

```java
// component/ModDataComponents.java:62-68
public static void register(IEventBus eventBus) {
    for (var type : Ammo.values()) {
        type.dataComponent = register("ammo_" + type.name, builder -> builder.persistent(Codec.INT));
    }
    DATA_COMPONENT_TYPES.register(eventBus);
}
```

`Ammo.name` 是小写下划线格式（`Ammo.java:54-56` `this.name = name().toLowerCase(Locale.ROOT);`），因此**实际注册的 data component 名称为**：

| Ammo | data component id | 值类型 |
|---|---|---|
| `HANDGUN` | `superbwarfare:ammo_handgun` | int |
| `RIFLE` | `superbwarfare:ammo_rifle` | int |
| `SHOTGUN` | `superbwarfare:ammo_shotgun` | int |
| `SNIPER` | `superbwarfare:ammo_sniper` | int |
| `HEAVY` | `superbwarfare:ammo_heavy` | int |

**读写 API**（`data/gun/Ammo.java:93-105`）：

```java
// data/gun/Ammo.java:93-105
// ItemStack
public int get(ItemStack stack) {
    var count = stack.get(this.dataComponent);
    return count == null ? 0 : count;
}

public void set(ItemStack stack, int count) {
    stack.set(this.dataComponent, count);
}

public void add(ItemStack stack, int count) {
    set(stack, safeAdd(get(stack), count));
}
```

**NBTTag 读写**（`data/gun/Ammo.java:107-119`），键名就是 `serializationName`（大驼峰）：

```java
// data/gun/Ammo.java:107-119
public int get(CompoundTag tag) {
    return tag.getInt(this.serializationName);   // 键名 = "RifleAmmo" / "HandgunAmmo" ...
}
public void set(CompoundTag tag, int count) {
    if (count < 0) count = 0;
    tag.putInt(this.serializationName, count);
}
```

#### （b）玩家身上的弹药总量 —— **NeoForge data attachment**

`capability/player/PlayerVariable.java:28`：`public Map<Ammo, Integer> ammo = new EnumMap<>(Ammo.class);`
挂载点：`init/ModAttachments.java` 的 `PLAYER_VARIABLE`。

**按玩家读写**（`data/gun/Ammo.java:121-153`）：

```java
public int get(PlayerVariable variable);
public void set(PlayerVariable variable, int count);
public void add(PlayerVariable variable, int count);

public int get(Entity entity);
public void set(Entity entity, int count);   // 内部自动 sync
public void add(Entity entity, int count);
```

标准用法（见 `AmmoSupplierItem.java:52-56`）：

```java
var capability = player.getData(ModAttachments.PLAYER_VARIABLE).watch();
this.type.add(capability, ammoToAdd * count);
player.setData(ModAttachments.PLAYER_VARIABLE, capability);
capability.sync(player);
```

#### （c）枪械内部的弹药数量 —— **NBT（CUSTOM_DATA / GunData 子 tag）**

**未找到 data component 化**。枪内弹药存在物品的 `DataComponents.CUSTOM_DATA` 的 `GunData` 子 tag 中，键为 `"Ammo"`：

```java
// data/gun/GunData.java:78-101（节选）
var customData = stack.get(DataComponents.CUSTOM_DATA);
this.tag = customData != null ? customData.copyTag() : new CompoundTag();

gunDataTag = getOrPut("GunData");
...
selectedAmmoType = new IntValue(gunDataTag, "SelectedAmmoType");
ammo            = new IntValue(gunDataTag, "Ammo");
virtualAmmo     = new IntValue(gunDataTag, "VirtualAmmo");
backupAmmoCount = new IntValue(gunDataTag, "BackupAmmoCount");
ammoSlot        = new AmmoSlot(gunDataTag);
```

枪械 NBT 的完整层级：`CUSTOM_DATA → { "GunData": {...}, "Perks": {...}, "Attachments": {...} }`，写回见 `GunData.save()`（`GunData.java:891-921`）。

### 2.5 内置「口径」id 全清单

**不存在口径 id**。内置的弹药类别枚举**只有 5 个**，其全部规范化字符串如下（`data/gun/Ammo.java:24-74` 的构造逻辑）：

| 枚举常量 | `name`（小写下划线） | `serializationName`（大驼峰） | `displayName` | `translationKey` | 默认物品 |
|---|---|---|---|---|---|
| `HANDGUN` | `handgun` | `HandgunAmmo` | `Handgun Ammo` | `item.superbwarfare.ammo.handgun` | `superbwarfare:handgun_ammo` |
| `RIFLE` | `rifle` | `RifleAmmo` | `Rifle Ammo` | `item.superbwarfare.ammo.rifle` | `superbwarfare:rifle_ammo` |
| `SHOTGUN` | `shotgun` | `ShotgunAmmo` | `Shotgun Ammo` | `item.superbwarfare.ammo.shotgun` | `superbwarfare:shotgun_ammo` |
| `SNIPER` | `sniper` | `SniperAmmo` | `Sniper Ammo` | `item.superbwarfare.ammo.sniper` | `superbwarfare:sniper_ammo` |
| `HEAVY` | `heavy` | `HeavyAmmo` | `Heavy Ammo` | `item.superbwarfare.ammo.heavy` | `superbwarfare:heavy_ammo` |

> **迁移含义**：TaC 中「9mm 弹药只能给手枪用」的粒度在 SBW 中退化为「HANDGUN 弹药给所有手枪用」。若你的模组依赖细口径区分（如 `9mm` / `45acp` / `57`），**这些信息在 SBW 中无处存放，必须由你的模组自行维护**（例如自定义物品 + 自定义 data component）。

### 2.6 枪械数据 JSON 中如何指定弹药（**这是 TaC 口径的实际替代品**）

枪械 JSON 中弹药不是「口径」，而是**一个可解析的弹药消费描述字符串**。字段名在 datagen/序列化类中为 `"Ammo"`：

```java
// data/gun/AmmoConsumer.java:30-51
public class AmmoConsumer implements DeserializeFromString, GunPropertyModifier {
    @SerializedName("Ammo")
    public String ammo;

    @SerializedName("AmmoSlot")
    public String ammoSlot = "Default";

    @SerializedName("Projectile")
    public StringToObject<ProjectileInfo> projectile = null;

    @SerializedName("Override")
    public JsonObject override = null;

    @SerializedName("Icon")
    public String icon = Mod.loc("textures/overlay/vehicle/weapon/icons/empty.png").toString();

    @SerializedName("ShouldUnload")
    public boolean shouldUnload = true;
}
```

**解析语法**（`AmmoConsumer.java:289` 正则）：

```java
private static final Pattern AMMO_PATTERN =
    Pattern.compile("^(?<count>(\\d+)?)\\s*(?<prefix>[@#]?)(?<id>\\w+(:\\w+)?)\\s*(?<data>(\\{.*})?)$");
```

解析逻辑（`AmmoConsumer.java:307-374`）：

| 写法 | 语义 | `AmmoConsumeType` |
|---|---|---|
| `infinity` / `infinite` | 无限弹药 | `INFINITE` |
| `empty` | 不消耗弹药 | `EMPTY` |
| `fe` / `rf` / `energy` | 消耗 FE 能量 | `ENERGY` |
| `@RifleAmmo` | **消耗玩家身上的该类弹药** | `PLAYER_AMMO` |
| `@HandgunAmmo` | 同上（手枪） | `PLAYER_AMMO` |
| `@ShotgunAmmo` / `@SniperAmmo` / `@HeavyAmmo` | 同上 | `PLAYER_AMMO` |
| `superbwarfare:some_item` | 消耗指定物品（普通物品 id） | `ITEM` |
| `superbwarfare:some_item{SomeTag:1}` | 消耗带 SNBT 数据的指定物品 | `ITEM` |
| `1 @RifleAmmo` | 前缀数字 = `loadAmount`（一发弹药物品折算几发子弹） | 组合 |

```java
// data/gun/AmmoConsumer.java:324-343（节选）
if (prefix.isBlank()) {
    this.type = switch (id.toLowerCase(Locale.ROOT)) {
        case "infinity", "infinite" -> AmmoConsumeType.INFINITE;
        case "empty" -> AmmoConsumeType.EMPTY;
        case "fe", "rf", "energy" -> AmmoConsumeType.ENERGY;
        default -> AmmoConsumeType.INVALID;
    };
    if (this.type != AmmoConsumeType.INVALID) return;
}

// Player Ammo
if ("@".equals(prefix)) {
    this.playerAmmoType = Ammo.getType(id);
    if (this.playerAmmoType == null) {
        Mod.LOGGER.warn("invalid player ammo type: {}", id);
        return;
    }
    this.type = AmmoConsumeType.PLAYER_AMMO;
    this.stack = this.playerAmmoType.getItemStack();
} else { /* ITEM 分支：ResourceLocation.tryParse(id) → BuiltInRegistries.ITEM */ }
```

字段在 `DefaultGunData` 中的声明（`data/gun/DefaultGunData.java:148-149`）：

```java
@SerializedName("AmmoType")
public ObjectToList<StringToObject<AmmoConsumer>> ammoConsumers = new ObjectToList<>();
```

**因此枪械 JSON 中指定弹药的片段形如**（结构由上述 `@SerializedName` 与 `ObjectToList` 反推，**这些 JSON 文件本身在源码树中未找到**）：

```json
{
  "ID": "superbwarfare:ak_47",
  "AmmoType": [
    {
      "Ammo": "@RifleAmmo",
      "AmmoSlot": "Default",
      "ShouldUnload": true
    }
  ]
}
```

`ObjectToList`（`data/ObjectToList.java`）允许单对象写法（会自动包装为列表），也允许纯字符串写法（走 `DeserializeFromString`），即 `"AmmoType": "@RifleAmmo"` 亦可。

**运行时读取当前生效的弹药消费配置**（`GunData.java:316-325`）：

```java
public AmmoConsumer selectedAmmoConsumer(List<AmmoConsumer> consumers);
public AmmoConsumer selectedAmmoConsumer();
```

### 2.7 弹药相关配方 JSON

**未找到任何「按口径输出弹药」的配方 JSON**，也不存在对应 recipe type —— 因为弹药物品只有 5 个固定物品，用**原版有序合成**即可。生成代码 `datagen/ModRecipeProvider.java:453-462, 472, 603, 613, 623`：

```java
// datagen/ModRecipeProvider.java:453-462（生成 superbwarfare:handgun_ammo，产出 64）
ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.HANDGUN_AMMO.get(), 64)
        .pattern(" a ")
        .pattern("bcb")
        .pattern(" d ")
        .define('a', Tags.Items.INGOTS_COPPER)
        .define('b', PLATES_COPPER)
        .define('c', Items.GUNPOWDER)
        .define('d', ModItems.PRIMER.get())
        .unlockedBy(getHasName(ModItems.PRIMER.get()), has(ModItems.PRIMER.get()))
        .save(writer, Mod.loc(getItemName(ModItems.HANDGUN_AMMO.get())));
```

对应产出量：`handgun_ammo` 64 / `rifle_ammo` 48 / `shotgun_ammo` 24 / `sniper_ammo` 16 / `heavy_ammo` 12（`ModRecipeProvider.java:453,603,613,623,472`）。生成的 JSON 就是标准原版 `minecraft:crafting_shaped`：

```json
{
  "type": "minecraft:crafting_shaped",
  "category": "equipment",
  "pattern": [" a ", "bcb", " d "],
  "key": {
    "a": { "tag": "c:ingots/copper" },
    "b": { "item": "superbwarfare:copper_plate" },
    "c": { "item": "minecraft:gunpowder" },
    "d": { "item": "superbwarfare:primer" }
  },
  "result": { "id": "superbwarfare:handgun_ammo", "count": 64 }
}
```

> **若你的模组想「按口径合成特定弹药」**：SBW 没有这种机制，你只能注册自己的物品 + 自己的 `RecipeSerializer`，或使用原版 `minecraft:crafting_shaped` / `minecraft:stonecutting` 输出 `superbwarfare:rifle_ammo` 等固定物品。

---

## 3. 枪械物品（Gun Item）

### 3.1 类继承结构

**存在等价于 `AbstractGunItem` 的基类**，且层次更清晰（3 层）：

```
net.minecraft.world.item.Item
  └─ GunItem                                         item/gun/GunItem.java
       │  implements ItemScreenProvider, GunPropertyModifier, EnergyStorageItem
       │  abstract class
       └─ GunGeoItem                                 item/gun/GunGeoItem.java
            │  implements software.bernie.geckolib.animatable.GeoItem, CustomRendererItem
            │  abstract class
            └─ 具体枪械（AK47Item, M4Item, Glock17Item, ...）
                 item/gun/{rifle,handgun,smg,sniper,shotgun,machinegun,launcher,special,vehicle}/*.java
```

```java
// item/gun/GunItem.java:81
public abstract class GunItem extends Item implements ItemScreenProvider, GunPropertyModifier, EnergyStorageItem {

// item/gun/GunGeoItem.java:35
public abstract class GunGeoItem extends GunItem implements GeoItem, CustomRendererItem {
```

TaC `com.tacz.guns.api.item.gun.AbstractGunItem` → **SBW 对应 `com.atsuishio.superbwarfare.item.gun.GunItem`**（`GunGeoItem` 是带 GeckoLib 模型渲染的实现层）。

**`GunItem` 中面向扩展者的关键可覆写方法**（`item/gun/GunItem.java`）：

| 方法 | 行号 | 用途 |
|---|---|---|
| `void computeProperties(GunData, DefaultGunData)` | :113 | 覆写数值（伤害/弹匣/RPM…） |
| `boolean canShoot(GunData, @Nullable Entity)` | :489 | 能否开火 |
| `void beforeShoot(@NotNull ShootParameters)` | :513 | 开火前钩子（内部广播 `ShootEvent.Pre`） |
| `void afterShoot(@NotNull ShootParameters)` | :544 | 开火后钩子（内部广播 `ShootEvent.Post`） |
| `void shoot(@NotNull ShootParameters)` | :629 | **服务端单次开火总入口** |
| `boolean shootBullet(@NotNull ShootParameters)` | :759 | 生成单发弹丸实体 |
| `boolean shootRay(@NotNull ShootParameters)` | :976 | 射线类武器 |
| `void playFireSounds(GunData, @Nullable Entity, boolean zoom)` | :696 | **开枪音效** |
| `void onFireKeyPress(GunData, Player, boolean)` | :729 | 按下扳机 |
| `void onFireKeyRelease(GunData, Player, double, boolean)` | :742 | 松开扳机 |
| `void onChangeSlot(GunData, Entity)` | :1147 | 切枪 |
| `void whenNoAmmo(GunData)` | :507 | 无弹回调 |
| `void addReloadTimeBehavior(Map<Integer, Consumer<GunData>>)` | :477 | 指定换弹时刻行为 |
| `void addBoltTimeBehavior(Map<Integer, Consumer<GunData>>)` | :483 | 指定拉栓时刻行为 |
| `boolean useSpecialFireProcedure(GunData)` | :499 | 特殊开火流程 |
| `boolean hasBulletInBarrel/hasCustomBarrel/hasCustomGrip/hasCustomMagazine/hasCustomScope/hasCustomStock/hasBipod(GunData)` | :291-355 | 配件槽位开关 |

### 3.2 枪械 id / GunId 的存储方式

**不存在 `GunId` 类，也不存在存储 GunId 的 data component 或 NBT 键。**

TaC 的 `GunId`（写在 NBT 的 `GunId` 键）在 SBW 中被**物品注册名取代**。枪械 id 由**推导**得到：

```java
// data/gun/GunData.java:202-206
public static String getRegistryId(Item item) {
    var id = item.getDescriptionId();                  // 如 "item.superbwarfare.ak_47"
    id = id.substring(id.indexOf(".") + 1).replace('.', ':');
    return id;                                         // → "superbwarfare:ak_47"
}
```

同一个 `GunData` 实例上暴露为字段（`GunData.java:62`）：

```java
public final String id;   // 构造时 this.id = getRegistryId(stack.getItem());  // GunData.java:74
```

**取值方式**：

```java
String gunId = GunData.from(stack).id;                        // "superbwarfare:ak_47"
String gunId2 = GunData.getRegistryId(stack.getItem());       // 静态写法
DefaultGunData def = GunData.getDefault(gunId);               // 按 id 取 JSON 定义
```

枪械物品上**真正持久化在 NBT 的东西**是：`UUID`（GeckoLib 实例 id + 枪械实例标识）、`GunData`（`Ammo`/`SelectedAmmoType`/`FireIndex`/`Heat`… 见 2.4(c)）、`Perks`、`Attachments`、`Override`。

```java
// item/gun/GunItem.java:170-178
public void init(GunData data) {
    if (isInitialized(data)) return;
    data.gunDataTag.putUUID("UUID", UUID.randomUUID());
}
public boolean isInitialized(GunData data) {
    return data.gunDataTag.hasUUID("UUID");
}
```

> **迁移含义**：TaC 用 `IGun.getGunId(stack)` 得到一个 `GunId` 对象来索引枪械数据；SBW 改用 `GunData.from(stack).id` 这个字符串，数据表是 `com.atsuishio.superbwarfare.data.CustomData.GUN_DATA`（`DataMap<DefaultGunData>`）。**任何「按 GunId 存/查」的自定义逻辑，都要改为按物品注册名字符串。**

### 3.3 射击逻辑脚本 API

**TaC `ModernKineticGunScriptAPI` 等价物：未找到。**

不存在「为单个枪械脚本提供的、可读可写临时状态」的专用 API 类。取而代之的是**两大入口**：

#### （1）服务端权威开火入口 —— `GunData` + `ShootParameters`

```java
// data/gun/ShootParameters.java:25-36
public record ShootParameters(
        @Nullable Entity ammoSupplier,
        @Nullable Entity shooter,
        @NotNull ServerLevel level,
        @NotNull Vec3 shootPosition,
        @NotNull Vec3 shootDirection,
        @NotNull GunData data,
        double spread,
        boolean zoom,
        @Nullable UUID targetEntityUUID,
        @Nullable Vec3 targetPos
) {}
```

`GunData` 上的便捷开火方法（`data/gun/GunData.java:602-622`）：

```java
public void shoot(@NotNull ServerLevel level, @NotNull Vec3 shootPosition, @NotNull Vec3 shootDirection, double spread, boolean zoom);
public void shoot(@NotNull Entity entity, double spread, boolean zoom, @Nullable UUID uuid);
public void shoot(@NotNull Entity entity, double spread, boolean zoom, @Nullable UUID uuid, @Nullable Vec3 targetPos);
public void shoot(@NotNull ShootParameters parameters);
```

`GunItem` 上的对应方法（`item/gun/GunItem.java:608-622, 629`）：

```java
public void shoot(@NotNull ServerLevel level, @NotNull Vec3 shootPosition, @NotNull Vec3 shootDirection, @NotNull GunData data, double spread, boolean zoom, @Nullable UUID uuid);
public void shoot(@NotNull GunData data, @NotNull Entity shooter, double spread, boolean zoom, UUID uuid);
public void shoot(@NotNull GunData data, @NotNull Entity shooter, double spread, boolean zoom, UUID uuid, Vec3 pos);
public void shoot(@NotNull ShootParameters parameters);   // 真正实现，:629
```

`GunData` 的完整状态机方法（迁移时最常用）：

```java
// data/gun/GunData.java
public static GunData from(ItemStack stack);              // :154  取缓存实例
public static GunData create(Item item);                  // :150
public DefaultGunData compute();                          // :227  计算最终属性
public boolean canShoot(@Nullable Entity shooter);        // :598
public boolean shouldStartReloading(@Nullable Entity);    // :413
public void startReload();                                // :430
public void startBolt();                                  // :437
public boolean reloading();                               // :863
public int currentAvailableAmmo(@Nullable Entity);        // :552
public boolean hasEnoughAmmoToShoot(@Nullable Entity);    // :559
public int countBackupAmmo(@Nullable Entity);             // :451
public void consumeBackupAmmo(@Nullable Entity, int);     // :480
public void reloadAmmo(@Nullable Entity);                 // :566
public void withdrawAmmo(@NotNull Entity);                // :642
public void tick(@Nullable Entity shooter, boolean inMainHand);  // :633
public void save();                                       // :891  写回 ItemStack
```

`Tick` 委托到 `event/GunEventHandler.gunTick(...)`（`GunEventHandler.java:220`）。

#### （2）事件总线 API（真正的「对外脚本 API」）

`api/event/ShootEvent.java`（**注意用 `@ApiStatus.AvailableSince("0.8.8")` 标注，属于正式 API**）：

```java
// api/event/ShootEvent.java:13-57
public class ShootEvent extends Event {
    public static class Pre  extends ShootEvent { public Pre(@NotNull ShootParameters parameters) {...} }
    public static class Post extends ShootEvent { public Post(@NotNull ShootParameters parameters) {...} }

    public @NotNull ShootParameters getShootParameters();
    public @Nullable Entity getShooter();
    public ServerLevel getLevel();
    public GunData getData();
    public double getSpread();
    public boolean isZoom();
}
```

广播点：`ShootEvent.Pre` 在 `GunItem.beforeShoot`（`GunItem.java:516`），`ShootEvent.Post` 在 `GunItem.afterShoot`（`GunItem.java:550`），通过 `NeoForge.EVENT_BUS.post(...)`。

其余 API 事件（`api/event/`）：

| 类 | 用途 | 关键方法 |
|---|---|---|
| `ShootEvent.Pre/Post` | 开火前/后 | `getShootParameters()`, `getData()`, `getShooter()`, `getSpread()`, `isZoom()` |
| `ReloadEvent.Pre/Post` | 换弹前/后 | `getEntity()`, `getStack()`；字段 `shooter`, `data`, `stack` |
| `ProjectileHitEvent.HitEntity/HitBlock` | 弹丸命中（可取消，`ICancellableEvent`） | `getOwner()`, `getProjectile()`, `getHitVec()`, `getTarget()`, `isHeadshot()`, `isLegShot()`, `getPos()`, `getState()`, `getFace()` |
| `PreKillEvent.SendKillMessage/Indicator` | 击杀播报/指示 | `getEntity()`, `getSource()`, `getTarget()` |
| `RenderPlayerArmEvent` | 客户端手臂渲染（可取消） | `getLocalPlayer()`, `getTransformType()`, `getStack()`, `getArm()`, `getBone()`, `getCurrentBuffer()`, `getRenderType()`, `getPackedLightIn()`, `isUseOldHandRender()` |
| `RegisterContainersEvent` | 注册容器类实体 | `add(DeferredHolder<EntityType<?>, EntityType<T>>)` |

### 3.4 开枪音效发送类（TaC `SoundManager.sendSoundToNearby` 等价物）

**存在等价物：`com.atsuishio.superbwarfare.tools.SoundTool`**（`tools/SoundTool.java`）。语义差异较大：TaC 的 `sendSoundToNearby` 是「对附近玩家广播一个音效」；SBW 拆成了**三个不同用途**的方法。

```java
// tools/SoundTool.java
public static void playLocalSound(Player player, SoundEvent sound);                                       // :21
public static void playLocalSound(Player player, SoundEvent sound, float volume, float pitch);            // :25
public static void playLocalSound(ServerPlayer player, SoundEvent sound);                                 // :31
public static void playLocalSound(ServerPlayer player, SoundEvent sound, float volume, float pitch);       // :35
public static void playLocalSound(ServerPlayer player, SoundEvent sound, SoundSource source, float volume, float pitch); // :39  ← 仅发给该玩家
public static void stopSound(ServerPlayer player, ResourceLocation sound);                                 // :44
public static void stopSound(ServerPlayer player, ResourceLocation sound, SoundSource source);             // :48
public static void playDistantSound(ServerLevel serverLevel, SoundEvent soundEvent, Vec3 pos, float radius, float pitch, Entity sender); // :52  ← 最接近 sendSoundToNearby
```

```java
// tools/SoundTool.java:52-59  「远距离音效」= 对半径内玩家发包广播
public static void playDistantSound(ServerLevel serverLevel, SoundEvent soundEvent, Vec3 pos, float radius, float pitch, Entity sender) {
    var players = serverLevel.getPlayers(p -> p.distanceToSqr(pos) < radius * radius * 256);
    for (var serverPlayer : players) {
        PacketDistributor.sendToPlayer(serverPlayer,
                new SoundClientMessage(soundEvent.getLocation(), pos.toVector3f(), radius, pitch, sender == null ? UUID.randomUUID() : sender.getUUID()));
    }
}
```

**枪械开火音效的官方实现**是 `GunItem.playFireSounds`（`GunItem.java:696-724`），走的是 `Entity#playSound` + 数据驱动的 `SoundInfo`（`data/gun/SoundInfo.java`，字段 `fire3P` / `fire3PFar` / `fire3PVeryFar` + 三个 `*Silent` 消音变体），并用带衰减倍率（×0.4 / ×0.7 / ×1.0）的三层音效模拟距离衰减：

```java
// item/gun/GunItem.java:706-723（节选）
float soundRadius = (float) data.compute().soundRadius;
var soundInfo = data.compute().soundInfo;
boolean isSilent = data.attachment.get(AttachmentType.BARREL) == 2;

SoundEvent sound3p = isSilent ? soundInfo.fire3PSilent : soundInfo.fire3P;
if (sound3p != null) shooter.playSound(sound3p, soundRadius * 0.4f, pitch);

SoundEvent soundFar = isSilent ? soundInfo.fire3PFarSilent : soundInfo.fire3PFar;
if (soundFar != null) shooter.playSound(soundFar, soundRadius * 0.7f, pitch);

SoundEvent soundVeryFar = isSilent ? soundInfo.fire3PVeryFarSilent : soundInfo.fire3PVeryFar;
if (soundVeryFar != null) shooter.playSound(soundVeryFar, soundRadius, pitch);
```

### 3.5 子弹实体类（TaC `EntityKineticBullet` 等价物）

**存在等价物：`com.atsuishio.superbwarfare.entity.projectile.ProjectileEntity`**

```
net.minecraft.world.entity.projectile.Projectile
  └─ ProjectileEntity   entity/projectile/ProjectileEntity.java
       implements GeoEntity, CustomSyncMotionEntity, ExplosiveProjectile
       └─ SmallCannonShellEntity / GrapeshotEntity / TaserBulletEntity / ...
```

```java
// entity/projectile/ProjectileEntity.java:72
public class ProjectileEntity extends Projectile implements GeoEntity, CustomSyncMotionEntity, ExplosiveProjectile {
```

**注册 id 为 `superbwarfare:projectile`**（默认投射物类型，见 `data/gun/ProjectileInfo.java:11`：`public String type = "superbwarfare:projectile";`）。

关键字段与 Builder 式 API（`ProjectileEntity.java:863-938`，**全部返回 `this`，可链式调用**）：

```java
public ProjectileEntity shooter(@Nullable Entity shooter);      // :863
public ProjectileEntity damage(float damage);                   // :868
public ProjectileEntity velocity(float velocity);               // :873
public ProjectileEntity headShot(float headShot);               // :878
public ProjectileEntity legShot(float legShot);                 // :883
public ProjectileEntity beast();                                // :888
public ProjectileEntity fireBullet(int fireLevel, boolean dragonBreath); // :893
public ProjectileEntity zoom(boolean zoom);                     // :899
public ProjectileEntity bypassArmorRate(float bypassArmorRate); // :904
public ProjectileEntity effect(ArrayList<MobEffectInstance>);   // :909
public ProjectileEntity knockback(float knockback);             // :920
public ProjectileEntity forceKnockback();                       // :925
public ProjectileEntity setGunItemId(ItemStack stack);          // :930
public ProjectileEntity setGunItemId(String id);                // :935
```

其它公开方法：

```java
public void shoot(LivingEntity living, double vecX, double vecY, double vecZ, float velocity, float spread);  // :681
public void setDamage(float damage);            // :673
public float getDamage();                       // :677
@Nullable public Entity getShooter();           // :772
public int getShooterId();                      // :776
public float getBypassArmorRate();              // :780
public boolean isZoom();                        // :843
@Nullable public String getGunItemId();         // :848
public boolean isPenetrating();                 // :852
public void setPenetrating(boolean penetrating);// :856
public void performOnHit(Entity, float, boolean, double);  // :647  可覆写
protected void onHitBlock(Vec3, BlockHitResult);           // :529  可覆写
protected void onHitEntity(Entity, ExtendedEntityRayTraceResult); // :586 可覆写
protected void explosionBullet(Entity, Vec3);              // :664
public static BlockHitResult rayTraceBlocks(Level, ClipContext, Predicate<BlockState>); // :694
```

**实体创建方式**（`GunItem.shootBullet`，`GunItem.java:796-816`）——通过 `EntityType.byString(projectileType)` 从 JSON 的 `Projectile.Type` 动态创建：

```java
// item/gun/GunItem.java:796-816（节选）
EntityType.byString(projectileType).ifPresent(entityType -> {
    var entity = entityType.create(level);
    if (entity == null) { Mod.LOGGER.warn("Failed to create projectile entity {}", projectileType); return; }
    if (entity instanceof Projectile projectileEntity) {
        projectileEntity.setOwner(shooter);
    }
    // SBW子弹弹射物专属属性
    if (entity instanceof ProjectileEntity projectile) {
        projectile.shooter(shooter)
                .damage((float) damage)
                .headShot((float) headshot)
                .zoom(zoom)
                .bypassArmorRate((float) bypassArmorRate)
                .setGunItemId(stack)
                .velocity(finalVelocity);
    }
    ...
});
```

投射物类型由 JSON 的 `ProjectileInfo` 指定（`data/gun/ProjectileInfo.java`）：

```java
public class ProjectileInfo implements IDBasedData<ProjectileInfo>, DeserializeFromString {
    @SerializedName("Type") public String type = "superbwarfare:projectile";
    @SerializedName("Data") public JsonObject data;
}
```

特殊值：`"Type": "empty"` → 不开火；`"Type": "ray"` → 走 `shootRay` 射线流程（`GunItem.java:777-781`）。

---

## 4. 客户端

### 4.1 枪械动画状态机（TaC `GunAnimationStateContext` 等价物）

**不存在独立的「动画状态上下文」对象类。** 等价能力分散在**两个位置**：

#### （a）动画状态机本体：`GunGeoItem.animationPredicate`（GeckoLib 驱动）

```java
// item/gun/GunGeoItem.java:61-127
@OnlyIn(Dist.CLIENT)
protected PlayState animationPredicate(AnimationState<GunGeoItem> event) {
    var player = Minecraft.getInstance().player;
    if (player == null) return PlayState.STOP;
    var stack = player.getMainHandItem();
    if (!(stack.getItem() instanceof GunItem)) return PlayState.STOP;

    var resource = GunResource.from(stack);      // 客户端资源
    var data = GunData.from(stack);              // 服务端同步来的状态
    var defaultResource = resource.compute();
    if (defaultResource == null) return PlayState.STOP;
    var animation = defaultResource.animation;
    if (animation == null || animation.idle == null) return PlayState.STOP;

    // Idle：非第一人称一律 Idle
    if (event.getData(DataTickets.ITEM_RENDER_PERSPECTIVE) != ItemDisplayContext.FIRST_PERSON_RIGHT_HAND)
        return event.setAndContinue(RawAnimation.begin().thenLoop(animation.idle));

    if (animation.edit  != null && ClientEventHandler.isEditing)                     return ...thenPlay(animation.edit);
    if (animation.bolt  != null && data.bolt.actionTimer.get() > 0)                   return ...thenPlay(animation.bolt);
    if (data.reloading()) { if (animation.reload != null) ... else if (data.reload.normal()) ... else if (data.reload.empty()) ... }
    if (animation.melee != null && ClientEventHandler.gunMelee > 0)                   return ...thenPlay(animation.melee);
    if (animation.fire  != null && ClientEventHandler.holdingFireKey && data.canShoot(player)) return ...thenLoop(animation.fire);
    if (player.isSprinting() && player.onGround() && ClientEventHandler.noSprintTicks == 0 && ClientEventHandler.drawTime < 0.01) {
        if (animation.sprint != null && ClientEventHandler.tacticalSprint) return ...thenLoop(animation.sprint);
        else if (animation.run != null)                                   return ...thenLoop(animation.run);
    }
    return event.setAndContinue(RawAnimation.begin().thenLoop(animation.idle));
}

// item/gun/GunGeoItem.java:124-127
@Override
public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
    controllers.add(new AnimationController<>(this, "animationController", 1, this::animationPredicate));
}
```

**动画名来自 JSON**（`resource/gun/GunAnimation.java`，位于 `assets/superbwarfare/sbw/guns/<id>.json`）：

```java
public class GunAnimation {
    @SerializedName("TransitionTickTime") public int transitionTickTime = 1;
    @SerializedName("Idle")         public String idle;
    @SerializedName("Fire")         public String fire;
    @SerializedName("Reload")       public String reload;
    @SerializedName("ReloadNormal") public String reloadNormal;
    @SerializedName("ReloadEmpty")  public String reloadEmpty;
    @SerializedName("Prepare")      public String prepare;
    @SerializedName("Iterative")    public String iterative;
    @SerializedName("Finish")       public String finish;
    @SerializedName("Edit")         public String edit;
    @SerializedName("Bolt")         public String bolt;
    @SerializedName("Run")          public String run;
    @SerializedName("Sprint")       public String sprint;
    @SerializedName("Melee")        public String melee;
}
```

#### （b）「全局可变动画状态」：`event/ClientEventHandler` 的 public static 字段

TaC 的 `GunAnimationStateContext` 在 SBW 中被**一整个 public static 字段集合替代**（`event/ClientEventHandler.java:73-238`）。这是迁移时最需要理解的一点：**SBW 用全局可变状态而非 per-gun 上下文对象**。

```
zoomTime, zoomPos, zoomPosZ, swayTime, swayX, swayY, moveTime, sprintTime,
movePosX, movePosY, moveRotZ, sprintBasicRotX/Y/Z, sprintPosX/Y, sprintBasicPosX/Y/Z,
movePosHorizon, velocityY, fireRecoilTime, firePosTimer, fireRotTimer, firePos, firePosZ,
fireRot, fireRotY, fireRotZ, customAnimSpeed, recoilTime, recoilHorizon, recoilY,
droneFov, droneFovLerp, fov, bowPullTimer, bowPower, bowPullPos, gunSpread, fireSpread,
fireCooldown, lookDistance, cameraLocation, switchVehicleWeaponCooldown, drawTime,
shellIndex, customZoom, artilleryIndicatorZoom, artilleryIndicatorCustomZoom,
holdingFireKey, bowPull, zoom, breath, tacticalSprint, stamina, switchTime,
moveFadeTime, sprintFadeTime, exhaustion, holdFireVehicle, zoomVehicle, burstFireAmount,
customRpm, gunMelee, chamberRot, actionMove, holdingFireKeyTicks, holdingFireKeyTicks0,
shouldPlayDischargeSound, revolverPreTime, revolverWheelPreTime, shakeTime, shakeRadius,
shakeAmplitude, shakeType, usingLunge, lungeAttack, lungeDraw, lungeSprint,
dismountCountdown, aimVillagerCountdown, cameraPitch, cameraYaw, cameraRoll,
noSprintTicks, canDoubleJump, holdArtilleryIndicator, holdToEjection, isEditing,
seekingTime, guideType, lockOn, seekingTimeVehicle, lockOnVehicle
```

#### （c）动画曲线/计时工具：`client/animation/*`

| 类 | 路径 | 作用 |
|---|---|---|
| `AnimationCurves` | `client/animation/AnimationCurves.java` | 缓动函数集合（如 `EASE_OUT_EXPO`、`EASE_IN_EXPO`、`EASE_OUT_CIRC`） |
| `AnimationTimer` | `client/animation/AnimationTimer.java` | 毫秒级「进程/回退」计时器，`new AnimationTimer(500, 2000).forwardAnimation(...).backwardAnimation(...)` |
| `AnimationTicker` | `client/animation/AnimationTicker.java` | tick 驱动的时间推进 |
| `ValueAnimator<T>` | `client/animation/ValueAnimator.java` | 数值过渡动画（`ValueAnimator.create(n, 800, 0)`、`lerp`、`compareAndUpdate`、`forward/backward`、`getProgress`、`reset`） |
| `AnimationHelper` | `client/animation/AnimationHelper.java` | 渲染辅助：`handleShootFlare`、`handleZoomCrossHair`、`renderArms`、`handleGunAttachments`（实际在 `client/ItemModelHelper`） |

### 4.2 枪械 HUD Overlay

**不存在单一的 `GunHudOverlay`。** SBW 把它拆成多个 `LayeredDraw.Layer` 实现（NeoForge 1.21.1 的 HUD 图层系统）。

枪械相关 overlay（`client/overlay/`）：

| 类 | 路径 | 作用 |
|---|---|---|
| `AmmoCountOverlay` | `client/overlay/AmmoCountOverlay.java` | **手持弹药物品/弹药盒时显示各类弹药总量**（最接近 TaC `GunHudOverlay` 的弹药部分） |
| `AmmoBarOverlay` | `client/overlay/AmmoBarOverlay.java` | 枪内弹匣弹药条 |
| `CrossHairOverlay` | `client/overlay/CrossHairOverlay.java` | 准星 + 命中/击杀指示（`headIndicator` / `killIndicator` / `vehicleIndicator` / `hitIndicator` / `gunRot`） |
| `HeatBarOverlay` | `client/overlay/HeatBarOverlay.java` | 过热条 |
| `StaminaOverlay` | `client/overlay/StaminaOverlay.java` | 体力条 |
| `HandsomeFrameOverlay` | `client/overlay/HandsomeFrameOverlay.java` | 开镜黑边 |
| `ItemRendererFixOverlay` | `client/overlay/ItemRendererFixOverlay.java` | 手持物品渲染修正 |
| `SpyglassRangeOverlay` / `RedTriangleOverlay` | 同上目录 | 望远镜测距 / 三角标记 |

其余（载具、无人机、导弹）：`VehicleHudOverlay`, `VehicleCrosshairOverlay`, `VehicleMainWeaponHudOverlay`, `VehicleTeamOverlay`, `JavelinHudOverlay`, `IglaHudOverlay`, `DroneHudOverlay`, `TowOverlay`, `MortarInfoOverlay`, `Type63InfoOverlay`, `IFFOverlay`, `ArmorPlateOverlay`, `KillMessageOverlay`。

**可注入/可继承的方法**：所有 overlay 都实现 `net.minecraft.client.gui.LayeredDraw.Layer` 的单一方法：

```java
// 以 client/overlay/AmmoCountOverlay.java:29,52 为例
@OnlyIn(Dist.CLIENT)
public class AmmoCountOverlay implements LayeredDraw.Layer {
    public static final ResourceLocation ID = Mod.loc("ammo_count");   // :31

    @Override
    @ParametersAreNonnullByDefault
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) { ... }   // :52
}
```

**注册位点**（`client/ClientRenderHandler.java:69-93`，`RegisterGuiLayersEvent`）：

```java
// client/ClientRenderHandler.java:69-93（节选）
@SubscribeEvent
public static void registerOverlays(RegisterGuiLayersEvent event) {
    event.registerBelowAll(KillMessageOverlay.ID, new KillMessageOverlay());
    event.registerBelow(KillMessageOverlay.ID, ArmorPlateOverlay.ID, new ArmorPlateOverlay());
    event.registerBelow(ArmorPlateOverlay.ID, AmmoBarOverlay.ID, new AmmoBarOverlay());
    ...
    event.registerBelowAll(StaminaOverlay.ID, new StaminaOverlay());
    event.registerBelowAll(AmmoCountOverlay.ID, new AmmoCountOverlay());
    event.registerBelowAll(ItemRendererFixOverlay.ID, new ItemRendererFixOverlay());
    event.registerBelowAll(CrossHairOverlay.ID, new CrossHairOverlay());
    event.registerBelowAll(HeatBarOverlay.ID, new HeatBarOverlay());
    ...
}
```

> **迁移含义**：要「注入」自己的 HUD，不再需要 mixin 或继承 `GunHudOverlay`，只需监听 `RegisterGuiLayersEvent`（MOD 总线）并 `event.registerBelowAll/registerAbove(ID, layer)`。注意 `ClientRenderHandler` 类本身**没有 `modid` 限定**（`@EventBusSubscriber(bus = MOD, value = Dist.CLIENT)`，`ClientRenderHandler.java:28`），不能直接复用其注册逻辑，但事件机制是可用的。

### 4.3 枪械渲染基础类（`client/renderer/`）

TaC 渲染基类对应物：

```
software.bernie.geckolib.renderer.GeoItemRenderer<T>
  └─ CustomGunRenderer<T extends GunGeoItem & GeoAnimatable>     client/renderer/CustomGunRenderer.java:24
       └─ SimpleGunRenderer<T extends GunGeoItem & GeoAnimatable> client/renderer/SimpleGunRenderer.java:18
            └─ 具体枪械渲染器（M4ItemRenderer 等，亦可直接继承 CustomGunRenderer）

software.bernie.geckolib.model.GeoModel<T>
  └─ CustomGunModel<T extends GunGeoItem & GeoAnimatable>          client/model/item/CustomGunModel.java:24
       └─ 具体枪械模型（M4ItemModel 等）
```

**可覆写方法**：

```java
// client/renderer/CustomGunRenderer.java
public CustomGunRenderer(GeoModel<T> model);                                    // :35
public void preRender(PoseStack, T, BakedGeoModel, MultiBufferSource, VertexConsumer, boolean, float, int, int, int);   // :40
public void actuallyRender(PoseStack, T, BakedGeoModel, RenderType, MultiBufferSource, VertexConsumer, boolean, float, int, int, int); // :49
public RenderType getRenderType(T, ResourceLocation, MultiBufferSource, float); // :61
public ResourceLocation getTextureLocation(T);                                  // :69
public void defaultRender(PoseStack, T, MultiBufferSource, RenderType, VertexConsumer, float, float, int);  // :122
public void renderIlluminatedBones(...);                                        // :187
public void illuminatedRender(...);                                             // :206
public void illuminatedRenderChildBones(...);                                   // :226
// 受保护字段（可供子类使用）
protected T animatable;                 // :30
protected boolean renderArms;           // :31
protected MultiBufferSource currentBuffer;  // :32
protected RenderType renderType;        // :33
```

```java
// client/model/item/CustomGunModel.java
public ItemStack gunItemStack;                                                  // :25
public ResourceLocation getAnimationResource(T);   // :28
public ResourceLocation getModelResource(T);       // :33
public ResourceLocation getTextureResource(T);     // :38
public ResourceLocation getLODModelResource(T);    // :42
public ResourceLocation getLODTextureResource(T);  // :46
protected ModelResource getModel(T);               // :50
protected DefaultGunResource getResource(T);       // :58
public void applyMolangQueries(AnimationState<T>, double animTime);  // :63
public boolean shouldCancelRender(ItemStack, AnimationState<T>);     // :112
```

GeckoLib 模型需要在物品注册时通过 `RegisterClientExtensionsEvent` 绑定（`GunGeoItem.java:129-149`），`getRenderer()` 由 `CustomRendererItem` 接口提供。

---

## 5. 合成 / 工作台

### 5.1 TaC `GunSmithTableResult` / `RawGunTableResult`

**未找到。SBW 不存在「枪械合成的配方结果类」。**

理由：SBW 的枪械**完全用原版工作台体系合成**，不自定义 `Recipe`/`RecipeSerializer`，因此不需要结果类型。

### 5.2 枪械实际是怎么造的：**原版锻造台（Smithing Transform）**

`datagen/ModRecipeProvider.java:1870-1895`：

```java
public static void gunSmithing(RecipeOutput writer, ItemLike blueprint, GunRarity rarity, TagKey<Item> tagKey, Item pResultItem) {
    gunSmithing(writer, blueprint, rarity, Ingredient.of(tagKey), pResultItem);
}

public static void gunSmithing(RecipeOutput writer, ItemLike blueprint, GunRarity rarity, ItemLike ingredient, Item pResultItem) {
    gunSmithing(writer, blueprint, rarity, Ingredient.of(ingredient), pResultItem);
}

public static void gunSmithing(RecipeOutput writer, ItemLike blueprint, GunRarity rarity, Ingredient ingredient, Item pResultItem) {
    ItemLike pack = switch (rarity) {
        case COMMON    -> ModItems.COMMON_MATERIAL_PACK.get();
        case RARE      -> ModItems.RARE_MATERIAL_PACK.get();
        case EPIC      -> ModItems.EPIC_MATERIAL_PACK.get();
        case LEGENDARY -> ModItems.LEGENDARY_MATERIAL_PACK.get();
    };

    SmithingTransformRecipeBuilder.smithing(
                    Ingredient.of(blueprint),   // 模板槽
                    Ingredient.of(pack),        // 基础槽（材料包）
                    ingredient,                 // 附加槽
                    RecipeCategory.COMBAT,
                    pResultItem
            )
            .unlocks(getHasName(blueprint), has(blueprint))
            .save(writer, Mod.loc(getItemName(pResultItem) + "_smithing"));   // → <gun>_smithing.json
}

public enum GunRarity { COMMON, RARE, EPIC, LEGENDARY }
```

调用示例（`ModRecipeProvider.java:1497-1537`，共 40 余条）：

```java
gunSmithing(writer, ModItems.AK_47_BLUEPRINT.get(), GunRarity.RARE, ItemTags.LOGS,        ModItems.AK_47.get());
gunSmithing(writer, ModItems.M_4_BLUEPRINT.get(),   GunRarity.RARE, ModTags.Items.INGOTS_STEEL, ModItems.M_4.get());
gunSmithing(writer, ModItems.GLOCK_17_BLUEPRINT.get(), GunRarity.COMMON, Items.IRON_INGOT, ModItems.GLOCK_17.get());
gunSmithing(writer, ModItems.SENTINEL_BLUEPRINT.get(), GunRarity.EPIC, ModItems.CELL.get(), ModItems.SENTINEL.get());
gunSmithing(writer, ModItems.MINIGUN_BLUEPRINT.get(), GunRarity.LEGENDARY, ModItems.MOTOR.get(), ModItems.MINIGUN.get());
// ……共 40 余条，完整清单见 ModRecipeProvider.java:1497-1537
```

**生成的 JSON 就是原版 `minecraft:smithing_transform`**（文件名 `<gun_id>_smithing.json`，位于 `data/superbwarfare/recipe/`）：

```json
{
  "type": "minecraft:smithing_transform",
  "template": { "item": "superbwarfare:ak_47_blueprint" },
  "base":     { "item": "superbwarfare:rare_material_pack" },
  "addition": { "tag": "minecraft:logs" },
  "result":   { "id": "superbwarfare:ak_47" }
}
```

`blueprint` 物品类极简（`item/common/BlueprintItem.java`，**无任何 id 字段**）：

```java
public class BlueprintItem extends Item {
    public BlueprintItem(Rarity rarity) { super(new Properties().rarity(rarity)); }
}
```

> **重要**：枪械 id **不在配方结果里以数据形式存在**，就是原版 `result` 的物品 id。所以**不存在「从配方结果读取枪械 id」的需求**，也就**没有 TaC 那种 accessor 需求**。取枪械 id 直接：

```java
ItemStack result = recipe.getResultItem(registryAccess);
String gunId = GunData.getRegistryId(result.getItem());   // "superbwarfare:ak_47"
```

### 5.3 SBW 自己注册的配方类型与序列化器（全清单）

`init/ModRecipes.java`：

```java
// init/ModRecipes.java:18-43
public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, Mod.MODID);
public static final DeferredRegister<RecipeType<?>>        RECIPE_TYPES        = DeferredRegister.create(BuiltInRegistries.RECIPE_TYPE, Mod.MODID);

public static final DeferredHolder<..., RecipeSerializer<PotionMortarShellRecipe>>    POTION_MORTAR_SHELL_SERIALIZER   = ...register("potion_mortar_shell",     () -> new SimpleCraftingRecipeSerializer<>(PotionMortarShellRecipe::new));
public static final DeferredHolder<..., RecipeSerializer<AmmoBoxAddAmmoRecipe>>        AMMO_BOX_ADD_AMMO_SERIALIZER     = ...register("ammo_box_add_ammo",     () -> new SimpleCraftingRecipeSerializer<>(AmmoBoxAddAmmoRecipe::new));
public static final DeferredHolder<..., RecipeSerializer<AmmoBoxExtractAmmoRecipe>>    AMMO_BOX_EXTRACT_AMMO_SERIALIZER = ...register("ammo_box_extract_ammo", () -> new SimpleCraftingRecipeSerializer<>(AmmoBoxExtractAmmoRecipe::new));
public static final DeferredHolder<..., RecipeSerializer<SmokeDyeRecipe>>              SMOKE_DYE_SERIALIZER             = ...register("smoke_dye",             () -> new SimpleCraftingRecipeSerializer<>(SmokeDyeRecipe::new));
public static final DeferredHolder<..., RecipeSerializer<VehicleAssemblingRecipe>>     VEHICLE_ASSEMBLING_SERIALIZER    = ...register("vehicle_assembling",    VehicleAssemblingRecipeSerializer::new);
public static final DeferredHolder<..., RecipeSerializer<VehicleResetRecipe>>          VEHICLE_RESET_SERIALIZER         = ...register("vehicle_reset",         () -> new SimpleCraftingRecipeSerializer<>(VehicleResetRecipe::new));

public static final DeferredHolder<..., RecipeType<VehicleAssemblingRecipe>> VEHICLE_ASSEMBLING_TYPE =
        RECIPE_TYPES.register("vehicle_assembling", () -> new RecipeType<>() {
            @Override public String toString() { return Mod.MODID + ":vehicle_assembling"; }
        });
```

| 配方 type / serializer id | Recipe 类 | 路径 | 说明 |
|---|---|---|---|
| `superbwarfare:vehicle_assembling` | `VehicleAssemblingRecipe` | `recipe/vehicle/VehicleAssemblingRecipe.java` | **唯一自定义 RecipeType**，用于载具装配台，形状配方 + 结果 |
| `superbwarfare:potion_mortar_shell` | `PotionMortarShellRecipe` | `recipe/PotionMortarShellRecipe.java` | 特殊合成（药水迫击炮弹） |
| `superbwarfare:ammo_box_add_ammo` | `AmmoBoxAddAmmoRecipe` | `recipe/AmmoBoxAddAmmoRecipe.java` | **特殊合成：给弹药盒加弹药** |
| `superbwarfare:ammo_box_extract_ammo` | `AmmoBoxExtractAmmoRecipe` | `recipe/AmmoBoxExtractAmmoRecipe.java` | **特殊合成：从弹药盒取弹药** |
| `superbwarfare:smoke_dye` | `SmokeDyeRecipe` | `recipe/SmokeDyeRecipe.java` | 特殊合成（烟雾弹染色） |
| `superbwarfare:vehicle_reset` | `VehicleResetRecipe` | `recipe/VehicleResetRecipe.java` | 特殊合成（载具重置） |

配套 `recipe/vehicle/`：`VehicleAssemblingIngredient.java`、`VehicleAssemblingRecipeSerializer.java`、`VehicleAssemblingResult.java`；datagen builder：`datagen/builder/VehicleAssemblingRecipeBuilder.java`。

**特殊配方的 datagen 写法**（`ModRecipeProvider.java:1860-1863`）：

```java
SpecialRecipeBuilder.special(AmmoBoxAddAmmoRecipe::new).save(writer, "ammo_box_add_ammo");
SpecialRecipeBuilder.special(AmmoBoxExtractAmmoRecipe::new).save(writer, "ammo_box_extract_ammo");
SpecialRecipeBuilder.special(SmokeDyeRecipe::new).save(writer, "smoke_dye");
SpecialRecipeBuilder.special(VehicleResetRecipe::new).save(writer, "vehicle_reset");
```

> **注意**：上表 **没有任何「枪械 / 弹药的配方结果类」**；`VehicleAssemblingResult` 是**载具**装配结果，不是枪械工作台结果。

### 5.4 是否存在枪械工作台？

**没有 TaC 那种「枪械工作台（Gun Smith Table）」。** SBW 用**两个不同的方块**承担相关职能：

| 方块 | 类 | 路径 | 职能 |
|---|---|---|---|
| **锻造台（Reforging Table）** | `ReforgingTableBlock` | `block/ReforgingTableBlock.java` | **给枪械装 Perk（词条）并升级**；菜单 `menu/ReforgingTableMenu.java` |
| **载具装配台** | （见 `ModBlocks.VEHICLE_ASSEMBLING_TABLE`） | `recipe/vehicle/*`, `menu/VehicleAssemblingMenu.java` | 装配载具 |

`ReforgingTableBlock` 打开菜单（`ReforgingTableBlock.java:126-132`）：

```java
@Override
@Nullable
@ParametersAreNonnullByDefault
public MenuProvider getMenuProvider(BlockState pState, Level pLevel, BlockPos pPos) {
    return new SimpleMenuProvider((i, inventory, player) ->
            new ReforgingTableMenu(i, inventory, ContainerLevelAccess.create(pLevel, pPos)), CONTAINER_TITLE);
}
```

`ReforgingTableMenu` 槽位（`ReforgingTableMenu.java:29-33`）——**没有枪械合成结果槽语义，而是「枪 + 3 个 Perk」**：

```java
public static final int INPUT_SLOT       = 0;   // 枪械
public static final int AMMO_PERK_SLOT   = 1;   // 弹药类 Perk
public static final int FUNC_PERK_SLOT   = 2;   // 功能类 Perk
public static final int DAMAGE_PERK_SLOT = 3;   // 伤害类 Perk
public static final int RESULT_SLOT      = 4;   // 重铸结果（带 Perk 的枪）
```

关键方法：

```java
public @Nullable ItemStack getGunStack();                       // :153
public @Nullable GunData  getGunData();                         // :162
public void generateResult();                                   // :225  生成带 Perk 的结果
public int availableLevel();                                    // :191  可用升级点数
public void setPerkLevel(Perk.Type type, boolean upgrade, boolean isCreative);   // :203
public @Nullable ItemStack getPerkItemBySlot(Perk.Type type);   // :399
```

**配件改装界面**在客户端另有一套：`client/screens/WeaponEditScreen.java`，通过 `ItemScreenProvider` 打开（`item/gun/GunItem.java:1153-1160`）：

```java
@OnlyIn(Dist.CLIENT)
@Override
public @Nullable Screen getItemScreen(ItemStack stack, Player player, InteractionHand hand) {
    if (ClientEventHandler.canOpenEditScreen(stack, hand) && stack.getItem() instanceof GunItem && canEditAttachments(GunData.from(stack))) {
        return new WeaponEditScreen(stack);
    }
    return null;
}
```

是否可改装由 `GunItem.canEditAttachments` 决定（`GunItem.java:1136-1138`）：

```java
public boolean canEditAttachments(GunData data) {
    return data.compute().getAmmoConsumers().size() > 1;
}
```

---

## 6. 对外公共 API 包清单

| 包 | 文件数 | 公开类 | 性质 |
|---|---|---|---|
| `com.atsuishio.superbwarfare.api.event` | 6 | `PreKillEvent`（`SendKillMessage`, `Indicator`）、`ProjectileHitEvent`（`HitEntity`, `HitBlock`）、`RegisterContainersEvent`、`ReloadEvent`（`Pre`, `Post`）、`RenderPlayerArmEvent`、`ShootEvent`（`Pre`, `Post`） | **唯一显式 API 包**。多个类带 `@ApiStatus.AvailableSince(...)` 标注（`0.8.0` / `0.8.7` / `0.8.7.1` / `0.8.8`），说明作者有意维护兼容性 |

> **重要事实**：`api` 包**只有 event 子包**，**不存在 `api.item`、`api.gun`、`api.ammo` 等接口包**。因此 TaC 里那些 `api.item.IGun`、`api.item.IAmmo`、`api.item.IAttachment` 之类的接口层，在 SBW 中**未找到**。

支撑性（但**未标注为 API**、仍被广泛使用的）公共包：

| 包 | 主要公开类 | 迁移关注度 |
|---|---|---|
| `...data.gun` | `Ammo`（枚举）、`AmmoConsumer`、`GunData`、`DefaultGunData`、`ShootParameters`、`GunType`、`FireMode`、`FireModeInfo`、`ProjectileInfo`、`SoundInfo`、`GunProp`、`GunPropertyModifier`、`ReloadType`、`SeekType`、`ShootPos`、`ShootRay`、`DamageReduce`、`SeekWeaponInfo` | **极高** |
| `...data.gun.value` | `AttachmentType`、`BooleanValue`、`DoubleValue`、`IntValue`、`StringValue`、`StringEnumValue`、`ItemStackValue`、`ReloadState`、`Starter`、`Timer` | 中 |
| `...data.gun.subdata` | `AmmoSlot`、`Attachment`、`Bolt`、`Charge`、`Perks`、`Reload` | 中 |
| `...data` | `CustomData`（`DataMap` 注册表）、`DataLoader`、`IDBasedData`、`ObjectToList`、`StringToObject`、`JsonPropertyModifier`、`PropertyModifier`、`DefaultDataSupplier`、`DeserializeFromString`、`ModColor`、`StringOrVec3` | 中 |
| `...item.gun` | `GunItem`、`GunGeoItem` | **极高** |
| `...item.gun.{rifle,handgun,...}` | 41 个具体枪械类 | 低 |
| `...item.common.ammo` | `AmmoSupplierItem`、`AmmoBoxItem`、`CreativeAmmoBox`、`HandgunAmmoBox`、`RifleAmmoBox`、`SniperAmmoBox`、`ShotgunAmmoBox`、`MortarShell`、`PotionMortarShell`、`RpgRocketStandard`、`RpgRocketTBG`、`MediumRocketItem`、`CannonShellItem` | **高** |
| `...item.common.ammo.box` | `AmmoBoxInfo` | 中 |
| `...item.common` | `BlueprintItem`、`MaterialPack`、`MedicalKitItem` | 低 |
| `...init` | `ModItems`（`GUNS`/`AMMO`/`ITEMS`/`BLOCKS`/`VEHICLES`/`PERKS` 六个 `DeferredRegister`）、`ModRecipes`、`ModDataComponents`、`ModAttachments`、`ModEntities`、`ModSounds`、`ModTags`、`ModPerks`、`ModMenuTypes`、`ModBlocks`、`ModSerializers`、`ModDamageTypes`、`ModParticles`/`ModParticleTypes`、`ModArmorMaterials`、`ModAttributes`、`ModCriteriaTriggers`、`ModCapabilities`、`ModCommandArguments`、`ModKeyMappings`、`ModProperties`、`ModTabs`、`ModVillagers`、`ModMobEffects`、`ModPotions`、`ModEnumExtensions` | **极高** |
| `...component` | `ModDataComponents` | 中 |
| `...capability.player` | `PlayerVariable` | **高**（弹药总量载体） |
| `...capability.energy` | `DynamicEnergyStorage`、`InfinityEnergyStorage`、`ItemEnergyStorage`、`SyncedEntityEnergyStorage`、`VehicleEnergyStorage` | 低 |
| `...capability.laser` | `LaserCapability`、`LaserCapabilityProvider`、`LaserHandler` | 低 |
| `...entity.projectile` | `ProjectileEntity`、`CustomSyncMotionEntity`、`ExplosiveProjectile`、`CustomDamageProjectile`、`CustomGravityEntity`、`DestroyableProjectile` 等 | **高** |
| `...tools` | `SoundTool`、`InventoryTool`、`GunsTool`、`NBTTool`、`DamageHandler`、`EntityFindUtil`、`FormatTool`、`ParticleTool`、`ProjectileTool`、`TraceTool`、`RangeTool`、`MathTool`、`VectorTool`、`OBB`、`HitboxHelper`、`CustomExplosion` 等 | **高** |
| `...client.renderer` | `CustomGunRenderer`、`SimpleGunRenderer`、`ModRenderTypes` | 高 |
| `...client.model.item` | `CustomGunModel` + 52 个具体模型 | 高 |
| `...client.animation` | `AnimationCurves`、`AnimationHelper`、`AnimationTicker`、`AnimationTimer`、`ValueAnimator` | 高 |
| `...client.overlay` | 22 个 overlay + `weapon` 子包 4 个 | 高 |
| `...client` | `ClientRenderHandler`、`ClickHandler`、`ClientEventHandler`(在 `event`) 、`GunRendererBuilder`、`PoseTool`、`ItemModelHelper`、`MouseMovementHandler`、`RenderHelper` | 高 |
| `...resource.gun` | `GunResource`、`DefaultGunResource`、`GunAnimation` | 高 |
| `...recipe` | 6 个 Recipe 类 + `vehicle/*` 4 个 | 中 |
| `...event` | `GunEventHandler`、`ClientEventHandler`、`EntityEventHandler`、`PlayerEventHandler`、`LivingEventHandler` 等 | 高 |
| `...menu` | `ReforgingTableMenu`、`VehicleAssemblingMenu`、`ChargingStationMenu` 等 | 中 |
| `...perk` | `Perk`、`PerkInstance` | 中 |
| `...datagen` | `ModRecipeProvider`（**JSON 结构的权威来源**）、`ModItemModelProvider`、`builder/VehicleAssemblingRecipeBuilder`、`builder/CustomSeparateModelBuilder` | 高 |

---

## 7. 弹药背包 / 弹药槽 / 按口径过滤

### 7.1 是否存在「弹药背包」接口

**未找到** `IAmmoBackpack` / `AmmoBackpack` 之类接口。

TaC 的「弹药背包」在 SBW 中的**语义等价物是两种东西**：

#### （a）玩家弹药总量：`PlayerVariable`（data attachment，非背包物品）

```java
// capability/player/PlayerVariable.java:25-29
@EventBusSubscriber(modid = Mod.MODID)
public class PlayerVariable implements INBTSerializable<CompoundTag> {
    private PlayerVariable old = null;
    public Map<Ammo, Integer> ammo = new EnumMap<>(Ammo.class);
    public boolean tacticalSprint = false;
```

- 挂载点：`init/ModAttachments.java` 的 `PLAYER_VARIABLE`
- 取用：`player.getData(ModAttachments.PLAYER_VARIABLE)`；写入后需 `player.setData(...)` + `cap.sync(player)`
- 便捷静态取用：`PlayerVariable.getOrDefault(Entity)`（`PlayerVariable.java:43-45`）
- 读写用 `Ammo#get/set/add(PlayerVariable)`（见 2.4(b)）

#### （b）真正的「弹药容器物品」：弹药盒

| 类 | 路径 | 容量载体 |
|---|---|---|
| `AmmoBoxItem` | `item/common/ammo/AmmoBoxItem.java` | 5 个 `ammo_*` data component（**每类弹药各自一个 int**） |
| `HandgunAmmoBox` / `RifleAmmoBox` / `SniperAmmoBox` / `ShotgunAmmoBox` | `item/common/ammo/*.java` | 同上 |
| `CreativeAmmoBox` | `item/common/ammo/CreativeAmmoBox.java` | 视为无限（`InventoryTool.hasCreativeAmmoBox`） |

`AmmoBoxItem` 的**「按类型过滤」机制**正是你问的「弹药按口径过滤」的对应物 —— 它是**用字符串类型名 + `AmmoBoxInfo` data component 实现的**（`item/common/ammo/AmmoBoxItem.java`）：

```java
// item/common/ammo/AmmoBoxItem.java:31-35
private static final List<String> AMMO_TYPE_LIST = generateAmmoTypeList();   // ["All", "HandgunAmmo", "RifleAmmo", "ShotgunAmmo", "SniperAmmo", "HeavyAmmo"]

private static List<String> generateAmmoTypeList() {                          // :78-87
    var list = new ArrayList<String>();
    list.add("All");
    for (var ammoType : Ammo.values()) {
        list.add(ammoType.serializationName);
    }
    return list;
}
```

过滤/选中逻辑（`AmmoBoxItem.java:45-51, 92-113`）：

```java
// :45-51
var info = stack.get(ModDataComponents.AMMO_BOX_INFO);
if (info == null) info = new AmmoBoxInfo("All", false);
String selectedType = info.type();

var cap = player.getData(ModAttachments.PLAYER_VARIABLE).watch();
if (!level.isClientSide()) {
    var types = (selectedType.equals("All") || info.isDrop()) ? Ammo.values() : new Ammo[]{Ammo.getType(selectedType)};
    ...
}

// :92-113 潜行挥空 = 循环切换过滤类型
var info = stack.get(ModDataComponents.AMMO_BOX_INFO) == null ? new AmmoBoxInfo("All", false) : stack.get(ModDataComponents.AMMO_BOX_INFO);
if (info.isDrop()) return false;

var index = Math.max(0, AMMO_TYPE_LIST.indexOf(info.type()));
var typeString = AMMO_TYPE_LIST.get((index + 1) % AMMO_TYPE_LIST.size());

stack.set(ModDataComponents.AMMO_BOX_INFO, new AmmoBoxInfo(typeString, false));
```

`AmmoBoxInfo`（`item/common/ammo/box/AmmoBoxInfo.java`，**data component**）：

```java
public record AmmoBoxInfo(String type, boolean isDrop) {
    public static final Codec<AmmoBoxInfo> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("type").forGetter(AmmoBoxInfo::type),
            Codec.BOOL.fieldOf("is_drop").forGetter(AmmoBoxInfo::isDrop)
    ).apply(instance, AmmoBoxInfo::new));
}
```

注册于 `component/ModDataComponents.java:48-51`，id 为 `superbwarfare:ammo_box_info`。

**弹药盒与玩家弹药互转的特殊配方**：`AmmoBoxAddAmmoRecipe` / `AmmoBoxExtractAmmoRecipe`（见 5.3）。

### 7.2 判断「一个 ItemStack 是否为弹药 / 口径是否匹配」的工具

**不存在统一谓词工具（没有 `isAmmo(ItemStack)`）**，但有三处可用：

#### （1）按物品类型判断（最直接）

```java
import com.atsuishio.superbwarfare.item.common.ammo.AmmoSupplierItem;

// 是否为弹药物品 + 属于哪一类
if (stack.getItem() instanceof AmmoSupplierItem ammoItem) {
    Ammo type = ammoItem.type;          // public final Ammo type
    int perItem = ammoItem.ammoToAdd;   // public final int ammoToAdd
}

// 是否为弹药盒
if (stack.getItem() instanceof AmmoBoxItem) { ... }
```

#### （2）由物品反查 `Ammo` 枚举（**没有现成方法，需自行推导**）

源码中**不存在** `Ammo.fromItem(Item)`。可用两种方式自行实现：

```java
// 方式 A：遍历枚举比较 defaultItemSupplier（Ammo.defaultItemSupplier 是 public final）
public static Ammo fromItem(Item item) {
    for (Ammo a : Ammo.values()) {
        if (a.defaultItemSupplier.get() == item) return a;
    }
    return null;
}

// 方式 B：比较 ItemStack
public static Ammo fromStack(ItemStack stack) {
    for (Ammo a : Ammo.values()) {
        if (stack.is(a.defaultItemSupplier.get())) return a;
    }
    return null;
}
```

#### （3）口径「匹配」判断：`AmmoConsumer.isAmmoItem`（**这是 SBW 官方的弹药匹配谓词**）

```java
// data/gun/AmmoConsumer.java:78-80
public boolean isAmmoItem(ItemStack stack) {
    return ItemStack.isSameItemSameComponents(stack, this.stack);
}
```

语义：**「该 ItemStack 是否与当前 `AmmoConsumer` 解析出的弹药物品完全一致（同物品 + 同 data component）」**。用于：
- `consume(...)` 的实际扣除（`AmmoConsumer.java:151`）
- `count(...)` 的统计（`AmmoConsumer.java:186`）

同文件还有按 `Ammo` 枚举过滤的背包统计/扣除（`tools/InventoryTool.java:75-147`），**这才是最接近「按口径过滤背包弹药」的工具**：

```java
// tools/InventoryTool.java:75-97
public static int countAmmoItem(@Nullable IItemHandler handler, @Nullable Ammo type) {
    if (handler == null || type == null) return 0;
    int count = 0;
    for (int i = 0; i < handler.getSlots(); i++) {
        var stack = handler.getStackInSlot(i);
        // AmmoSupplier Item
        if (stack.getItem() instanceof AmmoSupplierItem ammoSupplierItem && ammoSupplierItem.type == type) {
            count += ammoSupplierItem.ammoToAdd * stack.getCount();
        }
        // AmmoBox
        if (stack.getItem() instanceof AmmoBoxItem) {
            var stackAmmo = type.get(stack);
            if (stackAmmo > 0) count += stackAmmo;
        }
    }
    return count;
}

// tools/InventoryTool.java:107-147
public static int consumeAmmoItem(@Nullable Entity entity, @Nullable Ammo type, int count);
public static int consumeAmmoItem(@Nullable IItemHandler handler, @Nullable Ammo type, int count);
```

**`Ammo` 枚举与 `InventoryTool` 联合使用即完成「按口径过滤 + 统计 + 扣除」全链路**：

```java
Ammo type = Ammo.RIFLE;
int have = InventoryTool.countAmmoItem(player, type);        // 背包内该类弹药总数
InventoryTool.consumeAmmoItem(player, type, 10);             // 扣除 10 发
```

#### （4）标签层面的「弹药过滤」

**未找到弹药物品标签。** `init/ModTags.java:22-62` 的 `Items` 内部类只有枪械/护甲/材料/蓝图/工具标签：

```java
public static final TagKey<Item> GUN = modItemTag("gun");
public static final TagKey<Item> SMG = modItemTag("smg");
public static final TagKey<Item> RIFLE = modItemTag("rifle");
public static final TagKey<Item> SNIPER_RIFLE = modItemTag("sniper_rifle");
public static final TagKey<Item> SHOTGUN = modItemTag("shotgun");
public static final TagKey<Item> MACHINE_GUN = modItemTag("machine_gun");
public static final TagKey<Item> LAUNCHER = modItemTag("launcher");
public static final TagKey<Item> MILITARY_ARMOR = modItemTag("military_armor");
public static final TagKey<Item> MILITARY_ARMOR_HEAVY = modItemTag("military_armor_heavy");
public static final TagKey<Item> INGOTS_STEEL = modItemTag("ingots/steel");
public static final TagKey<Item> STORAGE_BLOCK_STEEL = modItemTag("storage_blocks/steel");
public static final TagKey<Item> INGOTS_CEMENTED_CARBIDE = modItemTag("ingots/cemented_carbide");
public static final TagKey<Item> STORAGE_BLOCK_CEMENTED_CARBIDE = modItemTag("storage_blocks/cemented_carbide");
public static final TagKey<Item> BLUEPRINT = modItemTag("blueprint");
public static final TagKey<Item> COMMON_BLUEPRINT / RARE_BLUEPRINT / EPIC_BLUEPRINT / LEGENDARY_BLUEPRINT / CANNON_BLUEPRINT
public static final TagKey<Item> HAMMER = modItemTag("hammer");
public static final TagKey<Item> WRENCHES / TOOLS_WRENCH / TOOLS_CROWBAR / TOOLS_HAMMER
public static final TagKey<Item> ANIMATED_PISTOL / ANIMATED_SNIPER / ANIMATED_RIFLE / ANIMATED_SHOTGUN / ANIMATED_SMG / ANIMATED_RPG / ANIMATED_MG / ANIMATED_MINIGUN
```

**没有 `superbwarfare:ammo`、`superbwarfare:rifle_ammo` 之类的弹药标签**（标签 id 前缀为 `superbwarfare:`，见 `ModTags.modItemTag`）。

---

## 8. TaC 1.18.2 → Superb Warfare 迁移对照总表

| # | TaC 1.18.2 能力 | Superb Warfare 对应物 | 状态 |
|---|---|---|---|
| 1 | `com.tacz.guns.api.item.gun.AbstractGunItem` | `com.atsuishio.superbwarfare.item.gun.GunItem`（+ `GunGeoItem`） | **存在且等价**（层次多一层） |
| 2 | `AmmoItemBuilder`（按 AmmoId 构造弹药） | `Ammo#getItemStack(int)` / `Ammo.defaultItemSupplier` | **语义差异极大**（无口径，只有 5 类） |
| 3 | `AmmoId`（`tacz:9mm` 等） | **未找到** | **不存在** |
| 4 | 口径数据存于弹药 ItemStack | 弹药类别由**物品本身**决定；数量存于 `superbwarfare:ammo_<name>` data component（int） | **语义差异极大** |
| 5 | `GunId` 对象 + NBT `GunId` 键 | **未找到**；改用 `GunData.from(stack).id`（物品注册名字符串） | **语义差异极大** |
| 6 | `IGun#getGunId(ItemStack)` | `GunData.from(stack).id` / `GunData.getRegistryId(Item)` | **存在且等价** |
| 7 | `ModernKineticGunScriptAPI` | **未找到**；由 `GunData` + `ShootParameters` + `ShootEvent.Pre/Post` 承担 | **语义差异极大** |
| 8 | `SoundManager.sendSoundToNearby` | `SoundTool.playDistantSound(...)`（最接近）；另有 `playLocalSound` 系列 | **存在但语义不同** |
| 9 | `EntityKineticBullet` | `entity.projectile.ProjectileEntity`（`superbwarfare:projectile`） | **存在且等价**（Builder 风格 API 更好用） |
| 10 | `GunAnimationStateContext` | **未找到**独立类；`GunGeoItem.animationPredicate(AnimationState)` + `event.ClientEventHandler` 的 public static 字段 | **语义差异极大**（全局可变状态） |
| 11 | `GunHudOverlay` | **未找到**单一类；拆为 `AmmoCountOverlay` / `AmmoBarOverlay` / `CrossHairOverlay` / `HeatBarOverlay` … 共 22 个 `LayeredDraw.Layer` | **语义差异极大**（但注入更容易：`RegisterGuiLayersEvent`） |
| 12 | `GunSmithTableResult` | **未找到** | **不存在** |
| 13 | `RawGunTableResult` | **未找到** | **不存在** |
| 14 | 枪械合成配方类型 + 序列化器 | **未找到**；改用原版 `minecraft:smithing_transform`（蓝图 + 材料包 + 附加材料） | **语义差异极大** |
| 15 | 从配方结果读 GunId（accessor） | **不需要**；结果就是 `superbwarfare:<gun_id>` 物品 | **不存在该需求** |
| 16 | 枪械工作台（Gun Smith Table） | **未找到**；只有「锻造台 `ReforgingTableBlock`」（装/升 Perk）+「载具装配台」 | **不存在** |
| 17 | `IAmmo` / `IAmmoBackpack` 之类接口 | **未找到**；改用 `PlayerVariable.ammo`（data attachment）+ `AmmoBoxItem`（弹药盒） | **语义差异极大** |
| 18 | 按口径过滤背包弹药 | `InventoryTool.countAmmoItem/consumeAmmoItem(handler, Ammo)` + `AmmoConsumer#isAmmoItem` | **存在但按「类别」而非「口径」** |
| 19 | 公共 API 包（`api.item` 等） | `api/` **只有 `api.event`**；其余能力散落在 `data.gun` / `item.gun` / `tools` / `client.*`（未标注 `@ApiStatus`） | **语义差异极大**（无稳定接口层） |
| 20 | 数据驱动定义（gun data pack） | **存在**：`data/superbwarfare/sbw/guns/<id>.json`（`DefaultGunData`）+ `assets/superbwarfare/sbw/guns/<id>.json`（`DefaultGunResource`） | **存在且等价**（字段名完全不同） |
| 21 | 弹药消耗声明 | `AmmoType` 数组，元素 `AmmoConsumer`，`"Ammo"` 字段语法 `@RifleAmmo` / `infinity` / `fe` / `物品id{snbt}` / `N @Type` | **全新机制** |
| 22 | 客户端动画名 | `GunAnimation`（JSON）→ `Idle`/`Fire`/`Reload`/`ReloadNormal`/`ReloadEmpty`/`Prepare`/`Iterative`/`Finish`/`Edit`/`Bolt`/`Run`/`Sprint`/`Melee` | **存在且等价** |
| 23 | 渲染基类 | `CustomGunRenderer`（extends GeckoLib `GeoItemRenderer`）+ `CustomGunModel`（extends `GeoModel`） | **不是等价物**：TaC 用内置骨骼渲染，SBW 依赖 **GeckoLib**（Bedrock 模型/动画） |
| 24 | 配件/Perk 系统 | `AttachmentType`（NBT `Attachments`）+ `Perk`/`PerkInstance`（NBT `Perks`，三类 AMMO/FUNCTIONAL/DAMAGE） | **存在，机制不同**（Perk 是 SBW 特色） |
| 25 | 能量武器 | `EnergyStorageItem` + `ModDataComponents.ENERGY`（`superbwarfare:energy`，int）+ `AmmoConsumer.AmmoConsumeType.ENERGY` | SBW 新增能力 |

---

## 9. 迁移决策要点（按风险排序）

1. **口径体系必须重设计喵~**。TaC 的「AmmoId = 口径 = 独立物品」在 SBW 中**完全不存在**，SBW 只有 5 个 `Ammo` 枚举。若你的模组依赖 `9mm`/`556`/`762` 这种细粒度，必须在自己的模组里新建物品 + data component 自行维护，SBW 不提供挂载点。

2. **`GunId` 消失，改用物品注册名字符串喵~**。所有按 `GunId` 索引的数据表、存档字段、网络包都要改为 `String`（`superbwarfare:ak_47` 形式），并注意 `GunData.getRegistryId` 是从 `getDescriptionId()` 推导出来的（意味着**枪械物品的注册名不可随意更改**，改了就断档）。

3. **`ModernKineticGunScriptAPI` 无对应物，改用事件喵~**。开火干预用 `ShootEvent.Pre`（可读 `ShootParameters`，`Pre` 未实现 `ICancellableEvent`，**不能取消，只能通过修改 `GunData` 影响结果**）；换弹干预用 `ReloadEvent.Pre/Post`；命中干预用 `ProjectileHitEvent.HitEntity/HitBlock`（**可取消**）。

4. **`GunHudOverlay` 拆成 22 个 Layer，注入反而更简单喵~**。监听 `RegisterGuiLayersEvent`，用 `registerBelowAll(ID, layer)` / `registerBelow(ID, ...)` / `registerAbove(...)` 插到 `AmmoCountOverlay.ID`(`superbwarfare:ammo_count`)、`CrossHairOverlay.ID` 等附近即可。

5. **枪械合成不是「工作台」而是「原版锻造台 + 蓝图」喵~**。迁移枪械获取途径时，产物是标准 `minecraft:smithing_transform` 配方，你不需要写任何自定义 `Recipe`；只要保证 `result` 指向 `superbwarfare:<gun_id>`。

6. **`api` 包极薄，别指望稳定接口层喵~**。除 `api.event` 的 6 个事件类外，其余「API」（`GunData`、`Ammo`、`InventoryTool`、`SoundTool`）都在普通包里、**未标注 `@ApiStatus`**，跨版本升级风险自担。

7. **GeckoLib 是硬依赖喵~**。所有枪械必须 `extends GunGeoItem` 并实现 `GeoItem`，模型/动画走 Bedrock geo + animation json；若你的模组有自己的枪械渲染，需要按 `CustomGunRenderer` + `CustomGunModel` 的形制改写。

8. **想拿到真实 JSON 原文，需要从 jar 提取喵~**。本源码树**不含** `data/superbwarfare/sbw/guns/*.json`。请从 `superbwarfare-<version>.jar` 中解出 `data/superbwarfare/sbw/guns/` 与 `data/superbwarfare/recipe/`，即可获得真实的枪械数据 JSON 与配方 JSON —— 结构已由本报告第 2.6 / 2.7 / 5.2 节的序列化注解与 datagen 源码**准确推断**。

---

## 附录 A：`Ammo` 枚举完整源码（`data/gun/Ammo.java`）

```java
public enum Ammo {
    HANDGUN(ChatFormatting.GREEN,       ModItems.HANDGUN_AMMO::get),
    RIFLE  (ChatFormatting.AQUA,        ModItems.RIFLE_AMMO::get),
    SHOTGUN(ChatFormatting.RED,         ModItems.SHOTGUN_AMMO::get),
    SNIPER (ChatFormatting.GOLD,        ModItems.SNIPER_AMMO::get),
    HEAVY  (ChatFormatting.LIGHT_PURPLE, ModItems.HEAVY_AMMO::get);

    public final String translationKey;        // item.superbwarfare.ammo.<name>
    public final String serializationName;     // HandgunAmmo / RifleAmmo / ...
    public final String name;                  // handgun / rifle / ...
    public final String displayName;           // "Handgun Ammo" / ...
    public final Supplier<Item> defaultItemSupplier;
    public final ChatFormatting color;
    public DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> dataComponent;
}
```

## 附录 B：`GunData` NBT 键名全清单（`data/gun/GunData.java:78-119`）

| NBT 路径 | 类型 | 含义 |
|---|---|---|
| `GunData.Override` | String | 属性覆盖表达式 |
| `GunData.SelectedAmmoType` | int | 当前选中的 `AmmoConsumer` 索引 |
| `GunData.SelectedFireMode` | int | 当前射击模式索引（默认 0） |
| `GunData.FireIndex` | int | 当前枪口位置索引（默认 0） |
| `GunData.Ammo` | int | **枪内弹药** |
| `GunData.VirtualAmmo` | int | 虚拟弹药（过载余数） |
| `GunData.BackupAmmoCount` | int | 备弹数量覆盖 |
| `GunData.AmmoSlot` | CompoundTag（`String → int[2]{ammo, virtualAmmo}`） | 多弹种切换时的分槽缓存 |
| `GunData.BurstAmount` | int | 剩余连发数 |
| `GunData.Level` / `GunData.Exp` | int / double | Perk 升级等级 / 经验 |
| `GunData.IsEmpty` / `CloseHammer` / `CloseStrike` / `Stopped` / `ForceStop` / `HoldOpen` / `HideBulletChain` / `OverHeat` / `Zooming` | boolean | 各类枪械状态标志 |
| `GunData.LoadIndex` / `Sensitivity` / `ShootAnimationTimer` / `ShootTimer` | int | 装填/灵敏度/动画计时 |
| `GunData.Heat` | double | 热量（0-100） |
| `GunData.UUID` | UUID | 枪械实例唯一标识（由 `GunItem.init` 写入） |
| `Perks` | CompoundTag | 各类 Perk（AMMO / FUNCTIONAL / DAMAGE） |
| `Attachments` | CompoundTag | 配件等级（SCOPE / BARREL / MAGAZINE / STOCK / GRIP） |

`AmmoSlot` 内部键名常量：`public static final String AMMO_SLOT = "AmmoSlot";`（`data/gun/subdata/AmmoSlot.java:6`）。

## 附录 C：全部 data component id（`component/ModDataComponents.java`）

| 常量 | 注册名（完整 id） | 类型 |
|---|---|---|
| `FIRING_PARAMETERS` | `superbwarfare:firing_parameters` | `FiringParameters.Parameters`（pos / radius / is_depressed） |
| `ENERGY` | `superbwarfare:energy` | int |
| `TRANSCRIPT_SCORE` | `superbwarfare:transcript_score` | `List<Pair<Integer, Double>>` |
| `AMMO_BOX_INFO` | `superbwarfare:ammo_box_info` | `AmmoBoxInfo(type, is_drop)` |
| `DOG_TAG_IMAGE` | `superbwarfare:dog_tag_image` | `List<Short>` |
| `Ammo.*.dataComponent` | `superbwarfare:ammo_handgun` / `ammo_rifle` / `ammo_shotgun` / `ammo_sniper` / `ammo_heavy` | int（**运行时动态注册**） |

---

*报告结束。所有结论均基于 `C:\Projects\Sources-1.21.1\com\atsuishio\superbwarfare` 源码逐文件阅读，未做任何文件修改喵~*
