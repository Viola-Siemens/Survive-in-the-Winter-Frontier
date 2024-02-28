package com.hexagram2021.misc_twf.common.block.entity;

import com.hexagram2021.misc_twf.common.block.UltravioletLampBlock;
import com.hexagram2021.misc_twf.common.entity.IAvoidBlockMonster;
import com.hexagram2021.misc_twf.common.menu.UltravioletLampMenu;
import com.hexagram2021.misc_twf.common.register.MISCTWFBlockEntities;
import com.hexagram2021.misc_twf.common.register.MISCTWFItemTags;
import com.hexagram2021.misc_twf.common.register.MISCTWFMobEffects;
import com.hexagram2021.misc_twf.server.MISCTWFSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
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
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class UltravioletLampBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer, StackedContentsCompatible {
	public static final int SLOT_BATTERY = 0;
	private static final int[] SLOTS_FOR_UP = new int[]{SLOT_BATTERY};
	private static final int[] SLOTS_FOR_DOWN = new int[]{SLOT_BATTERY};
	private static final int[] SLOTS_FOR_SIDES = new int[]{SLOT_BATTERY};

	protected NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);

	private int tickEnergyTime = 0;

	public UltravioletLampBlockEntity(BlockPos blockPos, BlockState blockState) {
		this(MISCTWFBlockEntities.ULTRAVIOLET_LAMP.get(), blockPos, blockState);
	}
	public UltravioletLampBlockEntity(BlockEntityType<UltravioletLampBlockEntity> type, BlockPos blockPos, BlockState blockState) {
		super(type, blockPos, blockState);
	}

	@Override
	protected Component getDefaultName() {
		return new TranslatableComponent("container.misc_twf.ultraviolet_lamp");
	}

	@Override
	protected UltravioletLampMenu createMenu(int id, Inventory inventory) {
		return new UltravioletLampMenu(id, inventory, this);
	}

	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
		ContainerHelper.loadAllItems(nbt, this.items);
	}

	@Override
	protected void saveAdditional(CompoundTag nbt) {
		super.saveAdditional(nbt);
		ContainerHelper.saveAllItems(nbt, this.items);
	}

	public static boolean isBattery(ItemStack itemStack) {
		return itemStack.is(MISCTWFItemTags.BATTERY);
	}

	@SuppressWarnings("ConstantConditions")
	public static void serverTick(Level level, BlockPos blockPos, BlockState blockState, UltravioletLampBlockEntity blockEntity) {
		boolean lit = blockState.getValue(UltravioletLampBlock.LIT);
		boolean newLit = false;
		if(--blockEntity.tickEnergyTime <= 0) {
			blockEntity.tickEnergyTime = 20;
			for(ItemStack itemStack: blockEntity.items) {
				if(!isBattery(itemStack)) {
					continue;
				}
				IEnergyStorage ies = itemStack.getCapability(CapabilityEnergy.ENERGY).orElse(null);
				if(ies != null && ies.getEnergyStored() > 0) {
					ies.extractEnergy(1, false);
					newLit = true;
					break;
				}
			}
			if(lit != newLit) {
				blockState = blockState.setValue(UltravioletLampBlock.LIT, newLit);
				level.setBlock(blockPos, blockState, 3);
				setChanged(level, blockPos, blockState);
				if(newLit) {
					MISCTWFSavedData.placeLamp(blockPos);
				} else {
					MISCTWFSavedData.destroyLamp(blockPos);
				}
			}
			if(newLit) {
				level.getEntities(EntityTypeTest.forClass(Monster.class), AABB.ofSize(Vec3.atCenterOf(blockPos), 36.0D, 36.0D, 36.0D), monster -> true)
						.forEach(monster -> {
							if(blockPos.closerThan(monster.blockPosition(), 32.0D)) {
								monster.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 0));
								monster.addEffect(new MobEffectInstance(MISCTWFMobEffects.FRAGILE.get(), 200, 3));
								if (monster.getTarget() == null && blockPos.closerThan(monster.blockPosition(), 20.0D) && monster instanceof IAvoidBlockMonster avoidBlockMonster) {
									avoidBlockMonster.getAvoidBlockGoal().blockPos = blockPos;
								}
							} else {
								monster.addEffect(new MobEffectInstance(MISCTWFMobEffects.FRAGILE.get(), 200, 2));
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
			return player.distanceToSqr((double)this.worldPosition.getX() + 0.5D, (double)this.worldPosition.getY() + 0.5D, (double)this.worldPosition.getZ() + 0.5D) <= 64.0D;
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
		return itemStack.getCapability(CapabilityEnergy.ENERGY).map(ies -> ies.getEnergyStored() <= 0).orElse(true);
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

	LazyOptional<? extends IItemHandler>[] handlers = SidedInvWrapper.create(this, Direction.UP, Direction.DOWN, Direction.NORTH);

	@Override @NotNull
	public <T> LazyOptional<T> getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, @Nullable Direction facing) {
		if (!this.remove && facing != null && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			if (facing == Direction.UP) {
				return this.handlers[0].cast();
			}
			if (facing == Direction.DOWN) {
				return this.handlers[1].cast();
			}
			return this.handlers[2].cast();
		}
		return super.getCapability(capability, facing);
	}

	@Override
	public void invalidateCaps() {
		super.invalidateCaps();
		for (LazyOptional<? extends IItemHandler> handler : this.handlers) {
			handler.invalidate();
		}
	}

	@Override
	public void reviveCaps() {
		super.reviveCaps();
		this.handlers = net.minecraftforge.items.wrapper.SidedInvWrapper.create(this, Direction.UP, Direction.DOWN, Direction.NORTH);
	}
}
