package com.hexagram2021.misc_twf;

import com.hexagram2021.misc_twf.common.MISCTWFContent;
import com.hexagram2021.misc_twf.common.ModVanillaCompat;
import com.hexagram2021.misc_twf.common.config.MISCTWFCommonConfig;
import com.hexagram2021.misc_twf.common.network.MonsterEggAnimationPacket;
import com.hexagram2021.misc_twf.common.network.ServerboundOpenTacBackpackPacket;
import com.hexagram2021.misc_twf.common.register.MISCTWFItems;
import com.hexagram2021.misc_twf.common.register.MISCTWFRecipeBookTypes;
import com.hexagram2021.misc_twf.common.register.MISCTWFRecipeTypes;
import com.hexagram2021.misc_twf.common.util.MISCTWFLogger;
import com.hexagram2021.misc_twf.server.MISCTWFSavedData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import org.apache.logging.log4j.LogManager;

@Mod(SurviveInTheWinterFrontier.MODID)
public class SurviveInTheWinterFrontier {
	public static final String MODID = "misc_twf";

	public SurviveInTheWinterFrontier(IEventBus modBus, ModContainer modContainer) {
		MISCTWFLogger.logger = LogManager.getLogger(MODID);
		MISCTWFContent.modConstruct(modBus);

		modContainer.registerConfig(ModConfig.Type.COMMON, MISCTWFCommonConfig.getConfig());

		modBus.addListener(this::setup);
		NeoForge.EVENT_BUS.addListener(this::serverStarted);
		NeoForge.EVENT_BUS.addListener(this::onTagsUpdate);
		NeoForge.EVENT_BUS.register(this);
	}

	private void setup(final FMLCommonSetupEvent event) {
		registerMessage(ServerboundOpenTacBackpackPacket.class, ServerboundOpenTacBackpackPacket::new);
		registerMessage(MonsterEggAnimationPacket.class, MonsterEggAnimationPacket::new);
		event.enqueueWork(() -> {
			ModVanillaCompat.init();
			MISCTWFRecipeBookTypes.init();
		});
	}

	public void serverStarted(final ServerStartedEvent event) {
		ServerLevel world = event.getServer().getLevel(Level.OVERWORLD);
		event.getServer().levelKeys().forEach(level -> MISCTWFSavedData.dimensions.add(level.location()));
		assert world != null;
		if(!world.isClientSide) {
			MISCTWFSavedData worldData = world.getDataStorage().computeIfAbsent(MISCTWFSavedData::new, MISCTWFSavedData::new, MISCTWFSavedData.SAVED_DATA_NAME);
			MISCTWFSavedData.setInstance(worldData);
		}
	}

	private void onTagsUpdate(final TagsUpdatedEvent event) {
		if (event.getUpdateCause() == TagsUpdatedEvent.UpdateCause.SERVER_DATA_LOAD) {
			com.tacz.guns.resource.CommonAssetsManager instance = com.tacz.guns.resource.CommonAssetsManager.getInstance();
			if (instance != null && instance.recipeManager != null) {
				instance.recipeManager.getAllRecipesFor(MISCTWFRecipeTypes.RECOVERY_FURNACE.get())
						.forEach(recipe -> recipe.ingredient().init());
			}
		}
	}

	public static final CreativeModeTab ITEM_GROUP = new CreativeModeTab(MODID) {
		@Override
		public ItemStack makeIcon() {
			return new ItemStack(MISCTWFItems.ABYSS_VIRUS_VACCINE);
		}
	};
}
