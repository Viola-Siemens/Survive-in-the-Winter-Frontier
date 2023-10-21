package com.hexagram2021.misc_twf.common;

import com.hexagram2021.misc_twf.common.block.entity.MutantPotionCauldronBlockEntity;
import com.hexagram2021.misc_twf.common.register.MISCTWFBlocks;
import com.hexagram2021.misc_twf.common.register.MISCTWFItems;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.gameevent.GameEvent;

import java.util.Map;

public class ModVanillaCompat {
	public static final Map<Item, CauldronInteraction> MUTANT_POTION = CauldronInteraction.newInteractionMap();
	public static final Map<Item, CauldronInteraction> ABYSS_VIRUS_VACCINE = CauldronInteraction.newInteractionMap();

	private static final String TAG_MUTANT_POTION_FLAG = "MutantPotionFlag";

	public static void init() {
		CauldronInteraction.EMPTY.put(MISCTWFItems.Materials.MUTANT_POTION_BUCKET.get(), (blockState, level, blockPos, player, hand, itemStack) -> {
			if(!level.isClientSide) {
				CompoundTag nbt = itemStack.getTag();
				player.awardStat(Stats.FILL_CAULDRON);
				player.awardStat(Stats.ITEM_USED.get(itemStack.getItem()));
				level.setBlockAndUpdate(blockPos, MISCTWFBlocks.MUTANT_POTION_CAULDRON.defaultBlockState());
				level.playSound(null, blockPos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
				if (nbt != null && nbt.contains(TAG_MUTANT_POTION_FLAG, Tag.TAG_INT)) {
					if (level.getBlockEntity(blockPos) instanceof MutantPotionCauldronBlockEntity mutantPotionCauldronBlockEntity) {
						mutantPotionCauldronBlockEntity.setFlag(nbt.getInt(TAG_MUTANT_POTION_FLAG));
					}
				}
				level.gameEvent(null, GameEvent.FLUID_PLACE, blockPos);
				player.setItemInHand(hand, ItemUtils.createFilledResult(itemStack, player, new ItemStack(Items.BUCKET)));
			}
			return InteractionResult.sidedSuccess(level.isClientSide);
		});
		CauldronInteraction.EMPTY.put(MISCTWFItems.Materials.ABYSS_VIRUS_VACCINE_BUCKET.get(), (blockState, level, blockPos, player, hand, itemStack) -> {
			if(!level.isClientSide) {
				player.setItemInHand(hand, ItemUtils.createFilledResult(itemStack, player, new ItemStack(Items.BUCKET)));
				player.awardStat(Stats.FILL_CAULDRON);
				player.awardStat(Stats.ITEM_USED.get(itemStack.getItem()));
				level.setBlockAndUpdate(blockPos, MISCTWFBlocks.ABYSS_VIRUS_VACCINE_CAULDRON.defaultBlockState());
				level.playSound(null, blockPos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
				level.gameEvent(null, GameEvent.FLUID_PLACE, blockPos);
			}
			return InteractionResult.sidedSuccess(level.isClientSide);
		});
		CauldronInteraction.WATER.put(MISCTWFItems.Materials.MUTANT_POTION.get(), (blockState, level, blockPos, player, hand, itemStack) -> {
			if(blockState.getValue(LayeredCauldronBlock.LEVEL) != 3) {
				return InteractionResult.PASS;
			}
			if(!level.isClientSide) {
				player.setItemInHand(hand, ItemUtils.createFilledResult(itemStack, player, new ItemStack(Items.GLASS_BOTTLE)));
				player.awardStat(Stats.USE_CAULDRON);
				player.awardStat(Stats.ITEM_USED.get(itemStack.getItem()));
				level.setBlockAndUpdate(blockPos, MISCTWFBlocks.MUTANT_POTION_CAULDRON.defaultBlockState());
				level.playSound(null, blockPos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
				level.gameEvent(null, GameEvent.FLUID_PLACE, blockPos);
			}
			return InteractionResult.sidedSuccess(level.isClientSide);
		});

		MUTANT_POTION.put(MISCTWFItems.Materials.GLASS_ROD.get(), (blockState, level, blockPos, player, hand, itemStack) -> {
			BlockEntity blockEntity = level.getBlockEntity(blockPos);
			if(blockEntity instanceof MutantPotionCauldronBlockEntity mutantPotionCauldronBlockEntity && mutantPotionCauldronBlockEntity.isComplete()) {
				if (level.isClientSide) {
					for(int i = 0; i < 10; ++i) {
						double x = blockPos.getX() + level.getRandom().nextDouble();
						double y = blockPos.getY() + level.getRandom().nextDouble() + 0.5D;
						double z = blockPos.getZ() + level.getRandom().nextDouble();
						level.addParticle(ParticleTypes.POOF, x, y, z, 0.0D, 0.004D, 0.0D);
					}
					for(int i = 0; i < 2; ++i) {
						double x = blockPos.getX() + level.getRandom().nextDouble();
						double y = blockPos.getY() + level.getRandom().nextDouble() + 0.5D;
						double z = blockPos.getZ() + level.getRandom().nextDouble();
						level.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, x, y, z, 0.0D, 0.008D, 0.0D);
					}
					return InteractionResult.SUCCESS;
				}
				player.awardStat(Stats.USE_CAULDRON);
				player.awardStat(Stats.ITEM_USED.get(itemStack.getItem()));
				level.setBlockAndUpdate(blockPos, MISCTWFBlocks.ABYSS_VIRUS_VACCINE_CAULDRON.defaultBlockState());
				level.playSound(null, blockPos, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 0.8F, 0.75F);
				level.gameEvent(null, GameEvent.FLUID_PLACE, blockPos);
				return InteractionResult.CONSUME;
			}
			return InteractionResult.PASS;
		});
		MUTANT_POTION.put(Items.BUCKET, (blockState, level, blockPos, player, hand, itemStack) -> {
			if(!level.isClientSide) {
				player.setItemInHand(hand, ItemUtils.createFilledResult(itemStack, player, new ItemStack(MISCTWFItems.Materials.MUTANT_POTION_BUCKET)));
				player.awardStat(Stats.USE_CAULDRON);
				player.awardStat(Stats.ITEM_USED.get(itemStack.getItem()));
				if (level.getBlockEntity(blockPos) instanceof MutantPotionCauldronBlockEntity mutantPotionCauldronBlockEntity) {
					ItemStack itemInHand = player.getItemInHand(hand);
					CompoundTag nbt = itemInHand.getOrCreateTag();
					nbt.putInt(TAG_MUTANT_POTION_FLAG, mutantPotionCauldronBlockEntity.getFlag());
					itemInHand.setTag(nbt);
				}
				level.setBlockAndUpdate(blockPos, Blocks.CAULDRON.defaultBlockState());
				level.playSound(null, blockPos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
				level.gameEvent(null, GameEvent.FLUID_PICKUP, blockPos);
			}
			return InteractionResult.sidedSuccess(level.isClientSide);
		});
		ABYSS_VIRUS_VACCINE.put(MISCTWFItems.Materials.SYRINGE.get(), (blockState, level, blockPos, player, hand, itemStack) -> {
			if(!level.isClientSide) {
				player.setItemInHand(hand, ItemUtils.createFilledResult(itemStack, player, new ItemStack(MISCTWFItems.ABYSS_VIRUS_VACCINE)));
				player.awardStat(Stats.USE_CAULDRON);
				player.awardStat(Stats.ITEM_USED.get(itemStack.getItem()));
				level.setBlockAndUpdate(blockPos, Blocks.CAULDRON.defaultBlockState());
				level.playSound(null, blockPos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
				level.gameEvent(null, GameEvent.FLUID_PICKUP, blockPos);
			}
			return InteractionResult.sidedSuccess(level.isClientSide);
		});
		ABYSS_VIRUS_VACCINE.put(Items.BUCKET, (blockState, level, blockPos, player, hand, itemStack) -> {
			if(!level.isClientSide) {
				player.setItemInHand(hand, ItemUtils.createFilledResult(itemStack, player, new ItemStack(MISCTWFItems.Materials.ABYSS_VIRUS_VACCINE_BUCKET)));
				player.awardStat(Stats.USE_CAULDRON);
				player.awardStat(Stats.ITEM_USED.get(itemStack.getItem()));
				level.setBlockAndUpdate(blockPos, Blocks.CAULDRON.defaultBlockState());
				level.playSound(null, blockPos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
				level.gameEvent(null, GameEvent.FLUID_PICKUP, blockPos);
			}
			return InteractionResult.sidedSuccess(level.isClientSide);
		});
	}
}
