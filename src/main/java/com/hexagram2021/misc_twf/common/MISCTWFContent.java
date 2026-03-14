package com.hexagram2021.misc_twf.common;

import com.hexagram2021.misc_twf.common.item.IEnergyItem;
import com.hexagram2021.misc_twf.common.network.ClientboundMonsterEggAnimationPacket;
import com.hexagram2021.misc_twf.common.network.ServerboundOpenTacBackpackPacket;
import com.hexagram2021.misc_twf.common.register.*;
import com.mrh0.createaddition.energy.InternalEnergyStorage;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ItemLike;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;

/**
 * 模组内容注册和初始化管理类喵~
 * 负责统一管理模组的所有注册器初始化，包括方块、物品、实体、配方等核心内容喵~
 *
 * @author liudongyu
 */
@EventBusSubscriber(modid = MODID)
public final class MISCTWFContent {
	/**
	 * 模组构造阶段的主入口方法喵~
	 * 按照依赖顺序初始化所有注册器，包括数据附件、属性、流体、方块、物品、实体等喵~
	 *
	 * @param bus 模组事件总线喵~
	 */
	public static void modConstruct(IEventBus bus) {
		initTags();

		MISCTWFArmorMaterials.init(bus);
		MISCTWFAttachmentTypes.init(bus);
		MISCTWFAttributes.init(bus);
		MISCTWFBlockStateProperties.init();
		MISCTWFCreativeModeTabs.init(bus);
		MISCTWFDataComponentTypes.init(bus);
		MISCTWFFluids.init(bus);
		MISCTWFBlocks.init(bus);
		MISCTWFBlockEntities.init(bus);
		MISCTWFItems.init(bus);
		MISCTWFEntities.init(bus);
		MISCTWFRecipeTypes.init(bus);
		MISCTWFRecipeSerializers.init(bus);
		MISCTWFStructurePieceTypes.init();
		MISCTWFStructureTypes.init(bus);
		MISCTWFMobEffects.init(bus);
		MISCTWFMenuTypes.init(bus);
		MISCTWFTravelersBackpackTacOps.init(bus);

		MISCTWFSkills.init(bus);
	}

	/**
	 * 初始化所有标签（Tag）定义喵~
	 */
	private static void initTags() {
		MISCTWFItemTags.init();
	}

	/**
	 * 监听通用注册事件，用于注册声音事件喵~
	 *
	 * @param event 注册事件喵~
	 */
	@SubscribeEvent
	public static void onRegister(RegisterEvent event) {
		event.register(Registries.SOUND_EVENT, MISCTWFSounds::init);
	}

	/**
	 * 注册酿造配方喵~
	 *
	 * @param event 酿造配方注册事件喵~
	 */
	@SubscribeEvent
	public static void registerPotions(RegisterBrewingRecipesEvent event) {
		MISCTWFBrewingRecipes.init(event.getBuilder());
	}

	/**
	 * 注册能力喵~
	 *
	 * @param event 注册能力事件喵~
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

	/**
	 * 修改原版实体的默认属性喵~
	 * 为玩家添加枪械精通度属性喵~
	 *
	 * @param event 实体属性修改事件喵~
	 */
	@SubscribeEvent
	public static void onModifyDefaultAttributes(EntityAttributeModificationEvent event) {
		event.add(EntityType.PLAYER, MISCTWFAttributes.GUN_MASTERY, 0.0D);
	}

	/**
	 * 注册网络包处理器喵~
	 * @param event 网络包处理器注册事件喵~
	 */
	@SubscribeEvent
	public static void networkRegistry(RegisterPayloadHandlersEvent event) {
		event.registrar("1")
				.commonToClient(
						ClientboundMonsterEggAnimationPacket.TYPE,
						ClientboundMonsterEggAnimationPacket.STREAM_CODEC,
						ClientboundMonsterEggAnimationPacket::handle
				)
				.commonToServer(
						ServerboundOpenTacBackpackPacket.TYPE,
						ServerboundOpenTacBackpackPacket.STREAM_CODEC,
						ServerboundOpenTacBackpackPacket::handle
				);
	}

	private MISCTWFContent() {
	}
}
