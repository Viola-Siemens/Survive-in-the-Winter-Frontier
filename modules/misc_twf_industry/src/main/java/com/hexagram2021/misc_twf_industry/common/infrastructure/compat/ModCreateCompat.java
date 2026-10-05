package com.hexagram2021.misc_twf_industry.common.infrastructure.compat;

import com.hexagram2021.misc_twf_industry.common.infrastructure.compat.create.Scenes;
import com.hexagram2021.misc_twf_industry.common.register.MISCTWFBlocks;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;

import static com.hexagram2021.misc_twf_industry.MiscTwfIndustry.CONTENT_NAMESPACE;
import static com.hexagram2021.misc_twf_industry.MiscTwfIndustry.MODID;

/**
 * 和 Create 模组的兼容
 *
 * @author liudongyu
 */
public final class ModCreateCompat implements PonderPlugin {
	public static final MISCTWFRegistrate REGISTRATE = MISCTWFRegistrate.create(MODID, CONTENT_NAMESPACE);

	@Override
	public String getModId() {
		return CONTENT_NAMESPACE;
	}

	@Override
	public void registerScenes(PonderSceneRegistrationHelper<ResourceLocation> helper) {
		PonderSceneRegistrationHelper<ItemProviderEntry<?, ?>> itemHelper = helper.withKeyFunction(DeferredHolder::getId);
		itemHelper.forComponents(MISCTWFBlocks.MOLD_DETACHER).addStoryBoard("mold_detacher", Scenes::moldDetacher);
		itemHelper.forComponents(MISCTWFBlocks.MOLD_WORKBENCH).addStoryBoard("mold_workbench", Scenes::moldWorkbench);
	}
}
