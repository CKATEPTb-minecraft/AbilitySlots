package dev.ckateptb.minecraft.abilityslots.user;

import dev.ckateptb.minecraft.abilityslots.ability.Ability;
import dev.ckateptb.minecraft.abilityslots.ability.declaration.IAbilityDeclaration;
import dev.ckateptb.minecraft.abilityslots.entity.PlayerAbilityTarget;
import dev.ckateptb.minecraft.colliders.internal.math3.util.FastMath;
import org.apache.commons.lang3.Validate;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class PlayerAbilityUser extends PlayerAbilityTarget implements AbilityUser {
    protected final IAbilityDeclaration<? extends Ability>[] abilities = new IAbilityDeclaration<?>[9];
    protected final Map<Class<? extends Ability>, Long> cooldowns = new HashMap<>();
    protected double currentEnergy;
    protected double maxEnergy;

    public PlayerAbilityUser(Player player) {
        super(player);
    }

    @Override
    public IAbilityDeclaration<? extends Ability>[] getAbilities() {
        return this.abilities;
    }

    @Override
    public IAbilityDeclaration<? extends Ability> getAbility(int slot) {
        Validate.inclusiveBetween(1, 9, slot);
        return this.abilities[slot - 1];
    }

    @Override
    public void setAbility(int slot, IAbilityDeclaration<? extends Ability> ability) {
        Validate.inclusiveBetween(1, 9, slot);
        this.abilities[slot - 1] = ability;
    }

    @Override
    public void setCooldown(Class<? extends Ability> ability, long duration) {
        this.cooldowns.put(ability, duration + System.currentTimeMillis());
    }

    @Override
    public boolean hasCooldown(Class<? extends Ability> ability) {
        return this.getCooldown(ability) > System.currentTimeMillis();
    }

    @Override
    public long getCooldown(Class<? extends Ability> ability) {
        return this.cooldowns.getOrDefault(ability, 0L);
    }

    @Override
    public double getEnergy() {
        return this.currentEnergy;
    }

    @Override
    public boolean removeEnergy(double value) {
        if (this.currentEnergy >= value) {
            this.currentEnergy = this.currentEnergy - value;
            return true;
        }
        return false;
    }

    @Override
    public void addEnergy(double value) {
        this.currentEnergy = FastMath.min(this.maxEnergy, this.currentEnergy + value);
    }

    @Override
    public void setEnergy(double value) {
        this.currentEnergy = FastMath.max(FastMath.min(value, this.maxEnergy), 0);
    }

    @Override
    public double getMaxEnergy() {
        return this.maxEnergy;
    }
}
