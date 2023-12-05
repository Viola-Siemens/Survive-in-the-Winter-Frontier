package com.hexagram2021.misc_twf.common.entity.compat;

import com.hexagram2021.misc_twf.common.entity.capability.CapabilityAnimal;
import mcp.mobius.waila.api.EntityAccessor;
import mcp.mobius.waila.api.IEntityComponentProvider;
import mcp.mobius.waila.api.IServerDataProvider;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.config.IPluginConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public enum LivingPoopProvider implements IEntityComponentProvider, IServerDataProvider<Entity> {
	INSTANCE;

	private static final String POOP_CD = "PoopCD";

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendTooltip(ITooltip tooltip, EntityAccessor accessor, IPluginConfig config) {
		if (!accessor.getServerData().contains(POOP_CD, Tag.TAG_INT)) {
			return;
		}
		int time = accessor.getServerData().getInt(POOP_CD);
		if (time > 0) {
			tooltip.add(new TranslatableComponent("jade.misc_twf.poop.time", time / 20));
		}
	}

	@Override
	public void appendServerData(CompoundTag compoundTag, ServerPlayer serverPlayer, Level level, Entity entity, boolean b) {
		entity.getCapability(CapabilityAnimal.POOPING).ifPresent(c -> {
			int time = c.getPoopingRemainingTicks();
			if(time > 0) {
				compoundTag.putInt(POOP_CD, time);
			}
		});
	}
}
