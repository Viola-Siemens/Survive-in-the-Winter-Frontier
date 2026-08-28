package com.hexagram2021.misc_twf.common.register;

import com.hexagram2021.misc_twf.common.ForgeEventHandler;
import com.hexagram2021.misc_twf.common.entity.capability.PoopingAnimal;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;

/**
 * 附件类型注册类，管理 NeoForge 的 AttachmentType 注册喵~
 * 附件类型用于将自定义数据附加到游戏实体上，无需继承或修改原始类喵~
 *
 * @author liudongyu
 */
public class MISCTWFAttachmentTypes {
	private static final DeferredRegister<AttachmentType<?>> REGISTER = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, MODID);

	/**
	 * 动物排便能力附件类型喵~
	 * 该附件类型用于为生物实体附加排便行为和计时功能喵~
	 */
	public static final DeferredHolder<AttachmentType<?>, AttachmentType<PoopingAnimal>> POOPING = REGISTER.register(
			ForgeEventHandler.POOPING.getPath(),
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
