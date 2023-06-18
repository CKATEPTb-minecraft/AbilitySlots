package dev.ckateptb.minecraft.abilityslots.ray;

import dev.ckateptb.minecraft.colliders.Colliders;
import dev.ckateptb.minecraft.colliders.geometry.RayTraceCollider;
import dev.ckateptb.minecraft.colliders.math.ImmutableVector;
import lombok.RequiredArgsConstructor;
import org.bukkit.World;

/**
 * Обертка для RayTraceCollider,
 * которая упрощает работу с выбором цели.
 */
@RequiredArgsConstructor
public class Ray {
    protected final double distance;
    protected final double size;
    protected final ImmutableVector source;
    protected final ImmutableVector direction;
    protected final World world;

    /**
     * Перейти в режим выбора {@link dev.ckateptb.minecraft.abilityslots.entity.IAbilityTarget}
     */
    public EntityRay entity() {
        return new EntityRay(distance, size, source, direction, world);
    }

    /**
     * Перейти в режим выбора {@link org.bukkit.block.Block}
     */
    public BlockRay block() {
        return new BlockRay(distance, size, source, direction, world);
    }

    /**
     * Перейти в режим выбора {@link ImmutableVector}
     */
    public PositionRay position() {
        return new PositionRay(distance, size, source, direction, world);
    }

    /**
     * Получить {@link RayTraceCollider} для продвинутой работы с выбором цели
     */
    public RayTraceCollider rayTraceCollider() {
        return Colliders.ray(world, source, direction, distance, size);
    }
}
