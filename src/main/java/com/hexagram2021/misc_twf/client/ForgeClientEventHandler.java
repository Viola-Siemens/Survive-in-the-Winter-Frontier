package com.hexagram2021.misc_twf.client;

import com.hexagram2021.misc_twf.common.item.IEnergyItem;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ForgeClientEventHandler {
	@SubscribeEvent
	public static void onToolTipShow(ItemTooltipEvent event) {
		if(event.getItemStack().getItem() instanceof IEnergyItem) {
			event.getItemStack().getCapability(CapabilityEnergy.ENERGY).ifPresent(ies ->
					event.getToolTip().add(new TranslatableComponent("item.misc_twf.energy.stored", ies.getEnergyStored(), ies.getMaxEnergyStored())));
		} else if(event.getItemStack().getItem() instanceof TravelersBackpackItem) {
			if(event.getItemStack().hasTag()) {
				CompoundTag tag = event.getItemStack().getTag();
				if (tag != null && tag.contains("UpgradeToTac", Tag.TAG_BYTE) && tag.getBoolean("UpgradeToTac")) {
					event.getToolTip().add(new TranslatableComponent("item.misc_twf.has_tac_slot").withStyle(ChatFormatting.GRAY));
				}
			}
		}
	}
}
