package dev.ckateptb.minecraft.abilityslots.user;

import dev.ckateptb.minecraft.abilityslots.ability.holder.AbilityHolder;
import dev.ckateptb.minecraft.abilityslots.ability.holder.IAbilityHolder;
import dev.ckateptb.minecraft.abilityslots.cooldown.CooldownHolder;
import dev.ckateptb.minecraft.abilityslots.cooldown.ICooldownHolder;
import dev.ckateptb.minecraft.abilityslots.energy.EnergyHolder;
import dev.ckateptb.minecraft.abilityslots.energy.IEnergyHolder;
import dev.ckateptb.minecraft.abilityslots.entity.IAbilityTarget;
import org.bukkit.entity.LivingEntity;

public class LivingEntityAbilityUser<T extends LivingEntity> implements IAbilityUser<T> {
    private final IAbilityTarget<T> entity;
    private final ICooldownHolder cooldown = new CooldownHolder();
    private final IEnergyHolder energy = new EnergyHolder();
    private final IAbilityHolder ability = new AbilityHolder();

    public LivingEntityAbilityUser(IAbilityTarget<T> entity) {
        this.entity = entity;
    }

    @Override
    public ICooldownHolder cooldown() {
        return this.cooldown;
    }

    @Override
    public IEnergyHolder energy() {
        return this.energy;
    }

    @Override
    public IAbilityHolder ability() {
        return this.ability;
    }

    @Override
    public IAbilityTarget<T> entity() {
        return this.entity;
    }
}
