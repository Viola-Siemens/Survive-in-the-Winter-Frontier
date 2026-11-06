package com.hexagram2021.misc_twf_adventure.common.block.entity;

import com.hexagram2021.misc_twf_adventure.common.block.MonsterEggBlock;
import com.hexagram2021.misc_twf_adventure.common.data_component.MonsterEggEntries;
import com.hexagram2021.misc_twf_adventure.common.network.ClientboundMonsterEggAnimationPacket;
import com.hexagram2021.misc_twf_adventure.common.register.MISCTWFBlockEntities;
import com.hexagram2021.misc_twf_adventure.common.register.MISCTWFBlockTags;
import com.hexagram2021.misc_twf_adventure.common.register.MISCTWFDataComponentTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * 怪物蛋方块实体类喵~
 * 核心功能包括：
 * 1. 存储和管理可生成的怪物类型及其权重喵~
 * 2. 定期检测周围 8 格范围内带有"曝光"效果的生物实体喵~
 * 3. 根据"曝光"效果的等级和持续时间，按概率破裂并生成怪物喵~
 * 4. 使用声音传播机制检测，会被"隔音屏障"方块阻挡喵~
 * 5. 支持 GeckoLib 动画，当检测到目标时播放震动动画喵~
 *
 * @author liudongyu
 */
public class MonsterEggBlockEntity extends BlockEntity implements GeoBlockEntity {
	protected WeightedRandomList<MonsterEggEntries.MonsterEggEntry> entries = WeightedRandomList.create();
	private final AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);
	/** 已加载的怪物蛋实例（按维度分组，仅服务端使用）喵~ */
	private static final Map<Level, Set<MonsterEggBlockEntity>> LOADED_EGGS = new WeakHashMap<>();
	/** 最近一次"听到"的等效强度（1~3，对应原曝光等级 +1）喵~ */
	private int heardStrength;
	/** 最近一次听到噪声的时间（tick）喵~ */
	private long lastHeardTick = Long.MIN_VALUE;
	/** 最近一次听到的噪声位置（用于隔声屏障判定）喵~ */
	@Nullable
	private Vec3 lastHeardPos;
	private boolean needReload;
	private boolean needPlay;
	private static final String CONTROLLER_NAME = "controller";

	/**
	 * 构造怪物蛋方块实体喵~
	 *
	 * @param blockPos 方块位置喵~
	 * @param blockState 方块状态喵~
	 */
	public MonsterEggBlockEntity(BlockPos blockPos, BlockState blockState) {
		this(MISCTWFBlockEntities.MONSTER_EGG.get(), blockPos, blockState);
	}

	/**
	 * 构造指定类型的怪物蛋方块实体喵~
	 *
	 * @param type 方块实体类型喵~
	 * @param blockPos 方块位置喵~
	 * @param blockState 方块状态喵~
	 */
	public MonsterEggBlockEntity(BlockEntityType<MonsterEggBlockEntity> type, BlockPos blockPos, BlockState blockState) {
		super(type, blockPos, blockState);
	}

	/**
	 * 根据配置的怪物生成表随机创建一个怪物实体喵~
	 *
	 * @param level 世界对象喵~
	 * @return 创建的怪物实体，如果生成表为空则返回 null 喵~
	 */
	@Nullable
	public Entity createMonster(Level level) {
		return this.entries.getRandom(level.random).map(entry -> entry.type().create(level)).orElse(null);
	}

	/**
	 * 设置怪物生成表喵~
	 *
	 * @param entries 带权重的怪物生成条目列表喵~
	 */
	public void setEntries(WeightedRandomList<MonsterEggEntries.MonsterEggEntry> entries) {
		this.entries = entries;
	}

	/**
	 * 从物品堆叠的 NBT 数据中加载怪物蛋的配置喵~
	 *
	 * @param itemStack 包含 NBT 数据的物品堆叠喵~
	 */
	public void fromItem(ItemStack itemStack) {
		MonsterEggEntries monsterEggEntries = itemStack.get(MISCTWFDataComponentTypes.MONSTER_EGG_ENTRIES);
		if(monsterEggEntries == null) {
			this.setEntries(WeightedRandomList.create());
		} else {
			this.setEntries(WeightedRandomList.create(monsterEggEntries.entries()));
		}
	}

	@Override
	public void loadAdditional(CompoundTag nbt, HolderLookup.Provider provider) {
		super.loadAdditional(nbt, provider);
		if (nbt.contains("entries", Tag.TAG_LIST)) {
			this.setEntries(WeightedRandomList.create(
					MonsterEggEntries.MonsterEggEntry.CODEC.listOf().parse(NbtOps.INSTANCE, nbt.getList("entries", Tag.TAG_COMPOUND)).getOrThrow()
			));
		}
	}

	@Override
	protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider provider) {
		super.saveAdditional(nbt, provider);
		nbt.put("entries", MonsterEggEntries.MonsterEggEntry.CODEC.listOf().encode(this.entries.unwrap(), NbtOps.INSTANCE, new ListTag()).getOrThrow());
	}

	/**
	 * 服务端刻更新逻辑喵~
	 * 每 5 游戏刻执行一次，检测周围带"暴露"效果的生物实体喵~
	 * 如果检测到符合条件的实体，根据效果等级计算破裂概率，并向客户端发送动画数据包喵~
	 * 使用声音传播算法，会被"隔音屏障"方块阻挡喵~
	 *
	 * @param level 世界对象喵~
	 * @param blockPos 方块位置喵~
	 * @param blockState 方块状态喵~
	 * @param monsterEgg 怪物蛋方块实体喵~
	 */
	public static void serverTick(Level level, BlockPos blockPos, BlockState blockState, MonsterEggBlockEntity monsterEgg) {
		long gameTime = level.getGameTime();
		if(gameTime % 5 != 0) {
			return;
		}
		monsterEgg.decayHeardStrength(gameTime);
		if(monsterEgg.heardStrength <= 0 || monsterEgg.lastHeardPos == null) {
			return;
		}
		// 噪声传播会被隔声屏障方块阻挡喵~
		if(!blockCheck(level, Vec3.atCenterOf(blockPos), monsterEgg.lastHeardPos)) {
			return;
		}
		if(level.random.nextDouble() < monsterEgg.heardStrength * 0.2D) {
			if(blockState.getBlock() instanceof MonsterEggBlock monsterEggBlock) {
				// 由噪声触发破壳：entity 传 null 表示非实体直接触发喵~
				monsterEggBlock.destroyEgg(level, blockPos, null);
			}
			monsterEgg.heardStrength = 0;
			monsterEgg.lastHeardPos = null;
			return;
		}
		broadcastEggAnimation(level, blockPos);
	}

	/**
	 * 由循声事件驱动：把附近的噪声写入怪物蛋的听声强度喵~
	 *
	 * @param level    服务端维度喵~
	 * @param position 噪声位置喵~
	 * @param strength 噪声强度（0~1）喵~
	 */
	public static void onNoiseHeard(ServerLevel level, Vec3 position, float strength) {
		Set<MonsterEggBlockEntity> eggs = LOADED_EGGS.get(level);
		if(eggs == null || eggs.isEmpty()) {
			return;
		}
		double radius = com.hexagram2021.misc_twf_adventure.common.config.MISCTWFCommonConfig.MONSTER_EGG_HEARING_RADIUS.get();
		double radiusSqr = radius * radius;
		for(MonsterEggBlockEntity egg : new HashSet<>(eggs)) {
			if(egg.isRemoved() || egg.level != level) {
				continue;
			}
			Vec3 center = Vec3.atCenterOf(egg.getBlockPos());
			if(center.distanceToSqr(position) > radiusSqr) {
				continue;
			}
			if(!blockCheck(level, center, position)) {
				continue;
			}
			egg.hear(strength, position);
		}
	}

	/**
	 * 记录一次听到的噪声：按强度映射到 1~3 的等效等级并刷新衰减计时喵~
	 *
	 * @param strength 噪声强度（0~1）喵~
	 * @param position 噪声位置喵~
	 */
	private void hear(float strength, Vec3 position) {
		int mapped = strength >= 0.9F ? 3 : (strength >= 0.6F ? 2 : 1);
		this.heardStrength = Math.max(this.heardStrength, mapped);
		this.lastHeardTick = this.level == null ? 0L : this.level.getGameTime();
		this.lastHeardPos = position;
		this.needReload = true;
	}

	/**
	 * 按配置间隔逐步衰减听声强度喵~
	 *
	 * @param gameTime 当前游戏时间（tick）喵~
	 */
	private void decayHeardStrength(long gameTime) {
		if(this.heardStrength <= 0) {
			return;
		}
		long decayTicks = com.hexagram2021.misc_twf_adventure.common.config.MISCTWFCommonConfig.MONSTER_EGG_HEARING_DECAY_TICKS.get();
		if(this.lastHeardTick != Long.MIN_VALUE && gameTime - this.lastHeardTick >= decayTicks) {
			this.heardStrength--;
			this.lastHeardTick = gameTime;
		}
	}

	/**
	 * 向附近玩家广播怪物蛋的震动动画喵~
	 *
	 * @param level 世界对象喵~
	 * @param pos   方块位置喵~
	 */
	private static void broadcastEggAnimation(Level level, BlockPos pos) {
		if(level instanceof ServerLevel serverLevel) {
			PacketDistributor.sendToPlayersNear(serverLevel, null,
					pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 32.0D,
					new ClientboundMonsterEggAnimationPacket(pos.getX(), pos.getY(), pos.getZ()));
		}
	}

	@Override
	public void onLoad() {
		super.onLoad();
		if(this.level != null) {
			LOADED_EGGS.computeIfAbsent(this.level, ignored -> new HashSet<>()).add(this);
		}
	}

	@Override
	public void setRemoved() {
		if(this.level != null) {
			Set<MonsterEggBlockEntity> eggs = LOADED_EGGS.get(this.level);
			if(eggs != null) {
				eggs.remove(this);
				if(eggs.isEmpty()) {
					LOADED_EGGS.remove(this.level);
				}
			}
		}
		super.setRemoved();
	}

	private static boolean blockCheck(Level level, Vec3 startPos, Vec3 endPos) {
		Vec3 direction = endPos.subtract(startPos);
		double distanceSqr = direction.lengthSqr();
		direction = direction.normalize().scale(0.5);
		Vec3 currentPos = startPos;
		for (int i = 0; i * i / 4D < distanceSqr; i++) {
			currentPos = currentPos.add(direction);
			BlockPos blockPos = BlockPos.containing(currentPos);
			BlockState blockState = level.getBlockState(blockPos);
			if (blockState.is(MISCTWFBlockTags.SOUND_BARRIER)) {
				int count = 0;
				for (Direction direction1 : Direction.values()) {
					if (level.getBlockState(blockPos.relative(direction1)).is(MISCTWFBlockTags.SOUND_BARRIER))
						count++;
				}
				if (count >= 2)
					return false;
			}

		}
		return true;
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
		controllers.add(new AnimationController<>(this, CONTROLLER_NAME, 0, this::predicate));
	}

	private PlayState predicate(AnimationState<MonsterEggBlockEntity> animationEvent) {
		if (this.needReload) {
			animationEvent.getController().forceAnimationReset();
			this.needReload = false;
			this.needPlay = true;
			return PlayState.STOP;
		} else if (this.needPlay) {
			animationEvent.getController().setAnimationSpeed(2);
			this.needPlay = false;
			animationEvent.getController().setAnimation(RawAnimation.begin().thenPlay("shake"));
		}
		return PlayState.CONTINUE;
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return this.factory;
	}

	/**
	 * 刷新动画状态，强制重新播放震动动画喵~
	 * 由客户端接收到动画数据包后调用喵~
	 */
	public void refreshAnimation() {
		this.needReload = true;
	}

	/**
	 * 声音数据记录，用于缓存已检测过的实体的"曝光"效果信息喵~
	 *
	 * @param amplifier 效果等级喵~
	 * @param duration 效果持续时间喵~
	 * @param gameTime 记录时的游戏时间喵~
	 */
	record SoundData(int amplifier, int duration, long gameTime) {
	}
}
