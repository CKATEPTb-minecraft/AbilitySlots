package dev.ckateptb.minecraft.abilityslots.user;

import dev.ckateptb.minecraft.abilityslots.ability.Ability;
import dev.ckateptb.minecraft.abilityslots.ability.declaration.IAbilityDeclaration;
import dev.ckateptb.minecraft.abilityslots.ability.sequence.annotation.AbilityAction;
import dev.ckateptb.minecraft.abilityslots.entity.PlayerAbilityTarget;
import dev.ckateptb.minecraft.colliders.internal.math3.util.FastMath;
import org.apache.commons.lang3.Validate;
import org.bukkit.entity.Player;

import java.util.*;

public class PlayerAbilityUser extends PlayerAbilityTarget implements AbilityUser {
    protected final IAbilityDeclaration<? extends Ability>[] abilities = new IAbilityDeclaration<?>[9];
    protected final List<AbilityAction> actionHistory = new ArrayList<>();
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
    public IAbilityDeclaration<? extends Ability> getSelectedAbility() {
        int slot = this.getInventory().getHeldItemSlot() + 1;
        return this.getAbility(slot);
    }

    @Override
    public void setAbility(int slot, IAbilityDeclaration<? extends Ability> ability) {
        Validate.inclusiveBetween(1, 9, slot);
        this.abilities[slot - 1] = ability;
    }

    public synchronized List<AbilityAction> registerAction(AbilityAction action) {
        this.actionHistory.add(action);
        return this.actionHistory;
    }

    @Override
    public boolean canUse(IAbilityDeclaration<? extends Ability> ability) {
        return true; //TODO
    }

    @Override
    public synchronized void setCooldown(Class<? extends Ability> ability, long duration) {
        this.cooldowns.put(ability, duration + System.currentTimeMillis());
    }

    @Override
    public boolean hasCooldown(Class<? extends Ability> ability) {
        return this.getCooldown(ability) > System.currentTimeMillis();
    }

    @Override
    public synchronized long getCooldown(Class<? extends Ability> ability) {
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

    public boolean equals(Object other) {
        return super.equals(other);
    }
}
