package com.hexagram2021.misc_twf_industry.common.register;

import com.hexagram2021.misc_twf_industry.common.menu.MoldWorkbenchMenu;
import com.hexagram2021.misc_twf_industry.common.menu.RecoveryFurnaceMenu;
import com.hexagram2021.misc_twf_industry.common.menu.UltravioletLampMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.hexagram2021.misc_twf_industry.MiscTwfIndustry.CONTENT_NAMESPACE;

/**
 * 工业生态模块菜单类型注册类喵~
 *
 * @author liudongyu
 */
public final class MISCTWFMenuTypes {
	private static final DeferredRegister<MenuType<?>> REGISTER = DeferredRegister.create(Registries.MENU, CONTENT_NAMESPACE);

	/** 强紫外线照射灯菜单类型喵~ */
	public static final DeferredHolder<MenuType<?>, MenuType<UltravioletLampMenu>> ULTRAVIOLET_LAMP_MENU = REGISTER.register(
			"ultraviolet_lamp", () -> new MenuType<>(UltravioletLampMenu::new, FeatureFlags.VANILLA_SET)
	);

	/** 模具加工台菜单类型喵~ */
	public static final DeferredHolder<MenuType<?>, MenuType<MoldWorkbenchMenu>> MOLD_WORKBENCH_MENU = REGISTER.register(
			"mold_workbench", () -> new MenuType<>(MoldWorkbenchMenu::new, FeatureFlags.VANILLA_SET)
	);

	/** 回收炉菜单类型喵~ */
	public static final DeferredHolder<MenuType<?>, MenuType<RecoveryFurnaceMenu>> RECOVERY_FURNACE_MENU = REGISTER.register(
			"recovery_furnace", () -> new MenuType<>(RecoveryFurnaceMenu::new, FeatureFlags.VANILLA_SET)
	);

	/**
	 * 初始化并注册所有菜单类型到事件总线喵~
	 *
	 * @param bus NeoForge 事件总线喵~
	 */
	public static void init(IEventBus bus) {
		REGISTER.register(bus);
	}

	private MISCTWFMenuTypes() {
	}
}
