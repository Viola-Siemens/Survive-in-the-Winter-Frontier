package com.hexagram2021.misc_twf_adventure.common.register;

import com.hexagram2021.misc_twf_adventure.common.block.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;
import java.util.function.Supplier;

import static com.hexagram2021.misc_twf_adventure.MiscTwfAdventure.CONTENT_NAMESPACE;

/**
 * 冒险与探索模块的方块注册喵~
 *
 * <p>注册 id 与 `assets/misc_twf/blockstates/<id>.json`、`data/misc_twf/loot_tables/blocks/<id>.json`
 * 一一对应；方块属性按方块类的形状/材质语义推断，推断值均标了 TODO喵~</p>
 *
 * @author liudongyu
 */
public final class MISCTWFBlocks {
	private static final DeferredRegister<Block> REGISTER = DeferredRegister.create(Registries.BLOCK, CONTENT_NAMESPACE);

	/** 方块物品工厂（默认 BlockItem）喵~ */
	private static final Function<Block, Item> DEFAULT_BLOCK_ITEM = block -> new BlockItem(block, new Item.Properties());

	// ===== 炼药锅 / 怪物蛋喵~ =====

	/** 变异药水炼药锅喵~（无 BlockItem，交互走炼药锅/桶） */
	public static final BlockEntry<MutantPotionCauldronBlock> MUTANT_POTION_CAULDRON = new BlockEntry<>(
			"mutant_potion_cauldron",
			() -> BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).strength(2.0F).noOcclusion(),
			MutantPotionCauldronBlock::new,
			null
	);
	/** 深渊病毒疫苗炼药锅喵~（无 BlockItem，交互走炼药锅/桶） */
	public static final BlockEntry<AbyssVirusVaccineCauldronBlock> ABYSS_VIRUS_VACCINE_CAULDRON = new BlockEntry<>(
			"abyss_virus_vaccine_cauldron",
			() -> BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE).strength(2.0F).noOcclusion(),
			AbyssVirusVaccineCauldronBlock::new,
			null
	);
	/** 怪物蛋方块喵~ */
	public static final BlockEntry<MonsterEggBlock> MONSTER_EGG = new BlockEntry<>(
			"monster_egg",
			() -> BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PINK).strength(0.5F).noOcclusion(),
			MonsterEggBlock::new,
			DEFAULT_BLOCK_ITEM
	);

	// ===== 医疗/军事/生活装饰喵~（材质与硬度按“金属/木/布”推断，TODO 待对齐原 1.18.2 手感） =====

	/** 弹药箱喵~ */
	public static final BlockEntry<AmmunitionBoxBlock> AMMUNITION_BOX = wood("ammunition_box", AmmunitionBoxBlock::new);
	/** 堆叠弹药箱喵~ */
	public static final BlockEntry<StackedAmmunitionBoxBlock> STACKED_AMMUNITION_BOX = wood("stacked_ammunition_box", StackedAmmunitionBoxBlock::new);
	/** 配给箱喵~ */
	public static final BlockEntry<DistributionBoxBlock> DISTRIBUTION_BOX = metal("distribution_box", 2.0F, DistributionBoxBlock::new);
	/** 油桶喵~ */
	public static final BlockEntry<JerricanBlock> JERRICAN = metal("jerrican", 1.5F, JerricanBlock::new);
	/** 打开的油桶喵~ */
	public static final BlockEntry<JerricanBlock> OPEN_JERRICAN = metal("open_jerrican", 1.5F, JerricanBlock::new);
	/** 易燃油桶喵~ */
	public static final BlockEntry<ExplosiveJerricanBlock> EXPLOSIVE_JERRICAN = metal("explosive_jerrican", 1.5F, ExplosiveJerricanBlock::new);
	/** 输液架喵~ */
	public static final BlockEntry<IVStandBlock> IV_STAND = metal("iv_stand", 1.0F, IVStandBlock::new);
	/** 空输液架喵~ */
	public static final BlockEntry<IVStandBlock> IV_STAND_EMPTY = metal("iv_stand_empty", 1.0F, IVStandBlock::new);
	/** 显微镜喵~ */
	public static final BlockEntry<MicroscopeBlock> MICROSCOPE = metal("microscope", 1.5F, MicroscopeBlock::new);
	/** 信号杆喵~ */
	public static final BlockEntry<SignalRodBlock> SIGNAL_ROD = metal("signal_rod", 0.5F, SignalRodBlock::new);
	/** 监控摄像头喵~ */
	public static final BlockEntry<SurveillanceCameraBlock> SURVEILLANCE_CAMERA = metal("surveillance_camera", 1.0F, SurveillanceCameraBlock::new);
	/** 垃圾桶喵~ */
	public static final BlockEntry<TrashCanBlock> TRASH_CAN = metal("trash_can", 1.5F, TrashCanBlock::new);
	/** 垃圾堆喵~ */
	public static final BlockEntry<TrashDumpBlock> TRASH_DUMP = metal("trash_dump", 1.5F, TrashDumpBlock::new);
	/** M4A1 卡宾枪摆件喵~ */
	public static final BlockEntry<M4A1CarbineBlock> M4A1_CARBINE = metal("m4a1_carbine", 0.5F, M4A1CarbineBlock::new);

	/** 沙袋喵~ */
	public static final BlockEntry<SandbagBlock> SANDBAG = cloth("sandbag", 1.0F, SandbagBlock::new);
	/** 压实沙袋喵~ */
	public static final BlockEntry<PackedSandbagBlock> PACKED_SANDBAG = cloth("packed_sandbag", 1.0F, PackedSandbagBlock::new);
	/** 肋骨堆喵~（骨块材质） */
	public static final BlockEntry<RibsBlock> RIBS = bone("ribs", 0.5F, RibsBlock::new);
	/** 病床喵~ */
	public static final BlockEntry<SickbedBlock> SICKBED = cloth("sickbed", 0.5F, SickbedBlock::new);
	/** 带血的病床喵~ */
	public static final BlockEntry<SickbedBlock> SICKBED_WITH_BLOOD = cloth("sickbed_with_blood", 0.5F, SickbedBlock::new);
	/** 带尸体的病床喵~ */
	public static final BlockEntry<SickbedBlock> SICKBED_WITH_BODY = cloth("sickbed_with_body", 0.5F, SickbedBlock::new);
	/** 轮椅喵~ */
	public static final BlockEntry<WheelchairBlock> WHEELCHAIR = metal("wheelchair", 0.5F, WheelchairBlock::new);
	/** 轮式担架喵~ */
	public static final BlockEntry<WheeledStretcherBlock> WHEELED_STRETCHER = cloth("wheeled_stretcher", 0.5F, WheeledStretcherBlock::new);
	/** 带尸体的轮式担架喵~ */
	public static final BlockEntry<WheeledStretcherBlock> WHEELED_STRETCHER_WITH_BODY = cloth("wheeled_stretcher_with_body", 0.5F, WheeledStretcherBlock::new);
	/** 小垃圾袋喵~ */
	public static final BlockEntry<TinyTrashBagBlock> TINY_TRASH_BAG = cloth("tiny_trash_bag", 0.2F, TinyTrashBagBlock::new);
	/** 白色垃圾袋喵~ */
	public static final BlockEntry<TrashBagBlock> WHITE_TRASH_BAG = cloth("white_trash_bag", 0.2F, TrashBagBlock::new);

	// ===== 无碰撞的纯装饰方块喵~（原 1.18.2 全部注册了 BlockItem，此处保持一致） =====

	/** 血迹喵~ */
	public static final BlockEntry<BloodstainBlock> BLOODSTAIN = noCollision("bloodstain", SoundType.WOOL, DEFAULT_BLOCK_ITEM, BloodstainBlock::new);
	/** 弹孔喵~ */
	public static final BlockEntry<BulletHoleBlock> BULLET_HOLE = noCollision("bullet_hole", SoundType.STONE, DEFAULT_BLOCK_ITEM, BulletHoleBlock::new);
	/** 废纸屑喵~ */
	public static final BlockEntry<WastepaperBlock> WASTEPAPER = noCollision("wastepaper", SoundType.WOOL, DEFAULT_BLOCK_ITEM, WastepaperBlock::new);

	// ===== 血肉/感染地块/肠子（1.18.2 原版注册，迁移时遗漏，此处补回）喵~ =====

	/** 血块喵~ */
	public static final BlockEntry<Block> BLOOD_BLOCK = flesh("blood_block", Block::new);
	/** 血肉块喵~ */
	public static final BlockEntry<Block> FLESH_AND_BLOOD_BLOCK = flesh("flesh_and_blood_block", Block::new);
	/** 血肉块（暗）喵~ */
	public static final BlockEntry<Block> FLESH_AND_BLOOD_BLOCK_D = flesh("flesh_and_blood_block_d", Block::new);
	/** 血肉块（极暗）喵~ */
	public static final BlockEntry<Block> FLESH_AND_BLOOD_BLOCK_DD = flesh("flesh_and_blood_block_dd", Block::new);
	/** 血肉台阶喵~ */
	public static final BlockEntry<SlabBlock> FLESH_AND_BLOOD_SLAB = fleshSlab("flesh_and_blood_slab");
	/** 血肉台阶（暗）喵~ */
	public static final BlockEntry<SlabBlock> FLESH_AND_BLOOD_SLAB_D = fleshSlab("flesh_and_blood_slab_d");
	/** 血肉台阶（极暗）喵~ */
	public static final BlockEntry<SlabBlock> FLESH_AND_BLOOD_SLAB_DD = fleshSlab("flesh_and_blood_slab_dd");
	/** 感染泥土喵~ */
	public static final BlockEntry<Block> INFECTED_DIRT = dirtCopy("infected_dirt", Block::new);
	/** 感染泥土（暗）喵~ */
	public static final BlockEntry<Block> INFECTED_DIRT_D = dirtCopy("infected_dirt_d", Block::new);
	/** 感染泥土（极暗）喵~ */
	public static final BlockEntry<Block> INFECTED_DIRT_DD = dirtCopy("infected_dirt_dd", Block::new);
	/** 感染草方块喵~ */
	public static final BlockEntry<Block> INFECTED_GRASS_BLOCK = grassCopy("infected_grass_block", Block::new);
	/** 感染草方块（暗）喵~ */
	public static final BlockEntry<Block> INFECTED_GRASS_BLOCK_D = grassCopy("infected_grass_block_d", Block::new);
	/** 感染草方块（极暗）喵~ */
	public static final BlockEntry<Block> INFECTED_GRASS_BLOCK_DD = grassCopy("infected_grass_block_dd", Block::new);
	/** 肠子喵~（无掉落，与血迹同形状类） */
	public static final BlockEntry<BloodstainBlock> INTESTINE = new BlockEntry<>(
			"intestine",
			() -> BlockBehaviour.Properties.ofFullCopy(Blocks.REDSTONE_WIRE).sound(SoundType.NETHER_WART).noLootTable(),
			BloodstainBlock::new,
			DEFAULT_BLOCK_ITEM
	);
	/** 5.56 弹匣摆件喵~ */
	public static final BlockEntry<SignalRodBlock> MAGAZINE_556 = new BlockEntry<>(
			"magazine_556",
			() -> BlockBehaviour.Properties.ofFullCopy(Blocks.SOUL_TORCH).noOcclusion().lightLevel(state -> 0),
			SignalRodBlock::new,
			DEFAULT_BLOCK_ITEM
	);

	// ===== 尸体类喵~（BodyBlock 的 xSize/zSize 由对应模型像素范围推断，TODO 待进游戏核对形状） =====

	/** 尸袋喵~ */
	public static final BlockEntry<BodyBlock> BODY_BAG = body("body_bag", 4, 14);
	/** 开膛尸体喵~ */
	public static final BlockEntry<BodyBlock> BODY_DISEMBOWELLED = body("body_disembowelled", 7, 16);
	/** 半截尸体喵~ */
	public static final BlockEntry<BodyBlock> BODY_HALF = body("body_half", 15, 13);
	/** 无头尸体喵~ */
	public static final BlockEntry<BodyBlock> BODY_HEADLESS = body("body_headless", 11, 15);
	/** 无头士兵尸体喵~ */
	public static final BlockEntry<BodyBlock> BODY_HEADLESS_SOLDIER = body("body_headless_soldier", 11, 19);
	/** 内脏尸体喵~ */
	public static final BlockEntry<BodyBlock> BODY_ORGA = body("body_orga", 10, 18);
	/** 穿防化服的内脏尸体喵~ */
	public static final BlockEntry<BodyBlock> BODY_ORGA_HAZMAT_SUIT = body("body_orga_hazmat_suit", 10, 18);
	/** 坐姿尸体喵~ */
	public static final BlockEntry<BodyBlock> BODY_SIT = body("body_sit", 10, 7);
	/** 坐姿研究员尸体喵~ */
	public static final BlockEntry<BodyBlock> BODY_SIT_RESEARCHER = body("body_sit_researcher", 10, 7);
	/** 坐姿士兵尸体喵~ */
	public static final BlockEntry<BodyBlock> BODY_SIT_SOLDIER = body("body_sit_soldier", 10, 8);
	/** 骷髅尸体喵~ */
	public static final BlockEntry<BodyBlock> BODY_SKELETON = body("body_skeleton", 16, 21);
	/** 被钉住的尸体喵~ */
	public static final BlockEntry<NailedBodyBlock> BODY_NAILED = nailedBody("body_nailed");
	/** 被钉住的士兵尸体喵~ */
	public static final BlockEntry<NailedBodyBlock> BODY_NAILED_SOLDIER = nailedBody("body_nailed_soldier");

	/**
	 * 注册全部方块喵~
	 *
	 * @param bus 模块事件总线喵~
	 */
	public static void init(IEventBus bus) {
		REGISTER.register(bus);
	}

	private static <T extends Block> BlockEntry<T> wood(String name, Function<BlockBehaviour.Properties, T> factory) {
		return new BlockEntry<>(name, () -> BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(0.5F)
				.sound(SoundType.WOOD).noOcclusion(), factory, DEFAULT_BLOCK_ITEM);
	}

	private static <T extends Block> BlockEntry<T> metal(String name, float strength, Function<BlockBehaviour.Properties, T> factory) {
		return new BlockEntry<>(name, () -> BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(strength)
				.sound(SoundType.METAL).noOcclusion(), factory, DEFAULT_BLOCK_ITEM);
	}

	private static <T extends Block> BlockEntry<T> cloth(String name, float strength, Function<BlockBehaviour.Properties, T> factory) {
		return new BlockEntry<>(name, () -> BlockBehaviour.Properties.of().mapColor(MapColor.WOOL).strength(strength)
				.sound(SoundType.WOOL).noOcclusion(), factory, DEFAULT_BLOCK_ITEM);
	}

	private static <T extends Block> BlockEntry<T> bone(String name, float strength, Function<BlockBehaviour.Properties, T> factory) {
		return new BlockEntry<>(name, () -> BlockBehaviour.Properties.of().mapColor(MapColor.SAND).strength(strength)
				.sound(SoundType.BONE_BLOCK).noOcclusion(), factory, DEFAULT_BLOCK_ITEM);
	}

	private static <T extends Block> BlockEntry<T> noCollision(String name, SoundType sound,
	                                                          @Nullable Function<? super T, Item> itemFactory,
	                                                          Function<BlockBehaviour.Properties, T> factory) {
		return new BlockEntry<>(name, () -> BlockBehaviour.Properties.of().mapColor(MapColor.NONE).instabreak()
				.sound(sound).noCollission().noOcclusion(), factory, itemFactory);
	}

	private static <T extends Block> BlockEntry<T> flesh(String name, Function<BlockBehaviour.Properties, T> factory) {
		return new BlockEntry<>(name, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.NETHER_WART_BLOCK),
				factory, DEFAULT_BLOCK_ITEM);
	}

	private static BlockEntry<SlabBlock> fleshSlab(String name) {
		// 与原版一致：只有双台阶才视为完整方块（可窒息/可作红石导体）喵~
		return new BlockEntry<>(name, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.NETHER_WART_BLOCK)
						.isSuffocating((state, level, pos) -> state.getValue(SlabBlock.TYPE) == SlabType.DOUBLE)
						.isRedstoneConductor((state, level, pos) -> state.getValue(SlabBlock.TYPE) == SlabType.DOUBLE),
				SlabBlock::new, DEFAULT_BLOCK_ITEM);
	}

	private static <T extends Block> BlockEntry<T> dirtCopy(String name, Function<BlockBehaviour.Properties, T> factory) {
		return new BlockEntry<>(name, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.DIRT), factory, DEFAULT_BLOCK_ITEM);
	}

	private static <T extends Block> BlockEntry<T> grassCopy(String name, Function<BlockBehaviour.Properties, T> factory) {
		return new BlockEntry<>(name, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.GRASS_BLOCK), factory, DEFAULT_BLOCK_ITEM);
	}

	private static BlockEntry<BodyBlock> body(String name, int xSize, int zSize) {
		// TODO 形状尺寸由对应模型像素范围推断，且可能超出单方块边界，进游戏后按实际表现微调喵~
		return new BlockEntry<>(name, () -> BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_RED).instabreak()
				.sound(SoundType.WOOL).noCollission().noOcclusion(),
				properties -> new BodyBlock(xSize, zSize, properties), DEFAULT_BLOCK_ITEM);
	}

	private static BlockEntry<NailedBodyBlock> nailedBody(String name) {
		return new BlockEntry<>(name, () -> BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_RED).instabreak()
				.sound(SoundType.WOOL).noCollission().noOcclusion(), NailedBodyBlock::new, DEFAULT_BLOCK_ITEM);
	}

	private MISCTWFBlocks() {
	}

	/**
	 * 方块注册条目喵~：同时持有方块与可选的方块物品，便于其它注册类引用喵~
	 *
	 * @param <T> 方块类型喵~
	 * @author liudongyu
	 */
	public static final class BlockEntry<T extends Block> implements Supplier<T> {
		private final DeferredHolder<Block, T> holder;
		@Nullable
		private final MISCTWFItems.ItemEntry<? extends Item> itemEntry;

		/**
		 * 构造方块注册条目喵~
		 *
		 * @param name        注册名喵~
		 * @param properties  方块属性提供者喵~
		 * @param factory     方块构造器喵~
		 * @param itemFactory 方块物品构造器；为 null 表示不注册方块物品喵~
		 */
		BlockEntry(String name, Supplier<BlockBehaviour.Properties> properties,
		           Function<BlockBehaviour.Properties, T> factory, @Nullable Function<? super T, Item> itemFactory) {
			this.holder = REGISTER.register(name, () -> factory.apply(properties.get()));
			this.itemEntry = itemFactory == null
					? null
					: MISCTWFItems.ItemEntry.register(name, () -> itemFactory.apply(this.holder.get()));
		}

		@Override
		public T get() {
			return this.holder.get();
		}

		/**
		 * 获取该方块的默认方块状态喵~
		 *
		 * @return 默认方块状态喵~
		 */
		public BlockState defaultBlockState() {
			return this.get().defaultBlockState();
		}

		/**
		 * 获取对应的方块物品条目喵~
		 *
		 * @return 方块物品条目；未注册方块物品时为 null喵~
		 */
		@Nullable
		public MISCTWFItems.ItemEntry<? extends Item> item() {
			return this.itemEntry;
		}
	}
}