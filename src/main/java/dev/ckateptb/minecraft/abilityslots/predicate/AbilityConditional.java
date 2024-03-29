package dev.ckateptb.minecraft.abilityslots.predicate;

import dev.ckateptb.minecraft.abilityslots.ability.Ability;
import dev.ckateptb.minecraft.abilityslots.ability.declaration.IAbilityDeclaration;
import dev.ckateptb.minecraft.abilityslots.user.AbilityUser;
import lombok.NoArgsConstructor;
import org.bukkit.GameMode;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public interface AbilityConditional extends Conditional<IAbilityDeclaration<? extends Ability>> {
    AbilityConditional IS_BINDABLE = (user, ability) -> ability != null && ability.isBindable();

    AbilityConditional HAS_ABILITY_PERMISSION = (user, ability) -> ability != null && user.hasPermission(String.format("abilityslots.abilities.%s.%s", ability.getCategory().getName(), ability.getName()).toLowerCase());

    AbilityConditional HAS_ABILITY_CATEGORY_PERMISSION = (user, ability) -> ability != null && CategoryConditional.HAS_CATEGORY_PERMISSION.matches(user, ability.getCategory());

    AbilityConditional NOT_ON_COOLDOWN = (user, ability) -> ability != null && !user.hasCooldown(ability);

    AbilityConditional IS_ENABLED = (user, ability) -> ability != null && ability.isEnabled();

    AbilityConditional IS_CATEGORY_ENABLED = (user, ability) -> ability != null && CategoryConditional.IS_ENABLED.matches(user, ability.getCategory());

    @NoArgsConstructor
    class Builder {
        private final Set<AbilityConditional> conditionals = new HashSet<>();

        public Builder isEnabled() {
            conditionals.add(IS_ENABLED);
            return this;
        }

        public Builder isBindable() {
            conditionals.add(IS_BINDABLE);
            return this;
        }

        public Builder hasPermission() {
            conditionals.add(HAS_ABILITY_PERMISSION);
            return this;
        }

        public Builder hasCategory() {
            conditionals.add(HAS_ABILITY_CATEGORY_PERMISSION);
            return this;
        }

        public Builder isCategoryEnabled() {
            conditionals.add(IS_CATEGORY_ENABLED);
            return this;
        }

        public Builder withoutCooldown() {
            conditionals.add(NOT_ON_COOLDOWN);
            return this;
        }

        public Builder enoughEnergy(double energy) {
            conditionals.add((user, ability) -> ability != null && energy <= user.getEnergy());
            return this;
        }

        public Builder gameModeNot(GameMode... gameModes) {
            conditionals.add((user, ability) -> !user.isPlayer() || !Arrays.asList(gameModes).contains(user.getGameMode()));
            return this;
        }

        public Builder custom(AbilityConditional conditional) {
            conditionals.add(conditional);
            return this;
        }

        public AbilityConditional build() {
            return (user, ability) -> conditionals.stream().allMatch(conditional -> conditional.matches(user, ability));
        }
    }

    @Override
    boolean matches(AbilityUser user, IAbilityDeclaration<? extends Ability> ability);
}
