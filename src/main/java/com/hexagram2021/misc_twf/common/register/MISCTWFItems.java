package com.hexagram2021.misc_twf.common.register;

import com.google.common.collect.Maps;
import com.hexagram2021.misc_twf.SurviveInTheWinterFrontier;
import com.hexagram2021.misc_twf.common.config.MISCTWFCommonConfig;
import com.hexagram2021.misc_twf.common.item.AbyssVirusVaccine;
import com.hexagram2021.misc_twf.common.item.AccumulatorItem;
import com.hexagram2021.misc_twf.common.item.NightVisionDeviceItem;
import com.hexagram2021.misc_twf.common.item.WayfarerArmorItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;

@SuppressWarnings("unused")
public final class MISCTWFItems {
	private static final DeferredRegister<Item> REGISTER = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

	public static final class Materials {
		public static final ItemEntry<Item> YARN = ItemEntry.register(
				"yarn", () -> new Item(new Item.Properties().tab(SurviveInTheWinterFrontier.ITEM_GROUP))
		);
		public static final ItemEntry<Item> GAUZE = ItemEntry.register(
				"gauze", () -> new Item(new Item.Properties().tab(SurviveInTheWinterFrontier.ITEM_GROUP))
		);
		public static final ItemEntry<Item> ENERGY_CORE = ItemEntry.register(
				"energy_core", () -> new Item(new Item.Properties().tab(SurviveInTheWinterFrontier.ITEM_GROUP))
		);
		public static final ItemEntry<Item> WHITE_CRYSTAL_CORE = ItemEntry.register(
				"white_crystal_core", () -> new Item(new Item.Properties().tab(SurviveInTheWinterFrontier.ITEM_GROUP))
		);
		public static final ItemEntry<Item> SCULK_SHARD = ItemEntry.register(
				"sculk_shard", () -> new Item(new Item.Properties().tab(SurviveInTheWinterFrontier.ITEM_GROUP))
		);
		public static final ItemEntry<Item> VOID_CRYSTAL = ItemEntry.register(
				"void_crystal", () -> new Item(new Item.Properties().tab(SurviveInTheWinterFrontier.ITEM_GROUP))
		);
		public static final ItemEntry<Item> GLOWING_NETHERITE_INGOT = ItemEntry.register(
				"glowing_netherite_ingot", () -> new Item(new Item.Properties().tab(SurviveInTheWinterFrontier.ITEM_GROUP))
		);
		public static final ItemEntry<Item> WAYFARER_INGOT = ItemEntry.register(
				"wayfarer_ingot", () -> new Item(new Item.Properties().tab(SurviveInTheWinterFrontier.ITEM_GROUP))
		);
		public static final ItemEntry<Item> ALUMINUM_PLATE = ItemEntry.register(
				"aluminum_plate", () -> new Item(new Item.Properties().tab(SurviveInTheWinterFrontier.ITEM_GROUP))
		);
		public static final ItemEntry<Item> RUBBER_PLATE = ItemEntry.register(
				"rubber_plate", () -> new Item(new Item.Properties().tab(SurviveInTheWinterFrontier.ITEM_GROUP))
		);
		public static final ItemEntry<Item> SECOND_BRAIN_CORE = ItemEntry.register(
				"second_brain_core", () -> new Item(new Item.Properties().tab(SurviveInTheWinterFrontier.ITEM_GROUP))
		);
		public static final ItemEntry<Item> SYRINGE = ItemEntry.register(
				"syringe", () -> new Item(new Item.Properties().stacksTo(1).tab(SurviveInTheWinterFrontier.ITEM_GROUP))
		);
		public static final ItemEntry<Item> GLASS_ROD = ItemEntry.register(
				"glass_rod", () -> new Item(new Item.Properties().stacksTo(1).tab(SurviveInTheWinterFrontier.ITEM_GROUP))
		);
		public static final ItemEntry<Item> MYSTERIOUS_FLESH = ItemEntry.register(
				"mysterious_flesh", () -> new Item(new Item.Properties().tab(SurviveInTheWinterFrontier.ITEM_GROUP))
		);
		public static final ItemEntry<Item> MUTANT_POTION = ItemEntry.register(
				"mutant_potion", () -> new Item(new Item.Properties().stacksTo(1).tab(SurviveInTheWinterFrontier.ITEM_GROUP))
		);
		public static final ItemEntry<Item> LAMP_SUPPORT = ItemEntry.register(
				"lamp_support", () -> new Item(new Item.Properties().tab(SurviveInTheWinterFrontier.ITEM_GROUP))
		);
		public static final ItemEntry<Item> LAMP_PEDESTAL = ItemEntry.register(
				"lamp_pedestal", () -> new Item(new Item.Properties().tab(SurviveInTheWinterFrontier.ITEM_GROUP))
		);
		public static final ItemEntry<Item> LAMP_TUBE = ItemEntry.register(
				"lamp_tube", () -> new Item(new Item.Properties().tab(SurviveInTheWinterFrontier.ITEM_GROUP))
		);
		public static final ItemEntry<Item> UV_LED = ItemEntry.register(
				"uv_led", () -> new Item(new Item.Properties().tab(SurviveInTheWinterFrontier.ITEM_GROUP))
		);
		public static final ItemEntry<Item> TRAVELERS_BACKPACK_TAC_SLOT = ItemEntry.register(
				"travelers_backpack_tac_slot", () -> new Item(new Item.Properties().stacksTo(1).tab(SurviveInTheWinterFrontier.ITEM_GROUP))
		);
		public static final ItemEntry<Item> MUTANT_POTION_BUCKET = ItemEntry.register(
				"mutant_potion_bucket", () -> new Item(new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1).tab(SurviveInTheWinterFrontier.ITEM_GROUP))
		);
		public static final ItemEntry<Item> ABYSS_VIRUS_VACCINE_BUCKET = ItemEntry.register(
				"abyss_virus_vaccine_bucket", () -> new Item(new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1).tab(SurviveInTheWinterFrontier.ITEM_GROUP))
		);
		public static final ItemEntry<Item> ANIMAL_POOP = ItemEntry.register(
				"animal_poop", () -> new BoneMealItem(new Item.Properties().tab(SurviveInTheWinterFrontier.ITEM_GROUP))
		);

		private Materials() {
		}

		public static void init() {
		}
	}

	public static final ItemEntry<AbyssVirusVaccine> ABYSS_VIRUS_VACCINE = ItemEntry.register(
			"abyss_virus_vaccine", () -> new AbyssVirusVaccine(new Item.Properties().stacksTo(1).tab(SurviveInTheWinterFrontier.ITEM_GROUP))
	);

	public static final ItemEntry<NightVisionDeviceItem> NIGHT_VISION_DEVICE = ItemEntry.register(
			"night_vision_device", () -> new NightVisionDeviceItem(new Item.Properties().stacksTo(1).tab(SurviveInTheWinterFrontier.ITEM_GROUP))
	);

	public static final ItemEntry<AccumulatorItem> ORDINARY_ACCUMULATOR = ItemEntry.register(
			"ordinary_accumulator", () -> new AccumulatorItem(new Item.Properties().stacksTo(1).tab(SurviveInTheWinterFrontier.ITEM_GROUP)) {
				@Override
				public int getEnergyCapability() {
					return MISCTWFCommonConfig.ORDINARY_ACCUMULATOR_CAPABILITY.get();
				}
			}
	);

	public static final ItemEntry<AccumulatorItem> MILITARY_ACCUMULATOR = ItemEntry.register(
			"military_accumulator", () -> new AccumulatorItem(new Item.Properties().stacksTo(1).tab(SurviveInTheWinterFrontier.ITEM_GROUP)) {
				@Override
				public int getEnergyCapability() {
					return MISCTWFCommonConfig.MILITARY_ACCUMULATOR_CAPABILITY.get();
				}
			}
	);

	private MISCTWFItems() {
	}

	public static final Map<EquipmentSlot, ItemEntry<WayfarerArmorItem>> WAYFARER_ARMORS = Maps.newEnumMap(EquipmentSlot.class);

	public static void init(IEventBus bus) {
		Materials.init();

		REGISTER.register(bus);

		for(EquipmentSlot type : EquipmentSlot.values()) {
			if(type.getType() == EquipmentSlot.Type.ARMOR) {
				WAYFARER_ARMORS.put(type, ItemEntry.register(WayfarerArmorItem.name + "_" + type.name().toLowerCase(Locale.ENGLISH), () -> new WayfarerArmorItem(type)));
			}
		}
	}

	public static class ItemEntry<T extends Item> implements Supplier<T>, ItemLike {
		private final RegistryObject<T> regObject;

		private ItemEntry(RegistryObject<T> regObject) {
			this.regObject = regObject;
		}

		public static <T extends Item> ItemEntry<T> register(String name, Supplier<? extends T> make) {
			return new ItemEntry<>(REGISTER.register(name, make));
		}

		@Override
		public T get() {
			return this.regObject.get();
		}

		@Override
		public Item asItem() {
			return this.regObject.get();
		}

		public ResourceLocation getId() {
			return this.regObject.getId();
		}
	}
}
