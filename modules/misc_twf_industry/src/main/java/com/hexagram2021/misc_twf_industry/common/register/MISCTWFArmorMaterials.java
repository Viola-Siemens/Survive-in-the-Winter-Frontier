package com.hexagram2021.misc_twf_industry.common.register;

import net.minecraft.Util;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.EnumMap;
import java.util.List;

import static com.hexagram2021.misc_twf_industry.MiscTwfIndustry.CONTENT_NAMESPACE;

/**
 * 工业生态模块护甲材料注册类喵~
 *
 * @author liudongyu
 */
public final class MISCTWFArmorMaterials {
	private static final DeferredRegister<ArmorMaterial> REGISTER = DeferredRegister.create(Registries.ARMOR_MATERIAL, CONTENT_NAMESPACE);

	/** 远行者护甲材料喵~ */
	public static final DeferredHolder<ArmorMaterial, ArmorMaterial> WAYFARER = REGISTER.register("wayfarer", () -> new ArmorMaterial(
			Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
				map.put(ArmorItem.Type.BOOTS, 12);
				map.put(ArmorItem.Type.LEGGINGS, 16);
				map.put(ArmorItem.Type.CHESTPLATE, 24);
				map.put(ArmorItem.Type.HELMET, 14);
				map.put(ArmorItem.Type.BODY, 28);
			}),
			20,
			SoundEvents.ARMOR_EQUIP_GOLD,
			() -> Ingredient.of(MISCTWFItems.Materials.WAYFARER_INGOT),
			List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(CONTENT_NAMESPACE, "wayfarer"))),
			8.0F,
			1.5F
	));

	/**
	 * 初始化护甲材料注册喵~
	 *
	 * @param bus 模组事件总线喵~
	 */
	public static void init(IEventBus bus) {
		REGISTER.register(bus);
	}

	private MISCTWFArmorMaterials() {
	}
}
