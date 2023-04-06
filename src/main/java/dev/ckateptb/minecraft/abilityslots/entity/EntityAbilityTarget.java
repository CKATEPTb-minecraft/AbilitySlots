package dev.ckateptb.minecraft.abilityslots.entity;

import dev.ckateptb.minecraft.abilityslots.ability.Ability;
import dev.ckateptb.minecraft.abilityslots.util.WorldUtils;
import dev.ckateptb.minecraft.colliders.math.ImmutableVector;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public class EntityAbilityTarget implements AbilityTarget {
    protected final Entity entity;

    public EntityAbilityTarget(Entity entity) {
        this.entity = entity;
    }

    @Override
    public void setVelocity(Ability ability, Vector velocity) {
        this.entity.setVelocity(velocity);
    }

    @Override
    public ImmutableVector getVelocity() {
        return ImmutableVector.of(this.entity.getVelocity());
    }

    @Override
    public ImmutableVector getLocation() {
        return ImmutableVector.of(this.entity.getLocation());
    }

    @Override
    public ImmutableVector getCenteredLocation() {
        return this.getLocation().add(0,this.entity.getHeight() / 2,0);
    }

    @Override
    public boolean isOnGround() {
        return this.entity.isOnGround();
    }

    @Override
    public double getDistanceAboveGround() {
        return this.getDistanceAboveGround(false);
    }

    @Override
    public double getDistanceAboveGround(boolean ignoreLiquids) {
        return WorldUtils.getDistanceAboveGround(this.getWorld(), this.getLocation(), ignoreLiquids);
    }

    @Override
    public World getWorld() {
        return this.entity.getWorld();
    }

    @Override
    public boolean isLiving() {
        return false;
    }

    @Override
    public boolean isPlayer() {
        return false;
    }

    public Entity getHandle() {
        return this.entity;
    }
}
