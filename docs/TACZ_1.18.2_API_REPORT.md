# TaC（Timeless and Classics Guns）1.18.2 API 精确形态报告

调研对象：`C:\Projects\Sources-1.18.2\com\tacz\guns`（Forge 1.18.2 版 TaC 源码，共 631 个 `.java` 文件）
调研方式：只读。未修改任何文件。
用途：与 NeoForge 1.21.1 的替代实现 Superb Warfare 做 API 差异对照。

> 重要前提：本报告严格区分 **「API 自身的能力」** 与 **「外部模组（`misc_twf`）依赖它的实际用法」**。
> 外部用法证据来自本工作区已有的消费方代码 `src/main/java/com/hexagram2021/misc_twf/mixin/tacz/`。
> 源码树中 **不含任何 `.json` 资源文件**（`Get-ChildItem -Include *.json` 计数为 0），因此所有 JSON 格式均由反序列化代码反推，并已标注。

---

## 0. 结论摘要：对差异对照最关键的 6 个事实

这 6 条是移植/对照时最容易踩坑的地方，先单独列出。

| # | 事实 | 依据 |
| --- | --- | --- |
| 1 | **`com.tacz.guns.api.item.gun.AbstractGunItem` 位于 `api.item.gun` 而非 `api.item`** | 文件路径 `api/item/gun/AbstractGunItem.java` |
| 2 | **弹药物品只有唯一一个：`tacz:ammo`**。不存在 `ModItems.AMMO_9MM` 之类按口径拆分的字段；口径完全由 NBT `AmmoId` + 数据包索引决定 | `init/ModItems.java:20` |
| 3 | **`AbstractGunItem` 本身不实现任何 NBT 访问器**。NBT 读写全部在 `GunItemDataAccessor` 中，由 `ModernKineticGunItem extends AbstractGunItem implements GunItemDataAccessor` 组合而成 | `api/item/gun/AbstractGunItem.java:42`、`item/ModernKineticGunItem.java:49` |
| 4 | **`IGunOperator` 由 TaC 自己的 `LivingEntityMixin` 织入 `LivingEntity`**，外部模组不能自己实现它，只能用 `IGunOperator.fromLivingEntity(entity)` 强转获取 | `mixin/common/LivingEntityMixin.java:27` |
| 5 | **`SoundManager.sendSoundToNearby` 完全不吸引怪物**。它只向「附近玩家」发 `ServerMessageSound` 包并在客户端播放，服务端没有任何 Mob AI/Anger/Target 逻辑。消费方模组是在该方法的 **调用点** 注入来实现自己的吸引效果 | `sound/SoundManager.java:102-111`；全库 grep `Mob\|setTarget\|HurtByTarget\|attract\|scare` 无相关命中 |
| 6 | **GunId 的 NBT 键是字符串字面量 `"GunId"`，口径键是 `"AmmoId"`**；两者都是 `TAG_STRING`，值为 `ResourceLocation#toString()`。消费方模组直接硬编码了 `"GunId"` | `api/item/nbt/GunItemDataAccessor.java:24`、`api/item/nbt/AmmoItemDataAccessor.java:17`、`ModernKineticGunScriptAPIMixin.java:103` |

命名空间常量：`GunMod.MOD_ID = "tacz"`（`GunMod.java:23`）。

---

## 1. `com.tacz.guns.api.item.IAmmo`

- 包名：`com.tacz.guns.api.item`
- 文件：`api/item/IAmmo.java`（44 行）
- 类型：`public interface`

### 1.1 接口自身能力

| 成员 | 签名 | 说明 |
| --- | --- | --- |
| 静态判定 | `@Nullable static IAmmo getIAmmoOrNull(@Nullable ItemStack stack)` | `stack.getItem() instanceof IAmmo` 则返回强转实例，否则 `null`；`stack == null` 也返回 `null` |
| 取口径 | `ResourceLocation getAmmoId(ItemStack ammo)` | 获取弹药 ID |
| 设口径 | `void setAmmoId(ItemStack ammo, @Nullable ResourceLocation ammoId)` | 设置弹药 ID |
| 判定归属 | `boolean isAmmoOfGun(ItemStack gun, ItemStack ammo)` | 该弹药是否属于这把枪 |

**「一个 ItemStack 是不是弹药」的判定方式正是 `stack.getItem() instanceof IAmmo`** —— 没有独立的 `isAmmo()` 方法：

```java
@Nullable
static IAmmo getIAmmoOrNull(@Nullable ItemStack stack) {
    if (stack == null) {
        return null;
    }
    if (stack.getItem() instanceof IAmmo iAmmo) {
        return iAmmo;
    }
    return null;
}
```

### 1.2 实现链

```java
// item/AmmoItem.java:34
public class AmmoItem extends Item implements AmmoItemDataAccessor
// api/item/nbt/AmmoItemDataAccessor.java:16
public interface AmmoItemDataAccessor extends IAmmo
```

`AmmoItemDataAccessor` 提供三个 `default` 实现，是 NBT 的真实落点：

```java
String AMMO_ID_TAG = "AmmoId";

@Override
@Nonnull
default ResourceLocation getAmmoId(ItemStack ammo) {
    CompoundTag nbt = ammo.getOrCreateTag();
    if (nbt.contains(AMMO_ID_TAG, Tag.TAG_STRING)) {
        ResourceLocation gunId = ResourceLocation.tryParse(nbt.getString(AMMO_ID_TAG));
        return Objects.requireNonNullElse(gunId, DefaultAssets.EMPTY_AMMO_ID);
    }
    return DefaultAssets.EMPTY_AMMO_ID;
}

@Override
default void setAmmoId(ItemStack ammo, @Nullable ResourceLocation ammoId) {
    CompoundTag nbt = ammo.getOrCreateTag();
    if (ammoId != null) {
        nbt.putString(AMMO_ID_TAG, ammoId.toString());
        return;
    }
    nbt.putString(AMMO_ID_TAG, DefaultAssets.DEFAULT_AMMO_ID.toString());
}

@Override
default boolean isAmmoOfGun(ItemStack gun, ItemStack ammo) {
    if (gun.getItem() instanceof IGun iGun && ammo.getItem() instanceof IAmmo iAmmo) {
        ResourceLocation gunId = iGun.getGunId(gun);
        ResourceLocation ammoId = iAmmo.getAmmoId(ammo);
        return TimelessAPI.getCommonGunIndex(gunId)
                .map(gunIndex -> gunIndex.getGunData().getAmmoId().equals(ammoId)).orElse(false);
    }
    return false;
}
```

注意两处语义差异：
- `getAmmoId` 解析失败/缺失 → 返回 `EMPTY_AMMO_ID`（`tacz:empty`），**不是**默认口径。
- `setAmmoId(ammo, null)` → 写入 **`DEFAULT_AMMO_ID`（`tacz:762x39`）**，而不是清空 NBT。

### 1.3 外部模组的典型用法

**用法 A —— 类型判定（最常用）**，`common/menu/slot/BulletSlotItemHandler.java:30`：

```java
public static boolean isValid(ItemStack stack) {
    return stack.getItem() instanceof IAmmo;
}
```

**用法 B —— 遍历容器筛选「属于当前枪」的弹药**，`mixin/tacz/RawGunTableResultAccess.java` 之外的 5 个 mixin 全部采用同一模式：

```java
if (ammoStack.getItem() instanceof IAmmo iAmmo && iAmmo.isAmmoOfGun(gunItem, ammoStack)) { ... }
```

出现位置：`AbstractGunItemMixin.java:36`、`GunAnimationStateContextMixin.java:47`、`GunHudOverlayMixin.java:37`、`ModernKineticGunScriptAPIMixin.java:54` 与 `:93`。

**未被外部模组使用的成员**：`IAmmo.getIAmmoOrNull`、`IAmmo.setAmmoId` 在本工作区消费方代码中无直接调用。

---

## 2. `com.tacz.guns.api.item.gun.AbstractGunItem`

- 包名：`com.tacz.guns.api.item.gun`（**不是** `api.item`）
- 文件：`api/item/gun/AbstractGunItem.java`（314 行，工具报告为 339 行含注释）
- 类型：`public abstract class AbstractGunItem extends Item implements IGun`

### 2.1 类结构

`AbstractGunItem` = 「枪械**行为**骨架」，不含 NBT。类 Javadoc 在 `IGun` 上明确说明：

```java
/**
 * 这里不包含枪械的逻辑，只包含枪械的各种 nbt 访问。<br>
 * 你可以在 {@link AbstractGunItem} 看到枪械逻辑
 */
public interface IGun {
```

因此完整枪械类必须 **双继承**（类 + 接口）：

```java
// item/ModernKineticGunItem.java:49
public class ModernKineticGunItem extends AbstractGunItem implements GunItemDataAccessor {
    public static final String TYPE_NAME = "modern_kinetic";
```

`TYPE_NAME` 是 `GunIndexPOJO.item_type` 的默认值（`resource/pojo/GunIndexPOJO.java:26`），用于把 GunId 映射回具体物品。

### 2.2 抽象方法（外部继承/覆写的核心扩展点）

全部为 `public abstract`，共 8 个：

```java
public abstract boolean startBolt(ShooterDataHolder dataHolder, ItemStack gunItem, LivingEntity shooter);
public abstract boolean tickBolt(ShooterDataHolder dataHolder, ItemStack gunItem, LivingEntity shooter);
public abstract void shoot(ShooterDataHolder dataHolder, ItemStack gunItem, Supplier<Float> pitch, Supplier<Float> yaw, LivingEntity shooter);
public abstract boolean startReload(ShooterDataHolder dataHolder, ItemStack gunItem, LivingEntity shooter);
public abstract ReloadState tickReload(ShooterDataHolder dataHolder, ItemStack gunItem, LivingEntity shooter);
public abstract void interruptReload(ShooterDataHolder dataHolder, ItemStack gunItem, LivingEntity shooter);
public abstract void fireSelect(ShooterDataHolder dataHolder, ItemStack gunItem);
public abstract void melee(ShooterDataHolder dataHolder, LivingEntity user, ItemStack gunItem);
```

**没有** `abstract` 的射击/换弹相关实体方法，全部为具体实现：

| 方法 | 签名 | 语义 |
| --- | --- | --- |
| 换弹前置检查 | `public boolean canReload(LivingEntity shooter, ItemStack gunItem)` | 弹匣是否已满 + 背包是否有可用 `IAmmo` / `IAmmoBox`；考虑 `useDummyAmmo` |
| 卸空弹匣 | `@Override public void dropAllAmmo(Player player, ItemStack gunItem)` | 退还至背包（含虚拟备弹逻辑），不退枪膛弹；源码内有 `//TODO` 注明对象应为 `LivingEntity` |
| 从容器扣弹 | `public int findAndExtractInventoryAmmos(IItemHandler itemHandler, ItemStack gunItem, int needAmmoCount)` | 返回实际取得数量；同时处理 `IAmmoBox` |
| 扣虚拟备弹 | `public int findAndExtractDummyAmmo(ItemStack gunItem, int needAmmoCount)` | |
| 配件白名单 | `@Override public boolean allowAttachment(ItemStack gun, ItemStack attachmentItem)` | 走 `AllowAttachmentTagMatcher.match(gunId, attachmentId)` |
| 配件类型白名单 | `@Override public boolean allowAttachmentType(ItemStack gun, AttachmentType type)` | 读 `GunData.getAllowAttachments()` |
| 显示名 | `@Override @OnlyIn(Dist.CLIENT) public Component getName(ItemStack stack)` | 走 `ClientGunIndex.getName()` |
| 阻止挥手 | `@Override public boolean onEntitySwing(ItemStack stack, LivingEntity entity)` | 恒返回 `true` |
| 客户端渲染属性 | `@Override public void initializeClient(Consumer<IItemRenderProperties> consumer)` | 注册 `GunItemRenderer` |
| Tooltip 图 | `@Override public Optional<TooltipComponent> getTooltipImage(ItemStack stack)` | 返回 `GunTooltip` |
| 创造栏填充 | `public static NonNullList<ItemStack> fillItemCategory(GunTabType type)` | 静态 |

`canReload` 内部有 **4 个 return 点**，这是外部 mixin 定位的关键（见 2.3）：

```java
public boolean canReload(LivingEntity shooter, ItemStack gunItem) {
    ResourceLocation gunId = this.getGunId(gunItem);
    CommonGunIndex gunIndex = TimelessAPI.getCommonGunIndex(gunId).orElse(null);
    if (gunIndex == null) {                       // return #1 (false)
        return false;
    }
    int currentAmmoCount = getCurrentAmmoCount(gunItem);
    int maxAmmoCount = AttachmentDataUtils.getAmmoCountWithAttachment(gunItem, gunIndex.getGunData());
    if (currentAmmoCount >= maxAmmoCount) {       // return #2 (false)
        return false;
    }
    if (useDummyAmmo(gunItem)) {
        return getDummyAmmoAmount(gunItem) > 0;   // return #3
    }
    return shooter.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).map(cap -> {
        // 背包检查 ... return true / false   // return #4
    }).orElse(false);
}
```

`shoot` 的具体实现（`ModernKineticGunItem.java:96`）展示了「Lua 脚本优先，否则走默认 API」的范式：

```java
@Override
public void shoot(ShooterDataHolder dataHolder, ItemStack gunItem, Supplier<Float> pitch, Supplier<Float> yaw, LivingEntity shooter) {
    ModernKineticGunScriptAPI api = new ModernKineticGunScriptAPI();
    api.setItemStack(gunItem);
    api.setShooter(shooter);
    api.setDataHolder(dataHolder);
    api.setPitchSupplier(pitch);
    api.setYawSupplier(yaw);
    CommonGunIndex gunIndex = api.getGunIndex();
    if (gunIndex == null) { return; }
    Optional.ofNullable(gunIndex.getScript())
            .map(script -> checkFunction(script.get("shoot")))
            .ifPresentOrElse(
                    func -> func.call(CoerceJavaToLua.coerce(api)),
                    ()   -> api.shootOnce(api.isShootingNeedConsumeAmmo()));
}
```

### 2.3 外部模组的典型用法

**唯一用法：`@Inject` 到 `canReload` 的第 4 个 RETURN，追加对第三方背包（旅行者背包）的弹药检查。**

`mixin/tacz/AbstractGunItemMixin.java`（工作区 `src/main/java/com/hexagram2021/misc_twf/mixin/tacz/AbstractGunItemMixin.java`）：

```java
@Mixin(value = AbstractGunItem.class, remap = false)
public class AbstractGunItemMixin {
	@SuppressWarnings("ConstantConditions")
	@Inject(method = "canReload", at = @At(value = "RETURN", ordinal = 3), cancellable = true)
	private void misc_twf$checkTravelersBackpackTacSlot(LivingEntity shooter, ItemStack gunItem, CallbackInfoReturnable<Boolean> cir) {
		if(cir.getReturnValue()) {
			return;
		}
		if(shooter instanceof Player player) {
			ITravelersBackpack backpack = CapabilityUtils.getCapability(player).orElse(null);
			// ... 遍历背包弹药槽，用 IAmmo.isAmmoOfGun 判定后 cir.setReturnValue(true)
		}
	}
}
```

要点：
- `remap = false`（TaC 类非 MC 原生，无需混淆重映射）。
- `ordinal = 3` 对应 `canReload` 里第 4 个 `RETURN`（即 `capability.map(...)` 的 `orElse(false)` 出口）。**`canReload` 的 return 点数量/顺序是硬依赖**，1.21.1 侧若结构变化会直接导致注入失败。
- 外部模组**没有覆写** `shoot` / `startReload` / `tickReload` 等抽象方法（本工作区未出现 `extends AbstractGunItem` 的自定义枪械类）。

---

## 3. `com.tacz.guns.api.item.builder.AmmoItemBuilder`

- 包名：`com.tacz.guns.api.item.builder`
- 文件：`api/item/builder/AmmoItemBuilder.java`（37 行，全文件如下）

```java
package com.tacz.guns.api.item.builder;

import com.tacz.guns.api.DefaultAssets;
import com.tacz.guns.api.item.IAmmo;
import com.tacz.guns.init.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public final class AmmoItemBuilder {
    private int count = 1;
    private ResourceLocation ammoId = DefaultAssets.DEFAULT_AMMO_ID;

    private AmmoItemBuilder() {
    }

    public static AmmoItemBuilder create() {
        return new AmmoItemBuilder();
    }

    public AmmoItemBuilder setCount(int count) {
        this.count = Math.max(count, 1);
        return this;
    }

    public AmmoItemBuilder setId(ResourceLocation id) {
        this.ammoId = id;
        return this;
    }

    public ItemStack build() {
        ItemStack ammo = new ItemStack(ModItems.AMMO.get(), this.count);
        if (ammo.getItem() instanceof IAmmo iAmmo) {
            iAmmo.setAmmoId(ammo, this.ammoId);
        }
        return ammo;
    }
}
```

| 项 | 精确形态 |
| --- | --- |
| 类修饰符 | `public final` |
| 构造方法 | `private AmmoItemBuilder()` —— 不可直接 `new` |
| 静态工厂 | `public static AmmoItemBuilder create()` |
| 数量 | `setCount(int)`，内部 `Math.max(count, 1)`，**下界为 1，不能造 0 个** |
| 口径 | `setId(ResourceLocation)`；字段默认 `DefaultAssets.DEFAULT_AMMO_ID` = `tacz:762x39` |
| `build()` 返回 | `ItemStack`（`ModItems.AMMO.get()`，即 `tacz:ammo`），并通过 `IAmmo.setAmmoId` 写入 `AmmoId` |
| 链式 | 所有 setter 返回 `this` |

**注意：`build()` 不校验 `ammoId` 是否在 `CommonAmmoIndex` 中存在**，可以构造出任意口径 id 的弹药物品（静默无效）。

### 外部模组的典型用法

`common/register/MISCTWFRecipeBookTypes.java:25,33-35`：

```java
private static final ResourceLocation LOGO_BULLETS = ResourceLocation.fromNamespaceAndPath("tacz", "9mm");

public static final EnumProxy<RecipeBookCategories> MISC_TWF_RECOVER_FURNACE_BULLETS = new EnumProxy<>(
        RecipeBookCategories.class, AmmoItemBuilder.create().setCount(1).setId(LOGO_BULLETS).build()
);
```

即：用 `tacz:9mm` 造一个弹药栈作为配方书分类图标。这是 `AmmoItemBuilder` 在外部唯一的用法。

TaC 自身内部用法（`api/item/gun/AbstractGunItem.java:174`，退还弹药）：

```java
ItemStack ammoItem = AmmoItemBuilder.create().setId(ammoId).setCount(count).build();
ItemHandlerHelper.giveItemToPlayer(player, ammoItem);
```

---

## 4. `com.tacz.guns.init.ModItems`

- 包名：`com.tacz.guns.init`
- 文件：`init/ModItems.java`（38 行）
- 注册方式：Forge 1.18.2 `DeferredRegister`

```java
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, GunMod.MOD_ID);

    public static RegistryObject<ModernKineticGunItem> MODERN_KINETIC_GUN = ITEMS.register("modern_kinetic_gun", ModernKineticGunItem::new);
    public static RegistryObject<Item> AMMO = ITEMS.register("ammo", AmmoItem::new);
    public static RegistryObject<Item> ATTACHMENT = ITEMS.register("attachment", AttachmentItem::new);

    public static RegistryObject<GunSmithTableItem> GUN_SMITH_TABLE = ITEMS.register("gun_smith_table", () -> new DefaultTableItem(ModBlocks.GUN_SMITH_TABLE.get()));
    public static RegistryObject<GunSmithTableItem> WORKBENCH_111 = ITEMS.register("workbench_a", () -> new GunSmithTableItem(ModBlocks.WORKBENCH_111.get()));
    public static RegistryObject<GunSmithTableItem> WORKBENCH_211 = ITEMS.register("workbench_b", () -> new GunSmithTableItem(ModBlocks.WORKBENCH_211.get()));
    public static RegistryObject<GunSmithTableItem> WORKBENCH_121 = ITEMS.register("workbench_c", () -> new GunSmithTableItem(ModBlocks.WORKBENCH_121.get()));

    public static RegistryObject<Item> TARGET = ITEMS.register("target", () -> new BlockItem(ModBlocks.TARGET.get(), new Item.Properties()));
    public static RegistryObject<Item> STATUE = ITEMS.register("statue", () -> new BlockItem(ModBlocks.STATUE.get(), new Item.Properties()));
    public static RegistryObject<Item> AMMO_BOX = ITEMS.register("ammo_box", AmmoBoxItem::new);
    public static RegistryObject<Item> TARGET_MINECART = ITEMS.register("target_minecart", TargetMinecartItem::new);

    @SubscribeEvent
    public static void onItemRegister(RegistryEvent.Register<Item> event) {
        GunItemManager.registerGunItem(ModernKineticGunItem.TYPE_NAME, MODERN_KINETIC_GUN);
        ModCreativeTabs.initCreativeTabs();
    }
}
```

### 字段清单（全量，共 11 个）

| 字段名 | 类型 | 注册名（RL） | 弹药相关 |
| --- | --- | --- | --- |
| `MODERN_KINETIC_GUN` | `RegistryObject<ModernKineticGunItem>` | `tacz:modern_kinetic_gun` | 否（枪） |
| **`AMMO`** | **`RegistryObject<Item>`** | **`tacz:ammo`** | **是** |
| `ATTACHMENT` | `RegistryObject<Item>` | `tacz:attachment` | 否（配件） |
| **`AMMO_BOX`** | **`RegistryObject<Item>`** | **`tacz:ammo_box`** | **是** |
| `GUN_SMITH_TABLE` | `RegistryObject<GunSmithTableItem>` | `tacz:gun_smith_table` | 否 |
| `WORKBENCH_111` | `RegistryObject<GunSmithTableItem>` | `tacz:workbench_a` | 否 |
| `WORKBENCH_211` | `RegistryObject<GunSmithTableItem>` | `tacz:workbench_b` | 否 |
| `WORKBENCH_121` | `RegistryObject<GunSmithTableItem>` | `tacz:workbench_c` | 否 |
| `TARGET` | `RegistryObject<Item>` | `tacz:target` | 否 |
| `STATUE` | `RegistryObject<Item>` | `tacz:statue` | 否 |
| `TARGET_MINECART` | `RegistryObject<Item>` | `tacz:target_minecart` | 否 |

### 关键否定结论

**不存在 `AMMO_9MM`、`AMMO_762X39`、`AMMO_556X45` 等按口径拆分的字段。** 全部口径共用一个 `AMMO` 物品，靠 NBT `AmmoId` 区分。搜索 `9mm|762x39|556x45|45acp` 在整个 `com/tacz/guns` 下仅命中 1 处：`DefaultAssets.DEFAULT_AMMO_ID` 的 `762x39`（`api/DefaultAssets.java:10`）。`9mm` 在 TaC Java 源码中**完全不出现**，它是数据包里的弹药索引 id。

同时注意 `ModItems` 里所有字段**都不是 `final`**（`DeferredRegister` 惯例），且均为 `public static`。

### 外部模组的典型用法

`common/register/MISCTWFRecipeBookTypes.java:39,49-57`：

```java
public static final EnumProxy<RecipeBookCategories> MISC_TWF_RECOVER_FURNACE_MISC = new EnumProxy<>(new ItemStack(ModItems.STATUE.get()));
// ...
if(itemStack.is(ModItems.MODERN_KINETIC_GUN.get())) {
    return MISC_TWF_RECOVER_FURNACE_GUNS;
}
if(itemStack.is(ModItems.AMMO.get())) {
    return MISC_TWF_RECOVER_FURNACE_BULLETS;
}
if(itemStack.is(ModItems.ATTACHMENT.get())) {
    return MISC_TWF_RECOVER_FURNACE_ATTACHMENTS;
}
```

用法特征：`ModItems.XXX.get()` 取 `Item`，再 `ItemStack#is(Item)` 做物品分类。用到了 `MODERN_KINETIC_GUN` / `AMMO` / `ATTACHMENT` / `STATUE` 四个字段。

> 移植提示：此文件在 1.21.1 侧的注释写明「回收炉配方书分类静态 import com.tacz.guns.*，E2：编译+运行依赖」——即该处编译期硬依赖 TaC 命名空间。

---

## 5. `com.tacz.guns.api.entity.IGunOperator`

- 包名：`com.tacz.guns.api.entity`
- 文件：`api/entity/IGunOperator.java`（150 行，工具报告 180 行含注释）
- 类型：`public interface`
- 作用：**实体侧枪械操作状态机门面**。聚合「射击/换弹/拉栓/切枪/瞄准/匍匐/近战」的服务端入口，加上从服务端同步到客户端的只读状态。

### 5.1 接口自身能力

**获取入口（唯一方式）** —— 依赖 TaC 自己的 mixin 织入：

```java
/**
 * LivingEntity 通过 Mixin 的方式实现了这个接口
 */
static IGunOperator fromLivingEntity(LivingEntity entity) {
    return (IGunOperator) entity;
}
```

织入证据 `mixin/common/LivingEntityMixin.java:26-40`：

```java
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements IGunOperator, KnockBackModifier {
    private final @Unique LivingEntity tacz$shooter = (LivingEntity) (Object) this;
    private final @Unique ShooterDataHolder tacz$data = new ShooterDataHolder();
    private final @Unique LivingEntityDrawGun tacz$draw = new LivingEntityDrawGun(tacz$shooter, tacz$data);
    private final @Unique LivingEntityShoot tacz$shoot = new LivingEntityShoot(tacz$shooter, this.tacz$data, this.tacz$draw);
    // ...
}
```

**同步只读状态（7 个 getter）**

| 方法 | 返回 | 语义 |
| --- | --- | --- |
| `getSynShootCoolDown()` | `long` | 服务端同步的射击冷却 |
| `getSynMeleeCoolDown()` | `long` | 服务端同步的近战（刺刀）冷却 |
| `getSynDrawCoolDown()` | `long` | 服务端同步的切枪冷却 |
| `getSynIsBolting()` | `boolean` | 服务端同步的手动拉栓状态 |
| `getSynReloadState()` | `ReloadState` | 服务端同步的换弹状态 |
| `getSynAimingProgress()` | `float` | 瞄准进度 |
| `getSynIsAiming()` | `boolean` | 是否正在瞄准（**不等价于 `progress > 0`**） |
| `getSynSprintTime()` | `float` | 持枪奔跑时长，夹在 `[0, sprintTime]` |

**服务端动作入口（写操作）**

```java
void initialData();
void draw(Supplier<ItemStack> itemStackSupplier);
void bolt();
void reload();
void cancelReload();
void fireSelect();
void zoom();
void melee();
ShootResult shoot(Supplier<Float> pitch, Supplier<Float> yaw);
ShootResult shoot(Supplier<Float> pitch, Supplier<Float> yaw, long timestamp);
void aim(boolean isAim);
void crawl(boolean isCrawl);
void updateCacheProperty(AttachmentCacheProperty cacheProperty);
```

**查询/判定**

```java
boolean needCheckAmmo();              // false = 开火不检查弹药（背包与枪内都不查）
boolean consumesAmmoOrNot();          // false = 开火不消耗弹药
boolean getProcessedSprintStatus(boolean sprint);
@Nullable AttachmentCacheProperty getCacheProperty();
ShooterDataHolder getDataHolder();
boolean nextBulletIsTracer(int tracerCountInterval);
```

**相关枚举/类（同包）**

- `api/entity/ShootResult.java`（`enum`，15 个值）：`SUCCESS, UNKNOWN_FAIL, COOL_DOWN, NO_AMMO, NOT_DRAW, NOT_GUN, ID_NOT_EXIST, NEED_BOLT, IS_RELOADING, IS_DRAWING, IS_BOLTING, IS_MELEE, IS_SPRINTING, NETWORK_FAIL, FORGE_EVENT_CANCEL`
- `api/entity/ReloadState.java`（`class`）：字段 `protected StateType stateType`、`protected long countDown`；常量 `NOT_RELOADING_COUNTDOWN = -1`；方法 `getStateType()`、`setStateType()`、`getCountDown()`、`setCountDown()`、`equals()`。内部 `enum StateType`：`NOT_RELOADING, EMPTY_RELOAD_FEEDING, EMPTY_RELOAD_FINISHING, TACTICAL_RELOAD_FEEDING, TACTICAL_RELOAD_FINISHING`，带方法 `isReloadingEmpty()`、`isReloadingTactical()`、`isReloading()`、`isReloadFinishing()`。

### 5.2 外部模组的典型用法

在 `mixin/tacz/ModernKineticGunScriptAPIMixin.java` 中作为**注入方法参数类型**出现（由 `lambda$shootOnce$0` 的签名决定）：

```java
@Inject(method = "lambda$shootOnce$0", at = @At(value = "INVOKE",
        target = "Lcom/tacz/guns/sound/SoundManager;sendSoundToNearby(Lnet/minecraft/world/entity/LivingEntity;ILnet/minecraft/resources/ResourceLocation;Lnet/minecraft/resources/ResourceLocation;Ljava/lang/String;FF)V"))
private void misc_twf$soundAttract(boolean consumeAmmo, GunData gunData, int bulletAmount, BulletData bulletData,
                                   IGunOperator gunOperator, float processedSpeed, float finalInaccuracy,
                                   int soundDistance, boolean useSilenceSound, CallbackInfoReturnable<Boolean> cir) {
    // ...
    if (gunOperator instanceof LivingEntity livingEntity) {
        livingEntity.addEffect(new MobEffectInstance(SonaMobEffects.EXPOSURE.get(), 10, useSilenceSound ? 0 : 3, true, false));
    }
}
```

关键点：**消费方使用的模式是 `gunOperator instanceof LivingEntity`**——因为 `fromLivingEntity` 本身就是无校验强转，反向判断可以安全地拿回实体。

---

## 6. `com.tacz.guns.item.ModernKineticGunScriptAPI`

- 包名：`com.tacz.guns.item`
- 文件：`item/ModernKineticGunScriptAPI.java`（607 行，工具报告 543 行）
- 类型：`public class`，标注 `@SuppressWarnings("unused")`
- 作用：**供 Lua 枪械脚本调用的 API 门面**，把「一次射击/换弹/拉栓」的完整业务编排暴露给脚本；同时也是 `ModernKineticGunItem` 各抽象方法默认实现的委托对象。

公开字段：`public static String MARKER = "ScriptAPI";`（非 `final`）

私有字段：`shooter`、`dataHolder`、`itemStack`、`abstractGunItem`、`gunIndex`、`gunId`、`gunDisplayId`、`pitchSupplier`、`yawSupplier`、`nbtUtil`、`entityAccessor`

### 6.1 射击流程 `shootOnce(boolean consumeAmmo)`

这是整个模组最核心的射击编排，也是外部 mixin 的注入目标。

```java
/**
 * 执行一次完整的射击逻辑，会考虑玩家的状态(是否在瞄准、是否在移动、是否在匍匐等)、配件数值影响、多弹丸散射、连发，播放开火音效、
 * @param consumeAmmo 本次射击是否消耗弹药
 */
public void shootOnce(boolean consumeAmmo){
    GunData gunData = gunIndex.getGunData();
    BulletData bulletData = gunIndex.getBulletData();
    IGunOperator gunOperator = IGunOperator.fromLivingEntity(shooter);

    // 获取配件数据缓存
    AttachmentCacheProperty cacheProperty = gunOperator.getCacheProperty();
    if (cacheProperty == null) { return; }

    // 散射影响
    InaccuracyType inaccuracyType = InaccuracyType.getInaccuracyType(shooter);
    float inaccuracy = Math.max(0, cacheProperty.<Map<InaccuracyType, Float>>getCache(InaccuracyModifier.ID).get(inaccuracyType));
    if (inaccuracyType == InaccuracyType.AIM) {
        inaccuracy = Math.max(0, cacheProperty.<Map<InaccuracyType, Float>>getCache(AimInaccuracyModifier.ID).get(inaccuracyType));
    }
    final float finalInaccuracy = inaccuracy;

    // 消音器影响
    Pair<Integer, Boolean> silence = cacheProperty.getCache(SilenceModifier.ID);
    final int soundDistance = silence.first();
    final boolean useSilenceSound = silence.right();

    // 子弹飞行速度
    float speed = cacheProperty.<Float>getCache(AmmoSpeedModifier.ID);
    float processedSpeed = Mth.clamp(speed / 20, 0, Float.MAX_VALUE);
    // 弹丸数量
    int bulletAmount = Math.max(bulletData.getBulletAmount(), 1);

    // 连发数量
    FireMode fireMode = abstractGunItem.getFireMode(itemStack);
    int cycles = fireMode == FireMode.BURST ? gunData.getBurstData().getCount() : 1;
    // 连发间隔
    long period = fireMode == FireMode.BURST ? gunData.getBurstShootInterval() : 1;

    CycleTaskHelper.addCycleTask(() -> {
        // 如果射击者死亡，取消射击
        if (shooter.isDeadOrDying()) { return false; }
        // 如果武器变了，取消射击
        if (!shooter.getMainHandItem().equals(itemStack) || shooter.getMainHandItem().isEmpty()) { return false; }
        // 触发击发事件
        boolean fire = !MinecraftForge.EVENT_BUS.post(new GunFireEvent(shooter, itemStack, LogicalSide.SERVER));
        if (fire) {
            NetworkHandler.sendToTrackingEntity(new ServerMessageGunFire(shooter.getId(), itemStack), shooter);
            // 削减弹药
            if (consumeAmmo) {
                if (!this.reduceAmmoOnce()) { return false; }
            }
            // 获取射击方向（pitch 和 yaw）
            float pitch = pitchSupplier != null ? pitchSupplier.get() : shooter.getXRot();
            float yaw = yawSupplier != null ? yawSupplier.get() : shooter.getYRot();
            // 生成子弹
            Level world = shooter.getLevel();
            ResourceLocation ammoId = gunData.getAmmoId();
            for (int i = 0; i < bulletAmount; i++) {
                boolean isTracer = bulletData.hasTracerAmmo() && gunOperator.nextBulletIsTracer(bulletData.getTracerCountInterval());
                EntityKineticBullet bullet = new EntityKineticBullet(world, shooter, itemStack, ammoId, gunId, isTracer, gunData, bulletData);
                bullet.shootFromRotation(bullet, pitch, yaw, 0.0F, processedSpeed, finalInaccuracy);
                world.addFreshEntity(bullet);
            }
            // 播放枪声
            if (soundDistance > 0) {
                String soundId = useSilenceSound ? SoundManager.SILENCE_3P_SOUND : SoundManager.SHOOT_3P_SOUND;
                SoundManager.sendSoundToNearby(shooter, soundDistance, gunId, gunDisplayId, soundId, 0.8f, 0.9f + shooter.getRandom().nextFloat() * 0.125f);
            }
        }
        return true;
    }, period, cycles);
}
```

流程顺序（重要）：
1. 取配件缓存，`null` 直接返回
2. 计算散射（瞄准态换用 `AimInaccuracyModifier`）
3. 取消音器 → `soundDistance` + `useSilenceSound`
4. 计算弹速 `speed/20`、弹丸数 `bulletAmount`
5. burst 决定 `cycles` / `period`
6. `CycleTaskHelper.addCycleTask` 投递延迟循环任务（主线程、线程安全、时间粒度取决于 TPS）
7. 任务内：死亡/换枪取消 → `GunFireEvent` → 同步 `ServerMessageGunFire` → **`reduceAmmoOnce()` 扣弹** → 逐发 `new EntityKineticBullet` + `shootFromRotation` + `addFreshEntity`
8. **最后**播放枪声（`sendSoundToNearby`）

**注意：`lambda$shootOnce$0` 这个 lambda 名字与参数表是编译产物，外部 mixin 硬依赖了它。**

### 6.2 弹药消耗 `reduceAmmoOnce()`

```java
/**
 * 让枪械内的子弹减少一发。会遵从栓动、闭膛待击和开膛待机的规律，消耗枪管内子弹或者弹匣内子弹。
 * 如果没有可以消耗的子弹，这个方法会返回 false。
 * @return 是否成功减少子弹。
 */
public boolean reduceAmmoOnce() {
    Bolt boltType = TimelessAPI.getCommonGunIndex(abstractGunItem.getGunId(itemStack))
            .map(index -> index.getGunData().getBolt())
            .orElse(null);
    if (boltType == null) { return false; }
    if (boltType == Bolt.MANUAL_ACTION) {
        if (!abstractGunItem.hasBulletInBarrel(itemStack)) { return false; }
        abstractGunItem.setBulletInBarrel(itemStack, false);
    } else if (boltType == Bolt.CLOSED_BOLT) {
        if (abstractGunItem.getCurrentAmmoCount(itemStack) > 0) {
            abstractGunItem.reduceCurrentAmmoCount(itemStack);
        } else {
            if (!abstractGunItem.hasBulletInBarrel(itemStack)) { return false; }
            abstractGunItem.setBulletInBarrel(itemStack, false);
        }
    } else {
        if (abstractGunItem.getCurrentAmmoCount(itemStack) == 0) { return false; }
        abstractGunItem.reduceCurrentAmmoCount(itemStack);
    }
    return true;
}
```

### 6.3 完整公开方法表

| 方法 | 签名 | 语义 |
| --- | --- | --- |
| `shootOnce` | `void shootOnce(boolean consumeAmmo)` | 完整射击编排（见 6.1） |
| `reduceAmmoOnce` | `boolean reduceAmmoOnce()` | 扣一发，遵循 bolt 类型 |
| `getReloadTime` | `long getReloadTime()` | 换弹已耗时 ms；`reloadTimestamp == -1` 返回 0 |
| `getBoltTime` | `long getBoltTime()` | 拉栓已耗时 ms；未拉栓返回 0 |
| `getShootInterval` | `long getShootInterval()` | 射击间隔 ms，**内部减 5ms 窗口**，`Math.max(coolDown, 0)` |
| `getLastShootTimestamp` | `long getLastShootTimestamp()` | `dataHolder.lastShootTimestamp + dataHolder.baseTimestamp`；切枪重置 -1 |
| `adjustShootInterval` | `void adjustShootInterval(long alpha)` | `dataHolder.shootTimestamp += alpha`；**客户端侧需在状态机里重复一次**（见 `GunAnimationStateContext#adjustClientShootInterval`） |
| `adjustReloadTime` | `void adjustReloadTime(long alpha)` | `reloadTimestamp -= alpha`（正数加快） |
| `adjustBoltTime` | `void adjustBoltTime(long alpha)` | `boltTimestamp -= alpha`（正数加快） |
| `getAimingProgress` | `float getAimingProgress()` | 0~1 |
| `getReloadStateType` | `int getReloadStateType()` | `ReloadState.StateType.ordinal()` |
| `getFireMode` | `int getFireMode()` | `FireMode.ordinal()` |
| `isShootingNeedConsumeAmmo` | `boolean isShootingNeedConsumeAmmo()` | 委托 `consumesAmmoOrNot()` |
| `isReloadingNeedConsumeAmmo` | `boolean isReloadingNeedConsumeAmmo()` | 委托 `needCheckAmmo()` |
| `getNeededAmmoAmount` | `int getNeededAmmoAmount()` | `maxAmmoCount - currentAmmoCount` |
| `getAmmoAmount` | `int getAmmoAmount()` | 当前弹匣备弹 |
| `getMaxAmmoCount` | `int getMaxAmmoCount()` | 含配件扩容 |
| `getMagExtentLevel` | `int getMagExtentLevel()` | 0~3 |
| **`consumeAmmoFromPlayer`** | `int consumeAmmoFromPlayer(int neededAmount)` | 虚拟备弹走 `findAndExtractDummyAmmo`，否则走 `ITEM_HANDLER_CAPABILITY` + `findAndExtractInventoryAmmos`；返回实际消耗量 |
| **`hasAmmoToConsume`** | `boolean hasAmmoToConsume()` | 创造模式直接 `true`；检查虚拟备弹/背包 `IAmmo`/`IAmmoBox` |
| `putAmmoInMagazine` | `int putAmmoInMagazine(int amount)` | 返回溢出的多余子弹 |
| `removeAmmoFromMagazine` | `int removeAmmoFromMagazine(int amount)` | 返回成功移除量 |
| `getAmmoCountInMagazine` | `int getAmmoCountInMagazine()` | |
| `hasAmmoInBarrel` | `boolean hasAmmoInBarrel()` | 开膛待击恒 `false` |
| `setAmmoInBarrel` | `void setAmmoInBarrel(boolean ammoInBarrel)` | |
| `cacheScriptData` | `void cacheScriptData(LuaValue luaValue)` | 存到 `dataHolder.scriptData` |
| `getCachedScriptData` | `LuaValue getCachedScriptData()` | |
| `getScriptParams` | `LuaTable getScriptParams()` | 从 `gunIndex.getScriptParam()`；`null` 时返回空表 |
| `safeAsyncTask` | `void safeAsyncTask(LuaValue value, long delayMs, long periodMs, int cycles)` | 委托 `CycleTaskHelper.addCycleTask`；`cycles = -1` 表示无限 |
| `getCurrentTimestamp` | `long getCurrentTimestamp()` | `System.currentTimeMillis()` |
| `getAttachment` | `String getAttachment(String type)` | `AttachmentType.valueOf` 失败返回 `tacz:empty` |
| `getNbt` | `LuaNbtAccessor getNbt()` | |
| `getEntityUtil` | `LuaEntityAccessor getEntityUtil()` | 懒加载 |
| setter | `setShooter(LivingEntity)`、`setItemStack(ItemStack)`、`setPitchSupplier(Supplier<Float>)`、`setYawSupplier(Supplier<Float>)`、`setDataHolder(ShooterDataHolder)` | 注入依赖 |
| getter | `getShooter()`、`getItemStack()`、`getAbstractGunItem()`、`getGunIndex()` | |
| 包私有 | `ShooterDataHolder getDataHolder()` | 注意**无 `public`** |
| 私有 | `void initGunItem()` | 从 `itemStack` 解析 `gunId`/`gunDisplayId`/`gunIndex`/`abstractGunItem`/`nbtUtil` |

### 6.4 外部模组的典型用法

`mixin/tacz/ModernKineticGunScriptAPIMixin.java` 共 3 处注入：

1. **`consumeAmmoFromPlayer`，`@At("RETURN") ordinal = 1`** —— 在 TaC 从背包扣弹之后再从旅行者背包扣，并 `cir.setReturnValue(原值 + cnt)`：

```java
@Inject(method = "consumeAmmoFromPlayer", at = @At(value = "RETURN", ordinal = 1), cancellable = true)
private void misc_twf$consumeTravelersBackpackTacSlot(int neededAmount, CallbackInfoReturnable<Integer> cir) {
    int stillNeededAmount = neededAmount - cir.getReturnValue();
    if(stillNeededAmount <= 0) { return; }
    // ... 遍历背包，shrink/setStackInSlot，最后
    cir.setReturnValue(cir.getReturnValue() + cnt);
}
```

2. **`hasAmmoToConsume`，`@At("RETURN") ordinal = 2`** —— 追加背包弹药存在性检查。
3. **`lambda$shootOnce$0`，`@At("INVOKE")` 命中 `SoundManager.sendSoundToNearby`** —— 见第 7 节与 5.2 节。

修改者需 `@Shadow private LivingEntity shooter;` 与 `@Shadow private ItemStack itemStack;`。

---

## 7. `com.tacz.guns.sound.SoundManager`

- 包名：`com.tacz.guns.sound`
- 文件：`sound/SoundManager.java`（112 行）
- 类型：`public class`（无构造器声明，隐含 public 无参）

### 7.1 音效 id 常量（全部为 `public static String`，**非 `final`**）

| 常量 | 值 | 语义 |
| --- | --- | --- |
| `SHOOT_SOUND` | `"shoot"` | 射击音效，自己能听见 |
| `SHOOT_3P_SOUND` | `"shoot_3p"` | 其他玩家听到的枪声 |
| `SILENCE_SOUND` | `"silence"` | 消音器音效 |
| `SILENCE_3P_SOUND` | `"silence_3p"` | 其他玩家听到的消音器枪声 |
| `MELEE_BAYONET` | `"melee_bayonet"` | 近战刺刀 |
| `MELEE_PUSH` | `"melee_push"` | 近战推人 |
| `MELEE_STOCK` | `"melee_stock"` | 近战枪托砸人 |
| `DRY_FIRE_SOUND` | `"dry_fire"` | 空击 |
| `RELOAD_EMPTY_SOUND` | `"reload_empty"` | 空仓换弹 |
| `RELOAD_TACTICAL_SOUND` | `"reload_tactical"` | 战术换弹 |
| `INSPECT_EMPTY_SOUND` | `"inspect_empty"` | 空仓检视 |
| `INSPECT_SOUND` | `"inspect"` | 普通检视 |
| `DRAW_SOUND` | `"draw"` | 切枪切入 |
| `PUT_AWAY_SOUND` | `"put_away"` | 切枪切出 |
| `BOLT_SOUND` | `"bolt"` | 拉栓 |
| `FIRE_SELECT` | `"fire_select"` | 切换开火模式 |
| `HEAD_HIT_SOUND` | `"head_hit"` | 爆头击中 |
| `FLESH_HIT_SOUND` | `"flesh_hit"` | 普通击中 |
| `KILL_SOUND` | `"kill"` | 击杀 |
| `UNINSTALL_SOUND` | `"uninstall"` | 卸载配件 |
| `INSTALL_SOUND` | `"install"` | 装载配件 |

这些 id 是 `GunDisplayInstance#getSounds(String)`（`Map<String, ResourceLocation>`）的查询 key，不是 `ResourceLocation`。

### 7.2 `sendSoundToNearby` 精确签名

```java
public static void sendSoundToNearby(LivingEntity sourceEntity, int distance, ResourceLocation gunId,
                                     ResourceLocation gunDisplayId, String soundName, float volume, float pitch) {
    if (sourceEntity.level instanceof ServerLevel serverLevel) {
        BlockPos pos = sourceEntity.blockPosition();
        ServerMessageSound soundMessage = new ServerMessageSound(sourceEntity.getId(), gunId, gunDisplayId, soundName, volume, pitch, distance);
        serverLevel.getChunkSource().chunkMap.getPlayers(new ChunkPos(pos), false).stream()
                .filter(p -> p.distanceToSqr(pos.getX(), pos.getY(), pos.getZ()) < distance * distance)
                .filter(p -> p.getId() != sourceEntity.getId())
                .forEach(p -> NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> p), soundMessage));
    }
}
```

| 参数 | 类型 | 含义 |
| --- | --- | --- |
| `sourceEntity` | `LivingEntity` | 声源实体（取 `blockPosition()` 与 `getId()`） |
| `distance` | `int` | 传播半径（**方块单位的欧氏距离，比较的是平方**）；`<= 0` 时调用方不触发 |
| `gunId` | `ResourceLocation` | 用于客户端查 `GunDisplayInstance` |
| `gunDisplayId` | `ResourceLocation` | 同上（皮肤/显示变体） |
| `soundName` | `String` | 音效 key（用上面的常量） |
| `volume` | `float` | 音量 |
| `pitch` | `float` | 音高 |

**语义（关键结论）**
- 方法体被 `if (sourceEntity.level instanceof ServerLevel ...)` 包裹：**仅在服务端生效**，客户端调用是 no-op。
- 通过 `chunkMap.getPlayers(new ChunkPos(pos), false)` 取同区块的玩家列表，再用 `distanceToSqr < distance²` 精选。
- **`filter(p -> p.getId() != sourceEntity.getId())` 排除开枪者自己**——开枪者听到的 `SHOOT_SOUND` 是另一条链路（客户端 `SoundPlayManager.playShootSound`，`client/sound/SoundPlayManager.java:76`）。
- 发送的是 `network/message/ServerMessageSound`，客户端 `handle` 里 `context.enqueueWork(() -> SoundPlayManager.playMessageSound(message))`。
- 对 `SHOOT_3P_SOUND` / `SILENCE_3P_SOUND` 会以 `mono = true` 播放（`SoundPlayManager.java:167-168`）。

### 7.3 「枪声吸引怪物」——**TaC 未实现**

这是本次调研最需要澄清的一点。

- 全库 grep `Mob|setTarget|HurtByTarget|attract|scare|Anger` 在 `com/tacz/guns` 下**没有任何与声音吸引怪物相关的命中**（仅命中 `MobEffect`/`MobCategory`/无关的 `setTarget` in gltf POJO）。
- `sendSoundToNearby` **不调用任何原版 `Level#playSound`**，因此服务端的 `Mob` AI（`HurtByTargetGoal` 等）完全不会感知。
- 声音链路是 `NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER...)` → 客户端 `GunSoundInstance`，**纯客户端音频**。

消费方模组正是因此才需要在**调用点**自行注入实现吸引效果（`mixin/tacz/ModernKineticGunScriptAPIMixin.java:101-109`）：

```java
@Inject(method = "lambda$shootOnce$0", at = @At(value = "INVOKE",
        target = "Lcom/tacz/guns/sound/SoundManager;sendSoundToNearby(Lnet/minecraft/world/entity/LivingEntity;ILnet/minecraft/resources/ResourceLocation;Lnet/minecraft/resources/ResourceLocation;Ljava/lang/String;FF)V"))
private void misc_twf$soundAttract(boolean consumeAmmo, GunData gunData, int bulletAmount, BulletData bulletData,
                                   IGunOperator gunOperator, float processedSpeed, float finalInaccuracy,
                                   int soundDistance, boolean useSilenceSound, CallbackInfoReturnable<Boolean> cir) {
    if (this.itemStack.hasTag() && MISCTWFCommonConfig.TACZ_WHITELIST.get().contains(this.itemStack.getTag().getString("GunId"))) {
        return;
    }
    if (gunOperator instanceof LivingEntity livingEntity) {
        livingEntity.addEffect(new MobEffectInstance(SonaMobEffects.EXPOSURE.get(), 10, useSilenceSound ? 0 : 3, true, false));
    }
}
```

即：**消音（`useSilenceSound`）时放大器 0，未消音时放大器 3**，给开枪者施加 10 tick 的 `SonaMobEffects.EXPOSURE`（暴露）效果，由另一个模组（Sona）的效果系统去吸引僵尸。**TaC 侧零参与。**

> 同时注意 `getTag().getString("GunId")` —— 外部模组直接硬编码了第 14 节要讲的 NBT 键字符串。

另有一条**已被注释掉的 6 参数重载**（`sound/SoundManager.java:98-100`），1.18.2 已不可用：

```java
//    public static void sendSoundToNearby(LivingEntity sourceEntity, int distance, ResourceLocation gunId, String soundName, float volume, float pitch) {
//        sendSoundToNearby(sourceEntity, distance, gunId, DefaultAssets.DEFAULT_GUN_DISPLAY_ID, soundName, volume, pitch);
//    }
```

---

## 8. `com.tacz.guns.entity.EntityKineticBullet`

- 包名：`com.tacz.guns.entity`
- 文件：`entity/EntityKineticBullet.java`（648 行，工具报告 610 行）
- 类型：`public class EntityKineticBullet extends Projectile implements IEntityAdditionalSpawnData`
- 类 Javadoc：`动能武器打出的子弹实体。`

### 8.1 实体类型注册

```java
public static final EntityType<EntityKineticBullet> TYPE = EntityType.Builder
        .<EntityKineticBullet>of(EntityKineticBullet::new, MobCategory.MISC)
        .noSummon().noSave().fireImmune().sized(0.0625F, 0.0625F)
        .clientTrackingRange(5).updateInterval(5)
        .setShouldReceiveVelocityUpdates(false).build("bullet");
```

注意 `.noSave()`（不持久化）与 `.noSummon()`。

### 8.2 公开常量（专门为外部模组设计的 persistent data 钩子）

```java
/**
 * 允许其他 mod 使用 persistent data（永久数据） 控制曳光弹的颜色和粗细。<p>
 * 使用永久数据的好处是即使以后本类大改，使用了这个功能的其他 mod 也不会崩溃。<p>
 * 下面两个字段是 persistent data 的 key。<p>
 * 这个字段的值的类型是 int[4]。<p>
 * 使用例：
 * <pre>{@code
 *     bullet.getPersistentData().putIntArray(TRACER_COLOR_OVERRIDER_KEY, new int[]{255, 255, 255, 255});
 * }</pre>
 */
public static final String TRACER_COLOR_OVERRIDER_KEY = GunMod.MOD_ID + ":tracer_override";   // "tacz:tracer_override"

/**
 * 这个字段的值的类型是 float。
 * 1 表示默认大小，0 表示 0 倍率粗细（不显示了）
 */
public static final String TRACER_SIZE_OVERRIDER_KEY = GunMod.MOD_ID + ":tracer_size";       // "tacz:tracer_size"
```

**这是本类中官方明确标注「供其他 mod 使用」的 API**，读取方法：

```java
public Optional<float[]> getTracerColorOverride() {
    var pd = getPersistentData();
    if (!pd.contains(TRACER_COLOR_OVERRIDER_KEY, Tag.TAG_INT_ARRAY)) {
        return Optional.empty();
    } else {
        var ints = pd.getIntArray(TRACER_COLOR_OVERRIDER_KEY);
        // 支持 0/1/2/3/4 个元素的容错分支（1~2 个仅为优雅处理异常，避免崩溃）
        // ...
    }
}

public float getTracerSizeOverride() {
    var pd = getPersistentData();
    return pd.contains(TRACER_SIZE_OVERRIDER_KEY, Tag.TAG_ANY_NUMERIC) ? pd.getFloat(TRACER_SIZE_OVERRIDER_KEY) : 1;
}
```

### 8.3 字段全表（**全部 `private`**，无 getter 的项已标注）

| 字段 | 类型 | 初值 | 语义 | 有公开 getter |
| --- | --- | --- | --- | --- |
| `ammoId` | `ResourceLocation` | `DefaultAssets.EMPTY_AMMO_ID` | 弹药口径 id | `getAmmoId()` |
| `life` | `int` | `200` | 生命周期（tick），由 `bulletData.getLifeSecond()*20` 夹取 | 否 |
| `speed` | `float` | `1` | 速度，`AmmoSpeedModifier` 缓存 `/20`，上限 30（=600 m/s） | 否 |
| `gravity` | `float` | `0` | 重力 | 否 |
| `friction` | `float` | `0.01F` | 阻力 | 否 |
| `damageAmount` | `LinkedList<DistanceDamagePair>` | 空链表 | 距离-伤害曲线 | 否 |
| `distanceAmount` | `float` | `0` | 有效射程 | 否 |
| `knockback` | `float` | `0` | 击退强度 | 否 |
| `explosion` | `boolean` | `false` | 是否爆炸 | 否 |
| `igniteEntity` | `boolean` | `false` | 是否点燃实体 | 否 |
| `igniteBlock` | `boolean` | `false` | 是否点燃方块 | 否 |
| `igniteEntityTime` | `int` | `2` | 点燃秒数 | 否 |
| `explosionDamage` | `float` | `3` | 爆炸伤害 | 否 |
| `explosionRadius` | `float` | `3` | 爆炸半径 | 否 |
| `explosionDelayCount` | `int` | `Integer.MAX_VALUE` | 爆炸延迟 tick | 否 |
| `explosionKnockback` | `boolean` | `false` | 爆炸击退 | 否 |
| `explosionDestroyBlock` | `boolean` | `false` | 爆炸破坏方块（受 `AmmoConfig.EXPLOSIVE_AMMO_DESTROYS_BLOCK` 限制） | 否 |
| `damageModifier` | `float` | `1` | 霰弹衰减，`bulletAmount > 1` 时为 `1/bulletAmount` | 否 |
| `pierce` | `int` | `1` | 穿透数 | 否 |
| `startPos` | `Vec3` | — | 初始位置（用于距离衰减） | 否 |
| `isTracerAmmo` | `boolean` | — | 是否曳光弹 | `isTracerAmmo()` |
| `originCameraPosition` | `Vec3` | — | 仅客户端用 | `getOriginCameraPosition()` / setter |
| `originRenderOffset` | `Vec3` | — | 仅客户端用 | `getOriginRenderOffset()` / setter |
| `gunId` | `ResourceLocation` | — | 发射枪械 id | `getGunId()` |
| `gunDisplayId` | `ResourceLocation` | — | 枪械显示 id | `getGunDisplayId()` |
| `armorIgnore` | `float` | — | 穿甲比例 0~1 | 否 |
| `headShot` | `float` | — | 爆头倍率 | 否 |

另有 `public Random getRandom()`。

### 8.4 构造方法

```java
public EntityKineticBullet(EntityType<? extends Projectile> type, Level worldIn)
public EntityKineticBullet(EntityType<? extends Projectile> type, double x, double y, double z, Level worldIn)

// 最常用（ModernKineticGunScriptAPI 使用）
public EntityKineticBullet(Level worldIn, LivingEntity throwerIn, ItemStack gunItem, ResourceLocation ammoId,
                           ResourceLocation gunId, boolean isTracerAmmo, GunData gunData, BulletData bulletData) {
    this(TYPE, worldIn, throwerIn, gunItem, ammoId, gunId, DefaultAssets.DEFAULT_GUN_DISPLAY_ID, isTracerAmmo, gunData, bulletData);
}

// protected，可带 gunDisplayId
protected EntityKineticBullet(EntityType<? extends Projectile> type, Level worldIn, LivingEntity throwerIn, ItemStack gunItem,
                              ResourceLocation ammoId, ResourceLocation gunId, ResourceLocation gunDisplayId,
                              boolean isTracerAmmo, GunData gunData, BulletData bulletData)
```

构造器内从 `IGunOperator.fromLivingEntity(throwerIn).getCacheProperty()` 用 `Objects.requireNonNull` 取配件缓存 —— **若开枪者没有配件缓存会 NPE**。

### 8.5 关键方法

| 方法 | 可见性 | 说明 |
| --- | --- | --- |
| `defineSynchedData()` | `protected` | 空实现 |
| `tick()` | `public` | 调用 `onBulletTick()`、粒子、朝向/抛物线、位置更新、水中阻力、生命结束 `discard()` |
| `onBulletTick()` | `protected` | 服务端主逻辑：延迟爆炸 → 方块射线 → 实体命中（`pierce`/爆炸分支）→ `onHitBlock` |
| `onHitEntity(TacHitResult result, Vec3 startVec, Vec3 endVec)` | `protected` | 关键：`ITargetEntity` 分支 → `EntityHurtByGunEvent.Pre` → 点燃 → 爆头倍率 → `tacAttackEntity` → 爆炸 → `EntityKillByGunEvent` / `EntityHurtByGunEvent.Post` + 同步包 |
| **`tacAttackEntity(DamageSource source, MaybeMultipartEntity parts, float damage)`** | **`private`** | 应用 `BULLET_RESISTANCE` 属性、末影人 `bypassInvul()`、按 `armorIgnore` 拆成普通伤害 + 穿甲伤害（各触发一次 `hurt`，并重置 `invulnerableTime = 0`） |
| `onHitBlock(BlockHitResult result, Vec3 startVec, Vec3 endVec)` | `protected` | `AmmoHitBlockEvent` → 爆炸 → 弹孔粒子（`BulletHoleOption`）→ 点燃方块 |
| `getDamage(Vec3 hitVec)` | `public` | 按 `damageAmount` 距离分段取伤害 × `damageModifier`；**越界返回 0** |
| `getAddEntityPacket()` | `public` | `NetworkHooks.getEntitySpawningPacket(this)` |
| `writeSpawnData(FriendlyByteBuf)` / `readSpawnData(FriendlyByteBuf)` | `public` | 手写序列化（20 个字段） |
| `ownedBy(@Nullable Entity)` | `public` | `null` 返回 `false` |

**内部类型**

```java
public record MaybeMultipartEntity(Entity hitPart, Entity core) {
    public static MaybeMultipartEntity of(Entity hitPart) {
        var core = (hitPart instanceof PartEntity<?> part) ? part.getParent() : hitPart;
        return new MaybeMultipartEntity(hitPart, core);
    }
}

public static class EntityResult {
    private final Entity entity;
    private final Vec3 hitVec;
    private final boolean headshot;
    public EntityResult(Entity entity, Vec3 hitVec, boolean headshot)
    public Entity getEntity()
    public Vec3 getHitPos()
    public boolean isHeadshot()
}
```

### 8.6 外部模组的典型用法（mixin 修改子弹行为）

`mixin/tacz/EntityKineticBulletMixin.java` —— 用 `@ModifyVariable` 改 `tacAttackEntity` 的 `damage` 入参：

```java
@SuppressWarnings("DataFlowIssue")
@Mixin(value = EntityKineticBullet.class, remap = false)
public class EntityKineticBulletMixin {
	@ModifyVariable(method = "tacAttackEntity", at = @At("HEAD"), argsOnly = true, index = 3)
	private float misc_twf$applyGunMastery(float value) {
		float multiplier = 1.0F;
		if(((EntityKineticBullet)(Object)this).getOwner() instanceof LivingEntity livingEntity) {
			AttributeInstance gunMastery = livingEntity.getAttribute(MISCTWFAttributes.GUN_MASTERY);
			if(gunMastery != null) {
				multiplier += (float)(gunMastery.getValue() / 100.0D);
			}
		}
		return value * multiplier;
	}
}
```

要点：
- 目标方法 `tacAttackEntity` 是 **`private`**，但 Mixin 可以正常注入。
- **`index = 3`** —— `this` 占 0，`source` 占 1，`parts` 占 2，`damage` 占 3。这个 index 与参数类型/顺序强耦合。
- `argsOnly = true` 表示只匹配方法参数而非局部变量。
- 通过 `((EntityKineticBullet)(Object)this).getOwner()` 拿开枪者。

---

## 9. `com.tacz.guns.client.animation.statemachine.GunAnimationStateContext`

- 包名：`com.tacz.guns.client.animation.statemachine`
- 文件：`client/animation/statemachine/GunAnimationStateContext.java`（404 行，工具报告 366 行）
- 类型：`public class GunAnimationStateContext extends ItemAnimationStateContext`，标注 `@SuppressWarnings("unused")`
- 继承链：`GunAnimationStateContext` → `ItemAnimationStateContext`（`client/animation/statemachine/ItemAnimationStateContext.java`，仅含 `putAwayTime`）→ `com.tacz.guns.api.client.animation.statemachine.AnimationStateContext`
- 作用：**客户端枪械动画状态机的执行上下文**。它是 Lua 状态机脚本（`display` 中的 `state_machine` 脚本）与游戏状态之间的桥；脚本通过它查询「有没有子弹/是否在瞄准/输入方向/行走距离」并触发「抛壳」等表现。

### 9.1 字段与私有辅助方法（**外部 mixin 会 Shadow 它们**）

```java
private ItemStack currentGunItem;
private IGun iGun;
private GunDisplayInstance display;
private GunData gunData;
private float partialTicks;
private float walkDistAnchor = 0f;
private LuaNbtAccessor nbtUtil;

private <T> Optional<T> processGunData(BiFunction<IGun, GunDisplayInstance, T> processor)
private <T> Optional<T> processGunOperator(Function<IClientPlayerGunOperator, T> processor)
private <T> Optional<T> processRemoteGunOperator(Function<IGunOperator, T> processor)
private <T> Optional<T> processCameraEntity(Function<Entity, T> processor)
```

三个 `process*` 都是**空安全包装**：`iGun`/`display` 为 `null` 或玩家不存在时返回 `Optional.empty()`，避免脚本报错。

### 9.2 关键方法

| 方法 | 签名 | 返回语义 |
| --- | --- | --- |
| `hasBulletInBarrel` | `boolean hasBulletInBarrel()` | 开膛待击恒 `false` |
| `getShootInterval` | `long getShootInterval()` | 射击间隔 ms；burst 用 `getBurstData().getMinInterval()*1000`，**不减 5ms**（与服务端 `ModernKineticGunScriptAPI.getShootInterval` 不同） |
| `getLastShootTimestamp` | `long getLastShootTimestamp()` | 读 `operator.getDataHolder().clientLastShootTimestamp`，缺省 `-1L` |
| `getCurrentTimestamp` | `long getCurrentTimestamp()` | `System.currentTimeMillis()` |
| `adjustClientShootInterval` | `void adjustClientShootInterval(long alpha)` | 改 `dataHolder.clientShootTimestamp`；**与服务端 `adjustShootInterval` 配对使用** |
| `getAmmoCount` | `int getAmmoCount()` | 当前弹匣备弹 |
| `getMaxAmmoCount` | `int getMaxAmmoCount()` | 含配件扩容 |
| **`hasAmmoToConsume`** | `boolean hasAmmoToConsume()` | 与 `ModernKineticGunScriptAPI` 同名同义（客户端侧版本），`needCheckAmmo()` 为 false 时直接 `true` |
| `getMagExtentLevel` | `int getMagExtentLevel()` | 0~3 |
| `getFireMode` | `int getFireMode()` | `FireMode.ordinal()` |
| `getAimingProgress` | `float getAimingProgress()` | 0~1，带 `partialTicks` 插值 |
| `isAiming` | `boolean isAiming()` | |
| `getShootCoolDown` | `long getShootCoolDown()` | ms |
| `getReloadStateType` | `int getReloadStateType()` | `StateType.ordinal()`，缺省 `NOT_RELOADING` |
| `isInputUp/Down/Left/Right/Jumping` | `boolean` × 5 | 直接读 `Minecraft.getInstance().player.input.*` |
| `isCrawl` | `boolean isCrawl()` | |
| `isOnGround` | `boolean isOnGround()` | |
| `isCrouching` | `boolean isCrouching()` | |
| `anchorWalkDist` | `void anchorWalkDist()` | 在当前行走距离打锚点 |
| `getWalkDist` | `float getWalkDist()` | 相对锚点的行走距离（含 `partialTicks` 插值） |
| **`popShellFrom`** | `void popShellFrom(int index)` | 从指定抛壳窗弹出弹壳（`ShellRender#addShell`） |
| `getStateMachineParams` | `LuaTable getStateMachineParams()` | 从 `display.getStateMachineParam()`，`null` 时返回空表 |
| `getNbtAccessor` | `LuaNbtAccessor getNbtAccessor()` | **Javadoc 明确警告：客户端不应修改 NBT，仅读** |
| `getAttachment` | `String getAttachment(String type)` | 非法类型返回 `tacz:empty` |
| **`setCurrentGunItem`** | `void setCurrentGunItem(ItemStack currentGunItem)` | 状态机更新时调用（**脚本不应调用**），一并解析 `iGun`/`display`/`gunData`/`nbtUtil` |
| `getPartialTicks` | `float getPartialTicks()` | |
| **`setPartialTicks`** | `void setPartialTicks(float partialTicks)` | **脚本不应调用** |

### 9.3 外部模组的典型用法

`mixin/tacz/GunAnimationStateContextMixin.java` —— `@Inject` 到 `hasAmmoToConsume` 的第 3 个 `RETURN`，追加旅行者背包检查：

```java
@Mixin(value = GunAnimationStateContext.class, remap = false)
public abstract class GunAnimationStateContextMixin {
	@Shadow
	protected abstract <T> Optional<T> processCameraEntity(Function<Entity, T> processor);

	@Shadow
	private ItemStack currentGunItem;

	@SuppressWarnings("ConstantConditions")
	@Inject(method = "hasAmmoToConsume", at = @At(value = "RETURN", ordinal = 2), cancellable = true)
	private void misc_twf$checkTravelersBackpackTacSlot(CallbackInfoReturnable<Boolean> cir) {
		if(cir.getReturnValue()) { return; }
		cir.setReturnValue(processCameraEntity(entity -> {
			if(entity instanceof Player player) {
				// ... 遍历背包弹药槽，用 IAmmo.isAmmoOfGun(this.currentGunItem, ammoStack)
			}
			return false;
		}).orElse(false));
	}
}
```

要点：
- **`@Shadow protected abstract <T> Optional<T> processCameraEntity(...)`** —— 外部模组 Shadow 了**私有辅助方法** `processCameraEntity`（虽然源码写的是 `private`，mixin 声明为 `protected abstract` 也能匹配）与**私有字段** `currentGunItem`。
- `ordinal = 2` 对应 `hasAmmoToConsume` 里 `processCameraEntity(...)` 那个 `.orElse(false)` 的 RETURN。
- 同一方法在客户端（本类）与服务端（`ModernKineticGunScriptAPI`）各有一份，**外部模组必须两处都注入**才能保证表现一致——这正是移植时最容易漏的点。

---

## 10. `com.tacz.guns.client.gui.overlay.GunHudOverlay`

- 包名：`com.tacz.guns.client.gui.overlay`
- 文件：`client/gui/overlay/GunHudOverlay.java`（189 行，工具报告 172 行）
- 类型：`public class`（无实例化，全静态方法）
- 作用：**HUD 弹药/开火模式显示**。绘制当前弹药数、背包备弹数、火模式图标、枪械图标、以及一行 MC+模组版本调试文本。

### 10.1 静态资源与缓存字段

```java
private static final ResourceLocation SEMI  = new ResourceLocation(GunMod.MOD_ID, "textures/hud/fire_mode_semi.png");
private static final ResourceLocation AUTO  = new ResourceLocation(GunMod.MOD_ID, "textures/hud/fire_mode_auto.png");
private static final ResourceLocation BURST = new ResourceLocation(GunMod.MOD_ID, "textures/hud/fire_mode_burst.png");
private static final DecimalFormat CURRENT_AMMO_FORMAT = new DecimalFormat("000");
private static final DecimalFormat CURRENT_AMMO_FORMAT_PERCENT = new DecimalFormat("000%");
private static final DecimalFormat INVENTORY_AMMO_FORMAT = new DecimalFormat("0000");
private static long checkAmmoTimestamp = -1L;
private static int cacheMaxAmmoCount = 0;
private static int cacheInventoryAmmoCount = 0;
```

### 10.2 方法

```java
public static void render(ForgeIngameGui gui, PoseStack poseStack, float partialTick, int width, int height)

private static void handleCacheCount(LocalPlayer player, ItemStack stack, GunData gunData, IGun iGun)
private static void handleInventoryAmmo(ItemStack stack, Inventory inventory)
```

`render` 流程：
1. `RenderConfig.GUN_HUD_ENABLE` 开关判定 → 关闭直接返回
2. `player instanceof IClientPlayerGunOperator` 与 `stack.getItem() instanceof IGun` 双重判定
3. 取 `gunId` → `GunData`（`getClientGunIndex`）与 `GunDisplayInstance`
4. 计算弹药数：`iGun.getCurrentAmmoCount(stack) + (hasBulletInBarrel && bolt != OPEN_BOLT ? 1 : 0)`
5. 弹药 < `cacheMaxAmmoCount * 0.25` 显示红色 `0xFF5555`，否则白色 `0xFFFFFF`
6. `display.getAmmoCountStyle() == AmmoCountStyle.PERCENT` 时按百分比格式化
7. 调 `handleCacheCount`（**200ms 节流**）
8. 绘制竖线、当前弹药数（1.5x 缩放）、背包备弹数（0.8x，虚拟备弹 `useDummyAmmo` 时青色 `0x55FFFF`，否则灰色 `0xAAAAAA`）、版本文本（0.5x）、枪械 HUD 贴图（`display.getHUDTexture()` / `getHudEmptyTexture()`）、火模式图标

`handleCacheCount` 语义：
- `(System.currentTimeMillis() - checkAmmoTimestamp) > 200` 才刷新
- `cacheMaxAmmoCount = AttachmentDataUtils.getAmmoCountWithAttachment(stack, gunData)`
- 若 `IGunOperator.fromLivingEntity(player).needCheckAmmo()` 为 false（创造模式）→ `cacheInventoryAmmoCount = 9999`
- 否则虚拟备弹取 `getDummyAmmoAmount`，实际备弹走 `handleInventoryAmmo`

`handleInventoryAmmo` 逻辑（**外部模组的注入目标**）：

```java
private static void handleInventoryAmmo(ItemStack stack, Inventory inventory) {
    cacheInventoryAmmoCount = 0;
    for (int i = 0; i < inventory.getContainerSize(); i++) {
        ItemStack inventoryItem = inventory.getItem(i);
        if (inventoryItem.getItem() instanceof IAmmo iAmmo && iAmmo.isAmmoOfGun(stack, inventoryItem)) {
            cacheInventoryAmmoCount += inventoryItem.getCount();
        }
        if (inventoryItem.getItem() instanceof IAmmoBox iAmmoBox && iAmmoBox.isAmmoBoxOfGun(stack, inventoryItem)) {
            // 创造模式弹药箱？直接返回 9999
            if (iAmmoBox.isAllTypeCreative(inventoryItem) || iAmmoBox.isCreative(inventoryItem)) {
                cacheInventoryAmmoCount = 9999;
                return;
            }
            cacheInventoryAmmoCount += iAmmoBox.getAmmoCount(inventoryItem);
        }
    }
}
```

### 10.3 外部模组的典型用法

`mixin/tacz/GunHudOverlayMixin.java` —— `@Inject` 到 `handleInventoryAmmo` 的 `RETURN`，**直接累加私有静态字段**：

```java
@Mixin(value = GunHudOverlay.class, remap = false)
public class GunHudOverlayMixin {
	@Shadow
	private static int cacheInventoryAmmoCount;

	@SuppressWarnings("ConstantConditions")
	@Inject(method = "handleInventoryAmmo", at = @At(value = "RETURN"))
	private static void misc_twf$handleTravelersBackpackTacSlot(ItemStack stack, Inventory inventory, CallbackInfo ci) {
		Player player = inventory.player;
		ITravelersBackpack backpack = CapabilityUtils.getCapability(player).orElse(null);
		// ... 遍历背包
			if (ammoStack.getItem() instanceof IAmmo iAmmo && iAmmo.isAmmoOfGun(stack, ammoStack)) {
				cacheInventoryAmmoCount += ammoStack.getCount();
			}
	}
}
```

要点：
- `@Shadow private static int cacheInventoryAmmoCount;` —— **静态字段 Shadow**。
- 注入方法也必须是 `private static`，且参数表与原方法一致 + `CallbackInfo`。
- 用 `inventory.player` 反查玩家（`handleInventoryAmmo` 只收 `ItemStack` + `Inventory`，**不直接给 Player**）——这是一个需要注意的间接点。

---

## 11. `GunSmithTableResult` 与 `RawGunTableResult`

外部模组为何需要对它们做 accessor mixin——**因为两者的字段全部 `private` 且没有 getter**，而模组需要在枪械工作台配方结果里识别弹药类配方。

### 11.1 `com.tacz.guns.crafting.result.RawGunTableResult`

- 文件：`crafting/result/RawGunTableResult.java`（103 行，工具报告 91 行）
- 类型：`public class`（非 final）

```java
/**
 * 配方加载时部分物品的上下文还未完成初始化<br/>
 * 等待到实际需要使用配方时再进行初始化
 */
public class RawGunTableResult {
    private final String type;
    private final int count;
    private final ResourceLocation id;
    @Nullable
    private GunResult extraData;
    @Nullable
    private CompoundTag nbt;
```

| 成员 | 签名 | 可见性 |
| --- | --- | --- |
| 字段 `type` | `private final String` | 无 getter |
| 字段 `count` | `private final int` | 无 getter |
| 字段 `id` | `private final ResourceLocation` | 无 getter |
| 字段 `extraData` | `@Nullable private GunResult` | 无 getter |
| 字段 `nbt` | `@Nullable private CompoundTag` | 无 getter |
| 构造器 | `public RawGunTableResult(@NotNull String type, @NotNull ResourceLocation id, int count)` | public |
| `setExtraData` | `public void setExtraData(@Nullable GunResult extraData)` | public |
| `setNbt` | `public void setNbt(@Nullable CompoundTag nbt)` | public |
| 静态工厂 | `public static GunSmithTableResult init(RawGunTableResult raw)` | public |
| `getGunStack` / `getAmmoStack` / `getAttachmentStack` | `private GunSmithTableResult` | private |

`init` 是「延迟初始化」的核心：

```java
public static GunSmithTableResult init(RawGunTableResult raw) {
    GunSmithTableResult result = switch (raw.type) {
        case GunSmithTableResult.GUN -> raw.getGunStack();
        case GunSmithTableResult.AMMO -> raw.getAmmoStack();
        case GunSmithTableResult.ATTACHMENT -> raw.getAttachmentStack();
        default -> new GunSmithTableResult(ItemStack.EMPTY, StringUtils.EMPTY);
    };
    if (raw.nbt != null) {
        CompoundTag itemTag = result.getResult().getOrCreateTag();
        for (String key : raw.nbt.getAllKeys()) {
            Tag tag = raw.nbt.get(key);
            if (tag != null) {
                itemTag.put(key, tag);
            }
        }
    }
    return result;
}

private GunSmithTableResult getAmmoStack() {
    return new GunSmithTableResult(AmmoItemBuilder.create().setCount(count).setId(id).build(), GunSmithTableResult.AMMO);
}
```

**「弹药配方结果」的真正构造点就是 `getAmmoStack()`**：

```java
AmmoItemBuilder.create().setCount(count).setId(id).build()
// group = GunSmithTableResult.AMMO = "ammo"
```

### 11.2 `com.tacz.guns.crafting.result.GunSmithTableResult`

- 文件：`crafting/result/GunSmithTableResult.java`（44 行，全量如下）

```java
package com.tacz.guns.crafting.result;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GunSmithTableResult {
    public static final String GUN = "gun";
    public static final String AMMO = "ammo";
    public static final String ATTACHMENT = "attachment";
    public static final String CUSTOM = "custom";

    private ItemStack result = ItemStack.EMPTY;
    private String group = "";

    @Nullable
    private RawGunTableResult raw = null;

    public GunSmithTableResult(@NotNull RawGunTableResult raw) {
        this.raw = raw;
    }

    public void init() {
        if (raw != null) {
            GunSmithTableResult result = RawGunTableResult.init(raw);
            this.result = result.getResult();
            this.group = result.getGroup();
            this.raw = null;
        }
    }

    public GunSmithTableResult(ItemStack result, String group) {
        this.result = result;
        this.group = group;
    }

    public ItemStack getResult() {
        return result;
    }

    public String getGroup() {
        return group;
    }
}
```

| 成员 | 签名 | 可见性 |
| --- | --- | --- |
| 常量 | `GUN="gun"`, `AMMO="ammo"`, `ATTACHMENT="attachment"`, `CUSTOM="custom"` | `public static final String` |
| 字段 `result` | `private ItemStack`（初值 `ItemStack.EMPTY`） | 有 `getResult()`，无 setter |
| 字段 `group` | `private String`（初值 `""`） | 有 `getGroup()`，无 setter |
| **字段 `raw`** | **`@Nullable private RawGunTableResult`** | **无任何 getter** ← accessor mixin 的动机 |
| 构造器 1 | `public GunSmithTableResult(@NotNull RawGunTableResult raw)` | 延迟形态 |
| 构造器 2 | `public GunSmithTableResult(ItemStack result, String group)` | 直接形态 |
| `init()` | `public void init()` | 惰性求值：把 `raw` 展开成 `result`+`group`，随后 `raw = null` |

**关键生命周期**：配方加载后 `raw != null`、`result` 为空；首次使用（`GunSmithTableRecipe#init` → `GunSmithTableResult#init`）之后 `raw` 被置 `null`。因此 accessor 读 `raw` 必须容忍 `null`。

Recipe 侧（`crafting/GunSmithTableRecipe.java`）：

```java
public void init() {
    result.init();
}
```

### 11.3 外部模组为何需要 accessor mixin —— 实证

工作区两个 accessor 接口：

`mixin/tacz/GunSmithTableResultAccess.java`：

```java
@Mixin(value = GunSmithTableResult.class, remap = false)
public interface GunSmithTableResultAccess {
	@Accessor("raw") @Nullable
	RawGunTableResult misc_twf$getResult();
}
```

`mixin/tacz/RawGunTableResultAccess.java`：

```java
@Mixin(value = RawGunTableResult.class, remap = false)
public interface RawGunTableResultAccess {
	@Accessor("type")
	String misc_twf$getType();
}
```

**原因链**：
1. `GunSmithTableResult.raw` 是 `private` 且**无 getter** → 无法判断「这条配方是不是弹药类」。
2. 拿到 `raw` 后，`RawGunTableResult.type` 同样是 `private` 且**无 getter** → 无法读取 `"ammo"` 字符串。
3. 因此必须**两级 accessor** 串联：`GunSmithTableResultAccess.misc_twf$getResult()` → `RawGunTableResultAccess.misc_twf$getType()` → 与 `GunSmithTableResult.AMMO` 比较。
4. 两者都要 `@Nullable` 处理（`raw` 可能已被 `init()` 置空）。
5. accessor 方法名带 `misc_twf$` 前缀（符合模组 mixin 命名规范），`remap = false`。

---

## 12. `GunData` 与 `BulletData`

### 12.1 `com.tacz.guns.resource.pojo.data.gun.GunData`

- 文件：`resource/pojo/data/gun/GunData.java`（274 行，工具报告 210 行）
- 类型：`public class`（Gson `@SerializedName` POJO，**全部字段 `private` 且无 setter**，除 `setInaccuracy`）
- 全部字段有默认值，因此 JSON 中所有键都是可选的

| 字段 | 类型 | JSON 键 | 默认值 | 含义 |
| --- | --- | --- | --- | --- |
| `ammoId` | `ResourceLocation` | `"ammo"` | `null` | **本枪使用的弹药口径 id**（`CommonGunIndex` 校验必须非空） |
| `ammoAmount` | `int` | `"ammo_amount"` | `30` | 弹匣容量（校验 `>= 1`） |
| `extendedMagAmmoAmount` | `int @Nullable[]` | `"extended_mag_ammo_amount"` | `null` | 扩容弹匣各级容量（校验长度 `>= 3`） |
| `bolt` | `Bolt` | `"bolt"` | `Bolt.OPEN_BOLT` | 枪机类型 |
| `roundsPerMinute` | `int` | `"rpm"` | `300` | 射速（校验 `>= 1`） |
| `bulletData` | `BulletData` | `"bullet"` | `new BulletData()` | 子弹数据（见 12.2） |
| `drawTime` | `float` | `"draw_time"` | `0.4f` | 切枪耗时（秒） |
| `putAwayTime` | `float` | `"put_away_time"` | `0.4f` | 收枪耗时 |
| `sprintTime` | `float` | `"sprint_time"` | `0.2f` | 奔跑后需等待的持枪时间 |
| `aimTime` | `float` | `"aim_time"` | `0.2f` | 瞄准耗时 |
| `boltActionTime` | `float` | `"bolt_action_time"` | `0` | 拉栓耗时 |
| `boltFeedTime` | `float` | `"bolt_feed_time"` | `-1` | 拉栓供弹时间（`-1` 表示未设置） |
| `reloadData` | `GunReloadData` | `"reload"` | `new GunReloadData()` | 换弹数据（`getType()` 必须非空） |
| `fireModeSet` | `List<FireMode>` | `"fire_mode"` | `[UNKNOWN]` | 可选开火模式（校验非空、不含 `null`/`UNKNOWN`） |
| `fireModeAdjust` | `Map<FireMode, GunFireModeAdjustData>` | `"fire_mode_adjust"` | 空 LinkedHashMap | 各模式 rpm 修正 |
| `burstData` | `BurstData` | `"burst_data"` | `new BurstData()` | 连发数据 |
| `crawlRecoilMultiplier` | `float` | `"crawl_recoil_multiplier"` | `0.5f` | 匍匐后坐力倍率 |
| `recoil` | `GunRecoil` | `"recoil"` | `new GunRecoil()` | 后坐力曲线 |
| `hurtBobTweakMultiplier` | `float` | `"hurt_bob_tweak_multiplier"` | `0.05f` | 受伤视角抖动 |
| `inaccuracy` | `Map<InaccuracyType, Float>` | `"inaccuracy"` | `null` | 散射（`null` 时用默认表填充） |
| `moveSpeed` | `MoveSpeed` | `"movement_speed"` | `new MoveSpeed()` | 移动速度倍率 |
| `gunMeleeData` | `GunMeleeData` | `"melee"` | `new GunMeleeData()` | 近战数据 |
| `allowAttachments` | `List<AttachmentType>` | `"allow_attachment_types"` | 空列表 | 允许的配件类型 |
| `exclusiveAttachments` | `Map<ResourceLocation, AttachmentData>` | `"exclusive_attachments"` | 空 HashMap | 专有配件 |
| `weight` | `float` | `"weight"` | `0f` | 重量 |
| `builtInAttachments` | `Map<AttachmentType, ResourceLocation>` | `"builtin_attachments"` | 空 HashMap | 内置配件 |
| `script` | `ResourceLocation` | `"script"` | `null` | Lua 脚本 id |
| `scriptParam` | `Map<String, Object>` | `"script_param"` | `null` | 传给脚本的参数表 |

**方法**：`getAmmoId()`、`getAmmoAmount()`、`getExtendedMagAmmoAmount()`、`getBolt()`、`@ApiStatus.Internal getRoundsPerMinute()`、`getRoundsPerMinute(FireMode)`、`getBulletData()`、`getDrawTime()`、`getPutAwayTime()`、`getAimTime()`、`getSprintTime()`、`getBoltActionTime()`、`getBoltFeedTime()`、`getReloadData()`、`getFireModeSet()`、`getBurstData()`、`getWeight()`、`@Nullable getFireModeAdjustData(FireMode)`、`getCrawlRecoilMultiplier()`、`getRecoil()`、`getHurtBobTweakMultiplier()`、`getInaccuracy()`、`setInaccuracy(Map)`、`getInaccuracy(InaccuracyType)`、`getInaccuracy(InaccuracyType, float addend)`、`getMoveSpeed()`、`getMeleeData()`、`@Nullable getAllowAttachments()`、`getBuiltInAttachments()`、`getExclusiveAttachments()`、`@Nullable getScript()`、`@Nullable getScriptParam()`

**射击间隔（关键算法）**：

```java
/**
 * @return 枪械开火的间隔，单位为 ms 。
 */
public long getShootInterval(LivingEntity shooter, FireMode fireMode) {
    int rpm = this.getRoundsPerMinute(fireMode);
    AttachmentCacheProperty cacheProperty = IGunOperator.fromLivingEntity(shooter).getCacheProperty();
    if (cacheProperty != null) {
        rpm = Mth.clamp(cacheProperty.<Integer>getCache(RpmModifier.ID), 1, 1200);
    }
    return 60_000L / rpm;
}

public long getBurstShootInterval() {
    if (burstData == null || burstData.getBpm() <= 0) { return 300; }
    return 60_000L / burstData.getBpm();
}
```

**注意**：`getShootInterval` 会通过 `IGunOperator.fromLivingEntity(shooter)` 反查配件缓存 —— 说明 `GunData` **不是纯数据类**，它对实体侧 API 有运行时依赖（这也是「POJO 依赖 API」的反直觉点）。

`Bolt` 枚举被引用的值：`OPEN_BOLT`、`CLOSED_BOLT`、`MANUAL_ACTION`（见 `ModernKineticGunScriptAPI#reduceAmmoOnce` 与 `GunHudOverlay`）。

### 12.2 `com.tacz.guns.resource.pojo.data.gun.BulletData`

- 文件：`resource/pojo/data/gun/BulletData.java`（103 行，工具报告 75 行）
- 类型：`public class`

| 字段 | 类型 | JSON 键 | 默认值 | 含义 |
| --- | --- | --- | --- | --- |
| `lifeSecond` | `float` | `"life"` | `10f` | 存活秒数（乘 20 → tick，夹取到 `>= 1`） |
| `bulletAmount` | `int` | `"bullet_amount"` | `1` | 单次射击弹丸数（霰弹 > 1；`shootOnce` 内 `Math.max(..., 1)`） |
| `damageAmount` | `float` | `"damage"` | `5` | 基础伤害 |
| `extraDamage` | `@Nullable ExtraDamage` | `"extra_damage"` | `null` | 额外伤害（距离衰减曲线，实际用 `DamageModifier` 缓存） |
| `speed` | `float` | `"speed"` | `5` | 弹速（**注意：`EntityKineticBullet` 构造器实际用的是配件缓存的 `AmmoSpeedModifier`，此字段在子弹初始化路径中未被直接读取**） |
| `gravity` | `float` | `"gravity"` | `0` | 重力 |
| `knockback` | `float` | `"knockback"` | `0` | 击退 |
| `friction` | `float` | `"friction"` | `0.01f` | 阻力 |
| `pierce` | `int` | `"pierce"` | `1` | 穿透数（**实际用 `PierceModifier` 缓存**） |
| `ignite` | `Ignite` | `"ignite"` | `new Ignite(false)` | 点燃开关（`isIgniteEntity` / `isIgniteBlock`） |
| `igniteEntityTime` | `int` | `"ignite_entity_time"` | `2` | 点燃秒数 |
| `tracerCountInterval` | `int` | `"tracer_count_interval"` | `-1` | 曳光弹间隔，`< 0` 表示无曳光 |
| `explosionData` | `@Nullable ExplosionData` | `"explosion"` | `null` | 爆炸数据（**实际用 `ExplosionModifier` 缓存**） |

**方法**：`getLifeSecond()`、`getBulletAmount()`、`getDamageAmount()`、`@Nullable getExtraDamage()`、`getSpeed()`、`getGravity()`、`getKnockback()`、`getFriction()`、`getPierce()`、`getIgnite()`、`getIgniteEntityTime()`、`hasTracerAmmo()`、`getTracerCountInterval()`、`@Nullable getExplosionData()`

```java
public boolean hasTracerAmmo() {
    return this.tracerCountInterval >= 0;
}
```

**重要提醒（差异对照易错点）**：`BulletData` 里不少字段在实例化 `EntityKineticBullet` 时会被**配件缓存（`AttachmentCacheProperty`）覆盖**，`BulletData` 只是「基础值」。`EntityKineticBullet` 构造器中的实际取值对照：

| 子弹字段 | 来源 |
| --- | --- |
| `armorIgnore` | `ArmorIgnoreModifier` 缓存（夹取 0~1） |
| `headShot` | `HeadShotModifier` 缓存 |
| `knockback` | `KnockbackModifier` 缓存（**不是** `bulletData.getKnockback()`） |
| `life` | `bulletData.getLifeSecond() * 20` |
| `speed` | `AmmoSpeedModifier` 缓存 `/20`，上限 30 |
| `gravity` / `friction` | `bulletData` |
| `igniteEntity` / `igniteBlock` | `bulletData.getIgnite()` **或** `IgniteModifier` 缓存 |
| `igniteEntityTime` | `bulletData.getIgniteEntityTime()` |
| `damageAmount` | `DamageModifier` 缓存 |
| `distanceAmount` | `EffectiveRangeModifier` 缓存 |
| `damageModifier` | `1 / bulletData.getBulletAmount()`（当 `bulletAmount > 1`） |
| `pierce` | `PierceModifier` 缓存 |
| `explosion*` | `ExplosionModifier` 缓存 |

---

## 13. 弹药口径 id 的表示

### 13.1 NBT 键名

| 载体 | 键名 | 类型 | 定义位置 |
| --- | --- | --- | --- |
| 弹药物品（`tacz:ammo`） | **`"AmmoId"`** | `TAG_STRING` | `api/item/nbt/AmmoItemDataAccessor.java:17` |
| 弹药盒（`tacz:ammo_box`） | **`"AmmoId"`** | `TAG_STRING` | `api/item/nbt/AmmoBoxItemDataAccessor.java:13` |

弹药盒的其余 NBT 键（`api/item/nbt/AmmoBoxItemDataAccessor.java:13-17`）：

```java
String AMMO_ID_TAG = "AmmoId";
String AMMO_COUNT_TAG = "AmmoCount";
String CREATIVE_TAG = "Creative";
String ALL_TYPE_CREATIVE_TAG = "AllTypeCreative";
String LEVEL_TAG = "Level";
```

### 13.2 id 命名空间与示例

- 命名空间：`tacz`（`GunMod.MOD_ID = "tacz"`）
- 存储格式：`ResourceLocation#toString()`，即 `namespace:path`
- 特殊值（`api/DefaultAssets.java`）：

```java
public static ResourceLocation DEFAULT_AMMO_ID = new ResourceLocation(GunMod.MOD_ID, "762x39");  // tacz:762x39
public static ResourceLocation EMPTY_AMMO_ID   = new ResourceLocation(GunMod.MOD_ID, "empty");    // tacz:empty
```

| id | 出处 | 说明 |
| --- | --- | --- |
| `tacz:762x39` | `DefaultAssets.DEFAULT_AMMO_ID` | 默认口径；`setAmmoId(null)` 写入此值 |
| `tacz:empty` | `DefaultAssets.EMPTY_AMMO_ID` | 空口径哨兵；`getAmmoId` 解析失败/缺失时返回 |
| `tacz:9mm` | 工作区 `MISCTWFRecipeBookTypes.java:25` | **TaC Java 源码中不出现**，是数据包定义的弹药索引 id |

其余口径（`556x45`、`45acp` 等）在 Java 源码中**没有任何硬编码**——全部由数据包 `index/ammo/*.json` 的 `ResourceLocation` 键决定。`CommonAmmoIndex` 是从索引构造的：

```java
// resource/index/CommonAmmoIndex.java
public class CommonAmmoIndex {
    private int stackSize;
    private AmmoIndexPOJO pojo;

    private static void checkIndex(AmmoIndexPOJO ammoIndexPOJO, CommonAmmoIndex index) {
        Preconditions.checkArgument(ammoIndexPOJO != null, "index object file is empty");
        index.stackSize = Math.max(ammoIndexPOJO.getStackSize(), 1);
    }
    public int getStackSize() { return stackSize; }
    public AmmoIndexPOJO getPojo() { return pojo; }
}
```

弹药索引 JSON 的字段（`resource/pojo/AmmoIndexPOJO.java`，**由注解反推，源码树无 JSON 文件**）：

```java
@SerializedName("name")       private String name;            // 翻译键
@SerializedName("display")    private ResourceLocation display; // 显示模型 id
@SerializedName("stack_size") private int stackSize;          // 最大堆叠（夹取 >= 1）
@SerializedName("tooltip")    @Nullable private String tooltip; // 可选 tooltip 翻译键
```

### 13.3 数据配方 JSON 中指定弹药输出的格式

TaC 1.18.2 的枪械工作台配方走**原版配方加载器**（`ResourceLocation` 路径，`data/<ns>/recipes/*.json`），反序列化器为 `crafting/GunSmithTableSerializer`，其 `fromJson` 直接 `CommonAssetsManager.GSON.fromJson(jsonObject, TableRecipe.class)`。`GunSmithTableResult` 由自定义 `JsonDeserializer` 处理：

- 文件：`resource/serialize/GunSmithTableResultSerializer.java`（77 行）
- 注册方式：在 `CommonAssetsManager.GSON` 上 `registerTypeAdapter(GunSmithTableResult.class, ...)`（`GunSmithTableResultSerializer implements JsonDeserializer<GunSmithTableResult>`）

```java
@Override
public GunSmithTableResult deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
    if (json.isJsonObject()) {
        JsonObject jsonObject = json.getAsJsonObject();
        String typeName = GsonHelper.getAsString(jsonObject, "type");
        int count = 1;
        CompoundTag extraTag = null;
        if (jsonObject.has("count")) {
            count = Math.max(GsonHelper.getAsInt(jsonObject, "count"), 1);
        }
        if (jsonObject.has("nbt")) {
            extraTag = CraftingHelper.getNBT(jsonObject.get("nbt"));
        }

        GunSmithTableResult result;
        switch (typeName) {
            case GunSmithTableResult.GUN, GunSmithTableResult.AMMO, GunSmithTableResult.ATTACHMENT -> {
                RawGunTableResult raw = new RawGunTableResult(typeName, getId(jsonObject), count);
                if (extraTag != null) { raw.setNbt(extraTag); }
                if (typeName.equals(GunSmithTableResult.GUN)) {
                    GunResult gunResult = CommonAssetsManager.GSON.fromJson(jsonObject, GunResult.class);
                    if (gunResult != null) { raw.setExtraData(gunResult); }
                }
                result = new GunSmithTableResult(raw);
            }
            case GunSmithTableResult.CUSTOM -> {
                JsonObject resultObject = GsonHelper.getAsJsonObject(jsonObject, "item");
                String group = GsonHelper.getAsString(jsonObject, "group", StringUtils.EMPTY);
                ItemStack itemStack = CraftingHelper.getItemStack(resultObject, true);
                result = new GunSmithTableResult(itemStack, group);
                // ... 合并 extraTag
            }
            default -> {
                return new GunSmithTableResult(ItemStack.EMPTY, StringUtils.EMPTY);
            }
        }
        return result;
    }
    return new GunSmithTableResult(ItemStack.EMPTY, StringUtils.EMPTY);
}

private ResourceLocation getId(JsonObject jsonObject) {
    return new ResourceLocation(GsonHelper.getAsString(jsonObject, "id"));
}
```

`TableRecipe` 的外层结构（`resource/pojo/data/recipe/TableRecipe.java`）：

```java
@SerializedName("materials") private List<GunSmithTableIngredient> materials;
@SerializedName("result")    private GunSmithTableResult result;
```

`GunResult`（仅 `type: "gun"` 时解析，`resource/pojo/data/recipe/GunResult.java`）：

```java
@SerializedName("ammo_count")  private int ammoCount = 0;
@SerializedName("attachments") private Map<AttachmentType, ResourceLocation> attachments = Maps.newHashMap();
```

**由此反推的弹药配方 JSON 格式**（⚠️ 源码树内无实际 JSON 文件，此示例由上述反序列化代码构造，非源码原文）：

```json
{
  "materials": [
    { "item": { "item": "minecraft:copper_ingot" }, "count": 1 },
    { "item": { "item": "minecraft:gunpowder" }, "count": 2 }
  ],
  "result": {
    "type": "ammo",
    "id": "tacz:9mm",
    "count": 30
  }
}
```

字段规则（精确）：
- `result.type`：**必填**，取值 `"gun"` / `"ammo"` / `"attachment"` / `"custom"`；未知值 → 返回 `ItemStack.EMPTY` 的结果（静默空配方）。
- `result.id`：`"gun"`/`"ammo"`/`"attachment"` 时**必填**，必须能被 `new ResourceLocation(String)` 解析，否则抛异常。**这是弹药口径 id 的唯一指定位置。**
- `result.count`：可选，缺省 `1`，内部 `Math.max(count, 1)`。
- `result.nbt`：可选，用原版 `CraftingHelper.getNBT` 解析，之后逐个键 `put` 到结果物品的 tag 上（**可覆盖** `AmmoId` 等）。
- `type: "custom"` 时改读 `result.item`（原版 `CraftingHelper.getItemStack`）+ 可选 `result.group`。

**口径校验时机**：`RawGunTableResult.getAmmoStack()` 直接调 `AmmoItemBuilder.create().setCount(count).setId(id).build()`，**不查询 `CommonAmmoIndex` 是否存在**。因此配方可以写出不存在的口径 id 而不报错，只是产出的弹药无对应索引（`getItemStackLimit` 回落为 1、无名称/贴图）。

---

## 14. 枪械 id（GunId）在 ItemStack 上的存储方式

### 14.1 NBT 键全表

定义位置：`api/item/nbt/GunItemDataAccessor.java:24-34`

```java
String GUN_ID_TAG = "GunId";
String GUN_FIRE_MODE_TAG = "GunFireMode";
String GUN_HAS_BULLET_IN_BARREL = "HasBulletInBarrel";
String GUN_CURRENT_AMMO_COUNT_TAG = "GunCurrentAmmoCount";
String GUN_ATTACHMENT_BASE = "Attachment";
String GUN_EXP_TAG = "GunLevelExp";
String GUN_DUMMY_AMMO = "DummyAmmo";
String GUN_MAX_DUMMY_AMMO = "MaxDummyAmmo";
String GUN_ATTACHMENT_LOCK = "AttachmentLock";

String GUN_DISPLAY_ID_TAG = "GunDisplayId";
```

| NBT 键 | 值类型 | 字段含义 | 缺省返回 |
| --- | --- | --- | --- |
| **`"GunId"`** | `TAG_STRING` | **枪械 id（核心）** | `tacz:empty` |
| `"GunFireMode"` | `TAG_STRING` | `FireMode.name()`（大写：`AUTO`/`SEMI`/`BURST`/`UNKNOWN`） | `FireMode.UNKNOWN` |
| `"HasBulletInBarrel"` | `TAG_BYTE`（boolean） | 枪膛是否有弹 | `false` |
| `"GunCurrentAmmoCount"` | `TAG_INT` | 当前弹匣弹药数 | `0` |
| `"Attachment" + type.name()` | `TAG_COMPOUND` | 配件（存整个 ItemStack 的 NBT，如 `AttachmentSCOPE`） | `ItemStack.EMPTY` |
| `"GunLevelExp"` | `TAG_INT` | 累计经验 | `0` |
| `"DummyAmmo"` | `TAG_INT` | 虚拟备弹（**存在即代表启用虚拟备弹**） | `0` |
| `"MaxDummyAmmo"` | `TAG_INT` | 虚拟备弹上限 | `0` |
| `"AttachmentLock"` | `TAG_BYTE`（boolean） | 配件锁 | `false` |
| `"GunDisplayId"` | `TAG_STRING` | 客户端显示/皮肤 id | `tacz:default` |

### 14.2 GunId 的读写实现（原样）

```java
@Override
@Nonnull
default ResourceLocation getGunId(ItemStack gun) {
    CompoundTag nbt = gun.getOrCreateTag();
    if (nbt.contains(GUN_ID_TAG, Tag.TAG_STRING)) {
        ResourceLocation gunId = ResourceLocation.tryParse(nbt.getString(GUN_ID_TAG));
        return Objects.requireNonNullElse(gunId, DefaultAssets.EMPTY_GUN_ID);
    }
    return DefaultAssets.EMPTY_GUN_ID;
}

@Override
default void setGunId(ItemStack gun, @Nullable ResourceLocation gunId) {
    CompoundTag nbt = gun.getOrCreateTag();
    if (gunId != null) {
        nbt.putString(GUN_ID_TAG, gunId.toString());
    }
}
```

**语义细节**
- `getGunId` 用 `ResourceLocation.tryParse`（不抛异常），解析失败 → `tacz:empty`（`DefaultAssets.EMPTY_GUN_ID`）。
- `setGunId(gun, null)` 是 **no-op**（不像 `setAmmoId(null)` 会写默认值）——即无法通过该方法清除 GunId。
- 返回值标注 `@NotNull`（`IGun` 上为 `@NotNull`，实现为 `@Nonnull`）。

### 14.3 GunId 的构造路径

`api/item/builder/GunItemBuilder.java:67-86`（**注意它通过 `item_type` 决定用哪个物品来承载 GunId**）：

```java
public ItemStack build() {
    String itemType = TimelessAPI.getCommonGunIndex(gunId).map(index -> index.getPojo().getItemType()).orElse(null);
    Preconditions.checkArgument(itemType != null, "Could not found gun id: " + gunId);

    RegistryObject<? extends AbstractGunItem> gunItemRegistryObject = GunItemManager.getGunItemRegistryObject(itemType);
    Preconditions.checkArgument(gunItemRegistryObject != null, "Could not found gun item type: " + itemType);

    ItemStack gun = new ItemStack(gunItemRegistryObject.get(), this.count);
    if (gun.getItem() instanceof IGun iGun) {
        iGun.setGunId(gun, this.gunId);
        iGun.setFireMode(gun, this.fireMode);
        iGun.setCurrentAmmoCount(gun, this.ammoCount);
        iGun.setBulletInBarrel(gun, this.bulletInBarrel);
        this.attachments.forEach((type, id) -> {
            ItemStack attachmentStack = AttachmentItemBuilder.create().setId(id).build();
            iGun.installAttachment(gun, attachmentStack);
        });
    }
    return gun;
}
```

`GunItemBuilder` 的其他成员：`create()`（静态）、`setCount(int)`（`Math.max(count,1)`）、`setAmmoCount(int)`（`Math.max(count,0)`）、`setId(ResourceLocation)`、`setFireMode(FireMode)`、`setAmmoInBarrel(boolean)`、`putAttachment(AttachmentType, ResourceLocation)`、`putAllAttachment(Map)`。`id` **无默认值**，未设置时 `getCommonGunIndex(null)` 会失败并抛 `IllegalArgumentException("Could not found gun id: null")`。

`GunItemManager`（`api/item/gun/GunItemManager.java`）是 `item_type` → 物品注册对象的映射表：

```java
public class GunItemManager {
    private static final Map<String, RegistryObject<? extends AbstractGunItem>> GUN_ITEM_MAP = Maps.newHashMap();

    /**
     * 建议在 RegistryEvent.Register<Item> 事件时注册此枪械变种
     */
    public static void registerGunItem(String name, RegistryObject<? extends AbstractGunItem> registryObject) {
        GUN_ITEM_MAP.put(name, registryObject);
    }

    public static RegistryObject<? extends AbstractGunItem> getGunItemRegistryObject(String key) {
        return GUN_ITEM_MAP.get(key);
    }

    public static Collection<RegistryObject<? extends AbstractGunItem>> getAllGunItems() {
        return GUN_ITEM_MAP.values();
    }
}
```

`ModItems.onItemRegister` 里注册了唯一的 `"modern_kinetic"` → `MODERN_KINETIC_GUN`。

### 14.4 外部模组的典型用法

**直接硬编码 NBT 键字符串**（`mixin/tacz/ModernKineticGunScriptAPIMixin.java:103`）：

```java
if (this.itemStack.hasTag() && MISCTWFCommonConfig.TACZ_WHITELIST.get().contains(this.itemStack.getTag().getString("GunId"))) {
    return;
}
```

即：用 `itemStack.getTag().getString("GunId")` 取 GunId 字符串，与配置项白名单比对来决定是否施加「暴露」效果。**注意这里绕过了 `IGun.getGunId`，直接用字面量 `"GunId"`** ——1.21.1 侧若键名变化，此判断会静默失效（返回 `""`，白名单不匹配 → 继续施加效果）。

---

## 15. 未找到 / 明确不存在的项

以下内容经检索确认**在 TaC 1.18.2 源码树中不存在**，不做猜测：

| 项 | 结论 |
| --- | --- |
| 任何 `.json` 资源文件（配方、索引、语言文件） | **不存在**。`com/tacz/guns` 下 `-Include *.json` 计数为 0，`C:\Projects\Sources-1.18.2` 下仅有 `com` 一个目录。所有 JSON 格式均由反序列化代码反推 |
| `ModItems` 中按口径拆分的弹药字段（`AMMO_9MM` 等） | **不存在**。仅有一个 `AMMO`（`tacz:ammo`） |
| Java 源码中硬编码的 `9mm` / `556x45` / `45acp` 口径 id | **不存在**。仅 `DefaultAssets.DEFAULT_AMMO_ID = tacz:762x39` 一处 |
| `SoundManager.sendSoundToNearby` 的 6 参数重载 | **已被注释掉**（`sound/SoundManager.java:98-100`），运行时不存在 |
| 「枪声吸引怪物」的服务端实现 | **不存在**。全库无 Mob AI / Anger / Target 相关代码；`sendSoundToNearby` 只向玩家发包。该行为由消费方模组在调用点自行注入实现 |
| `GunSmithTableResult.getRaw()` 之类的公开访问器 | **不存在**。`raw` 字段 `private` 且无 getter（这正是外部 accessor mixin 的原因） |
| `RawGunTableResult.getType()/getId()/getCount()` | **不存在**。三个字段均 `private final` 且无 getter |
| `EntityKineticBullet` 中 `life`/`speed`/`gravity`/`friction`/`pierce`/`armorIgnore`/`headShot` 等字段的 getter | **不存在**。仅 `ammoId`/`gunId`/`gunDisplayId`/`isTracerAmmo` 有公开 getter |
| `AbstractGunItem` 的 NBT 读写方法 | **不存在**。全部在 `GunItemDataAccessor` 中 |
| 外部模组自定义的 `AbstractGunItem` 子类（枪械变种） | 本工作区中**未出现**；仅通过 mixin 注入 `canReload` |
| `MobileKineticGunItem` 等其它枪械实现类 | 源码树中**不存在**（`ModItems` 仅注册 `modern_kinetic_gun`） |

---

## 16. 移植差异对照检查清单（供 Superb Warfare 对照使用）

按「外部模组实际依赖点」的脆弱程度排序：

| 优先级 | 依赖点 | 1.18.2 精确形态 | 风险 |
| --- | --- | --- | --- |
| **P0** | `AbstractGunItem#canReload` 的 **4 个 return 点** | `@At("RETURN") ordinal = 3` | return 点数量/顺序变化 → 注入失败 |
| **P0** | `ModernKineticGunScriptAPI#lambda$shootOnce$0` **lambda 名 + 参数表** | `(boolean, GunData, int, BulletData, IGunOperator, float, float, int, boolean)` | 编译产物，任何重构都会破坏 |
| **P0** | `SoundManager.sendSoundToNearby` **精确描述符** | `(LivingEntity;ILnet/minecraft/resources/ResourceLocation;Lnet/minecraft/resources/ResourceLocation;Ljava/lang/String;FF)V` | 被 `@At("INVOKE") target` 硬引用 |
| **P0** | NBT 键字面量 | `"AmmoId"`、`"GunId"` | 键名变化 → 静默失效（无异常） |
| **P1** | `ModernKineticGunScriptAPI#consumeAmmoFromPlayer` | `@At("RETURN") ordinal = 1` | 分支增减 |
| **P1** | `ModernKineticGunScriptAPI#hasAmmoToConsume` | `@At("RETURN") ordinal = 2` | 分支增减 |
| **P1** | `GunAnimationStateContext#hasAmmoToConsume` | `@At("RETURN") ordinal = 2`；Shadow `private ItemStack currentGunItem` 与 `protected abstract <T> Optional<T> processCameraEntity(Function<Entity,T>)` | 字段/方法签名变化 |
| **P1** | `EntityKineticBullet#tacAttackEntity` | `private`，`@ModifyVariable index = 3`，参数序 `(DamageSource, MaybeMultipartEntity, float)` | 参数序或 privacy 变化 |
| **P1** | `GunHudOverlay#handleInventoryAmmo` | `private static (ItemStack, Inventory)`；Shadow `private static int cacheInventoryAmmoCount` | 签名变化 |
| **P1** | `GunSmithTableResult.raw` / `RawGunTableResult.type` | 必须仍为 `private` 字段且无同名 getter（否则 `@Accessor` 冲突） | 若上游补了 getter，accessor mixin 会失效 |
| **P2** | `IAmmo` / `AmmoItemBuilder` / `ModItems` | 见第 1、3、4 节 | 编译期 API |
| **P2** | `IGunOperator` 由 `LivingEntityMixin` 织入 | 若 1.21.1 改为 Capability/Attachment 而非 mixin，则 `IGunOperator.fromLivingEntity` 的「无校验强转」语义会变 | 语义性破坏 |
| **P2** | `BulletData` 字段被配件缓存覆盖的行为 | 见 12.2 对照表 | 数值差异 |

**建议在 1.21.1 侧逐项验证的最小断言集**：
1. `class com.tacz.guns.api.item.gun.AbstractGunItem` 存在且 `canReload(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;)Z` 的 RETURN 数仍为 4。
2. `class com.tacz.guns.sound.SoundManager` 存在 `sendSoundToNearby` 且描述符不变。
3. `com.tacz.guns.item.ModernKineticGunScriptAPI` 仍有 `shootOnce(Z)V`，且其中仍调用 `sendSoundToNearby`（若被内联/抽取到别处，`lambda$shootOnce$0` 注入即失效）。
4. `AmmoId` 仍是 `TAG_STRING`，`tacz:empty` 仍是空哨兵，`tacz:762x39` 是否仍为默认值。
5. `ModItems` 是否仍是单一 `AMMO` 字段（若被拆成多物品，第 4 节的所有外部用途都需要改写）。
6. `GunSmithTableResult` 是否仍无 `raw` 的 getter。

---

*报告生成于只读调研，未修改 `C:\Projects\Sources-1.18.2` 下任何文件。*
