package com.hexagram2021.misc_twf.common.loot;

import com.hexagram2021.misc_twf.common.util.IAmmoBackpack;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tiviacz.travelersbackpack.blockentity.BackpackBlockEntity;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

/**
 * 旅行背包TAC操作修改器，用于在旅行背包被破坏时保存弹药槽数据喵~
 * 当旅行背包方块实体被破坏时，将弹药槽的内容序列化到掉落的旅行背包物品的NBT中喵~
 *
 * @author liudongyu
 */
public class TravelersBackpackTacOpsModifier extends OrConditionLootModifier {
	/**
	 * 用于序列化和反序列化该修改器的编解码器喵~
	 */
	public static final MapCodec<TravelersBackpackTacOpsModifier> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
			LOOT_CONDITIONS_CODEC.fieldOf("conditions").forGetter(modifier -> modifier.conditions)
	).apply(instance, TravelersBackpackTacOpsModifier::new));

	/**
	 * 构造一个旅行背包TAC操作修改器喵~
	 *
	 * @param conditionsIn 触发修改器的战利品条件数组喵~
	 */
	public TravelersBackpackTacOpsModifier(LootItemCondition[] conditionsIn) {
		super(conditionsIn);
	}

	/**
	 * 执行战利品修改逻辑喵~
	 * 如果被破坏的方块实体是旅行背包��支持弹药存储，则将弹药槽数据保存到掉落物品的NBT中喵~
	 *
	 * @param generatedLoot 生成的战利品列表喵~
	 * @param context 战利品上下文，包含被破坏的方块实体信息喵~
	 * @return 修改后的战利品列表喵~
	 */
	@Override
	protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
		// 检查战利品上下文中是否包含方块实体参数喵~
		if(context.hasParam(LootContextParams.BLOCK_ENTITY)) {
			BlockEntity blockEntity = context.getParamOrNull(LootContextParams.BLOCK_ENTITY);
			// 如果方块实体是旅行背包方块实体喵~
			if(blockEntity instanceof BackpackBlockEntity) {
				IAmmoBackpack ammoBackpack = (IAmmoBackpack)blockEntity;
				// 如果该背包支持弹药存储喵~
				if(ammoBackpack.canStoreAmmo()) {
					// 遍历所有掉落物品喵~
					for(ItemStack itemStack: generatedLoot) {
						// 如果掉落物品是旅行背包物品喵~
						if(itemStack.getItem() instanceof TravelersBackpackItem) {
							CompoundTag tag = itemStack.getOrCreateTag();
							// 标记该背包已升级为TAC版本喵~
							tag.putBoolean("UpgradeToTac", true);
							// 将弹药槽数据序列化到NBT中喵~
							tag.put("AmmoInventory", ammoBackpack.getAmmoHandler().serializeNBT());
							itemStack.setTag(tag);
						}
					}
				}
			}
		}
		return generatedLoot;
	}

	/**
	 * 返回该修改器的编解码器喵~
	 *
	 * @return 编解码器实例喵~
	 */
	@Override
	public MapCodec<TravelersBackpackTacOpsModifier> codec() {
		return CODEC;
	}
}
