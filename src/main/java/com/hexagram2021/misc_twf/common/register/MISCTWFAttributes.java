package com.hexagram2021.misc_twf.common.register;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;

import java.util.function.Consumer;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;

public final class MISCTWFAttributes {
	public static final Attribute EXTRA_STOMACH = new RangedAttribute(
			"attribute.name.misc_twf.extra_stomach", 0.0D, -1.0D, 20.0D
	);
	public static final Attribute GUN_MASTERY = new RangedAttribute(
			"attribute.name.misc_twf.gun_mastery", 0.0D, -100.0D, 100.0D
	);

	private MISCTWFAttributes() {
	}

	public static void init(Consumer<Attribute> register) {
		EXTRA_STOMACH.setRegistryName(MODID, "extra_stomach");
		GUN_MASTERY.setRegistryName(MODID, "gun_mastery");
		register.accept(EXTRA_STOMACH);
		register.accept(GUN_MASTERY);
	}
}
