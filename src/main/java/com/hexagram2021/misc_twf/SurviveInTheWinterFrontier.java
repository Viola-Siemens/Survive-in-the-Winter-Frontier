package com.hexagram2021.misc_twf;

import com.hexagram2021.misc_twf.common.MISCTWFContent;
import com.hexagram2021.misc_twf.common.register.MISCTWFItems;
import com.hexagram2021.misc_twf.common.util.MISCTWFLogger;
import com.hexagram2021.misc_twf.server.MISCTWFSavedData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;

@Mod(SurviveInTheWinterFrontier.MODID)
public class SurviveInTheWinterFrontier {
	public static final String MODID = "misc_twf";

	public SurviveInTheWinterFrontier() {
		MISCTWFLogger.logger = LogManager.getLogger(MODID);
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		MISCTWFContent.modConstruct(bus);

		bus.addListener(this::setup);
		MinecraftForge.EVENT_BUS.addListener(this::serverStarted);
		MinecraftForge.EVENT_BUS.register(this);
	}

	private void setup(final FMLCommonSetupEvent event) {

	}

	public void serverStarted(final ServerStartedEvent event) {
		ServerLevel world = event.getServer().getLevel(Level.OVERWORLD);
		assert world != null;
		if(!world.isClientSide) {
			MISCTWFSavedData worldData = world.getDataStorage().computeIfAbsent(MISCTWFSavedData::new, MISCTWFSavedData::new, MISCTWFSavedData.SAVED_DATA_NAME);
			MISCTWFSavedData.setInstance(worldData);
		}
	}

	public static final CreativeModeTab ITEM_GROUP = new CreativeModeTab(MODID) {
		@Override
		public ItemStack makeIcon() {
			return new ItemStack(MISCTWFItems.ABYSS_VIRUS_VACCINE);
		}
	};
}
