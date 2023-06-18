package dev.ckateptb.minecraft.abilityslots.ray;

import dev.ckateptb.minecraft.abilityslots.entity.IAbilityTarget;
import dev.ckateptb.minecraft.colliders.geometry.RayTraceCollider;
import dev.ckateptb.minecraft.colliders.math.ImmutableVector;
import lombok.Getter;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

public class EntityRay extends Ray {
    private final Predicate<Entity> filter;
    @Getter
    private final boolean ignoreLiquids;
    @Getter
    private final boolean ignorePassable;
    @Getter
    private final boolean ignoreBlocks;
    @Getter
    private final boolean livingOnly;

    public EntityRay(double distance, double size, ImmutableVector source, ImmutableVector direction, World world) {
        this(distance, size, source, direction, world, (entity) -> true, true, true, true, true);
    }

    public EntityRay(double distance, double size, ImmutableVector source, ImmutableVector direction, World world,
                     Predicate<Entity> filter, boolean ignoreLiquids, boolean ignorePassable, boolean ignoreBlocks, boolean livingOnly) {
        super(distance, size, source, direction, world);
        this.filter = filter;
        this.ignoreLiquids = ignoreLiquids;
        this.ignorePassable = ignorePassable;
        this.ignoreBlocks = ignoreBlocks;
        this.livingOnly = livingOnly;
    }

    @Override
    public EntityRay entity() {
        return new EntityRay(distance, size, source, direction, world, filter, ignoreLiquids, ignorePassable, ignoreBlocks, livingOnly);
    }

    /**
     * Задать фильтр для итерации {@link Entity}
     */
    public EntityRay filter(Predicate<Entity> filter) {
        return new EntityRay(distance, size, source, direction, world, filter, ignoreLiquids, ignorePassable, ignoreBlocks, livingOnly);
    }

    /**
     * Стоит ли игнорировать жидкости в итерации.
     * Этот параметр не учитывается при условии ignoreBlock
     */
    public EntityRay ignoreLiquids(boolean ignoreLiquids) {
        return new EntityRay(distance, size, source, direction, world, filter, ignoreLiquids, ignorePassable, ignoreBlocks, livingOnly);
    }

    /**
     * Стоит ли игнорировать проходимые блоки в итерации.
     * Этот параметр не учитывается при условии ignoreBlock
     */
    public EntityRay ignorePassable(boolean ignorePassable) {
        return new EntityRay(distance, size, source, direction, world, filter, ignoreLiquids, ignorePassable, ignoreBlocks, livingOnly);
    }

    /**
     * Стоит ли игнорировать {@link Block} в итерации
     */
    public EntityRay ignoreBlocks(boolean ignoreBlocks) {
        return new EntityRay(distance, size, source, direction, world, filter, ignoreLiquids, ignorePassable, ignoreBlocks, livingOnly);
    }

    /**
     * Стоит ли учитывать только {@link LivingEntity}
     */
    public EntityRay livingOnly(boolean livingOnly) {
        return new EntityRay(distance, size, source, direction, world, filter, ignoreLiquids, ignorePassable, ignoreBlocks, livingOnly);
    }

    /**
     * Найти {@link IAbilityTarget<Entity>} учитывая все условия.
     */
    public Optional<IAbilityTarget<Entity>> find() {
        RayTraceCollider rayTraceCollider = this.rayTraceCollider();
        AtomicReference<Double> atomicDistance = new AtomicReference<>((double) -1);
        if (!this.ignoreBlocks) {
            rayTraceCollider.getBlock(ignoreLiquids, ignorePassable, block -> true)
                    .ifPresent(block -> atomicDistance.set(ImmutableVector.of(block.getLocation()).distance(source)));
        }
        return rayTraceCollider.getEntity(entity -> {
            if (livingOnly && !(entity instanceof LivingEntity)) return false;
            return filter.test(entity);
        }).filter(entity -> {
            Double distance = atomicDistance.get();
            if (distance == -1) return true;
            return ImmutableVector.of(entity.getLocation()).distance(source) <= distance;
        }).map(IAbilityTarget::of);
    }
}
