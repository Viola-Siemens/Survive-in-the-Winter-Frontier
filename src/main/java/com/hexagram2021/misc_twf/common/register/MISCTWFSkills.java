package com.hexagram2021.misc_twf.common.register;

/*
import com.hexagram2021.misc_twf.common.config.MISCTWFCommonConfig;
import com.seniors.justlevelingfork.registry.RegistryAptitudes;
import com.seniors.justlevelingfork.registry.RegistryPassives;
import com.seniors.justlevelingfork.registry.RegistrySkills;
import com.seniors.justlevelingfork.registry.aptitude.Aptitude;
import com.seniors.justlevelingfork.registry.passive.Passive;
import com.seniors.justlevelingfork.registry.skills.Skill;
import net.mcbbs.uid1525632.hungerreworkedreforged.init.AttributeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;
 */

import net.neoforged.bus.api.IEventBus;

/**
 * 模组技能系统注册类喵~
 * 用于与 Just Leveling Fork 模组集成，注册自定义技能和被动能力喵~
 * 当前所有技能代码已被注释，等待后续重新实现喵~
 *
 * @author liudongyu
 */
public final class MISCTWFSkills {
	/*
	public static final DeferredRegister<Skill> SKILLS = DeferredRegister.create(RegistrySkills.SKILLS_KEY, MODID);
	public static final DeferredRegister<Passive> PASSIVES = DeferredRegister.create(RegistryPassives.PASSIVES_KEY, MODID);

	public static final RegistryObject<Passive> STRONG_STOMACH = PASSIVES.register("strong_stomach", () -> createPassive(
			"strong_stomach",
			RegistryAptitudes.CONSTITUTION.get(),
			"textures/skill/constitution/strong_stomach.png",
			AttributeRegistration.EXTRA_STOMACH,
			"7203a4bc-d7a1-4d23-a904-214b59cca000",
			20.0D,
			MISCTWFCommonConfig.STRONG_STOMACH_SKILL_LEVELS.get().stream().mapToInt(i -> i).toArray()
	));
	public static final RegistryObject<Passive> GUN_MASTERY = PASSIVES.register("gun_mastery", () -> createPassive(
			"gun_mastery",
			RegistryAptitudes.STRENGTH.get(),
			"textures/skill/strength/gun_mastery.png",
			MISCTWFAttributes.GUN_MASTERY,
			"7203a4bc-d7a1-4d23-a904-214b59cca001",
			50.0D,
			MISCTWFCommonConfig.GUN_MASTERY_SKILL_LEVELS.get().stream().mapToInt(i -> i).toArray()
	));

	private static Passive createPassive(String name, Aptitude aptitude, String textureName, Attribute attribute, String attributeUuid, double attributeValue, int... levelsRequired) {
		return new Passive(new ResourceLocation(MODID, name), aptitude, new ResourceLocation(MODID, textureName), attribute, attributeUuid, attributeValue, levelsRequired);
	}
	 */

	/**
	 * 私有构造函数，防止实例化喵~
	 */
	private MISCTWFSkills() {
	}

	/**
	 * 初始化技能注册器喵~
	 * 当前未实际注册任何内容，等待后续实现喵~
	 *
	 * @param bus 模组事件总线喵~
	 */
	public static void init(IEventBus bus) {
		// SKILLS.register(bus);
		// PASSIVES.register(bus);
	}
}
