package dev.ckateptb.minecraft.abilityslots.user;

import dev.ckateptb.minecraft.abilityslots.ability.Ability;
import dev.ckateptb.minecraft.abilityslots.container.AbilityContainer;
import dev.ckateptb.minecraft.abilityslots.declaration.AbilityDeclaration;
import dev.ckateptb.minecraft.abilityslots.entity.LivingEntityAbilityTarget;
import dev.ckateptb.minecraft.abilityslots.predicate.AbilityConditional;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class LivingEntityAbilityUser extends LivingEntityAbilityTarget implements AbilityUser {

    public LivingEntityAbilityUser(LivingEntity entity) {
        super(entity);
    }

    @Override
    public boolean canActivate(AbilityDeclaration declaration) {
        return false;
    }

    @Override
    public AbilityConditional getAbilityActivateConditional() {
        return null;
    }

    @Override
    public boolean addAbilityActivateConditional(AbilityConditional conditional) {
        return false;
    }

    @Override
    public boolean removeAbilityActivateConditional(AbilityConditional conditional) {
        return false;
    }

    @Override
    public void setAbilityActivateConditional(AbilityConditional conditional) {

    }

    @Override
    public boolean canUse(Location location) {
        return true;
    }

    @Override
    public Collection<? extends Ability> getActiveAbilities() {
        return null;
    }

    @Override
    public Collection<? extends Ability> getActiveAbilities(Class<? extends Ability> type) {
        return null;
    }

    @Override
    public Collection<? extends Ability> getPassiveAbilities() {
        return null;
    }

    @Override
    public boolean destroyAbility(Class<? extends Ability> type) {
        return false;
    }

    @Override
    public boolean destroyAbility(AbilityDeclaration declaration) {
        return false;
    }

    @Override
    public void destroyAbility(Ability ability) {

    }

    @Override
    public void destroyAbilities() {

    }

    @Override
    public void activateAbility(Ability ability) {

    }

    @Override
    public void activatePassiveAbilities() {

    }

    @Override
    public void destroyPassiveAbilities() {

    }
    @Override
    public double getEnergy() {
        return Double.MAX_VALUE;
    }

    @Override
    public boolean removeEnergy(double value) {
        return true;
    }

    @Override
    public void addEnergy(double value) {
    }

    @Override
    public void setEnergy(double value) {
    }

    @Override
    public double getMaxEnergy() {
        return Double.MAX_VALUE;
    }

    @Override
    public void updateEnergyBar() {

    }

    @Override
    public void enableEnergyBar() {

    }

    @Override
    public void disableEnergyBar() {

    }

    @Override
    public boolean isEnergyBarEnabled() {
        return false;
    }

    @Override
    public void setCooldown(Ability ability) {

    }

    @Override
    public void setCooldown(AbilityDeclaration abilityDeclaration) {

    }

    @Override
    public void setCooldown(Class<? extends Ability> type) {

    }

    @Override
    public void setCooldown(Class<? extends Ability> type, long duration) {

    }

    @Override
    public boolean hasCooldown(AbilityDeclaration declaration) {
        return false;
    }

    @Override
    public Map<Class<? extends Ability>, Long> getCooldowns() {
        return Collections.emptyMap();
    }

    @Override
    public AbilityContainer getAbilityContainer() {
        return null;
    }

    @Override
    public void setAbilityContainer(AbilityContainer slotContainer) {

    }

    @Override
    public AbilityDeclaration[] getAbilities() {
        return new AbilityDeclaration[0];
    }

    @Override
    public AbilityDeclaration getAbility(int slot) {
        return null;
    }

    @Override
    public AbilityDeclaration getSelectedAbility() {
        return null;
    }
}
