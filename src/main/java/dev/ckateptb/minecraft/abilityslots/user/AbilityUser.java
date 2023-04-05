package dev.ckateptb.minecraft.abilityslots.user;

import dev.ckateptb.minecraft.abilityslots.ability.Ability;
import dev.ckateptb.minecraft.abilityslots.container.AbilityHolder;
import dev.ckateptb.minecraft.abilityslots.cooldown.CooldownHolder;
import dev.ckateptb.minecraft.abilityslots.declaration.AbilityDeclaration;
import dev.ckateptb.minecraft.abilityslots.energy.EnergyHolder;
import dev.ckateptb.minecraft.abilityslots.entity.LivingAbilityTarget;
import dev.ckateptb.minecraft.abilityslots.predicate.AbilityConditional;
import org.bukkit.Location;

import java.util.Collection;

public interface AbilityUser extends AbilityHolder, LivingAbilityTarget, EnergyHolder, CooldownHolder {
    boolean canActivate(AbilityDeclaration declaration);

    AbilityConditional getAbilityActivateConditional();

    boolean addAbilityActivateConditional(AbilityConditional conditional);

    boolean removeAbilityActivateConditional(AbilityConditional conditional);

    void setAbilityActivateConditional(AbilityConditional conditional);

    boolean canUse(Location location);

    Collection<? extends Ability> getActiveAbilities();

    Collection<? extends Ability> getActiveAbilities(Class<? extends Ability> type);

    Collection<? extends Ability> getPassiveAbilities();

    boolean destroyAbility(Class<? extends Ability> type);

    boolean destroyAbility(AbilityDeclaration declaration);

    void destroyAbility(Ability ability);

    void destroyAbilities();

    void activateAbility(Ability ability);

    void activatePassiveAbilities();

    void destroyPassiveAbilities();

}
