package com.hexagram2021.misc_twf.common.block.entity;

import com.google.common.collect.Lists;
import com.hexagram2021.misc_twf.common.block.RecoveryFurnaceBlock;
import com.hexagram2021.misc_twf.common.menu.RecoveryFurnaceMenu;
import com.hexagram2021.misc_twf.common.recipe.RecoveryFurnaceRecipe;
import com.hexagram2021.misc_twf.common.register.MISCTWFBlockEntities;
import com.hexagram2021.misc_twf.common.register.MISCTWFRecipeTypes;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.*;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
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
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.RecipeCraftingHolder;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
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
			return player.containerMenu instanceof RecoveryFurnaceMenu menu && menu.getContainer() == RecoveryFurnaceBlockEntity.this;
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

	@Override
	protected RecoveryFurnaceMenu createMenu(int id, Inventory inventory) {
		return new RecoveryFurnaceMenu(id, inventory, this, this.dataAccess);
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
		boolean lit = blockEntity.isLit();
		boolean changed = false;
		if (blockEntity.isLit()) {
			// 燃烧时间递减喵~
			--blockEntity.litTime;
		}

		ItemStack itemstack = blockEntity.items.get(SLOT_FUEL);
		if (!blockEntity.isLit() && (itemstack.isEmpty() || blockEntity.items.get(SLOT_INPUT).isEmpty())) {
			// 没有燃料或输入物品时,回收进度缓慢倒退喵~
			if (blockEntity.recoveringProgress > 0) {
				blockEntity.recoveringProgress = Mth.clamp(blockEntity.recoveringProgress - 2, 0, blockEntity.recoveringTotalTime);
			}
		} else {
			RecipeHolder<RecoveryFurnaceRecipe> recipe = level.getRecipeManager().getRecipeFor(MISCTWFRecipeTypes.RECOVERY_FURNACE.get(), new SingleRecipeInput(blockEntity.items.getFirst()), level).orElse(null);
			int i = blockEntity.getMaxStackSize();
			if (!blockEntity.isLit() && blockEntity.canBurn(recipe, blockEntity.items, i)) {
				// 燃烧燃料喵~
				blockEntity.litTime = blockEntity.getBurnDuration(itemstack);
				blockEntity.litDuration = blockEntity.litTime;
				if (blockEntity.isLit()) {
					changed = true;
					// 处理容器物品(如桶)喵~
					if (itemstack.hasContainerItem()) {
						blockEntity.items.set(SLOT_FUEL, itemstack.getContainerItem());
					} else if (!itemstack.isEmpty()) {
						itemstack.shrink(1);
						if (itemstack.isEmpty()) {
							blockEntity.items.set(SLOT_FUEL, itemstack.getContainerItem());
						}
					}
				}
			}

			if (blockEntity.isLit() && blockEntity.canBurn(recipe, blockEntity.items, i)) {
				// 正在燃烧且可以回收,增加回收进度喵~
				++blockEntity.recoveringProgress;
				if (blockEntity.recoveringProgress == blockEntity.recoveringTotalTime) {
					// 回收完成,执行回收操作喵~
					blockEntity.recoveringProgress = 0;
					blockEntity.recoveringTotalTime = getTotalCookTime(level, blockEntity);
					if (blockEntity.burn(level, recipe, blockEntity.items, i)) {
						blockEntity.setRecipeUsed(recipe);
					}

					changed = true;
				}
			} else {
				// 无法回收,重置进度喵~
				blockEntity.recoveringProgress = 0;
			}
		}

		if (lit != blockEntity.isLit()) {
			// 燃烧状态改变,更新方块状态喵~
			changed = true;
			blockState = blockState.setValue(RecoveryFurnaceBlock.LIT, blockEntity.isLit());
			level.setBlock(blockPos, blockState, Block.UPDATE_ALL);
		}

		if (changed) {
			setChanged(level, blockPos, blockState);
		}
	}

	/**
	 * 检查是否可以执行回收操作喵~
	 *
	 * @param recipe 回收配方喵~
	 * @param items 物品槽位列表喵~
	 * @param maxCount 最大堆叠数量喵~
	 * @return 如果可以回收则返回 true,否则返回 false 喵~
	 */
	public boolean canBurn(@Nullable RecipeHolder<RecoveryFurnaceRecipe> recipe, NonNullList<ItemStack> items, int maxCount) {
		if (!items.get(SLOT_INPUT).isEmpty() && recipe != null) {
			// 获取所有产物喵~
			List<ItemStack> results = recipe.value().assembleAll(this);
			if(results.isEmpty()) {
				return false;
			}
			// 检查每个产物是否有足够的空间喵~
			for(ItemStack result: results) {
				if (result.isEmpty()) {
					return false;
				}
				int count = result.getCount();
				for(int i = SLOT_RESULT_START; i < SLOT_RESULT_END; ++i) {
					ItemStack slotItem = items.get(i);
					if (slotItem.isEmpty()) {
						// 找到空槽位,可以放入喵~
						count = 0;
						break;
					}
					if (ItemStack.isSameItem(slotItem, result)) {
						// 找到相同物品,检查是否有空间喵~
						int minMaxCount = Math.min(maxCount, slotItem.getMaxStackSize());
						if (slotItem.getCount() + count <= minMaxCount) {
							count = 0;
							break;
						}
						count -= minMaxCount - slotItem.getCount();
					}
				}
				if(count > 0) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * 执行回收操作,将输入物品转换为产物喵~
	 *
	 * @param level 世界对象喵~
	 * @param recipe 回收配方喵~
	 * @param items 物品槽位列表喵~
	 * @param maxCount 最大堆叠数量喵~
	 * @return 如果回收成功则返回 true,否则返回 false 喵~
	 */
	private boolean burn(Level level, @Nullable RecipeHolder<RecoveryFurnaceRecipe> recipe, NonNullList<ItemStack> items, int maxCount) {
		if (recipe != null && this.canBurn(recipe, items, maxCount)) {
			ItemStack input = items.get(SLOT_INPUT);
			List<ItemStack> results = recipe.value().assembleAll(this);
			// 将所有产物放入输出槽喵~
			for(ItemStack result: results) {
				int count = result.getCount();
				for (int i = SLOT_RESULT_START; i < SLOT_RESULT_END; ++i) {
					ItemStack slotItem = items.get(i);
					if (slotItem.isEmpty()) {
						items.set(i, result.copy());
						count = 0;
						break;
					}
					if (ItemStack.isSameItem(slotItem, result)) {
						int minMaxCount = Math.min(maxCount, slotItem.getMaxStackSize());
						if (slotItem.getCount() + count <= minMaxCount) {
							slotItem.grow(count);
							count = 0;
							break;
						}
						int grow = minMaxCount - slotItem.getCount();
						slotItem.grow(grow);
						count -= grow;
					}
				}
				if (count > 0) {
					// 输出槽已满,将多余的产物掉落到世界中喵~
					Block.popResource(level, this.worldPosition, new ItemStack(result.getItem(), count));
				}
			}
			// 消耗输入物品喵~
			input.shrink(recipe.value().ingredient().getResult().getCount());
			return true;
		}
		return false;
	}

	/**
	 * 获取燃料的燃烧持续时间喵~
	 *
	 * @param fuel 燃料物品喵~
	 * @return 燃烧持续时间(游戏刻),如果不是燃料则返回 0 喵~
	 */
	protected int getBurnDuration(ItemStack fuel) {
		if (fuel.isEmpty()) {
			return 0;
		}
		return fuel.getBurnTime(MISCTWFRecipeTypes.RECOVERY_FURNACE.get());
	}

	/**
	 * 获取配方的总回收时间喵~
	 *
	 * @param level 世界对象喵~
	 * @param container 容器对象喵~
	 * @return 回收时间(游戏刻),如果没有配方则返回 200 喵~
	 */
	private static int getTotalCookTime(Level level, Container container) {
		return level.getRecipeManager().getRecipeFor(MISCTWFRecipeTypes.RECOVERY_FURNACE.get(), new SingleRecipeInput(container.getItem(0)), level)
				.map(recipeHolder -> recipeHolder.value().recoveringTime()).orElse(200);
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
	 *
	 * <p>回收炉不使用此方法来授予配方,而是使用 {@link #awardUsedRecipesAndPopExperience(ServerPlayer)} 喵~</p>
	 */
	@Override
	public void awardUsedRecipes(Player player, List<ItemStack> itemStacks) {
		// 空实现,不需要做任何事情喵~
	}

	/**
	 * 授予玩家已使用的配方并生成经验球喵~
	 *
	 * @param player 要授予配方的玩家喵~
	 */
	public void awardUsedRecipesAndPopExperience(ServerPlayer player) {
		List<RecipeHolder<?>> list = this.getRecipesToAwardAndPopExperience(player.serverLevel(), player.position());
		player.awardRecipes(list);
		this.recipesUsed.clear();
	}

	/**
	 * 获取需要授予的配方并在指定位置生成经验球喵~
	 *
	 * @param level 服务端世界喵~
	 * @param position 生成经验球的位置喵~
	 * @return 需要授予的配方列表喵~
	 */
	public List<RecipeHolder<?>> getRecipesToAwardAndPopExperience(ServerLevel level, Vec3 position) {
		List<RecipeHolder<?>> list = Lists.newArrayList();

		for (Object2IntMap.Entry<ResourceLocation> entry : this.recipesUsed.object2IntEntrySet()) {
			level.getRecipeManager().byKey(entry.getKey()).ifPresent(recipe -> {
				list.add(recipe);
				createExperience(level, position, entry.getIntValue(), ((RecoveryFurnaceRecipe) recipe.value()).experience());
			});
		}

		return list;
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
