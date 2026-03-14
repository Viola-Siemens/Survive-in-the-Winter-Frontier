package com.hexagram2021.misc_twf.common.block;

import com.hexagram2021.misc_twf.common.ModVanillaCompat;
import com.hexagram2021.misc_twf.common.block.entity.MutantPotionCauldronBlockEntity;
import com.hexagram2021.misc_twf.common.register.MISCTWFBlockEntities;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 突变药水炼药锅方块喵~
 * 这是一个特殊的炼药锅，具有方块实体，可以容纳突变药水流体喵~
 * 玩家可以向其中投掷特定物品（第二脑核心、金苹果、糖）来合成深渊病毒疫苗喵~
 *
 * @author liudongyu
 */
public class MutantPotionCauldronBlock extends AbstractCauldronBlock implements EntityBlock {
	public static final MapCodec<MutantPotionCauldronBlock> CODEC = simpleCodec(MutantPotionCauldronBlock::new);

	/**
	 * 构造函数，创建一个突变药水炼药锅方块喵~
	 *
	 * @param properties 方块属性喵~
	 */
	public MutantPotionCauldronBlock(Properties properties) {
		super(properties, ModVanillaCompat.MUTANT_POTION);
	}

	/**
	 * 获取炼药锅内流体的高度喵~
	 *
	 * @param blockState 方块状态喵~
	 * @return 流体高度（0.9375D 表示满炼药锅）喵~
	 */
	@Override
	protected double getContentHeight(BlockState blockState) {
		return 0.9375D;
	}

	/**
	 * 检查炼药锅是否已满喵~
	 *
	 * @param blockState 方块状态喵~
	 * @return 总是返回 true，表示该炼药锅始终处于满状态喵~
	 */
	@Override
	public boolean isFull(BlockState blockState) {
		return true;
	}

	/**
	 * 当实体进入炼药锅内部时触发喵~
	 * 如果是生物实体则给予中毒效果喵~
	 * 如果是物品实体且投掷者拥有"vaccination"游戏阶段，则将物品添加到合成配方中喵~
	 *
	 * @param blockState 方块状态喵~
	 * @param level 世界喵~
	 * @param blockPos 方块位置喵~
	 * @param entity 进入的实体喵~
	 */
	@Override
	public void entityInside(BlockState blockState, Level level, BlockPos blockPos, Entity entity) {
		if (this.isEntityInsideContent(blockState, blockPos, entity)) {
			// 如果是生物，给予中毒效果喵~
			if(entity instanceof LivingEntity livingEntity) {
				livingEntity.addEffect(new MobEffectInstance(MobEffects.POISON, 80));
				return;
			}
			// 如果是物品实体，尝试将其添加到合成配方中喵~
			if(entity instanceof ItemEntity itemEntity && level instanceof ServerLevel serverLevel) {
				Entity thrower = itemEntity.getOwner();
				// 检查投掷者是否拥有合成权限喵~
				if(!(thrower instanceof ServerPlayer serverPlayer) || !hasStageToCovert(serverPlayer)) {
					return;
				}
				serverLevel.getBlockEntity(blockPos, MISCTWFBlockEntities.MUTANT_POTION_CAULDRON.get()).ifPresent(mutantPotionCauldronBlockEntity -> {
					ItemStack itemStack = itemEntity.getItem();
					int flag = MutantPotionCauldronBlockEntity.getFlag(itemEntity.getItem());
					// 如果该材料尚未添加过
					// 则消耗物品并添加标记喵~
					if(!mutantPotionCauldronBlockEntity.containsFlag(flag)) {
						itemStack.shrink(1);
						if (itemStack.isEmpty()) {
							itemEntity.discard();
						} else {
							itemEntity.setItem(itemStack);
						}
						mutantPotionCauldronBlockEntity.appendFlag(flag);
					}
				});
			}
		}
	}

	@Override
	public boolean triggerEvent(BlockState blockState, Level level, BlockPos blockPos, int eventID, int eventParam) {
		super.triggerEvent(blockState, level, blockPos, eventID, eventParam);
		BlockEntity blockentity = level.getBlockEntity(blockPos);
		return blockentity != null && blockentity.triggerEvent(eventID, eventParam);
	}

	/**
	 * 创建该方块的方块实体喵~
	 *
	 * @param blockPos 方块位置喵~
	 * @param blockState 方块状态喵~
	 * @return 突变药水炼药锅方块实体喵~
	 */
	@Override
	public MutantPotionCauldronBlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
		return new MutantPotionCauldronBlockEntity(blockPos, blockState);
	}

	/**
	 * 检查玩家是否拥有"vaccination"游戏阶段，用于判断是否可以进行合成喵~
	 *
	 * @param player 玩家喵~
	 * @return 如果玩家拥有该游戏阶段则返回 true 喵~
	 */
	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	public static boolean hasStageToCovert(Player player) {
		return true;
	}

	@Override
	protected MapCodec<MutantPotionCauldronBlock> codec() {
		return CODEC;
	}
}
