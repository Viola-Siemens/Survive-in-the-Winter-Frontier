package com.hexagram2021.misc_twf.common;

import com.hexagram2021.misc_twf.common.network.ClientboundMonsterEggAnimationPacket;
import com.hexagram2021.misc_twf.common.network.ServerboundOpenTacBackpackPacket;
import com.hexagram2021.misc_twf.common.register.MISCTWFAttributes;
import com.hexagram2021.misc_twf.common.register.MISCTWFBrewingRecipes;
import net.minecraft.world.entity.EntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

/**
 * 模组内容注册和初始化管理类喵~
 * 负责统一管理模组的所有注册器初始化，包括方块、物品、实体、配方等核心内容喵~
 *
 * @author liudongyu
 */
public final class MISCTWFContent {

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
