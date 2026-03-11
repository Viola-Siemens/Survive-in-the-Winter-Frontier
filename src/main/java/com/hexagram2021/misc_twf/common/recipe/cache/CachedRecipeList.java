package com.hexagram2021.misc_twf.common.recipe.cache;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RecipesUpdatedEvent;
import net.neoforged.neoforge.event.TagsUpdatedEvent;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;

/**
 * 配方缓存列表，根据配方类型缓存对应的配方数据，并在标签或配方更新时自动刷新缓存喵~
 * <p>
 * 通过监听 {@link TagsUpdatedEvent} 和 {@link RecipesUpdatedEvent} 事件来维护缓存的有效性喵~
 *
 * @param <R> 配方类型喵~
 *
 * @author liudongyu
 */
@EventBusSubscriber(modid = MODID)
@SuppressWarnings("unused")
public class CachedRecipeList<R extends Recipe<?>> {
	/** 无效的重载计数值，表示缓存尚未初始化喵~ */
	public static final int INVALID_RELOAD_COUNT = -1;
	private static int reloadCount = 0;

	private final Supplier<RecipeType<R>> type;
	private final Class<R> recipeClass;
	@Nullable
	private Map<ResourceLocation, R> recipes;
	private boolean cachedDataIsClient;
	private int cachedAtReloadCount = INVALID_RELOAD_COUNT;

	/**
	 * 构造一个配方缓存列表喵~
	 *
	 * @param type 配方类型的延迟供应器喵~
	 * @param recipeClass 配方的具体 Class 对象，用于类型转换喵~
	 */
	public CachedRecipeList(Supplier<RecipeType<R>> type, Class<R> recipeClass) {
		this.type = type;
		this.recipeClass = recipeClass;
	}

	@SubscribeEvent
	public static void onTagsUpdated(TagsUpdatedEvent event) {
		++reloadCount;
	}

	@SubscribeEvent
	public static void onRecipeUpdatedClient(RecipesUpdatedEvent event) {
		++reloadCount;
	}

	/**
	 * 获取当前的重载计数器值，每次标签或配方更新时递增喵~
	 *
	 * @return 当前重载计数值喵~
	 */
	public static int getReloadCount() {
		return reloadCount;
	}

	/**
	 * 获取所有缓存的配方实例喵~
	 *
	 * @param level 当前世界实例，用于获取配方管理器和判断客户端/服务端喵~
	 * @return 该类型下所有配方的集合喵~
	 */
	public Collection<R> getRecipes(Level level) {
		updateCache(level.getRecipeManager(), level.isClientSide());
		return Objects.requireNonNull(this.recipes).values();
	}

	/**
	 * 获取所有缓存配方的资源路径名称喵~
	 *
	 * @param level 当前世界实例，用于获取配方管理器和判断客户端/服务端喵~
	 * @return 该类型下所有配方名称的集合喵~
	 */
	public Collection<ResourceLocation> getRecipeNames(Level level) {
		updateCache(level.getRecipeManager(), level.isClientSide());
		return Objects.requireNonNull(this.recipes).keySet();
	}

	/**
	 * 根据资源路径名称获取指定的配方喵~
	 *
	 * @param level 当前世界实例，用于获取配方管理器和判断客户端/服务端喵~
	 * @param name 配方的资源路径标识符喵~
	 * @return 对应的配方实例，若不存在则返回 null 喵~
	 */
	public R getById(Level level, ResourceLocation name) {
		updateCache(level.getRecipeManager(), level.isClientSide());
		return Objects.requireNonNull(this.recipes).get(name);
	}

	private void updateCache(RecipeManager manager, boolean isClient) {
		if(this.recipes != null && this.cachedAtReloadCount == reloadCount && (!this.cachedDataIsClient || isClient)) {
			return;
		}
		this.recipes = manager.getRecipes().stream()
				.filter(r -> r.value().getType() == this.type.get())
				.collect(Collectors.toMap(RecipeHolder::id, r -> this.recipeClass.cast(r.value())));
		this.cachedDataIsClient = isClient;
		this.cachedAtReloadCount = reloadCount;
	}
}
