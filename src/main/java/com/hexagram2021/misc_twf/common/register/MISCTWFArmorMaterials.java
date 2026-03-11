package com.hexagram2021.misc_twf.common.register;

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

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;

/**
 * 模组护甲材料注册类，负责注册所有自定义护甲材料喵~
 *
 * @author liudongyu
 */
public final class MISCTWFArmorMaterials {
	private static final DeferredRegister<ArmorMaterial> REGISTER = DeferredRegister.create(Registries.ARMOR_MATERIAL, MODID);

	/** 旅行者护甲材料喵~ */
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
			List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(MODID, "wayfarer"))),
			8.0F,
			1.5F
	));

	private MISCTWFArmorMaterials() {
	}

	/**
	 * 初始化护甲材料注册，将延迟注册器绑定到模组事件总线喵~
	 *
	 * @param bus 模组事件总线喵~
	 */
	public static void init(IEventBus bus) {
		REGISTER.register(bus);
	}
}
