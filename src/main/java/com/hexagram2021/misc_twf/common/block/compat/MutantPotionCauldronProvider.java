package com.hexagram2021.misc_twf.common.block.compat;

import com.hexagram2021.misc_twf.common.block.entity.MutantPotionCauldronBlockEntity;
import com.hexagram2021.misc_twf.common.register.MISCTWFItems;
import mcp.mobius.waila.api.BlockAccessor;
import mcp.mobius.waila.api.IComponentProvider;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.config.IPluginConfig;
import mcp.mobius.waila.impl.ui.ItemStackElement;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public enum MutantPotionCauldronProvider implements IComponentProvider {
	INSTANCE;

	@Override
	public void appendTooltip(ITooltip iTooltip, BlockAccessor blockAccessor, IPluginConfig iPluginConfig) {
		if(blockAccessor.getBlockEntity() instanceof MutantPotionCauldronBlockEntity mutantPotionCauldronBlockEntity) {
			if(mutantPotionCauldronBlockEntity.isComplete()) {
				iTooltip.add(new TranslatableComponent("jade.misc_twf.mutant_potion_cauldron.need_rod"));
				iTooltip.add(ItemStackElement.of(new ItemStack(MISCTWFItems.Materials.GLASS_ROD), 0.5F));
			} else {
				iTooltip.add(new TranslatableComponent("jade.misc_twf.mutant_potion_cauldron.need_material"));
				if(!mutantPotionCauldronBlockEntity.containsFlag(MutantPotionCauldronBlockEntity.FLAG_SUGAR)) {
					iTooltip.add(ItemStackElement.of(new ItemStack(Items.SUGAR), 0.5F));
				}
				if(!mutantPotionCauldronBlockEntity.containsFlag(MutantPotionCauldronBlockEntity.FLAG_GOLDEN_APPLE)) {
					iTooltip.add(ItemStackElement.of(new ItemStack(Items.GOLDEN_APPLE), 0.5F));
				}
				if(!mutantPotionCauldronBlockEntity.containsFlag(MutantPotionCauldronBlockEntity.FLAG_SECOND_BRAIN_CORE)) {
					iTooltip.add(ItemStackElement.of(new ItemStack(MISCTWFItems.Materials.SECOND_BRAIN_CORE), 0.5F));
				}
			}
		}
	}
}
