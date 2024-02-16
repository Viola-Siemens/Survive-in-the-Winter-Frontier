package com.hexagram2021.misc_twf.common.item;

import com.hexagram2021.misc_twf.SurviveInTheWinterFrontier;
import com.hexagram2021.misc_twf.common.config.MISCTWFCommonConfig;
import com.hexagram2021.misc_twf.common.register.MISCTWFItems;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.LazyLoadedValue;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraftforge.energy.CapabilityEnergy;

import javax.annotation.Nullable;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;

public class WayfarerArmorItem extends ArmorItem implements IEnergyItem {
	private static final int[] HEALTH_PER_SLOT = new int[]{13, 15, 16, 11};

	public static final String name = "wayfarer";
	private static final int durabilityMultiplier = 12;
	private static final int[] slotProtections = new int[]{12, 16, 24, 14};
	private static final int enchantmentValue = 20;
	private static final float toughness = 8.0F;
	private static final float knockbackResistance = 1.5F;

	@SuppressWarnings("deprecation")
	private static final LazyLoadedValue<Ingredient> repairIngredient = new LazyLoadedValue<>(() -> Ingredient.of(MISCTWFItems.Materials.WAYFARER_INGOT));

	public static final ArmorMaterial mat = new WayfarerArmorMaterial();

	public WayfarerArmorItem(EquipmentSlot type) {
		super(mat, type, new Properties().stacksTo(1).tab(SurviveInTheWinterFrontier.ITEM_GROUP));
	}

	@Override
	public boolean canBeDepleted() {
		return false;
	}

	@Override
	public boolean isEnchantable(ItemStack itemStack) {
		return this.getItemStackLimit(itemStack) == 1;
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
		return MODID + ":textures/models/armor/" + name + "_layer" + (slot==EquipmentSlot.LEGS?"_legs": "") + ".png";
	}

	@Override
	public int getEnergyCapability() {
		return MISCTWFCommonConfig.WAYFARER_ARMOR_CAPABILITY.get();
	}

	@Override
	public int getMaxEnergyReceiveSpeed() {
		return 5;
	}

	@Override
	public int getMaxEnergyExtractSpeed() {
		return 1;
	}

	@Override
	public void readShareTag(ItemStack stack, @Nullable CompoundTag nbt) {
		if (nbt != null) {
			this.readEnergyShareTag(nbt, stack);
		}
		super.readShareTag(stack, nbt);
	}

	@Nullable
	protected MobEffectInstance getTickedEffect() {
		return switch (this.slot) {
			case HEAD -> new MobEffectInstance(MobEffects.WATER_BREATHING, 40);
			case CHEST -> new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 40);
			case LEGS -> new MobEffectInstance(MobEffects.DIG_SPEED, 40);
			case FEET -> new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 40);
			default -> null;
		};
	}

	@Override
	public void onArmorTick(ItemStack stack, Level level, Player player) {
		if(player.tickCount % 20 == 0) {
			stack.getCapability(CapabilityEnergy.ENERGY).ifPresent(ies -> {
				if(ies.getEnergyStored() > 0) {
					MobEffectInstance effectInstance = this.getTickedEffect();
					if(effectInstance != null) {
						player.addEffect(effectInstance);
					}
					ies.extractEnergy(1, false);
				}
			});
		}
	}

	@Override
	public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> list) {
		if (this.allowdedIn(tab)) {
			ItemStack itemStack = new ItemStack(this);
			this.readShareTag(itemStack, this.getMaxEnergyTag(itemStack));
			list.add(itemStack);
		}
	}

	private static class WayfarerArmorMaterial implements ArmorMaterial {
		@Override
		public int getDurabilityForSlot(EquipmentSlot pSlot) {
			return HEALTH_PER_SLOT[pSlot.getIndex()] * durabilityMultiplier;
		}

		@Override
		public int getDefenseForSlot(EquipmentSlot pSlot) {
			return slotProtections[pSlot.getIndex()];
		}

		@Override
		public int getEnchantmentValue() {
			return enchantmentValue;
		}

		@Override
		public SoundEvent getEquipSound() {
			return SoundEvents.ARMOR_EQUIP_GOLD;
		}

		@Override
		public Ingredient getRepairIngredient() {
			return repairIngredient.get();
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public float getToughness() {
			return toughness;
		}

		@Override
		public float getKnockbackResistance() {
			return knockbackResistance;
		}
	}
}