package dev.ckateptb.minecraft.abilityslots.ray;

import dev.ckateptb.minecraft.colliders.math.ImmutableVector;
import lombok.Getter;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.Optional;
import java.util.function.Predicate;

public class BlockRay extends Ray {
    private final Predicate<Block> filter;
    @Getter
    private final boolean ignoreLiquids;
    @Getter
    private final boolean ignorePassable;

    public BlockRay(double distance, double size, ImmutableVector source, ImmutableVector direction, World world) {
        this(distance, size, source, direction, world, (block) -> true, true, true);
    }

    public BlockRay(double distance, double size, ImmutableVector source, ImmutableVector direction, World world,
                    Predicate<Block> filter, boolean ignoreLiquids, boolean ignorePassable) {
        super(distance, size, source, direction, world);
        this.filter = filter;
        this.ignoreLiquids = ignoreLiquids;
        this.ignorePassable = ignorePassable;
    }

    @Override
    public BlockRay block() {
        return new BlockRay(distance, size, source, direction, world, filter, ignoreLiquids, ignorePassable);
    }

    /**
     * Стоит ли игнорировать {@link Block} в итерации
     */
    public BlockRay filter(Predicate<Block> filter) {
        return new BlockRay(distance, size, source, direction, world, filter, ignoreLiquids, ignorePassable);
    }

    /**
     * Стоит ли игнорировать жидкости в итерации.
     * Этот параметр не учитывается при условии ignoreBlock
     */
    public BlockRay ignoreLiquids(boolean ignoreLiquids) {
        return new BlockRay(distance, size, source, direction, world, filter, ignoreLiquids, ignorePassable);
    }

    /**
     * Стоит ли игнорировать проходимые блоки в итерации.
     * Этот параметр не учитывается при условии ignoreBlock
     */
    public BlockRay ignorePassable(boolean ignorePassable) {
        return new BlockRay(distance, size, source, direction, world, filter, ignoreLiquids, ignorePassable);
    }

    /**
     * Найти {@link Block} учитывая все условия.
     */
    public Optional<Block> find() {
        return this.rayTraceCollider().getBlock(ignoreLiquids, ignorePassable, filter);
    }
}
