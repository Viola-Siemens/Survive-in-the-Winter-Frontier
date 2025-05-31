package com.hexagram2021.misc_twf.common.register;

import com.google.common.collect.ImmutableList;
import com.hexagram2021.misc_twf.SurviveInTheWinterFrontier;
import com.hexagram2021.misc_twf.common.block.*;
import com.hexagram2021.misc_twf.common.infrastructure.compat.ModCreateCompat;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;
import java.util.function.Supplier;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;
import static net.minecraft.world.level.block.Blocks.*;

@SuppressWarnings("unused")
public final class MISCTWFBlocks {
	private static final DeferredRegister<Block> REGISTER = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);

	public static final BlockEntry<UltravioletLampBlock> ULTRAVIOLET_LAMP = new BlockEntry<>(
			"ultraviolet_lamp",
			() -> BlockBehaviour.Properties.of(Material.DECORATION).instabreak()
					.lightLevel(blockState -> blockState.getValue(UltravioletLampBlock.LIT) ? 15 : 0).sound(SoundType.METAL).noOcclusion(),
			UltravioletLampBlock::new
	);

	public static final com.tterrag.registrate.util.entry.BlockEntry<MoldDetacherBlock> MOLD_DETACHER = ModCreateCompat.REGISTRATE
			.block("mold_detacher", MoldDetacherBlock::new)
			.initialProperties(Material.WOOD, MaterialColor.PODZOL)
			.properties(properties -> properties.strength(2.0F).sound(SoundType.WOOD).noOcclusion())
			.simpleItem()
			.register();
	public static final com.tterrag.registrate.util.entry.BlockEntry<MoldWorkbenchBlock> MOLD_WORKBENCH = ModCreateCompat.REGISTRATE
			.block("mold_workbench", MoldWorkbenchBlock::new)
			.initialProperties(Material.WOOD)
			.properties(properties -> properties.strength(2.0F).sound(SoundType.WOOD))
			.simpleItem()
			.register();

	public static final BlockEntry<RecoveryFurnaceBlock> RECOVERY_FURNACE = new BlockEntry<>(
			"recovery_furnace",
			() -> BlockBehaviour.Properties.of(Material.STONE).strength(5.0F, 6.0F).color(MaterialColor.TERRACOTTA_RED),
			RecoveryFurnaceBlock::new
	);

	public static final BlockEntry<Block> MECHANICAL_ENCLOSURE = new BlockEntry<>(
			"mechanical_enclosure",
			() -> BlockBehaviour.Properties.of(Material.STONE).strength(2.0F).color(MaterialColor.TERRACOTTA_BLACK),
			Block::new
	);

	public static final BlockEntry<MutantPotionCauldronBlock> MUTANT_POTION_CAULDRON = new BlockEntry<>(
			"mutant_potion_cauldron",
			() -> BlockBehaviour.Properties.copy(CAULDRON).lightLevel(blockState -> 1),
			MutantPotionCauldronBlock::new, null
	);
	public static final BlockEntry<AbyssVirusVaccineCauldronBlock> ABYSS_VIRUS_VACCINE_CAULDRON = new BlockEntry<>(
			"abyss_virus_vaccine_cauldron",
			() -> BlockBehaviour.Properties.copy(CAULDRON),
			AbyssVirusVaccineCauldronBlock::new, null
	);

	public static final BlockEntry<Block> BLOOD_BLOCK = new BlockEntry<>("blood_block", () -> BlockBehaviour.Properties.copy(NETHER_WART_BLOCK), Block::new);
	public static final BlockEntry<Block> FLESH_AND_BLOOD_BLOCK = new BlockEntry<>("flesh_and_blood_block", () -> BlockBehaviour.Properties.copy(NETHER_WART_BLOCK), Block::new);
	public static final BlockEntry<Block> FLESH_AND_BLOOD_BLOCK_D = new BlockEntry<>("flesh_and_blood_block_d", () -> BlockBehaviour.Properties.copy(NETHER_WART_BLOCK), Block::new);
	public static final BlockEntry<Block> FLESH_AND_BLOOD_BLOCK_DD = new BlockEntry<>("flesh_and_blood_block_dd", () -> BlockBehaviour.Properties.copy(NETHER_WART_BLOCK), Block::new);
	public static final BlockEntry<Block> FLESH_AND_BLOOD_SLAB = new BlockEntry<>("flesh_and_blood_slab", () -> BlockBehaviour.Properties.copy(NETHER_WART_BLOCK), p -> new SlabBlock(
			p.isSuffocating(
					(state, world, pos) -> FLESH_AND_BLOOD_BLOCK.defaultBlockState().isSuffocating(world, pos) && state.getValue(SlabBlock.TYPE) == SlabType.DOUBLE
			).isRedstoneConductor(
					(state, world, pos) -> FLESH_AND_BLOOD_BLOCK.defaultBlockState().isRedstoneConductor(world, pos) && state.getValue(SlabBlock.TYPE) == SlabType.DOUBLE
			)
	));
	public static final BlockEntry<Block> FLESH_AND_BLOOD_SLAB_D = new BlockEntry<>("flesh_and_blood_slab_d", () -> BlockBehaviour.Properties.copy(NETHER_WART_BLOCK), p -> new SlabBlock(
			p.isSuffocating(
					(state, world, pos) -> FLESH_AND_BLOOD_BLOCK_D.defaultBlockState().isSuffocating(world, pos) && state.getValue(SlabBlock.TYPE) == SlabType.DOUBLE
			).isRedstoneConductor(
					(state, world, pos) -> FLESH_AND_BLOOD_BLOCK_D.defaultBlockState().isRedstoneConductor(world, pos) && state.getValue(SlabBlock.TYPE) == SlabType.DOUBLE
			)
	));
	public static final BlockEntry<Block> FLESH_AND_BLOOD_SLAB_DD = new BlockEntry<>("flesh_and_blood_slab_dd", () -> BlockBehaviour.Properties.copy(NETHER_WART_BLOCK), p -> new SlabBlock(
			p.isSuffocating(
					(state, world, pos) -> FLESH_AND_BLOOD_BLOCK_DD.defaultBlockState().isSuffocating(world, pos) && state.getValue(SlabBlock.TYPE) == SlabType.DOUBLE
			).isRedstoneConductor(
					(state, world, pos) -> FLESH_AND_BLOOD_BLOCK_DD.defaultBlockState().isRedstoneConductor(world, pos) && state.getValue(SlabBlock.TYPE) == SlabType.DOUBLE
			)
	));
	public static final BlockEntry<Block> INFECTED_DIRT = new BlockEntry<>("infected_dirt", () -> BlockBehaviour.Properties.copy(DIRT), Block::new);
	public static final BlockEntry<Block> INFECTED_DIRT_D = new BlockEntry<>("infected_dirt_d", () -> BlockBehaviour.Properties.copy(DIRT), Block::new);
	public static final BlockEntry<Block> INFECTED_DIRT_DD = new BlockEntry<>("infected_dirt_dd", () -> BlockBehaviour.Properties.copy(DIRT), Block::new);
	public static final BlockEntry<Block> INFECTED_GRASS_BLOCK = new BlockEntry<>("infected_grass_block", () -> BlockBehaviour.Properties.copy(GRASS_BLOCK), Block::new);
	public static final BlockEntry<Block> INFECTED_GRASS_BLOCK_D = new BlockEntry<>("infected_grass_block_d", () -> BlockBehaviour.Properties.copy(GRASS_BLOCK), Block::new);
	public static final BlockEntry<Block> INFECTED_GRASS_BLOCK_DD = new BlockEntry<>("infected_grass_block_dd", () -> BlockBehaviour.Properties.copy(GRASS_BLOCK), Block::new);
	public static final BlockEntry<Block> INTESTINE = new BlockEntry<>("intestine", () -> BlockBehaviour.Properties.copy(REDSTONE_WIRE).sound(SoundType.NETHER_WART).noDrops(), BloodstainBlock::new);
	public static final BlockEntry<Block> BLOODSTAIN = new BlockEntry<>("bloodstain", () -> BlockBehaviour.Properties.copy(REDSTONE_WIRE).sound(SoundType.NETHER_WART).noDrops(), BloodstainBlock::new);
	public static final BlockEntry<Block> RIBS = new BlockEntry<>("ribs", () -> BlockBehaviour.Properties.copy(REDSTONE_WIRE).sound(SoundType.BONE_BLOCK), RibsBlock::new);

	public static final BlockEntry<MonsterEggBlock> MONSTER_EGG = new BlockEntry<>("monster_egg", () -> BlockBehaviour.Properties.of(Material.EGG, MaterialColor.COLOR_GRAY).strength(0.5F).sound(SoundType.METAL).noOcclusion(), MonsterEggBlock::new);

	private MISCTWFBlocks() {
	}

	public static final class DeadAnimals {
		public static final BlockEntry<DeadAnimalBlock> DEAD_CHICKEN = new BlockEntry<>(
				"dead_chicken",
				() -> BlockBehaviour.Properties.copy(NETHER_WART_BLOCK).noOcclusion(),
				props -> new DeadAnimalBlock(ImmutableList.of(
						new ItemStack(Items.BONE),
						new ItemStack(Items.BONE),
						new ItemStack(Items.CHICKEN),
						new ItemStack(Items.FEATHER),
						new ItemStack(Items.FEATHER)
				), 3, props)
		);
		public static final BlockEntry<DeadAnimalBlock> DEAD_COW = new BlockEntry<>(
				"dead_cow",
				() -> BlockBehaviour.Properties.copy(NETHER_WART_BLOCK).noOcclusion(),
				props -> new DeadAnimalBlock(ImmutableList.of(
						new ItemStack(Items.BEEF),
						new ItemStack(Items.BEEF),
						new ItemStack(Items.BEEF),
						new ItemStack(Items.BONE),
						new ItemStack(Items.BONE),
						new ItemStack(Items.BONE),
						new ItemStack(Items.LEATHER)
				), 5, props)
		);
		public static final BlockEntry<DeadAnimalBlock> DEAD_GOAT = new BlockEntry<>(
				"dead_goat",
				() -> BlockBehaviour.Properties.copy(NETHER_WART_BLOCK).noOcclusion(),
				props -> new DeadAnimalBlock(Util.make(() -> {
					ImmutableList.Builder<ItemStack> builder = ImmutableList.builder();
					Item rawGoat = ForgeRegistries.ITEMS.getValue(new ResourceLocation("delightful", "raw_goat"));
					Item goatFur = ForgeRegistries.ITEMS.getValue(new ResourceLocation("cold_sweat", "goat_fur"));
					if(rawGoat != null) {
						builder.add(new ItemStack(rawGoat), new ItemStack(rawGoat));
					}
					if(goatFur != null) {
						builder.add(new ItemStack(goatFur), new ItemStack(goatFur));
					}
					builder.add(new ItemStack(Items.BONE), new ItemStack(Items.BONE));
					return builder.build();
				}), 8, props)
		);
		public static final BlockEntry<DeadAnimalBlock> DEAD_HORSE = new BlockEntry<>(
				"dead_horse",
				() -> BlockBehaviour.Properties.copy(NETHER_WART_BLOCK).noOcclusion(),
				props -> new DeadAnimalBlock(Util.make(() -> {
					ImmutableList.Builder<ItemStack> builder = ImmutableList.builder();
					Item rawHorse = ForgeRegistries.ITEMS.getValue(new ResourceLocation("kubejs", "sheng_horsemeat"));
					if(rawHorse != null) {
						builder.add(new ItemStack(rawHorse), new ItemStack(rawHorse));
					}
					builder.add(new ItemStack(Items.BONE), new ItemStack(Items.BONE), new ItemStack(Items.LEATHER));
					return builder.build();
				}), 5, props)
		);
		public static final BlockEntry<DeadAnimalBlock> DEAD_PIG = new BlockEntry<>(
				"dead_pig",
				() -> BlockBehaviour.Properties.copy(NETHER_WART_BLOCK).noOcclusion(),
				props -> new DeadAnimalBlock(ImmutableList.of(
						new ItemStack(Items.BONE),
						new ItemStack(Items.BONE),
						new ItemStack(Items.LEATHER),
						new ItemStack(Items.PORKCHOP),
						new ItemStack(Items.PORKCHOP),
						new ItemStack(Items.PORKCHOP)
				), 5, props)
		);
		public static final BlockEntry<DeadAnimalBlock> DEAD_POLARBEAR = new BlockEntry<>(
				"dead_polarbear",
				() -> BlockBehaviour.Properties.copy(NETHER_WART_BLOCK).noOcclusion(),
				props -> new DeadAnimalBlock(Util.make(() -> {
					ImmutableList.Builder<ItemStack> builder = ImmutableList.builder();
					Item polarBear = ForgeRegistries.ITEMS.getValue(new ResourceLocation("kubejs", "polar_bear"));
					Item rawBear = ForgeRegistries.ITEMS.getValue(new ResourceLocation("kubejs", "sheng_bearmeat"));
					if(polarBear != null) {
						builder.add(new ItemStack(polarBear), new ItemStack(polarBear));
					}
					if(rawBear != null) {
						builder.add(new ItemStack(rawBear), new ItemStack(rawBear), new ItemStack(rawBear), new ItemStack(rawBear));
					}
					builder.add(new ItemStack(Items.BONE), new ItemStack(Items.BONE));
					return builder.build();
				}), 10, props)
		);
		public static final BlockEntry<DeadAnimalBlock> DEAD_RABBIT = new BlockEntry<>(
				"dead_rabbit",
				() -> BlockBehaviour.Properties.copy(NETHER_WART_BLOCK).noOcclusion(),
				props -> new DeadAnimalBlock(ImmutableList.of(
						new ItemStack(Items.RABBIT),
						new ItemStack(Items.RABBIT_FOOT),
						new ItemStack(Items.RABBIT_FOOT),
						new ItemStack(Items.RABBIT_HIDE)
				), 3, props)
		);
		public static final BlockEntry<DeadAnimalBlock> DEAD_SHEEP = new BlockEntry<>(
				"dead_sheep",
				() -> BlockBehaviour.Properties.copy(NETHER_WART_BLOCK).noOcclusion(),
				props -> new DeadAnimalBlock(Util.make(() -> {
					ImmutableList.Builder<ItemStack> builder = ImmutableList.builder();
					builder.add(new ItemStack(Items.BONE), new ItemStack(Items.BONE), new ItemStack(Items.MUTTON), new ItemStack(Items.MUTTON));
					Item rawGigot = ForgeRegistries.ITEMS.getValue(new ResourceLocation("kubejs", "sheng_yangtui"));
					if(rawGigot != null) {
						builder.add(new ItemStack(rawGigot), new ItemStack(rawGigot));
					}
					return builder.build();
				}), 5, props)
		);
		public static final BlockEntry<DeadAnimalBlock> DEAD_WOLF = new BlockEntry<>(
				"dead_wolf",
				() -> BlockBehaviour.Properties.copy(NETHER_WART_BLOCK).noOcclusion(),
				props -> new DeadAnimalBlock(Util.make(() -> {
					ImmutableList.Builder<ItemStack> builder = ImmutableList.builder();
					Item rawWolf = ForgeRegistries.ITEMS.getValue(new ResourceLocation("kubejs", "sheng_wolfmeat"));
					if(rawWolf != null) {
						builder.add(new ItemStack(rawWolf), new ItemStack(rawWolf, 2));
					}
					builder.add(new ItemStack(Items.BONE), new ItemStack(Items.BONE));
					return builder.build();
				}), 4, props)
		);

		private static void init() {
		}
	}

	public static final class Decorations {
		public static final BlockEntry<TrashBagBlock> WHITE_TRASH_BAG = new BlockEntry<>(
				"white_trash_bag",
				() -> BlockBehaviour.Properties.of(Material.DECORATION, MaterialColor.TERRACOTTA_WHITE).strength(0.1F).sound(SoundType.CROP).noOcclusion(),
				TrashBagBlock::new
		);
		public static final BlockEntry<TinyTrashBagBlock> TINY_TRASH_BAG = new BlockEntry<>(
				"tiny_trash_bag",
				() -> BlockBehaviour.Properties.of(Material.DECORATION, MaterialColor.TERRACOTTA_GRAY).strength(0.1F).sound(SoundType.CROP).noOcclusion(),
				TinyTrashBagBlock::new
		);
		public static final BlockEntry<TrashDumpBlock> TRASH_DUMP = new BlockEntry<>(
				"trash_dump",
				() -> BlockBehaviour.Properties.of(Material.DECORATION, MaterialColor.TERRACOTTA_GRAY).strength(0.1F).sound(SoundType.CROP).noOcclusion(),
				TrashDumpBlock::new
		);
		public static final BlockEntry<JerricanBlock> JERRICAN = new BlockEntry<>(
				"jerrican",
				() -> BlockBehaviour.Properties.of(Material.METAL, MaterialColor.TERRACOTTA_RED).strength(5.0F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.METAL).noOcclusion(),
				JerricanBlock::new
		);
		public static final BlockEntry<ExplosiveJerricanBlock> EXPLOSIVE_JERRICAN = new BlockEntry<>(
				"explosive_jerrican",
				() -> BlockBehaviour.Properties.of(Material.METAL, MaterialColor.TERRACOTTA_RED).strength(5.0F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.METAL).noOcclusion(),
				ExplosiveJerricanBlock::new
		);
		public static final BlockEntry<SickbedBlock> SICKBED = new BlockEntry<>(
				"sickbed",
				() -> BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_BLUE).strength(5.0F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.METAL).noOcclusion(),
				SickbedBlock::new
		);
		public static final BlockEntry<SickbedBlock> SICKBED_WITH_BLOOD = new BlockEntry<>(
				"sickbed_with_blood",
				() -> BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_BLUE).strength(5.0F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.METAL).noOcclusion(),
				SickbedBlock::new
		);
		public static final BlockEntry<SickbedBlock> SICKBED_WITH_BODY = new BlockEntry<>(
				"sickbed_with_body",
				() -> BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_BLUE).strength(5.0F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.METAL).noOcclusion(),
				SickbedBlock::new
		);
		public static final BlockEntry<BodyBlock> BODY_DISEMBOWELLED = new BlockEntry<>(
				"body_disembowelled",
				() -> BlockBehaviour.Properties.copy(NETHER_WART_BLOCK).noOcclusion(),
				props -> new BodyBlock(6, 12, props)
		);
		public static final BlockEntry<BodyBlock> BODY_HALF = new BlockEntry<>(
				"body_half",
				() -> BlockBehaviour.Properties.copy(NETHER_WART_BLOCK).noOcclusion(),
				props -> new BodyBlock(6, 7, props)
		);
		public static final BlockEntry<BodyBlock> BODY_HEADLESS = new BlockEntry<>(
				"body_headless",
				() -> BlockBehaviour.Properties.copy(NETHER_WART_BLOCK).noOcclusion(),
				props -> new BodyBlock(6, 10, props)
		);
		public static final BlockEntry<NailedBodyBlock> BODY_NAILED = new BlockEntry<>(
				"body_nailed",
				() -> BlockBehaviour.Properties.copy(NETHER_WART_BLOCK).noOcclusion(),
				NailedBodyBlock::new
		);
		public static final BlockEntry<BodyBlock> BODY_ORGA = new BlockEntry<>(
				"body_orga",
				() -> BlockBehaviour.Properties.copy(NETHER_WART_BLOCK).noOcclusion(),
				props -> new BodyBlock(6, 12, props)
		);
		public static final BlockEntry<BodyBlock> BODY_SIT = new BlockEntry<>(
				"body_sit",
				() -> BlockBehaviour.Properties.copy(NETHER_WART_BLOCK).noOcclusion(),
				props -> new BodyBlock(6, 8, props)
		);
		public static final BlockEntry<BodyBlock> BODY_SKELETON = new BlockEntry<>(
				"body_skeleton",
				() -> BlockBehaviour.Properties.copy(NETHER_WART_BLOCK).sound(SoundType.BONE_BLOCK).noOcclusion(),
				props -> new BodyBlock(6, 14, props)
		);
		public static final BlockEntry<BodyBlock> BODY_BAG = new BlockEntry<>(
				"body_bag",
				() -> BlockBehaviour.Properties.copy(NETHER_WART_BLOCK).sound(SoundType.CROP).noOcclusion(),
				props -> new BodyBlock(6, 8, props)
		);
		public static final BlockEntry<IVStandBlock> IV_STAND = new BlockEntry<>(
				"iv_stand",
				() -> BlockBehaviour.Properties.copy(IRON_BARS),
				IVStandBlock::new
		);
		public static final BlockEntry<IVStandBlock> IV_STAND_EMPTY = new BlockEntry<>(
				"iv_stand_empty",
				() -> BlockBehaviour.Properties.copy(IRON_BARS),
				IVStandBlock::new
		);
		public static final BlockEntry<TrashCanBlock> TRASH_CAN = new BlockEntry<>(
				"trash_can",
				() -> BlockBehaviour.Properties.copy(IRON_BARS),
				TrashCanBlock::new
		);
		public static final BlockEntry<WheelchairBlock> WHEELCHAIR = new BlockEntry<>(
				"wheelchair",
				() -> BlockBehaviour.Properties.copy(IRON_BARS),
				WheelchairBlock::new
		);
		public static final BlockEntry<PackedSandbagBlock> PACKED_SANDBAG = new BlockEntry<>(
				"packed_sandbag",
				() -> BlockBehaviour.Properties.of(Material.SAND).strength(5.0F, 6.0F).noOcclusion(),
				PackedSandbagBlock::new
		);
		public static final BlockEntry<SandbagBlock> SANDBAG = new BlockEntry<>(
				"sandbag",
				() -> BlockBehaviour.Properties.of(Material.SAND).strength(5.0F, 6.0F).noOcclusion(),
				SandbagBlock::new
		);
		public static final BlockEntry<WheeledStretcherBlock> WHEELED_STRETCHER = new BlockEntry<>(
				"wheeled_stretcher",
				() -> BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_BLUE).strength(5.0F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.METAL).noOcclusion(),
				WheeledStretcherBlock::new
		);
		public static final BlockEntry<WheeledStretcherBlock> WHEELED_STRETCHER_WITH_BODY = new BlockEntry<>(
				"wheeled_stretcher_with_body",
				() -> BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_BLUE).strength(5.0F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.METAL).noOcclusion(),
				WheeledStretcherBlock::new
		);
		public static final BlockEntry<WastepaperBlock> WASTE_PAPER = new BlockEntry<>(
				"wastepaper",
				() -> BlockBehaviour.Properties.of(Material.DECORATION, MaterialColor.TERRACOTTA_WHITE).strength(0.1F).sound(SoundType.CROP).noCollission(),
				WastepaperBlock::new
		);

		private static void init() {
		}
	}

	public static void init(IEventBus bus) {
		REGISTER.register(bus);

		DeadAnimals.init();
		Decorations.init();
	}

	public static final class BlockEntry<T extends Block> implements Supplier<T>, ItemLike {
		private final RegistryObject<T> regObject;
		private final Supplier<BlockBehaviour.Properties> properties;

		public BlockEntry(String name, Supplier<BlockBehaviour.Properties> properties, Function<BlockBehaviour.Properties, T> make) {
			this(name, properties, make, SurviveInTheWinterFrontier.ITEM_GROUP);
		}

		public BlockEntry(String name, Supplier<BlockBehaviour.Properties> properties, Function<BlockBehaviour.Properties, T> make, @Nullable CreativeModeTab tab) {
			this.properties = properties;
			this.regObject = REGISTER.register(name, () -> make.apply(properties.get()));
			if(tab != null) {
				MISCTWFItems.ItemEntry.register(name, () -> new BlockItem(this.regObject.get(), new Item.Properties().tab(tab)));
			}
		}

		public T get() {
			return this.regObject.get();
		}

		public BlockState defaultBlockState() {
			return this.get().defaultBlockState();
		}

		public ResourceLocation getId() {
			return this.regObject.getId();
		}

		public BlockBehaviour.Properties getProperties() {
			return this.properties.get();
		}

		public Item asItem() {
			return this.get().asItem();
		}
	}
}
