package com.hexagram2021.misc_twf_hearing.common.register;

import com.hexagram2021.misc_twf_hearing.MiscTwfHearing;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

/**
 * 循声模块的 Attachment 注册喵~
 *
 * <p>唯一的附件是"循声 Goal 已注入"标记：**非持久化**（不提供 serializer），
 * 因此退出重进世界时实体会被重新注入一次，而不会因存档残留导致漏注入喵~</p>
 *
 * @author liudongyu
 */
public final class MISCTWFHearingAttachments {
	private static final DeferredRegister<AttachmentType<?>> REGISTER =
			DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, MiscTwfHearing.MODID);

	/** 循声 Goal 注入标记（非持久化）喵~ */
	public static final DeferredHolder<AttachmentType<?>, AttachmentType<Boolean>> HEARING_GOAL_ATTACHED =
			REGISTER.register("hearing_goal_attached", () -> AttachmentType.builder(() -> Boolean.FALSE).build());

	/**
	 * 注册附件类型喵~
	 *
	 * @param modBus 模块事件总线喵~
	 */
	public static void init(IEventBus modBus) {
		REGISTER.register(modBus);
	}

	private MISCTWFHearingAttachments() {
	}
}
