package com.hexagram2021.misc_twf.common.entity;

import com.hexagram2021.misc_twf.common.config.MISCTWFCommonConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.event.ForgeEventFactory;

import javax.annotation.Nullable;

public class ZombieAnimalEntity<T extends Animal> extends Zombie {
	private static final EntityDataAccessor<Boolean> DATA_CONVERTING_ID = SynchedEntityData.defineId(ZombieAnimalEntity.class, EntityDataSerializers.BOOLEAN);
	private int conversionTime;

	@Nullable
	private EntityType<? extends T> animalEntityType;

	public ZombieAnimalEntity(EntityType<? extends Zombie> entityType, Level level) {
		super(entityType, level);
		this.animalEntityType = null;
	}

	public ZombieAnimalEntity(EntityType<? extends Zombie> entityType, EntityType<? extends T> animalEntityType, Level level) {
		super(entityType, level);
		this.animalEntityType = animalEntityType;
	}

	@Override
	protected boolean convertsInWater() {
		return false;
	}

	@Override
	protected ItemStack getSkull() {
		return ItemStack.EMPTY;
	}

	@Override
	public void tick() {
		if (!this.level.isClientSide && this.isAlive() && this.isConverting()) {
			int i = this.getConversionProgress();
			this.conversionTime -= i;
			if (this.conversionTime <= 0 && ForgeEventFactory.canLivingConvert(this, EntityType.VILLAGER, (timer) -> this.conversionTime = timer)) {
				this.finishConversion((ServerLevel)this.level);
			}
		}

		super.tick();
	}

	@Override
	public InteractionResult mobInteract(Player player, InteractionHand hand) {
		ItemStack itemstack = player.getItemInHand(hand);
		if(!MISCTWFCommonConfig.ZOMBIE_ANIMALS_CAN_BE_HEALED.get() || !itemstack.is(Items.GOLDEN_APPLE) || this.animalEntityType == null) {
			return super.mobInteract(player, hand);
		}
		if (this.hasEffect(MobEffects.WEAKNESS)) {
			if (!player.getAbilities().instabuild) {
				itemstack.shrink(1);
			}

			if (!this.level.isClientSide) {
				this.startConverting(this.random.nextInt(2401) + 3600);
			}

			this.gameEvent(GameEvent.MOB_INTERACT, this.eyeBlockPosition());
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.CONSUME;
	}

	private void startConverting(int time) {
		this.conversionTime = time;
		this.getEntityData().set(DATA_CONVERTING_ID, true);
		this.removeEffect(MobEffects.WEAKNESS);
		this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, time, Math.min(this.level.getDifficulty().getId() - 1, 0)));
		this.level.broadcastEntityEvent(this, (byte)16);
	}

	private void finishConversion(ServerLevel level) {
		if(this.animalEntityType == null) {
			return;
		}
		T animal = this.convertTo(this.animalEntityType, false);
		if(animal == null) {
			return;
		}
		animal.finalizeSpawn(level, level.getCurrentDifficultyAt(animal.blockPosition()), MobSpawnType.CONVERSION, null, null);
		if (!this.isSilent()) {
			level.levelEvent(null, LevelEvent.SOUND_ZOMBIE_CONVERTED, this.blockPosition(), 0);
		}
		ForgeEventFactory.onLivingConvert(this, animal);
	}

	public boolean isConverting() {
		return this.getEntityData().get(DATA_CONVERTING_ID);
	}

	private int getConversionProgress() {
		int ret = 1;
		if (this.random.nextFloat() < 0.01F) {
			int total = 0;
			BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

			for(int x = (int)this.getX() - 4; x < (int)this.getX() + 4 && total < 15; ++x) {
				for(int y = (int)this.getY() - 4; y < (int)this.getY() + 4 && total < 15; ++y) {
					for(int z = (int)this.getZ() - 4; z < (int)this.getZ() + 4 && total < 15; ++z) {
						BlockState blockstate = this.level.getBlockState(mutable.set(x, y, z));
						if (blockstate.is(Blocks.IRON_BARS) || blockstate.is(Blocks.HAY_BLOCK)) {
							if (this.random.nextFloat() < 0.3F) {
								++ret;
							}

							++total;
						}
					}
				}
			}
		}

		return ret;
	}

	@Override
	public void handleEntityEvent(byte event) {
		if (event == 16) {
			if (!this.isSilent()) {
				this.level.playLocalSound(this.getX(), this.getEyeY(), this.getZ(), SoundEvents.ZOMBIE_VILLAGER_CURE, this.getSoundSource(), 1.0F + this.random.nextFloat(), this.random.nextFloat() * 0.7F + 0.3F, false);
			}
		} else {
			super.handleEntityEvent(event);
		}
	}

	public void setAnimalEntityType(EntityType<? extends T> animalEntityType) {
		this.animalEntityType = animalEntityType;
	}
}
