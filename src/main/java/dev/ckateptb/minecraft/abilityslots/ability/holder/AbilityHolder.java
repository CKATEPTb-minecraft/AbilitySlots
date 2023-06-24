package dev.ckateptb.minecraft.abilityslots.ability.holder;

import dev.ckateptb.minecraft.abilityslots.ability.Ability;
import dev.ckateptb.minecraft.abilityslots.ability.declaration.IAbilityDeclaration;

public interface AbilityHolder {
    IAbilityDeclaration<? extends Ability>[] getAbilities();

    IAbilityDeclaration<? extends Ability> getAbility(int slot);
    void setAbility(int slot, IAbilityDeclaration<? extends Ability> ability);
}
