package dev.ckateptb.minecraft.abilityslots.predicate;

import dev.ckateptb.minecraft.abilityslots.declaration.AbilityDeclaration;
import dev.ckateptb.minecraft.abilityslots.user.AbilityUser;
import lombok.NoArgsConstructor;
import org.bukkit.GameMode;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public interface AbilityConditional extends Conditional<AbilityDeclaration> {
    AbilityConditional IS_BINDABLE = (user, ability) -> ability != null && ability.isBindable();

    AbilityConditional HAS_ABILITY_PERMISSION = (user, ability) -> ability != null && user.hasPermission(String.format("abilityslots.abilities.%s.%s", ability.getCategory().getName(), ability.getName()).toLowerCase());

    AbilityConditional HAS_CATEGORY_PERMISSION = (user, ability) -> ability != null && CategoryConditional.HAS_CATEGORY_PERMISSION.matches(user, ability.getCategory());

    AbilityConditional NOT_ON_COOLDOWN = (user, ability) -> ability != null && !user.hasCooldown(ability);

    AbilityConditional IS_ENABLED = (user, ability) -> ability != null && ability.isEnabled();

    AbilityConditional ENOUGH_ENERGY = (user, ability) -> ability != null && ability.getCost() <= user.getEnergy();

    AbilityConditional NOT_PARALYZED = (user, ability) -> !user.getHandle().hasMetadata("tablecloth:paralyze");

    @Override
    boolean matches(AbilityUser user, AbilityDeclaration ability);

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
            conditionals.add(HAS_CATEGORY_PERMISSION);
            return this;
        }

        public Builder withoutCooldown() {
            conditionals.add(NOT_ON_COOLDOWN);
            return this;
        }

        public Builder enoughEnergy() {
            conditionals.add(ENOUGH_ENERGY);
            return this;
        }

        public Builder gameModeNot(GameMode... gameModes) {
            conditionals.add((user, ability) -> !user.isPlayer() || !Arrays.asList(gameModes).contains(user.getGameMode()));
            return this;
        }

        public Builder notParalyzed() {
            conditionals.add(NOT_PARALYZED);
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
}
