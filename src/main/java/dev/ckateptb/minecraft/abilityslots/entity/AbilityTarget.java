package dev.ckateptb.minecraft.abilityslots.entity;

import dev.ckateptb.minecraft.abilityslots.ability.Ability;
import dev.ckateptb.minecraft.abilityslots.ray.Ray;
import dev.ckateptb.minecraft.colliders.Collider;
import dev.ckateptb.minecraft.colliders.math.ImmutableVector;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.MainHand;
import org.bukkit.util.Vector;

public interface AbilityTarget {
    static EntityAbilityTarget of(Entity entity) {
        return new EntityAbilityTarget(entity);
    }

    static LivingEntityAbilityTarget of(LivingEntity entity) {
        return new LivingEntityAbilityTarget(entity);
    }

    static PlayerAbilityTarget of(Player entity) {
        return new PlayerAbilityTarget(entity);
    }

    void setVelocity(Vector velocity, Ability ability);

    ImmutableVector getLocation();

    ImmutableVector getCenterLocation();

    double getDistanceAboveGround(boolean ignoreLiquid);

    boolean isOnGround();

    World getWorld();

    Entity getHandle();

    boolean isLiving();

    boolean isPlayer();

    boolean isSneaking();

    boolean isSprinting();

    boolean isSwimming();

    boolean isOnline();

    boolean isDead();

    Collider getCollider();

    ImmutableVector getEyeLocation();

    float getYaw();

    float getPitch();

    boolean hasPermission(String permission);

    GameMode getGameMode();

    MainHand getMainHand();

    ImmutableVector getHandLocation(MainHand hand);

    ImmutableVector getDirection();

    void damage(double amount, Ability ability);

    void damage(double amount, boolean ignoreNoDamageTicks, Ability ability);

    Ray ray(double distance, double size);
}
