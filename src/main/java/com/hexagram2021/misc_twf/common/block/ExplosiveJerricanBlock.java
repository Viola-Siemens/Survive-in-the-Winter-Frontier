package com.hexagram2021.misc_twf.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

@SuppressWarnings("deprecation")
public class ExplosiveJerricanBlock extends JerricanBlock {
	public ExplosiveJerricanBlock(Properties props) {
		super(props);
	}

	@Override
	public void onCaughtFire(BlockState blockState, Level level, BlockPos blockPos, @Nullable Direction face, @Nullable LivingEntity sourceMob) {
		if (!level.isClientSide) {
			Vec3 position = Vec3.atCenterOf(blockPos);
			level.explode(sourceMob, position.x, position.y, position.z, 5.0F, Explosion.BlockInteraction.BREAK);
		}
	}

	@Override
	public void wasExploded(Level level, BlockPos blockPos, Explosion explosion) {
		if (!level.isClientSide) {
			this.onCaughtFire(level.getBlockState(blockPos), level, blockPos, null, explosion.getSourceMob());
		}
	}

	@Override
	public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand hand, BlockHitResult result) {
		ItemStack itemStack = player.getItemInHand(hand);
		if(itemStack.is(Items.FLINT_AND_STEEL) || itemStack.is(Items.FIRE_CHARGE)) {
			this.onCaughtFire(blockState, level, blockPos, result.getDirection(), player);
			level.setBlock(blockPos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL_IMMEDIATE);
			if (!player.isCreative()) {
				if (itemStack.is(Items.FLINT_AND_STEEL)) {
					itemStack.hurtAndBreak(1, player, player1 -> player1.broadcastBreakEvent(hand));
				} else {
					itemStack.shrink(1);
				}
			}
			player.awardStat(Stats.ITEM_USED.get(itemStack.getItem()));
			return InteractionResult.sidedSuccess(level.isClientSide);
		}
		return super.use(blockState, level, blockPos, player, hand, result);
	}

	@Override
	public void onProjectileHit(Level level, BlockState blockState, BlockHitResult result, Projectile projectile) {
		if (!level.isClientSide) {
			BlockPos blockpos = result.getBlockPos();
			Entity entity = projectile.getOwner();
			if (projectile.isOnFire() && projectile.mayInteract(level, blockpos)) {
				this.onCaughtFire(blockState, level, blockpos, null, entity instanceof LivingEntity living ? living : null);
				level.removeBlock(blockpos, false);
			}
		}
	}

	@Override
	public boolean dropFromExplosion(Explosion explosion) {
		return false;
	}
}
