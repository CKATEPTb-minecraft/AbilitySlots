package dev.ckateptb.minecraft.abilityslots.cooldown;

import dev.ckateptb.minecraft.abilityslots.ability.IAbility;

public interface ICooldownHolder {
    void setCooldown(Class<? extends IAbility> ability, long duration);
    boolean hasCooldown(Class<? extends IAbility> ability);
    long getCooldown(Class<? extends IAbility> ability);

}
