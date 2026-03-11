package com.hexagram2021.misc_twf.common.block.entity;

import com.hexagram2021.misc_twf.common.block.UltravioletLampBlock;
import com.hexagram2021.misc_twf.common.entity.IAvoidBlockMonster;
import com.hexagram2021.misc_twf.common.menu.UltravioletLampMenu;
import com.hexagram2021.misc_twf.common.register.MISCTWFBlockEntities;
import com.hexagram2021.misc_twf.common.register.MISCTWFItemTags;
import com.hexagram2021.misc_twf.common.register.MISCTWFMobEffects;
import com.hexagram2021.misc_twf.server.MISCTWFSavedData;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

/**
 * 强紫外线照射灯方块实体,用于照射和削弱周围的怪物喵~
 *
 * <p>强紫外线照射灯需要电力才能工作,可以从蓄电池或能源网络中获取能量喵~
 * 它会对周围的怪物施加缓慢和脆弱效果,并使某些怪物主动逃离喵~</p>
 *
 * @author liudongyu
 */
public class UltravioletLampBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer, StackedContentsCompatible {
	/** 蓄电池槽位索引喵~ */
	public static final int SLOT_BATTERY = 0;
	/** 从上方可访问的槽位索引数组喵~ */
	private static final int[] SLOTS_FOR_UP = new int[]{SLOT_BATTERY};
	/** 从下方可访问的槽位索引数组喵~ */
	private static final int[] SLOTS_FOR_DOWN = new int[]{SLOT_BATTERY};
	/** 从侧面可访问的槽位索引数组喵~ */
	private static final int[] SLOTS_FOR_SIDES = new int[]{SLOT_BATTERY};

	/** 物品槽位列表(仅包含一个蓄电池槽)喵~ */
	protected NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);

	/** 能量消耗倒计时,每次消耗能量后重置为 20 游戏刻喵~ */
	private int tickEnergyTime = 0;

	/**
	 * 构造强紫外线照射灯方块实体喵~
	 *
	 * @param blockPos 方块位置喵~
	 * @param blockState 方块状态喵~
	 */
	public UltravioletLampBlockEntity(BlockPos blockPos, BlockState blockState) {
		this(MISCTWFBlockEntities.ULTRAVIOLET_LAMP.get(), blockPos, blockState);
	}
	/**
	 * 使用指定方块实体类型构造强紫外线照射灯方块实体喵~
	 *
	 * @param type 方块实体类型喵~
	 * @param blockPos 方块位置喵~
	 * @param blockState 方块状态喵~
	 */
	public UltravioletLampBlockEntity(BlockEntityType<UltravioletLampBlockEntity> type, BlockPos blockPos, BlockState blockState) {
		super(type, blockPos, blockState);
	}

	@Override
	protected Component getDefaultName() {
		return Component.translatable("container.misc_twf.ultraviolet_lamp");
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
	protected UltravioletLampMenu createMenu(int id, Inventory inventory) {
		return new UltravioletLampMenu(id, inventory, this);
	}

	@Override
	public void loadAdditional(CompoundTag nbt, HolderLookup.Provider provider) {
		super.loadAdditional(nbt, provider);
		this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
		ContainerHelper.loadAllItems(nbt, this.items, provider);
	}

	@Override
	protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider provider) {
		super.saveAdditional(nbt, provider);
		ContainerHelper.saveAllItems(nbt, this.items, provider);
	}

	/**
	 * 检查物品是否为有效的蓄电池喵~
	 *
	 * @param itemStack 要检查的物品喵~
	 * @return 如果物品是蓄电池则返回 true 喵~
	 */
	public static boolean isBattery(ItemStack itemStack) {
		return itemStack.is(MISCTWFItemTags.BATTERY);
	}

	/**
	 * 服务端 Tick 方法,用于消耗能量并对周围的怪物施加效果喵~
	 *
	 * @param level 世界对象喵~
	 * @param blockPos 方块位置喵~
	 * @param blockState 方块状态喵~
	 * @param blockEntity 方块实体喵~
	 */
	@SuppressWarnings("ConstantConditions")
	public static void serverTick(Level level, BlockPos blockPos, BlockState blockState, UltravioletLampBlockEntity blockEntity) {
		boolean lit = blockState.getValue(UltravioletLampBlock.LIT);
		boolean newLit = false;
		if(--blockEntity.tickEnergyTime <= 0) {
			// 每 20 游戏刻消耗 1 点能量喵~
			blockEntity.tickEnergyTime = 20;
			IEnergyStorage energy = level.getCapability(Capabilities.EnergyStorage.BLOCK, blockPos, blockState, blockEntity, Direction.UP);
			if(energy != null && energy.getEnergyStored() > 0) {
				// 优先从能源网络获取能量喵~
				energy.extractEnergy(1, false);
				newLit = true;
			} else {
				// 从蓄电池获取能量喵~
				for (ItemStack itemStack : blockEntity.items) {
					if (!isBattery(itemStack)) {
						continue;
					}
					IEnergyStorage ies = itemStack.getCapability(Capabilities.EnergyStorage.ITEM);
					if (ies != null && ies.getEnergyStored() > 0) {
						ies.extractEnergy(1, false);
						newLit = true;
						break;
					}
				}
			}
			if(lit != newLit) {
				// 点亮状态改变,更新方块状态并注册/注销照射灯喵~
				blockState = blockState.setValue(UltravioletLampBlock.LIT, newLit);
				level.setBlock(blockPos, blockState, 3);
				setChanged(level, blockPos, blockState);
				if(newLit) {
					// 注册照射灯位置喵~
					MISCTWFSavedData.placeLamp(GlobalPos.of(level.dimension(), blockPos));
				} else {
					// 注销照射灯位置喵~
					MISCTWFSavedData.destroyLamp(GlobalPos.of(level.dimension(), blockPos));
				}
			}
			if(newLit) {
				// 对周围的怪物施加效果喵~
				level.getEntities(EntityTypeTest.forClass(Monster.class), AABB.ofSize(Vec3.atCenterOf(blockPos), 36.0D, 36.0D, 36.0D), monster -> true)
						.forEach(monster -> {
							if(blockPos.closerThan(monster.blockPosition(), 32.0D)) {
								// 32 格内的怪物:缓慢 I 和脆弱 IV 喵~
								monster.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 0));
								monster.addEffect(new MobEffectInstance(MISCTWFMobEffects.FRAGILE, 200, 3));
								if (monster.getTarget() == null && blockPos.closerThan(monster.blockPosition(), 20.0D) && monster instanceof IAvoidBlockMonster avoidBlockMonster) {
									// 20 格内的特殊怪物会主动逃离照射灯喵~
									avoidBlockMonster.misc_twf$getAvoidBlockGoal().blockPos = blockPos;
								}
							} else {
								// 32-36 格内的怪物:脆弱 III 喵~
								monster.addEffect(new MobEffectInstance(MISCTWFMobEffects.FRAGILE, 200, 2));
							}
						});
			}
		}
	}

	@Override
	public int getContainerSize() {
		return this.items.size();
	}

	@Override
	public boolean isEmpty() {
		for(ItemStack itemstack : this.items) {
			if (!itemstack.isEmpty()) {
				return false;
			}
		}

		return true;
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
		this.setChanged();
	}

	@SuppressWarnings("ConstantConditions")
	@Override
	public boolean stillValid(Player player) {
		if (this.level.getBlockEntity(this.worldPosition) != this) {
			return false;
		} else {
			return player.distanceToSqr(
					this.worldPosition.getX() + 0.5D,
					this.worldPosition.getY() + 0.5D,
					this.worldPosition.getZ() + 0.5D
			) <= 64.0D;
		}
	}

	@Override
	public boolean canPlaceItem(int index, ItemStack itemStack) {
		return isBattery(itemStack);
	}

	@Override
	public int[] getSlotsForFace(Direction direction) {
		if (direction == Direction.DOWN) {
			return SLOTS_FOR_DOWN;
		}
		return direction == Direction.UP ? SLOTS_FOR_UP : SLOTS_FOR_SIDES;
	}

	@Override
	public boolean canPlaceItemThroughFace(int index, ItemStack itemStack, @Nullable Direction direction) {
		return this.canPlaceItem(index, itemStack);
	}

	@Override
	public boolean canTakeItemThroughFace(int index, ItemStack itemStack, Direction direction) {
		// 只有电量耗尽的蓄电池才能被取出喵~
		IEnergyStorage c = itemStack.getCapability(Capabilities.EnergyStorage.ITEM);
		if(c == null) {
			return true;
		}
		return c.getEnergyStored() <= 0;
	}

	@Override
	public void clearContent() {
		this.items.clear();
	}

	@Override
	public void fillStackedContents(StackedContents contents) {
		for(ItemStack itemstack : this.items) {
			contents.accountStack(itemstack);
		}
	}
}
