package com.hexagram2021.misc_twf.common.register;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;

/**
 * 模组创造模式物品栏注册类，负责注册自定义创造模式标签页喵~
 *
 * @author liudongyu
 */
public final class MISCTWFCreativeModeTabs {
	private static final DeferredRegister<CreativeModeTab> REGISTER = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

	/** 模组主创造模式标签页，包含所有模组物品喵~ */
	public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN = REGISTER.register("main", () -> CreativeModeTab.builder()
			.title(Component.translatable("itemGroup.misc_twf"))
			.icon(() -> new ItemStack(MISCTWFItems.ABYSS_VIRUS_VACCINE))
			.displayItems((parameters, output) -> MISCTWFItems.ItemEntry.getItems().forEach(output::accept))
			.build()
	);

	private MISCTWFCreativeModeTabs() {
	}

	/**
	 * 初始化创造模式标签页注册，将延迟注册器绑定到模组事件总线喵~
	 *
	 * @param bus 模组事件总线喵~
	 */
	public static void init(IEventBus bus) {
		REGISTER.register(bus);
	}
}
