package dev.ckateptb.minecraft.abilityslots.util;

import dev.ckateptb.minecraft.colliders.Colliders;
import dev.ckateptb.minecraft.colliders.internal.math3.util.FastMath;
import dev.ckateptb.minecraft.colliders.math.ImmutableVector;
import org.bukkit.World;
import org.bukkit.util.Vector;

public class WorldUtils {
    public static double getDistanceAboveGround(World world, Vector vector, boolean ignoreLiquids) {
        double y = vector.getY();
        return y - Colliders.ray(world, vector, ImmutableVector.MINUS_J, FastMath.min(world.getMaxHeight(), y), 0)
                .getFirstBlock(ignoreLiquids, true)
                .map(entry -> Colliders.aabb(entry.getKey()).getMax().getY())
                .orElse(0d);
    }
}
