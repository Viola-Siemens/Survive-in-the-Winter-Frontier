package com.hexagram2021.misc_twf;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

/**
 * 根工程主类喵~
 *
 * <p>根工程自 M4 迁移完成后**不再承载业务代码**：内容分布在
 * {@code misc_twf_zombie_animals}、{@code misc_twf_wildlife}、{@code misc_twf_industry}、
 * {@code misc_twf_hearing}、{@code misc_twf_adventure} 五个模块中，并通过 **jar-in-jar** 内嵌进本 jar，
 * 因此整合包只需安装本 jar 即可获得全部内容（也可单独安装模块）喵~</p>
 *
 * <p>保留 modid {@code misc_twf} 是为了：旧存档/数据包中的 {@code misc_twf:*} 内容命名空间不变，
 * 以及聚合 jar 在模组列表中有稳定的身份喵~</p>
 *
 * @author liudongyu
 */
@Mod(SurviveInTheWinterFrontier.MODID)
public class SurviveInTheWinterFrontier {
	/** 聚合工程 mod id（内容命名空间同为 misc_twf）喵~ */
	public static final String MODID = "misc_twf";
	/** 内容命名空间喵~ */
	public static final String CONTENT_NAMESPACE = "misc_twf";

	/**
	 * 聚合工程构造方法喵~
	 *
	 * <p>本类不注册任何内容：所有注册逻辑都在内嵌的五个模块中，由各自的 {@code @Mod} 主类完成喵~</p>
	 *
	 * @param modBus       模块事件总线喵~
	 * @param modContainer 模块容器喵~
	 */
	public SurviveInTheWinterFrontier(IEventBus modBus, ModContainer modContainer) {
		// 聚合 jar 无业务代码；如需全局初始化，在此处添加喵~
	}
}
