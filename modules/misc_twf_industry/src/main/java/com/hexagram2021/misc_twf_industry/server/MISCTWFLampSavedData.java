package com.hexagram2021.misc_twf_industry.server;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.hexagram2021.misc_twf_industry.common.config.MISCTWFIndustryConfig;
import com.hexagram2021.misc_twf_industry.common.util.MISCTWFLogger;
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
import java.util.function.Function;

/**
 * 工业生态模块紫外线灯坐标存档（决策 7.2：从原单体存档拆出）喵~
 *
 * <p>使用 KD 树存储紫外线灯坐标以支持高效的最近邻查询喵~</p>
 *
 * @author liudongyu
 */
public class MISCTWFLampSavedData extends SavedData {
	/** 已注册的维度集合喵~ */
	public static final Set<ResourceLocation> dimensions = Sets.newHashSet(
			Level.OVERWORLD.location(), Level.NETHER.location(), Level.END.location()
	);
	private static final Function<ResourceLocation, KDTree<BlockPos, Integer>> computeFunction = k -> KDTree.newLinkedKDTree(3);

	@Nullable
	private static MISCTWFLampSavedData INSTANCE;

	/** 存档数据名称（带模块标识）喵~ */
	public static final String SAVED_DATA_NAME = "MiscTWF-Industry";

	private static final String TAG_LAMPS = "lamps";
	private static final String TAG_POSITION = "position";

	private final Map<ResourceLocation, KDTree<BlockPos, Integer>> lampPositions;

	/**
	 * 无参构造函数
	 */
	public MISCTWFLampSavedData() {
		super();
		this.lampPositions = Maps.newHashMap();
	}

	/**
	 * 从 NBT 数据反序列化存档喵~
	 *
	 * @param nbt      NBT 复合标签喵~
	 * @param provider 注册表查找提供者喵~
	 */
	public MISCTWFLampSavedData(CompoundTag nbt, @SuppressWarnings("unused") HolderLookup.Provider provider) {
		this();
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
	 * 记录紫外线灯的放置位置喵~
	 *
	 * @param globalPos 全局坐标（含维度信息）喵~
	 */
	public static void placeLamp(GlobalPos globalPos) {
		BlockPos blockPos = globalPos.pos();
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
		return closest.distanceWith(target) <= MISCTWFIndustryConfig.ULTRAVIOLET_LAMPS_RADIUS.get();
	}

	/**
	 * 设置存档数据单例实例喵~
	 *
	 * @param in 存档数据实例喵~
	 */
	public static void setInstance(MISCTWFLampSavedData in) {
		INSTANCE = in;
	}
}
