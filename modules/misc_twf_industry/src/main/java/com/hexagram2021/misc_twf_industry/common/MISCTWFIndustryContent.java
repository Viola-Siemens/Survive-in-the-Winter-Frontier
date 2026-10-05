package com.hexagram2021.misc_twf_industry.common;

import com.hexagram2021.misc_twf_industry.common.item.IEnergyItem;
import com.hexagram2021.misc_twf_industry.common.register.*;
import com.mrh0.createaddition.energy.InternalEnergyStorage;
import net.minecraft.world.level.ItemLike;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;

import static com.hexagram2021.misc_twf_industry.MiscTwfIndustry.MODID;

/**
 * 工业生态模块内容注册与初始化管理类喵~
 *
 * <p>负责模块内注册器初始化和方块实体/物品能力（物品栏、能量）注册喵~</p>
 *
 * @author liudongyu
 */
@EventBusSubscriber(modid = MODID)
public final class MISCTWFIndustryContent {
	/**
	 * 模块构造阶段的主入口方法喵~
	 *
	 * @param bus 模块事件总线喵~
	 */
	public static void modConstruct(IEventBus bus) {
		MISCTWFItemTags.init();
		MISCTWFBlockStateProperties.init();

		MISCTWFArmorMaterials.init(bus);
		MISCTWFCreativeModeTabs.init(bus);
		MISCTWFBlocks.init(bus);
		MISCTWFBlockEntities.init(bus);
		MISCTWFItems.init(bus);
		MISCTWFMobEffects.init(bus);
		MISCTWFMenuTypes.init(bus);
		MISCTWFRecipeTypes.init(bus);
		MISCTWFRecipeSerializers.init(bus);
	}

	/**
	 * 注册方块实体与物品的能力喵~
	 *
	 * @param event 能力注册事件喵~
	 */
	@SubscribeEvent
	public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
		event.registerBlockEntity(
				Capabilities.ItemHandler.BLOCK,
				MISCTWFBlockEntities.MOLD_DETACHER.get(),
				(container, side) -> side == null ? new InvWrapper(container) : new SidedInvWrapper(container, side)
		);
		event.registerBlockEntity(
				Capabilities.ItemHandler.BLOCK,
				MISCTWFBlockEntities.MOLD_WORKBENCH.get(),
				(container, side) -> side == null ? new InvWrapper(container) : new SidedInvWrapper(container, side)
		);
		event.registerBlockEntity(
				Capabilities.ItemHandler.BLOCK,
				MISCTWFBlockEntities.RECOVERY_FURNACE.get(),
				(container, side) -> side == null ? new InvWrapper(container) : new SidedInvWrapper(container, side)
		);
		event.registerBlockEntity(
				Capabilities.ItemHandler.BLOCK,
				MISCTWFBlockEntities.ULTRAVIOLET_LAMP.get(),
				(container, side) -> side == null ? new InvWrapper(container) : new SidedInvWrapper(container, side)
		);
		event.registerBlockEntity(
				Capabilities.EnergyStorage.BLOCK,
				MISCTWFBlockEntities.ULTRAVIOLET_LAMP.get(),
				(container, side) -> new InternalEnergyStorage(160, 160, 1)
		);
		event.registerItem(
				Capabilities.EnergyStorage.ITEM,
				(itemStack, context) -> itemStack.getItem() instanceof IEnergyItem energyItem ? new InternalEnergyStorage(
						energyItem.getEnergyCapability(),
						energyItem.getMaxEnergyReceiveSpeed(),
						energyItem.getMaxEnergyExtractSpeed()
				) : new InternalEnergyStorage(0, 0, 0),
				MISCTWFItems.MILITARY_ACCUMULATOR, MISCTWFItems.ORDINARY_ACCUMULATOR, MISCTWFItems.NIGHT_VISION_DEVICE
		);
		event.registerItem(
				Capabilities.EnergyStorage.ITEM,
				(itemStack, context) -> itemStack.getItem() instanceof IEnergyItem energyItem ? new InternalEnergyStorage(
						energyItem.getEnergyCapability(),
						energyItem.getMaxEnergyReceiveSpeed(),
						energyItem.getMaxEnergyExtractSpeed()
				) : new InternalEnergyStorage(0, 0, 0),
				MISCTWFItems.WAYFARER_ARMORS.values().toArray(ItemLike[]::new)
		);
	}

	private MISCTWFIndustryContent() {
	}
}
