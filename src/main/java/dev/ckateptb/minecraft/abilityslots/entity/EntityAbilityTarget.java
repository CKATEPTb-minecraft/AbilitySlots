package dev.ckateptb.minecraft.abilityslots.entity;

import dev.ckateptb.minecraft.abilityslots.ability.Ability;
import dev.ckateptb.minecraft.abilityslots.ray.Ray;
import dev.ckateptb.minecraft.colliders.Collider;
import dev.ckateptb.minecraft.colliders.Colliders;
import dev.ckateptb.minecraft.colliders.internal.math3.util.FastMath;
import dev.ckateptb.minecraft.colliders.math.ImmutableVector;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.MainHand;
import org.bukkit.util.Vector;

import java.util.Optional;

@Getter
@Setter
public class EntityAbilityTarget implements AbilityTarget {
    protected final Entity handle;
    private boolean living;
    private boolean player;
    private boolean sneaking;
    private boolean sprinting;
    private boolean swimming;
    private boolean online;
    private GameMode gameMode;
    private MainHand mainHand;
    private float yaw;
    private float pitch;

    protected EntityAbilityTarget(Entity entity) {
        this.handle = entity;
    }

    @Override
    public void setVelocity(Vector velocity, Ability ability) {
        this.handle.setVelocity(velocity);
    }

    @Override
    public ImmutableVector getLocation() {
        return ImmutableVector.of(this.handle.getLocation());
    }

    @Override
    public ImmutableVector getCenterLocation() {
        return getLocation().add(new ImmutableVector(0, this.handle.getHeight() / 2, 0));
    }

    @Override
    public double getDistanceAboveGround(boolean ignoreLiquid) {
        return this.getLocation().getDistanceAboveGround(this.getWorld(), ignoreLiquid);
    }

    @Override
    public boolean isOnGround() {
        return this.handle.isOnGround();
    }

    @Override
    public World getWorld() {
        return this.handle.getWorld();
    }

    @Override
    public boolean isDead() {
        return this.handle.isDead();
    }

    @Override
    public Collider getCollider() {
        return Colliders.aabb(this.handle);
    }

    @Override
    public ImmutableVector getEyeLocation() {
        return this.getLocation();
    }

    @Override
    public boolean hasPermission(String permission) {
        return true;
    }

    @Override
    public GameMode getGameMode() {
        return Optional.ofNullable(this.gameMode).orElse(this.handle.getServer().getDefaultGameMode());
    }

    @Override
    public MainHand getMainHand() {
        return Optional.ofNullable(this.mainHand).orElse(MainHand.RIGHT);
    }

    @Override
    public ImmutableVector getHandLocation(MainHand hand) {
        ImmutableVector direction = this.getDirection();
        if (hand == null) return this.getEyeLocation().add(direction.multiply(0.4));
        double angle = FastMath.toRadians(this.getYaw());
        ImmutableVector location = this.getLocation();
        ImmutableVector offset = direction.multiply(0.4).add(0, 1.2, 0);
        ImmutableVector vector = new ImmutableVector(FastMath.cos(angle), 0, FastMath.sin(angle)).normalize().multiply(0.3);
        return (hand == MainHand.LEFT ? location.add(vector) : location.subtract(vector)).add(offset);
    }

    @Override
    public ImmutableVector getDirection() {
        double xz = FastMath.cos(FastMath.toRadians(this.pitch));
        double yawRadians = FastMath.toRadians(this.yaw);
        return new ImmutableVector(-xz * FastMath.sin(yawRadians),
                -FastMath.sin(FastMath.toRadians(this.pitch)),
                xz * FastMath.cos(yawRadians));
    }

    @Override
    public void damage(double amount, Ability ability) {
        this.damage(amount, false, ability);
    }

    @Override
    public void damage(double amount, boolean ignoreNoDamageTicks, Ability ability) {

    }

    @Override
    public Ray ray(double distance, double size) {
        return new Ray(distance, size, this.getEyeLocation(), this.getDirection(), this.getWorld());
    }
}
