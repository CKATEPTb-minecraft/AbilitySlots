package dev.ckateptb.minecraft.abilityslots.ability.holder;

import dev.ckateptb.minecraft.abilityslots.ability.IAbility;
import dev.ckateptb.minecraft.abilityslots.ability.declaration.IAbilityDeclaration;

public interface IAbilityHolder {
    IAbilityDeclaration<? extends IAbility>[] getAbilities();

    IAbilityDeclaration<? extends IAbility> getAbility(int slot);
    void setAbility(int slot, IAbilityDeclaration<? extends IAbility> ability);
}
