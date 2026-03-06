package com.hexagram2021.misc_twf.common.register;

import com.hexagram2021.misc_twf.common.data_component.MonsterEggEntries;
import com.hexagram2021.misc_twf.common.data_component.TravelersBackpackTacData;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;

/**
 * 数据组件类型注册类喵~
 * <p>
 * 负责注册模组中所有自定义的数据组件类型喵~
 * 数据组件用于在物品栈或其他数据持有者上附加额外的自定义数据喵~
 * </p>
 *
 * @author liudongyu
 */
public final class MISCTWFDataComponentTypes {
	private static final DeferredRegister<DataComponentType<?>> REGISTER = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, MODID);

	/**
	 * 旅行者背包枪械数据组件类型喵~
	 * <p>
	 * 用于在旅行者背包物品上附加枪械升级状态和弹药库存信息喵~
	 * 该组件支持持久化存储和网络同步喵~
	 * </p>
	 */
	public static final DeferredHolder<DataComponentType<?>, DataComponentType<TravelersBackpackTacData>> TRAVELERS_BACKPACK_TAC_DATA = REGISTER.register(
			"travelers_backpack_tac_data", () -> DataComponentType.<TravelersBackpackTacData>builder()
					.persistent(TravelersBackpackTacData.CODEC)
					.networkSynchronized(TravelersBackpackTacData.STREAM_CODEC)
					.build()
	);

	/**
	 * 怪物蛋刷怪条目列表喵~
	 */
	public static final DeferredHolder<DataComponentType<?>, DataComponentType<MonsterEggEntries>> MONSTER_EGG_ENTRIES = REGISTER.register(
			"monster_egg_entries", () -> DataComponentType.<MonsterEggEntries>builder()
					.persistent(MonsterEggEntries.CODEC)
					.networkSynchronized(MonsterEggEntries.STREAM_CODEC)
					.build()
	);

	/**
	 * 突变药水标记位喵~
	 */
	public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> MUTANT_POTION_FLAG = REGISTER.register(
			"mutant_potion_flag", () -> DataComponentType.<Integer>builder()
					.persistent(Codec.INT)
					.networkSynchronized(ByteBufCodecs.INT)
					.build()
	);

	private MISCTWFDataComponentTypes() {
	}

	/**
	 * 初始化数据组件类型注册喵~
	 * <p>
	 * 将注册器绑定到模组事件总线上，以便在游戏启动时完成注册喵~
	 * </p>
	 *
	 * @param bus 模组事件总线喵~
	 */
	public static void init(IEventBus bus) {
		REGISTER.register(bus);
	}
}
