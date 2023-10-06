package dev.ckateptb.minecraft.abilityslots.entity;

import dev.ckateptb.minecraft.abilityslots.ability.Ability;
import dev.ckateptb.minecraft.abilityslots.ray.Ray;
import dev.ckateptb.minecraft.atom.adapter.AdapterUtils;
import dev.ckateptb.minecraft.atom.adapter.entity.EntityAdapter;
import dev.ckateptb.minecraft.colliders.Collider;
import dev.ckateptb.minecraft.colliders.Colliders;
import dev.ckateptb.minecraft.colliders.internal.math3.util.FastMath;
import dev.ckateptb.minecraft.colliders.math.ImmutableVector;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Delegate;
import org.bukkit.GameMode;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.MainHand;
import org.bukkit.util.Vector;

import java.util.Objects;
import java.util.Optional;

@Getter
@Setter
public class EntityAbilityTarget implements AbilityTarget, Entity {
    @Delegate
    protected final EntityAdapter handle_;
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
    private double lastFallDistance;

    protected EntityAbilityTarget(Entity entity) {
        this.handle_ = AdapterUtils.adapt(entity);
    }

    @Override
    public void setVelocity(Vector velocity, Ability ability) {
        if(this.handle_.getHandle_() instanceof ArmorStand) {
            return;
        }
        this.handle_.setVelocity(velocity);
    }

    @Override
    public ImmutableVector getVector() {
        return ImmutableVector.of(this.handle_.getLocation());
    }

    @Override
    public ImmutableVector getCenterLocation() {
        return getVector().add(new ImmutableVector(0, this.handle_.getHeight() / 2, 0));
    }

    @Override
    public double getDistanceAboveGround(boolean ignoreLiquid) {
        return this.getVector().getDistanceAboveGround(this.getWorld(), ignoreLiquid);
    }

    @Override
    public Collider getCollider() {
        return Colliders.aabb(this.handle_);
    }

    @Override
    public ImmutableVector getEyeVector() {
        return this.getVector();
    }

    @Override
    public GameMode getGameMode() {
        return Optional.ofNullable(this.gameMode).orElse(this.handle_.getServer().getDefaultGameMode());
    }

    @Override
    public MainHand getMainHand() {
        return Optional.ofNullable(this.mainHand).orElse(MainHand.RIGHT);
    }

    @Override
    public ImmutableVector getHandLocation(MainHand hand) {
        ImmutableVector direction = this.getDirection();
        if (hand == null) return this.getEyeVector().add(direction.multiply(0.4));
        double angle = FastMath.toRadians(this.getYaw());
        ImmutableVector location = this.getVector();
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
        return new Ray(distance, size, this.getEyeVector(), this.getDirection(), this.getWorld());
    }

    public boolean equals(Object other) {
        if (other instanceof EntityAbilityTarget adapter) {
            other = adapter.handle_;
        }

        return Objects.equals(this.handle_, other);
    }

    public int hashCode() {
        return this.handle_.hashCode();
    }
}
