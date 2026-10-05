package com.hexagram2021.misc_twf_industry.common.infrastructure.compat;

import com.hexagram2021.misc_twf_industry.common.util.MISCTWFLogger;
import com.tterrag.registrate.AbstractRegistrate;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;

/**
 * 模组注册器
 *
 * @author liudongyu
 */
public class MISCTWFRegistrate extends AbstractRegistrate<MISCTWFRegistrate> {
	private final String actualModId;

	protected MISCTWFRegistrate(String actualModId, String namespace) {
		super(namespace);
		this.actualModId = actualModId;
	}

	/**
	 * 实际模组 ID——与注册项命名空间不同
	 * @return 实际模组 ID
	 */
	public String getActualModId() {
		return this.actualModId;
	}

	/**
	 * 注册器工厂函数
	 *
	 * @param actualModId 实际模组 ID
	 * @param namespace 注册项命名空间
	 * @return 新建的注册器
	 */
	public static MISCTWFRegistrate create(String actualModId, String namespace) {
		MISCTWFRegistrate ret = new MISCTWFRegistrate(actualModId, namespace);
		ModList.get().getModContainerById(actualModId).map(ModContainer::getEventBus)
				.ifPresentOrElse(ret::registerEventListeners, () -> {
					String message = "# [Registrate] Failed to register eventListeners for mod " + actualModId + " with namespace " + namespace + ", This should be reported to this mod's dev #";
					StringBuilder hashtags = new StringBuilder().append("#".repeat(message.length()));
					MISCTWFLogger.fatal(hashtags.toString());
					MISCTWFLogger.fatal(message);
					MISCTWFLogger.fatal(hashtags.toString());
				});
		return ret;
	}
}
