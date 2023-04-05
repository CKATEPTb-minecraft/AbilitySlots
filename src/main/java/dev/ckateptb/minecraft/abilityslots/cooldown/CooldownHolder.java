package dev.ckateptb.minecraft.abilityslots.cooldown;

import dev.ckateptb.minecraft.abilityslots.ability.Ability;
import dev.ckateptb.minecraft.abilityslots.declaration.AbilityDeclaration;

import java.util.Map;

public interface CooldownHolder {
    void setCooldown(Ability ability);

    void setCooldown(AbilityDeclaration abilityDeclaration);

    void setCooldown(Class<? extends Ability> type);

    void setCooldown(Class<? extends Ability> type, long duration);

    boolean hasCooldown(Ability ability);

    boolean hasCooldown(AbilityDeclaration declaration);

    boolean hasCooldown(Class<? extends Ability> type);

    Map<Class<? extends Ability>, Long> getCooldowns();
}
