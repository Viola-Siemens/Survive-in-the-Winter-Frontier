package com.hexagram2021.misc_twf.common.register;

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

public final class MISCTWFSkills {
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

	private MISCTWFSkills() {
	}

	public static void init(IEventBus bus) {
		SKILLS.register(bus);
		PASSIVES.register(bus);
	}

	private static Passive createPassive(String name, Aptitude aptitude, String textureName, Attribute attribute, String attributeUuid, double attributeValue, int... levelsRequired) {
		return new Passive(new ResourceLocation(MODID, name), aptitude, new ResourceLocation(MODID, textureName), attribute, attributeUuid, attributeValue, levelsRequired);
	}
}
