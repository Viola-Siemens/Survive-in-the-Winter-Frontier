package com.hexagram2021.misc_twf.common.register;

import com.hexagram2021.misc_twf.common.loot.TravelersBackpackTacOpsModifier;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;

@SuppressWarnings("unused")
public final class MISCTWFTravelersBackpackTacOps {
	private static final DeferredRegister<GlobalLootModifierSerializer<?>> REGISTER = DeferredRegister.create(ForgeRegistries.Keys.LOOT_MODIFIER_SERIALIZERS, MODID);

	private static final RegistryObject<GlobalLootModifierSerializer<TravelersBackpackTacOpsModifier>> TRAVELERS_BACKPACK_TAC_OPS = REGISTER.register(
			"travelers_backpack_tac_nbt_ops", TravelersBackpackTacOpsModifier.Serializer::new
	);

	private MISCTWFTravelersBackpackTacOps() {
	}

	public static void init(IEventBus bus) {
		REGISTER.register(bus);
	}
}
