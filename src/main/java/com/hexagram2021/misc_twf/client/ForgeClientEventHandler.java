package com.hexagram2021.misc_twf.client;

import com.hexagram2021.misc_twf.common.data_component.TravelersBackpackTacData;
import com.hexagram2021.misc_twf.common.item.IEnergyItem;
import com.hexagram2021.misc_twf.common.register.MISCTWFDataComponentTypes;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;

/**
 * Forge 客户端事件处理器，用���监听客户端物品提示事件喵~
 * 负责为能源物品显示电量信息，以及为已升级 TAC 弹药槽的旅行背包显示提示信息喵~
 *
 * @author liudongyu
 */
@EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
public class ForgeClientEventHandler {
	/**
	 * 处理物品提示显示事件喵~
	 * 当鼠标悬浮在能源物品上时显示电量信息，悬浮在旅行背包上时显示 TAC 弹药槽提示喵~
	 *
	 * @param event 物品提示事件喵~
	 */
	@SubscribeEvent
	public static void onToolTipShow(ItemTooltipEvent event) {
		if(event.getItemStack().getItem() instanceof IEnergyItem) {
			IEnergyStorage ies = event.getItemStack().getCapability(Capabilities.EnergyStorage.ITEM);
			if(ies != null) {
				event.getToolTip().add(Component.translatable("item.misc_twf.energy.stored", ies.getEnergyStored(), ies.getMaxEnergyStored()));
			}
		} else if(event.getItemStack().getItem() instanceof TravelersBackpackItem) {
			TravelersBackpackTacData data = event.getItemStack().get(MISCTWFDataComponentTypes.TRAVELERS_BACKPACK_TAC_DATA);
			if(data != null && data.upgradedToTac()) {
				event.getToolTip().add(Component.translatable("item.misc_twf.has_tac_slot").withStyle(ChatFormatting.GRAY));
			}
		}
	}
}
