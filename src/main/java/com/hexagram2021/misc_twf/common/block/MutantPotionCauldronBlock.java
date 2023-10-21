package com.hexagram2021.misc_twf.common.block;

import com.hexagram2021.misc_twf.common.ModVanillaCompat;
import com.hexagram2021.misc_twf.common.block.entity.MutantPotionCauldronBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

@SuppressWarnings("deprecation")
public class MutantPotionCauldronBlock extends AbstractCauldronBlock implements EntityBlock {
	public MutantPotionCauldronBlock(Properties properties) {
		super(properties, ModVanillaCompat.MUTANT_POTION);
	}

	@Override
	protected double getContentHeight(BlockState p_153500_) {
		return 0.9375D;
	}

	@Override
	public boolean isFull(BlockState blockState) {
		return true;
	}

	@Override
	public void entityInside(BlockState blockState, Level level, BlockPos blockPos, Entity entity) {
		if (this.isEntityInsideContent(blockState, blockPos, entity)) {
			if(entity instanceof LivingEntity livingEntity) {
				livingEntity.addEffect(new MobEffectInstance(MobEffects.POISON, 80));
				return;
			}
			if(entity instanceof ItemEntity itemEntity) {
				BlockEntity blockEntity = level.getBlockEntity(blockPos);
				if(blockEntity instanceof MutantPotionCauldronBlockEntity mutantPotionCauldronBlockEntity) {
					ItemStack itemStack = itemEntity.getItem();
					int flag = MutantPotionCauldronBlockEntity.getFlag(itemEntity.getItem());
					if(!mutantPotionCauldronBlockEntity.containsFlag(flag)) {
						itemStack.shrink(1);
						if (itemStack.isEmpty()) {
							itemEntity.discard();
						} else {
							itemEntity.setItem(itemStack);
						}
						mutantPotionCauldronBlockEntity.appendFlag(flag);
					}
				}
			}
		}
	}

	@Override
	public boolean triggerEvent(BlockState blockState, Level level, BlockPos blockPos, int eventID, int eventParam) {
		super.triggerEvent(blockState, level, blockPos, eventID, eventParam);
		BlockEntity blockentity = level.getBlockEntity(blockPos);
		return blockentity != null && blockentity.triggerEvent(eventID, eventParam);
	}

	@Override
	public MutantPotionCauldronBlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
		return new MutantPotionCauldronBlockEntity(blockPos, blockState);
	}
}
