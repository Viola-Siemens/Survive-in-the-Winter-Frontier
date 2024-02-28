package com.hexagram2021.misc_twf.common;

import be.florens.expandability.api.forge.PlayerSwimEvent;
import com.hexagram2021.misc_twf.common.effect.FragileEffect;
import com.hexagram2021.misc_twf.common.entity.ZombieGoatEntity;
import com.hexagram2021.misc_twf.common.entity.ZombieSheepEntity;
import com.hexagram2021.misc_twf.common.entity.capability.AnimalPoopingHandler;
import com.hexagram2021.misc_twf.common.entity.capability.CapabilityAnimal;
import com.hexagram2021.misc_twf.common.entity.capability.IPoopingAnimal;
import com.hexagram2021.misc_twf.common.item.AbyssVirusVaccine;
import com.hexagram2021.misc_twf.common.item.IEnergyItem;
import com.hexagram2021.misc_twf.common.item.capability.ItemStackEnergyHandler;
import com.hexagram2021.misc_twf.common.register.MISCTWFEntityTags;
import com.hexagram2021.misc_twf.common.register.MISCTWFFluidTags;
import com.hexagram2021.misc_twf.common.register.MISCTWFItems;
import com.hexagram2021.misc_twf.common.register.MISCTWFMobEffects;
import com.hexagram2021.misc_twf.server.MISCTWFSavedData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.animal.goat.GoatAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingConversionEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEventHandler {
	public static final ResourceLocation ENERGY = new ResourceLocation(MODID, "energy");
	public static final ResourceLocation POOPING = new ResourceLocation(MODID, "pooping");

	@SubscribeEvent
	public static void onAttachItemStackCapability(AttachCapabilitiesEvent<ItemStack> event) {
		if(event.getObject().getItem() instanceof IEnergyItem energyItem) {
			event.addCapability(ENERGY, new ItemStackEnergyHandler(
					event.getObject(),
					energyItem.getEnergyCapability(),
					energyItem.getMaxEnergyReceiveSpeed(),
					energyItem.getMaxEnergyExtractSpeed()
			));
		}
	}

	@SubscribeEvent
	public static void onAttachLivingEntityCapability(AttachCapabilitiesEvent<Entity> event) {
		if(event.getObject() instanceof LivingEntity livingEntity && livingEntity.getType().is(MISCTWFEntityTags.POOPING_ANIMALS)) {
			event.addCapability(POOPING, new AnimalPoopingHandler(livingEntity));
		}
	}

	@SubscribeEvent
	public static void onLivingHurt(LivingHurtEvent event) {
		LivingEntity livingEntity = event.getEntityLiving();
		MobEffectInstance effectInstance = livingEntity.getEffect(MISCTWFMobEffects.FRAGILE.get());
		if(effectInstance != null) {
			event.setAmount(event.getAmount() * FragileEffect.getDamageMultiplier(effectInstance.getAmplifier()));
		}

		if(event.getSource().getEntity() instanceof ZombieGoatEntity goat) {
			Vec3 direction = goat.position().subtract(livingEntity.position()).normalize();
			double multiplier = goat.isBaby() ? GoatAi.BABY_RAM_KNOCKBACK_FORCE : GoatAi.ADULT_RAM_KNOCKBACK_FORCE;
			livingEntity.knockback(direction.x * multiplier, direction.y * multiplier, direction.z * multiplier);
		}
	}

	@SubscribeEvent
	public static void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
		LivingEntity livingEntity = event.getEntityLiving();
		if(!livingEntity.level.isClientSide) {
			livingEntity.getCapability(CapabilityAnimal.POOPING).ifPresent(IPoopingAnimal::tick);
		}
	}

	@SubscribeEvent
	public static void onLivingConvert(LivingConversionEvent.Post event) {
		if(event.getEntityLiving() instanceof Sheep sheep && event.getOutcome() instanceof ZombieSheepEntity zombieSheep) {
			zombieSheep.setColor(sheep.getColor());
		}
	}

	@SubscribeEvent
	public static void onInteractWithEntity(PlayerInteractEvent.EntityInteract event) {
		if(event.getTarget() instanceof LivingEntity entity) {
			Player player = event.getPlayer();
			ItemStack itemstack = player.getItemInHand(event.getHand());
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
				if(entity instanceof Mob mob) {
					mob.setPersistenceRequired();
				}
				MISCTWFSavedData.setImmuneToZombification(entity.getUUID(), entity.tickCount);
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

	@SubscribeEvent
	public static void onPlayerSwim(PlayerSwimEvent event) {
		Player player = event.getPlayer();
		double fluidHeight = player.getFluidHeight(MISCTWFFluidTags.BLOOD);
		if(fluidHeight > 0 && (!player.isOnGround() || fluidHeight > player.getFluidJumpThreshold())) {
			event.setResult(Event.Result.ALLOW);
		}
	}

	@SubscribeEvent
	public static void onEntitySpawn(LivingSpawnEvent.CheckSpawn event) {
		if(event.getEntityLiving().getType().getCategory().equals(MobCategory.MONSTER) && MISCTWFSavedData.denyMonsterSpawn(event.getEntityLiving().getOnPos())) {
			event.setResult(Event.Result.DENY);
		}
	}
}
