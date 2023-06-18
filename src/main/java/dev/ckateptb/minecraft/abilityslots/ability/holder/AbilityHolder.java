package dev.ckateptb.minecraft.abilityslots.ability.holder;

import dev.ckateptb.minecraft.abilityslots.ability.IAbility;
import dev.ckateptb.minecraft.abilityslots.ability.declaration.IAbilityDeclaration;
import org.apache.commons.lang3.Validate;

public class AbilityHolder implements IAbilityHolder {
    private final IAbilityDeclaration<? extends IAbility>[] abilities = new IAbilityDeclaration<?>[9];

    @Override
    public IAbilityDeclaration<? extends IAbility>[] getAbilities() {
        return this.abilities;
    }

    @Override
    public IAbilityDeclaration<? extends IAbility> getAbility(int slot) {
        Validate.exclusiveBetween(0, 10, slot);
        return this.abilities[slot - 1];
    }

    public void setAbility(int slot, IAbilityDeclaration<? extends IAbility> ability) {
        Validate.exclusiveBetween(0, 10, slot);
        this.abilities[slot - 1] = ability;
    }
}
