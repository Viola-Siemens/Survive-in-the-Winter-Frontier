package com.hexagram2021.misc_twf.common.infrastructure.compat;

import com.hexagram2021.misc_twf.common.infrastructure.compat.create.Scenes;
import com.hexagram2021.misc_twf.common.register.MISCTWFBlocks;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;

/**
 * 和 Create 模组的兼容
 *
 * @author liudongyu
 */
public final class ModCreateCompat implements PonderPlugin {
	public static final Registrate REGISTRATE = Registrate.create(MODID);

	@Override
	public String getModId() {
		return MODID;
	}

	@Override
	public void registerScenes(PonderSceneRegistrationHelper<ResourceLocation> helper) {
		PonderSceneRegistrationHelper<ItemProviderEntry<?, ?>> itemHelper = helper.withKeyFunction(DeferredHolder::getId);
		itemHelper.forComponents(MISCTWFBlocks.MOLD_DETACHER).addStoryBoard("mold_detacher", Scenes::moldDetacher);
		itemHelper.forComponents(MISCTWFBlocks.MOLD_WORKBENCH).addStoryBoard("mold_workbench", Scenes::moldWorkbench);
	}
}
