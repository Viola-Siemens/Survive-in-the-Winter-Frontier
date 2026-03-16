package com.hexagram2021.misc_twf.common.block.entity;

import com.hexagram2021.misc_twf.common.block.MoldWorkbenchBlock;
import com.hexagram2021.misc_twf.common.block.properties.MoldWorkbenchPart;
import com.hexagram2021.misc_twf.common.menu.MoldWorkbenchMenu;
import com.hexagram2021.misc_twf.common.recipe.MoldWorkbenchRecipe;
import com.hexagram2021.misc_twf.common.register.MISCTWFBlockEntities;
import com.hexagram2021.misc_twf.common.register.MISCTWFBlocks;
import com.simibubi.create.api.contraption.transformable.TransformableBlockEntity;
import com.simibubi.create.content.contraptions.StructureTransform;
import com.simibubi.create.content.decoration.bracket.BracketedBlockEntityBehaviour;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.simpleRelays.AbstractSimpleShaftBlock;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 模具加工台方块实体,用于将粘土模具加工成各种子弹模具喵~
 *
 * <p>模具加工台有两种工作模式喵~:</p>
 * <ul>
 *     <li>手动模式:玩家手动放入粘土模具和选择配方进行加工喵~</li>
 *     <li>自动模式:安装动力臂并接入旋转应力后,自动批量加工子弹模具喵~</li>
 * </ul>
 *
 * @author liudongyu
 */
public class MoldWorkbenchBlockEntity extends KineticBlockEntity implements Container, MenuProvider, Nameable, WorldlyContainer, StackedContentsCompatible, TransformableBlockEntity {
	/** 输入槽位索引喵~ */
	public static final int SLOT_INPUT = 0;
	/** 输出槽位索引喵~ */
	public static final int SLOT_RESULT = 1;
	/** 动力臂槽位索引喵~ */
	public static final int SLOT_MECHANICAL_ARM = 2;
	/** 从上方可访问的槽位索引数组喵~ */
	private static final int[] SLOTS_FOR_UP = new int[]{0, 2};
	/** 从下方可访问的槽位索引数组喵~ */
	private static final int[] SLOTS_FOR_DOWN = new int[]{1};
	/** 从侧面可访问的槽位索引数组喵~ */
	private static final int[] SLOTS_FOR_SIDES = new int[]{0, 1};
	/** 槽位总数喵~ */
	public static final int NUM_SLOTS = 3;
	/** 数据槽数量喵~ */
	public static final int DATA_SLOTS = 3;

	/** 自定义容器名称喵~ */
	@Nullable
	private Component name;
	/** 物品槽位列表喵~ */
	protected NonNullList<ItemStack> items;
	/** 当前加工进度(游戏刻)喵~ */
	int workProgress;
	/** 加工总时间(游戏刻)喵~ */
	int workTotalTime;
	/** 当前选择的配方索引喵~ */
	int recipeIndex = -1;
	/** 正在使用的配方资源位置喵~ */
	@Nullable
	private ResourceLocation recipeUsed = null;

	/** 容器数据访问器,用于与 GUI 同步数据喵~ */
	private final ContainerData dataAccess = new ContainerData() {
		@Override
		public int get(int index) {
			return switch (index) {
				case 0 -> MoldWorkbenchBlockEntity.this.workProgress;
				case 1 -> MoldWorkbenchBlockEntity.this.workTotalTime;
				case 2 -> MoldWorkbenchBlockEntity.this.recipeIndex;
				default -> 0;
			};
		}

		@Override
		public void set(int index, int value) {
			switch (index) {
				case 0 -> MoldWorkbenchBlockEntity.this.workProgress = value;
				case 1 -> MoldWorkbenchBlockEntity.this.workTotalTime = value;
				case 2 -> {
					MoldWorkbenchBlockEntity.this.recipeIndex = value;
					if(value < 0) {
						MoldWorkbenchBlockEntity.this.recipeUsed = null;
					}
				}
				default -> {
					// 忽略越界的 set 操作
				}
			}
		}

		@Override
		public int getCount() {
			return MoldWorkbenchBlockEntity.DATA_SLOTS;
		}
	};

	/**
	 * 构造模具加工台方块实体喵~
	 *
	 * @param blockPos 方块位置喵~
	 * @param blockState 方块状态喵~
	 */
	public MoldWorkbenchBlockEntity(BlockPos blockPos, BlockState blockState) {
		super(MISCTWFBlockEntities.MOLD_WORKBENCH.get(), blockPos, blockState);
		this.items = NonNullList.withSize(NUM_SLOTS, ItemStack.EMPTY);
	}

	@Override
	public Component getName() {
		return this.name != null ? this.name : this.getDefaultName();
	}

	@Override
	public Component getDisplayName() {
		return this.getName();
	}

	/**
	 * 获取自定义容器名称喵~
	 *
	 * @return 自定义名称,如果未设置则返回 null 喵~
	 */
	@Override @Nullable
	public Component getCustomName() {
		return this.name;
	}

	/**
	 * 获取默认容器名称喵~
	 *
	 * @return 翻译键对应的容器名称喵~
	 */
	protected Component getDefaultName() {
		return Component.translatable("container.misc_twf.mold_workbench");
	}

	@Override
	public MoldWorkbenchMenu createMenu(int id, Inventory inventory, Player player) {
		return new MoldWorkbenchMenu(id, inventory, this, this.dataAccess);
	}

	@Override
	public void read(CompoundTag nbt, HolderLookup.Provider provider, boolean clientPacket) {
		super.read(nbt, provider, clientPacket);
		if (nbt.contains("CustomName", Tag.TAG_STRING)) {
			this.name = Component.Serializer.fromJson(nbt.getString("CustomName"), provider);
		}
		this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
		ContainerHelper.loadAllItems(nbt, this.items, provider);
		this.workProgress = nbt.getInt("WorkProgress");
		this.workTotalTime = nbt.getInt("WorkTotalTime");
		this.recipeIndex = nbt.getInt("RecipeIndex");
		if(nbt.contains("RecipeUsed", Tag.TAG_STRING)) {
			this.recipeUsed = ResourceLocation.parse(nbt.getString("RecipeUsed"));
		}
	}

	@Override
	public void write(CompoundTag nbt, HolderLookup.Provider provider, boolean clientPacket) {
		super.write(nbt, provider, clientPacket);
		if (this.name != null) {
			nbt.putString("CustomName", Component.Serializer.toJson(this.name, provider));
		}
		ContainerHelper.saveAllItems(nbt, this.items, provider);
		nbt.putInt("WorkProgress", this.workProgress);
		nbt.putInt("WorkTotalTime", this.workTotalTime);
		nbt.putInt("RecipeIndex", this.recipeIndex);
		if(this.recipeUsed != null) {
			nbt.putString("RecipeUsed", this.recipeUsed.toString());
		}
	}

	@Override
	public void tick() {
		super.tick();
		// 只有底部部分才执行 tick 逻辑喵~
		if(this.getBlockState().getValue(MoldWorkbenchBlock.PART) != MoldWorkbenchPart.BOTTOM) {
			return;
		}
		assert this.level != null;
		if(this.workTotalTime > 0) {
			if(this.recipeUsed != null) {
				ItemStack input = this.getItem(SLOT_INPUT);
				ItemStack result = this.getItem(SLOT_RESULT);
				RecipeHolder<?> recipe = this.level.getRecipeManager().byKey(this.recipeUsed).orElse(null);
				boolean resultEmpty = result.isEmpty();
				// 检查配方是否仍然有效喵~
				if(recipe != null && (resultEmpty || ItemStack.isSameItemSameComponents(
						result, recipe.value().getResultItem(this.level.registryAccess())
				))) {
					// 增加加工进度喵~
					this.workProgress += 1;
					if (this.workProgress < this.workTotalTime) {
						return;
					}
					// 加工完成,生成产物喵~
					SingleRecipeInput recipeInput = new SingleRecipeInput(input);
					if (recipe.value() instanceof MoldWorkbenchRecipe moldWorkbenchRecipe && moldWorkbenchRecipe.matches(recipeInput, this.level)) {
						input.shrink(1);
						if(resultEmpty) {
							this.setItem(SLOT_RESULT, moldWorkbenchRecipe.assemble(recipeInput, this.level.registryAccess()));
						} else {
							result.grow(1);
						}
					}
				}
				// 如果满足速度要求且输入槽不为空,继续下一次加工喵~
				if(this.isSpeedRequirementFulfilled() && !input.isEmpty()) {
					this.workProgress = 0;
					this.setChanged();
					return;
				}
				// 停止加工,重置配方喵~
				this.recipeIndex = -1;
				this.recipeUsed = null;
			}
			this.workProgress = this.workTotalTime = 0;
			this.setChanged();
		}
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
		behaviours.add(new BracketedBlockEntityBehaviour(this, state -> {
			Block block = state.getBlock();
			return block instanceof AbstractSimpleShaftBlock || block instanceof MoldWorkbenchBlock;
		}));
		super.addBehaviours(behaviours);
	}

	@Override
	public void transform(BlockEntity blockEntity, StructureTransform transform) {
		BracketedBlockEntityBehaviour bracketBehaviour = this.getBehaviour(BracketedBlockEntityBehaviour.TYPE);
		if (bracketBehaviour != null) {
			bracketBehaviour.transformBracket(transform);
		}
	}

	@Override
	protected Block getStressConfigKey() {
		return MISCTWFBlocks.MOLD_WORKBENCH.get();
	}

	@Override
	public float calculateStressApplied() {
		float impact = 16.0F;
		this.lastStressApplied = impact;
		return impact;
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
		this.items.set(index, itemStack);
		if (itemStack.getCount() > this.getMaxStackSize()) {
			itemStack.setCount(this.getMaxStackSize());
		}
		if(index == SLOT_INPUT) {
			this.setChanged();
		}
	}

	@Override
	public boolean stillValid(Player player) {
		assert this.level != null;
		if (this.level.getBlockEntity(this.worldPosition) != this) {
			return false;
		}
		return player.distanceToSqr((double)this.worldPosition.getX() + 0.5, (double)this.worldPosition.getY() + 0.5, (double)this.worldPosition.getZ() + 0.5) <= 64.0;
	}

	@Override
	public boolean canPlaceItem(int index, ItemStack itemStack) {
		return index == SLOT_INPUT;
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
	public void clearContent() {
		this.items.clear();
	}

	@Override
	public void fillStackedContents(StackedContents contents) {
		this.items.forEach(contents::accountStack);
	}

	/**
	 * 设置当前使用的配方喵~
	 *
	 * @param recipe 配方持有者,为 null 时清除当前配方喵~
	 */
	public void setRecipeUsed(@Nullable RecipeHolder<MoldWorkbenchRecipe> recipe) {
		if (recipe == null) {
			this.recipeUsed = null;
		} else {
			this.recipeUsed = recipe.id();
		}
	}

	/**
	 * 开始加工操作喵~
	 *
	 * @param totalTime 加工总时间(游戏刻)喵~
	 */
	public void startWorking(int totalTime) {
		this.workProgress = 0;
		this.workTotalTime = totalTime;
	}

	/**
	 * 当动力臂槽位变化时更新方块状态喵~
	 *
	 * @param hasMechanicalArm 是否安装了动力臂喵~
	 */
	public void mechanicalArmSlotChange(boolean hasMechanicalArm) {
		if(this.level instanceof ServerLevel) {
			this.level.setBlock(this.worldPosition, this.getBlockState().setValue(MoldWorkbenchBlock.ARMED, hasMechanicalArm), Block.UPDATE_ALL);
		}
	}
}
