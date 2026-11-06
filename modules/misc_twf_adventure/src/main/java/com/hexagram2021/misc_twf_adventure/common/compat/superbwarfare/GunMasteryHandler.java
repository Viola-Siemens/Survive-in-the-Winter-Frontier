package com.hexagram2021.misc_twf_adventure.common.compat.superbwarfare;

import com.atsuishio.superbwarfare.entity.projectile.ProjectileEntity;
import com.hexagram2021.misc_twf_adventure.MiscTwfAdventure;
import com.hexagram2021.misc_twf_adventure.common.register.MISCTWFAttributes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import org.jetbrains.annotations.Nullable;

/**
 * 枪械精通伤害加成喵~
 *
 * <p>迁移自 1.18.2 的 TaC `EntityKineticBulletMixin`：当子弹持有者是生物且拥有
 * {@code misc_twf:gun_mastery} 属性时，伤害乘以 {@code 1 + attribute / 100}（属性 0~100，
 * 最大 2 倍）。1.21.1 改用 Superb Warfare 的弹丸实体 + NeoForge 伤害事件实现，不再 Mixin 第三方类喵~</p>
 *
 * @author liudongyu
 */
@EventBusSubscriber(modid = MiscTwfAdventure.MODID)
public final class GunMasteryHandler {
	/**
	 * 在伤害结算前按枪械精通属性放大弹丸伤害喵~
	 *
	 * @param event 生物受伤前事件喵~
	 */
	@SubscribeEvent
	public static void onLivingDamage(LivingDamageEvent.Pre event) {
		Entity direct = event.getSource().getDirectEntity();
		if(!(direct instanceof ProjectileEntity)) {
			return;
		}
		LivingEntity shooter = resolveShooter(event.getSource().getEntity());
		if(shooter == null) {
			return;
		}
		AttributeInstance mastery = shooter.getAttribute(MISCTWFAttributes.GUN_MASTERY);
		if(mastery == null) {
			return;
		}
		float multiplier = 1.0F + (float) (mastery.getValue() / 100.0D);
		if(multiplier != 1.0F) {
			event.setNewDamage(event.getNewDamage() * multiplier);
		}
	}

	@Nullable
	private static LivingEntity resolveShooter(@Nullable Entity owner) {
		return owner instanceof LivingEntity livingEntity ? livingEntity : null;
	}

	private GunMasteryHandler() {
	}
}
