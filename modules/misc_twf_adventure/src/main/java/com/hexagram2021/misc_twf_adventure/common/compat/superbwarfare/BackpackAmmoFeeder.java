package com.hexagram2021.misc_twf_adventure.common.compat.superbwarfare;

import com.atsuishio.superbwarfare.api.event.ReloadEvent;
import com.atsuishio.superbwarfare.api.event.ShootEvent;
import com.atsuishio.superbwarfare.data.gun.Ammo;
import com.atsuishio.superbwarfare.item.ammo.AmmoBoxItem;
import com.hexagram2021.misc_twf_adventure.MiscTwfAdventure;
import com.hexagram2021.misc_twf_adventure.common.data_component.TravelersBackpackTacData;
import com.hexagram2021.misc_twf_adventure.common.register.MISCTWFDataComponentTypes;
import com.tiviacz.travelersbackpack.capability.AttachmentUtils;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 战术背包自动供弹器喵~
 *
 * <p>按决策 D-A：背包弹药槽保留，但改为"SW 弹药物品"语义。开火与换弹前，把槽内存放的
 * SW 弹药（{@code AmmoSupplierItem} 与其 {@code AmmoBoxItem}）结算进玩家的
 * {@code PlayerVariable.ammo} 弹药池（受 {@link Ammo#getLimit()} 上限约束），
 * 从而让背包真正成为"随身弹药储备"喵~</p>
 *
 * <p>只在**服务端**执行；TaC 的 {@code IAmmo} 供弹体系已删除喵~</p>
 *
 * @author liudongyu
 */
@EventBusSubscriber(modid = MiscTwfAdventure.MODID)
public final class BackpackAmmoFeeder {
	/** 单次事件最多补入的弹药数量（防御性上限，避免一次刷爆弹药池）喵~ */
	private static final int MAX_TRANSFER_PER_EVENT = 256;

	/**
	 * 开火前补弹喵~
	 *
	 * @param event SW 开火前事件喵~
	 */
	@SubscribeEvent
	public static void onShoot(ShootEvent.Pre event) {
		feed(event.getShooter());
	}

	/**
	 * 换弹前补弹喵~
	 *
	 * @param event SW 换弹前事件喵~
	 */
	@SubscribeEvent
	public static void onReload(ReloadEvent.Pre event) {
		feed(event.getEntity());
	}

	/**
	 * 把战术背包弹药槽的内容结算进玩家弹药池喵~
	 *
	 * @param entity 持枪实体喵~
	 */
	private static void feed(@Nullable Entity entity) {
		if(!(entity instanceof ServerPlayer player) || player.level().isClientSide) {
			return;
		}
		BackpackWrapper wrapper = AttachmentUtils.getBackpackWrapper(player);
		if(wrapper == null) {
			return;
		}
		ItemStack backpack = wrapper.getBackpackStack();
		TravelersBackpackTacData data = backpack.getOrDefault(
				MISCTWFDataComponentTypes.TRAVELERS_BACKPACK_TAC_DATA, TravelersBackpackTacData.EMPTY
		);
		if(!data.upgradedToTac()) {
			return;
		}
		List<ItemStack> ammoStacks = new ArrayList<>(data.ammoInventory());
		boolean changed = false;
		int transferred = 0;
		for(ItemStack stack : ammoStacks) {
			if(transferred >= MAX_TRANSFER_PER_EVENT) {
				break;
			}
			if(stack.isEmpty()) {
				continue;
			}
			changed |= feedStack(player, stack);
			transferred++;
		}
		if(changed) {
			backpack.set(MISCTWFDataComponentTypes.TRAVELERS_BACKPACK_TAC_DATA,
					new TravelersBackpackTacData(true, ammoStacks));
		}
	}

	/**
	 * 结算单个槽位物品喵~
	 *
	 * @param player 玩家喵~
	 * @param stack  槽位物品（会被就地修改）喵~
	 * @return 是否有内容被移入弹药池喵~
	 */
	private static boolean feedStack(ServerPlayer player, ItemStack stack) {
		boolean changed = false;
		if(stack.getItem() instanceof AmmoBoxItem) {
			for(Ammo type : Ammo.values()) {
				int stored = type.get(stack);
				if(stored <= 0) {
					continue;
				}
				int moved = transfer(player, type, stored);
				if(moved > 0) {
					type.set(stack, stored - moved);
					changed = true;
				}
			}
			return changed;
		}
		Ammo type = resolveAmmo(stack);
		if(type == null) {
			return false;
		}
		int moved = transfer(player, type, stack.getCount());
		if(moved > 0) {
			stack.shrink(moved);
			changed = true;
		}
		return changed;
	}

	/**
	 * 把指定数量的弹药移入玩家弹药池（不超过上限）喵~
	 *
	 * @param player 玩家喵~
	 * @param type   弹药类型喵~
	 * @param amount 期望移入数量喵~
	 * @return 实际移入数量喵~
	 */
	private static int transfer(ServerPlayer player, Ammo type, int amount) {
		int space = type.getLimit() - type.get(player);
		int moved = Math.min(space, Math.min(amount, MAX_TRANSFER_PER_EVENT));
		if(moved <= 0) {
			return 0;
		}
		return type.add(player, moved) ? moved : 0;
	}

	/**
	 * 由物品注册名反查 SW 弹药类型喵~
	 *
	 * @param stack 物品喵~
	 * @return 弹药类型，无法识别时为空喵~
	 */
	@Nullable
	private static Ammo resolveAmmo(ItemStack stack) {
		ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());
		if(id == null || !"superbwarfare".equals(id.getNamespace())) {
			return null;
		}
		String path = id.getPath();
		String suffix = "_ammo";
		if(!path.endsWith(suffix) || path.length() == suffix.length()) {
			return null;
		}
		return Ammo.getType(toSerializationName(path.substring(0, path.length() - suffix.length())));
	}

	/**
	 * 把 {@code rifle_ammo} 形式的路径转换为 {@link Ammo#getType(String)} 需要的 {@code RifleAmmo}喵~
	 *
	 * @param path 物品路径前缀（不含 {@code _ammo}）喵~
	 * @return 序列化名喵~
	 */
	private static String toSerializationName(String path) {
		StringBuilder builder = new StringBuilder();
		boolean upper = true;
		for(char c : path.toLowerCase(Locale.ROOT).toCharArray()) {
			if(c == '_') {
				upper = true;
			} else if(upper) {
				builder.append(Character.toUpperCase(c));
				upper = false;
			} else {
				builder.append(c);
			}
		}
		return builder.append("Ammo").toString();
	}

	private BackpackAmmoFeeder() {
	}
}
