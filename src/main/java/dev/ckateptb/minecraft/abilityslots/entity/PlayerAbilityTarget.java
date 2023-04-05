package dev.ckateptb.minecraft.abilityslots.entity;

import dev.ckateptb.minecraft.colliders.Colliders;
import dev.ckateptb.minecraft.colliders.geometry.AxisAlignedBoundingBoxCollider;
import dev.ckateptb.minecraft.colliders.math.ImmutableVector;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.MainHand;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.atomic.AtomicBoolean;

public class PlayerAbilityTarget extends LivingEntityAbilityTarget {
    protected final Player entity;

    public PlayerAbilityTarget(Player entity) {
        super(entity);
        this.entity = entity;
    }

    @Override
    public boolean isOnGround() {
        AxisAlignedBoundingBoxCollider entityBounds = Colliders.aabb(this.entity).grow(new ImmutableVector(0, 0.05, 0));
        ImmutableVector floorHalf = new ImmutableVector(1, 0.1, 1);
        AxisAlignedBoundingBoxCollider floorBounds = Colliders.aabb(this.getWorld(), floorHalf.negative(), floorHalf);
        AtomicBoolean result = new AtomicBoolean(false);
        floorBounds.at(this.getLocation()).affectBlocks(flux -> flux.runOn(Schedulers.immediate())
                .map(Colliders::aabb)
                .map(aabb -> aabb.intersects(entityBounds))
                .filter(intersects -> intersects)
                .subscribe(result::set));
        return result.get();
    }

    @Override
    public boolean isPlayer() {
        return true;
    }

    @Override
    public boolean isSneaking() {
        return this.entity.isSneaking();
    }

    @Override
    public boolean isSprinting() {
        return this.entity.isSprinting();
    }

    @Override
    public boolean hasPermission(String permission) {
        return this.entity.hasPermission(permission);
    }

    @Override
    public GameMode getGameMode() {
        return this.entity.getGameMode();
    }

    @Override
    public boolean isOnline() {
        return this.entity.isOnline();
    }

    @Override
    public MainHand getMainHand() {
        return this.entity.getMainHand();
    }
}
