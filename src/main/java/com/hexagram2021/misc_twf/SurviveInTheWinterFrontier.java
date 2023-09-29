package com.hexagram2021.misc_twf;

import com.hexagram2021.misc_twf.common.MISCTWFContent;
import com.hexagram2021.misc_twf.common.register.MISCTWFItems;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(SurviveInTheWinterFrontier.MODID)
public class SurviveInTheWinterFrontier {
	public static final String MODID = "misc_twf";

	public SurviveInTheWinterFrontier() {
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		MISCTWFContent.modConstruct(bus);

		bus.addListener(this::setup);
		MinecraftForge.EVENT_BUS.register(this);
	}

	private void setup(final FMLCommonSetupEvent event) {

	}

	public static final CreativeModeTab ITEM_GROUP = new CreativeModeTab(MODID) {
		@Override
		public ItemStack makeIcon() {
			return new ItemStack(MISCTWFItems.XXX);
		}
	};
}
