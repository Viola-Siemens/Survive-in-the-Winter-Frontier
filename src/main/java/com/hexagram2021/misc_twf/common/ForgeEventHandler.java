package com.hexagram2021.misc_twf.common;

import com.hexagram2021.misc_twf.common.item.AbyssVirusVaccine;
import com.hexagram2021.misc_twf.common.register.MISCTWFItems;
import com.hexagram2021.misc_twf.server.MISCTWFSavedData;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.smileycorp.hordes.common.event.SpawnZombiePlayerEvent;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEventHandler {
	@SubscribeEvent
	public static void onSpawnZombiePlayer(SpawnZombiePlayerEvent event) {
		if(MISCTWFSavedData.isImmuneToZombification(event.getPlayer().getUUID())) {
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void onInteractWithEntity(PlayerInteractEvent.EntityInteract event) {
		if(event.getTarget() instanceof LivingEntity entity) {
			ItemStack itemstack = event.getPlayer().getItemInHand(event.getHand());
			if(itemstack.is(MISCTWFItems.ABYSS_VIRUS_VACCINE.asItem())) {
				if(entity.level.isClientSide) {
					event.setCancellationResult(InteractionResult.SUCCESS);
					event.setCanceled(true);
					return;
				}
				if(MISCTWFSavedData.isImmuneToZombification(entity.getUUID())) {
					event.setCancellationResult(InteractionResult.FAIL);
					event.setCanceled(true);
					return;
				}
				itemstack.shrink(1);
				MISCTWFSavedData.setImmuneToZombification(entity.getUUID(), entity.tickCount);
				AbyssVirusVaccine.afterUse(event.getPlayer(), entity);
				event.setCancellationResult(InteractionResult.CONSUME);
				event.setCanceled(true);
			}
		}
	}
}
