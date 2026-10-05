package com.hexagram2021.misc_twf_industry.common.register;

import com.hexagram2021.misc_twf_industry.common.block.MoldDetacherBlock;
import com.hexagram2021.misc_twf_industry.common.block.MoldWorkbenchBlock;
import com.hexagram2021.misc_twf_industry.common.block.RecoveryFurnaceBlock;
import com.hexagram2021.misc_twf_industry.common.block.UltravioletLampBlock;
import com.hexagram2021.misc_twf_industry.common.infrastructure.compat.ModCreateCompat;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;
import java.util.function.Supplier;

import static com.hexagram2021.misc_twf_industry.MiscTwfIndustry.CONTENT_NAMESPACE;
import static net.minecraft.world.level.block.Blocks.OAK_PLANKS;

/**
 * 工业生态模块方块注册器喵~
 *
 * <p>包含紫外线灯、模具加工/分离台、回收炉与机械外壳喵~</p>
 *
 * @author liudongyu
 */
@SuppressWarnings("unused")
public final class MISCTWFBlocks {
	private static final DeferredRegister<Block> REGISTER = DeferredRegister.create(Registries.BLOCK, CONTENT_NAMESPACE);

	/** 强紫外线照射灯方块，可发光并禁止怪物生成，且具有能量存储功能喵~ */
	public static final BlockEntry<UltravioletLampBlock> ULTRAVIOLET_LAMP = new BlockEntry<>(
			"ultraviolet_lamp",
			() -> BlockBehaviour.Properties.of().instabreak()
					.lightLevel(blockState -> blockState.getValue(UltravioletLampBlock.LIT) ? 15 : 0).sound(SoundType.METAL).noOcclusion(),
			UltravioletLampBlock::new
	);

	/** 模具分离器方块，与 Create 模组联动使用喵~ */
	public static final com.tterrag.registrate.util.entry.BlockEntry<MoldDetacherBlock> MOLD_DETACHER = ModCreateCompat.REGISTRATE
			.block("mold_detacher", MoldDetacherBlock::new)
			.initialProperties(() -> net.minecraft.world.level.block.Blocks.PODZOL)
			.properties(properties -> properties.strength(2.0F).sound(SoundType.WOOD).noOcclusion())
			.simpleItem()
			.register();

	/** 模具加工台方块，与 Create 模组联动使用喵~ */
	public static final com.tterrag.registrate.util.entry.BlockEntry<MoldWorkbenchBlock> MOLD_WORKBENCH = ModCreateCompat.REGISTRATE
			.block("mold_workbench", MoldWorkbenchBlock::new)
			.initialProperties(() -> OAK_PLANKS)
			.properties(properties -> properties.strength(2.0F).sound(SoundType.WOOD))
			.simpleItem()
			.register();

	/** 回收炉方块，用于回收物品喵~ */
	public static final BlockEntry<RecoveryFurnaceBlock> RECOVERY_FURNACE = new BlockEntry<>(
			"recovery_furnace",
			() -> BlockBehaviour.Properties.of().instrument(NoteBlockInstrument.BASEDRUM).mapColor(MapColor.TERRACOTTA_RED).strength(5.0F, 6.0F),
			RecoveryFurnaceBlock::new
	);

	/** 机械外壳方块喵~ */
	public static final BlockEntry<Block> MECHANICAL_ENCLOSURE = new BlockEntry<>(
			"mechanical_enclosure",
			() -> BlockBehaviour.Properties.of().instrument(NoteBlockInstrument.BASEDRUM).mapColor(MapColor.TERRACOTTA_BLACK).strength(2.0F),
			Block::new
	);

	/**
	 * 初始化方块注册器喵~
	 *
	 * @param bus 事件总线喵~
	 */
	public static void init(IEventBus bus) {
		REGISTER.register(bus);
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
