package com.hexagram2021.misc_twf_zombie_animals.server;

import com.google.common.collect.Maps;
import com.hexagram2021.misc_twf_zombie_animals.common.util.MISCTWFLogger;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.saveddata.SavedData;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.UUID;

/**
 * 僵尸动物模块免疫存档（决策 D4：免疫是“僵尸化”概念的属主数据，随本模块）喵~
 *
 * <p>持久化疫苗对实体的“僵尸化免疫”记录；供本模块与冒险模块的疫苗（通过公开 API，方向 M4 → M1）读写喵~</p>
 *
 * @author liudongyu
 */
public class MISCTWFImmunitySavedData extends SavedData {
	/** 存档数据名称（带模块标识，与其它模块存档隔离）喵~ */
	public static final String SAVED_DATA_NAME = "MiscTWF-ZombieAnimals";

	private static final String TAG_IMMUNITY = "immunity";
	private static final String TAG_ID = "id";
	private static final String TAG_CONTENT = "content";

	@Nullable
	private static MISCTWFImmunitySavedData INSTANCE;

	private final Map<UUID, VaccineContent> immunityAgainstZombification;

	/**
	 * 免疫存储数据
	 */
	public MISCTWFImmunitySavedData() {
		super();
		this.immunityAgainstZombification = Maps.newHashMap();
	}

	/**
	 * 从 NBT 数据反序列化存档喵~
	 *
	 * @param nbt      NBT 复合标签喵~
	 * @param provider 注册表查找提供者喵~
	 */
	public MISCTWFImmunitySavedData(CompoundTag nbt, @SuppressWarnings("unused") HolderLookup.Provider provider) {
		this();
		if(nbt.contains(TAG_IMMUNITY, Tag.TAG_LIST)) {
			ListTag list = nbt.getList(TAG_IMMUNITY, Tag.TAG_COMPOUND);
			for(Tag tag: list) {
				CompoundTag compoundTag = (CompoundTag)tag;
				// TODO 原单体实现此处误读外层 nbt（疑似笔误），拆分时修正为逐条读取
				this.immunityAgainstZombification.put(compoundTag.getUUID(TAG_ID), new VaccineContent(compoundTag.getCompound(TAG_CONTENT)));
			}
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
		return nbt;
	}

	/**
	 * 设置指定 UUID 的实体对僵尸化的免疫喵~
	 *
	 * @param uuid 实体 UUID 喵~
	 * @param time 记录时间（tick），沿用原实现的记录语义喵~
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
	 * 设置存档数据单例实例喵~
	 *
	 * @param in 存档数据实例喵~
	 */
	public static void setInstance(MISCTWFImmunitySavedData in) {
		INSTANCE = in;
	}

	/**
	 * 疫苗免疫内容，记录免疫附加时刻（沿用原单体语义，实际有效期逻辑待运行时确认）喵~
	 */
	public static class VaccineContent {
		/** 记录时刻（tick），-1 表示永久喵~ */
		public final int time;

		/**
		 * 通过时间值构造疫苗内容喵~
		 *
		 * @param time 记录时间（tick）喵~
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
