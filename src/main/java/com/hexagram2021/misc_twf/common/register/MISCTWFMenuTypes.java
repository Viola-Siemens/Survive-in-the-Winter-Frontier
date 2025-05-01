package com.hexagram2021.misc_twf.common.register;

import com.hexagram2021.misc_twf.common.menu.*;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;

public final class MISCTWFMenuTypes {
	private static final DeferredRegister<MenuType<?>> REGISTER = DeferredRegister.create(ForgeRegistries.CONTAINERS, MODID);

	public static final RegistryObject<MenuType<UltravioletLampMenu>> ULTRAVIOLET_LAMP_MENU = REGISTER.register(
			"ultraviolet_lamp", () -> new MenuType<>(UltravioletLampMenu::new)
	);
	public static final RegistryObject<MenuType<TravelersBackpackBlockEntityTacMenu>> TRAVELERS_BACKPACK_BLOCK_ENTITY_TAC_SLOT_MENU = REGISTER.register(
			"travelers_backpack_block_entity_tac_slot", () -> IForgeMenuType.create(TravelersBackpackBlockEntityTacMenu::new)
	);
	public static final RegistryObject<MenuType<TravelersBackpackItemTacMenu>> TRAVELERS_BACKPACK_ITEM_TAC_SLOT_MENU = REGISTER.register(
			"travelers_backpack_item_tac_slot", () -> IForgeMenuType.create(TravelersBackpackItemTacMenu::new)
	);
	public static final RegistryObject<MenuType<MoldWorkbenchMenu>> MOLD_WORKBENCH_MENU = REGISTER.register(
			"mold_workbench", () -> new MenuType<>(MoldWorkbenchMenu::new)
	);
	public static final RegistryObject<MenuType<RecoveryFurnaceMenu>> RECOVERY_FURNACE = REGISTER.register(
			"recovery_furnace", () -> new MenuType<>(RecoveryFurnaceMenu::new)
	);

	private MISCTWFMenuTypes() {
	}

	public static void init(IEventBus bus) {
		REGISTER.register(bus);
	}
}
