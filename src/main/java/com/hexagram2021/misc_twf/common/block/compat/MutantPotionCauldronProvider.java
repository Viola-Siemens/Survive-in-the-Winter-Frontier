package com.hexagram2021.misc_twf.common.block.compat;

import com.hexagram2021.misc_twf.common.block.MutantPotionCauldronBlock;
import com.hexagram2021.misc_twf.common.block.entity.MutantPotionCauldronBlockEntity;
import com.hexagram2021.misc_twf.common.register.MISCTWFItems;
import mcp.mobius.waila.api.BlockAccessor;
import mcp.mobius.waila.api.IComponentProvider;
import mcp.mobius.waila.api.IServerDataProvider;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.config.IPluginConfig;
import mcp.mobius.waila.api.ui.IElement;
import mcp.mobius.waila.api.ui.IElementHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import snownee.jade.Jade;
import snownee.jade.VanillaPlugin;

import java.util.List;

public enum MutantPotionCauldronProvider implements IComponentProvider, IServerDataProvider<BlockEntity> {
	INSTANCE;

	private static final String TAG_FLAG = "Flag";

	private static List<IElement> makeList(IElementHelper helper, ItemLike item) {
		return List.of(
				Jade.smallItem(helper, new ItemStack(item)),
				helper.text((new TextComponent(Integer.toString(1))).append("× ").append(item.asItem().getDescription())).tag(VanillaPlugin.INVENTORY).message(null)
		);
	}

	@Override
	public void appendTooltip(ITooltip iTooltip, BlockAccessor blockAccessor, IPluginConfig iPluginConfig) {
		IElementHelper helper = iTooltip.getElementHelper();
		if(blockAccessor.getBlockEntity() instanceof MutantPotionCauldronBlockEntity mutantPotionCauldronBlockEntity) {
			if(blockAccessor.getServerData().contains(TAG_FLAG, Tag.TAG_INT)) {
				mutantPotionCauldronBlockEntity.setFlag(blockAccessor.getServerData().getInt(TAG_FLAG));
			}
			if(!MutantPotionCauldronBlock.hasStageToCovert(blockAccessor.getPlayer())) {
				iTooltip.add(new TranslatableComponent("jade.misc_twf.mutant_potion_cauldron.stage_required"));
			} else if(mutantPotionCauldronBlockEntity.isComplete()) {
				iTooltip.add(new TranslatableComponent("jade.misc_twf.mutant_potion_cauldron.need_rod"));
				iTooltip.add(makeList(helper, MISCTWFItems.Materials.GLASS_ROD));
			} else {
				iTooltip.add(new TranslatableComponent("jade.misc_twf.mutant_potion_cauldron.need_material"));
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

	@Override
	public void appendServerData(CompoundTag compoundTag, ServerPlayer serverPlayer, Level level, BlockEntity blockEntity, boolean b) {
		if(blockEntity instanceof MutantPotionCauldronBlockEntity mutantPotionCauldronBlockEntity) {
			compoundTag.putInt(TAG_FLAG, mutantPotionCauldronBlockEntity.getFlag());
		}
	}
}
