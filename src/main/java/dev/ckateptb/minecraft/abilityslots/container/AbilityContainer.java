package dev.ckateptb.minecraft.abilityslots.container;

import dev.ckateptb.minecraft.abilityslots.declaration.AbilityDeclaration;

public interface AbilityContainer {
    AbilityDeclaration[] getAbilities();

    AbilityDeclaration getAbility(int slot);

    void setAbility(int slot, AbilityDeclaration ability);

    boolean isEditable();
}
