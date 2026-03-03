package com.hexagram2021.misc_twf.common.register;

import com.google.common.base.Suppliers;
import com.hexagram2021.misc_twf.common.loot.TravelersBackpackTacOpsModifier;
import com.mojang.serialization.MapCodec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;

/**
 * 旅行背包TAC操作注册类，管理旅行背包弹药槽相关的全局战利品修改器喵~
 * 该类注册了战利品修改器，用于在旅行背包被破坏时保存弹药槽的数据喵~
 *
 * @author liudongyu
 */
@SuppressWarnings("unused")
public final class MISCTWFTravelersBackpackTacOps {
	private static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> REGISTER = DeferredRegister.create(NeoForgeRegistries.GLOBAL_LOOT_MODIFIER_SERIALIZERS, MODID);

	/**
	 * 旅行背包TAC操作修改器编解码器，用于序列化和反序列化弹药槽数据喵~
	 * 在旅行背包被破坏时，将弹药槽的内容保存到掉落的物品NBT中喵~
	 */
	private static final DeferredHolder<MapCodec<? extends IGlobalLootModifier>, MapCodec<TravelersBackpackTacOpsModifier>> TRAVELERS_BACKPACK_TAC_OPS = REGISTER.register(
			"travelers_backpack_tac_nbt_ops", Suppliers.memoize(() -> TravelersBackpackTacOpsModifier.CODEC)
	);

	private MISCTWFTravelersBackpackTacOps() {
	}

	/**
	 * 初始化并注册所有全局战利品修改器编解码器到事件总线喵~
	 *
	 * @param bus NeoForge 事件总线喵~
	 */
	public static void init(IEventBus bus) {
		REGISTER.register(bus);
	}
}
