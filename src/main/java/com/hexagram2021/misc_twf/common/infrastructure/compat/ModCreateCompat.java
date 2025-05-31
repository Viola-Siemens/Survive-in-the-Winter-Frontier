package com.hexagram2021.misc_twf.common.infrastructure.compat;

import com.hexagram2021.misc_twf.common.infrastructure.compat.create.Scenes;
import com.hexagram2021.misc_twf.common.register.MISCTWFBlocks;
import com.simibubi.create.foundation.ponder.PonderRegistrationHelper;
import com.tterrag.registrate.Registrate;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.ITEM_GROUP;
import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;

public class ModCreateCompat {
	public static final Registrate REGISTRATE = Registrate.create(MODID).creativeModeTab(() -> ITEM_GROUP);
	private static final PonderRegistrationHelper HELPER = new PonderRegistrationHelper(MODID);

	public static void register() {
		HELPER.forComponents(MISCTWFBlocks.MOLD_DETACHER).addStoryBoard("mold_detacher", Scenes::moldDetacher);
		HELPER.forComponents(MISCTWFBlocks.MOLD_WORKBENCH).addStoryBoard("mold_workbench", Scenes::moldWorkbench);
	}
}
