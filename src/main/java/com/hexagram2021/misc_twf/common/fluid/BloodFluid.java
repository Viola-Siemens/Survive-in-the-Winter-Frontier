package com.hexagram2021.misc_twf.common.fluid;

import com.hexagram2021.misc_twf.common.register.MISCTWFFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
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
import net.minecraftforge.fluids.FluidAttributes;

import java.util.Optional;
import java.util.Random;

@SuppressWarnings("deprecation")
public abstract class BloodFluid extends FlowingFluid {
	private final MISCTWFFluids.FluidEntry<BloodFluid> fluidEntry;
	private final TagKey<Fluid> fluidTag;
	protected final ResourceLocation stillTex;
	protected final ResourceLocation flowingTex;

	public BloodFluid(MISCTWFFluids.FluidEntry<BloodFluid> fluidEntry, TagKey<Fluid> fluidTag, ResourceLocation stillTex, ResourceLocation flowingTex) {
		this.fluidEntry = fluidEntry;
		this.fluidTag = fluidTag;
		this.stillTex = stillTex;
		this.flowingTex = flowingTex;
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
	public void animateTick(Level level, BlockPos blockPos, FluidState fluidState, Random random) {
		if (!fluidState.isSource() && !fluidState.getValue(FALLING)) {
			if (random.nextInt(64) == 0) {
				level.playLocalSound((double)blockPos.getX() + 0.5D, (double)blockPos.getY() + 0.5D, (double)blockPos.getZ() + 0.5D, SoundEvents.WATER_AMBIENT, SoundSource.BLOCKS, random.nextFloat() * 0.25F + 0.75F, random.nextFloat() + 0.5F, false);
			}
		} else if (random.nextInt(10) == 0) {
			level.addParticle(ParticleTypes.UNDERWATER, (double)blockPos.getX() + random.nextDouble(), (double)blockPos.getY() + random.nextDouble(), (double)blockPos.getZ() + random.nextDouble(), 0.0D, 0.0D, 0.0D);
		}
	}

	@Override
	protected boolean canConvertToSource() {
		return false;
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
	protected FluidAttributes createAttributes() {
		FluidAttributes.Builder builder = FluidAttributes.builder(stillTex, flowingTex);
		return builder.build(this);
	}

	public static class Flowing extends BloodFluid {
		public Flowing(MISCTWFFluids.FluidEntry<BloodFluid> fluidEntry, TagKey<Fluid> fluidTag, ResourceLocation stillTex, ResourceLocation flowingTex) {
			super(fluidEntry, fluidTag, stillTex, flowingTex);
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

	public static class Source extends BloodFluid {
		public Source(MISCTWFFluids.FluidEntry<BloodFluid> fluidEntry, TagKey<Fluid> fluidTag, ResourceLocation stillTex, ResourceLocation flowingTex) {
			super(fluidEntry, fluidTag, stillTex, flowingTex);
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
