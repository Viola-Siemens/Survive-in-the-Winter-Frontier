package com.hexagram2021.misc_twf.common.block;

import com.hexagram2021.misc_twf.common.block.entity.MonsterEggBlockEntity;
import com.hexagram2021.misc_twf.common.config.MISCTWFCommonConfig;
import com.hexagram2021.misc_twf.common.register.MISCTWFBlockEntities;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

/**
 * 怪物蛋方块，可以孵化出各种怪物喵~
 * <p>
 * 该方块具有以下特性：
 * <ul>
 *   <li>支持含水状态，可以在水中放置喵~</li>
 *   <li>只能放置在坚固的方块表面上方喵~</li>
 *   <li>被实体踩踏、从上方坠落或被玩家破坏时，有概率孵化出怪物喵~</li>
 *   <li>使用实体动画渲染，提供更好的视觉效果喵~</li>
 * </ul>
 * </p>
 *
 * @see MonsterEggBlockEntity
 * @author liudongyu
 */
public class MonsterEggBlock extends BaseEntityBlock {
	/**
	 * 怪物蛋方块的编解码器，用于方块的序列化和反序列化喵~
	 */
	public static final MapCodec<MonsterEggBlock> CODEC = simpleCodec(MonsterEggBlock::new);

	/**
	 * 含水状态属性，表示方块是否处于水中喵~
	 */
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	/**
	 * 怪物蛋方块的碰撞箱形状，大小为 10x12x10 像素喵~
	 */
	protected static final VoxelShape SHAPE = Block.box(3, 0, 3, 13, 12, 13);

	/**
	 * 构造怪物蛋方块喵~
	 *
	 * @param props 方块属性喵~
	 */
	public MonsterEggBlock(Properties props) {
		super(props);
		this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, Boolean.FALSE));
	}

	@Override
	protected MapCodec<? extends BaseEntityBlock> codec() {
		return CODEC;
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.ENTITYBLOCK_ANIMATED;
	}

	@Override @Nullable
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		Level level = context.getLevel();
		BlockPos clickedPos = context.getClickedPos();
		FluidState fluidstate = level.getFluidState(clickedPos);

		BlockState blockstate = this.defaultBlockState().setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
		if (!level.getBlockState(clickedPos.below()).isAir()) {
			return blockstate;
		}

		return null;
	}

	@Override
	public boolean canSurvive(BlockState blockState, LevelReader level, BlockPos pos) {
		BlockPos belowPos = pos.below();
		BlockState belowState = level.getBlockState(belowPos);
		return belowState.isFaceSturdy(level, belowPos, Direction.UP);
	}

	@Override
	public BlockState updateShape(BlockState blockState, Direction direction, BlockState neighbor, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
		return !blockState.canSurvive(level, pos) ? Blocks.AIR.defaultBlockState() : super.updateShape(blockState, direction, neighbor, level, pos, neighborPos);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(WATERLOGGED);
	}

	@Override
	public VoxelShape getShape(BlockState blockState, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	/**
	 * 实体从上方坠落到怪物蛋方块时的处理逻辑喵~
	 * <p>
	 * 当坠落距离大于 1 格，并且随机判定成功时，会破坏蛋并孵化怪物喵~
	 * </p>
	 *
	 * @param level 世界对象喵~
	 * @param blockState 方块状态喵~
	 * @param pos 方块位置喵~
	 * @param entity 坠落的实体喵~
	 * @param fallDistance 坠落距离喵~
	 */
	@Override
	public void fallOn(Level level, BlockState blockState, BlockPos pos, Entity entity, float fallDistance) {
		super.fallOn(level, blockState, pos, entity, fallDistance);
		if (fallDistance > 1 && level.random.nextInt() < MISCTWFCommonConfig.POSSIBILITY_FALL_DESTROY_EGG.get()) {
			this.destroyEgg(level, pos, entity);
		}
	}

	/**
	 * 实体踩踏怪物蛋方块时的处理逻辑喵~
	 * <p>
	 * 随机判定成功时，会破坏蛋并孵化怪物喵~
	 * </p>
	 *
	 * @param level 世界对象喵~
	 * @param pos 方块位置喵~
	 * @param blockState 方块状态喵~
	 * @param entity 踩踏的实体喵~
	 */
	@Override
	public void stepOn(Level level, BlockPos pos, BlockState blockState, Entity entity) {
		if (level.random.nextInt() < MISCTWFCommonConfig.POSSIBILITY_STEP_DESTROY_EGG.get()) {
			this.destroyEgg(level, pos, entity);
		}
	}

	/**
	 * 实体进入怪物蛋方块内部时的处理逻辑喵~
	 * <p>
	 * 随机判定成功时，会破坏蛋并孵化怪物喵~
	 * </p>
	 *
	 * @param blockState 方块状态喵~
	 * @param level 世界对象喵~
	 * @param pos 方块位置喵~
	 * @param entity 进入的实体喵~
	 */
	@Override
	public void entityInside(BlockState blockState, Level level, BlockPos pos, Entity entity) {
		if (level.random.nextInt() < MISCTWFCommonConfig.POSSIBILITY_STEP_DESTROY_EGG.get()) {
			this.destroyEgg(level, pos, entity);
		}
	}

	/**
	 * 玩家破坏怪物蛋方块时的处理逻辑喵~
	 * <p>
	 * 破坏蛋并孵化怪物，同时播放蛋破裂的音效喵~
	 * </p>
	 *
	 * @param level 世界对象喵~
	 * @param player 破坏方块的玩家喵~
	 * @param pos 方块位置喵~
	 * @param blockState 方块状态喵~
	 * @param blockEntity 方块实体喵~
	 * @param stack 使用的工具物品喵~
	 */
	@Override
	public void playerDestroy(Level level, Player player, BlockPos pos, BlockState blockState, @Nullable BlockEntity blockEntity, ItemStack stack) {
		super.playerDestroy(level, player, pos, blockState, blockEntity, stack);
		if(blockEntity instanceof MonsterEggBlockEntity monsterEggBlockEntity) {
			this.destroyEgg(level, pos, monsterEggBlockEntity);
			level.playSound(null, pos, SoundEvents.TURTLE_EGG_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
		}
	}

	/**
	 * 破坏怪物蛋并孵化怪物喵~
	 * <p>
	 * 该方法会在服务端执行以下操作：
	 * <ul>
	 *   <li>从方块实体中获取怪物数据并生成怪物实体喵~</li>
	 *   <li>播放蛋破裂的音效喵~</li>
	 *   <li>移除方块但不掉落物品喵~</li>
	 * </ul>
	 * </p>
	 *
	 * @param level 世界对象喵~
	 * @param pos 方块位置喵~
	 * @param entity 触发破坏的实体（可能为踩踏实体或坠落实体）喵~
	 */
	public void destroyEgg(Level level, BlockPos pos, Entity entity) {
		if (!level.isClientSide) {
			if(level.getBlockEntity(pos) instanceof MonsterEggBlockEntity monsterEggBlockEntity) {
				this.destroyEgg(level, pos, monsterEggBlockEntity);
			}
			level.playSound(null, pos, SoundEvents.TURTLE_EGG_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
			level.destroyBlock(pos, false, entity);
		}
	}

	/**
	 * 从怪物蛋方块实体中提取怪物数据并生成怪物实体喵~
	 *
	 * @param level 世界对象喵~
	 * @param pos 方块位置喵~
	 * @param monsterEggBlockEntity 怪物蛋方块实体喵~
	 */
	private void destroyEgg(Level level, BlockPos pos, MonsterEggBlockEntity monsterEggBlockEntity) {
		Entity monster = monsterEggBlockEntity.createMonster(level);
		if (monster != null) {
			monster.setPosRaw(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
			level.addFreshEntity(monster);
		}
	}

	/**
	 * 方块被放置后，从物品中加载数据到方块实体喵~
	 *
	 * @param level 世界对象喵~
	 * @param blockPos 方块位置喵~
	 * @param blockState 方块状态喵~
	 * @param livingEntity 放置方块的生物（通常是玩家）喵~
	 * @param itemStack 放置的物品堆喵~
	 */
	@Override
	public void setPlacedBy(Level level, BlockPos blockPos, BlockState blockState, @Nullable LivingEntity livingEntity, ItemStack itemStack) {
		if(!level.isClientSide) {
			level.getBlockEntity(blockPos, MISCTWFBlockEntities.MONSTER_EGG.get()).ifPresent(blockEntity -> blockEntity.fromItem(itemStack));
		}
	}

	@Override
	public FluidState getFluidState(BlockState blockState) {
		return blockState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(true) : super.getFluidState(blockState);
	}

	@Override @Nullable
	public MonsterEggBlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
		return new MonsterEggBlockEntity(blockPos, blockState);
	}

	@Override @Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> blockEntityType) {
		return createMonsterEggTicker(level, blockEntityType, MISCTWFBlockEntities.MONSTER_EGG.get());
	}

	/**
	 * 创建怪物蛋的方块实体 Ticker，用于服务端定时更新逻辑喵~
	 *
	 * @param level 世界对象喵~
	 * @param blockEntityType1 当前方块实体类型喵~
	 * @param blockEntityType2 目标怪物蛋方块实体类型喵~
	 * @param <T> 方块实体类型参数喵~
	 * @return 方块实体 Ticker，客户端返回 null，服务端返回实际 Ticker 喵~
	 */
	@Nullable
	protected static <T extends BlockEntity> BlockEntityTicker<T> createMonsterEggTicker(Level level, BlockEntityType<T> blockEntityType1, BlockEntityType<? extends MonsterEggBlockEntity> blockEntityType2) {
		return level.isClientSide ? null : createTickerHelper(blockEntityType1, blockEntityType2, MonsterEggBlockEntity::serverTick);
	}
}
