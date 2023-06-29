package dev.ckateptb.minecraft.abilityslots.ability.holder;

import dev.ckateptb.minecraft.abilityslots.ability.Ability;
import dev.ckateptb.minecraft.abilityslots.ability.declaration.IAbilityDeclaration;
import dev.ckateptb.minecraft.abilityslots.ability.sequence.annotation.AbilityAction;

import java.util.List;
import java.util.Set;

public interface AbilityHolder {
    IAbilityDeclaration<? extends Ability>[] getAbilities();

    IAbilityDeclaration<? extends Ability> getAbility(int slot);

    IAbilityDeclaration<? extends Ability> getSelectedAbility();

    void setAbility(int slot, IAbilityDeclaration<? extends Ability> ability);

    boolean canUse(IAbilityDeclaration<? extends Ability> ability);

    boolean canBind(IAbilityDeclaration<? extends Ability> ability);
}
