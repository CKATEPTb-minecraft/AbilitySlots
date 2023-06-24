package dev.ckateptb.minecraft.abilityslots.entity;

import dev.ckateptb.minecraft.abilityslots.ability.Ability;
import dev.ckateptb.minecraft.colliders.math.ImmutableVector;
import lombok.Getter;
import org.bukkit.entity.LivingEntity;

@Getter
public class LivingEntityAbilityTarget extends EntityAbilityTarget {
    protected final LivingEntity handle;

    protected LivingEntityAbilityTarget(LivingEntity livingEntity) {
        super(livingEntity);
        this.handle = livingEntity;
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
    public ImmutableVector getEyeLocation() {
        return ImmutableVector.of(this.handle.getEyeLocation());
    }

    @Override
    public float getYaw() {
        return this.handle.getEyeLocation().getYaw();
    }

    @Override
    public float getPitch() {
        return this.handle.getEyeLocation().getPitch();
    }

    @Override
    public ImmutableVector getDirection() {
        return ImmutableVector.of(this.handle.getEyeLocation().getDirection());
    }

    @Override
    public void damage(double amount, boolean ignoreNoDamageTicks, Ability ability) {
        if (ignoreNoDamageTicks) this.handle.setNoDamageTicks(0);
        this.handle.damage(amount);
    }
}