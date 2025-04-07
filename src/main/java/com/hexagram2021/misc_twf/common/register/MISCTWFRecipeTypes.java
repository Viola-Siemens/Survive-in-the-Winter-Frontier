package com.hexagram2021.misc_twf.common.register;

import com.hexagram2021.misc_twf.common.recipe.MoldDetacherRecipe;
import com.hexagram2021.misc_twf.common.recipe.MoldWorkbenchRecipe;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;

@SuppressWarnings("SameParameterValue")
public final class MISCTWFRecipeTypes {
	private static final DeferredRegister<RecipeType<?>> REGISTER = DeferredRegister.create(Registry.RECIPE_TYPE_REGISTRY, MODID);

	public static final RegistryObject<RecipeType<MoldDetacherRecipe>> MOLD_DETACHER = register("mold_detach");
	public static final RegistryObject<RecipeType<MoldWorkbenchRecipe>> MOLD_WORKBENCH = register("mold_workbench");

	private MISCTWFRecipeTypes() {
	}

	public static void init(IEventBus bus) {
		REGISTER.register(bus);
	}

	private static <T extends Recipe<?>> RegistryObject<RecipeType<T>> register(String name) {
		return REGISTER.register(name, () -> new RecipeType<>() {
			@Override
			public String toString() {
				return new ResourceLocation(MODID, name).toString();
			}
		});
	}
}
