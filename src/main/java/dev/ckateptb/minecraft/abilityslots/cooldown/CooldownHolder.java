package dev.ckateptb.minecraft.abilityslots.cooldown;

import dev.ckateptb.minecraft.abilityslots.ability.Ability;
import dev.ckateptb.minecraft.abilityslots.ability.declaration.IAbilityDeclaration;

public interface CooldownHolder {
    void setCooldown(IAbilityDeclaration<? extends Ability> ability, long duration);

    boolean hasCooldown(IAbilityDeclaration<? extends Ability> ability);

    long getCooldown(IAbilityDeclaration<? extends Ability> ability);

    boolean isCooldownEnabled();

}
