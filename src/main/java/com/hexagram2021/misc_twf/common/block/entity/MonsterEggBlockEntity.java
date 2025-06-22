package com.hexagram2021.misc_twf.common.block.entity;

import com.google.common.collect.Maps;
import com.hexagram2021.misc_twf.SurviveInTheWinterFrontier;
import com.hexagram2021.misc_twf.common.block.MonsterEggBlock;
import com.hexagram2021.misc_twf.common.network.MonsterEggAnimationPacket;
import com.hexagram2021.misc_twf.common.register.MISCTWFBlockEntities;
import com.hexagram2021.misc_twf.common.register.MISCTWFBlockTags;
import com.hexagram2021.misc_twf.common.util.MISCTWFLogger;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.scarasol.sona.init.SonaMobEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.random.Weight;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class MonsterEggBlockEntity extends BlockEntity implements IAnimatable {
    protected WeightedRandomList<MonsterEggEntry> entries = WeightedRandomList.create();
    private final Map<UUID, SoundData> soundData = Maps.newHashMap();
    public AnimationFactory factory = GeckoLibUtil.createFactory(this);
    private boolean needReload;
    private boolean needPlay;
    private static final String CONTROLLER_NAME = "controller";

    public MonsterEggBlockEntity(BlockPos blockPos, BlockState blockState) {
        this(MISCTWFBlockEntities.MONSTER_EGG.get(), blockPos, blockState);
    }

    public MonsterEggBlockEntity(BlockEntityType<MonsterEggBlockEntity> type, BlockPos blockPos, BlockState blockState) {
        super(type, blockPos, blockState);
    }

    @Nullable
    public Entity createMonster(Level level) {
        return this.entries.getRandom(level.random).map(entry -> entry.type().create(level)).orElse(null);
    }

    public void setEntries(WeightedRandomList<MonsterEggEntry> entries) {
        this.entries = entries;
    }

    public void fromItem(ItemStack itemStack) {
        CompoundTag nbt = itemStack.getTag();
        if (nbt == null) {
            return;
        }
        this.loadInner(nbt);
    }

    private void loadInner(CompoundTag nbt) {
        if (nbt.contains("Entries", Tag.TAG_LIST)) {
            this.setEntries(WeightedRandomList.create(
                    MonsterEggEntry.CODEC.listOf().parse(NbtOps.INSTANCE, nbt.getList("Entries", Tag.TAG_COMPOUND)).getOrThrow(false, MISCTWFLogger::error)
            ));
        }
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        this.loadInner(nbt);
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.put("Entries", MonsterEggEntry.CODEC.listOf().encode(this.entries.unwrap(), NbtOps.INSTANCE, new ListTag()).getOrThrow(false, MISCTWFLogger::error));
    }

    public static void serverTick(Level level, BlockPos blockPos, BlockState blockState, MonsterEggBlockEntity monsterEgg) {
        long gameTime = level.getGameTime();
        if (gameTime % 5 == 0) {
            monsterEgg.soundData.entrySet().removeIf(entry ->
                    gameTime - entry.getValue().gameTime > entry.getValue().duration);
            List<LivingEntity> livingEntities = level.getEntitiesOfClass(LivingEntity.class, monsterEgg.getRenderBoundingBox().inflate(8), livingEntity -> livingEntity.hasEffect(SonaMobEffects.EXPOSURE.get()));
            AtomicBoolean flag = new AtomicBoolean(!livingEntities.isEmpty());
            livingEntities.forEach(livingEntity -> {
                        MobEffectInstance mobEffectInstance = livingEntity.getEffect(SonaMobEffects.EXPOSURE.get());
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
                                    flag.set(false);
                                }
                            }
                        }
                        monsterEgg.soundData.put(livingEntity.getUUID(), new SoundData(amplifier, duration, gameTime));
                    });
            if (flag.get()) {
                level.getEntitiesOfClass(ServerPlayer.class, monsterEgg.getRenderBoundingBox().inflate(32))
                        .forEach(player -> SurviveInTheWinterFrontier.packetHandler.send(PacketDistributor.PLAYER.with(() -> player), new MonsterEggAnimationPacket(blockPos.getX(), blockPos.getY(), blockPos.getZ())));
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
            BlockPos blockPos = new BlockPos(currentPos);
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
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, CONTROLLER_NAME, 0, this::predicate));
    }

    private PlayState predicate(AnimationEvent<MonsterEggBlockEntity> animationEvent) {
        if (needReload) {
            animationEvent.getController().markNeedsReload();
            needReload = false;
            needPlay = true;
            return PlayState.STOP;
        } else if (needPlay) {
            animationEvent.getController().setAnimationSpeed(2);
            needPlay = false;
            animationEvent.getController().setAnimation(new AnimationBuilder()
                    .addAnimation("shake", ILoopType.EDefaultLoopTypes.PLAY_ONCE));
        }
        return PlayState.CONTINUE;
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

    public void refreshAnimation() {
        needReload = true;
    }

    static record SoundData(int amplifier, int duration, long gameTime) {

    }

    public static class MonsterEggEntry extends WeightedEntry.IntrusiveBase {
        public static final Codec<MonsterEggEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ForgeRegistries.ENTITIES.getCodec().fieldOf("type").forGetter(MonsterEggEntry::type),
                Weight.CODEC.fieldOf("weight").forGetter(WeightedEntry.IntrusiveBase::getWeight)
        ).apply(instance, MonsterEggEntry::new));

        private final EntityType<?> type;

        public MonsterEggEntry(EntityType<?> type, Weight weight) {
            super(weight);
            this.type = type;
        }

        public EntityType<?> type() {
            return type;
        }
    }
}
