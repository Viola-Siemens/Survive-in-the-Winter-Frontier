package com.hexagram2021.misc_twf_industry.common.menu;

import com.google.common.collect.Lists;
import com.hexagram2021.misc_twf_industry.common.block.entity.MoldWorkbenchBlockEntity;
import com.hexagram2021.misc_twf_industry.common.recipe.MoldWorkbenchRecipe;
import com.hexagram2021.misc_twf_industry.common.register.MISCTWFMenuTypes;
import com.hexagram2021.misc_twf_industry.common.register.MISCTWFRecipeTypes;
import com.simibubi.create.AllBlocks;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * 模具加工台菜单
 *
 * @author liudongyu
 */
public class MoldWorkbenchMenu extends AbstractContainerMenu {
	public static final int PROGRESS_BAR_LENGTH = 18;
	public static final int INV_SLOT_START = 3;
	public static final int INV_SLOT_END = 30;
	public static final int USE_ROW_SLOT_START = 30;
	public static final int USE_ROW_SLOT_END = 39;
	public static final int DATA_RECIPE_INDEX = 2;
	private final Container container;
	private final ContainerData containerData;
	protected final Level level;
	private final Slot inputSlot;

	private final Slot mechanicalArmSlot;
	private ItemStack input = ItemStack.EMPTY;
	private final List<RecipeHolder<MoldWorkbenchRecipe>> recipes = Lists.newArrayList();
	Runnable slotUpdateListener;

	/**
	 * 客户端构造函数
	 * @param id 菜单 ID
	 * @param inventory 玩家物品栏
	 */
	public MoldWorkbenchMenu(int id, Inventory inventory) {
		this(id, inventory, new SimpleContainer(MoldWorkbenchBlockEntity.NUM_SLOTS), new SimpleContainerData(MoldWorkbenchBlockEntity.DATA_SLOTS));
	}

	/**
	 * 服务端构造函数
	 * @param id 菜单 ID
	 * @param inventory 玩家物品栏
	 * @param container 模具加工台方块实体
	 * @param containerData 模具加工台数据访问器
	 */
	public MoldWorkbenchMenu(int id, Inventory inventory, Container container, ContainerData containerData) {
		super(MISCTWFMenuTypes.MOLD_WORKBENCH_MENU.get(), id);
		checkContainerSize(container, MoldWorkbenchBlockEntity.NUM_SLOTS);
		checkContainerDataCount(containerData, MoldWorkbenchBlockEntity.DATA_SLOTS);
		this.container = container;
		this.containerData = containerData;
		this.level = inventory.player.level();
		this.slotUpdateListener = () -> {
		};

		this.inputSlot = this.addSlot(new Slot(container, MoldWorkbenchBlockEntity.SLOT_INPUT, 20, 33) {
			@Override
			public void setChanged() {
				super.setChanged();
				MoldWorkbenchMenu.this.slotsChanged(MoldWorkbenchMenu.this.container);
				MoldWorkbenchMenu.this.slotUpdateListener.run();
			}
		});
		this.addSlot(new Slot(container, MoldWorkbenchBlockEntity.SLOT_RESULT, 143, 33) {
			@Override
			public boolean mayPlace(ItemStack itemStack) {
				return false;
			}
		});
		this.mechanicalArmSlot = this.addSlot(new Slot(container, MoldWorkbenchBlockEntity.SLOT_MECHANICAL_ARM, 147, 8) {
			@Override
			public boolean mayPlace(ItemStack itemStack) {
				return AllBlocks.MECHANICAL_ARM.isIn(itemStack);
			}

			@Override
			public void setChanged() {
				super.setChanged();
				if(this.container instanceof MoldWorkbenchBlockEntity moldWorkbenchBlockEntity) {
					moldWorkbenchBlockEntity.mechanicalArmSlotChange(AllBlocks.MECHANICAL_ARM.isIn(this.getItem()));
				}
			}
		});

		for(int i = 0; i < 3; ++i) {
			for(int j = 0; j < 9; ++j) {
				this.addSlot(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}
		for(int i = 0; i < 9; ++i) {
			this.addSlot(new Slot(inventory, i, 8 + i * 18, 142));
		}
		this.addDataSlots(containerData);

		ItemStack input = container.getItem(MoldWorkbenchBlockEntity.SLOT_INPUT);
		if(!input.isEmpty()) {
			this.setupAllWorkbenchRecipes(container, input);
		}
	}

	public int getSelectedRecipeIndex() {
		return this.containerData.get(DATA_RECIPE_INDEX);
	}

	public List<RecipeHolder<MoldWorkbenchRecipe>> getRecipes() {
		return this.recipes;
	}

	public int getNumRecipes() {
		return this.recipes.size();
	}

	/**
	 * 交互界面中是否有输入物品
	 * @return 是否有输入物品
	 */
	public boolean hasInputItem() {
		return this.inputSlot.hasItem() && !this.recipes.isEmpty();
	}

	@Override
	public boolean stillValid(Player player) {
		return this.container.stillValid(player);
	}

	@Override
	public boolean clickMenuButton(Player player, int index) {
		if (this.isValidRecipeIndex(index)) {
			this.containerData.set(DATA_RECIPE_INDEX, index);
			this.setupResultSlot();
		}

		return true;
	}

	protected boolean isValidRecipeIndex(int index) {
		return index >= 0 && index < this.recipes.size();
	}

	@Override
	public void slotsChanged(Container container) {
		super.slotsChanged(container);
		ItemStack inputItem = this.inputSlot.getItem();
		if (!inputItem.is(this.input.getItem())) {
			this.input = inputItem.copy();
			this.setupRecipeList(container, inputItem);
		}
	}

	private void setupRecipeList(Container container, ItemStack itemStack) {
		this.containerData.set(DATA_RECIPE_INDEX, -1);
		this.setupAllWorkbenchRecipes(container, itemStack);
		this.broadcastChanges();
	}

	private void setupAllWorkbenchRecipes(Container container, ItemStack itemStack) {
		this.recipes.clear();
		if (!itemStack.isEmpty()) {
			this.recipes.addAll(this.level.getRecipeManager().getRecipesFor(
					MISCTWFRecipeTypes.MOLD_WORKBENCH.get(),
					new SingleRecipeInput(container.getItem(MoldWorkbenchBlockEntity.SLOT_INPUT)),
					this.level
			));
		}
	}

	void setupResultSlot() {
		if(this.container instanceof MoldWorkbenchBlockEntity moldWorkbenchBlockEntity) {
			RecipeHolder<MoldWorkbenchRecipe> recipe = this.recipes.get(this.containerData.get(DATA_RECIPE_INDEX));
			moldWorkbenchBlockEntity.setRecipeUsed(recipe);
			moldWorkbenchBlockEntity.startWorking(recipe.value().workingTime());
		}
		this.broadcastChanges();
	}

	@SuppressWarnings("ConstantValue")
	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		ItemStack ret = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);
		if(slot != null && slot.hasItem()) {
			ItemStack slotItem = slot.getItem();
			ret = slotItem.copy();
			if (index == MoldWorkbenchBlockEntity.SLOT_RESULT) {
				if (!this.moveItemStackTo(slotItem, INV_SLOT_START, USE_ROW_SLOT_END, true)) {
					return ItemStack.EMPTY;
				}

				slot.onQuickCraft(slotItem, ret);
			} else if(index == MoldWorkbenchBlockEntity.SLOT_INPUT || index == MoldWorkbenchBlockEntity.SLOT_MECHANICAL_ARM) {
				if (!this.moveItemStackTo(slotItem, INV_SLOT_START, USE_ROW_SLOT_END, false)) {
					return ItemStack.EMPTY;
				}
			} else if(this.canWorkOn(slotItem)) {
				if (!this.moveItemStackTo(slotItem, MoldWorkbenchBlockEntity.SLOT_INPUT, MoldWorkbenchBlockEntity.SLOT_INPUT + 1, false)) {
					return ItemStack.EMPTY;
				}
			} else if(this.mechanicalArmSlot.mayPlace(slotItem)) {
				if (!this.moveItemStackTo(slotItem, MoldWorkbenchBlockEntity.SLOT_MECHANICAL_ARM, MoldWorkbenchBlockEntity.SLOT_MECHANICAL_ARM + 1, false)) {
					return ItemStack.EMPTY;
				}
			} else if(index >= INV_SLOT_START && index < INV_SLOT_END) {
				if (!this.moveItemStackTo(slotItem, USE_ROW_SLOT_START, USE_ROW_SLOT_END, false)) {
					return ItemStack.EMPTY;
				}
			} else if(index >= USE_ROW_SLOT_START && index < USE_ROW_SLOT_END) {
				if (!this.moveItemStackTo(slotItem, INV_SLOT_START, INV_SLOT_END, false)) {
					return ItemStack.EMPTY;
				}
			}
			if (slotItem.isEmpty()) {
				slot.set(ItemStack.EMPTY);
			} else {
				slot.setChanged();
			}

			if (slotItem.getCount() == ret.getCount()) {
				return ItemStack.EMPTY;
			}

			slot.onTake(player, slotItem);
		}

		return ret;
	}

	protected boolean canWorkOn(ItemStack itemStack) {
		return this.level.getRecipeManager().getRecipeFor(MISCTWFRecipeTypes.MOLD_WORKBENCH.get(), new SingleRecipeInput(itemStack), this.level).isPresent();
	}

	/**
	 * （客户端）注册槽位更新监听器
	 * @param runnable 监听器
	 */
	public void registerUpdateListener(Runnable runnable) {
		this.slotUpdateListener = runnable;
	}

	/**
	 *
	 * @return
	 */
	public int getWorkingProgress() {
		int totalTime = this.containerData.get(1);
		return totalTime == 0 ? 0 : this.containerData.get(0) * PROGRESS_BAR_LENGTH / totalTime;
	}


	/**
	 * 机械臂槽位
	 * @return 机械臂槽位
	 */
	public Slot getMechanicalArmSlot() {
		return this.mechanicalArmSlot;
	}
}
