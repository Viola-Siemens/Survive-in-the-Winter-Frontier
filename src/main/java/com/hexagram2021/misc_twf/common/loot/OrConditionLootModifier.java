package com.hexagram2021.misc_twf.common.loot;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.AnyOfCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;

import java.util.List;
import java.util.function.Predicate;

/**
 * 或条件战利品修改器抽象基类，使用或逻辑组合多个战利品条件喵~
 * 只要有任意一个条件满足，就会应用战利品修改逻辑喵~
 *
 * @author liudongyu
 */
public abstract class OrConditionLootModifier implements IGlobalLootModifier {
	/**
	 * 战利品条件数组，至少需要满足其中一个条件才会触发修改喵~
	 */
	protected final LootItemCondition[] conditions;

	/**
	 * 组合后的条件谓词，使用或逻辑连接所有条件喵~
	 */
	private final Predicate<LootContext> combinedConditions;

	/**
	 * 构造一个或条件战利品修改器喵~
	 *
	 * @param conditionsIn 需要匹配的战利品条件数组，使用或逻辑组合喵~
	 */
	protected OrConditionLootModifier(LootItemCondition[] conditionsIn) {
		this.conditions = conditionsIn;
		this.combinedConditions = new AnyOfCondition(List.of(conditionsIn));
	}

	/**
	 * 应用战利品修改逻辑喵~
	 * 如果任意一个条件满足，则调用 {@link #doApply} 方法进行修改喵~
	 *
	 * @param generatedLoot 生成的战利品列表喵~
	 * @param context 战利品上下文喵~
	 * @return 修改后的战利品列表喵~
	 */
	@Override
	public final ObjectArrayList<ItemStack> apply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
		return this.combinedConditions.test(context) ? this.doApply(generatedLoot, context) : generatedLoot;
	}

	/**
	 * 执行实际的战利品修改逻辑，由子类实现具体修改行为喵~
	 *
	 * @param generatedLoot 生成的战利品列表喵~
	 * @param context 战利品上下文喵~
	 * @return 修改后的战利品列表喵~
	 */
	protected abstract ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context);
}
