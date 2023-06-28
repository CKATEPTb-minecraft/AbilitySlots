package dev.ckateptb.minecraft.abilityslots.user;

import dev.ckateptb.minecraft.abilityslots.ability.Ability;
import dev.ckateptb.minecraft.abilityslots.ability.declaration.IAbilityDeclaration;
import dev.ckateptb.minecraft.abilityslots.entity.LivingEntityAbilityTarget;
import org.apache.commons.lang.NotImplementedException;
import org.bukkit.entity.LivingEntity;

public class LivingEntityAbilityUser extends LivingEntityAbilityTarget implements AbilityUser {
    public LivingEntityAbilityUser(LivingEntity livingEntity) {
        super(livingEntity);
    }

    @Override
    public IAbilityDeclaration<? extends Ability>[] getAbilities() {
        throw new NotImplementedException();
    }

    @Override
    public IAbilityDeclaration<? extends Ability> getAbility(int slot) {
        throw new NotImplementedException();
    }

    @Override
    public IAbilityDeclaration<? extends Ability> getSelectedAbility() {
        throw new NotImplementedException();
    }

    @Override
    public void setAbility(int slot, IAbilityDeclaration<? extends Ability> ability) {
        throw new NotImplementedException();
    }

    @Override
    public boolean canUse(IAbilityDeclaration<? extends Ability> ability) {
        throw new NotImplementedException();
    }

    @Override
    public void setCooldown(Class<? extends Ability> ability, long duration) {
        throw new NotImplementedException();
    }

    @Override
    public boolean hasCooldown(Class<? extends Ability> ability) {
        throw new NotImplementedException();
    }

    @Override
    public long getCooldown(Class<? extends Ability> ability) {
        throw new NotImplementedException();
    }

    @Override
    public double getEnergy() {
        throw new NotImplementedException();
    }

    @Override
    public boolean removeEnergy(double value) {
        throw new NotImplementedException();
    }

    @Override
    public void addEnergy(double value) {
        throw new NotImplementedException();
    }

    @Override
    public void setEnergy(double value) {
        throw new NotImplementedException();
    }

    @Override
    public double getMaxEnergy() {
        throw new NotImplementedException();
    }
}
