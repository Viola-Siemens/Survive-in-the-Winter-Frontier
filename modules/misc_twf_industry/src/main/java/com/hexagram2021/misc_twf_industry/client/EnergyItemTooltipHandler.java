package com.hexagram2021.misc_twf_industry.client;

import com.hexagram2021.misc_twf_industry.common.item.IEnergyItem;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import static com.hexagram2021.misc_twf_industry.MiscTwfIndustry.MODID;

/**
 * 工业生态模块客户端物品提示处理器喵~
 *
 * <p>当鼠标悬浮在能源物品（夜视仪、蓄电池、远行者护甲等实现了 {@link IEnergyItem} 的物品）上时，
 * 显示其当前电量与最大电量喵~</p>
 *
 * @author liudongyu
 */
@EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
public final class EnergyItemTooltipHandler {
	/**
	 * 处理物品提示显示事件，为能源物品追加电量信息喵~
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
		}
	}

	private EnergyItemTooltipHandler() {
	}
}
