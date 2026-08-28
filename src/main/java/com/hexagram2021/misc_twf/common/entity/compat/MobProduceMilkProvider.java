package com.hexagram2021.misc_twf.common.entity.compat;

import com.hexagram2021.misc_twf.common.entity.IProduceMilk;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;

/**
 * 动物产奶信息的 Jade/WAILA 提示提供者喵~
 * 在客户端为目标动物显示产奶冷却时间，在服务端从实现了 IProduceMilk 接口的实体中读取冷却信息喵~
 *
 * @author liudongyu
 */
public enum MobProduceMilkProvider implements IEntityComponentProvider, IServerDataProvider<EntityAccessor> {
	INSTANCE;

	private static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(MODID, "mob_produce_milk");
	private static final String MILK_CD = "MilkCD";

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendTooltip(ITooltip tooltip, EntityAccessor accessor, IPluginConfig config) {
		if (!accessor.getServerData().contains(MILK_CD, Tag.TAG_INT)) {
			return;
		}
		int time = accessor.getServerData().getInt(MILK_CD);
		if (time > 0) {
			tooltip.add(Component.translatable("jade.misc_twf.producemilk.time", time / 20));
		}
	}

	@Override
	public void appendServerData(CompoundTag compoundTag, EntityAccessor entityAccessor) {
		Entity entity = entityAccessor.getEntity();
		if(entity instanceof IProduceMilk produceMilk) {
			int time = produceMilk.misc_twf$getMilkCoolDown();
			if(time > 0) {
				compoundTag.putInt(MILK_CD, time);
			}
		}
	}


	@Override
	public ResourceLocation getUid() {
		return UID;
	}
}
