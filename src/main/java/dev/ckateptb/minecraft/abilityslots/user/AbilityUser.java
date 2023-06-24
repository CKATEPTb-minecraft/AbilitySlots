package dev.ckateptb.minecraft.abilityslots.user;

import dev.ckateptb.minecraft.abilityslots.ability.holder.AbilityHolder;
import dev.ckateptb.minecraft.abilityslots.cooldown.CooldownHolder;
import dev.ckateptb.minecraft.abilityslots.energy.EnergyHolder;
import dev.ckateptb.minecraft.abilityslots.entity.AbilityTarget;

public interface AbilityUser extends AbilityTarget, CooldownHolder, EnergyHolder, AbilityHolder {
}
