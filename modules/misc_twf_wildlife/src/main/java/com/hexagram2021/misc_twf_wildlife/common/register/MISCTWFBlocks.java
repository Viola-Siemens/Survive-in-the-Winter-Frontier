package com.hexagram2021.misc_twf_wildlife.common.register;

import com.google.common.collect.ImmutableList;
import com.hexagram2021.misc_twf_wildlife.common.block.DeadAnimalBlock;
import com.hexagram2021.misc_twf_wildlife.common.util.MISCTWFLogger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.hexagram2021.misc_twf_wildlife.MiscTwfWildlife.CONTENT_NAMESPACE;
import static net.minecraft.world.level.block.Blocks.NETHER_WART_BLOCK;
import static net.minecraft.world.level.block.Blocks.WHEAT;

/**
 * 农牧生态模块方块注册器（仅本模块域内的方块）喵~
 *
 * <p>包含冬小麦作物与九种动物尸体方块喵~</p>
 *
 * @author liudongyu
 */
@SuppressWarnings("unused")
public final class MISCTWFBlocks {
	private static final DeferredRegister<Block> REGISTER = DeferredRegister.create(Registries.BLOCK, CONTENT_NAMESPACE);

	/**
	 * 冬小麦作物方块喵~
	 */
	public static final BlockEntry<CropBlock> WINTER_WHEAT = new BlockEntry<>("winter_wheat", () -> BlockBehaviour.Properties.ofFullCopy(WHEAT), props -> new CropBlock(props) {
		@Override
		protected ItemLike getBaseSeedId() {
			return MISCTWFItems.Materials.WINTER_WHEAT;
		}
	}, null);

	/**
	 * 初始化方块注册器喵~
	 *
	 * @param bus 事件总线喵~
	 */
	public static void init(IEventBus bus) {
		REGISTER.register(bus);

		DeadAnimals.init();
	}

	/**
	 * 动物尸体方块注册器喵~
	 *
	 * <p>尸体掉落物中引用第三方模组物品（kubejs/cold_sweat/delightful）时按 6.2-E5 判空并过滤，
	 * 缺失时记录日志并跳过该条目，不产出 AIR 物品堆叠喵~</p>
	 */
	public static final class DeadAnimals {
		/** 鸡的尸体喵~ */
		public static final BlockEntry<DeadAnimalBlock> DEAD_CHICKEN = new BlockEntry<>(
				"dead_chicken",
				() -> BlockBehaviour.Properties.ofFullCopy(NETHER_WART_BLOCK).noOcclusion(),
				props -> new DeadAnimalBlock(() -> List.of(
						new ItemStack(Items.BONE),
						new ItemStack(Items.BONE),
						new ItemStack(Items.CHICKEN),
						new ItemStack(Items.FEATHER),
						new ItemStack(Items.FEATHER)
				), 3, props)
		);
		/** 牛的尸体喵~ */
		public static final BlockEntry<DeadAnimalBlock> DEAD_COW = new BlockEntry<>(
				"dead_cow",
				() -> BlockBehaviour.Properties.ofFullCopy(NETHER_WART_BLOCK).noOcclusion(),
				props -> new DeadAnimalBlock(() -> List.of(
						new ItemStack(Items.BEEF),
						new ItemStack(Items.BEEF),
						new ItemStack(Items.BEEF),
						new ItemStack(Items.BONE),
						new ItemStack(Items.BONE),
						new ItemStack(Items.BONE),
						new ItemStack(Items.LEATHER)
				), 5, props)
		);
		/** 山羊的尸体喵~ */
		public static final BlockEntry<DeadAnimalBlock> DEAD_GOAT = new BlockEntry<>(
				"dead_goat",
				() -> BlockBehaviour.Properties.ofFullCopy(NETHER_WART_BLOCK).noOcclusion(),
				props -> new DeadAnimalBlock(() -> {
					ImmutableList.Builder<ItemStack> builder = ImmutableList.builder();
					addLoot(builder, optionalItem("delightful", "raw_goat"), 2);
					addLoot(builder, optionalItem("cold_sweat", "goat_fur"), 2);
					addLoot(builder, Items.BONE, 2);
					return builder.build();
				}, 8, props)
		);
		/** 马的尸体喵~ */
		public static final BlockEntry<DeadAnimalBlock> DEAD_HORSE = new BlockEntry<>(
				"dead_horse",
				() -> BlockBehaviour.Properties.ofFullCopy(NETHER_WART_BLOCK).noOcclusion(),
				props -> new DeadAnimalBlock(() -> {
					ImmutableList.Builder<ItemStack> builder = ImmutableList.builder();
					addLoot(builder, optionalItem("kubejs", "raw_horse_meat"), 2);
					addLoot(builder, Items.BONE, 2);
					addLoot(builder, Items.LEATHER, 1);
					return builder.build();
				}, 5, props)
		);
		/** 猪的尸体喵~ */
		public static final BlockEntry<DeadAnimalBlock> DEAD_PIG = new BlockEntry<>(
				"dead_pig",
				() -> BlockBehaviour.Properties.ofFullCopy(NETHER_WART_BLOCK).noOcclusion(),
				props -> new DeadAnimalBlock(() -> List.of(
						new ItemStack(Items.BONE),
						new ItemStack(Items.BONE),
						new ItemStack(Items.LEATHER),
						new ItemStack(Items.PORKCHOP),
						new ItemStack(Items.PORKCHOP),
						new ItemStack(Items.PORKCHOP)
				), 5, props)
		);
		/** 北极熊的尸体喵~ */
		public static final BlockEntry<DeadAnimalBlock> DEAD_POLARBEAR = new BlockEntry<>(
				"dead_polarbear",
				() -> BlockBehaviour.Properties.ofFullCopy(NETHER_WART_BLOCK).noOcclusion(),
				props -> new DeadAnimalBlock(() -> {
					ImmutableList.Builder<ItemStack> builder = ImmutableList.builder();
					addLoot(builder, optionalItem("kubejs", "polar_bear"), 2);
					addLoot(builder, optionalItem("kubejs", "raw_bear_meat"), 4);
					addLoot(builder, Items.BONE, 2);
					return builder.build();
				}, 10, props)
		);
		/** 兔子的尸体喵~ */
		public static final BlockEntry<DeadAnimalBlock> DEAD_RABBIT = new BlockEntry<>(
				"dead_rabbit",
				() -> BlockBehaviour.Properties.ofFullCopy(NETHER_WART_BLOCK).noOcclusion(),
				props -> new DeadAnimalBlock(() -> List.of(
						new ItemStack(Items.RABBIT),
						new ItemStack(Items.RABBIT_FOOT),
						new ItemStack(Items.RABBIT_FOOT),
						new ItemStack(Items.RABBIT_HIDE)
				), 3, props)
		);
		/** 羊的尸体喵~ */
		public static final BlockEntry<DeadAnimalBlock> DEAD_SHEEP = new BlockEntry<>(
				"dead_sheep",
				() -> BlockBehaviour.Properties.ofFullCopy(NETHER_WART_BLOCK).noOcclusion(),
				props -> new DeadAnimalBlock(() -> {
					ImmutableList.Builder<ItemStack> builder = ImmutableList.builder();
					addLoot(builder, Items.BONE, 2);
					addLoot(builder, Items.MUTTON, 2);
					addLoot(builder, optionalItem("kubejs", "raw_mutton_leg"), 2);
					return builder.build();
				}, 5, props)
		);
		/** 狼的尸体喵~ */
		public static final BlockEntry<DeadAnimalBlock> DEAD_WOLF = new BlockEntry<>(
				"dead_wolf",
				() -> BlockBehaviour.Properties.ofFullCopy(NETHER_WART_BLOCK).noOcclusion(),
				props -> new DeadAnimalBlock(() -> {
					ImmutableList.Builder<ItemStack> builder = ImmutableList.builder();
					addLoot(builder, optionalItem("kubejs", "raw_wolf_meat"), 1);
					addLoot(builder, optionalItem("kubejs", "raw_wolf_meat"), 2);
					addLoot(builder, Items.BONE, 2);
					return builder.build();
				}, 4, props)
		);

		/**
		 * 向掉落列表中加入物品堆叠，物品缺失（AIR）时跳过喵~
		 *
		 * @param builder 掉落列表构造器喵~
		 * @param item    物品喵~
		 * @param count   数量喵~
		 */
		private static void addLoot(ImmutableList.Builder<ItemStack> builder, @Nullable Item item, int count) {
			if(item != null && item != Items.AIR) {
				builder.add(new ItemStack(item, count));
			}
		}

		/**
		 * 按注册名可选地获取物品，缺失时记录日志并返回 null 喵~
		 *
		 * @param namespace 命名空间喵~
		 * @param path      注册路径喵~
		 * @return 物品，缺失时为 null 喵~
		 */
		@Nullable
		private static Item optionalItem(String namespace, String path) {
			ResourceLocation id = ResourceLocation.fromNamespaceAndPath(namespace, path);
			Item item = BuiltInRegistries.ITEM.get(id);
			if(item == Items.AIR) {
				MISCTWFLogger.warn("Missing optional item " + id + " for dead animal loot, entry skipped.");
				return null;
			}
			return item;
		}

		private DeadAnimals() {
		}

		/**
		 * 初始化方法，触发类加载喵~
		 */
		private static void init() {
			// 触发静态字段初始化喵~
		}
	}

	/**
	 * 方块注册入口类，封装方块注册和物品注册逻辑喵~
	 *
	 * @param <T> 方块类型喵~
	 */
	public static final class BlockEntry<T extends Block> implements Supplier<T>, ItemLike {
		private final DeferredHolder<Block, T> regObject;
		private final Supplier<BlockBehaviour.Properties> properties;

		/**
		 * 构造方法，自动注册到模块创造模式标签页喵~
		 *
		 * @param name       方块注册名喵~
		 * @param properties 方块属性提供者喵~
		 * @param make       方块构造函数喵~
		 */
		public BlockEntry(String name, Supplier<BlockBehaviour.Properties> properties, Function<BlockBehaviour.Properties, T> make) {
			this(name, properties, make, MISCTWFCreativeModeTabs.MAIN);
		}

		/**
		 * 构造方法，可指定创造模式标签页喵~
		 *
		 * @param name       方块注册名喵~
		 * @param properties 方块属性提供者喵~
		 * @param make       方块构造函数喵~
		 * @param tab        创造模式标签页，为 null 则不注册物品喵~
		 */
		public BlockEntry(String name, Supplier<BlockBehaviour.Properties> properties, Function<BlockBehaviour.Properties, T> make, @Nullable DeferredHolder<CreativeModeTab, CreativeModeTab> tab) {
			this.properties = properties;
			this.regObject = REGISTER.register(name, () -> make.apply(properties.get()));
			if(tab != null) {
				MISCTWFItems.ItemEntry.register(name, () -> new BlockItem(this.regObject.get(), new Item.Properties()));
			}
		}

		public T get() {
			return this.regObject.get();
		}

		/**
		 * 获取默认方块状态喵~
		 *
		 * @return 默认方块状态喵~
		 */
		public BlockState defaultBlockState() {
			return this.get().defaultBlockState();
		}

		/**
		 * 获取方块的注册 ID 喵~
		 *
		 * @return 资源位置喵~
		 */
		public ResourceLocation getId() {
			return this.regObject.getId();
		}

		/**
		 * 获取方块属性喵~
		 *
		 * @return 方块行为属性喵~
		 */
		public BlockBehaviour.Properties getProperties() {
			return this.properties.get();
		}

		@Override
		public Item asItem() {
			return this.get().asItem();
		}
	}

	private MISCTWFBlocks() {
	}
}
