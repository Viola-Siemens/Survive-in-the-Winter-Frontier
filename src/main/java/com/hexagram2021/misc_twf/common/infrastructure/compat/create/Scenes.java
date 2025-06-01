package com.hexagram2021.misc_twf.common.infrastructure.compat.create;

import com.hexagram2021.misc_twf.common.block.MoldWorkbenchBlock;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.simpleRelays.CogWheelBlock;
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

	public static void moldWorkbench(SceneBuilder scene, SceneBuildingUtil util) {
		scene.title("mold_workbench", "Using mold workbenches");

		scene.configureBasePlate(0, 0, 5);
		scene.world.showSection(util.select.layer(0), Direction.UP);
		scene.idle(10);
		scene.world.showSection(util.select.layer(1), Direction.WEST);
		scene.idle(10);
		scene.world.showSection(util.select.layer(2), Direction.DOWN);
		scene.idle(10);

		BlockPos detacher = util.grid.at(2, 1, 2);
		BlockPos detacherUp = util.grid.at(2, 2, 2);
		scene.overlay.showText(140)
				.attachKeyFrame()
				.sharedText("mold_workbench_default")
				.pointAt(util.vector.centerOf(detacher));
		scene.idle(160);

		scene.world.cycleBlockProperty(detacher.north(), MoldWorkbenchBlock.ARMED);
		scene.world.cycleBlockProperty(detacher, MoldWorkbenchBlock.ARMED);
		scene.world.cycleBlockProperty(detacher.south(), MoldWorkbenchBlock.ARMED);
		scene.world.cycleBlockProperty(detacherUp.north(), MoldWorkbenchBlock.ARMED);
		scene.world.cycleBlockProperty(detacherUp, MoldWorkbenchBlock.ARMED);
		scene.world.cycleBlockProperty(detacherUp.south(), MoldWorkbenchBlock.ARMED);
		scene.world.setBlock(util.grid.at(2, 1, 0), AllBlocks.COGWHEEL.getDefaultState().setValue(CogWheelBlock.AXIS, Direction.Axis.Z), true);
		scene.world.modifyKineticSpeed(util.select.everywhere(), f -> 32.0F);
		scene.overlay.showText(140)
				.attachKeyFrame()
				.sharedText("mold_workbench_armed")
				.pointAt(util.vector.topOf(detacherUp.north()));
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
	}
}
