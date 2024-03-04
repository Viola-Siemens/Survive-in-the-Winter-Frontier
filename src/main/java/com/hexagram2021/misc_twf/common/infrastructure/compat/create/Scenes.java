package com.hexagram2021.misc_twf.common.infrastructure.compat.create;

import com.simibubi.create.foundation.ponder.SceneBuilder;
import com.simibubi.create.foundation.ponder.SceneBuildingUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public final class Scenes {
	private Scenes() {
	}

	public static void moldDetacher(SceneBuilder scene, SceneBuildingUtil util) {
		scene.title("mold_detacher", "Using mold detachers");

		scene.world.modifyKineticSpeed(util.select.everywhere(), f -> -32.0F);
		scene.configureBasePlate(0, 0, 5);
		scene.world.showSection(util.select.layer(0), Direction.UP);
		scene.idle(10);
		scene.world.showSection(util.select.layer(1), Direction.DOWN);
		scene.idle(10);

		BlockPos detacher = util.grid.at(2, 1, 2);
		scene.overlay.showText(140)
				.attachKeyFrame()
				.sharedText("mold_detacher")
				.pointAt(util.vector.centerOf(detacher));
		scene.idle(160);
		scene.overlay.showText(140)
				.attachKeyFrame()
				.sharedText("power_side")
				.pointAt(util.vector.centerOf(detacher.south()));
		scene.idle(160);
		scene.overlay.showText(160)
				.attachKeyFrame()
				.sharedText("input_funnel")
				.pointAt(util.vector.centerOf(detacher.west()));
		scene.idle(160);
		scene.markAsFinished();
	}
}
