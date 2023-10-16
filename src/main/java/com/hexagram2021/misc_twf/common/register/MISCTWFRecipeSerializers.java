package com.hexagram2021.misc_twf.common.register;

import com.hexagram2021.misc_twf.common.recipe.BackpackTacUpgradeRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;

public final class MISCTWFRecipeSerializers {
	private static final DeferredRegister<RecipeSerializer<?>> REGISTER = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MODID);

	public static final RegistryObject<RecipeSerializer<BackpackTacUpgradeRecipe>> BACKPACK_TAC_UPGRADE = REGISTER.register("backpack_tac_upgrade", BackpackTacUpgradeRecipe.Serializer::new);

	private MISCTWFRecipeSerializers() {
	}

	public static void init(IEventBus bus) {
		REGISTER.register(bus);
	}
}
