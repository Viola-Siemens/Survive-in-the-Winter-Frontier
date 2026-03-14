package com.hexagram2021.misc_twf.common.register;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
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
