package com.hexagram2021.misc_twf.common.fluid;

import com.hexagram2021.misc_twf.common.register.MISCTWFFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.fluids.FluidType;

import java.util.Optional;

/**
 * 血液流体，继承自 {@link FlowingFluid}，实现血液的流动和静止两种状态喵~
 *
 * @author liudongyu
 */
@SuppressWarnings("deprecation")
public abstract class BloodFluid extends FlowingFluid {
	private final MISCTWFFluids.FluidEntry<BloodFluid> fluidEntry;
	private final TagKey<Fluid> fluidTag;

	/**
	 * 构造血液流体实例喵~
	 *
	 * @param fluidEntry 流体注册条目喵~
	 * @param fluidTag   流体标签喵~
	 */
	protected BloodFluid(MISCTWFFluids.FluidEntry<BloodFluid> fluidEntry, TagKey<Fluid> fluidTag) {
		this.fluidEntry = fluidEntry;
		this.fluidTag = fluidTag;
	}

	@Override
	public Fluid getFlowing() {
		return this.fluidEntry.getFlowing();
	}

	@Override
	public Fluid getSource() {
		return this.fluidEntry.getStill();
	}

	@Override
	public Item getBucket() {
		return this.fluidEntry.getBucket();
	}

	@Override
	public boolean isSame(Fluid fluid) {
		return fluid == this.fluidEntry.getFlowing() || fluid == this.fluidEntry.getStill();
	}

	@Override
	public void animateTick(Level level, BlockPos blockPos, FluidState fluidState, RandomSource random) {
		if (!fluidState.isSource() && !fluidState.getValue(FALLING)) {
			if (random.nextInt(64) == 0) {
				level.playLocalSound(blockPos.getX() + 0.5D, blockPos.getY() + 0.5D, blockPos.getZ() + 0.5D, SoundEvents.WATER_AMBIENT, SoundSource.BLOCKS, random.nextFloat() * 0.25F + 0.75F, random.nextFloat() + 0.5F, false);
			}
		} else if (random.nextInt(10) == 0) {
			level.addParticle(ParticleTypes.UNDERWATER, blockPos.getX() + random.nextDouble(), blockPos.getY() + random.nextDouble(), blockPos.getZ() + random.nextDouble(), 0.0D, 0.0D, 0.0D);
		}
	}

	@Override
	protected void beforeDestroyingBlock(LevelAccessor level, BlockPos blockPos, BlockState blockState) {
		BlockEntity blockentity = blockState.hasBlockEntity() ? level.getBlockEntity(blockPos) : null;
		Block.dropResources(blockState, level, blockPos, blockentity);
	}

	@Override
	protected int getSlopeFindDistance(LevelReader level) {
		return 4;
	}

	@Override
	protected int getDropOff(LevelReader level) {
		return 2;
	}

	@Override
	protected boolean canBeReplacedWith(FluidState fluidState, BlockGetter level, BlockPos blockPos, Fluid fluid, Direction direction) {
		return direction == Direction.DOWN && !fluid.is(this.fluidTag);
	}

	@Override
	public int getTickDelay(LevelReader level) {
		return 10;
	}

	@Override
	protected float getExplosionResistance() {
		return 5.0F;
	}

	@Override
	protected BlockState createLegacyBlock(FluidState fluidState) {
		return this.fluidEntry.getBlock().defaultBlockState().setValue(LiquidBlock.LEVEL, getLegacyLevel(fluidState));
	}

	@Override
	public Optional<SoundEvent> getPickupSound() {
		return Optional.of(SoundEvents.BUCKET_FILL);
	}

	@Override
	protected boolean canConvertToSource(Level level) {
		return false;
	}

	@Override
	public FluidType getFluidType() {
		return MISCTWFFluids.BLOOD_FLUID.type().get();
	}

	/**
	 * 血液流体的流动状态喵~
	 */
	public static class Flowing extends BloodFluid {
		/**
		 * 构造流动状态的血液流体实例喵~
		 *
		 * @param fluidEntry 流体注册条目喵~
		 * @param fluidTag   流体标签喵~
		 */
		public Flowing(MISCTWFFluids.FluidEntry<BloodFluid> fluidEntry, TagKey<Fluid> fluidTag) {
			super(fluidEntry, fluidTag);
		}

		@Override
		protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
			super.createFluidStateDefinition(builder);
			builder.add(LEVEL);
		}

		@Override
		public int getAmount(FluidState fluidState) {
			return fluidState.getValue(LEVEL);
		}

		@Override
		public boolean isSource(FluidState fluidState) {
			return false;
		}
	}

	/**
	 * 血液流体的源方块状态喵~
	 */
	public static class Source extends BloodFluid {
		/**
		 * 构造源方块状态的血液流体实例喵~
		 *
		 * @param fluidEntry 流体注册条目喵~
		 * @param fluidTag   流体标签喵~
		 */
		public Source(MISCTWFFluids.FluidEntry<BloodFluid> fluidEntry, TagKey<Fluid> fluidTag) {
			super(fluidEntry, fluidTag);
		}

		@Override
		public int getAmount(FluidState fluidState) {
			return 8;
		}

		@Override
		public boolean isSource(FluidState fluidState) {
			return true;
		}
	}
}
