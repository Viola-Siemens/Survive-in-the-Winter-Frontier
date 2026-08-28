package com.hexagram2021.misc_twf.common.entity.compat;

import com.hexagram2021.misc_twf.common.register.MISCTWFAttachmentTypes;
import com.hexagram2021.misc_twf.common.register.MISCTWFEntityTags;
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
 * 动物排便信息的 Jade/WAILA 提示提供者喵~
 * 在客户端为目标动物显示排便冷却时间，在服务端从动物的能力数据中读取冷却信息喵~
 *
 * @author liudongyu
 */
public enum LivingPoopProvider implements IEntityComponentProvider, IServerDataProvider<EntityAccessor> {
	INSTANCE;

	private static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(MODID, "living_poop");
	private static final String POOP_CD = "PoopCD";

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendTooltip(ITooltip tooltip, EntityAccessor accessor, IPluginConfig config) {
		if (!accessor.getServerData().contains(POOP_CD, Tag.TAG_INT)) {
			return;
		}
		int time = accessor.getServerData().getInt(POOP_CD);
		if (time > 0) {
			tooltip.add(Component.translatable("jade.misc_twf.poop.time", time / 20));
		}
	}

	@Override
	public void appendServerData(CompoundTag compoundTag, EntityAccessor entityAccessor) {
		Entity entity = entityAccessor.getEntity();
		if(entity.getType().is(MISCTWFEntityTags.POOPING_ANIMALS)) {
			int time = entity.getData(MISCTWFAttachmentTypes.POOPING).getPoopingRemainingTicks();
			if (time > 0) {
				compoundTag.putInt(POOP_CD, time);
			}
		}
	}

	@Override
	public ResourceLocation getUid() {
		return UID;
	}
}
