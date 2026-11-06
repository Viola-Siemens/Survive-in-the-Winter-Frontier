package com.hexagram2021.misc_twf_adventure.common.loot;

import com.hexagram2021.misc_twf_adventure.common.data_component.TravelersBackpackTacData;
import com.hexagram2021.misc_twf_adventure.common.menu.BackpackAmmoHandler;
import com.hexagram2021.misc_twf_adventure.common.register.MISCTWFDataComponentTypes;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tiviacz.travelersbackpack.blockentity.BackpackBlockEntity;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

/**
 * 旅行背包 TAC 操作修改器喵~
 *
 * <p>当战术背包方块实体被破坏时，把弹药槽内容（数据组件中的弹药列表）复制到掉落的背包物品上喵~</p>
 *
 * @author liudongyu
 */
public class TravelersBackpackTacOpsModifier extends OrConditionLootModifier {
	/**
	 * 用于序列化与反序列化该修改器的编解码器喵~
	 */
	public static final MapCodec<TravelersBackpackTacOpsModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			LOOT_CONDITIONS_CODEC.fieldOf("conditions").forGetter(modifier -> modifier.conditions)
	).apply(instance, TravelersBackpackTacOpsModifier::new));

	/**
	 * 构造一个旅行背包 TAC 操作修改器喵~
	 *
	 * @param conditionsIn 触发修改器的战利品条件数组喵~
	 */
	public TravelersBackpackTacOpsModifier(LootItemCondition[] conditionsIn) {
		super(conditionsIn);
	}

	@Override
	protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
		if(!context.hasParam(LootContextParams.BLOCK_ENTITY)) {
			return generatedLoot;
		}
		BlockEntity blockEntity = context.getParamOrNull(LootContextParams.BLOCK_ENTITY);
		if(!(blockEntity instanceof BackpackBlockEntity backpackBlockEntity)) {
			return generatedLoot;
		}
		BackpackWrapper wrapper = backpackBlockEntity.getWrapper();
		if(!BackpackAmmoHandler.canStoreAmmo(wrapper)) {
			return generatedLoot;
		}
		// 弹药槽内容以数据组件为准，直接复制到掉落物即可喵~
		TravelersBackpackTacData source = wrapper.getBackpackStack().getOrDefault(
				MISCTWFDataComponentTypes.TRAVELERS_BACKPACK_TAC_DATA, TravelersBackpackTacData.EMPTY
		);
		for(ItemStack dropped : generatedLoot) {
			if(dropped.getItem() instanceof TravelersBackpackItem) {
				dropped.set(MISCTWFDataComponentTypes.TRAVELERS_BACKPACK_TAC_DATA,
						new TravelersBackpackTacData(true, source.ammoInventory()));
			}
		}
		return generatedLoot;
	}

	@Override
	public MapCodec<TravelersBackpackTacOpsModifier> codec() {
		return CODEC;
	}
}