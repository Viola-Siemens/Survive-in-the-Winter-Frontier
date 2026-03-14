package com.hexagram2021.misc_twf.common.infrastructure.compat;

import com.tterrag.registrate.Registrate;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;

public class ModCreateCompat {
	public static final Registrate REGISTRATE = Registrate.create(MODID).defaultCreativeTab(ResourceKey.create(Registries.CREATIVE_MODE_TAB, ResourceLocation.fromNamespaceAndPath(MODID, "main")));
}
