package com.hexagram2021.misc_twf.common.register;

import com.hexagram2021.misc_twf.common.fluid.BloodFluid;
import com.hexagram2021.misc_twf.common.fluid.FluidConstructor;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;

import java.util.function.BiFunction;
import java.util.function.Supplier;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;

/**
 * 流体注册类，管理模组中所有流体、流体方块和桶物品的注册喵~
 * 提供了便捷的流体注册封装，自动创建静态流体、流动流体、流体方块和对应的桶物品喵~
 *
 * @author liudongyu
 */
public final class MISCTWFFluids {
	public static final DeferredRegister<Fluid> REGISTER = DeferredRegister.create(Registries.FLUID, MODID);
	public static final DeferredRegister<FluidType> FLUID_TYPE_REGISTER = DeferredRegister.create(NeoForgeRegistries.FLUID_TYPES, MODID);

	/**
	 * 血液流体条目，包含静态血液、流动血液、血液方块和血桶喵~
	 * 血液呈现红色，移动速度减半，具有液体特性喵~
	 */
	public static final FluidEntry<BloodFluid> BLOOD_FLUID = FluidEntry.register(
			"blood", MISCTWFFluidTags.BLOOD, BloodFluid.Source::new, BloodFluid.Flowing::new,
			ResourceLocation.fromNamespaceAndPath(MODID, "block/fluid/blood_still"), ResourceLocation.fromNamespaceAndPath(MODID, "block/fluid/blood_flowing"),
			() -> BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_RED).strength(5.0F)
					.speedFactor(0.5F).noCollission().replaceable().pushReaction(PushReaction.DESTROY).liquid().noLootTable(),
			(entry, props) -> new LiquidBlock(entry.getStill(), props),
			FluidType.Properties.create()
					.descriptionId("block.misc_twf.blood")
					.motionScale(0.02D).fallDistanceModifier(0.2F)
					.canExtinguish(true).supportsBoating(true)
					.pathType(PathType.LAVA)
					.sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
					.sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
					.sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH)
	);

	/**
	 * 流体条目记录类，封装了流体的完整注册信息喵~
	 * 包含静态流体、流动流体、流体方块和桶物品的注册持有器喵~
	 *
	 * @param still 静态流体注册持有器喵~
	 * @param flowing 流动流体注册持有器喵~
	 * @param fluidBlock 流体方块条目喵~
	 * @param bucket 桶物品条目喵~
	 * @param <T> 流体类型喵~
	 */
	public record FluidEntry<T extends Fluid>(DeferredHolder<Fluid, T> still, DeferredHolder<Fluid, T> flowing,
											  MISCTWFBlocks.BlockEntry<LiquidBlock> fluidBlock, MISCTWFItems.ItemEntry<BucketItem> bucket,
											  DeferredHolder<FluidType, FluidType> type) {
		/**
		 * 获取流动状态的流体实例喵~
		 *
		 * @return 流动流体实例喵~
		 */
		public T getFlowing() {
			return this.flowing.get();
		}

		/**
		 * 获取静止状态的流体实例喵~
		 *
		 * @return 静止流体实例喵~
		 */
		public T getStill() {
			return this.still.get();
		}

		/**
		 * 获取流体对应的液体方块实例喵~
		 *
		 * @return 液体方块实例喵~
		 */
		public LiquidBlock getBlock() {
			return this.fluidBlock.get();
		}

		/**
		 * 获取流体对应的桶物品实例喵~
		 *
		 * @return 桶物品实例喵~
		 */
		public BucketItem getBucket() {
			return this.bucket.get();
		}

		/**
		 * 注册一个完整的流体条目，包括静态流体、流动流体、流体方块和桶物品喵~
		 *
		 * @param name 流体名称喵~
		 * @param fluidTag 流体标签喵~
		 * @param stillMaker 静态流体构造器喵~
		 * @param flowingMaker 流动流体构造器喵~
		 * @param stillTex 静态流体纹理资源位置喵~
		 * @param flowingTex 流动流体纹理资源位置喵~
		 * @param blockProperties 流体方块属性提供器喵~
		 * @param blockMaker 流体方块创建函数喵~
		 * @param <T> 流体类型喵~
		 * @return 流体条目喵~
		 */
		public static <T extends Fluid> FluidEntry<T> register(String name, TagKey<Fluid> fluidTag,
															   FluidConstructor<T> stillMaker, FluidConstructor<T> flowingMaker,
															   ResourceLocation stillTex, ResourceLocation flowingTex,
															   Supplier<BlockBehaviour.Properties> blockProperties,
															   BiFunction<FluidEntry<T>, BlockBehaviour.Properties, ? extends LiquidBlock> blockMaker,
															   FluidType.Properties properties) {
			Mutable<FluidEntry<T>> thisMutable = new MutableObject<>();
			DeferredHolder<Fluid, T> still = REGISTER.register(name, () -> makeFluid(
					stillMaker, thisMutable.getValue(), fluidTag, stillTex, flowingTex
			));
			DeferredHolder<Fluid, T> flowing = REGISTER.register("flowing_" + name, () -> makeFluid(
					flowingMaker, thisMutable.getValue(), fluidTag, stillTex, flowingTex
			));
			MISCTWFBlocks.BlockEntry<LiquidBlock> block = new MISCTWFBlocks.BlockEntry<>(
					name,
					blockProperties,
					p -> blockMaker.apply(thisMutable.getValue(), p),
					null
			);
			MISCTWFItems.ItemEntry<BucketItem> bucket = MISCTWFItems.ItemEntry.register(name + "_bucket", () -> makeBucket(still));
			DeferredHolder<FluidType, FluidType> type = FLUID_TYPE_REGISTER.register(name, () -> new FluidType(properties));
			FluidEntry<T> entry = new FluidEntry<>(still, flowing, block, bucket, type);
			thisMutable.setValue(entry);
			return entry;
		}

		/**
		 * 使用流体构造器创建流体实例喵~
		 *
		 * @param maker 流体构造器喵~
		 * @param entry 流体条目喵~
		 * @param fluidTag 流体标签喵~
		 * @param stillTex 静态流体纹理喵~
		 * @param flowingTex 流动流体纹理喵~
		 * @param <T> 流体类型喵~
		 * @return 流体实例喵~
		 */
		private static <T extends Fluid> T makeFluid(FluidConstructor<T> maker, FluidEntry<T> entry, TagKey<Fluid> fluidTag, ResourceLocation stillTex, ResourceLocation flowingTex) {
			return maker.create(entry, fluidTag, stillTex, flowingTex);
		}

		/**
		 * 创建流体对应的桶物品喵~
		 *
		 * @param still 静态流体注册持有器喵~
		 * @param <T> 流体类型喵~
		 * @return 桶物品实例喵~
		 */
		private static <T extends Fluid> BucketItem makeBucket(DeferredHolder<Fluid, T> still) {
			return new BucketItem(still.get(), new Item.Properties().stacksTo(1).craftRemainder(Items.BUCKET));
		}
	}

	/**
	 * 初始化并注册所有流体到事件总线喵~
	 *
	 * @param bus NeoForge 事件总线喵~
	 */
	public static void init(IEventBus bus) {
		REGISTER.register(bus);
		FLUID_TYPE_REGISTER.register(bus);
	}
}
