package com.hexagram2021.misc_twf;

import com.hexagram2021.misc_twf.common.MISCTWFContent;
import com.hexagram2021.misc_twf.common.ModVanillaCompat;
import com.hexagram2021.misc_twf.common.config.MISCTWFCommonConfig;
import com.hexagram2021.misc_twf.common.network.IMISCTWFPacket;
import com.hexagram2021.misc_twf.common.network.ServerboundOpenTacBackpackPacket;
import com.hexagram2021.misc_twf.common.register.MISCTWFItems;
import com.hexagram2021.misc_twf.common.util.MISCTWFLogger;
import com.hexagram2021.misc_twf.server.MISCTWFSavedData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import top.theillusivec4.curios.Curios;
import top.theillusivec4.curios.api.SlotTypeMessage;
import top.theillusivec4.curios.api.SlotTypePreset;

import java.util.function.Function;

@Mod(SurviveInTheWinterFrontier.MODID)
public class SurviveInTheWinterFrontier {
	public static final String MODID = "misc_twf";
	public static final String VERSION = ModList.get().getModFileById(MODID).versionString();

	public static final SimpleChannel packetHandler = NetworkRegistry.ChannelBuilder
			.named(new ResourceLocation(MODID, "main"))
			.networkProtocolVersion(() -> VERSION)
			.serverAcceptedVersions(VERSION::equals)
			.clientAcceptedVersions(VERSION::equals)
			.simpleChannel();

	public SurviveInTheWinterFrontier() {
		MISCTWFLogger.logger = LogManager.getLogger(MODID);
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		MISCTWFContent.modConstruct(bus);

		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, MISCTWFCommonConfig.getConfig());

		bus.addListener(this::setup);
		bus.addListener(this::enqueueIMC);
		MinecraftForge.EVENT_BUS.addListener(this::serverStarted);
		MinecraftForge.EVENT_BUS.register(this);
	}

	private static int messageId = 0;
	@SuppressWarnings("SameParameterValue")
	private static <T extends IMISCTWFPacket> void registerMessage(Class<T> packetType,
																   Function<FriendlyByteBuf, T> constructor) {
		packetHandler.registerMessage(messageId++, packetType, IMISCTWFPacket::write, constructor, (packet, ctx) -> packet.handle(ctx.get()));
	}

	private void setup(final FMLCommonSetupEvent event) {
		registerMessage(ServerboundOpenTacBackpackPacket.class, ServerboundOpenTacBackpackPacket::new);
		ModVanillaCompat.init();
	}

	private void enqueueIMC(final InterModEnqueueEvent event) {
		InterModComms.sendTo(Curios.MODID, SlotTypeMessage.REGISTER_TYPE, () -> SlotTypePreset.HEAD.getMessageBuilder().build());
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
