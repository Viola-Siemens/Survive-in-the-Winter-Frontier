package com.hexagram2021.misc_twf_wildlife.common.register;

import com.google.common.collect.Lists;
import net.minecraft.Util;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.hexagram2021.misc_twf_wildlife.MiscTwfWildlife.CONTENT_NAMESPACE;

/**
 * 农牧生态模块物品注册器（仅本模块域内的材料类物品）喵~
 *
 * @author liudongyu
 */
@SuppressWarnings("unused")
public final class MISCTWFItems {
	private static final DeferredRegister<Item> REGISTER = DeferredRegister.create(Registries.ITEM, CONTENT_NAMESPACE);

	/**
	 * 材料物品注册器喵~
	 */
	public static final class Materials {
		/** 纱线喵~ */
		public static final ItemEntry<Item> YARN = ItemEntry.register(
				"yarn", () -> new Item(new Item.Properties())
		);
		/** 动物粪便（骨粉）喵~ */
		public static final ItemEntry<BoneMealItem> ANIMAL_POOP = ItemEntry.register(
				"animal_poop", () -> new BoneMealItem(new Item.Properties())
		);
		/** 冬小麦喵~ */
		public static final ItemEntry<Item> WINTER_WHEAT = ItemEntry.register(
				"winter_wheat", () -> new Item(new Item.Properties())
		);
		/** 冬小麦种子喵~ */
		public static final ItemEntry<ItemNameBlockItem> WINTER_WHEAT_SEEDS = ItemEntry.register(
				"winter_wheat_seeds", () -> new ItemNameBlockItem(MISCTWFBlocks.WINTER_WHEAT.get(), new Item.Properties())
		);

		private Materials() {
		}

		/**
		 * 初始化方法，触发类加载喵~
		 */
		private static void init() {
			// 触发静态字段初始化喵~
		}
	}

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
		 * @param regObject        延迟注册对象喵~
		 * @param tabStackModifier 物品堆叠修改器喵~
		 */
		private ItemEntry(DeferredHolder<Item, T> regObject, Consumer<ItemStack> tabStackModifier) {
			this.regObject = regObject;
			this.tabStackModifier = tabStackModifier;
			ITEMS.add(this);
		}

		/**
		 * 注册物品喵~
		 *
		 * @param name 物品注册名喵~
		 * @param make 物品构造函数喵~
		 * @param <T>  物品类型喵~
		 * @return 物品注册条目喵~
		 */
		public static <T extends Item> ItemEntry<T> register(String name, Supplier<? extends T> make) {
			return new ItemEntry<>(REGISTER.register(name, make), stack -> {
			});
		}

		/**
		 * 注册物品喵~
		 *
		 * @param name             物品注册名喵~
		 * @param make             物品构造函数喵~
		 * @param tabStackModifier 物品堆叠修改器喵~
		 * @param <T>              物品类型喵~
		 * @return 物品注册条目喵~
		 */
		public static <T extends Item> ItemEntry<T> register(String name, Supplier<? extends T> make, Consumer<ItemStack> tabStackModifier) {
			return new ItemEntry<>(REGISTER.register(name, make), tabStackModifier);
		}

		@Override
		public T get() {
			return this.regObject.get();
		}

		@Override
		public Item asItem() {
			return this.regObject.get();
		}

		/**
		 * 获取物品的注册 ID 喵~
		 *
		 * @return 资源位置喵~
		 */
		public ResourceLocation getId() {
			return this.regObject.getId();
		}

		/**
		 * 获取所有已注册的物品堆叠，便于在创造物品栏中展示喵~
		 *
		 * @return 物品堆叠流喵~
		 */
		public static Stream<ItemStack> getItems() {
			return ITEMS.stream().map(ItemEntry::toTabStack);
		}

		/**
		 * 获取随机物品喵~
		 *
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

	private MISCTWFItems() {
	}
}
