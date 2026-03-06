package com.hexagram2021.misc_twf.common;

import com.hexagram2021.misc_twf.common.block.entity.MutantPotionCauldronBlockEntity;
import com.hexagram2021.misc_twf.common.register.MISCTWFBlocks;
import com.hexagram2021.misc_twf.common.register.MISCTWFDataComponentTypes;
import com.hexagram2021.misc_twf.common.register.MISCTWFFluids;
import com.hexagram2021.misc_twf.common.register.MISCTWFItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.gameevent.GameEvent;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;

/**
 * 原版 Minecraft 兼容性工具类喵~
 * 用于注册与原版系统的集成，包括炼药锅交互和发射器行为喵~
 *
 * @author liudongyu
 */
public final class ModVanillaCompat {
	/** 突变药水炼药锅的交互映射喵~ */
	public static final CauldronInteraction.InteractionMap MUTANT_POTION =
			CauldronInteraction.newInteractionMap(ResourceLocation.fromNamespaceAndPath(MODID, "mutant_potion").toString());
	/** 深渊病毒疫苗炼药锅的交互映射喵~ */
	public static final CauldronInteraction.InteractionMap ABYSS_VIRUS_VACCINE =
			CauldronInteraction.newInteractionMap(ResourceLocation.fromNamespaceAndPath(MODID, "abyss_virus_vaccine").toString());

	/**
	 * 初始化与原版的兼容性内容，包括炼药锅交互和发射器行为喵~
	 * 该方法在模组初始化时被调用喵~
	 */
	public static void init() {
		// ======== 炼药锅交互注册 ======== 喵~
		// 使用突变药水桶填充空炼药锅喵~
		CauldronInteraction.EMPTY.map().put(MISCTWFItems.Materials.MUTANT_POTION_BUCKET.get(), (blockState, level, blockPos, player, hand, itemStack) -> {
			if(!level.isClientSide) {
				// 获取桶中保存的材料标记喵~
				Integer flag = itemStack.get(MISCTWFDataComponentTypes.MUTANT_POTION_FLAG);
				player.awardStat(Stats.FILL_CAULDRON);
				player.awardStat(Stats.ITEM_USED.get(itemStack.getItem()));
				level.setBlockAndUpdate(blockPos, MISCTWFBlocks.MUTANT_POTION_CAULDRON.defaultBlockState());
				level.playSound(null, blockPos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
				// 如果桶中有材料标记，则将其应用到方块实体中喵~
				if (flag != null && level.getBlockEntity(blockPos) instanceof MutantPotionCauldronBlockEntity mutantPotionCauldronBlockEntity) {
					mutantPotionCauldronBlockEntity.setFlag(flag);
				}

				level.gameEvent(null, GameEvent.FLUID_PLACE, blockPos);
				player.setItemInHand(hand, ItemUtils.createFilledResult(itemStack, player, new ItemStack(Items.BUCKET)));
			}
			return ItemInteractionResult.sidedSuccess(level.isClientSide);
		});
		// 使用深渊病毒疫苗桶填充空炼药锅喵~
		CauldronInteraction.EMPTY.map().put(MISCTWFItems.Materials.ABYSS_VIRUS_VACCINE_BUCKET.get(), (blockState, level, blockPos, player, hand, itemStack) -> {
			if(!level.isClientSide) {
				player.setItemInHand(hand, ItemUtils.createFilledResult(itemStack, player, new ItemStack(Items.BUCKET)));
				player.awardStat(Stats.FILL_CAULDRON);
				player.awardStat(Stats.ITEM_USED.get(itemStack.getItem()));
				level.setBlockAndUpdate(blockPos, MISCTWFBlocks.ABYSS_VIRUS_VACCINE_CAULDRON.defaultBlockState());
				level.playSound(null, blockPos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
				level.gameEvent(null, GameEvent.FLUID_PLACE, blockPos);
			}
			return ItemInteractionResult.sidedSuccess(level.isClientSide);
		});
		// 向满水炼药锅中倒入突变药水，转换为突变药水炼药锅喵~
		CauldronInteraction.WATER.map().put(MISCTWFItems.Materials.MUTANT_POTION.get(), (blockState, level, blockPos, player, hand, itemStack) -> {
			if(blockState.getValue(LayeredCauldronBlock.LEVEL) != 3) {
				// 必须是满水炼药锅（3 层）才能转换喵~
				return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
			}
			if(!level.isClientSide) {
				player.setItemInHand(hand, ItemUtils.createFilledResult(itemStack, player, new ItemStack(Items.GLASS_BOTTLE)));
				player.awardStat(Stats.USE_CAULDRON);
				player.awardStat(Stats.ITEM_USED.get(itemStack.getItem()));
				level.setBlockAndUpdate(blockPos, MISCTWFBlocks.MUTANT_POTION_CAULDRON.defaultBlockState());
				level.playSound(null, blockPos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
				level.gameEvent(null, GameEvent.FLUID_PLACE, blockPos);
			}
			return ItemInteractionResult.sidedSuccess(level.isClientSide);
		});
		// 突变药水炼药锅的交互喵~
		// 使用玻璃棒搅拌完成的突变药水炼药锅，转换为深渊病毒疫苗炼药锅喵~
		MUTANT_POTION.map().put(MISCTWFItems.Materials.GLASS_ROD.get(), (blockState, level, blockPos, player, hand, itemStack) -> {
			BlockEntity blockEntity = level.getBlockEntity(blockPos);
			// 只有所有材料都已添加完成才能搅拌喵~
			if(blockEntity instanceof MutantPotionCauldronBlockEntity mutantPotionCauldronBlockEntity && mutantPotionCauldronBlockEntity.isComplete()) {
				if (level.isClientSide) {
					// 客户端生成粒子效果喵~
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
					return ItemInteractionResult.SUCCESS;
				}
				player.awardStat(Stats.USE_CAULDRON);
				player.awardStat(Stats.ITEM_USED.get(itemStack.getItem()));
				level.setBlockAndUpdate(blockPos, MISCTWFBlocks.ABYSS_VIRUS_VACCINE_CAULDRON.defaultBlockState());
				level.playSound(null, blockPos, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 0.8F, 0.75F);
				level.gameEvent(null, GameEvent.FLUID_PLACE, blockPos);
				return ItemInteractionResult.CONSUME;
			}
			return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
		});
		// 使用空桶收集突变药水炼药锅的内容物喵~
		MUTANT_POTION.map().put(Items.BUCKET, (blockState, level, blockPos, player, hand, itemStack) -> {
			if(!level.isClientSide) {
				player.setItemInHand(hand, ItemUtils.createFilledResult(itemStack, player, new ItemStack(MISCTWFItems.Materials.MUTANT_POTION_BUCKET)));
				player.awardStat(Stats.USE_CAULDRON);
				player.awardStat(Stats.ITEM_USED.get(itemStack.getItem()));
				// 将当前的材料标记保存到桶的数据组件中喵~
				if (level.getBlockEntity(blockPos) instanceof MutantPotionCauldronBlockEntity mutantPotionCauldronBlockEntity) {
					ItemStack itemInHand = player.getItemInHand(hand);
					itemInHand.set(MISCTWFDataComponentTypes.MUTANT_POTION_FLAG, mutantPotionCauldronBlockEntity.getFlag());
				}
				level.setBlockAndUpdate(blockPos, Blocks.CAULDRON.defaultBlockState());
				level.playSound(null, blockPos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
				level.gameEvent(null, GameEvent.FLUID_PICKUP, blockPos);
			}
			return ItemInteractionResult.sidedSuccess(level.isClientSide);
		});
		// 深渊病毒疫苗炼药锅的交互喵~
		// 使用注射器提取深渊病毒疫苗喵~
		ABYSS_VIRUS_VACCINE.map().put(MISCTWFItems.Materials.SYRINGE.get(), (blockState, level, blockPos, player, hand, itemStack) -> {
			if(!level.isClientSide) {
				player.setItemInHand(hand, ItemUtils.createFilledResult(itemStack, player, new ItemStack(MISCTWFItems.ABYSS_VIRUS_VACCINE)));
				player.awardStat(Stats.USE_CAULDRON);
				player.awardStat(Stats.ITEM_USED.get(itemStack.getItem()));
				level.setBlockAndUpdate(blockPos, Blocks.CAULDRON.defaultBlockState());
				level.playSound(null, blockPos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
				level.gameEvent(null, GameEvent.FLUID_PICKUP, blockPos);
			}
			return ItemInteractionResult.sidedSuccess(level.isClientSide);
		});
		// 使用空桶收集深渊病毒疫苗炼药锅的内容物喵~
		ABYSS_VIRUS_VACCINE.map().put(Items.BUCKET, (blockState, level, blockPos, player, hand, itemStack) -> {
			if(!level.isClientSide) {
				player.setItemInHand(hand, ItemUtils.createFilledResult(itemStack, player, new ItemStack(MISCTWFItems.Materials.ABYSS_VIRUS_VACCINE_BUCKET)));
				player.awardStat(Stats.USE_CAULDRON);
				player.awardStat(Stats.ITEM_USED.get(itemStack.getItem()));
				level.setBlockAndUpdate(blockPos, Blocks.CAULDRON.defaultBlockState());
				level.playSound(null, blockPos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
				level.gameEvent(null, GameEvent.FLUID_PICKUP, blockPos);
			}
			return ItemInteractionResult.sidedSuccess(level.isClientSide);
		});

		// ======== 发射器行为注册 ======== 喵~
		// 动物粪便使用骨粉的发射器行为喵~
		DispenserBlock.registerBehavior(MISCTWFItems.Materials.ANIMAL_POOP.get(), DispenserBlock.DISPENSER_REGISTRY.getOrDefault(Items.BONE_MEAL, DispenseItemBehavior.NOOP));
		// 血液桶使用自定义的发射器行为喵~
		DispenserBlock.registerBehavior(MISCTWFFluids.BLOOD_FLUID.getBucket(), BUCKET_DISPENSE_BEHAVIOR);
	}

	/**
	 * 桶的发射器行为，用于从发射器中倒出流体喵~
	 */
	private static final DispenseItemBehavior BUCKET_DISPENSE_BEHAVIOR = new DefaultDispenseItemBehavior() {
		/** 默认的发射器行为，用于处理失败情况喵~ */
		private final DefaultDispenseItemBehavior defaultBehavior = new DefaultDispenseItemBehavior();

		/**
		 * 执行桶的发射器行为喵~
		 *
		 * @param blockSource 方块源喵~
		 * @param itemStack 物品堆（桶）喵~
		 * @return 执行后的物品堆喵~
		 */
		@Override
		@SuppressWarnings("deprecation")
		public ItemStack execute(BlockSource blockSource, ItemStack itemStack) {
			BucketItem bucketitem = (BucketItem)itemStack.getItem();
			// 计算发射器前方的方块位置喵~
			BlockPos blockpos = blockSource.pos().relative(blockSource.state().getValue(DispenserBlock.FACING));
			Level level = blockSource.level();
			// 尝试倒出流体，成功则返回空桶喵~
			if(bucketitem.emptyContents(null, level, blockpos, null)) {
				bucketitem.checkExtraContent(null, level, itemStack, blockpos);
				return new ItemStack(Items.BUCKET);
			}
			// 如果无法倒出流体，则使用默认行为（弹出物品）喵~
			return this.defaultBehavior.dispense(blockSource, itemStack);
		}
	};

	private ModVanillaCompat() {
	}
}
