package com.hexagram2021.misc_twf.common;

import com.hexagram2021.misc_twf.common.register.*;
import net.minecraftforge.eventbus.api.IEventBus;

public class MISCTWFContent {
	public static void modConstruct(IEventBus bus) {
		initTags();

		MISCTWFBlocks.init(bus);
		MISCTWFBlockEntities.init(bus);
		MISCTWFItems.init(bus);
		MISCTWFMenuTypes.init(bus);
	}

	private static void initTags() {
		MISCTWFItemTags.init();
	}
}
