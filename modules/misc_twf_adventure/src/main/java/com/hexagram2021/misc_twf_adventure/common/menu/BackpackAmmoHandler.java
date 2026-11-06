package com.hexagram2021.misc_twf_adventure.common.menu;

import com.hexagram2021.misc_twf_adventure.common.data_component.TravelersBackpackTacData;
import com.hexagram2021.misc_twf_adventure.common.register.MISCTWFDataComponentTypes;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * 战术背包的弹药槽处理器喵~
 *
 * <p>1.21 的 Travelers Backpack 用 {@link BackpackWrapper} 统一持有背包状态，因此弹药槽改为
 * 直接读写背包物品上的 {@code TRAVELERS_BACKPACK_TAC_DATA} 数据组件：打开界面时装载，
 * 槽位变化时写回，避免为方块实体/容器各写一套 NBT mixin喵~</p>
 *
 * @author liudongyu
 */
public final class BackpackAmmoHandler extends ItemStackHandler {
	/** 弹药槽数量喵~ */
	public static final int SLOTS = 9;

	private final BackpackWrapper wrapper;

	/**
	 * 构造弹药槽处理器喵~
	 *
	 * @param wrapper 背包状态喵~
	 */
	public BackpackAmmoHandler(BackpackWrapper wrapper) {
		super(SLOTS);
		this.wrapper = wrapper;
		List<ItemStack> stored = readData().ammoInventory();
		for(int i = 0; i < SLOTS; i++) {
			this.stacks.set(i, i < stored.size() ? stored.get(i).copy() : ItemStack.EMPTY);
		}
	}

	/**
	 * 判断该背包是否已升级为战术背包喵~
	 *
	 * @param wrapper 背包状态喵~
	 * @return 是否可存放弹药喵~
	 */
	public static boolean canStoreAmmo(BackpackWrapper wrapper) {
		return wrapper.getBackpackStack().getOrDefault(
				MISCTWFDataComponentTypes.TRAVELERS_BACKPACK_TAC_DATA, TravelersBackpackTacData.EMPTY
		).upgradedToTac();
	}

	@Override
	protected void onContentsChanged(int slot) {
		ItemStack backpack = this.wrapper.getBackpackStack();
		TravelersBackpackTacData data = backpack.getOrDefault(
				MISCTWFDataComponentTypes.TRAVELERS_BACKPACK_TAC_DATA, TravelersBackpackTacData.EMPTY
		);
		List<ItemStack> ammo = new ArrayList<>(SLOTS);
		for(int i = 0; i < SLOTS; i++) {
			ammo.add(this.getStackInSlot(i).copy());
		}
		backpack.set(MISCTWFDataComponentTypes.TRAVELERS_BACKPACK_TAC_DATA,
				new TravelersBackpackTacData(data.upgradedToTac(), ammo));
		// TODO：TB 的物品/方块实体同步由 wrapper 自身负责；这里仅更新持有中的背包物品喵~
	}

	private TravelersBackpackTacData readData() {
		return this.wrapper.getBackpackStack().getOrDefault(
				MISCTWFDataComponentTypes.TRAVELERS_BACKPACK_TAC_DATA, TravelersBackpackTacData.EMPTY
		);
	}
}