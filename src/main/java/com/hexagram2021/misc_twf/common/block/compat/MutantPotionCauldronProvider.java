package com.hexagram2021.misc_twf.common.block.compat;

import com.hexagram2021.misc_twf.common.block.MutantPotionCauldronBlock;
import com.hexagram2021.misc_twf.common.block.entity.MutantPotionCauldronBlockEntity;
import com.hexagram2021.misc_twf.common.register.MISCTWFItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.IElement;
import snownee.jade.api.ui.IElementHelper;

import java.util.List;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;

/**
 * 装变异药品的炼药锅的 Jade 提供器,用于在 Jade 悬浮窗中显示炼药锅的状态和所需材料喵~
 *
 * @author liudongyu
 */
public enum MutantPotionCauldronProvider implements IComponentProvider<BlockAccessor>, IServerDataProvider<BlockAccessor> {
	/** 单例实例喵~ */
	INSTANCE;

	/** 提供器的唯一标识符喵~ */
	private static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(MODID, "mutant_potion_cauldron");
	/** NBT 标签键,用于存储炼药锅的材料标志位喵~ */
	private static final String TAG_FLAG = "Flag";

	/**
	 * 创建物品元素列表,用于在 Jade 悬浮窗中显示所需材料喵~
	 *
	 * @param helper Jade 元素辅助工具喵~
	 * @param item 要显示的物品喵~
	 * @return 包含物品图标和文本的元素列表喵~
	 */
	private static List<IElement> makeList(IElementHelper helper, ItemLike item) {
		return List.of(
				helper.smallItem(new ItemStack(item)),
				helper.text((Component.literal(Integer.toString(1))).append("× ").append(item.asItem().getDescription())).message(null)
		);
	}

	/**
	 * 向 Jade 悬浮窗添加炼药锅的状态信息和所需材料提示喵~
	 *
	 * @param iTooltip Jade 悬浮窗对象喵~
	 * @param blockAccessor 方块访问器,用于获取方块实体和玩家信息喵~
	 * @param iPluginConfig 插件配置喵~
	 */
	@Override
	public void appendTooltip(ITooltip iTooltip, BlockAccessor blockAccessor, IPluginConfig iPluginConfig) {
		IElementHelper helper = IElementHelper.get();
		if(blockAccessor.getBlockEntity() instanceof MutantPotionCauldronBlockEntity mutantPotionCauldronBlockEntity) {
			// 从服务端数据同步材料标志位喵~
			if(blockAccessor.getServerData().contains(TAG_FLAG, Tag.TAG_INT)) {
				mutantPotionCauldronBlockEntity.setFlag(blockAccessor.getServerData().getInt(TAG_FLAG));
			}
			// 检查玩家是否解锁了使用炼药锅所需的阶段喵~
			if(!MutantPotionCauldronBlock.hasStageToCovert(blockAccessor.getPlayer())) {
				iTooltip.add(Component.translatable("jade.misc_twf.mutant_potion_cauldron.stage_required"));
			} else if(mutantPotionCauldronBlockEntity.isComplete()) {
				// 所有材料已添加,提示需要玻璃棒搅拌喵~
				iTooltip.add(Component.translatable("jade.misc_twf.mutant_potion_cauldron.need_rod"));
				iTooltip.add(makeList(helper, MISCTWFItems.Materials.GLASS_ROD));
			} else {
				// 显示缺失的材料列表喵~
				iTooltip.add(Component.translatable("jade.misc_twf.mutant_potion_cauldron.need_material"));
				if(!mutantPotionCauldronBlockEntity.containsFlag(MutantPotionCauldronBlockEntity.FLAG_SUGAR)) {
					iTooltip.add(makeList(helper, Items.SUGAR));
				}
				if(!mutantPotionCauldronBlockEntity.containsFlag(MutantPotionCauldronBlockEntity.FLAG_GOLDEN_APPLE)) {
					iTooltip.add(makeList(helper, Items.GOLDEN_APPLE));
				}
				if(!mutantPotionCauldronBlockEntity.containsFlag(MutantPotionCauldronBlockEntity.FLAG_SECOND_BRAIN_CORE)) {
					iTooltip.add(makeList(helper, MISCTWFItems.Materials.SECOND_BRAIN_CORE));
				}
			}
		}
	}

	/**
	 * 向服务端数据中添加炼药锅的材料标志位,用于同步到客户端喵~
	 *
	 * @param compoundTag 服务端数据标签喵~
	 * @param blockAccessor 方块访问器,用于获取方块实体喵~
	 */
	@Override
	public void appendServerData(CompoundTag compoundTag, BlockAccessor blockAccessor) {
		if(blockAccessor.getBlockEntity() instanceof MutantPotionCauldronBlockEntity mutantPotionCauldronBlockEntity) {
			compoundTag.putInt(TAG_FLAG, mutantPotionCauldronBlockEntity.getFlag());
		}
	}

	/**
	 * 获取提供器的唯一标识符喵~
	 *
	 * @return 提供器的资源位置标识符喵~
	 */
	@Override
	public ResourceLocation getUid() {
		return UID;
	}
}
