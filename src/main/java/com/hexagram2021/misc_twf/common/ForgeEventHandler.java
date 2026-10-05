package com.hexagram2021.misc_twf.common;

import be.florens.expandability.api.EventResult;
import be.florens.expandability.api.forge.PlayerSwimEvent;
import com.hexagram2021.misc_twf.common.item.AbyssVirusVaccine;
import com.hexagram2021.misc_twf.common.register.MISCTWFFluids;
import com.hexagram2021.misc_twf.common.register.MISCTWFItems;
import com.hexagram2021.misc_twf_zombie_animals.server.MISCTWFImmunitySavedData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.furnace.FurnaceFuelBurnTimeEvent;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;

/**
 * Forge 事件处理器喵~
 * 处理游戏运行时的各种事件，包括能力附加、实体更新、伤害计算、转化事件、交互事件和生成检查等喵~
 *
 * @author liudongyu
 */
@EventBusSubscriber(modid = MODID)
public final class ForgeEventHandler {
	/** 能量能力的标识符喵~ */
	public static final ResourceLocation ENERGY = ResourceLocation.fromNamespaceAndPath(MODID, "energy");

	/**
	 * 处理玩家与实体交互的事件喵~
	 * 实现深渊病毒疫苗的使用逻辑，为生物提供僵尸化免疫效果喵~
	 *
	 * @param event 玩家实体交互事件喵~
	 */
	@SubscribeEvent
	public static void onInteractWithEntity(PlayerInteractEvent.EntityInteract event) {
		if(event.getTarget() instanceof LivingEntity entity) {
			Player player = event.getEntity();
			ItemStack itemstack = player.getItemInHand(event.getHand());
			if(itemstack.is(MISCTWFItems.ABYSS_VIRUS_VACCINE.asItem())) {
				if(entity.level().isClientSide) {
					event.setCancellationResult(InteractionResult.SUCCESS);
					event.setCanceled(true);
					return;
				}
				if(MISCTWFImmunitySavedData.isImmuneToZombification(entity.getUUID())) {
					event.setCancellationResult(InteractionResult.FAIL);
					event.setCanceled(true);
					return;
				}
				itemstack.shrink(1);
				if(entity instanceof Mob mob) {
					mob.setPersistenceRequired();
				}
				MISCTWFImmunitySavedData.setImmuneToZombification(entity.getUUID(), entity.tickCount);
				AbyssVirusVaccine.afterUse(player, entity);
				if(itemstack.isEmpty()) {
					player.setItemInHand(event.getHand(), new ItemStack(MISCTWFItems.Materials.SYRINGE));
				} else {
					player.drop(new ItemStack(MISCTWFItems.Materials.SYRINGE), true);
				}
				event.setCancellationResult(InteractionResult.CONSUME);
				event.setCanceled(true);
			}
		}
	}

	/**
	 * 处理玩家游泳判定事件喵~
	 * 允许玩家在血液流体中游泳喵~
	 *
	 * @param event 玩家游泳事件喵~
	 */
	@SubscribeEvent
	public static void onPlayerSwim(PlayerSwimEvent event) {
		Player player = event.getEntity();
		double fluidHeight = player.getFluidTypeHeight(MISCTWFFluids.BLOOD_FLUID.type().get());
		if(fluidHeight > 0 && (!player.onGround() || fluidHeight > player.getFluidJumpThreshold())) {
			event.setResult(EventResult.SUCCESS);
		}
	}

	/**
	 * 处理获取燃料燃烧时间获取事件喵~
	 *
	 * @param event 获取燃料燃烧时间获取事件喵~
	 */
	@SubscribeEvent
	public static void onGetBurnTime(FurnaceFuelBurnTimeEvent event) {
		// empty
	}

	private ForgeEventHandler() {
	}
}
