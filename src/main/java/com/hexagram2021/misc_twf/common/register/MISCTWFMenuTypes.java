package com.hexagram2021.misc_twf.common.register;

import com.hexagram2021.misc_twf.common.menu.TravelersBackpackBlockEntityTacMenu;
import com.hexagram2021.misc_twf.common.menu.TravelersBackpackItemTacMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;

/**
 * 菜单类型注册类，管理模组中所有自定义容器菜单的注册喵~
 * 菜单类型定义了玩家可以打开的各种GUI界面，用于与方块实体或物品进行交互喵~
 *
 * @author liudongyu
 */
public final class MISCTWFMenuTypes {
	private static final DeferredRegister<MenuType<?>> REGISTER = DeferredRegister.create(Registries.MENU, MODID);

	/**
	 * 旅行背包方块实体TAC槽位菜单类型，用于管理放置状态下旅行背包的弹药槽界面喵~
	 */
	public static final DeferredHolder<MenuType<?>, MenuType<TravelersBackpackBlockEntityTacMenu>> TRAVELERS_BACKPACK_BLOCK_ENTITY_TAC_SLOT_MENU = REGISTER.register(
			"travelers_backpack_block_entity_tac_slot", () -> IMenuTypeExtension.create(TravelersBackpackBlockEntityTacMenu::new)
	);

	/**
	 * 旅行背包物品TAC槽位菜单类型，用于管理装备状态下旅行背包的弹药槽界面喵~
	 */
	public static final DeferredHolder<MenuType<?>, MenuType<TravelersBackpackItemTacMenu>> TRAVELERS_BACKPACK_ITEM_TAC_SLOT_MENU = REGISTER.register(
			"travelers_backpack_item_tac_slot", () -> IMenuTypeExtension.create(TravelersBackpackItemTacMenu::new)
	);

	private MISCTWFMenuTypes() {
	}

	/**
	 * 初始化并注册所有菜单类型到事件总线喵~
	 *
	 * @param bus NeoForge 事件总线喵~
	 */
	public static void init(IEventBus bus) {
		REGISTER.register(bus);
	}
}
