package dev.ckateptb.minecraft.abilityslots.user;

import dev.ckateptb.minecraft.abilityslots.ability.holder.IAbilityHolder;
import dev.ckateptb.minecraft.abilityslots.cooldown.ICooldownHolder;
import dev.ckateptb.minecraft.abilityslots.energy.IEnergyHolder;
import dev.ckateptb.minecraft.abilityslots.entity.IAbilityTarget;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public interface IAbilityUser<T extends LivingEntity> {
    static LivingEntityAbilityUser<LivingEntity> of(LivingEntity livingEntity) {
        return new LivingEntityAbilityUser<>(IAbilityTarget.of(livingEntity));
    }

    static PlayerAbilityUser<Player> of(Player player) {
        return new PlayerAbilityUser<>(IAbilityTarget.of(player));
    }

    ICooldownHolder cooldown();

    IEnergyHolder energy();

    IAbilityHolder ability();

    IAbilityTarget<T> entity();
}
