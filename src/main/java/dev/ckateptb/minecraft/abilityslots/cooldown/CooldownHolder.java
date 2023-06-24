package dev.ckateptb.minecraft.abilityslots.cooldown;

import dev.ckateptb.minecraft.abilityslots.ability.Ability;

public interface CooldownHolder {
    void setCooldown(Class<? extends Ability> ability, long duration);
    boolean hasCooldown(Class<? extends Ability> ability);
    long getCooldown(Class<? extends Ability> ability);

}
