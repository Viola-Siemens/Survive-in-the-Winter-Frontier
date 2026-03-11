package com.hexagram2021.misc_twf.server;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.hexagram2021.misc_twf.common.config.MISCTWFCommonConfig;
import com.hexagram2021.misc_twf.common.util.MISCTWFLogger;
import com.hexagram2021.tetrachordlib.core.container.IMultidimensional;
import com.hexagram2021.tetrachordlib.core.container.KDTree;
import com.hexagram2021.tetrachordlib.vanilla.MDUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

/**
 * 模组存档数据，用于持久化存储疫苗免疫信息和紫外线灯位置喵~
 * <br/>
 * 使用 KD 树存储紫外线灯坐标以支持高效的最近邻查询喵~
 *
 * @author liudongyu
 */
public class MISCTWFSavedData extends SavedData {
	/** 已注册的维度集合喵~ */
	public static final Set<ResourceLocation> dimensions = Sets.newHashSet(
			Level.OVERWORLD.location(), Level.NETHER.location(), Level.END.location()
	);
	private static final Function<ResourceLocation, KDTree<BlockPos, Integer>> computeFunction = k -> KDTree.newLinkedKDTree(3);

	@Nullable
	private static MISCTWFSavedData INSTANCE;

	/** 存档数据名称喵~ */
	public static final String SAVED_DATA_NAME = "MiscTWF-SavedData";

	private static final String TAG_IMMUNITY = "immunity";
	private static final String TAG_ID = "id";
	private static final String TAG_CONTENT = "content";

	private static final String TAG_LAMPS = "lamps";
	private static final String TAG_POSITION = "position";

	private final Map<UUID, VaccineContent> immunityAgainstZombification;
	private final Map<ResourceLocation, KDTree<BlockPos, Integer>> lampPositions;

	public MISCTWFSavedData() {
		super();
		this.immunityAgainstZombification = Maps.newHashMap();
		this.lampPositions = Maps.newHashMap();
	}

	/**
	 * 从 NBT 数据反序列化存档数据喵~
	 *
	 * @param nbt      NBT 复合标签喵~
	 * @param provider 注册表查找提供者喵~
	 */
	public MISCTWFSavedData(CompoundTag nbt, @SuppressWarnings("unused") HolderLookup.Provider provider) {
		this();
		if(nbt.contains(TAG_IMMUNITY, Tag.TAG_LIST)) {
			ListTag list = nbt.getList(TAG_IMMUNITY, Tag.TAG_COMPOUND);
			for(Tag tag: list) {
				CompoundTag compoundTag = (CompoundTag)tag;
				this.immunityAgainstZombification.put(compoundTag.getUUID(TAG_ID), new VaccineContent(nbt.getCompound(TAG_CONTENT)));
			}
		}
		if(nbt.contains(TAG_LAMPS, Tag.TAG_COMPOUND)) {
			CompoundTag lamps = nbt.getCompound(TAG_LAMPS);
			dimensions.forEach(dimension -> {
				String dimensionKey = dimension.toString();
				if(lamps.contains(dimensionKey, Tag.TAG_LIST)) {
					ListTag list = lamps.getList(dimensionKey, Tag.TAG_COMPOUND);
					@SuppressWarnings("unchecked")
					KDTree.BuildNode<BlockPos, Integer>[] buildNodes = list.stream().map(tag -> {
						CompoundTag compoundTag = (CompoundTag)tag;
						BlockPos blockPos = BlockPos.of(compoundTag.getLong(TAG_POSITION));
						return new KDTree.BuildNode<>(MDUtils.vec3i(blockPos), blockPos);
					}).toArray(KDTree.BuildNode[]::new);
					this.lampPositions.computeIfAbsent(dimension, computeFunction).build(buildNodes);
				}
			});
		}
	}

	@Override
	public CompoundTag save(CompoundTag nbt, HolderLookup.Provider provider) {
		ListTag immunity = new ListTag();
		synchronized (this.immunityAgainstZombification) {
			this.immunityAgainstZombification.forEach((uuid, content) -> {
				CompoundTag tag = new CompoundTag();
				tag.putUUID(TAG_ID, uuid);
				tag.put(TAG_CONTENT, content.save());
				immunity.add(tag);
			});
		}
		nbt.put(TAG_IMMUNITY, immunity);

		synchronized (this.lampPositions) {
			CompoundTag lamps = new CompoundTag();
			this.lampPositions.forEach((dimension, tree) -> {
				ListTag list = new ListTag();
				tree.inDfs((blockPos, intPosition) -> {
					CompoundTag tag = new CompoundTag();
					tag.putLong(TAG_POSITION, blockPos.asLong());
					list.add(tag);
				});
				lamps.put(dimension.toString(), list);
			});
			nbt.put(TAG_LAMPS, lamps);
		}

		return nbt;
	}

	/**
	 * 设置指定 UUID 的实体对僵尸化的免疫时间喵~
	 *
	 * @param uuid 实体 UUID 喵~
	 * @param time 免疫时间（tick）喵~
	 */
	public static void setImmuneToZombification(UUID uuid, int time) {
		if(INSTANCE == null) {
			MISCTWFLogger.warn("Ignore trying to set immunity for uuid " + uuid + " as saved data is not loaded.");
			return;
		}
		synchronized (INSTANCE.immunityAgainstZombification) {
			INSTANCE.immunityAgainstZombification.put(uuid, new VaccineContent(time));
		}
		INSTANCE.setDirty();
	}
	/**
	 * 检查指定 UUID 的实体是否对僵尸化免疫喵~
	 *
	 * @param uuid 实体 UUID 喵~
	 * @return 是否免疫喵~
	 */
	public static boolean isImmuneToZombification(UUID uuid) {
		if(INSTANCE == null) {
			MISCTWFLogger.warn("Ignore trying to get immunity for uuid " + uuid + " as saved data is not loaded.");
			return false;
		}
		return INSTANCE.immunityAgainstZombification.containsKey(uuid);
	}

	/**
	 * 记录紫外线灯的放置位置喵~
	 *
	 * @param globalPos 全局坐标（含维度信息）喵~
	 */
	public static void placeLamp(GlobalPos globalPos) {
		BlockPos blockPos = globalPos.pos();
//		MISCTWFLogger.debug("Place lamp at " + globalPos.dimension().location() + " (" + blockPos.toShortString() + ").");
		if(INSTANCE == null) {
			MISCTWFLogger.warn("Ignore trying to place lamp at (" + blockPos.toShortString() + ") as saved data is not loaded.");
			return;
		}
		INSTANCE.lampPositions.computeIfAbsent(globalPos.dimension().location(), computeFunction)
				.insert(KDTree.BuildNode.of(blockPos, MDUtils.vec3i(blockPos)));
		INSTANCE.setDirty();
	}
	/**
	 * 移除紫外线灯的放置记录喵~
	 *
	 * @param globalPos 全局坐标（含维度信息）喵~
	 */
	public static void destroyLamp(GlobalPos globalPos) {
		BlockPos blockPos = globalPos.pos();
//		MISCTWFLogger.debug("Destroy lamp at " + globalPos.dimension().location() + " (" + blockPos.toShortString() + ").");
		if(INSTANCE == null) {
			MISCTWFLogger.warn("Ignore trying to destroy lamp at (" + blockPos.toShortString() + ") as saved data is not loaded.");
			return;
		}
		KDTree<BlockPos, Integer> dimensionKDT = INSTANCE.lampPositions.computeIfAbsent(globalPos.dimension().location(), computeFunction);
		if(dimensionKDT.isEmpty() || dimensionKDT.remove(MDUtils.vec3i(blockPos)) == null) {
			MISCTWFLogger.warn("Ignore trying to destroy lamp at (" + blockPos.toShortString() + ") as the target block is not exists in the container.");
		}
		INSTANCE.setDirty();
	}
	/**
	 * 判断指定位置是否在紫外线灯的有效范围内，以阻止怪物生成喵~
	 *
	 * @param globalPos 全局坐标（含维度信息）喵~
	 * @return 是否应阻止怪物生成喵~
	 */
	public static boolean denyMonsterSpawn(GlobalPos globalPos) {
		BlockPos blockPos = globalPos.pos();
//		MISCTWFLogger.debug("Query lamp at " + globalPos.dimension().location() + " (" + blockPos.toShortString() + ").");
		if(INSTANCE == null) {
			MISCTWFLogger.warn("Ignore trying to query lamp on (" + blockPos.toShortString() + ") as saved data is not loaded.");
			return false;
		}
		KDTree<BlockPos, Integer> dimensionKDT = INSTANCE.lampPositions.get(globalPos.dimension().location());
		if(dimensionKDT == null || dimensionKDT.isEmpty()) {
			return false;
		}
		IMultidimensional<Integer> target = MDUtils.vec3i(blockPos);
		IMultidimensional<Integer> closest = dimensionKDT.findClosest(target).value();
		return closest.distanceWith(target) <= MISCTWFCommonConfig.ULTRAVIOLET_LAMPS_RADIUS.get();
	}

	/**
	 * 设置存档数据单例实例喵~
	 *
	 * @param in 存档数据实例喵~
	 */
	public static void setInstance(MISCTWFSavedData in) {
		INSTANCE = in;
	}

	/**
	 * 疫苗免疫内容，记录免疫持续时间喵~
	 */
	public static class VaccineContent {
		/** 免疫持续时间（tick），-1 表示永久喵~ */
		public final int time;

		/**
		 * 通过时间值构造疫苗内容喵~
		 *
		 * @param time 免疫时间（tick）喵~
		 */
		public VaccineContent(int time) {
			this.time = time;
		}

		/**
		 * 从 NBT 数据反序列化疫苗内容喵~
		 *
		 * @param nbt NBT 复合标签喵~
		 */
		public VaccineContent(CompoundTag nbt) {
			if(nbt.contains("time")) {
				this.time = nbt.getInt("time");
			} else {
				this.time = -1;
			}
		}

		/**
		 * 将疫苗内容序列化为 NBT 数据喵~
		 *
		 * @return 序列化后的 NBT 复合标签喵~
		 */
		public CompoundTag save() {
			CompoundTag nbt = new CompoundTag();
			nbt.putInt("time", this.time);
			return nbt;
		}
	}
}
