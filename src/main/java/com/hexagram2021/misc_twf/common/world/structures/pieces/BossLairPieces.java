package com.hexagram2021.misc_twf.common.world.structures.pieces;

import com.google.common.collect.Lists;
import com.hexagram2021.misc_twf.SurviveInTheWinterFrontier;
import com.hexagram2021.misc_twf.common.register.MISCTWFFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
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

import static com.hexagram2021.misc_twf.common.register.MISCTWFStructurePieceTypes.*;

public class BossLairPieces {
	@FunctionalInterface
	private interface PieceFactory<T extends AbstractBossLairPiece> {
		@Nullable
		T createPiece(StructurePieceAccessor pieces, int x, int y, int z, Direction direction, int depth);
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
				AbstractBossLairPiece newPiece = factory.createPiece(pieces, x, y, z, direction, genDepth);
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
			this.generateBox(level, bbox, 3, 1, 0, WIDTH - 4, 1, LENGTH - 1, BLOOD, BLOOD, false);
		}

		@Nullable
		public static HallPiece createPiece(StructurePieceAccessor pieces, int x, int y, int z, Direction direction, int depth) {
			BoundingBox boundingbox = BoundingBox.orientBox(x, y, z, -OFF_X, -OFF_Y, -OFF_Z, WIDTH, HEIGHT, LENGTH, direction);
			return isOkBox(boundingbox) && pieces.findCollisionPiece(boundingbox) == null ? new HallPiece(depth, boundingbox, direction) : null;
		}
	}

	public static final class StartPiece extends HallPiece {
		public final List<StructurePiece> pendingChildren = Lists.newArrayList();

		public StartPiece(Random random, int x, int z) {
			this(x, z, getRandomHorizontalDirection(random));
		}
		private StartPiece(int x, int z, Direction direction) {
			super(START_TYPE, 0, makeBoundingBox(x, 5, z, direction, WIDTH, HEIGHT, LENGTH), direction);
		}

		public StartPiece(StructurePieceSerializationContext context, CompoundTag nbt) {
			super(START_TYPE, context, nbt);
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
		}
	}

	public static abstract sealed class AbstractEarRoomPiece extends AbstractBossLairPiece permits BoilerRoomPiece, BossRoomPiece {
		protected static final BlockState BRICKS = Blocks.BRICKS.defaultBlockState();
		protected static final BlockState POLISHED_CUT_DRIPSTONE = RegistryObject.create(new ResourceLocation("create", "polished_cut_dripstone"), ForgeRegistries.BLOCKS).get().defaultBlockState();
		protected static final BlockState POLISHED_CUT_OCHRUM = RegistryObject.create(new ResourceLocation("create", "polished_cut_ochrum"), ForgeRegistries.BLOCKS).get().defaultBlockState();
		protected static final BlockState OCHRUM_PILLAR = RegistryObject.create(new ResourceLocation("create", "ochrum_pillar"), ForgeRegistries.BLOCKS).get().defaultBlockState();

		protected static final int WIDTH = 37;
		protected static final int HEIGHT = 14;
		protected static final int LENGTH = 15;

		protected static final int OFF_X = 18;
		protected static final int OFF_Y = 1;
		protected static final int OFF_Z = 0;

		protected AbstractEarRoomPiece(StructurePieceType type, int depth, BoundingBox bbox, Direction direction) {
			super(type, depth, bbox);
			this.setOrientation(direction);
		}

		protected AbstractEarRoomPiece(StructurePieceType type, @SuppressWarnings("unused") StructurePieceSerializationContext context, CompoundTag nbt) {
			super(type, nbt);
		}

		@Override
		public void addChildren(StartPiece startPiece, StructurePieceAccessor pieces) {
		}

		private static final ResourceLocation LOOT_TABLE = new ResourceLocation(SurviveInTheWinterFrontier.MODID, "chests/ear");

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
			if(random.nextBoolean()) {
				this.createChest(level, bbox, random, 2, 1, 11, LOOT_TABLE);
			}
		}
	}

	public static final class BoilerRoomPiece extends AbstractEarRoomPiece {
		private static final BlockState CAST_IRON_HULL = RegistryObject.create(new ResourceLocation("createdeco", "cast_iron_hull"), ForgeRegistries.BLOCKS).get().defaultBlockState()
				.setValue(BlockStateProperties.FACING, Direction.DOWN);
		private static final BlockState INDUSTRIAL_IRON_BLOCK = RegistryObject.create(new ResourceLocation("create", "industrial_iron_block"), ForgeRegistries.BLOCKS).get().defaultBlockState();
		private static final BlockState FLUID_PIPE_VERTICAL, FLUID_PIPE_CORNER, FLUID_PIPE_HORIZONTAL;

		static {
			Block fluidPipe = RegistryObject.create(new ResourceLocation("create", "fluid_pipe"), ForgeRegistries.BLOCKS).get();
			BlockState initialFluidPipe = fluidPipe.defaultBlockState()
					.setValue(BlockStateProperties.WEST, false).setValue(BlockStateProperties.EAST, false);
			FLUID_PIPE_VERTICAL = initialFluidPipe
					.setValue(BlockStateProperties.NORTH, false).setValue(BlockStateProperties.SOUTH, false)
					.setValue(BlockStateProperties.UP, true).setValue(BlockStateProperties.DOWN, true);
			FLUID_PIPE_CORNER = initialFluidPipe
					.setValue(BlockStateProperties.NORTH, false).setValue(BlockStateProperties.SOUTH, true)
					.setValue(BlockStateProperties.UP, false).setValue(BlockStateProperties.DOWN, true);
			FLUID_PIPE_HORIZONTAL = initialFluidPipe
					.setValue(BlockStateProperties.NORTH, true).setValue(BlockStateProperties.SOUTH, true)
					.setValue(BlockStateProperties.UP, false).setValue(BlockStateProperties.DOWN, false);
		}

		public BoilerRoomPiece(int depth, BoundingBox bbox, Direction direction) {
			super(BOILER_ROOM_TYPE, depth, bbox, direction);
		}

		public BoilerRoomPiece(StructurePieceSerializationContext context, CompoundTag nbt) {
			super(BOILER_ROOM_TYPE, context, nbt);
		}

		@Override
		public void postProcess(WorldGenLevel level, StructureFeatureManager manager, ChunkGenerator chunk, Random random,
								BoundingBox bbox, ChunkPos chunkPos, BlockPos blockPos) {
			super.postProcess(level, manager, chunk, random, bbox, chunkPos, blockPos);

			//Boilers
			this.createBoiler(level, bbox, 13, 6 + random.nextInt(3));
			this.createBoiler(level, bbox, 17, 6 + random.nextInt(3));
		}

		private void createBoiler(WorldGenLevel level, BoundingBox bbox, int x, int z) {
			this.placeBlock(level, CAST_IRON_HULL, x, 1, z, bbox);
			this.placeBlock(level, CAST_IRON_HULL, x, 2, z, bbox);
			this.placeBlock(level, CAST_IRON_HULL, x, 3, z, bbox);
			this.placeBlock(level, INDUSTRIAL_IRON_BLOCK, x, 4, z, bbox);
			this.placeBlock(level, FLUID_PIPE_VERTICAL, x, 5, z, bbox);
			this.placeBlock(level, FLUID_PIPE_VERTICAL, x, 6, z, bbox);
			this.placeBlock(level, FLUID_PIPE_CORNER, x, 7, z, bbox);
			for(int dz = 1; dz < z - 1; ++dz) {
				this.placeBlock(level, FLUID_PIPE_HORIZONTAL, x, 7, z - dz, bbox);
			}
		}

		@Nullable
		public static BoilerRoomPiece createPiece(StructurePieceAccessor pieces, int x, int y, int z, Direction direction, int depth) {
			BoundingBox boundingbox = BoundingBox.orientBox(x, y, z, -OFF_X, -OFF_Y, -OFF_Z, WIDTH, HEIGHT, LENGTH, direction);
			return isOkBox(boundingbox) && pieces.findCollisionPiece(boundingbox) == null ? new BoilerRoomPiece(depth, boundingbox, direction) : null;
		}
	}

	public static final class BossRoomPiece extends AbstractEarRoomPiece {
		public BossRoomPiece(int depth, BoundingBox bbox, Direction direction) {
			super(BOSS_ROOM_TYPE, depth, bbox, direction);
		}

		public BossRoomPiece(StructurePieceSerializationContext context, CompoundTag nbt) {
			super(BOSS_ROOM_TYPE, context, nbt);
		}

		@Override
		public void postProcess(WorldGenLevel level, StructureFeatureManager manager, ChunkGenerator chunk, Random random,
								BoundingBox bbox, ChunkPos chunkPos, BlockPos blockPos) {
			super.postProcess(level, manager, chunk, random, bbox, chunkPos, blockPos);

		}

		@Nullable
		public static BossRoomPiece createPiece(StructurePieceAccessor pieces, int x, int y, int z, Direction direction, int depth) {
			BoundingBox boundingbox = BoundingBox.orientBox(x, y, z, -OFF_X, -OFF_Y, -OFF_Z, WIDTH, HEIGHT, LENGTH, direction);
			return isOkBox(boundingbox) && pieces.findCollisionPiece(boundingbox) == null ? new BossRoomPiece(depth, boundingbox, direction) : null;
		}
	}

	public static final class StaircasePiece extends AbstractBossLairPiece {
		private static final int WIDTH = 15;
		private static final int HEIGHT = 17;
		private static final int LENGTH = 16;

		private static final int OFF_X = 7;
		private static final int OFF_Y = 1;
		private static final int OFF_Z = 0;

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
					for (int y = dz + 1; y < HEIGHT - (LENGTH / 2) + dz - 1; ++y) {
						this.placeBlock(level, CAVE_AIR, x, y, z, bbox);
					}
					this.placeBlock(level, STONE, x, HEIGHT - (LENGTH / 2) + dz - 1, z, bbox);
				}
			}
		}

		@Nullable
		public static StaircasePiece createPiece(StructurePieceAccessor pieces, int x, int y, int z, Direction direction, int depth) {
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
