package com.hexagram2021.misc_twf_wildlife;

import com.hexagram2021.misc_twf_wildlife.common.MISCTWFWildlifeContent;
import com.hexagram2021.misc_twf_wildlife.common.config.MISCTWFWildlifeConfig;
import com.hexagram2021.misc_twf_wildlife.common.register.MISCTWFItems;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.DispenserBlock;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

/**
 * 农牧生态模块主类喵~
 *
 * <p>模块 modid 为 {@code misc_twf_wildlife}，对外作为独立 Mod 安装；
 * 内容命名空间沿用 {@code misc_twf}（决策 D6），注册 id 与资源路径不做迁移喵~</p>
 *
 * @author liudongyu
 */
@Mod(MiscTwfWildlife.MODID)
public class MiscTwfWildlife {
	/** 模块 mod id（对外身份）喵~ */
	public static final String MODID = "misc_twf_wildlife";
	/** 内容命名空间（注册 id 与资源统一使用的命名空间）喵~ */
	public static final String CONTENT_NAMESPACE = "misc_twf";

	/**
	 * 模块构造方法喵~
	 *
	 * @param modBus       模块事件总线喵~
	 * @param modContainer 模块容器喵~
	 */
	public MiscTwfWildlife(IEventBus modBus, ModContainer modContainer) {
		MISCTWFWildlifeContent.modConstruct(modBus);

		modContainer.registerConfig(ModConfig.Type.COMMON, MISCTWFWildlifeConfig.getConfig());

		modBus.addListener(this::setup);
	}

	/**
	 * 通用配置阶段回调，注册随域原版兼容行为喵~
	 *
	 * @param event 通用配置事件喵~
	 */
	private void setup(final FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
			// 动物粪便使用骨粉的发射器行为喵~
			DispenserBlock.registerBehavior(MISCTWFItems.Materials.ANIMAL_POOP.get(), DispenserBlock.DISPENSER_REGISTRY.getOrDefault(Items.BONE_MEAL, DispenseItemBehavior.NOOP));
		});
	}
}
