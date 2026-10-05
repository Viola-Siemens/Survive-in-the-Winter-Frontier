package com.hexagram2021.misc_twf.common.register;

import com.google.common.collect.Lists;
import com.hexagram2021.misc_twf.common.item.AbyssVirusVaccine;
import net.minecraft.Util;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;

/**
 * 物品注册器，负责注册模组中的所有物品喵~
 * <p>
 * 包含材料物品、装备、蓄电池、模具等各类物品的注册喵~
 *
 * @author liudongyu
 */
@SuppressWarnings("unused")
public final class MISCTWFItems {
	private static final DeferredRegister<Item> REGISTER = DeferredRegister.create(Registries.ITEM, MODID);

	/**
	 * 材料物品注册器喵~
	 * <p>
	 * 包含各类合成材料、作物等基础物品的注册喵~
	 */
	public static final class Materials {
		/**
		 * 纱布喵~
		 */
		public static final ItemEntry<Item> GAUZE = ItemEntry.register(
				"gauze", () -> new Item(new Item.Properties())
		);		/**
		 * 白色水晶核喵~
		 */
		public static final ItemEntry<Item> WHITE_CRYSTAL_CORE = ItemEntry.register(
				"white_crystal_core", () -> new Item(new Item.Properties())
		);		/**
		 * 虚能结晶喵~
		 */
		public static final ItemEntry<Item> VOID_CRYSTAL = ItemEntry.register(
				"void_crystal", () -> new Item(new Item.Properties())
		);		/**
		 * 虚能锭喵~
		 */
		public static final ItemEntry<Item> WAYFARER_INGOT = ItemEntry.register(
				"wayfarer_ingot", () -> new Item(new Item.Properties())
		);		/**
		 * 橡胶板喵~
		 */
		public static final ItemEntry<Item> RUBBER_PLATE = ItemEntry.register(
				"rubber_plate", () -> new Item(new Item.Properties())
		);
		/**
		 * 二阶脑核喵~
		 */
		public static final ItemEntry<Item> SECOND_BRAIN_CORE = ItemEntry.register(
				"second_brain_core", () -> new Item(new Item.Properties())
		);
		/**
		 * 空注射器喵~
		 */
		public static final ItemEntry<Item> SYRINGE = ItemEntry.register(
				"syringe", () -> new Item(new Item.Properties())
		);
		/**
		 * 玻璃棒喵~
		 */
		public static final ItemEntry<Item> GLASS_ROD = ItemEntry.register(
				"glass_rod", () -> new Item(new Item.Properties().stacksTo(1))
		);
		/**
		 * 神秘血肉喵~
		 */
		public static final ItemEntry<Item> MYSTERIOUS_FLESH = ItemEntry.register(
				"mysterious_flesh", () -> new Item(new Item.Properties())
		);
		/**
		 * 变异药品喵~
		 */
		public static final ItemEntry<Item> MUTANT_POTION = ItemEntry.register(
				"mutant_potion", () -> new Item(new Item.Properties().stacksTo(1))
		);		/**
		 * 紫外线灯底座喵~
		 */
		public static final ItemEntry<Item> LAMP_PEDESTAL = ItemEntry.register(
				"lamp_pedestal", () -> new Item(new Item.Properties())
		);		/**
		 * 紫外LED灯喵~
		 */
		public static final ItemEntry<Item> UV_LED = ItemEntry.register(
				"uv_led", () -> new Item(new Item.Properties())
		);
		/**
		 * 旅行背包永恒枪械工坊子弹槽喵~
		 */
		public static final ItemEntry<Item> TRAVELERS_BACKPACK_TAC_SLOT = ItemEntry.register(
				"travelers_backpack_tac_slot", () -> new Item(new Item.Properties().stacksTo(1))
		);
		/**
		 * 变异药剂桶喵~
		 */
		public static final ItemEntry<Item> MUTANT_POTION_BUCKET = ItemEntry.register(
				"mutant_potion_bucket", () -> new Item(new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1))
		);
		/**
		 * 深渊病毒免疫药剂桶喵~
		 */
		public static final ItemEntry<Item> ABYSS_VIRUS_VACCINE_BUCKET = ItemEntry.register(
				"abyss_virus_vaccine_bucket", () -> new Item(new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1))
		);
		private Materials() {
		}

		/**
		 * 初始化方法，触发类加载喵~
		 */
		public static void init() {
			// 触发静态字段初始化喵~
		}
	}

	/**
	 * 深渊病毒免疫注射剂喵~
	 */
	public static final ItemEntry<AbyssVirusVaccine> ABYSS_VIRUS_VACCINE = ItemEntry.register(
			"abyss_virus_vaccine", () -> new AbyssVirusVaccine(new Item.Properties().stacksTo(1))
	);
	/**
	 * 初始化物品注册器喵~
	 *
	 * @param bus 事件总线喵~
	 */
	public static void init(IEventBus bus) {
		Materials.init();

		REGISTER.register(bus);
	}

	/**
	 * 物品注册入口类，封装物品注册逻辑喵~
	 *
	 * @param <T> 物品类型喵~
	 */
	public static class ItemEntry<T extends Item> implements Supplier<T>, ItemLike {
		private static final List<ItemEntry<?>> ITEMS = Lists.newArrayList();

		private final DeferredHolder<Item, T> regObject;
		private final Consumer<ItemStack> tabStackModifier;

		/**
		 * 构造方法喵~
		 *
		 * @param regObject 延迟注册对象喵~
		 * @param tabStackModifier 物品堆叠修改器喵~
		 */
		private ItemEntry(DeferredHolder<Item, T> regObject, Consumer<ItemStack> tabStackModifier) {
			this.regObject = regObject;
			this.tabStackModifier = tabStackModifier;
		}

		/**
		 * 注册物品喵~
		 *
		 * @param name 物品注册名喵~
		 * @param make 物品构造函数喵~
		 * @param <T> 物品类型喵~
		 * @return 物品注册条目喵~
		 */
		public static <T extends Item> ItemEntry<T> register(String name, Supplier<? extends T> make) {
			return new ItemEntry<>(REGISTER.register(name, make), stack -> {});
		}

		/**
		 * 注册物品喵~
		 *
		 * @param name 物品注册名喵~
		 * @param make 物品构造函数喵~
		 * @param tabStackModifier 物品堆叠修改器喵~
		 * @param <T> 物品类型喵~
		 * @return 物品注册条目喵~
		 */
		public static <T extends Item> ItemEntry<T> register(String name, Supplier<? extends T> make, Consumer<ItemStack> tabStackModifier) {
			return new ItemEntry<>(REGISTER.register(name, make), tabStackModifier);
		}

		/**
		 * 获取注册的物品实例喵~
		 *
		 * @return 物品实例喵~
		 */
		@Override
		public T get() {
			return this.regObject.get();
		}

		/**
		 * 获取物品实例（实现 ItemLike 接口）喵~
		 *
		 * @return 物品实例喵~
		 */
		@Override
		public Item asItem() {
			return this.regObject.get();
		}

		/**
		 * 获取物品的注册ID喵~
		 *
		 * @return 资源位置喵~
		 */
		public ResourceLocation getId() {
			return this.regObject.getId();
		}

		/**
		 * 获取所有已注册的物品堆叠喵~
		 * <br/>
		 * 便于在创造物品栏中获取物品喵~
		 *
		 * @return 物品堆叠流喵~
		 */
		public static Stream<ItemStack> getItems() {
			return ITEMS.stream().map(ItemEntry::toTabStack);
		}

		/**
		 * 获取随机物品喵~
		 * @param random 随机数生成器喵~
		 * @return 随机物品喵~
		 */
		public static ItemStack getRandom(RandomSource random) {
			return new ItemStack(Util.getRandom(ITEMS, random));
		}

		private ItemStack toTabStack() {
			ItemStack ret = new ItemStack(this.regObject.get());
			this.tabStackModifier.accept(ret);
			return ret;
		}
	}
}
