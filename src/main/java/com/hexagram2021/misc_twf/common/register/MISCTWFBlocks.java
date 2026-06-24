package com.hexagram2021.misc_twf.common.register;

import com.google.common.collect.ImmutableList;
import com.hexagram2021.misc_twf.SurviveInTheWinterFrontier;
import com.hexagram2021.misc_twf.common.block.*;
import com.hexagram2021.misc_twf.common.infrastructure.compat.ModCreateCompat;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;
import java.util.function.Supplier;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;
import static net.minecraft.world.level.block.Blocks.*;

/**
 * 方块注册器，负责注册模组中的所有方块喵~
 * <p>
 * 包含紫外线灯、回收熔炉、尸体方块、装饰方块等各类方块的注册喵~
 *
 * @author liudongyu
 */
@SuppressWarnings("unused")
public final class MISCTWFBlocks {
	private static final DeferredRegister<Block> REGISTER = DeferredRegister.create(Registries.BLOCK, MODID);

	/**
	 * 强紫外线照射灯方块，可发光和禁止怪物生成，且具有能量存储功能喵~
	 */
	public static final BlockEntry<UltravioletLampBlock> ULTRAVIOLET_LAMP = new BlockEntry<>(
			"ultraviolet_lamp",
			() -> BlockBehaviour.Properties.of().instabreak()
					.lightLevel(blockState -> blockState.getValue(UltravioletLampBlock.LIT) ? 15 : 0).sound(SoundType.METAL).noOcclusion(),
			UltravioletLampBlock::new
	);

	/**
	 * 模具分离器方块，与 Create 模组联动使用喵~
	 */
	public static final com.tterrag.registrate.util.entry.BlockEntry<MoldDetacherBlock> MOLD_DETACHER = ModCreateCompat.REGISTRATE
			.block("mold_detacher", MoldDetacherBlock::new)
			.initialProperties(() -> PODZOL)
			.properties(properties -> properties.strength(2.0F).sound(SoundType.WOOD).noOcclusion())
			.simpleItem()
			.register();
	/**
	 * 模具加工台方块，与 Create 模组联动使用喵~
	 */
	public static final com.tterrag.registrate.util.entry.BlockEntry<MoldWorkbenchBlock> MOLD_WORKBENCH = ModCreateCompat.REGISTRATE
			.block("mold_workbench", MoldWorkbenchBlock::new)
			.initialProperties(() -> OAK_PLANKS)
			.properties(properties -> properties.strength(2.0F).sound(SoundType.WOOD))
			.simpleItem()
			.register();

	/**
	 * 回收炉方块，用于回收物品喵~
	 */
	public static final BlockEntry<RecoveryFurnaceBlock> RECOVERY_FURNACE = new BlockEntry<>(
			"recovery_furnace",
			() -> BlockBehaviour.Properties.of().instrument(NoteBlockInstrument.BASEDRUM).mapColor(MapColor.TERRACOTTA_RED).strength(5.0F, 6.0F),
			RecoveryFurnaceBlock::new
	);

	/**
	 * 机械外壳方块喵~
	 */
	public static final BlockEntry<Block> MECHANICAL_ENCLOSURE = new BlockEntry<>(
			"mechanical_enclosure",
			() -> BlockBehaviour.Properties.of().instrument(NoteBlockInstrument.BASEDRUM).mapColor(MapColor.TERRACOTTA_BLACK).strength(2.0F),
			Block::new
	);

	/**
	 * 装变异药品的炼药锅喵~
	 */
	public static final BlockEntry<MutantPotionCauldronBlock> MUTANT_POTION_CAULDRON = new BlockEntry<>(
			"mutant_potion_cauldron",
			() -> BlockBehaviour.Properties.ofFullCopy(CAULDRON).lightLevel(blockState -> 1),
			MutantPotionCauldronBlock::new, null
	);
	/**
	 * 装深渊病毒免疫药剂的炼药锅喵~
	 */
	public static final BlockEntry<AbyssVirusVaccineCauldronBlock> ABYSS_VIRUS_VACCINE_CAULDRON = new BlockEntry<>(
			"abyss_virus_vaccine_cauldron",
			() -> BlockBehaviour.Properties.ofFullCopy(CAULDRON),
			AbyssVirusVaccineCauldronBlock::new, null
	);

	/**
	 * 血块喵~
	 */
	public static final BlockEntry<Block> BLOOD_BLOCK = new BlockEntry<>("blood_block", () -> BlockBehaviour.Properties.ofFullCopy(NETHER_WART_BLOCK), Block::new);
	/**
	 * 血肉块喵~
	 */
	public static final BlockEntry<Block> FLESH_AND_BLOOD_BLOCK = new BlockEntry<>("flesh_and_blood_block", () -> BlockBehaviour.Properties.ofFullCopy(NETHER_WART_BLOCK), Block::new);
	/**
	 * 血肉块（暗）喵~
	 */
	public static final BlockEntry<Block> FLESH_AND_BLOOD_BLOCK_D = new BlockEntry<>("flesh_and_blood_block_d", () -> BlockBehaviour.Properties.ofFullCopy(NETHER_WART_BLOCK), Block::new);
	/**
	 * 血肉块（极暗）喵~
	 */
	public static final BlockEntry<Block> FLESH_AND_BLOOD_BLOCK_DD = new BlockEntry<>("flesh_and_blood_block_dd", () -> BlockBehaviour.Properties.ofFullCopy(NETHER_WART_BLOCK), Block::new);
	/**
	 * 血肉台阶喵~
	 */
	public static final BlockEntry<Block> FLESH_AND_BLOOD_SLAB = new BlockEntry<>("flesh_and_blood_slab", () -> BlockBehaviour.Properties.ofFullCopy(NETHER_WART_BLOCK), p -> new SlabBlock(
			p.isSuffocating(
					(state, world, pos) -> FLESH_AND_BLOOD_BLOCK.defaultBlockState().isSuffocating(world, pos) && state.getValue(SlabBlock.TYPE) == SlabType.DOUBLE
			).isRedstoneConductor(
					(state, world, pos) -> FLESH_AND_BLOOD_BLOCK.defaultBlockState().isRedstoneConductor(world, pos) && state.getValue(SlabBlock.TYPE) == SlabType.DOUBLE
			)
	));
	/**
	 * 血肉台阶（暗）喵~
	 */
	public static final BlockEntry<Block> FLESH_AND_BLOOD_SLAB_D = new BlockEntry<>("flesh_and_blood_slab_d", () -> BlockBehaviour.Properties.ofFullCopy(NETHER_WART_BLOCK), p -> new SlabBlock(
			p.isSuffocating(
					(state, world, pos) -> FLESH_AND_BLOOD_BLOCK_D.defaultBlockState().isSuffocating(world, pos) && state.getValue(SlabBlock.TYPE) == SlabType.DOUBLE
			).isRedstoneConductor(
					(state, world, pos) -> FLESH_AND_BLOOD_BLOCK_D.defaultBlockState().isRedstoneConductor(world, pos) && state.getValue(SlabBlock.TYPE) == SlabType.DOUBLE
			)
	));
	/**
	 * 血肉台阶（极暗）喵~
	 */
	public static final BlockEntry<Block> FLESH_AND_BLOOD_SLAB_DD = new BlockEntry<>("flesh_and_blood_slab_dd", () -> BlockBehaviour.Properties.ofFullCopy(NETHER_WART_BLOCK), p -> new SlabBlock(
			p.isSuffocating(
					(state, world, pos) -> FLESH_AND_BLOOD_BLOCK_DD.defaultBlockState().isSuffocating(world, pos) && state.getValue(SlabBlock.TYPE) == SlabType.DOUBLE
			).isRedstoneConductor(
					(state, world, pos) -> FLESH_AND_BLOOD_BLOCK_DD.defaultBlockState().isRedstoneConductor(world, pos) && state.getValue(SlabBlock.TYPE) == SlabType.DOUBLE
			)
	));
	/**
	 * 感染泥土喵~
	 */
	public static final BlockEntry<Block> INFECTED_DIRT = new BlockEntry<>("infected_dirt", () -> BlockBehaviour.Properties.ofFullCopy(DIRT), Block::new);
	/**
	 * 感染泥土（暗）喵~
	 */
	public static final BlockEntry<Block> INFECTED_DIRT_D = new BlockEntry<>("infected_dirt_d", () -> BlockBehaviour.Properties.ofFullCopy(DIRT), Block::new);
	/**
	 * 感染泥土（极暗）喵~
	 */
	public static final BlockEntry<Block> INFECTED_DIRT_DD = new BlockEntry<>("infected_dirt_dd", () -> BlockBehaviour.Properties.ofFullCopy(DIRT), Block::new);
	/**
	 * 感染草方块喵~
	 */
	public static final BlockEntry<Block> INFECTED_GRASS_BLOCK = new BlockEntry<>("infected_grass_block", () -> BlockBehaviour.Properties.ofFullCopy(GRASS_BLOCK), Block::new);
	/**
	 * 感染草方块（暗）喵~
	 */
	public static final BlockEntry<Block> INFECTED_GRASS_BLOCK_D = new BlockEntry<>("infected_grass_block_d", () -> BlockBehaviour.Properties.ofFullCopy(GRASS_BLOCK), Block::new);
	/**
	 * 感染草方块（极暗）喵~
	 */
	public static final BlockEntry<Block> INFECTED_GRASS_BLOCK_DD = new BlockEntry<>("infected_grass_block_dd", () -> BlockBehaviour.Properties.ofFullCopy(GRASS_BLOCK), Block::new);
	/**
	 * 肠子喵~
	 */
	public static final BlockEntry<Block> INTESTINE = new BlockEntry<>("intestine", () -> BlockBehaviour.Properties.ofFullCopy(REDSTONE_WIRE).sound(SoundType.NETHER_WART).noLootTable(), BloodstainBlock::new);
	/**
	 * 血迹喵~
	 */
	public static final BlockEntry<Block> BLOODSTAIN = new BlockEntry<>("bloodstain", () -> BlockBehaviour.Properties.ofFullCopy(REDSTONE_WIRE).sound(SoundType.NETHER_WART).noLootTable(), BloodstainBlock::new);
	/**
	 * 肋骨骨架喵~
	 */
	public static final BlockEntry<Block> RIBS = new BlockEntry<>("ribs", () -> BlockBehaviour.Properties.ofFullCopy(REDSTONE_WIRE).sound(SoundType.BONE_BLOCK), RibsBlock::new);

	/**
	 * 怪物蛋喵~
	 */
	public static final BlockEntry<MonsterEggBlock> MONSTER_EGG = new BlockEntry<>("monster_egg", () -> BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).strength(0.5F).sound(SoundType.METAL).noOcclusion(), MonsterEggBlock::new);

	/**
	 * 冬小麦作物方块喵~
	 */
	public static final BlockEntry<CropBlock> WINTER_WHEAT = new BlockEntry<>("winter_wheat", () -> BlockBehaviour.Properties.ofFullCopy(WHEAT), props -> new CropBlock(props) {
		@Override
		protected ItemLike getBaseSeedId() {
			return MISCTWFItems.Materials.WINTER_WHEAT;
		}
	}, null);

	private MISCTWFBlocks() {
	}

	/**
	 * 动物尸体方块注册器喵~
	 * <p>
	 * 包含各类动物尸体方块的注册喵~
	 */
	public static final class DeadAnimals {
		/**
		 * 鸡的尸体喵~
		 */
		public static final BlockEntry<DeadAnimalBlock> DEAD_CHICKEN = new BlockEntry<>(
				"dead_chicken",
				() -> BlockBehaviour.Properties.ofFullCopy(NETHER_WART_BLOCK).noOcclusion(),
				props -> new DeadAnimalBlock(() -> ImmutableList.of(
						new ItemStack(Items.BONE),
						new ItemStack(Items.BONE),
						new ItemStack(Items.CHICKEN),
						new ItemStack(Items.FEATHER),
						new ItemStack(Items.FEATHER)
				), 3, props)
		);
		/**
		 * 牛的尸体喵~
		 */
		public static final BlockEntry<DeadAnimalBlock> DEAD_COW = new BlockEntry<>(
				"dead_cow",
				() -> BlockBehaviour.Properties.ofFullCopy(NETHER_WART_BLOCK).noOcclusion(),
				props -> new DeadAnimalBlock(() -> ImmutableList.of(
						new ItemStack(Items.BEEF),
						new ItemStack(Items.BEEF),
						new ItemStack(Items.BEEF),
						new ItemStack(Items.BONE),
						new ItemStack(Items.BONE),
						new ItemStack(Items.BONE),
						new ItemStack(Items.LEATHER)
				), 5, props)
		);
		/**
		 * 山羊的尸体喵~
		 */
		public static final BlockEntry<DeadAnimalBlock> DEAD_GOAT = new BlockEntry<>(
				"dead_goat",
				() -> BlockBehaviour.Properties.ofFullCopy(NETHER_WART_BLOCK).noOcclusion(),
				props -> new DeadAnimalBlock(() -> {
					ImmutableList.Builder<ItemStack> builder = ImmutableList.builder();
					Item rawGoat = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("delightful", "raw_goat"));
					Item goatFur = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("cold_sweat", "goat_fur"));
					builder.add(new ItemStack(rawGoat), new ItemStack(rawGoat));
					builder.add(new ItemStack(goatFur), new ItemStack(goatFur));
					builder.add(new ItemStack(Items.BONE), new ItemStack(Items.BONE));
					return builder.build();
				}, 8, props)
		);
		/**
		 * 马的尸体喵~
		 */
		public static final BlockEntry<DeadAnimalBlock> DEAD_HORSE = new BlockEntry<>(
				"dead_horse",
				() -> BlockBehaviour.Properties.ofFullCopy(NETHER_WART_BLOCK).noOcclusion(),
				props -> new DeadAnimalBlock(() -> {
					ImmutableList.Builder<ItemStack> builder = ImmutableList.builder();
					Item rawHorse = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("kubejs", "raw_horse_meat"));
					builder.add(new ItemStack(rawHorse), new ItemStack(rawHorse));
					builder.add(new ItemStack(Items.BONE), new ItemStack(Items.BONE), new ItemStack(Items.LEATHER));
					return builder.build();
				}, 5, props)
		);
		/**
		 * 猪的尸体喵~
		 */
		public static final BlockEntry<DeadAnimalBlock> DEAD_PIG = new BlockEntry<>(
				"dead_pig",
				() -> BlockBehaviour.Properties.ofFullCopy(NETHER_WART_BLOCK).noOcclusion(),
				props -> new DeadAnimalBlock(() -> ImmutableList.of(
						new ItemStack(Items.BONE),
						new ItemStack(Items.BONE),
						new ItemStack(Items.LEATHER),
						new ItemStack(Items.PORKCHOP),
						new ItemStack(Items.PORKCHOP),
						new ItemStack(Items.PORKCHOP)
				), 5, props)
		);
		/**
		 * 北极熊的尸体喵~
		 */
		public static final BlockEntry<DeadAnimalBlock> DEAD_POLARBEAR = new BlockEntry<>(
				"dead_polarbear",
				() -> BlockBehaviour.Properties.ofFullCopy(NETHER_WART_BLOCK).noOcclusion(),
				props -> new DeadAnimalBlock(() -> {
					ImmutableList.Builder<ItemStack> builder = ImmutableList.builder();
					Item polarBear = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("kubejs", "polar_bear"));
					Item rawBear = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("kubejs", "raw_bear_meat"));
					builder.add(new ItemStack(polarBear), new ItemStack(polarBear));
					builder.add(new ItemStack(rawBear), new ItemStack(rawBear), new ItemStack(rawBear), new ItemStack(rawBear));
					builder.add(new ItemStack(Items.BONE), new ItemStack(Items.BONE));
					return builder.build();
				}, 10, props)
		);
		/**
		 * 兔子的尸体喵~
		 */
		public static final BlockEntry<DeadAnimalBlock> DEAD_RABBIT = new BlockEntry<>(
				"dead_rabbit",
				() -> BlockBehaviour.Properties.ofFullCopy(NETHER_WART_BLOCK).noOcclusion(),
				props -> new DeadAnimalBlock(() -> ImmutableList.of(
						new ItemStack(Items.RABBIT),
						new ItemStack(Items.RABBIT_FOOT),
						new ItemStack(Items.RABBIT_FOOT),
						new ItemStack(Items.RABBIT_HIDE)
				), 3, props)
		);
		/**
		 * 羊的尸体喵~
		 */
		public static final BlockEntry<DeadAnimalBlock> DEAD_SHEEP = new BlockEntry<>(
				"dead_sheep",
				() -> BlockBehaviour.Properties.ofFullCopy(NETHER_WART_BLOCK).noOcclusion(),
				props -> new DeadAnimalBlock(() -> {
					ImmutableList.Builder<ItemStack> builder = ImmutableList.builder();
					builder.add(new ItemStack(Items.BONE), new ItemStack(Items.BONE), new ItemStack(Items.MUTTON), new ItemStack(Items.MUTTON));
					Item rawGigot = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("kubejs", "raw_mutton_leg"));
					builder.add(new ItemStack(rawGigot), new ItemStack(rawGigot));
					return builder.build();
				}, 5, props)
		);
		/**
		 * 狼的尸体喵~
		 */
		public static final BlockEntry<DeadAnimalBlock> DEAD_WOLF = new BlockEntry<>(
				"dead_wolf",
				() -> BlockBehaviour.Properties.ofFullCopy(NETHER_WART_BLOCK).noOcclusion(),
				props -> new DeadAnimalBlock(() -> {
					ImmutableList.Builder<ItemStack> builder = ImmutableList.builder();
					Item rawWolf = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("kubejs", "raw_wolf_meat"));
					builder.add(new ItemStack(rawWolf), new ItemStack(rawWolf, 2));
					builder.add(new ItemStack(Items.BONE), new ItemStack(Items.BONE));
					return builder.build();
				}, 4, props)
		);

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
	 * 装饰方块注册器喵~
	 * <p>
	 * 包含各类装饰方块的注册喵~
	 */
	public static final class Decorations {
		/**
		 * 白色垃圾袋喵~
		 */
		public static final BlockEntry<TrashBagBlock> WHITE_TRASH_BAG = new BlockEntry<>(
				"white_trash_bag",
				() -> BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_WHITE).strength(0.1F).sound(SoundType.CROP).noOcclusion(),
				TrashBagBlock::new
		);
		/**
		 * 小型垃圾袋喵~
		 */
		public static final BlockEntry<TinyTrashBagBlock> TINY_TRASH_BAG = new BlockEntry<>(
				"tiny_trash_bag",
				() -> BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_GRAY).strength(0.1F).sound(SoundType.CROP).noOcclusion(),
				TinyTrashBagBlock::new
		);
		/**
		 * 垃圾堆喵~
		 */
		public static final BlockEntry<TrashDumpBlock> TRASH_DUMP = new BlockEntry<>(
				"trash_dump",
				() -> BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_GRAY).strength(0.1F).sound(SoundType.CROP).noOcclusion(),
				TrashDumpBlock::new
		);
		/**
		 * 油桶喵~
		 */
		public static final BlockEntry<JerricanBlock> JERRICAN = new BlockEntry<>(
				"jerrican",
				() -> BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_RED).strength(5.0F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.METAL).noOcclusion(),
				JerricanBlock::new
		);
		/**
		 * 易爆油桶喵~
		 */
		public static final BlockEntry<ExplosiveJerricanBlock> EXPLOSIVE_JERRICAN = new BlockEntry<>(
				"explosive_jerrican",
				() -> BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_RED).strength(5.0F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.METAL).noOcclusion(),
				ExplosiveJerricanBlock::new
		);
		/**
		 * 病床喵~
		 */
		public static final BlockEntry<SickbedBlock> SICKBED = new BlockEntry<>(
				"sickbed",
				() -> BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLUE).strength(5.0F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.METAL).noOcclusion(),
				SickbedBlock::new
		);
		/**
		 * 带血的病床喵~
		 */
		public static final BlockEntry<SickbedBlock> SICKBED_WITH_BLOOD = new BlockEntry<>(
				"sickbed_with_blood",
				() -> BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLUE).strength(5.0F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.METAL).noOcclusion(),
				SickbedBlock::new
		);
		/**
		 * 躺着尸体的病床喵~
		 */
		public static final BlockEntry<SickbedBlock> SICKBED_WITH_BODY = new BlockEntry<>(
				"sickbed_with_body",
				() -> BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLUE).strength(5.0F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.METAL).noOcclusion(),
				SickbedBlock::new
		);
		/**
		 * 尸体（开膛破肚）喵~
		 */
		public static final BlockEntry<BodyBlock> BODY_DISEMBOWELLED = new BlockEntry<>(
				"body_disembowelled",
				() -> BlockBehaviour.Properties.ofFullCopy(NETHER_WART_BLOCK).noOcclusion(),
				props -> new BodyBlock(6, 12, props)
		);
		/**
		 * 尸体（半截）喵~
		 */
		public static final BlockEntry<BodyBlock> BODY_HALF = new BlockEntry<>(
				"body_half",
				() -> BlockBehaviour.Properties.ofFullCopy(NETHER_WART_BLOCK).noOcclusion(),
				props -> new BodyBlock(6, 7, props)
		);
		/**
		 * 无头尸体喵~
		 */
		public static final BlockEntry<BodyBlock> BODY_HEADLESS = new BlockEntry<>(
				"body_headless",
				() -> BlockBehaviour.Properties.ofFullCopy(NETHER_WART_BLOCK).noOcclusion(),
				props -> new BodyBlock(6, 10, props)
		);
		/**
		 * 无头军人尸体喵~
		 */
		public static final BlockEntry<BodyBlock> BODY_HEADLESS_SOLDIER = new BlockEntry<>(
				"body_headless_soldier",
				() -> BlockBehaviour.Properties.ofFullCopy(NETHER_WART_BLOCK).noOcclusion(),
				props -> new BodyBlock(6, 10, props)
		);
		/**
		 * 钉在墙上的尸体喵~
		 */
		public static final BlockEntry<NailedBodyBlock> BODY_NAILED = new BlockEntry<>(
				"body_nailed",
				() -> BlockBehaviour.Properties.ofFullCopy(NETHER_WART_BLOCK).noOcclusion(),
				NailedBodyBlock::new
		);
		/**
		 * 钉在墙上的军人尸体喵~
		 */
		public static final BlockEntry<NailedBodyBlock> BODY_NAILED_SOLDIER = new BlockEntry<>(
				"body_nailed_soldier",
				() -> BlockBehaviour.Properties.ofFullCopy(NETHER_WART_BLOCK).noOcclusion(),
				NailedBodyBlock::new
		);
		/**
		 * 尸体（摆放）喵~
		 */
		public static final BlockEntry<BodyBlock> BODY_ORGA = new BlockEntry<>(
				"body_orga",
				() -> BlockBehaviour.Properties.ofFullCopy(NETHER_WART_BLOCK).noOcclusion(),
				props -> new BodyBlock(6, 12, props)
		);
		/**
		 * 倒地防化服尸体喵~
		 */
		public static final BlockEntry<BodyBlock> BODY_ORGA_HAZMAT_SUIT = new BlockEntry<>(
				"body_orga_hazmat_suit",
				() -> BlockBehaviour.Properties.ofFullCopy(NETHER_WART_BLOCK).noOcclusion(),
				props -> new BodyBlock(6, 12, props)
		);
		/**
		 * 尸体（坐姿）喵~
		 */
		public static final BlockEntry<BodyBlock> BODY_SIT = new BlockEntry<>(
				"body_sit",
				() -> BlockBehaviour.Properties.ofFullCopy(NETHER_WART_BLOCK).noOcclusion(),
				props -> new BodyBlock(6, 8, props)
		);
		/**
		 * 军人尸体（坐姿）喵~
		 */
		public static final BlockEntry<BodyBlock> BODY_SIT_SOLDIER = new BlockEntry<>(
				"body_sit_soldier",
				() -> BlockBehaviour.Properties.ofFullCopy(NETHER_WART_BLOCK).noOcclusion(),
				props -> new BodyBlock(6, 8, props)
		);
		/**
		 * 研究员尸体（坐姿）喵~
		 */
		public static final BlockEntry<BodyBlock> BODY_SIT_RESEARCHER = new BlockEntry<>(
				"body_sit_researcher",
				() -> BlockBehaviour.Properties.ofFullCopy(NETHER_WART_BLOCK).noOcclusion(),
				props -> new BodyBlock(6, 8, props)
		);
		/**
		 * 骷髅尸体喵~
		 */
		public static final BlockEntry<BodyBlock> BODY_SKELETON = new BlockEntry<>(
				"body_skeleton",
				() -> BlockBehaviour.Properties.ofFullCopy(NETHER_WART_BLOCK).sound(SoundType.BONE_BLOCK).noOcclusion(),
				props -> new BodyBlock(6, 14, props)
		);
		/**
		 * 尸体袋喵~
		 */
		public static final BlockEntry<BodyBlock> BODY_BAG = new BlockEntry<>(
				"body_bag",
				() -> BlockBehaviour.Properties.ofFullCopy(NETHER_WART_BLOCK).sound(SoundType.CROP).noOcclusion(),
				props -> new BodyBlock(6, 8, props)
		);
		/**
		 * 输液架喵~
		 */
		public static final BlockEntry<IVStandBlock> IV_STAND = new BlockEntry<>(
				"iv_stand",
				() -> BlockBehaviour.Properties.ofFullCopy(IRON_BARS),
				IVStandBlock::new
		);
		/**
		 * 空的输液架喵~
		 */
		public static final BlockEntry<IVStandBlock> IV_STAND_EMPTY = new BlockEntry<>(
				"iv_stand_empty",
				() -> BlockBehaviour.Properties.ofFullCopy(IRON_BARS),
				IVStandBlock::new
		);
		/**
		 * 垃圾桶喵~
		 */
		public static final BlockEntry<TrashCanBlock> TRASH_CAN = new BlockEntry<>(
				"trash_can",
				() -> BlockBehaviour.Properties.ofFullCopy(IRON_BARS),
				TrashCanBlock::new
		);
		/**
		 * 轮椅喵~
		 */
		public static final BlockEntry<WheelchairBlock> WHEELCHAIR = new BlockEntry<>(
				"wheelchair",
				() -> BlockBehaviour.Properties.ofFullCopy(IRON_BARS),
				WheelchairBlock::new
		);
		/**
		 * 堆积的沙袋喵~
		 */
		public static final BlockEntry<PackedSandbagBlock> PACKED_SANDBAG = new BlockEntry<>(
				"packed_sandbag",
				() -> BlockBehaviour.Properties.of().mapColor(MapColor.SAND).strength(5.0F, 6.0F).sound(SoundType.SAND).noOcclusion(),
				PackedSandbagBlock::new
		);
		/**
		 * 沙袋喵~
		 */
		public static final BlockEntry<SandbagBlock> SANDBAG = new BlockEntry<>(
				"sandbag",
				() -> BlockBehaviour.Properties.of().mapColor(MapColor.SAND).strength(5.0F, 6.0F).sound(SoundType.SAND).noOcclusion(),
				SandbagBlock::new
		);
		/**
		 * 手推床喵~
		 */
		public static final BlockEntry<WheeledStretcherBlock> WHEELED_STRETCHER = new BlockEntry<>(
				"wheeled_stretcher",
				() -> BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLUE).strength(5.0F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.METAL).noOcclusion(),
				WheeledStretcherBlock::new
		);
		/**
		 * 躺着尸体的手推床喵~
		 */
		public static final BlockEntry<WheeledStretcherBlock> WHEELED_STRETCHER_WITH_BODY = new BlockEntry<>(
				"wheeled_stretcher_with_body",
				() -> BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLUE).strength(5.0F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.METAL).noOcclusion(),
				WheeledStretcherBlock::new
		);
		/**
		 * 废纸喵~
		 */
		public static final BlockEntry<WastepaperBlock> WASTE_PAPER = new BlockEntry<>(
				"wastepaper",
				() -> BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_WHITE).strength(0.1F).sound(SoundType.CROP).noCollission(),
				WastepaperBlock::new
		);
		/**
		 * M4A1 卡宾枪喵~
		 */
		public static final BlockEntry<M4A1CarbineBlock> M4A1_CARBINE = new BlockEntry<>(
				"m4a1_carbine",
				() -> BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_LIGHT_GRAY).strength(0.1F).sound(SoundType.METAL).noOcclusion(),
				M4A1CarbineBlock::new
		);
		/**
		 * 弹孔喵~
		 */
		public static final BlockEntry<BulletHoleBlock> BULLET_HOLE = new BlockEntry<>(
				"bullet_hole",
				() -> BlockBehaviour.Properties.of().instabreak().sound(SoundType.METAL).noCollission(),
				BulletHoleBlock::new
		);
		/**
		 * 信号棒喵~
		 */
		public static final BlockEntry<SignalRodBlock> SIGNAL_ROD = new BlockEntry<>(
				"signal_rod",
				() -> BlockBehaviour.Properties.ofFullCopy(SOUL_TORCH).noOcclusion(),
				SignalRodBlock::new
		);
		/**
		 * 显微镜喵~
		 */
		public static final BlockEntry<MicroscopeBlock> MICROSCOPE = new BlockEntry<>(
				"microscope",
				() -> BlockBehaviour.Properties.ofFullCopy(IRON_BLOCK).noOcclusion(),
				MicroscopeBlock::new
		);
		/**
		 * 监控喵~
		 */
		public static final BlockEntry<SurveillanceCameraBlock> SURVEILLANCE_CAMERA = new BlockEntry<>(
				"surveillance_camera",
				() -> BlockBehaviour.Properties.ofFullCopy(IRON_BLOCK).noOcclusion(),
				SurveillanceCameraBlock::new
		);
		/**
		 * 配电箱喵~
		 */
		public static final BlockEntry<DistributionBoxBlock> DISTRIBUTION_BOX = new BlockEntry<>(
				"distribution_box",
				() -> BlockBehaviour.Properties.ofFullCopy(IRON_BLOCK).noOcclusion(),
				DistributionBoxBlock::new
		);
		/**
		 * 弹药箱喵~
		 */
		public static final BlockEntry<AmmunitionBoxBlock> AMMUNITION_BOX = new BlockEntry<>(
				"ammunition_box",
				() -> BlockBehaviour.Properties.of().mapColor(MapColor.GRASS).instrument(NoteBlockInstrument.BASS)
						.strength(1.0F, 1.0F)
						.sound(SoundType.BAMBOO).noOcclusion(),
				AmmunitionBoxBlock::new
		);
		/**
		 * 堆叠弹药箱喵~
		 */
		public static final BlockEntry<StackedAmmunitionBoxBlock> STACKED_AMMUNITION_BOX = new BlockEntry<>(
				"stacked_ammunition_box",
				() -> BlockBehaviour.Properties.ofFullCopy(AMMUNITION_BOX.get()).noOcclusion(),
				StackedAmmunitionBoxBlock::new
		);
		/**
		 * 5.56弹匣喵~
		 */
		public static final BlockEntry<SignalRodBlock> MAGAZINE_556 = new BlockEntry<>(
				"magazine_556",
				() -> BlockBehaviour.Properties.ofFullCopy(SOUL_TORCH).noOcclusion().lightLevel(state -> 0),
				SignalRodBlock::new
		);

		private Decorations() {
		}

		/**
		 * 初始化方法，触发类加载喵~
		 */
		private static void init() {
			// 触发静态字段初始化喵~
		}
	}

	/**
	 * 初始化方块注册器喵~
	 *
	 * @param bus 事件总线喵~
	 */
	public static void init(IEventBus bus) {
		REGISTER.register(bus);

		DeadAnimals.init();
		Decorations.init();
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
		 * 构造方法，自动注册到默认创造模式标签页喵~
		 *
		 * @param name 方块注册名喵~
		 * @param properties 方块属性提供者喵~
		 * @param make 方块构造函数喵~
		 */
		public BlockEntry(String name, Supplier<BlockBehaviour.Properties> properties, Function<BlockBehaviour.Properties, T> make) {
			this(name, properties, make, SurviveInTheWinterFrontier.ITEM_GROUP);
		}

		/**
		 * 构造方法，可指定创造模式标签页喵~
		 *
		 * @param name 方块注册名喵~
		 * @param properties 方块属性提供者喵~
		 * @param make 方块构造函数喵~
		 * @param tab 创造模式标签页，为 null 则不注册物品喵~
		 */
		public BlockEntry(String name, Supplier<BlockBehaviour.Properties> properties, Function<BlockBehaviour.Properties, T> make, @Nullable CreativeModeTab tab) {
			this.properties = properties;
			this.regObject = REGISTER.register(name, () -> make.apply(properties.get()));
			if(tab != null) {
				MISCTWFItems.ItemEntry.register(name, () -> new BlockItem(this.regObject.get(), new Item.Properties()));
			}
		}

		/**
		 * 获取注册的方块实例喵~
		 *
		 * @return 方块实例喵~
		 */
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
		 * 获取方块的注册ID喵~
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

		/**
		 * 获取方块对应的物品喵~
		 *
		 * @return 方块物品喵~
		 */
		public Item asItem() {
			return this.get().asItem();
		}
	}
}
