package com.hexagram2021.misc_twf.common;

import com.hexagram2021.misc_twf.common.entity.ZombieAnimalEntity;
import com.hexagram2021.misc_twf.common.network.ClientboundMonsterEggAnimationPacket;
import com.hexagram2021.misc_twf.common.network.ServerboundOpenTacBackpackPacket;
import com.hexagram2021.misc_twf.common.register.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;

/**
 * 模组内容注册和初始化管理类喵~
 * 负责统一管理模组的所有注册器初始化，包括方块、物品、实体、配方等核心内容喵~
 *
 * @author liudongyu
 */
@EventBusSubscriber(modid = MODID)
public final class MISCTWFContent {
	/**
	 * 模组构造阶段的主入口方法喵~
	 * 按照依赖顺序初始化所有注册器，包括数据附件、属性、流体、方块、物品、实体等喵~
	 *
	 * @param bus 模组事件总线喵~
	 */
	public static void modConstruct(IEventBus bus) {
		initTags();

		MISCTWFAttachmentTypes.init(bus);
		MISCTWFAttributes.init(bus);
		MISCTWFBlockStateProperties.init();
		MISCTWFDataComponentTypes.init(bus);
		MISCTWFFluids.init(bus);
		MISCTWFBlocks.init(bus);
		MISCTWFBlockEntities.init(bus);
		MISCTWFItems.init(bus);
		MISCTWFEntities.init(bus);
		MISCTWFRecipeTypes.init(bus);
		MISCTWFRecipeSerializers.init(bus);
		MISCTWFMobEffects.init(bus);
		MISCTWFMenuTypes.init(bus);
		MISCTWFTravelersBackpackTacOps.init(bus);

		MISCTWFSkills.init(bus);
	}

	/**
	 * 初始化所有标签（Tag）定义喵~
	 */
	private static void initTags() {
		MISCTWFItemTags.init();
	}

	/**
	 * 监听通用注册事件，用于注册声音事件喵~
	 *
	 * @param event 注册事件喵~
	 */
	@SubscribeEvent
	public static void onRegister(RegisterEvent event) {
		event.register(Registries.SOUND_EVENT, MISCTWFSounds::init);
	}

	/**
	 * 注册酿造配方喵~
	 *
	 * @param event 酿造配方注册事件喵~
	 */
	@SubscribeEvent
	public static void registerPotions(RegisterBrewingRecipesEvent event) {
		MISCTWFBrewingRecipes.init(event.getBuilder());
	}

	/**
	 * 注册结构特征喵~
	 *
	 * @param event 结构特征注册事件喵~
	 */
	@SubscribeEvent
	public static void registerStructures(RegistryEvent.Register<StructureFeature<?>> event) {
		MISCTWFStructures.init(event.getRegistry()::register);
		MISCTWFStructurePieceTypes.init();
		MISCTWFConfiguredStructures.init();
		MISCTWFStructureSets.init();
	}

	/**
	 * 注册世界生成特征喵~
	 *
	 * @param event 特征注册事件喵~
	 */
	@SubscribeEvent
	public static void registerFeatures(RegistryEvent.Register<Feature<?>> event) {
		MISCTWFFeatures.init(event.getRegistry()::register);
		MISCTWFConfiguredFeatures.init();
		MISCTWFPlacedFeatures.init();
	}

	/**
	 * 创建自定义实体的属性喵~
	 * 为所有僵尸动物实体配置生命值、移动速度和攻击力等属性喵~
	 *
	 * @param event 实体属性创建事件喵~
	 */
	@SubscribeEvent
	public static void onAttributeCreate(EntityAttributeCreationEvent event) {
		event.put(MISCTWFEntities.ZOMBIE_CHICKEN.get(), ZombieAnimalEntity.createAttributes(4.0D, 0.25D).build());
		event.put(MISCTWFEntities.ZOMBIE_COW.get(), ZombieAnimalEntity.createAttributes(10.0D, 0.25D).add(Attributes.ATTACK_DAMAGE, 3.0D).build());
		event.put(MISCTWFEntities.ZOMBIE_GOAT.get(), ZombieAnimalEntity.createAttributes(10.0D, 0.3D).build());
		event.put(MISCTWFEntities.ZOMBIE_PIG.get(), ZombieAnimalEntity.createAttributes(10.0D, 0.25D).build());
		event.put(MISCTWFEntities.ZOMBIE_POLAR_BEAR.get(), ZombieAnimalEntity.createAttributes(30.0D, 0.25D).add(Attributes.ATTACK_DAMAGE, 6.0D).build());
		event.put(MISCTWFEntities.ZOMBIE_RABBIT.get(), ZombieAnimalEntity.createAttributes(3.0D, 0.3D).build());
		event.put(MISCTWFEntities.ZOMBIE_SHEEP.get(), ZombieAnimalEntity.createAttributes(8.0D, 0.23D).build());
		event.put(MISCTWFEntities.ZOMBIE_WOLF.get(), ZombieAnimalEntity.createAttributes(8.0D, 0.3D).add(Attributes.ATTACK_DAMAGE, 2.0D).build());
	}

	/**
	 * 修改原版实体的默认属性喵~
	 * 为玩家添加枪械精通度属性喵~
	 *
	 * @param event 实体属性修改事件喵~
	 */
	@SubscribeEvent
	public static void onModifyDefaultAttributes(EntityAttributeModificationEvent event) {
		event.add(EntityType.PLAYER, MISCTWFAttributes.GUN_MASTERY, 0.0D);
	}

	/**
	 * 注册网络包处理器喵~
	 * @param event 网络包处理器注册事件喵~
	 */
	@SubscribeEvent
	public static void networkRegistry(RegisterPayloadHandlersEvent event) {
		event.registrar("1")
				.commonToClient(
						ClientboundMonsterEggAnimationPacket.TYPE,
						ClientboundMonsterEggAnimationPacket.STREAM_CODEC,
						ClientboundMonsterEggAnimationPacket::handle
				)
				.commonToServer(
						ServerboundOpenTacBackpackPacket.TYPE,
						ServerboundOpenTacBackpackPacket.STREAM_CODEC,
						ServerboundOpenTacBackpackPacket::handle
				);
	}

	private MISCTWFContent() {
	}
}
