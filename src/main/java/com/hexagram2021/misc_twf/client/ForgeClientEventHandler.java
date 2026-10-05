package com.hexagram2021.misc_twf.client;

import com.hexagram2021.misc_twf.common.data_component.TravelersBackpackTacData;
import com.hexagram2021.misc_twf.common.register.MISCTWFDataComponentTypes;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;

/**
 * 模组客户端事件处理器，用于监听客户端物品提示事件喵~
 *
 * <p>为已升级 TAC 弹药槽的旅行背包显示提示信息；能源物品的电量提示已随模块化迁至
 * 工业生态模块（`misc_twf_industry` 的 `client/EnergyItemTooltipHandler`）喵~</p>
 *
 * @author liudongyu
 */
@EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
public final class ForgeClientEventHandler {
	/**
	 * 处理物品提示显示事件，为已升级 TAC 弹药槽的旅行背包追加提示喵~
	 *
	 * @param event 物品提示事件喵~
	 */
	@SubscribeEvent
	public static void onToolTipShow(ItemTooltipEvent event) {
		if(event.getItemStack().getItem() instanceof TravelersBackpackItem) {
			TravelersBackpackTacData data = event.getItemStack().get(MISCTWFDataComponentTypes.TRAVELERS_BACKPACK_TAC_DATA);
			if(data != null && data.upgradedToTac()) {
				event.getToolTip().add(Component.translatable("item.misc_twf.has_tac_slot").withStyle(ChatFormatting.GRAY));
			}
		}
	}

	private ForgeClientEventHandler() {
	}
}
