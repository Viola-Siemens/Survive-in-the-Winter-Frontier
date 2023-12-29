package com.hexagram2021.misc_twf.common.register;

import com.hexagram2021.misc_twf.SurviveInTheWinterFrontier;
import com.hexagram2021.misc_twf.common.fluid.BloodFluid;
import com.hexagram2021.misc_twf.common.fluid.FluidConstructor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.capability.wrappers.FluidBucketWrapper;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;

import javax.annotation.Nullable;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;

public final class MISCTWFFluids {
	public static final DeferredRegister<Fluid> REGISTER = DeferredRegister.create(ForgeRegistries.FLUIDS, MODID);

	public static final FluidEntry<BloodFluid> BLOOD_FLUID = FluidEntry.register(
			"blood", MISCTWFFluidTags.BLOOD, BloodFluid.Source::new, BloodFluid.Flowing::new,
			new ResourceLocation(MODID, "block/fluid/blood_still"), new ResourceLocation(MODID, "block/fluid/blood_flowing"),
			() -> BlockBehaviour.Properties.of(Material.WATER).color(MaterialColor.COLOR_RED).noCollission().strength(5.0F).speedFactor(0.5F).noDrops(),
			(entry, props) -> new LiquidBlock(entry::getStill, props)
	);

	public record FluidEntry<T extends Fluid>(RegistryObject<T> still, RegistryObject<T> flowing,
											  MISCTWFBlocks.BlockEntry<LiquidBlock> fluidBlock, MISCTWFItems.ItemEntry<BucketItem> bucket) {
		public T getFlowing() {
			return this.flowing.get();
		}

		public T getStill() {
			return this.still.get();
		}

		public LiquidBlock getBlock() {
			return this.fluidBlock.get();
		}

		public BucketItem getBucket() {
			return this.bucket.get();
		}

		public static <T extends Fluid> FluidEntry<T> register(String name, TagKey<Fluid> fluidTag,
															   FluidConstructor<T> stillMaker, FluidConstructor<T> flowingMaker,
															   ResourceLocation stillTex, ResourceLocation flowingTex,
															   Supplier<BlockBehaviour.Properties> blockProperties,
															   BiFunction<FluidEntry<T>, BlockBehaviour.Properties, ? extends LiquidBlock> blockMaker) {
			Mutable<FluidEntry<T>> thisMutable = new MutableObject<>();
			RegistryObject<T> still = REGISTER.register(name, () -> makeFluid(
					stillMaker, thisMutable.getValue(), fluidTag, stillTex, flowingTex
			));
			RegistryObject<T> flowing = REGISTER.register("flowing_" + name, () -> makeFluid(
					flowingMaker, thisMutable.getValue(), fluidTag, stillTex, flowingTex
			));
			MISCTWFBlocks.BlockEntry<LiquidBlock> block = new MISCTWFBlocks.BlockEntry<>(
					name,
					blockProperties,
					p -> blockMaker.apply(thisMutable.getValue(), p),
					null
			);
			MISCTWFItems.ItemEntry<BucketItem> bucket = MISCTWFItems.ItemEntry.register(name+"_bucket", () -> makeBucket(still));
			FluidEntry<T> entry = new FluidEntry<>(still, flowing, block, bucket);
			thisMutable.setValue(entry);
			return entry;
		}

		private static <T extends Fluid> T makeFluid(FluidConstructor<T> maker, FluidEntry<T> entry, TagKey<Fluid> fluidTag, ResourceLocation stillTex, ResourceLocation flowingTex) {
			return maker.create(entry, fluidTag, stillTex, flowingTex);
		}

		private static <T extends Fluid> BucketItem makeBucket(RegistryObject<T> still) {
			return new BucketItem(still, new Item.Properties().stacksTo(1).craftRemainder(Items.BUCKET).tab(SurviveInTheWinterFrontier.ITEM_GROUP)) {
				@Override
				public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
					return new FluidBucketWrapper(stack);
				}
			};
		}
	}

	public static void init(IEventBus bus) {
		REGISTER.register(bus);
	}
}
