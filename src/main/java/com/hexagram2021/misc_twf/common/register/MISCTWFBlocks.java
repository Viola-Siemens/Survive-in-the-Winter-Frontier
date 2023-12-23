package com.hexagram2021.misc_twf.common.register;

import com.hexagram2021.misc_twf.SurviveInTheWinterFrontier;
import com.hexagram2021.misc_twf.common.block.AbyssVirusVaccineCauldronBlock;
import com.hexagram2021.misc_twf.common.block.MutantPotionCauldronBlock;
import com.hexagram2021.misc_twf.common.block.UltravioletLampBlock;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
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
	public static final BlockEntry<Block> INFECTED_DIRT = new BlockEntry<>("infected_dirt", () -> BlockBehaviour.Properties.copy(DIRT), Block::new);
	public static final BlockEntry<Block> INFECTED_DIRT_D = new BlockEntry<>("infected_dirt_d", () -> BlockBehaviour.Properties.copy(DIRT), Block::new);
	public static final BlockEntry<Block> INFECTED_DIRT_DD = new BlockEntry<>("infected_dirt_dd", () -> BlockBehaviour.Properties.copy(DIRT), Block::new);
	public static final BlockEntry<Block> INFECTED_GRASS_BLOCK = new BlockEntry<>("infected_grass_block", () -> BlockBehaviour.Properties.copy(GRASS_BLOCK), Block::new);
	public static final BlockEntry<Block> INFECTED_GRASS_BLOCK_D = new BlockEntry<>("infected_grass_block_d", () -> BlockBehaviour.Properties.copy(GRASS_BLOCK), Block::new);
	public static final BlockEntry<Block> INFECTED_GRASS_BLOCK_DD = new BlockEntry<>("infected_grass_block_dd", () -> BlockBehaviour.Properties.copy(GRASS_BLOCK), Block::new);
	public static final BlockEntry<Block> INTESTINE = new BlockEntry<>("intestine", () -> BlockBehaviour.Properties.copy(REDSTONE_WIRE).sound(SoundType.NETHER_WART).noDrops(), Block::new);
	public static final BlockEntry<Block> RIBS = new BlockEntry<>("ribs", () -> BlockBehaviour.Properties.copy(REDSTONE_WIRE).sound(SoundType.BONE_BLOCK), Block::new);

	private MISCTWFBlocks() {
	}

	public static void init(IEventBus bus) {
		REGISTER.register(bus);
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
