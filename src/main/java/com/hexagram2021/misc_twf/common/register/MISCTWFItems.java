package com.hexagram2021.misc_twf.common.register;

import com.hexagram2021.misc_twf.SurviveInTheWinterFrontier;
import com.hexagram2021.misc_twf.common.config.MISCTWFCommonConfig;
import com.hexagram2021.misc_twf.common.item.AbyssVirusVaccine;
import com.hexagram2021.misc_twf.common.item.AccumulatorItem;
import com.hexagram2021.misc_twf.common.item.NightVisionDeviceItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;

public final class MISCTWFItems {
	private static final DeferredRegister<Item> REGISTER = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

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

	public static void init(IEventBus bus) {
		REGISTER.register(bus);
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
