package com.hexagram2021.misc_twf_hearing.api;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * 噪声快照喵~：对外只读的噪声视图，外部模组只能读取，无法修改内部状态。
 *
 * <p>{@code position} 为发声瞬间的位置快照，不跟随来源实体移动；{@code createdTick} 为首次发声时间，
 * {@code lastEmitTick} 为最近一次有效发声时间（合并时会刷新），评分的新鲜度以 {@code lastEmitTick} 为准喵~</p>
 *
 * @param id           噪声稳定 id（同一 {@code ServerLevel} 内单调递增，合并后保持不变）喵~
 * @param position     发声位置快照喵~
 * @param type         噪声类型喵~
 * @param radius       基础传播半径（格）喵~
 * @param strength     噪声强度（0.0 ~ 1.0）喵~
 * @param createdTick  首次发声的游戏时间（tick）喵~
 * @param lastEmitTick 最近一次发声的游戏时间（tick）喵~
 * @param expireTick   过期时间（tick，不含）喵~
 * @param sourceUuid   来源实体 UUID，可能为空喵~
 * @author liudongyu
 */
public record NoiseSnapshot(long id, Vec3 position, ResourceLocation type, float radius, float strength,
                            long createdTick, long lastEmitTick, long expireTick, @Nullable UUID sourceUuid) {
}
