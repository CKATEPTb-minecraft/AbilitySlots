package dev.ckateptb.minecraft.abilityslots.entity;

import dev.ckateptb.minecraft.abilityslots.ability.Ability;
import dev.ckateptb.minecraft.colliders.math.ImmutableVector;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public interface AbilityTarget {
    static EntityAbilityTarget of(Entity entity) {
        return new EntityAbilityTarget(entity);
    }

    static LivingEntityAbilityTarget of(LivingEntity entity) {
        return new LivingEntityAbilityTarget(entity);
    }

    static PlayerAbilityTarget of(Player player) {
        return new PlayerAbilityTarget(player);
    }

    /**
     * Apply {@link Entity#setVelocity(Vector)} for wrapped {@link Entity}.
     *
     * @param velocity – New velocity to travel with
     * @param ability  {@link Ability} that applied Velocity
     */
    void setVelocity(Ability ability, Vector velocity);

    /**
     * Gets this entity's current velocity
     *
     * @return Current traveling velocity of this entity
     */
    ImmutableVector getVelocity();

    /**
     * Gets the ImmutableVector of wrapped entity current position.
     *
     * @return the resulting {@link ImmutableVector}
     */
    ImmutableVector getLocation();

    /**
     * Calculates a vector at the center of the wrapped entity using its height.
     *
     * @return the resulting {@link ImmutableVector}
     */
    ImmutableVector getCenteredLocation();

    /**
     * Returns true if the entity is supported by a block. This value is a
     * state updated by the server and is not recalculated unless the entity
     * moves.
     *
     * @return True if entity is on ground.
     */
    boolean isOnGround();

    /**
     * Calculates the distance between an entity and the ground using precise {@link dev.ckateptb.minecraft.colliders.geometry.RayTraceCollider} colliders.
     *
     * @return the distance in blocks between the wrapped entity and ground or void
     */
    double getDistanceAboveGround();

    /**
     * Calculates the distance between an entity and the ground using precise {@link dev.ckateptb.minecraft.colliders.geometry.RayTraceCollider} colliders.
     *
     * @param ignoreLiquids if false - consider liquid as ground when calculating
     * @return the distance in blocks between the wrapped entity and ground or void
     */
    double getDistanceAboveGround(boolean ignoreLiquids);

    /**
     * Gets the current world of wrapped entity resides in
     *
     * @return World
     */
    World getWorld();

    /**
     * @return true if entity is living
     */
    boolean isLiving();

    /**
     * @return true if entity is player
     */
    boolean isPlayer();

    Entity getHandle();
}
