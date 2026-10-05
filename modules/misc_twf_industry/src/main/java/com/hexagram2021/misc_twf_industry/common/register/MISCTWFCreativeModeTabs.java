package com.hexagram2021.misc_twf_industry.common.register;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.hexagram2021.misc_twf_industry.MiscTwfIndustry.CONTENT_NAMESPACE;

/**
 * 工业生态模块创造模式标签页注册类喵~
 *
 * @author liudongyu
 */
public final class MISCTWFCreativeModeTabs {
	private static final DeferredRegister<CreativeModeTab> REGISTER = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CONTENT_NAMESPACE);

	/** 模块主创造页喵~ */
	public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN = REGISTER.register("industry", () -> CreativeModeTab.builder()
			.title(Component.translatable("itemGroup.misc_twf.industry"))
			.icon(() -> new ItemStack(MISCTWFItems.Materials.ENERGY_CORE.get()))
			.displayItems((parameters, output) -> MISCTWFItems.ItemEntry.getItems().forEach(output::accept))
			.build()
	);

	/**
	 * 初始化并注册创造页到事件总线喵~
	 *
	 * @param bus NeoForge 事件总线喵~
	 */
	public static void init(IEventBus bus) {
		REGISTER.register(bus);
	}

	private MISCTWFCreativeModeTabs() {
	}
}
