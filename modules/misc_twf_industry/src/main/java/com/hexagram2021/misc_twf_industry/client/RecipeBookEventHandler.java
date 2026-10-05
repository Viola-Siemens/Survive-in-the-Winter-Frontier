package com.hexagram2021.misc_twf_industry.client;

import com.hexagram2021.misc_twf_industry.common.register.MISCTWFRecipeBookTypes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterRecipeBookCategoriesEvent;

import static com.hexagram2021.misc_twf_industry.MiscTwfIndustry.MODID;

/**
 * 工业生态模块客户端配方书注册处理器（仅客户端）喵~
 *
 * @author liudongyu
 */
@EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
public final class RecipeBookEventHandler {
	/**
	 * 注册回收炉配方书的分类与分类查找器喵~
	 *
	 * @param event 配方书分类注册事件喵~
	 */
	@SubscribeEvent
	public static void onRegisterRecipeBookCategories(RegisterRecipeBookCategoriesEvent event) {
		MISCTWFRecipeBookTypes.init(event);
	}

	private RecipeBookEventHandler() {
	}
}
