package com.hexagram2021.misc_twf_wildlife.common.register;

import com.hexagram2021.misc_twf_wildlife.common.data_component.DeadAnimalData;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.hexagram2021.misc_twf_wildlife.MiscTwfWildlife.CONTENT_NAMESPACE;

/**
 * 农牧生态模块数据组件类型注册类喵~
 *
 * @author liudongyu
 */
public final class MISCTWFDataComponentTypes {
	private static final DeferredRegister<DataComponentType<?>> REGISTER = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, CONTENT_NAMESPACE);

	/** 动物尸体数据组件喵~ */
	public static final DeferredHolder<DataComponentType<?>, DataComponentType<DeadAnimalData>> DEAD_ANIMAL_DATA = REGISTER.register(
			"dead_animal_data", () -> DataComponentType.<DeadAnimalData>builder()
					.persistent(DeadAnimalData.CODEC)
					.networkSynchronized(DeadAnimalData.STREAM_CODEC)
					.build()
	);

	/**
	 * 初始化并注册所有数据组件类型到事件总线喵~
	 *
	 * @param bus NeoForge 事件总线喵~
	 */
	public static void init(IEventBus bus) {
		REGISTER.register(bus);
	}

	private MISCTWFDataComponentTypes() {
	}
}
