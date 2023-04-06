package dev.ckateptb.minecraft.abilityslots.container;

import dev.ckateptb.minecraft.abilityslots.declaration.AbilityDeclaration;

public interface AbilityHolder {
    AbilityContainer getAbilityContainer();

    void setAbilityContainer(AbilityContainer slotContainer);

    AbilityDeclaration[] getAbilities();

    AbilityDeclaration getAbility(int slot);

    AbilityDeclaration getSelectedAbility();
}
