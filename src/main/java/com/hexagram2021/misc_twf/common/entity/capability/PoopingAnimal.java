package com.hexagram2021.misc_twf.common.entity.capability;

import com.hexagram2021.misc_twf.common.register.MISCTWFItems;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

/**
 * 动物排便能力的实现类，管理动物定期生成粪便的逻辑喵~
 * 提供了数据序列化和排便行为的具体实现喵~
 *
 * @author liudongyu
 */
public class PoopingAnimal implements IPoopingAnimal {
	/**
	 * 用于序列化和反序列化排便数据的编解码器喵~
	 */
	public static final Codec<PoopingAnimal> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.INT.fieldOf("RemainingTicks").forGetter(PoopingAnimal::getPoopingRemainingTicks)
	).apply(instance, PoopingAnimal::new));

	/**
	 * 距离下次排便的剩余时间（以刻为单位）喵~
	 */
	protected int remainingTicks = 0;

	/**
	 * 创建一个新的排便能力实例，并使用默认时间初始化计时器喵~
	 */
	public PoopingAnimal() {
		this.resetPoopingTicks();
	}

	/**
	 * 创建一个新的排便能力实例，并指定初始剩余时间喵~
	 *
	 * @param remainingTicks 初始剩余时间（刻）喵~
	 */
	public PoopingAnimal(int remainingTicks) {
		this.remainingTicks = remainingTicks;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getPoopingRemainingTicks() {
		return this.remainingTicks;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setPoopingRemainingTicks(int ticks) {
		this.remainingTicks = ticks;
	}

	/**
	 * {@inheritDoc}
	 * 在实体附近随机位置生成一个动物粪便物品实体喵~
	 */
	@Override
	public void poop(LivingEntity self) {
		self.level().addFreshEntity(new ItemEntity(
				self.level(),
				self.getRandomX(0.5D), self.getRandomY(), self.getRandomZ(0.5D),
				new ItemStack(MISCTWFItems.Materials.ANIMAL_POOP)
		));
	}
}
