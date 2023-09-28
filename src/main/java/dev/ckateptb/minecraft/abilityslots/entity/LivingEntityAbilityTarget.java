package dev.ckateptb.minecraft.abilityslots.entity;

import dev.ckateptb.minecraft.abilityslots.ability.Ability;
import dev.ckateptb.minecraft.atom.adapter.AdapterUtils;
import dev.ckateptb.minecraft.atom.adapter.entity.LivingEntityAdapter;
import dev.ckateptb.minecraft.colliders.math.ImmutableVector;
import lombok.Getter;
import lombok.experimental.Delegate;
import org.bukkit.entity.LivingEntity;

import java.util.Objects;

@Getter
public class LivingEntityAbilityTarget extends EntityAbilityTarget implements LivingEntity {
    @Delegate
    protected final LivingEntityAdapter handle_;

    public LivingEntityAdapter getAdapter() {
        return handle_;
    }

    protected LivingEntityAbilityTarget(LivingEntity livingEntity) {
        super(livingEntity);
        this.handle_ = AdapterUtils.adapt(livingEntity);
    }

    @Override
    public boolean isLiving() {
        return true;
    }

    @Override
    public boolean isPlayer() {
        return false;
    }

    @Override
    public ImmutableVector getEyeVector() {
        return ImmutableVector.of(this.handle_.getEyeLocation());
    }

    @Override
    public float getYaw() {
        return this.handle_.getEyeLocation().getYaw();
    }

    @Override
    public float getPitch() {
        return this.handle_.getEyeLocation().getPitch();
    }

    @Override
    public ImmutableVector getDirection() {
        return ImmutableVector.of(this.handle_.getEyeLocation().getDirection());
    }

    @Override
    public void damage(double amount, boolean ignoreNoDamageTicks, Ability ability) {
        if (ignoreNoDamageTicks) this.handle_.setNoDamageTicks(0);
        this.handle_.damage(amount);
    }

    public boolean equals(Object other) {
        if (other instanceof LivingEntityAbilityTarget adapter) {
            other = adapter.handle_;
        }

        return Objects.equals(this.handle_, other);
    }

    public int hashCode() {
        return this.handle_.hashCode();
    }
}