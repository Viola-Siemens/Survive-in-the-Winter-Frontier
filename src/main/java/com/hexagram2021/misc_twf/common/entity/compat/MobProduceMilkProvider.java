package com.hexagram2021.misc_twf.common.entity.compat;

import com.hexagram2021.misc_twf.common.entity.IProduceMilk;
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

public enum MobProduceMilkProvider implements IEntityComponentProvider, IServerDataProvider<Entity> {
	INSTANCE;

	private static final String MILK_CD = "MilkCD";

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendTooltip(ITooltip tooltip, EntityAccessor accessor, IPluginConfig config) {
		if (!accessor.getServerData().contains(MILK_CD, Tag.TAG_INT)) {
			return;
		}
		int time = accessor.getServerData().getInt(MILK_CD);
		if (time > 0) {
			tooltip.add(new TranslatableComponent("jade.misc_twf.producemilk.time", time / 20));
		}
	}

	@Override
	public void appendServerData(CompoundTag compoundTag, ServerPlayer serverPlayer, Level level, Entity entity, boolean b) {
		if(entity instanceof IProduceMilk produceMilk) {
			int time = produceMilk.getMilkCoolDown();
			if(time > 0) {
				compoundTag.putInt(MILK_CD, time);
			}
		}
	}
}
