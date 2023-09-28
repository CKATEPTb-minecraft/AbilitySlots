package dev.ckateptb.minecraft.abilityslots.user;

import dev.ckateptb.minecraft.abilityslots.ability.Ability;
import dev.ckateptb.minecraft.abilityslots.ability.holder.AbilityHolder;
import dev.ckateptb.minecraft.abilityslots.cooldown.CooldownHolder;
import dev.ckateptb.minecraft.abilityslots.energy.EnergyHolder;
import dev.ckateptb.minecraft.abilityslots.entity.AbilityTarget;
import org.bukkit.entity.LivingEntity;

import java.util.stream.Stream;

public interface AbilityUser extends AbilityTarget, LivingEntity, CooldownHolder, EnergyHolder, AbilityHolder {
    Stream<Ability> getAbilityInstances();
    <T extends Ability> Stream<T> getAbilityInstances(Class<T> type);
}
