package com.hexagram2021.misc_twf.server;

import com.google.common.collect.Maps;
import com.hexagram2021.misc_twf.common.util.MISCTWFLogger;
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

	private final Map<UUID, VaccineContent> immunityAgainstZombification;

	public MISCTWFSavedData() {
		super();
		this.immunityAgainstZombification = Maps.newHashMap();
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
	}

	@Override
	public CompoundTag save(CompoundTag nbt) {
		ListTag list = new ListTag();
		this.immunityAgainstZombification.forEach((uuid, content) -> {
			CompoundTag tag = new CompoundTag();
			tag.putUUID(TAG_ID, uuid);
			tag.put(TAG_CONTENT, content.save());
			list.add(tag);
		});
		nbt.put(TAG_IMMUNITY, list);
		return nbt;
	}

	public static void setImmuneToZombification(UUID uuid, int time) {
		if(INSTANCE == null) {
			MISCTWFLogger.warn("Ignore trying to set immunity for uuid " + uuid + " as saved data is not loaded.");
			return;
		}
		INSTANCE.immunityAgainstZombification.put(uuid, new VaccineContent(time));
	}

	public static boolean isImmuneToZombification(UUID uuid) {
		if(INSTANCE == null) {
			MISCTWFLogger.warn("Ignore trying to get immunity for uuid " + uuid + " as saved data is not loaded.");
			return false;
		}
		return INSTANCE.immunityAgainstZombification.containsKey(uuid);
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
