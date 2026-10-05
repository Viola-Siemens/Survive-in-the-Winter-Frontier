package com.hexagram2021.misc_twf_industry.common;

import com.google.common.collect.Streams;
import com.hexagram2021.misc_twf_industry.common.config.MISCTWFIndustryConfig;
import com.hexagram2021.misc_twf_industry.common.effect.FragileEffect;
import com.hexagram2021.misc_twf_industry.common.item.WayfarerArmorItem;
import com.hexagram2021.misc_twf_industry.common.register.MISCTWFMobEffects;
import com.hexagram2021.misc_twf_industry.server.MISCTWFLampSavedData;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.MobSpawnEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.Objects;

import static com.hexagram2021.misc_twf_industry.MiscTwfIndustry.MODID;

/**
 * 工业生态模块运行时事件处理器喵~
 *
 * <p>处理远行者护甲供能与套装效果、紫外线灯阻止刷怪、点火微调喵~</p>
 *
 * @author liudongyu
 */
@EventBusSubscriber(modid = MODID)
public final class IndustryEventHandler {
	/**
	 * 处理实体生成位置检查事件，紫外线灯范围内禁止怪物生成喵~
	 *
	 * @param event 实体生成位置检查事件喵~
	 */
	@SubscribeEvent
	public static void onEntitySpawn(MobSpawnEvent.SpawnPlacementCheck event) {
		if(event.getEntityType().getCategory().equals(MobCategory.MONSTER) &&
				MISCTWFLampSavedData.denyMonsterSpawn(GlobalPos.of(
						event.getLevel().getLevel().dimension(), event.getPos().above()
				))) {
			event.setResult(MobSpawnEvent.SpawnPlacementCheck.Result.FAIL);
		}
	}

	/**
	 * 处理玩家的每 tick 更新，为远行者护甲供能并施加套装效果喵~
	 *
	 * @param event 玩家 tick 事件喵~
	 */
	@SubscribeEvent
	public static void onPlayerTick(PlayerTickEvent.Pre event) {
		Player player = event.getEntity();
		if(!player.level().isClientSide && player.tickCount % 20 == 0) {
			player.getArmorSlots().forEach(armorSlot -> {
				if(armorSlot.getItem() instanceof WayfarerArmorItem item) {
					IEnergyStorage ies = armorSlot.getCapability(Capabilities.EnergyStorage.ITEM);
					if (ies != null && ies.getEnergyStored() > 0) {
						MobEffectInstance effectInstance = item.getTickedEffect();
						if (effectInstance != null) {
							player.addEffect(effectInstance);
						}
						ies.extractEnergy(1, false);
					}
				}
			});

			if (Streams.stream(player.getArmorSlots()).filter(Objects::nonNull).allMatch(itemStack -> itemStack.getItem() instanceof WayfarerArmorItem)) {
				MISCTWFIndustryConfig.WAYFARER_ARMOR_EFFECTS.get().forEach(
						id -> BuiltInRegistries.MOB_EFFECT
								.getHolder(ResourceLocation.parse(id))
								.ifPresent(effect -> player.addEffect(new MobEffectInstance(effect, 40)))
				);
			}
		}
	}

	/**
	 * 处理生物受伤前的伤害计算，应用“脆弱”效果的伤害加成喵~
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
	}

	/**
	 * 处理打火石或火焰弹点燃熔炉喵~
	 *
	 * @param event 方块工具修改事件喵~
	 */
	@SubscribeEvent
	public static void onBlockToolModification(BlockEvent.BlockToolModificationEvent event) {
		BlockState blockState = event.getFinalState();
		if(event.getItemAbility().equals(ItemAbilities.FIRESTARTER_LIGHT) && blockState.getBlock() instanceof AbstractFurnaceBlock &&
				blockState.hasProperty(BlockStateProperties.LIT) && !blockState.getValue(BlockStateProperties.LIT) &&
				(!blockState.hasProperty(BlockStateProperties.WATERLOGGED) || !blockState.getValue(BlockStateProperties.WATERLOGGED))) {
			event.setFinalState(blockState.setValue(BlockStateProperties.LIT, true));
		}
	}

	private IndustryEventHandler() {
	}
}
