package com.hexagram2021.misc_twf.common.infrastructure.compat.create;

import com.hexagram2021.misc_twf.common.block.MoldWorkbenchBlock;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.base.RotatedPillarKineticBlock;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

/**
 * 思索场景
 *
 * @author liudongyu
 */
public final class Scenes {
	private Scenes() {
	}

	/**
	 * 模具分离器场景
	 * @param builder 场景构造器
	 * @param util 工具
	 */
	public static void moldDetacher(SceneBuilder builder, SceneBuildingUtil util) {
		CreateSceneBuilder scene = new CreateSceneBuilder(builder);
		scene.title("mold_detacher", "Using mold detachers");

		scene.world().modifyKineticSpeed(util.select().everywhere(), f -> -32.0F);
		scene.configureBasePlate(0, 0, 5);
		scene.world().showSection(util.select().layer(0), Direction.UP);
		scene.idle(10);
		scene.world().showSection(util.select().layer(1), Direction.DOWN);
		scene.idle(10);

		BlockPos detacher = util.grid().at(2, 1, 2);
		scene.overlay().showText(140)
				.attachKeyFrame()
				.sharedText("mold_detacher")
				.pointAt(util.vector().centerOf(detacher));
		scene.idle(160);
		scene.overlay().showText(140)
				.attachKeyFrame()
				.sharedText("power_side")
				.pointAt(util.vector().centerOf(detacher.south()));
		scene.idle(160);
		scene.overlay().showText(160)
				.attachKeyFrame()
				.sharedText("input_funnel")
				.pointAt(util.vector().centerOf(detacher.west()));
		scene.idle(160);
		scene.markAsFinished();
	}

	/**
	 * 模具加工台
	 * @param builder 场景构造器
	 * @param util 工具
	 */
	public static void moldWorkbench(SceneBuilder builder, SceneBuildingUtil util) {
		CreateSceneBuilder scene = new CreateSceneBuilder(builder);
		scene.title("mold_workbench", "Using mold workbenches");

		scene.configureBasePlate(0, 0, 5);
		scene.world().showSection(util.select().layer(0), Direction.UP);
		scene.idle(10);
		scene.world().showSection(util.select().layer(1), Direction.WEST);
		scene.idle(10);
		scene.world().showSection(util.select().layer(2), Direction.DOWN);
		scene.idle(10);

		BlockPos detacher = util.grid().at(2, 1, 2);
		BlockPos detacherUp = util.grid().at(2, 2, 2);
		scene.overlay().showText(140)
				.attachKeyFrame()
				.sharedText("mold_workbench_default")
				.pointAt(util.vector().centerOf(detacher));
		scene.idle(160);

		scene.world().cycleBlockProperty(detacher.north(), MoldWorkbenchBlock.ARMED);
		scene.world().cycleBlockProperty(detacher, MoldWorkbenchBlock.ARMED);
		scene.world().cycleBlockProperty(detacher.south(), MoldWorkbenchBlock.ARMED);
		scene.world().cycleBlockProperty(detacherUp.north(), MoldWorkbenchBlock.ARMED);
		scene.world().cycleBlockProperty(detacherUp, MoldWorkbenchBlock.ARMED);
		scene.world().cycleBlockProperty(detacherUp.south(), MoldWorkbenchBlock.ARMED);
		scene.world().setBlock(util.grid().at(2, 1, 0), AllBlocks.COGWHEEL.getDefaultState().setValue(RotatedPillarKineticBlock.AXIS, Direction.Axis.Z), true);
		scene.world().modifyKineticSpeed(util.select().everywhere(), f -> 32.0F);
		scene.overlay().showText(140)
				.attachKeyFrame()
				.sharedText("mold_workbench_armed")
				.pointAt(util.vector().topOf(detacherUp.north()));
		scene.idle(160);
		scene.overlay().showText(140)
				.attachKeyFrame()
				.sharedText("power_side")
				.pointAt(util.vector().centerOf(detacher.south()));
		scene.idle(160);
		scene.overlay().showText(160)
				.attachKeyFrame()
				.sharedText("input_funnel")
				.pointAt(util.vector().centerOf(detacher.west()));
		scene.idle(160);
	}
}
