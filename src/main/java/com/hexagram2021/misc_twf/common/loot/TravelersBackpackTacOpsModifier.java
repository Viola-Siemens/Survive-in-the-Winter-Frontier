package com.hexagram2021.misc_twf.common.loot;

import com.google.gson.JsonObject;
import com.hexagram2021.misc_twf.common.util.IAmmoBackpack;
import com.tiviacz.travelersbackpack.blockentity.TravelersBackpackBlockEntity;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.List;

public class TravelersBackpackTacOpsModifier extends OrConditionLootModifier {
	public TravelersBackpackTacOpsModifier(LootItemCondition[] conditionsIn) {
		super(conditionsIn);
	}

	@Override
	protected List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
		if(context.hasParam(LootContextParams.BLOCK_ENTITY)) {
			BlockEntity blockEntity = context.getParamOrNull(LootContextParams.BLOCK_ENTITY);
			if(blockEntity instanceof TravelersBackpackBlockEntity) {
				IAmmoBackpack ammoBackpack = (IAmmoBackpack)blockEntity;
				if(ammoBackpack.canStoreAmmo()) {
					for(ItemStack itemStack: generatedLoot) {
						if(itemStack.getItem() instanceof TravelersBackpackItem) {
							CompoundTag tag = itemStack.getOrCreateTag();
							tag.putBoolean("UpgradeToTac", true);
							tag.put("AmmoInventory", ammoBackpack.getAmmoHandler().serializeNBT());
							itemStack.setTag(tag);
						}
					}
				}
			}
		}
		return generatedLoot;
	}

	public static class Serializer extends OrConditionLootModifier.Serializer<TravelersBackpackTacOpsModifier> {
		@Override
		public TravelersBackpackTacOpsModifier read(ResourceLocation location, JsonObject json, LootItemCondition[] conditions) {
			return new TravelersBackpackTacOpsModifier(conditions);
		}

		@Override
		public JsonObject write(TravelersBackpackTacOpsModifier instance) {
			return new JsonObject();
		}
	}
}
