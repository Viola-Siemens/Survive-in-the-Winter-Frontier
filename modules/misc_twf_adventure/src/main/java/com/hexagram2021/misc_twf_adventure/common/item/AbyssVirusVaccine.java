package com.hexagram2021.misc_twf_adventure.common.item;

import com.hexagram2021.misc_twf_adventure.common.register.MISCTWFItems;
import com.hexagram2021.misc_twf_zombie_animals.server.MISCTWFImmunitySavedData;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * 深渊病毒疫苗物品
 *
 * @author liudongyu
 */
public class AbyssVirusVaccine extends Item {
	public AbyssVirusVaccine(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack itemstack = player.getItemInHand(hand);
		if(level.isClientSide) {
			return InteractionResultHolder.success(itemstack);
		}
		if(MISCTWFImmunitySavedData.isImmuneToZombification(player.getUUID())) {
			return InteractionResultHolder.pass(itemstack);
		}
		itemstack.shrink(1);
		MISCTWFImmunitySavedData.setImmuneToZombification(player.getUUID(), player.tickCount);
		afterUse(player, player);
		if(itemstack.isEmpty()) {
			return InteractionResultHolder.consume(new ItemStack(MISCTWFItems.Materials.SYRINGE));
		}
		player.drop(new ItemStack(MISCTWFItems.Materials.SYRINGE), true);
		return InteractionResultHolder.consume(itemstack);
	}

	/**
	 * 使用后的行为
	 * @param player 玩家
	 * @param entity 作用实体
	 */
	public static void afterUse(Player player, LivingEntity entity) {
		player.sendSystemMessage(Component.translatable("message.misc_twf.abyss_virus_vaccine.success", entity.getDisplayName()));
	}
}
