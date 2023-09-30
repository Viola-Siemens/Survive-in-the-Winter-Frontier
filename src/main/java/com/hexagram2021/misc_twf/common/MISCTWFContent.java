package com.hexagram2021.misc_twf.common;

import com.hexagram2021.misc_twf.common.register.MISCTWFItems;
import net.minecraftforge.eventbus.api.IEventBus;

public class MISCTWFContent {
	public static void modConstruct(IEventBus bus) {
		MISCTWFItems.init(bus);
	}
}
