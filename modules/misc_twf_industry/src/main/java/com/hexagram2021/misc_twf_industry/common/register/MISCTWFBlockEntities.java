package com.hexagram2021.misc_twf_industry.common.register;

import com.hexagram2021.misc_twf_industry.common.block.entity.MoldDetacherBlockEntity;
import com.hexagram2021.misc_twf_industry.common.block.entity.MoldWorkbenchBlockEntity;
import com.hexagram2021.misc_twf_industry.common.block.entity.RecoveryFurnaceBlockEntity;
import com.hexagram2021.misc_twf_industry.common.block.entity.UltravioletLampBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.hexagram2021.misc_twf_industry.MiscTwfIndustry.CONTENT_NAMESPACE;

/**
 * 工业生态模块方块实体类型注册类喵~
 *
 * @author liudongyu
 */
public final class MISCTWFBlockEntities {
	private static final DeferredRegister<BlockEntityType<?>> REGISTER = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, CONTENT_NAMESPACE);

	/** 强紫外线照射灯方块实体类型喵~ */
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<UltravioletLampBlockEntity>> ULTRAVIOLET_LAMP = REGISTER.register("ultraviolet_lamp", () -> BlockEntityType.Builder.of(
			UltravioletLampBlockEntity::new, MISCTWFBlocks.ULTRAVIOLET_LAMP.get()
	).build(null));

	/** 模具分离器方块实体类型喵~ */
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MoldDetacherBlockEntity>> MOLD_DETACHER = REGISTER.register("mold_detacher", () -> BlockEntityType.Builder.of(
			MoldDetacherBlockEntity::new, MISCTWFBlocks.MOLD_DETACHER.get()
	).build(null));

	/** 回收炉方块实体类型喵~ */
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RecoveryFurnaceBlockEntity>> RECOVERY_FURNACE = REGISTER.register("recovery_furnace", () -> BlockEntityType.Builder.of(
			RecoveryFurnaceBlockEntity::new, MISCTWFBlocks.RECOVERY_FURNACE.get()
	).build(null));

	/** 模具加工台方块实体类型喵~ */
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MoldWorkbenchBlockEntity>> MOLD_WORKBENCH = REGISTER.register("mold_workbench", () -> BlockEntityType.Builder.of(
			MoldWorkbenchBlockEntity::new, MISCTWFBlocks.MOLD_WORKBENCH.get()
	).build(null));

	/**
	 * 初始化并注册所有方块实体类型到事件总线喵~
	 *
	 * @param bus NeoForge 事件总线喵~
	 */
	public static void init(IEventBus bus) {
		REGISTER.register(bus);
	}

	private MISCTWFBlockEntities() {
	}
}
