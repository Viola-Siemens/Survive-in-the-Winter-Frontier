package com.hexagram2021.misc_twf.common.block.entity;

import com.google.common.collect.Maps;
import com.hexagram2021.misc_twf.common.block.MonsterEggBlock;
import com.hexagram2021.misc_twf.common.data_component.MonsterEggEntries;
import com.hexagram2021.misc_twf.common.network.ClientboundMonsterEggAnimationPacket;
import com.hexagram2021.misc_twf.common.register.MISCTWFBlockEntities;
import com.hexagram2021.misc_twf.common.register.MISCTWFBlockTags;
import com.hexagram2021.misc_twf.common.register.MISCTWFDataComponentTypes;
import com.scarasol.sona.init.SonaMobEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
	private final Map<UUID, SoundData> soundData = Maps.newHashMap();
	private final AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);
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
		if (gameTime % 5 == 0) {
			monsterEgg.soundData.entrySet().removeIf(entry ->
					gameTime - entry.getValue().gameTime > entry.getValue().duration);
			List<LivingEntity> livingEntities = level.getEntitiesOfClass(LivingEntity.class, AABB.ofSize(blockPos.getBottomCenter(), 8.0D, 4.0D, 8.0D), livingEntity -> livingEntity.hasEffect(SonaMobEffects.EXPOSURE.get()));
			boolean flag = !livingEntities.isEmpty();
			for(LivingEntity livingEntity: livingEntities) {
				MobEffectInstance mobEffectInstance = livingEntity.getEffect(SonaMobEffects.EXPOSURE.get());
				if(mobEffectInstance != null) {
					int amplifier = mobEffectInstance.getAmplifier();
					int duration = mobEffectInstance.getDuration();
					if (monsterEgg.soundData.containsKey(livingEntity.getUUID())) {
						SoundData soundData = monsterEgg.soundData.get(livingEntity.getUUID());
						if (amplifier == soundData.amplifier() && duration < soundData.duration())
							return;
					}
					if (blockCheck(level, Vec3.atCenterOf(blockPos), livingEntity.position())) {
						double token = level.random.nextDouble();
						if (token < (amplifier + 1) * 0.2) {
							if (blockState.getBlock() instanceof MonsterEggBlock monsterEggBlock) {
								monsterEggBlock.destroyEgg(level, blockPos, livingEntity);
								flag = false;
							}
						}
					}
					monsterEgg.soundData.put(livingEntity.getUUID(), new SoundData(amplifier, duration, gameTime));
				}
			}
			if (flag) {
				level.getEntitiesOfClass(ServerPlayer.class, AABB.ofSize(blockPos.getBottomCenter(), 32.0D, 16.0D, 32.0D))
						.forEach(player -> player.connection.send(new ClientboundMonsterEggAnimationPacket(blockPos.getX(), blockPos.getY(), blockPos.getZ())));
			}
		}
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
