package com.hexagram2021.misc_twf.common.block.entity;

import com.hexagram2021.misc_twf.common.block.RecoveryFurnaceBlock;
import com.hexagram2021.misc_twf.common.register.MISCTWFBlockEntities;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.*;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.RecipeCraftingHolder;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 回收炉方块实体,用于回收装备和工具,提取其中的材料喵~
 *
 * <p>回收炉类似于熔炉,需要燃料才能工作喵~
 * 它可以将装备分解为原材料,并给予玩家经验值喵~</p>
 *
 * @author liudongyu
 */
public class RecoveryFurnaceBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer, RecipeCraftingHolder, StackedContentsCompatible {
	/** 输入槽位索引喵~ */
	public static final int SLOT_INPUT = 0;
	/** 燃料槽位索引喵~ */
	public static final int SLOT_FUEL = 1;
	/** 输出槽位起始索引喵~ */
	public static final int SLOT_RESULT_START = 2;
	/** 输出槽位结束索引喵~ */
	public static final int SLOT_RESULT_END = 6;
	/** 槽位总数喵~ */
	public static final int NUM_SLOTS = 6;
	/** 数据槽:燃烧剩余时间喵~ */
	public static final int DATA_LIT_TIME = 0;
	/** 数据槽:燃烧持续时间喵~ */
	public static final int DATA_LIT_DURATION = 1;
	/** 数据槽:回收进度喵~ */
	public static final int DATA_RECOVERING_PROGRESS = 2;
	/** 数据槽:回收总时间喵~ */
	public static final int DATA_RECOVERING_TOTAL_TIME = 3;
	/** 数据槽总数喵~ */
	public static final int DATA_SLOTS = 4;

	/** 从上方可访问的槽位索引数组喵~ */
	private static final int[] SLOTS_FOR_UP = new int[]{0, 1};
	/** 从下方可访问的槽位索引数组(输出槽位)喵~ */
	private static final int[] SLOTS_FOR_DOWN = new int[]{5, 4, 3, 2};
	/** 从侧面可访问的槽位索引数组喵~ */
	private static final int[] SLOTS_FOR_SIDES = new int[]{1, 2, 3, 4, 5, 0};

	/** 物品槽位列表喵~ */
	protected NonNullList<ItemStack> items;
	/** 燃烧剩余时间(游戏刻)喵~ */
	int litTime;
	/** 燃烧持续时间(游戏刻)喵~ */
	int litDuration;
	/** 回收进度(游戏刻)喵~ */
	int recoveringProgress;
	/** 回收总时间(游戏刻)喵~ */
	int recoveringTotalTime;
	/** 已使用配方的记录,用于计算经验值喵~ */
	private final Object2IntOpenHashMap<ResourceLocation> recipesUsed = new Object2IntOpenHashMap<>();

	/** 容器打开计数器,用于管理开关状态和音效喵~ */
	private final ContainerOpenersCounter openersCounter = new ContainerOpenersCounter() {
		@Override
		protected void onOpen(Level level, BlockPos blockPos, BlockState blockState) {
			RecoveryFurnaceBlockEntity.this.playSound(blockState, SoundEvents.BARREL_OPEN);
			RecoveryFurnaceBlockEntity.this.updateBlockState(blockState, true);
		}

		@Override
		protected void onClose(Level level, BlockPos blockPos, BlockState blockState) {
			RecoveryFurnaceBlockEntity.this.playSound(blockState, SoundEvents.BARREL_CLOSE);
			RecoveryFurnaceBlockEntity.this.updateBlockState(blockState, false);
		}

		@Override
		protected void openerCountChanged(Level level, BlockPos blockPos, BlockState blockState, int before, int after) {
		}

		@Override
		protected boolean isOwnContainer(Player player) {
			return false;
		}
	};

	/** 容器数据访问器,用于与 GUI 同步数据喵~ */
	protected final ContainerData dataAccess = new ContainerData() {
		@Override
		public int get(int index) {
			return switch (index) {
				case DATA_LIT_TIME -> RecoveryFurnaceBlockEntity.this.litTime;
				case DATA_LIT_DURATION -> RecoveryFurnaceBlockEntity.this.litDuration;
				case DATA_RECOVERING_PROGRESS -> RecoveryFurnaceBlockEntity.this.recoveringProgress;
				case DATA_RECOVERING_TOTAL_TIME -> RecoveryFurnaceBlockEntity.this.recoveringTotalTime;
				default -> 0;
			};
		}

		@Override
		public void set(int index, int value) {
			switch (index) {
				case DATA_LIT_TIME -> RecoveryFurnaceBlockEntity.this.litTime = value;
				case DATA_LIT_DURATION -> RecoveryFurnaceBlockEntity.this.litDuration = value;
				case DATA_RECOVERING_PROGRESS -> RecoveryFurnaceBlockEntity.this.recoveringProgress = value;
				case DATA_RECOVERING_TOTAL_TIME -> RecoveryFurnaceBlockEntity.this.recoveringTotalTime = value;
			}
		}

		@Override
		public int getCount() {
			return DATA_SLOTS;
		}
	};

	/**
	 * 构造回收炉方块实体喵~
	 *
	 * @param blockPos 方块位置喵~
	 * @param blockState 方块状态喵~
	 */
	public RecoveryFurnaceBlockEntity(BlockPos blockPos, BlockState blockState) {
		super(MISCTWFBlockEntities.RECOVERY_FURNACE.get(), blockPos, blockState);
		this.items = NonNullList.withSize(NUM_SLOTS, ItemStack.EMPTY);
	}

	@Override
	protected Component getDefaultName() {
		return Component.translatable("container.misc_twf.recovery_furnace");
	}

	@Override
	protected NonNullList<ItemStack> getItems() {
		return this.items;
	}

	@Override
	protected void setItems(NonNullList<ItemStack> items) {
		this.items = items;
	}

	@Override @Nullable
	protected AbstractContainerMenu createMenu(int id, Inventory inventory) {
		return null;
	}

	@Override
	public void loadAdditional(CompoundTag nbt, HolderLookup.Provider provider) {
		super.loadAdditional(nbt, provider);
		this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
		ContainerHelper.loadAllItems(nbt, this.items, provider);
		this.litTime = nbt.getInt("LitTime");
		this.litDuration = nbt.getInt("LitDuration");
		this.recoveringProgress = nbt.getInt("RecoveringProgress");
		this.recoveringTotalTime = nbt.getInt("RecoveringTotalTime");
	}

	@Override
	protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider provider) {
		super.saveAdditional(nbt, provider);
		ContainerHelper.saveAllItems(nbt, this.items, provider);
		nbt.putInt("LitTime", this.litTime);
		nbt.putInt("LitDuration", this.litDuration);
		nbt.putInt("RecoveringProgress", this.recoveringProgress);
		nbt.putInt("RecoveringTotalTime", this.recoveringTotalTime);
	}

	/**
	 * 检查回收炉是否正在燃烧喵~
	 *
	 * @return 如果燃烧剩余时间大于 0 则返回 true 喵~
	 */
	private boolean isLit() {
		return this.litTime > 0;
	}

	/**
	 * 客户端 Tick 方法,用于生成粒子效果和播放音效喵~
	 *
	 * @param level 世界对象喵~
	 * @param blockPos 方块位置喵~
	 * @param blockState 方块状态喵~
	 * @param blockEntity 方块实体喵~
	 */
	public static void clientTick(Level level, BlockPos blockPos, BlockState blockState, RecoveryFurnaceBlockEntity blockEntity) {
		RandomSource random = level.getRandom();
		if(blockState.getValue(RecoveryFurnaceBlock.LIT)) {
			double x = blockPos.getX() + 0.5D;
			double y = blockPos.getY() + 0.5D;
			double z = blockPos.getZ() + 0.5D;
			if(blockState.getValue(RecoveryFurnaceBlock.OPEN)) {
				// 回收炉门打开时,生成大量浓烟粒子喵~
				Direction direction = blockState.getValue(RecoveryFurnaceBlock.FACING).getClockWise();
				if (random.nextInt() < 20) {
					level.addParticle(
							ParticleTypes.CAMPFIRE_SIGNAL_SMOKE,
							x + direction.getStepX() + (random.nextDouble() - 0.5D) / 2.0D,
							y + (random.nextDouble() - 0.5D) / 2.0D,
							z + direction.getStepZ() + (random.nextDouble() - 0.5D) / 2.0D,
							0.0D, 0.0078125D, 0.0D
					);
				}
			} else {
				// 回收炉门关闭时,从顶部生成温馨的烟雾粒子喵~
				if (random.nextInt() < 10) {
					level.addParticle(
							ParticleTypes.CAMPFIRE_COSY_SMOKE,
							x + (random.nextDouble() - 0.5D) / 2.0D,
							y + 2.0625D + (random.nextDouble() - 0.5D) / 2.0D,
							z + (random.nextDouble() - 0.5D) / 2.0D,
							0.0D, 0.0078125D, 0.0D
					);
				}
			}
			// 随机播放燃烧音效喵~
			if(random.nextInt() < 10) {
				level.playLocalSound(x, y, z, SoundEvents.BLASTFURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 1.0F, 1.0F, false);
			}
		}
	}

	/**
	 * 服务端 Tick 方法,用于处理燃烧和回收逻辑喵~
	 *
	 * @param level 世界对象喵~
	 * @param blockPos 方块位置喵~
	 * @param blockState 方块状态喵~
	 * @param blockEntity 方块实体喵~
	 */
	public static void serverTick(Level level, BlockPos blockPos, BlockState blockState, RecoveryFurnaceBlockEntity blockEntity) {
	}

	/**
	 * 获取配方的总回收时间喵~
	 *
	 * @param level 世界对象喵~
	 * @param container 容器对象喵~
	 * @return 回收时间(游戏刻),如果没有配方则返回 200 喵~
	 */
	private static int getTotalCookTime(Level level, Container container) {
		return 200;
	}

	@Override
	public int[] getSlotsForFace(Direction direction) {
		if (direction == Direction.DOWN) {
			return SLOTS_FOR_DOWN;
		}
		if(direction == Direction.UP) {
			return SLOTS_FOR_UP;
		}
		return SLOTS_FOR_SIDES;
	}

	@Override
	public boolean canPlaceItemThroughFace(int index, ItemStack itemStack, @Nullable Direction direction) {
		return this.canPlaceItem(index, itemStack);
	}

	@Override
	public boolean canTakeItemThroughFace(int index, ItemStack itemStack, Direction direction) {
		return true;
	}

	@Override
	public int getContainerSize() {
		return this.items.size();
	}

	@Override
	public boolean isEmpty() {
		return this.items.stream().allMatch(ItemStack::isEmpty);
	}

	@Override
	public ItemStack getItem(int index) {
		return this.items.get(index);
	}

	@Override
	public ItemStack removeItem(int index, int count) {
		return ContainerHelper.removeItem(this.items, index, count);
	}

	@Override
	public ItemStack removeItemNoUpdate(int index) {
		return ContainerHelper.takeItem(this.items, index);
	}

	@Override
	public void setItem(int index, ItemStack itemStack) {
		ItemStack slotItem = this.items.get(index);
		boolean flag = !itemStack.isEmpty() && ItemStack.isSameItemSameComponents(itemStack, slotItem);
		this.items.set(index, itemStack);
		if (itemStack.getCount() > this.getMaxStackSize()) {
			itemStack.setCount(this.getMaxStackSize());
		}

		if (index == SLOT_INPUT && !flag) {
			assert this.level != null;
			this.recoveringTotalTime = getTotalCookTime(this.level, this);
			this.recoveringProgress = 0;
			this.setChanged();
		}
	}

	@Override
	public boolean stillValid(Player player) {
		assert this.level != null;
		if (this.level.getBlockEntity(this.worldPosition) != this) {
			return false;
		}
		return player.distanceToSqr(this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 0.5, this.worldPosition.getZ() + 0.5) <= 64.0;
	}

	@Override
	public boolean canPlaceItem(int index, ItemStack itemStack) {
		return index == SLOT_INPUT;
	}

	@Override
	public void clearContent() {
		this.items.clear();
	}

	@Override
	public void setRecipeUsed(@Nullable RecipeHolder<?> recipe) {
		if (recipe != null) {
			ResourceLocation resourcelocation = recipe.id();
			this.recipesUsed.addTo(resourcelocation, 1);
		}

	}

	@Override @Nullable
	public RecipeHolder<?> getRecipeUsed() {
		return null;
	}

	/**
	 * 空实现,用于兼容接口喵~
	 */
	@Override
	public void awardUsedRecipes(Player player, List<ItemStack> itemStacks) {
		// 空实现,不需要做任何事情喵~
	}

	/**
	 * 根据配方和数量创建经验球喵~
	 *
	 * @param level 服务端世界喵~
	 * @param position 生成经验球的位置喵~
	 * @param count 使用配方的次数喵~
	 * @param experience 单次配方提供的经验值喵~
	 */
	private static void createExperience(ServerLevel level, Vec3 position, int count, float experience) {
		int i = Mth.floor(count * experience);
		float f = Mth.frac(count * experience);
		if (f != 0.0F && Math.random() < f) {
			++i;
		}

		ExperienceOrb.award(level, position, i);
	}

	@Override
	public void startOpen(Player player) {
		if (!this.remove && !player.isSpectator()) {
			assert this.level != null;
			this.openersCounter.incrementOpeners(player, this.level, this.getBlockPos(), this.getBlockState());
		}
	}

	@Override
	public void stopOpen(Player player) {
		if (!this.remove && !player.isSpectator()) {
			assert this.level != null;
			this.openersCounter.decrementOpeners(player, this.level, this.getBlockPos(), this.getBlockState());
		}
	}

	/**
	 * 重新检查打开状态,确保方块状态与实际打开者数量一致喵~
	 */
	public void recheckOpen() {
		if (!this.remove) {
			assert this.level != null;
			this.openersCounter.recheckOpeners(this.level, this.getBlockPos(), this.getBlockState());
		}
	}

	/**
	 * 更新方块的开关状态喵~
	 *
	 * @param blockState 方块状态喵~
	 * @param value 是否打开喵~
	 */
	void updateBlockState(BlockState blockState, boolean value) {
		assert this.level != null;
		this.level.setBlock(this.getBlockPos(), blockState.setValue(RecoveryFurnaceBlock.OPEN, value), 3);
	}

	/**
	 * 播放回收炉的开关音效喵~
	 *
	 * @param blockState 方块状态喵~
	 * @param soundEvent 要播放的音效喵~
	 */
	void playSound(BlockState blockState, SoundEvent soundEvent) {
		Vec3i vec3i = blockState.getValue(RecoveryFurnaceBlock.FACING).getNormal();
		double d0 = this.worldPosition.getX() + 0.5D + vec3i.getX() / 2.0D;
		double d1 = this.worldPosition.getY() + 0.5D + vec3i.getY() / 2.0D;
		double d2 = this.worldPosition.getZ() + 0.5D + vec3i.getZ() / 2.0D;
		assert this.level != null;
		this.level.playSound(null, d0, d1, d2, soundEvent, SoundSource.BLOCKS, 0.5F, this.level.random.nextFloat() * 0.1F + 0.9F);
	}

	@Override
	public void fillStackedContents(StackedContents contents) {
		this.items.forEach(contents::accountStack);
	}
}
