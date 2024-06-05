package com.hexagram2021.misc_twf.common.world.structures.pieces;

import com.google.common.collect.Lists;
import com.hexagram2021.misc_twf.common.register.MISCTWFFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructurePieceAccessor;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;
import static com.hexagram2021.misc_twf.common.register.MISCTWFStructurePieceTypes.*;

public class BossLairPieces {
	@FunctionalInterface
	private interface PieceFactory<T extends AbstractBossLairPiece> {
		@Nullable
		T createPiece(StructurePieceAccessor pieces, int x, int y, int z, long seed, Direction direction, int depth);
	}

	private static sealed abstract class AbstractBossLairPiece extends StructurePiece permits HallPiece, AbstractEarRoomPiece, StaircasePiece, WallPiece {
		protected static final BlockState STONE = Blocks.STONE.defaultBlockState();

		protected AbstractBossLairPiece(StructurePieceType type, int depth, BoundingBox bbox) {
			super(type, depth, bbox);
		}
		protected AbstractBossLairPiece(StructurePieceType type, CompoundTag nbt) {
			super(type, nbt);
		}

		@Override
		protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag nbt) {
		}

		@SuppressWarnings({"UnusedReturnValue", "SameParameterValue"})
		@Nullable
		protected StructurePiece generateChildForward(StartPiece startPiece, StructurePieceAccessor pieces,
													  PieceFactory<? extends AbstractBossLairPiece> factory, int xOffset, int yOffset) {
			Direction direction = this.getOrientation();
			if (direction != null) {
				switch(direction) {
					case NORTH:
						return this.generateAndAddPiece(startPiece, pieces, factory, this.boundingBox.minX() + xOffset, this.boundingBox.minY() + yOffset, this.boundingBox.minZ() - 1, direction, this.getGenDepth());
					case SOUTH:
						return this.generateAndAddPiece(startPiece, pieces, factory, this.boundingBox.minX() + xOffset, this.boundingBox.minY() + yOffset, this.boundingBox.maxZ() + 1, direction, this.getGenDepth());
					case WEST:
						return this.generateAndAddPiece(startPiece, pieces, factory, this.boundingBox.minX() - 1, this.boundingBox.minY() + yOffset, this.boundingBox.minZ() + xOffset, direction, this.getGenDepth());
					case EAST:
						return this.generateAndAddPiece(startPiece, pieces, factory, this.boundingBox.maxX() + 1, this.boundingBox.minY() + yOffset, this.boundingBox.minZ() + xOffset, direction, this.getGenDepth());
				}
			}

			return null;
		}

		@SuppressWarnings({"UnusedReturnValue", "SameParameterValue"})
		@Nullable
		protected StructurePiece generateChildLeft(StartPiece startPiece, StructurePieceAccessor pieces,
												   PieceFactory<? extends AbstractBossLairPiece> factory, int yOffset, int zOffset) {
			Direction direction = this.getOrientation();
			if (direction != null) {
				switch(direction) {
					case NORTH:
					case SOUTH:
						return this.generateAndAddPiece(startPiece, pieces, factory, this.boundingBox.minX() - 1, this.boundingBox.minY() + yOffset, this.boundingBox.minZ() + zOffset, Direction.WEST, this.getGenDepth());
					case WEST:
					case EAST:
						return this.generateAndAddPiece(startPiece, pieces, factory, this.boundingBox.minX() + zOffset, this.boundingBox.minY() + yOffset, this.boundingBox.minZ() - 1, Direction.NORTH, this.getGenDepth());
				}
			}

			return null;
		}

		@SuppressWarnings({"UnusedReturnValue", "SameParameterValue"})
		@Nullable
		protected StructurePiece generateChildRight(StartPiece startPiece, StructurePieceAccessor pieces,
													PieceFactory<? extends AbstractBossLairPiece> factory, int yOffset, int zOffset) {
			Direction direction = this.getOrientation();
			if (direction != null) {
				switch(direction) {
					case NORTH:
					case SOUTH:
						return this.generateAndAddPiece(startPiece, pieces, factory, this.boundingBox.maxX() + 1, this.boundingBox.minY() + yOffset, this.boundingBox.minZ() + zOffset, Direction.EAST, this.getGenDepth());
					case WEST:
					case EAST:
						return this.generateAndAddPiece(startPiece, pieces, factory, this.boundingBox.minX() + zOffset, this.boundingBox.minY() + yOffset, this.boundingBox.maxZ() + 1, Direction.SOUTH, this.getGenDepth());
				}
			}

			return null;
		}

		@Override
		public void addChildren(StructurePiece structurePiece, StructurePieceAccessor pieces, Random random) {
			this.addChildren((StartPiece)structurePiece, pieces);
		}
		public abstract void addChildren(StartPiece startPiece, StructurePieceAccessor pieces);

		@Nullable
		private StructurePiece generateAndAddPiece(StartPiece startPiece, StructurePieceAccessor pieces, PieceFactory<? extends AbstractBossLairPiece> factory,
												   int x, int y, int z, Direction direction, int genDepth) {
			if (Math.abs(x - startPiece.getBoundingBox().minX()) <= 112 && Math.abs(z - startPiece.getBoundingBox().minZ()) <= 112 && genDepth < 50) {
				AbstractBossLairPiece newPiece = factory.createPiece(pieces, x, y, z, startPiece.seed++, direction, genDepth);
				if(newPiece != null) {
					pieces.addPiece(newPiece);
					startPiece.pendingChildren.add(newPiece);
				}

				return newPiece;
			}
			return null;
		}

		protected static boolean isOkBox(BoundingBox bbox) {
			return bbox.minY() > 3;
		}
	}

	public static sealed class HallPiece extends AbstractBossLairPiece permits StartPiece {
		protected static final int WIDTH = 15;
		protected static final int HEIGHT = 11;
		protected static final int LENGTH = 37;

		protected static final int OFF_X = 7;
		protected static final int OFF_Y = 1;
		protected static final int OFF_Z = 0;

		protected static final BlockState BLOOD = MISCTWFFluids.BLOOD_FLUID.getBlock().defaultBlockState();
		protected static final BlockState DEAN_BRICKS = RegistryObject.create(new ResourceLocation("createdeco", "dean_bricks"), ForgeRegistries.BLOCKS).get().defaultBlockState();
		protected static final BlockState MOSSY_DEAN_BRICKS = RegistryObject.create(new ResourceLocation("createdeco", "mossy_dean_bricks"), ForgeRegistries.BLOCKS).get().defaultBlockState();
		protected static final BlockState PEBBLES = RegistryObject.create(new ResourceLocation("verdure", "pebbles"), ForgeRegistries.BLOCKS).get().defaultBlockState();

		public HallPiece(int depth, BoundingBox bbox, Direction direction) {
			this(HALL_TYPE, depth, bbox, direction);
		}

		public HallPiece(StructurePieceSerializationContext context, CompoundTag nbt) {
			this(HALL_TYPE, context, nbt);
		}

		protected HallPiece(StructurePieceType type, int depth, BoundingBox bbox, Direction direction) {
			super(type, depth, bbox);
			this.setOrientation(direction);
		}

		protected HallPiece(StructurePieceType type, @SuppressWarnings("unused") StructurePieceSerializationContext context, CompoundTag nbt) {
			super(type, nbt);
		}

		@Override
		public void addChildren(StartPiece startPiece, StructurePieceAccessor pieces) {
			this.generateChildLeft(startPiece, pieces, BoilerRoomPiece::createPiece, 1, LENGTH / 2);
			this.generateChildRight(startPiece, pieces, BoilerRoomPiece::createPiece, 1, LENGTH / 2);
		}

		@Override
		public void postProcess(WorldGenLevel level, StructureFeatureManager manager, ChunkGenerator chunk, Random random,
								BoundingBox bbox, ChunkPos chunkPos, BlockPos blockPos) {
			this.generateBox(level, bbox, 0, 0, 0, WIDTH - 1, HEIGHT - 1, LENGTH - 1, STONE, CAVE_AIR, true);
			this.generateBox(level, bbox, 1, 1, 0, WIDTH - 2, HEIGHT - 2, 0, CAVE_AIR, CAVE_AIR, false);
			this.generateBox(level, bbox, 0, 1, 17, 0, 3, 19, CAVE_AIR, CAVE_AIR, false);
			this.generateBox(level, bbox, WIDTH - 1, 1, 17, WIDTH - 1, 3, 19, CAVE_AIR, CAVE_AIR, false);
			this.generateBox(level, bbox, 0, 0, 0, WIDTH - 1, 0, LENGTH - 1, STONE, STONE, false);
			this.generateBox(level, bbox, 0, 1, 0, 3, 1, LENGTH - 1, DEAN_BRICKS, DEAN_BRICKS, false);
			this.generateBox(level, bbox, WIDTH - 4, 1, 0, WIDTH - 1, 1, LENGTH - 1, DEAN_BRICKS, DEAN_BRICKS, false);
			this.generateBox(level, bbox, 4, 1, 0, WIDTH - 5, 1, LENGTH - 1, BLOOD, BLOOD, false);
			int pebbles = random.nextInt(8) + random.nextInt(4);
			for(int ignored = 0; ignored < pebbles; ++ignored) {
				int x = random.nextInt(4) + (random.nextBoolean() ? 0 : WIDTH - 4);
				int z = random.nextInt(LENGTH);
				this.placeBlock(level, PEBBLES, x, 2, z, bbox);
			}
			int mossy = random.nextInt(4) + random.nextInt(4);
			for(int ignored = 0; ignored < mossy; ++ignored) {
				int x = random.nextInt(4) + (random.nextBoolean() ? 0 : WIDTH - 4);
				int z = random.nextInt(LENGTH);
				this.placeBlock(level, MOSSY_DEAN_BRICKS, x, 1, z, bbox);
			}
		}

		@Nullable
		public static HallPiece createPiece(StructurePieceAccessor pieces, int x, int y, int z, long seed, Direction direction, int depth) {
			BoundingBox boundingbox = BoundingBox.orientBox(x, y, z, -OFF_X, -OFF_Y, -OFF_Z, WIDTH, HEIGHT, LENGTH, direction);
			return isOkBox(boundingbox) && pieces.findCollisionPiece(boundingbox) == null ? new HallPiece(depth, boundingbox, direction) : null;
		}
	}

	public static final class StartPiece extends HallPiece {
		public final List<StructurePiece> pendingChildren = Lists.newArrayList();
		public long seed;

		public StartPiece(Random random, int x, int z) {
			this(x, z, random.nextLong(), getRandomHorizontalDirection(random));
		}
		private StartPiece(int x, int z, long seed, Direction direction) {
			super(START_TYPE, 0, makeBoundingBox(x, 5, z, direction, WIDTH, HEIGHT, LENGTH), direction);
			this.seed = seed;
		}

		public StartPiece(StructurePieceSerializationContext context, CompoundTag nbt) {
			super(START_TYPE, context, nbt);
			this.seed = nbt.getLong("Seed");
		}

		@Override
		protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag nbt) {
			super.addAdditionalSaveData(context, nbt);
			nbt.putLong("Seed", this.seed);
		}

		@Override
		public void addChildren(StartPiece startPiece, StructurePieceAccessor pieces) {
			this.generateChildForward(startPiece, pieces, StaircasePiece::createPiece, OFF_X, 1);
			this.generateChildLeft(startPiece, pieces, BossRoomPiece::createPiece, 1, LENGTH / 2);
			this.generateChildRight(startPiece, pieces, BossRoomPiece::createPiece, 1, LENGTH / 2);
		}

		@Override
		public void postProcess(WorldGenLevel level, StructureFeatureManager manager, ChunkGenerator chunk, Random random,
								BoundingBox bbox, ChunkPos chunkPos, BlockPos blockPos) {
			this.generateBox(level, bbox, 0, 0, 0, WIDTH - 1, HEIGHT - 1, LENGTH - 1, STONE, CAVE_AIR, true);
			this.generateBox(level, bbox, 0, 1, 17, 0, 3, 19, CAVE_AIR, CAVE_AIR, false);
			this.generateBox(level, bbox, WIDTH - 1, 1, 17, WIDTH - 1, 3, 19, CAVE_AIR, CAVE_AIR, false);
			this.generateBox(level, bbox, 1, 1, LENGTH - 1, WIDTH - 2, HEIGHT - 2, LENGTH - 1, CAVE_AIR, CAVE_AIR, false);
			this.generateBox(level, bbox, 0, 1, 0, 3, 1, LENGTH - 1, DEAN_BRICKS, DEAN_BRICKS, false);
			this.generateBox(level, bbox, WIDTH - 4, 1, 0, WIDTH - 1, 1, LENGTH - 1, DEAN_BRICKS, DEAN_BRICKS, false);
			this.generateBox(level, bbox, 4, 1, 0, WIDTH - 5, 1, LENGTH - 1, BLOOD, BLOOD, false);
			int pebbles = random.nextInt(8) + random.nextInt(4);
			for(int ignored = 0; ignored < pebbles; ++ignored) {
				int x = random.nextInt(4) + (random.nextBoolean() ? 0 : WIDTH - 4);
				int z = random.nextInt(LENGTH);
				this.placeBlock(level, PEBBLES, x, 2, z, bbox);
			}
			int mossy = random.nextInt(4) + random.nextInt(4);
			for(int ignored = 0; ignored < mossy; ++ignored) {
				int x = random.nextInt(4) + (random.nextBoolean() ? 0 : WIDTH - 4);
				int z = random.nextInt(LENGTH);
				this.placeBlock(level, MOSSY_DEAN_BRICKS, x, 1, z, bbox);
			}
		}
	}

	public static abstract sealed class AbstractEarRoomPiece extends AbstractBossLairPiece permits BoilerRoomPiece, BossRoomPiece {
		protected static final BlockState BRICKS = Blocks.BRICKS.defaultBlockState();
		protected static final BlockState POLISHED_CUT_DRIPSTONE = RegistryObject.create(new ResourceLocation("create", "polished_cut_dripstone"), ForgeRegistries.BLOCKS).get().defaultBlockState();
		protected static final BlockState POLISHED_CUT_OCHRUM = RegistryObject.create(new ResourceLocation("create", "polished_cut_ochrum"), ForgeRegistries.BLOCKS).get().defaultBlockState();
		protected static final BlockState OCHRUM_PILLAR = RegistryObject.create(new ResourceLocation("create", "ochrum_pillar"), ForgeRegistries.BLOCKS).get().defaultBlockState();

		protected static final BlockState FLESH = RegistryObject.create(new ResourceLocation("biomancy", "flesh"), ForgeRegistries.BLOCKS).get().defaultBlockState();
		protected static final BlockState FLESH_SLAB = RegistryObject.create(new ResourceLocation("biomancy", "flesh_slab"), ForgeRegistries.BLOCKS).get().defaultBlockState();
		protected static final BlockState MALIGNANT_FLESH = RegistryObject.create(new ResourceLocation("biomancy", "malignant_flesh"), ForgeRegistries.BLOCKS).get().defaultBlockState();
		protected static final BlockState MALIGNANT_FLESH_SLAB = RegistryObject.create(new ResourceLocation("biomancy", "malignant_flesh_slab"), ForgeRegistries.BLOCKS).get().defaultBlockState();

		protected static final int WIDTH = 37;
		protected static final int HEIGHT = 14;
		protected static final int LENGTH = 15;

		protected static final int OFF_X = 18;
		protected static final int OFF_Y = 1;
		protected static final int OFF_Z = 0;

		protected static final int PDF_LENGTH = 11;
		protected final byte[][] pdf = new byte[PDF_LENGTH][PDF_LENGTH];

		private static void addGaussian(double[][] destination, double mu1, double mu2, double sig1, double sig2, double cov) {
			double det = sig1 * sig2 - cov * cov;
			for(int i = 0; i < PDF_LENGTH; ++i) {
				double x = i - mu1;
				for(int j = 0; j < PDF_LENGTH; ++j) {
					double y = j - mu2;
					destination[i][j] += Math.pow(2.0D * Math.PI, -1.0D) * Math.pow(det, -0.5D) * Math.exp(
							-0.5D * (x * x * sig2 - 2.0D * x * y * cov + y * y * sig1) / det
					);
				}
			}
		}

		private void setRandomPDF(long seed) {
			double[][] pdf = new double[PDF_LENGTH][PDF_LENGTH];
			for(int i = 0; i < PDF_LENGTH; ++i) {
				for(int j = 0; j < PDF_LENGTH; ++j) {
					pdf[i][j] = Math.pow(2.0D * Math.PI, -1.0D) / 5.0D * Math.exp(-0.05D * (i * i + j * j));
				}
			}
			Random random = new Random(seed);
			for(int ignored = 0; ignored < 7; ++ignored) {
				double sig1 = random.nextDouble() * 5.0D + 3.0D;
				double sig2 = random.nextDouble() * 5.0D + 3.0D;
				double bound = Math.pow(sig1 * sig2, 0.5D);
				addGaussian(
						pdf,
						random.nextDouble() * 6.0D + 2.5D, random.nextDouble() * 6.0D + 2.5D,
						sig1, sig2, (random.nextDouble() * 1.6D - 0.8D) * bound
				);
			}

			for(int i = 0; i < PDF_LENGTH; ++i) {
				for(int j = 0; j < PDF_LENGTH; ++j) {
					this.pdf[i][j] = (byte)(pdf[i][j] / 0.03D);
				}
			}
		}

		@Override
		protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag nbt) {
			super.addAdditionalSaveData(context, nbt);
			ListTag pdfList = new ListTag();
			for(int i = 0; i < PDF_LENGTH; ++i) {
				pdfList.add(new ByteArrayTag(this.pdf[i]));
			}
			nbt.put("PDF", pdfList);
		}

		protected AbstractEarRoomPiece(StructurePieceType type, int depth, BoundingBox bbox, Direction direction, long seed) {
			super(type, depth, bbox);
			this.setOrientation(direction);
			this.setRandomPDF(seed);
		}

		protected AbstractEarRoomPiece(StructurePieceType type, @SuppressWarnings("unused") StructurePieceSerializationContext context, CompoundTag nbt) {
			super(type, nbt);

			if(nbt.contains("PDF", Tag.TAG_LIST)) {
				ListTag pdfList = nbt.getList("PDF", Tag.TAG_BYTE_ARRAY);
				for(int i = 0; i < PDF_LENGTH; ++i) {
					ByteArrayTag byteTags = (ByteArrayTag)pdfList.get(i);
					for(int j = 0; j < PDF_LENGTH; ++j) {
						this.pdf[i][j] = byteTags.get(j).getAsByte();
					}
				}
			}
		}

		@Override
		public void addChildren(StartPiece startPiece, StructurePieceAccessor pieces) {
		}

		private static final ResourceLocation LOOT_TABLE_TEACH = new ResourceLocation(MODID, "chests/abyss_lair_teach");
		private static final ResourceLocation LOOT_TABLE_ORDINARY = new ResourceLocation(MODID, "chests/abyss_lair_ordinary");
		private static final ResourceLocation LOOT_TABLE_RARE = new ResourceLocation(MODID, "chests/abyss_lair_rare");
		private static final double CHEST_POSSIBILITY = 0.75D;

		@Override
		public void postProcess(WorldGenLevel level, StructureFeatureManager manager, ChunkGenerator chunk, Random random,
								BoundingBox bbox, ChunkPos chunkPos, BlockPos blockPos) {
			//outline
			this.generateBox(level, bbox, 0, 0, 0, WIDTH - 1, HEIGHT - 1, LENGTH - 1, STONE, CAVE_AIR, false);
			this.generateBox(level, bbox, 2, 1, LENGTH - 2, WIDTH - 3, 6, LENGTH - 2, BRICKS, BRICKS, false);
			this.generateBox(level, bbox, 2, 1, 1, 15, 7, 1, BRICKS, BRICKS, false);
			this.generateBox(level, bbox, 21, 1, 1, WIDTH - 3, 7, 1, BRICKS, BRICKS, false);
			this.generateBox(level, bbox, 1, 1, 2, 1, 7, LENGTH - 3, BRICKS, BRICKS, false);
			this.generateBox(level, bbox, WIDTH - 2, 1, 2, WIDTH - 2, 7, LENGTH - 3, BRICKS, BRICKS, false);

			//floor
			this.generateBox(level, bbox, 1, 0, 1, WIDTH - 2, 0, LENGTH - 2, POLISHED_CUT_DRIPSTONE, POLISHED_CUT_DRIPSTONE, false);

			//vertical pillars
			this.placeBlock(level, POLISHED_CUT_OCHRUM, 1, 1, 1, bbox);
			this.generateBox(level, bbox, 1, 2, 1, 1, 7, 1, OCHRUM_PILLAR, OCHRUM_PILLAR, false);
			this.placeBlock(level, POLISHED_CUT_OCHRUM, 1, 1, LENGTH - 2, bbox);
			this.generateBox(level, bbox, 1, 2, LENGTH - 2, 1, 7, LENGTH - 2, OCHRUM_PILLAR, OCHRUM_PILLAR, false);
			this.placeBlock(level, POLISHED_CUT_OCHRUM, WIDTH - 2, 1, 1, bbox);
			this.generateBox(level, bbox, WIDTH - 2, 2, 1, WIDTH - 2, 7, 1, OCHRUM_PILLAR, OCHRUM_PILLAR, false);
			this.placeBlock(level, POLISHED_CUT_OCHRUM, WIDTH - 2, 1, LENGTH - 2, bbox);
			this.generateBox(level, bbox, WIDTH - 2, 2, LENGTH - 2, WIDTH - 2, 7, LENGTH - 2, OCHRUM_PILLAR, OCHRUM_PILLAR, false);

			this.placeBlock(level, POLISHED_CUT_OCHRUM, 16, 1, 1, bbox);
			this.generateBox(level, bbox, 16, 2, 1, 16, 7, 1, OCHRUM_PILLAR, OCHRUM_PILLAR, false);
			this.placeBlock(level, POLISHED_CUT_OCHRUM, 20, 1, 1, bbox);
			this.generateBox(level, bbox, 20, 2, 1, 20, 7, 1, OCHRUM_PILLAR, OCHRUM_PILLAR, false);

			//horizontal pillars
			this.generateBox(level, bbox, 2, 7, 1, WIDTH - 3, 7, 1, POLISHED_CUT_OCHRUM, POLISHED_CUT_OCHRUM, false);
			this.generateBox(level, bbox, 2, 7, LENGTH - 2, WIDTH - 3, 7, LENGTH - 2, POLISHED_CUT_OCHRUM, POLISHED_CUT_OCHRUM, false);
			this.generateBox(level, bbox, 1, 7, 2, 1, 7, LENGTH - 3, POLISHED_CUT_OCHRUM, POLISHED_CUT_OCHRUM, false);
			this.generateBox(level, bbox, WIDTH - 2, 7, 2, WIDTH - 2, 7, LENGTH - 3, POLISHED_CUT_OCHRUM, POLISHED_CUT_OCHRUM, false);

			//door
			this.generateBox(level, bbox, 17, 1, 0, 19, 3, 0, CAVE_AIR, CAVE_AIR, false);

			//chests
			if(random.nextDouble() < CHEST_POSSIBILITY) {
				this.createChest(level, bbox, random, 2, 1, LENGTH - 3, LOOT_TABLE_TEACH);
			}
			if(random.nextDouble() < CHEST_POSSIBILITY) {
				this.createChest(level, bbox, random, 6, 1, 2, LOOT_TABLE_ORDINARY);
				this.createChest(level, bbox, random, 7, 1, 2, LOOT_TABLE_ORDINARY);
			}
			if(random.nextDouble() < CHEST_POSSIBILITY) {
				this.createChest(level, bbox, random, 17, 1, LENGTH - 3, LOOT_TABLE_RARE);
			}
			if(random.nextDouble() < CHEST_POSSIBILITY) {
				this.createChest(level, bbox, random, WIDTH - 3, 1, LENGTH - 3, LOOT_TABLE_ORDINARY);
				this.createChest(level, bbox, random, WIDTH - 3, 1, LENGTH - 4, LOOT_TABLE_ORDINARY);
			}
		}
	}

	public static final class BoilerRoomPiece extends AbstractEarRoomPiece {
		private static final BlockState CAST_IRON_HULL = RegistryObject.create(new ResourceLocation("createdeco", "cast_iron_hull"), ForgeRegistries.BLOCKS).get().defaultBlockState()
				.setValue(BlockStateProperties.FACING, Direction.DOWN);
		private static final BlockState INDUSTRIAL_IRON_BLOCK = RegistryObject.create(new ResourceLocation("create", "industrial_iron_block"), ForgeRegistries.BLOCKS).get().defaultBlockState();
		private static final BlockState POLISHED_CUT_DEEPSLATE = RegistryObject.create(new ResourceLocation("create", "polished_cut_deepslate"), ForgeRegistries.BLOCKS).get().defaultBlockState();
		private static final BlockState POLISHED_CUT_DEEPSLATE_STAIRS = RegistryObject.create(new ResourceLocation("create", "polished_cut_deepslate_stairs"), ForgeRegistries.BLOCKS).get().defaultBlockState();

		private static final BlockState FLUID_PIPE_VERTICAL, FLUID_PIPE_CORNER, FLUID_PIPE_HORIZONTAL, FLUID_PIPE_HORIZONTAL_2;
		private static final BlockState FLUID_PIPE_CORNER_1, FLUID_PIPE_CORNER_2;
		private static final BlockState FLUID_PIPE_T_CROSS_1, FLUID_PIPE_T_CROSS_2, FLUID_PIPE_T_CROSS_3, FLUID_PIPE_T_CROSS_4, FLUID_PIPE_X_CROSS;

		static {
			Block fluidPipe = RegistryObject.create(new ResourceLocation("create", "fluid_pipe"), ForgeRegistries.BLOCKS).get();
			BlockState initialFluidPipe = fluidPipe.defaultBlockState()
					.setValue(BlockStateProperties.UP, false).setValue(BlockStateProperties.DOWN, false)
					.setValue(BlockStateProperties.NORTH, false).setValue(BlockStateProperties.SOUTH, false)
					.setValue(BlockStateProperties.WEST, false).setValue(BlockStateProperties.EAST, false);
			FLUID_PIPE_VERTICAL = initialFluidPipe.setValue(BlockStateProperties.UP, true).setValue(BlockStateProperties.DOWN, true);
			FLUID_PIPE_CORNER = initialFluidPipe.setValue(BlockStateProperties.DOWN, true).setValue(BlockStateProperties.SOUTH, true);
			FLUID_PIPE_HORIZONTAL = initialFluidPipe.setValue(BlockStateProperties.NORTH, true).setValue(BlockStateProperties.SOUTH, true);
			FLUID_PIPE_HORIZONTAL_2 = initialFluidPipe.setValue(BlockStateProperties.WEST, true).setValue(BlockStateProperties.EAST, true);
			FLUID_PIPE_CORNER_1 = initialFluidPipe.setValue(BlockStateProperties.DOWN, true).setValue(BlockStateProperties.SOUTH, true).setValue(BlockStateProperties.EAST, true);
			FLUID_PIPE_CORNER_2 = initialFluidPipe.setValue(BlockStateProperties.DOWN, true).setValue(BlockStateProperties.SOUTH, true).setValue(BlockStateProperties.WEST, true);
			FLUID_PIPE_T_CROSS_1 = initialFluidPipe
					.setValue(BlockStateProperties.SOUTH, true)
					.setValue(BlockStateProperties.WEST, true).setValue(BlockStateProperties.EAST, true);
			FLUID_PIPE_T_CROSS_2 = initialFluidPipe
					.setValue(BlockStateProperties.NORTH, true).setValue(BlockStateProperties.SOUTH, true)
					.setValue(BlockStateProperties.EAST, true);
			FLUID_PIPE_T_CROSS_3 = initialFluidPipe
					.setValue(BlockStateProperties.NORTH, true)
					.setValue(BlockStateProperties.WEST, true).setValue(BlockStateProperties.EAST, true);
			FLUID_PIPE_T_CROSS_4 = initialFluidPipe
					.setValue(BlockStateProperties.NORTH, true).setValue(BlockStateProperties.SOUTH, true)
					.setValue(BlockStateProperties.WEST, true);
			FLUID_PIPE_X_CROSS = initialFluidPipe
					.setValue(BlockStateProperties.DOWN, true)
					.setValue(BlockStateProperties.NORTH, true).setValue(BlockStateProperties.SOUTH, true)
					.setValue(BlockStateProperties.WEST, true).setValue(BlockStateProperties.EAST, true);
		}

		public BoilerRoomPiece(int depth, BoundingBox bbox, Direction direction, long seed) {
			super(BOILER_ROOM_TYPE, depth, bbox, direction, seed);
		}

		public BoilerRoomPiece(StructurePieceSerializationContext context, CompoundTag nbt) {
			super(BOILER_ROOM_TYPE, context, nbt);
		}

		@Override
		public void postProcess(WorldGenLevel level, StructureFeatureManager manager, ChunkGenerator chunk, Random random,
								BoundingBox bbox, ChunkPos chunkPos, BlockPos blockPos) {
			super.postProcess(level, manager, chunk, random, bbox, chunkPos, blockPos);

			//Boilers
			this.createBoiler(level, bbox, 13, 6 + random.nextInt(3), true);
			this.createBoiler(level, bbox, 17, 6 + random.nextInt(3), true);

			this.createBoiler(level, bbox, 4, 4, false);
			this.placeBlock(level, FLUID_PIPE_HORIZONTAL, 4, 7, 2, bbox);
			this.placeBlock(level, FLUID_PIPE_HORIZONTAL, 4, 7, 3, bbox);
			this.placeBlock(level, FLUID_PIPE_T_CROSS_2.setValue(BlockStateProperties.DOWN, true), 4, 7, 4, bbox);
			this.placeBlock(level, FLUID_PIPE_HORIZONTAL, 4, 7, 5, bbox);
			this.placeBlock(level, FLUID_PIPE_HORIZONTAL, 4, 7, 6, bbox);
			this.placeBlock(level, FLUID_PIPE_T_CROSS_2, 4, 7, 7, bbox);
			this.placeBlock(level, FLUID_PIPE_HORIZONTAL, 4, 7, 8, bbox);
			this.placeBlock(level, FLUID_PIPE_HORIZONTAL, 4, 7, 9, bbox);
			this.createBoiler(level, bbox, 4, 10, false);
			this.placeBlock(level, FLUID_PIPE_CORNER_1, 4, 7, 10, bbox);
			this.placeBlock(level, FLUID_PIPE_HORIZONTAL_2, 5, 7, 4, bbox);
			this.placeBlock(level, FLUID_PIPE_HORIZONTAL_2, 5, 7, 7, bbox);
			this.placeBlock(level, FLUID_PIPE_HORIZONTAL_2, 5, 7, 10, bbox);
			this.placeBlock(level, FLUID_PIPE_HORIZONTAL_2, 6, 7, 4, bbox);
			this.placeBlock(level, FLUID_PIPE_HORIZONTAL_2, 6, 7, 7, bbox);
			this.placeBlock(level, FLUID_PIPE_HORIZONTAL_2, 6, 7, 10, bbox);
			this.placeBlock(level, FLUID_PIPE_T_CROSS_3, 7, 7, 4, bbox);
			this.placeBlock(level, FLUID_PIPE_HORIZONTAL, 7, 7, 5, bbox);
			this.placeBlock(level, FLUID_PIPE_HORIZONTAL, 7, 7, 6, bbox);
			this.createBoiler(level, bbox, 7, 7, false);
			this.placeBlock(level, FLUID_PIPE_X_CROSS, 7, 7, 7, bbox);
			this.placeBlock(level, FLUID_PIPE_HORIZONTAL, 7, 7, 8, bbox);
			this.placeBlock(level, FLUID_PIPE_HORIZONTAL, 7, 7, 9, bbox);
			this.placeBlock(level, FLUID_PIPE_T_CROSS_1, 7, 7, 10, bbox);
			this.placeBlock(level, FLUID_PIPE_HORIZONTAL_2, 8, 7, 4, bbox);
			this.placeBlock(level, FLUID_PIPE_HORIZONTAL_2, 8, 7, 7, bbox);
			this.placeBlock(level, FLUID_PIPE_HORIZONTAL_2, 8, 7, 10, bbox);
			this.placeBlock(level, FLUID_PIPE_HORIZONTAL_2, 9, 7, 4, bbox);
			this.placeBlock(level, FLUID_PIPE_HORIZONTAL_2, 9, 7, 7, bbox);
			this.placeBlock(level, FLUID_PIPE_HORIZONTAL_2, 9, 7, 10, bbox);
			this.placeBlock(level, FLUID_PIPE_HORIZONTAL, 10, 7, 2, bbox);
			this.placeBlock(level, FLUID_PIPE_HORIZONTAL, 10, 7, 3, bbox);
			this.createBoiler(level, bbox, 10, 4, false);
			this.placeBlock(level, FLUID_PIPE_T_CROSS_4.setValue(BlockStateProperties.DOWN, true), 10, 7, 4, bbox);
			this.placeBlock(level, FLUID_PIPE_HORIZONTAL, 10, 7, 5, bbox);
			this.placeBlock(level, FLUID_PIPE_HORIZONTAL, 10, 7, 6, bbox);
			this.placeBlock(level, FLUID_PIPE_T_CROSS_4, 10, 7, 7, bbox);
			this.placeBlock(level, FLUID_PIPE_HORIZONTAL, 10, 7, 8, bbox);
			this.placeBlock(level, FLUID_PIPE_HORIZONTAL, 10, 7, 9, bbox);
			this.createBoiler(level, bbox, 10, 10, false);
			this.placeBlock(level, FLUID_PIPE_CORNER_2, 10, 7, 10, bbox);
			this.generateBox(level, bbox, 3, 0, 3, 11, 0, 11, POLISHED_CUT_DEEPSLATE, POLISHED_CUT_DEEPSLATE, false);

			//Veins
			int deadX = random.nextInt(PDF_LENGTH);
			int deadZ = random.nextInt(PDF_LENGTH);
			for(int i = 0; i < PDF_LENGTH; ++i) {
				for(int j = 0; j < PDF_LENGTH; ++j) {
					double possibility = 1.0D / (1.0D + 0.04D * (Math.pow(deadX - i, 2) + Math.pow(deadZ - j, 2)));
					byte h = this.pdf[i][j];
					int y = 1;
					while(h > 0) {
						if(h == 1) {
							this.placeBlock(level, random.nextDouble() < possibility ? MALIGNANT_FLESH_SLAB : FLESH_SLAB, 22 + i, y, 2 + j, bbox);
							break;
						}
						this.placeBlock(level, random.nextDouble() < possibility ? MALIGNANT_FLESH : FLESH, 22 + i, y, 2 + j, bbox);
						y += 1;
						h -= 2;
					}
				}
			}
		}

		private void createBoiler(WorldGenLevel level, BoundingBox bbox, int x, int z, boolean connect) {
			this.placeBlock(level, CAST_IRON_HULL, x, 1, z, bbox);
			this.placeBlock(level, CAST_IRON_HULL, x, 2, z, bbox);
			this.placeBlock(level, CAST_IRON_HULL, x, 3, z, bbox);
			this.placeBlock(level, INDUSTRIAL_IRON_BLOCK, x, 4, z, bbox);
			this.placeBlock(level, FLUID_PIPE_VERTICAL, x, 5, z, bbox);
			this.placeBlock(level, FLUID_PIPE_VERTICAL, x, 6, z, bbox);
			if(connect) {
				this.placeBlock(level, POLISHED_CUT_DEEPSLATE_STAIRS.setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.EAST), x + 1, 0, z, bbox);
				this.placeBlock(level, POLISHED_CUT_DEEPSLATE_STAIRS.setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.WEST), x - 1, 0, z, bbox);
				this.placeBlock(level, POLISHED_CUT_DEEPSLATE_STAIRS.setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH), x, 0, z + 1, bbox);
				this.placeBlock(level, POLISHED_CUT_DEEPSLATE_STAIRS.setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH), x, 0, z - 1, bbox);
				this.placeBlock(level, POLISHED_CUT_DEEPSLATE_STAIRS.setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.EAST).setValue(BlockStateProperties.STAIRS_SHAPE, StairsShape.INNER_LEFT), x + 1, 0, z + 1, bbox);
				this.placeBlock(level, POLISHED_CUT_DEEPSLATE_STAIRS.setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH).setValue(BlockStateProperties.STAIRS_SHAPE, StairsShape.INNER_LEFT), x + 1, 0, z - 1, bbox);
				this.placeBlock(level, POLISHED_CUT_DEEPSLATE_STAIRS.setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH).setValue(BlockStateProperties.STAIRS_SHAPE, StairsShape.INNER_LEFT), x - 1, 0, z + 1, bbox);
				this.placeBlock(level, POLISHED_CUT_DEEPSLATE_STAIRS.setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.WEST).setValue(BlockStateProperties.STAIRS_SHAPE, StairsShape.INNER_LEFT), x - 1, 0, z - 1, bbox);
				this.placeBlock(level, CAST_IRON_HULL, x, 0, z, bbox);
				this.placeBlock(level, FLUID_PIPE_CORNER, x, 7, z, bbox);
				for (int dz = 1; dz < z - 1; ++dz) {
					this.placeBlock(level, FLUID_PIPE_HORIZONTAL, x, 7, z - dz, bbox);
				}
			}
		}

		@Nullable
		public static BoilerRoomPiece createPiece(StructurePieceAccessor pieces, int x, int y, int z, long seed, Direction direction, int depth) {
			BoundingBox boundingbox = BoundingBox.orientBox(x, y, z, -OFF_X, -OFF_Y, -OFF_Z, WIDTH, HEIGHT, LENGTH, direction);
			return isOkBox(boundingbox) && pieces.findCollisionPiece(boundingbox) == null ? new BoilerRoomPiece(depth, boundingbox, direction, seed) : null;
		}
	}

	public static final class BossRoomPiece extends AbstractEarRoomPiece {
		private static final BlockState EMPTY_CANS = RegistryObject.create(new ResourceLocation("zombie_extreme", "empty_cans"), ForgeRegistries.BLOCKS).get().defaultBlockState();
		private static final BlockState IRON_TABLE = RegistryObject.create(new ResourceLocation("zombie_extreme", "iron_table"), ForgeRegistries.BLOCKS).get().defaultBlockState();
		private static final BlockState WOODEN_CHAIR = RegistryObject.create(new ResourceLocation("zombie_extreme", "wooden_chair"), ForgeRegistries.BLOCKS).get().defaultBlockState();
		private static final BlockState DECOMPOSING_BACKPACK = RegistryObject.create(new ResourceLocation("zombie_extreme", "decomposing_backpack"), ForgeRegistries.BLOCKS).get().defaultBlockState();

		private static final BlockState PRIMORDIAL_CRADLE = RegistryObject.create(new ResourceLocation("biomancy", "primordial_cradle"), ForgeRegistries.BLOCKS).get().defaultBlockState();

		public BossRoomPiece(int depth, BoundingBox bbox, Direction direction, long seed) {
			super(BOSS_ROOM_TYPE, depth, bbox, direction, seed);
		}

		public BossRoomPiece(StructurePieceSerializationContext context, CompoundTag nbt) {
			super(BOSS_ROOM_TYPE, context, nbt);
		}

		private static final ResourceLocation LOOT_TABLE_BACKPACK = new ResourceLocation(MODID, "chests/backpack");

		@Override
		public void postProcess(WorldGenLevel level, StructureFeatureManager manager, ChunkGenerator chunk, Random random,
								BoundingBox bbox, ChunkPos chunkPos, BlockPos blockPos) {
			super.postProcess(level, manager, chunk, random, bbox, chunkPos, blockPos);

			//misc
			this.placeBlock(level, EMPTY_CANS, 2, 1, 4, bbox);
			this.placeBlock(level, IRON_TABLE, 3, 1, 5, bbox);
			this.placeBlock(level, IRON_TABLE, 4, 1, 5, bbox);
			this.placeBlock(level, WOODEN_CHAIR, 2, 1, 11, bbox);
			this.createBackpack(level, bbox, random, 5, 1, 8, LOOT_TABLE_BACKPACK);

			//Veins
			double x0d = 0, z0d = 0;
			double mass = 0;
			int deadX = random.nextInt(PDF_LENGTH);
			int deadZ = random.nextInt(PDF_LENGTH);
			for(int i = 0; i < PDF_LENGTH; ++i) {
				for(int j = 0; j < PDF_LENGTH; ++j) {
					double possibility = 1.0D / (1.0D + 0.05D * (Math.pow(deadX - i, 2) + Math.pow(deadZ - j, 2)));
					byte h = this.pdf[i][j];
					mass += h;
					x0d += h * (i + 0.5D);
					z0d += h * (j + 0.5D);
					int y = 1;
					while(h > 0) {
						if(h == 1) {
							this.placeBlock(level, random.nextDouble() < possibility ? MALIGNANT_FLESH_SLAB : FLESH_SLAB, 16 + i, y, 2 + j, bbox);
							break;
						}
						this.placeBlock(level, random.nextDouble() < possibility ? MALIGNANT_FLESH : FLESH, 16 + i, y, 2 + j, bbox);
						y += 1;
						h -= 2;
					}
				}
			}
			int x0 = (int) (x0d / mass);
			int z0 = (int) (z0d / mass);
			this.placeBlock(level, PRIMORDIAL_CRADLE, 16 + x0, this.pdf[x0][z0] / 2 + 1, 2 + z0, bbox);
		}

		@Nullable
		public static BossRoomPiece createPiece(StructurePieceAccessor pieces, int x, int y, int z, long seed, Direction direction, int depth) {
			BoundingBox boundingbox = BoundingBox.orientBox(x, y, z, -OFF_X, -OFF_Y, -OFF_Z, WIDTH, HEIGHT, LENGTH, direction);
			return isOkBox(boundingbox) && pieces.findCollisionPiece(boundingbox) == null ? new BossRoomPiece(depth, boundingbox, direction, seed) : null;
		}

		@SuppressWarnings({"UnusedReturnValue", "SameParameterValue"})
		private boolean createBackpack(WorldGenLevel level, BoundingBox bbox, Random random, int x, int y, int z, ResourceLocation lootTable) {
			return this.createBackpack(level, bbox, random, this.getWorldPos(x, y, z), lootTable, null);
		}

		@SuppressWarnings("SameParameterValue")
		private boolean createBackpack(ServerLevelAccessor level, BoundingBox bbox, Random random, BlockPos blockPos,
									   ResourceLocation lootTable, @Nullable BlockState blockState) {
			if (bbox.isInside(blockPos) && !level.getBlockState(blockPos).is(DECOMPOSING_BACKPACK.getBlock())) {
				if (blockState == null) {
					blockState = reorient(level, blockPos, DECOMPOSING_BACKPACK);
				}

				level.setBlock(blockPos, blockState, Block.UPDATE_CLIENTS);
				BlockEntity blockentity = level.getBlockEntity(blockPos);
				if (blockentity instanceof RandomizableContainerBlockEntity container) {
					container.setLootTable(lootTable, random.nextLong());
				}

				return true;
			}
			return false;
		}
	}

	public static final class StaircasePiece extends AbstractBossLairPiece {
		private static final int WIDTH = 15;
		private static final int HEIGHT = 17;
		private static final int LENGTH = 16;

		private static final int OFF_X = 7;
		private static final int OFF_Y = 1;
		private static final int OFF_Z = 0;

		private static final BlockState DEAN_BRICKS = RegistryObject.create(new ResourceLocation("createdeco", "dean_bricks"), ForgeRegistries.BLOCKS).get().defaultBlockState();
		private static final BlockState DEAN_BRICKS_SLAB = RegistryObject.create(new ResourceLocation("createdeco", "dean_bricks_slab"), ForgeRegistries.BLOCKS).get().defaultBlockState();

		public StaircasePiece(int depth, BoundingBox bbox, Direction direction) {
			super(STAIRCASE_TYPE, depth, bbox);
			this.setOrientation(direction);
		}

		public StaircasePiece(@SuppressWarnings("unused") StructurePieceSerializationContext context, CompoundTag nbt) {
			super(STAIRCASE_TYPE, nbt);
		}

		@Override
		public void addChildren(StartPiece startPiece, StructurePieceAccessor pieces) {
			this.generateChildForward(startPiece, pieces, HallPiece::createPiece, 7, 9);
		}

		@Override
		public void postProcess(WorldGenLevel level, StructureFeatureManager manager, ChunkGenerator chunk, Random random,
								BoundingBox bbox, ChunkPos chunkPos, BlockPos blockPos) {
			for(int z = 0; z < LENGTH; ++z) {
				int dz = (z + 1) / 2;
				for(int y = dz; y < HEIGHT - (LENGTH / 2) + dz; ++y) {
					this.placeBlock(level, STONE, 0, y, z, bbox);
					this.placeBlock(level, STONE, WIDTH - 1, y, z, bbox);
				}
			}
			for(int x = 1; x < WIDTH - 1; ++x) {
				for(int z = 0; z < LENGTH; ++z) {
					int dz = (z + 1) / 2;
					this.placeBlock(level, STONE, x, dz, z, bbox);
					BlockState floor;
					if(x >= 4 && x <= WIDTH - 5) {
						floor = CAVE_AIR;
					} else {
						floor = (z & 1) == 0 ? DEAN_BRICKS : DEAN_BRICKS_SLAB;
					}
					this.placeBlock(level, floor, x, dz + 1, z, bbox);
					for (int y = dz + 2; y < HEIGHT - (LENGTH / 2) + dz - 1; ++y) {
						this.placeBlock(level, CAVE_AIR, x, y, z, bbox);
					}
					this.placeBlock(level, STONE, x, HEIGHT - (LENGTH / 2) + dz - 1, z, bbox);
				}
			}
		}

		@Nullable
		public static StaircasePiece createPiece(StructurePieceAccessor pieces, int x, int y, int z, long seed, Direction direction, int depth) {
			BoundingBox boundingbox = BoundingBox.orientBox(x, y, z, -OFF_X, -OFF_Y, -OFF_Z, WIDTH, HEIGHT, LENGTH, direction);
			return isOkBox(boundingbox) && pieces.findCollisionPiece(boundingbox) == null ? new StaircasePiece(depth, boundingbox, direction) : null;
		}
	}

	public static final class WallPiece extends AbstractBossLairPiece {
		private static final int WIDTH = 5;
		private static final int HEIGHT = 5;
		private static final int LENGTH = 1;

		private static final int OFF_X = 1;
		private static final int OFF_Y = 1;
		private static final int OFF_Z = 0;

		public WallPiece(int depth, BoundingBox bbox, Direction direction) {
			super(WALL_TYPE, depth, bbox);
			this.setOrientation(direction);
		}

		public WallPiece(@SuppressWarnings("unused") StructurePieceSerializationContext context, CompoundTag nbt) {
			super(WALL_TYPE, nbt);
		}

		@Override
		public void addChildren(StartPiece startPiece, StructurePieceAccessor pieces) {
		}

		@Override
		public void postProcess(WorldGenLevel level, StructureFeatureManager manager, ChunkGenerator chunk, Random random,
								BoundingBox bbox, ChunkPos chunkPos, BlockPos blockPos) {
			this.generateBox(level, bbox, 0, 0, 0, WIDTH - 1, HEIGHT - 1, LENGTH - 1, STONE, STONE, false);
		}

		@Nullable
		public static WallPiece createPiece(StructurePieceAccessor pieces, int x, int y, int z, Direction direction, int depth) {
			BoundingBox boundingbox = BoundingBox.orientBox(x, y, z, -OFF_X, -OFF_Y, -OFF_Z, WIDTH, HEIGHT, LENGTH, direction);
			return isOkBox(boundingbox) && pieces.findCollisionPiece(boundingbox) == null ? new WallPiece(depth, boundingbox, direction) : null;
		}
	}
}
