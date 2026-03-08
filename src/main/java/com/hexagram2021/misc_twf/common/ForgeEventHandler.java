package com.hexagram2021.misc_twf.common;

import be.florens.expandability.api.EventResult;
import be.florens.expandability.api.forge.PlayerSwimEvent;
import com.hexagram2021.misc_twf.common.effect.FragileEffect;
import com.hexagram2021.misc_twf.common.entity.ZombieGoatEntity;
import com.hexagram2021.misc_twf.common.entity.ZombieSheepEntity;
import com.hexagram2021.misc_twf.common.entity.capability.PoopingAnimal;
import com.hexagram2021.misc_twf.common.item.AbyssVirusVaccine;
import com.hexagram2021.misc_twf.common.item.IEnergyItem;
import com.hexagram2021.misc_twf.common.item.capability.ItemStackEnergyHandler;
import com.hexagram2021.misc_twf.common.register.*;
import com.hexagram2021.misc_twf.server.MISCTWFSavedData;
import net.minecraft.core.GlobalPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.animal.goat.GoatAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingConversionEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.MobSpawnEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

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
	/** 排泄能力的标识符喵~ */
	public static final ResourceLocation POOPING = ResourceLocation.fromNamespaceAndPath(MODID, "pooping");

	/**
	 * 为物品栈附加能力喵~
	 * 如果物品实现了 IEnergyItem 接口，则为其附加能量存储能力喵~
	 *
	 * @param event 物品栈能力附加事件喵~
	 */
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

	/**
	 * 处理生物实体的每 tick 更新喵~
	 * 管理具有排泄能力的动物的排泄计时器和排泄行为喵~
	 *
	 * @param event 实体 tick 事件喵~
	 */
	@SubscribeEvent
	public static void onLivingTick(EntityTickEvent.Post event) {
		if(event.getEntity() instanceof LivingEntity livingEntity && !livingEntity.level().isClientSide && livingEntity.getType().is(MISCTWFEntityTags.POOPING_ANIMALS)) {
			PoopingAnimal poopingAnimal = livingEntity.getData(MISCTWFAttachmentTypes.ITEM_ENTITY_CONVERSION);
			int remainingTicks = poopingAnimal.getPoopingRemainingTicks();
			if(remainingTicks < 0) {
				poopingAnimal.resetPoopingTicks(livingEntity);
			} else if(remainingTicks > 0) {
				poopingAnimal.setPoopingRemainingTicks(remainingTicks - 1);
			} else {
				poopingAnimal.poop(livingEntity);
			}
		}
	}

	/**
	 * 处理生物受伤前的伤害计算喵~
	 * 应用"脆弱"效果的伤害加成，以及僵尸山羊的冲撞击退效果喵~
	 *
	 * @param event 生物伤害事件喵~
	 */
	@SubscribeEvent
	public static void onLivingHurt(LivingDamageEvent.Pre event) {
		LivingEntity livingEntity = event.getEntity();
		MobEffectInstance effectInstance = livingEntity.getEffect(MISCTWFMobEffects.FRAGILE);
		if(effectInstance != null) {
			event.setNewDamage(event.getNewDamage() * FragileEffect.getDamageMultiplier(effectInstance.getAmplifier()));
		}

		if(event.getSource().getEntity() instanceof ZombieGoatEntity goat) {
			Vec3 direction = goat.position().subtract(livingEntity.position()).normalize();
			double multiplier = goat.isBaby() ? GoatAi.BABY_RAM_KNOCKBACK_FORCE : GoatAi.ADULT_RAM_KNOCKBACK_FORCE;
			livingEntity.knockback(direction.x * multiplier, direction.y * multiplier, direction.z * multiplier);
		}
	}

	/**
	 * 处理生物转化完成后的事件喵~
	 * 保持绵羊的羊毛颜色在转化为僵尸绵羊后不变喵~
	 *
	 * @param event 生物转化事件喵~
	 */
	@SubscribeEvent
	public static void onLivingConvert(LivingConversionEvent.Post event) {
		if(event.getEntity() instanceof Sheep sheep && event.getOutcome() instanceof ZombieSheepEntity zombieSheep) {
			zombieSheep.setColor(sheep.getColor());
		}
	}

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

	/**
	 * 处理玩家游泳判定事件喵~
	 * 允许玩家在血液流体中游泳喵~
	 *
	 * @param event 玩家游泳事件喵~
	 */
	@SubscribeEvent
	public static void onPlayerSwim(PlayerSwimEvent event) {
		Player player = event.getEntity();
		double fluidHeight = player.getFluidHeight(MISCTWFFluidTags.BLOOD);
		if(fluidHeight > 0 && (!player.onGround() || fluidHeight > player.getFluidJumpThreshold())) {
			event.setResult(EventResult.SUCCESS);
		}
	}

	/**
	 * 处理实体生成位置检查事件喵~
	 * 阻止怪物在特定区域生成（通过保存数据判定）喵~
	 *
	 * @param event 实体生成位置检查事件喵~
	 */
	@SubscribeEvent
	public static void onEntitySpawn(MobSpawnEvent.SpawnPlacementCheck event) {
		if(event.getEntityType().getCategory().equals(MobCategory.MONSTER) &&
				MISCTWFSavedData.denyMonsterSpawn(GlobalPos.of(
						event.getLevel().getLevel().dimension(), event.getPos().above()
				))) {
			event.setResult(MobSpawnEvent.SpawnPlacementCheck.Result.FAIL);
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
