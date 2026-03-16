package com.hexagram2021.misc_twf.common.item;

import com.hexagram2021.misc_twf.common.register.MISCTWFItems;
import com.hexagram2021.misc_twf.server.MISCTWFSavedData;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

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
		if(MISCTWFSavedData.isImmuneToZombification(player.getUUID())) {
			return InteractionResultHolder.pass(itemstack);
		}
		itemstack.shrink(1);
		MISCTWFSavedData.setImmuneToZombification(player.getUUID(), player.tickCount);
		afterUse(player, player);
		if(itemstack.isEmpty()) {
			return InteractionResultHolder.consume(new ItemStack(MISCTWFItems.Materials.SYRINGE));
		}
		player.drop(new ItemStack(MISCTWFItems.Materials.SYRINGE), true);
		return InteractionResultHolder.consume(itemstack);
	}

	public static void afterUse(Player player, LivingEntity entity) {
		player.sendSystemMessage(Component.translatable("message.misc_twf.abyss_virus_vaccine.success", entity.getDisplayName()));
	}
}
