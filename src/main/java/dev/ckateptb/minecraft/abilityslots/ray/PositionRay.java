package dev.ckateptb.minecraft.abilityslots.ray;

import dev.ckateptb.minecraft.colliders.math.ImmutableVector;
import lombok.Getter;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

import java.util.Optional;
import java.util.function.Predicate;

public class PositionRay extends Ray {
    private final Predicate<Entity> entityFilter;
    private final Predicate<Block> blockFilter;
    @Getter
    private final boolean ignoreEntity;
    @Getter
    private final boolean ignoreBlock;
    @Getter
    private final boolean ignoreLiquid;
    @Getter
    private final boolean ignorePassable;

    public PositionRay(double distance, double size, ImmutableVector source, ImmutableVector direction, World world) {
        this(distance, size, source, direction, world, entity -> true, block -> true, true, true, true, true);
    }

    public PositionRay(double distance, double size, ImmutableVector source, ImmutableVector direction, World world,
                       Predicate<Entity> entityFilter, Predicate<Block> blockFilter, boolean ignoreEntity,
                       boolean ignoreBlock, boolean ignoreLiquid, boolean ignorePassable
    ) {
        super(distance, size, source, direction, world);
        this.entityFilter = entityFilter;
        this.blockFilter = blockFilter;
        this.ignoreEntity = ignoreEntity;
        this.ignoreBlock = ignoreBlock;
        this.ignoreLiquid = ignoreLiquid;
        this.ignorePassable = ignorePassable;
    }

    @Override
    public PositionRay position() {
        return new PositionRay(distance, size, source, direction, world, entityFilter, blockFilter, ignoreEntity, ignoreBlock, ignoreLiquid, ignorePassable);
    }

    /**
     * Задать фильтр для итерации {@link Entity}
     */
    public PositionRay entityFilter(Predicate<Entity> entityFilter) {
        return new PositionRay(distance, size, source, direction, world, entityFilter, blockFilter, ignoreEntity, ignoreBlock, ignoreLiquid, ignorePassable);
    }

    /**
     * Задать фильтр для итерации {@link Block}
     */
    public PositionRay blockFilter(Predicate<Block> blockFilter) {
        return new PositionRay(distance, size, source, direction, world, entityFilter, blockFilter, ignoreEntity, ignoreBlock, ignoreLiquid, ignorePassable);
    }

    /**
     * Стоит ли игнорировать {@link Entity} в итерации
     */
    public PositionRay ignoreEntity(boolean ignoreEntity) {
        return new PositionRay(distance, size, source, direction, world, entityFilter, blockFilter, ignoreEntity, ignoreBlock, ignoreLiquid, ignorePassable);
    }

    /**
     * Стоит ли игнорировать {@link Block} в итерации
     */
    public PositionRay ignoreBlock(boolean ignoreBlock) {
        return new PositionRay(distance, size, source, direction, world, entityFilter, blockFilter, ignoreEntity, ignoreBlock, ignoreLiquid, ignorePassable);
    }

    /**
     * Стоит ли игнорировать жидкости в итерации.
     * Этот параметр не учитывается при условии ignoreBlock
     */
    public PositionRay ignoreLiquid(boolean ignoreLiquid) {
        return new PositionRay(distance, size, source, direction, world, entityFilter, blockFilter, ignoreEntity, ignoreBlock, ignoreLiquid, ignorePassable);
    }

    /**
     * Стоит ли игнорировать проходимые блоки в итерации.
     * Этот параметр не учитывается при условии ignoreBlock
     */
    public PositionRay ignorePassable(boolean ignorePassable) {
        return new PositionRay(distance, size, source, direction, world, entityFilter, blockFilter, ignoreEntity, ignoreBlock, ignoreLiquid, ignorePassable);
    }

    /**
     * Найти {@link ImmutableVector} учитывая все условия.
     */
    public Optional<ImmutableVector> find() {
        return this.rayTraceCollider().getPosition(ignoreEntity, ignoreBlock, ignoreLiquid, ignorePassable, entityFilter, blockFilter)
                .map(ImmutableVector::of);
    }
}
