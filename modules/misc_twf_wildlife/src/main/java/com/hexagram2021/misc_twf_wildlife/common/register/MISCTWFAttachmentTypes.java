package com.hexagram2021.misc_twf_wildlife.common.register;

import com.hexagram2021.misc_twf_wildlife.common.entity.capability.PoopingAnimal;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import static com.hexagram2021.misc_twf_wildlife.MiscTwfWildlife.CONTENT_NAMESPACE;

/**
 * 农牧生态模块附件类型注册类喵~
 *
 * @author liudongyu
 */
public final class MISCTWFAttachmentTypes {
	private static final DeferredRegister<AttachmentType<?>> REGISTER = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, CONTENT_NAMESPACE);

	/** 动物排便能力附件类型喵~ */
	public static final DeferredHolder<AttachmentType<?>, AttachmentType<PoopingAnimal>> POOPING = REGISTER.register(
			"pooping",
			() -> AttachmentType.builder(() -> new PoopingAnimal())
					.serialize(PoopingAnimal.CODEC)
					.copyHandler((attachment, holder, provider) -> new PoopingAnimal(attachment.getPoopingRemainingTicks()))
					.build()
	);

	/**
	 * 初始化并注册所有附件类型到事件总线喵~
	 *
	 * @param bus NeoForge 事件总线喵~
	 */
	public static void init(IEventBus bus) {
		REGISTER.register(bus);
	}

	private MISCTWFAttachmentTypes() {
	}
}
