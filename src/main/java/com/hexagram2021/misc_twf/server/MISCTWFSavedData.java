package com.hexagram2021.misc_twf.server;

import com.google.common.collect.Maps;
import com.hexagram2021.misc_twf.common.config.MISCTWFCommonConfig;
import com.hexagram2021.misc_twf.common.util.MISCTWFLogger;
import com.hexagram2021.tetrachordlib.core.container.IMultidimensional;
import com.hexagram2021.tetrachordlib.core.container.KDTree;
import com.hexagram2021.tetrachordlib.vanilla.MDUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.saveddata.SavedData;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.UUID;

public class MISCTWFSavedData extends SavedData {
	@Nullable
	private static MISCTWFSavedData INSTANCE;

	public static final String SAVED_DATA_NAME = "MiscTWF-SavedData";

	private static final String TAG_IMMUNITY = "immunity";
	private static final String TAG_ID = "id";
	private static final String TAG_CONTENT = "content";

	private static final String TAG_LAMPS = "lamps";
	private static final String TAG_POSITION = "position";

	private final Map<UUID, VaccineContent> immunityAgainstZombification;
	private final KDTree<BlockPos, Integer> lampPositions;

	public MISCTWFSavedData() {
		super();
		this.immunityAgainstZombification = Maps.newHashMap();
		this.lampPositions = KDTree.newLinkedKDTree(3);
	}

	public MISCTWFSavedData(CompoundTag nbt) {
		this();
		if(nbt.contains(TAG_IMMUNITY, Tag.TAG_LIST)) {
			ListTag list = nbt.getList(TAG_IMMUNITY, Tag.TAG_COMPOUND);
			for(Tag tag: list) {
				CompoundTag compoundTag = (CompoundTag)tag;
				this.immunityAgainstZombification.put(compoundTag.getUUID(TAG_ID), new VaccineContent(nbt.getCompound(TAG_CONTENT)));
			}
		}
		if(nbt.contains(TAG_LAMPS, Tag.TAG_LIST)) {
			ListTag list = nbt.getList(TAG_LAMPS, Tag.TAG_COMPOUND);
			@SuppressWarnings("unchecked")
			KDTree.BuildNode<BlockPos, Integer>[] buildNodes = list.stream().map(tag -> {
				CompoundTag compoundTag = (CompoundTag)tag;
				BlockPos blockPos = BlockPos.of(compoundTag.getLong(TAG_POSITION));
				return new KDTree.BuildNode<>(MDUtils.vec3i(blockPos), blockPos);
			}).toArray(KDTree.BuildNode[]::new);
			this.lampPositions.build(buildNodes);
		}
	}

	@Override
	public CompoundTag save(CompoundTag nbt) {
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

		ListTag lamps = new ListTag();
		synchronized (this.lampPositions) {
			this.lampPositions.inDfs((blockPos, intPosition) -> {
				CompoundTag tag = new CompoundTag();
				tag.putLong(TAG_POSITION, blockPos.asLong());
				lamps.add(tag);
			});
		}
		nbt.put(TAG_LAMPS, lamps);

		return nbt;
	}

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
	public static boolean isImmuneToZombification(UUID uuid) {
		if(INSTANCE == null) {
			MISCTWFLogger.warn("Ignore trying to get immunity for uuid " + uuid + " as saved data is not loaded.");
			return false;
		}
		return INSTANCE.immunityAgainstZombification.containsKey(uuid);
	}

	public static void placeLamp(BlockPos blockPos) {
		if(INSTANCE == null) {
			MISCTWFLogger.warn("Ignore trying to place lamp at (" + blockPos.toShortString() + ") as saved data is not loaded.");
			return;
		}
		INSTANCE.lampPositions.insert(KDTree.BuildNode.of(blockPos, MDUtils.vec3i(blockPos)));
		INSTANCE.setDirty();
	}
	public static void destroyLamp(BlockPos blockPos) {
		if(INSTANCE == null) {
			MISCTWFLogger.warn("Ignore trying to destroy lamp at (" + blockPos.toShortString() + ") as saved data is not loaded.");
			return;
		}
		if(INSTANCE.lampPositions.remove(MDUtils.vec3i(blockPos)) == null) {
			MISCTWFLogger.warn("Ignore trying to destroy lamp at (" + blockPos.toShortString() + ") as the target block is not exists in the container.");
		}
		INSTANCE.setDirty();
	}
	public static boolean denyMonsterSpawn(BlockPos blockPos) {
		if(INSTANCE == null) {
			MISCTWFLogger.warn("Ignore trying to query lamp on (" + blockPos.toShortString() + ") as saved data is not loaded.");
			return false;
		}
		if(INSTANCE.lampPositions.isEmpty()) {
			return false;
		}
		IMultidimensional<Integer> target = MDUtils.vec3i(blockPos);
		IMultidimensional<Integer> closest = INSTANCE.lampPositions.findClosest(target).value();
		return closest.distanceWith(target) <= MISCTWFCommonConfig.ULTRAVIOLET_LAMPS_RADIUS.get();
	}

	public static void setInstance(MISCTWFSavedData in) {
		INSTANCE = in;
	}

	public static class VaccineContent {
		public final int time;

		public VaccineContent(int time) {
			this.time = time;
		}

		public VaccineContent(CompoundTag nbt) {
			if(nbt.contains("time")) {
				this.time = nbt.getInt("time");
			} else {
				this.time = -1;
			}
		}

		public CompoundTag save() {
			CompoundTag nbt = new CompoundTag();
			nbt.putInt("time", this.time);
			return nbt;
		}
	}
}
